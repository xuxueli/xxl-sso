

### 项目结构
- xxl-sso-server：中央认证服务，集群支持，多中心支持；
- xxl-sso-core：单点登陆Client端依赖；
- xxl-sso-samples：单点登陆Client端示例项目；
    - xxl-sso-sample-springboot
    
    
    - server ticket: cookie(server.domain) + kv(timeout 2h/append)
    - client ticket: cookie(client.domain) + encache(refresh 30min)
    - login: client>server(login 2 token)>client(token 2 ticket)>client(ticket)
    - logout: client(ticket)>server(del ticket, broadcast)>client
    
    
    
    
    
    
    
    
    