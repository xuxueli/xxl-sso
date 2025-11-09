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
 * @author xuxueli 2015-7-10 18:34:07
 */
public class JedisTool {
    private static final Logger logger = LoggerFactory.getLogger(JedisTool.class);


    // ---------------------- param ----------------------

    /**
     * nodes=127.0.0.1:6379
     * user=
     * password=
     */
    private String nodes;
    private String user;
    private String password;

    private int connectionTimeout = 2000;
    private int soTimeout = 2000;
    private int maxAttempts = 3;
    private Set<HostAndPort> clusterNodes = new HashSet<>();
    private JavaSerializer serializer = new JavaSerializer();

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

    private JedisPool jedisPool;
    private JedisCluster jedisCluster;

    /**
     * start
     */
    public void start() {

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
                        .timeoutMillis(soTimeout)
                        .connectionTimeoutMillis(connectionTimeout)
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
                        .timeoutMillis(soTimeout)
                        .connectionTimeoutMillis(connectionTimeout)
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
     * stop
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
        }
    }


    // ---------------------- util ----------------------

    /**
     * set
     *
     * @param key       key
     * @param value     value
     * @param seconds   seconds
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
                    long milliseconds = seconds * 1000;
                    jedis.psetex(key.getBytes(StandardCharsets.UTF_8), milliseconds, valueBytes);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * get
     *
     * @param key   key
     * @return  value
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
     * delete
     *
     * @param key   key
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
