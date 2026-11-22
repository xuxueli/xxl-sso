package com.xxl.sso.core.util;

import com.xxl.tool.core.StringTool;
import com.xxl.tool.serializer.impl.JavaSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;


/**
 * Redis client base on jedis
 *
 * <p>
 * 统一封装 Jedis 单机（连接池）与集群模式，按节点数量自动选择：单节点使用 {@link JedisPool}，
 * 多节点使用 {@link JedisCluster}。值统一通过 JavaSerializer 序列化后以 byte[] 写入。
 * </p>
 *
 * <p>
 * 异常策略为「记录日志并降级」：{@link #get(String)} 失败返回 {@code null}，
 * {@link #set(String, Object, long)} / {@link #del(String)} 失败不抛异常，调用方需自行处理失败场景。
 * </p>
 *
 * @author xuxueli 2015-7-10 18:34:07
 */
public class JedisTool {
    private static final Logger logger = LoggerFactory.getLogger(JedisTool.class);


    // ---------------------- param ----------------------

    /**
     * nodes, like "127.0.0.1:6379" or "host1:port1,host2:port2"
     */
    private String nodes;

    /**
     * redis user, nullable
     */
    private String user;

    /**
     * redis password, nullable
     */
    private String password;

    /**
     * connection timeout, seconds
     */
    private int connectionTimeout = 2;

    /**
     * socket read timeout, seconds
     */
    private int soTimeout = 2;

    /**
     * max redirect attempts for cluster
     */
    private int maxAttempts = 3;

    /**
     * parsed cluster nodes
     */
    private Set<HostAndPort> clusterNodes = new HashSet<>();

    /**
     * value serializer
     */
    private JavaSerializer serializer = new JavaSerializer();

    /**
     * create jedis tool
     *
     * @param nodes     redis nodes, like "127.0.0.1:6379" or "host1:port1,host2:port2"
     * @param user      redis user, nullable
     * @param password  redis password, nullable
     */
    public JedisTool(String nodes, String user, String password) {
        this.nodes = nodes;
        if (StringTool.isNotBlank(user)) {
            this.user = user;
        }
        if (StringTool.isNotBlank(password)) {
            this.password = password;
        }
    }

    // ---------------------- start / stop ----------------------

    /**
     * single-node connection pool, used when nodes size == 1
     */
    private JedisPool jedisPool;

    /**
     * cluster client, used when nodes size > 1
     */
    private JedisCluster jedisCluster;

    /**
     * start jedis pool or cluster client
     *
     * <p>幂等：已启动时直接返回，避免重复创建连接池导致资源泄漏。</p>
     *
     * @throws IllegalArgumentException when nodes is blank or malformed
     * @throws RuntimeException when client initialization fails
     */
    public void start() {

        // idempotent, avoid recreating pool / cluster and leaking old resource
        if (jedisPool != null || jedisCluster != null) {
            logger.debug(">>>>>>>>>>> JedisTool already started, skip start.");
            return;
        }

        // process param
        if (nodes!=null && !nodes.trim().isEmpty()) {
            for (String node : nodes.split(",")) {
                String[] parts = node.split(":");
                if (parts.length == 2) {
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    clusterNodes.add(new HostAndPort(host, port));
                }
            }
        }
        if (clusterNodes==null || clusterNodes.isEmpty()){
            throw new IllegalArgumentException("clusterNodes can not be null or empty.");
        }

        // pooled client
        if (clusterNodes.size() > 1) {
            try {
                GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
                JedisClientConfig clientConfig = DefaultJedisClientConfig
                        .builder()
                        .timeoutMillis(soTimeout * 1000)
                        .connectionTimeoutMillis(connectionTimeout * 1000)
                        .user(user)
                        .password(password)
                        .build();
                jedisCluster = new JedisCluster(clusterNodes, clientConfig, maxAttempts, poolConfig);
                logger.info(">>>>>>>>>>> JedisTool(JedisCluster) initialized successfully.");
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> JedisTool(JedisCluster) initialized error.", e);
                throw new RuntimeException("JedisTool(JedisCluster) initialized error.", e);
            }
        } else {
            try {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                JedisClientConfig clientConfig = DefaultJedisClientConfig
                        .builder()
                        .timeoutMillis(soTimeout * 1000)
                        .connectionTimeoutMillis(connectionTimeout * 1000)
                        .user(user)
                        .password(password)
                        .build();

                jedisPool = new JedisPool(poolConfig, clusterNodes.stream().findFirst().get(), clientConfig);
                logger.info(">>>>>>>>>>> JedisTool(jedisPool) initialized successfully.");
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> JedisTool(jedisPool) initialized error.", e);
                throw new RuntimeException("JedisTool(jedisPool) initialized error.", e);
            }
        }
    }

    /**
     * stop and release pool / cluster resource; exceptions are logged and ignored
     *
     * <p>停止后清空客户端引用，允许再次调用 {@link #start()}。</p>
     */
    public void stop() {
        try {
            if (jedisCluster!=null) {
                jedisCluster.close();
            }
            if (jedisPool!=null) {
                jedisPool.close();
            }
            logger.info(">>>>>>>>>>> JedisTool stop finish.");
        } catch (Exception e) {
            logger.error(">>>>>>>>>>> JedisTool stop error.", e);
        } finally {
            jedisCluster = null;
            jedisPool = null;
        }
    }


    // ---------------------- util ----------------------

    /**
     * set value with expire seconds
     *
     * <p>
     * {@code seconds < 0} 表示不设置过期时间；异常仅记录日志，不向调用方抛出。
     * </p>
     *
     * @param key       key
     * @param value     value, serialized by JavaSerializer
     * @param seconds   expire seconds, negative means no expire
     */
    public void set(String key, Object value, long seconds) {
        if (jedisCluster!=null) {
            try {
                byte[] valueBytes = serializer.serialize(value);
                if (seconds < 0) {
                    jedisCluster.set(key.getBytes(StandardCharsets.UTF_8), valueBytes);
                } else {
                    jedisCluster.setex(key.getBytes(StandardCharsets.UTF_8), seconds, valueBytes);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] valueBytes = serializer.serialize(value);
                if (seconds < 0) {
                    jedis.set(key.getBytes(StandardCharsets.UTF_8), valueBytes);
                } else {
                    jedis.setex(key.getBytes(StandardCharsets.UTF_8), seconds, valueBytes);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * get value
     *
     * @param key   key
     * @return  deserialized value; {@code null} when key does not exist or read fails
     */
    public Object get(String key) {
        if (jedisCluster!=null) {
            try {
                byte[] valueBytes = jedisCluster.get(key.getBytes(StandardCharsets.UTF_8));
                if (valueBytes == null) {
                    return null;
                }

                return serializer.deserialize(valueBytes);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] valueBytes = jedis.get(key.getBytes(StandardCharsets.UTF_8));
                if (valueBytes == null) {
                    return null;
                }

                return serializer.deserialize(valueBytes);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * delete key
     *
     * @param key   key; exceptions are logged and ignored
     */
    public void del(String key) {
        if (jedisCluster!=null) {
            try {
                jedisCluster.del(key.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del(key.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


}
