# XXL-SSO 研发规范

> **定位**：XXL-SSO 是一个分布式单点登录框架，仓库包含核心 SDK（`xxl-sso-core`）、CAS 认证中心（`xxl-sso-server`）与多场景接入示例（`xxl-sso-samples`）。本文件沉淀本仓库的架构约定、API 设计、编码规范、测试与发布流程。
>
> **读者**：参与本仓库研发的工程师与 AI 编程助手。
>
> **硬约束**：新增或修改代码前先阅读本文件；同类能力以仓库现有实现为准，不另起一套写法；`xxl-sso-core` 是对外发布的公共 SDK，任何公共类、方法、枚举、常量的变更都按兼容性契约处理。

## 文档导航

| 我要做的事 | 入口 |
|---|---|
| 了解仓库定位、模块划分与包结构 | 一、项目架构 |
| 拿捏实现取舍（简单 / 抽象 / 复用 / 兼容） | 二、设计原则 |
| 命名、依赖版本、注释、日志 | 三、通用约定 |
| 设计核心 SDK 的类 / 接口 / 注解 | 四、API 设计规范 |
| 编写具体模块（auth、store、token、helper…） | 五、模块实现规范 |
| 补测试 | 六、测试规范 |
| 更新 README、官方文档、版本与发布 | 七、文档与发布 |
| 需求记录与官方文档同步 | 八、需求与文档 |
| 从需求到提交走完整流程 | 九、开发流程 |
| 交付前自查 | 十、验收清单 |

---

## 一、项目架构

### 1.1 仓库定位

- Maven 多模块工程：父级 `com.xuxueli:xxl-sso`（`pom`），聚合 `xxl-sso-core`、`xxl-sso-server`、`xxl-sso-samples`。
- **对外发布的只有 `xxl-sso-core`**；`xxl-sso-server`、`xxl-sso-samples` 仅用于演示与联调，`maven.install.skip` / `maven.deploy.skip` 已开启。
- 运行环境：JDK 17+、Maven 3+；源码与资源统一 UTF-8；Web 层基于 Jakarta EE（`jakarta.servlet`）与 Spring Framework 6/7+。
- 设计目标：轻量级、高扩展、渐进式；按需引入依赖，核心 SDK 不把可选框架强加给使用方。

### 1.2 模块地图

```text
xxl-sso
├── xxl-sso-core        核心 SDK（jar，发布到 Maven Central）
│   ├── annotation      @XxlSso：登录 / 权限 / 角色声明式校验
│   ├── auth            登录认证组件（按容器与登录类型划分，见 1.3）
│   ├── bootstrap       XxlSsoBootstrap：生命周期入口，初始化 Helper
│   ├── constant        Const：Cookie/Header key、错误码、有效期常量
│   ├── exception       XxlSsoException：统一运行时异常，携带 errorCode
│   ├── helper          XxlSsoHelper（Servlet）/ XxlSsoReactiveHelper（WebFlux）
│   ├── model           LoginInfo：统一登录用户模型
│   ├── path            PathMatcher + AntPathMatcher：排除路径匹配
│   ├── store           LoginStore SPI + LocalLoginStore / RedisLoginStore
│   ├── token           TokenHelper：LoginInfo 与 token 互相转换
│   └── util            JedisTool：Redis 连接与序列化封装
├── xxl-sso-server      CAS 认证中心（Spring Boot 应用，不发布）
└── xxl-sso-samples     接入示例（9 个 Spring Boot 应用，不发布）
```

### 1.3 登录类型与认证组件矩阵

登录类型：

| 类型 | 适用场景 | 登录凭证载体 | 特点 |
|---|---|---|---|
| Web | 单体系统、同域名多系统 | 同域名 Cookie | 简单，受域名限制 |
| Native | APP / 小程序 / 前后端分离 / 无 Cookie 场景 | 请求 Header（默认 `xxl-sso-token`） | 不受域名限制，客户端自管理 token |
| CAS | 多系统跨域、企业统一登录 | CAS Server Cookie + 跳转 Ticket | 需独立部署认证中心 |

认证组件必须成矩阵维护，三个容器下同名能力行为保持一致：

| 容器 | Web | Native | CAS |
|---|---|---|---|
| Servlet Filter | `XxlSsoWebFilter` | `XxlSsoNativeFilter` | `XxlSsoCasFilter` |
| Spring MVC Interceptor | `XxlSsoWebInterceptor` | `XxlSsoNativeInterceptor` | `XxlSsoCasInterceptor` |
| Spring WebFlux WebFilter | `XxlSsoWebReactiveFilter` | `XxlSsoNativeReactiveFilter` | `XxlSsoCasReactiveFilter` |

约束：

- 认证组件只做「取 token → 调 Helper 校验 → 放行 / 跳转 / 返回错误」，业务逻辑集中在 `XxlSsoHelper` / `XxlSsoReactiveHelper`。
- 新增登录类型或容器时，同步补齐矩阵中的实现与对应示例项目，不允许只做单端。
- 响应式（WebFlux）实现不得依赖 `jakarta.servlet` 类型。

### 1.4 依赖方向与边界

- `xxl-sso-core` 是底座，禁止依赖 `xxl-sso-server`、`xxl-sso-samples`。
- `xxl-sso-server`、`xxl-sso-samples` 依赖 `xxl-sso-core`，不得把示例专用代码反向塞进 core。
- core 内的可选依赖一律 `provided`：`jakarta.servlet-api`、`spring-webmvc`、`reactor-core`、`jedis`。
- 可选依赖未引入时，类加载不得报错，仅在使用对应 API 时报错并给出明确提示。
- 第三方能力优先复用 `xxl-tool`（`Response`、`GsonTool`、`CookieTool`、`Base64Tool`、`JavaSerializer` 等），不重复造轮子。

---

## 二、设计原则

按优先级执行；冲突时先保证简单、职责清晰与向后兼容。

> 速记：**能删就删 → 第三次再抽 → 一个变更理由 → 已有方案优先 → 不破坏使用者**。

| 原则 | 核心要求 | 落地判据 / 禁止 |
|---|---|---|
| **KISS 优先** | 先写最直接能工作的实现 | 禁止为假想需求预留扩展点、配置项、包装层 |
| **DRY，但只在必要时抽象** | 同一逻辑出现**第三次**再抽公共实现 | 两处相似但边界不同的代码可保留；禁止提前建抽象层 |
| **单一职责** | 一个类/方法只承担一个变更理由 | 认证组件不掺业务；Helper 不掺容器细节 |
| **同类场景同一方案** | 已有组件/接口/模式能覆盖就不另起一套 | 禁止 Web/Native/CAS 三套逻辑各写一份 |
| **向后兼容优先** | core 的公共 API 是契约，删除/改签名按废弃流程走 | 禁止在 patch/minor 版本静默删除或改变语义 |
| **零侵入、按需依赖** | 不把可选框架强加给使用方 | 禁止把 core 的可选依赖改成 `compile` 传递依赖 |

补充约束：

- 抽象前先回答：是否已重复第三次？现有工具是否已覆盖？现在是否真的需要？三个答案不成立就不抽象。
- 修复问题优先改现有实现，不平行复制一份「新版本」；确需重写时保留旧实现一个周期并标注 `@Deprecated`。
- 新增能力先查 JDK 17、`xxl-tool` 与已有模块是否已覆盖，避免重复造轮子。

---

## 三、通用约定

### 3.1 语言与命名

- 沟通与提交说明使用中文；标识符使用英文，表达真实语义，禁止拼音与无意义单字母。
- 类/接口/枚举：大驼峰（`XxlSsoHelper`、`LoginStore`）；方法/变量：小驼峰；常量：全大写下划线（`XXL_SSO_TOKEN`）。
- 核心前缀统一 `XxlSso`：认证组件 `XxlSso{Type}{AuthType}`，如 `XxlSsoCasFilter`、`XxlSsoWebReactiveFilter`。
- 接口 `Xxx` / `XxxStore`；实现 `XxxImpl` 或「存储介质 + 语义」，如 `LocalLoginStore`、`RedisLoginStore`；匹配器实现 `AntPathMatcher`。
- 布尔字段与方法不用 `isXxx` 前缀做字段名（避免与 getter 冲突）；集合返回值用复数（`roleList`、`permissionList`）。
- 包名全小写、单数能力名（`store`、`token`、`helper`）；避免出现 `util`、`common` 这类无边界包，确需工具类时放入 `util` 并保持单一职责。

### 3.2 依赖与版本

- **版本一律集中在父级 `pom.xml`**：依赖与插件版本写入 `<properties>` + `dependencyManagement`；子模块只引用，不写具体版本（`spring-boot-maven-plugin` 使用 `${spring-boot.version}` 除外）。
- 版本写精确值；禁止 `LATEST`、`RELEASE`、区间版本。升级依赖必须：查最新稳定版 → 更新父级属性 → 全量编译 + 相关测试 → 同步版本更新日志。
- `xxl-sso-core` 的可选依赖必须为 `provided`，与现有 `jakarta.servlet-api`、`spring-webmvc`、`reactor-core`、`jedis` 保持一致。
- 基础依赖保持克制：`slf4j-api` 为运行依赖；`slf4j-reload4j`、`junit-jupiter` 仅测试使用。
- 新增依赖必须说明必要性、作用域与文档同步点；禁止为单个示例引入重量级依赖。
- 不引入 Lombok、字节码增强、运行时代理等改变使用方构建体验的依赖。

### 3.3 注释与 Javadoc

对外 API 必须有 Javadoc；注释解释「做什么 / 为什么」，不复述代码。存量代码以英文类说明为主，新增代码保持所在文件的语言风格一致。

| 层级 | 要求 |
|---|---|
| 文件/类型 | 类、接口、枚举使用 Javadoc：功能描述 + 空行 + `@author 作者 yyyy-MM-dd` |
| 方法 | `public` / `protected` 方法使用 Javadoc；补 `@param`、`@return`、`@throws`；**DTO 的简单 getter/setter 不写 Javadoc** |
| 字段 | 业务字段使用 Javadoc，说明含义、单位、可空性与取值；`LoginInfo` 等模型字段必须写全 |
| 行为契约 | 空值语义、线程安全、超时单位、边界行为、Cookie/Header key 必须写清 |
| 分支 | `//` 置于分支/提前返回上方，说明原因（如「token 非法时清理 Cookie」） |

```java
/**
 * xxl sso login info
 *
 * @author xuxueli 2018-04-02 19:59:49
 */
public class LoginInfo implements Serializable {

    /**
     * user id
     */
    private String userId;

    /**
     * expire time, epoch seconds
     */
    private long expireTime;
}
```

禁止：无意义注释（`// 返回结果`）、注释掉的死代码、长期滞留的 `TODO`（要么建 Issue，要么删除）、与实现矛盾的注释。

### 3.4 日志

- 统一使用 `org.slf4j.Logger` / `LoggerFactory`，禁止 `System.out`、`printStackTrace()`（存量测试中的临时输出应在修改该测试时移除）。
- 使用占位符，禁止字符串拼接：

```java
logger.debug("xxl-sso login check fail, token is invalid");
```

- 级别：`debug` 记录内部流程与可恢复异常；`info` 仅记录生命周期关键节点（启动、停止）；`warn`/`error` 必须可行动，禁止在循环中刷屏。
- 日志不得包含密码、完整 token、Cookie 值、密钥、手机号等敏感信息。
- SDK 默认安静：正常认证路径不打 `info`；排查信息通过 `debug` 输出。

---

## 四、API 设计规范

### 4.1 公共 API 形态

- **门面类无状态**：`XxlSsoHelper` / `XxlSsoReactiveHelper` 以静态方法对外，内部持有由 `XxlSsoBootstrap` 注入的单例状态；禁止调用方自行 new。
- **扩展点用接口**：`LoginStore`、`PathMatcher` 是 SPI；新增存储或匹配策略实现接口并在配置处装配，禁止修改接口既有方法签名。
- **注解无副作用**：`@XxlSso` 只声明登录 / 权限 / 角色要求，校验逻辑集中在认证组件。
- **模型可序列化**：`LoginInfo` 实现 `Serializable` 且显式声明 `serialVersionUID`；新增字段必须兼容旧数据（默认值可解析）。
- 构造器注入：认证组件通过构造器接收 `excludedPaths`、`loginPath`、`serverAddress` 等配置，字段不可变优先。

### 4.2 返回值与空值契约

- 统一使用 `com.xxl.tool.response.Response<T>` 表达结果：成功 `Response.ofSuccess(data)`，失败 `Response.ofFail(msg)`；禁止返回 `null` 表示失败。
- 错误码集中定义在 `Const`（`CODE_LOGIN_ERROR`、`CODE_LOGIN_FAIL`、`CODE_PERMISSION_FAIL`、`CODE_ROLE_FAIL`），禁止业务代码散落魔法数字。
- 查询/校验类方法未命中返回明确的失败 `Response`；解析类方法（如 `TokenHelper.parseToken`）非法输入返回 `null` 并在 Javadoc 写明。
- 参数非法时优先抛 `IllegalArgumentException`；框架级错误抛 `XxlSsoException`（携带 errorCode）。

### 4.3 异常

- `XxlSsoException` 是运行时异常，默认错误码 `Const.CODE_LOGIN_ERROR`，需要区分场景时使用 `(errorCode, msg)` 构造器。
- 受检异常尽量在边界转换；捕获异常必须有处理：记录日志、降级、重新抛出三选一；禁止空 `catch`。
- 异常信息包含可定位上下文（key、参数名、场景），但不得泄露 token、密码等敏感数据。

### 4.4 线程安全与生命周期

- `XxlSsoHelper` 的初始化状态由 `XxlSsoBootstrap.start()` 写入、`stop()` 释放；重复 `start/stop` 必须幂等，启动顺序为「Store 启动 → Helper 初始化」。
- `LoginStore` 实现必须声明并发语义：`LocalLoginStore` 基于 `ConcurrentHashMap`，`RedisLoginStore` 依赖 Jedis 连接池线程安全。
- `JedisTool` 的 `start()` 幂等创建连接池 / 集群客户端，`stop()` 释放资源；集群与单机分支行为保持一致。
- 认证组件本身应无共享可变状态；`AntPathMatcher` 可复用实例。

### 4.5 性能与资源

- 热路径（每次请求）避免创建 `Pattern`、频繁 JSON 编解码与字符串拼接；路径匹配使用现有 `AntPathMatcher`。
- 所有连接、连接池、流使用 try-with-resources 或显式关闭；`Jedis` 资源必须归还连接池。
- 有容量语义的容器必须支持上限或淘汰策略，禁止无界增长。

### 4.6 安全

- Token 由 `TokenHelper` 生成：`LoginInfo` 仅携带 `userId + signature` 做 Base64Url 编码；签名校验在 `XxlSsoHelper.loginCheck` 完成，禁止绕开签名直接信任 token 内容。
- Cookie 写入统一走 `CookieTool`，key 使用 `Const.XXL_SSO_TOKEN`（可配置覆盖）；登出必须同时清理服务端登录态与客户端 Cookie。
- CAS Ticket 一次性使用、短时效（1s～3min），校验后立即失效；禁止延长有效期换取便利。
- 登录态有效期支持自动续期（过半续期），续期逻辑集中在 Helper，禁止各认证组件自行实现。
- 禁止硬编码密钥、账号、Token；示例配置使用占位符。日志与异常不得输出完整 token。
- 对外部输入（redirect url、ticket、token）做白名单或格式校验，防范开放重定向与伪造票据。

### 4.7 兼容性与废弃

- `xxl-sso-core` 的公共类/方法签名、返回语义、`Const` 常量、SPI 接口均视为兼容契约：
  - 新增能力用**重载 / 新方法**，不修改既有签名；
  - 不改变既有方法的空值、异常、线程安全语义；
  - `Serializable` 类显式声明 `serialVersionUID`。
- 废弃流程：
  1. 类或方法加 `@Deprecated`，Javadoc 加 `@deprecated` 说明替代方案与起始版本；
  2. 在官方文档「版本更新日志」标注废弃与移除计划；
  3. 未确认使用方迁移前，禁止直接删除。
- 破坏性变更必须提升主版本或明确在日志中标注迁移方式，禁止隐性破坏。

---

## 五、模块实现规范

### 5.1 auth（登录认证组件）

- 三种登录类型（Web / Native / CAS）与三种容器（Filter / Interceptor / Reactive）必须一一对应，成对修改。
- 统一流程：解析 `excludedPaths`（Ant 表达式，逗号分隔）→ 取 token（Cookie / Header / Ticket）→ 调 Helper 校验 → 校验 `@XxlSso` 的 login / permission / role → 放行或返回错误。
- 未登录响应需区分场景：页面请求跳转登录页 / CAS Server；Ajax 与 Native 请求返回 JSON（`Response`）；Reactive 场景使用 `Mono` 写回。
- 排除路径、登录路径、CAS Server 地址等配置必须支持构造器注入，默认值回退到 `Const`。
- 新增认证组件必须补齐单元测试或示例可运行验证。

### 5.2 store（登录态持久化）

- `LoginStore` 是 SPI：核心方法 `set / update / remove / get`，CAS 扩展 `createTicket / validTicket` 提供默认实现（不支持时抛 `XxlSsoException`）。
- 存储 key 统一带前缀：`Const.XXL_SSO_STORE_PREFIX` 或配置的 `storeKeyPrefix`；userId 与 ticket 的 key 规则集中在实现内。
- 新实现必须说明：序列化方式、过期语义、并发安全、`start/stop` 资源释放。
- 不得在接口中新增抽象方法而不提供 `default` 实现，避免破坏第三方实现。

### 5.3 token / helper

- `TokenHelper` 只负责 token 编解码与基本合法性校验；过期、签名、续期、存储校验在 Helper 完成。
- `XxlSsoHelper`（Servlet）与 `XxlSsoReactiveHelper`（WebFlux）能力保持一致：login / loginWithCookie / loginUpdate / logout / loginCheck / createTicket / validTicket / hasRole / hasPermission。
- 新增 Helper 方法时必须同步两端（Servlet 与 Reactive），并保持返回 `Response` 的语义一致。
- 静态单例的初始化入口只有 `XxlSsoBootstrap`；禁止在 Helper 内隐式创建 Store。

### 5.4 bootstrap / constant / exception

- `XxlSsoBootstrap` 只做装配与生命周期（`setLoginStore`、`setTokenKey`、`setTokenTimeout`、`start`、`stop`），不承载业务逻辑。
- 新增常量放入 `Const` 并写 Javadoc；Cookie/Header/Attribute 的 key 不得在代码中硬编码。
- 错误码沿用 `3xx` 段位并集中维护；新增错误码同步官方文档。

### 5.5 path / util

- 路径匹配优先复用 `PathMatcher` / `AntPathMatcher`，禁止各组件自行实现通配符匹配。
- `JedisTool` 统一封装单机与集群、序列化与异常处理；业务代码不直接依赖 Jedis 类型。
- `JedisTool` 的异常策略为「记录日志并降级」，调用方需能处理 `get` 返回 `null` 的情况。

### 5.6 xxl-sso-server（CAS 认证中心）

- 仅作为认证中心示例与联调服务：登录页、账号校验（Mock）、CAS 跳转、注销。
- 配置通过 `application.properties` + `@Value` 注入，装配集中在 `XxlSsoConfig`；禁止在 Controller 中直接创建 Store。
- 新增服务端能力需评估是否应下沉到 core；仅认证中心专用的逻辑留在 server。

### 5.7 xxl-sso-samples（接入示例）

- 命名规则：`xxl-sso-sample-{authType}-{loginType}`，如 `xxl-sso-sample-filter-cas`、`xxl-sso-sample-reactive-web`。
- 目录结构：`XxlSsoXxxApplication` + `config/XxlSsoConfig` + `controller` + `resources`；Native/CAS 示例可增加 `openapi` 等子包。
- 端口约定：CAS `8081`、Native `8082`、Web `8083`；新增示例沿用该约定，避免冲突。
- 示例必须可独立启动，配置项（token key/timeout、Redis 地址）与 server 保持一致；示例中禁止硬编码生产密钥。
- 修改 core 公共行为时，至少验证对应的一个示例可启动、可完成登录 / 校验 / 注销主流程。

---

## 六、测试规范

- 框架：JUnit 5（`org.junit.jupiter.api.Test`）；测试代码放 `src/test/java/com/xxl/sso/core/test/{module}/`，类名 `XxxTest`。
- 测试方法名表达场景：`loginTest`、`logoutTest`、`ticketTest`、`multiThreadPutTest`；一个方法聚焦一个行为。
- 新增/修改功能必须**包含断言**（`org.junit.jupiter.api.Assertions`），不得只打印日志靠人工判断；存量纯日志测试在改动时逐步迁移。
- 必测边界：`null`、空串/空集合、过期 token、非法签名、票据超时、路径不匹配、异常分支。
- 依赖 Redis 的测试需在本地启动 Redis（默认 `127.0.0.1:6379`），无 Redis 环境下优先运行 `LocalLoginStore`、`TokenHelper` 等无外部依赖用例。
- 本地执行（仓库默认跳过测试，需显式开启）：

```bash
# 全量构建（默认跳过测试）
mvn -B package

# 开启测试
mvn -B -Dmaven.test.skip=false test

# 只跑某模块 / 某用例
mvn -B -Dmaven.test.skip=false -pl xxl-sso-core -Dtest='TokenHelperTest' test
```

- CI 执行 `mvn -B package`；提交前本地至少完成编译 + 相关模块测试。
- 不提交只用于本地调试的临时测试；不修改无关测试来「让构建变绿」。

---

## 七、文档与发布

### 7.1 文档同步（强制）

| 变更 | 必须同步 |
|---|---|
| 新增/调整公共 API、模块能力 | `README.md` 对应特性说明；`doc/XXL-SSO官方文档.md` 对应章节 |
| 新增/调整登录类型、认证组件 | 官方文档「接入项目示例」与组件说明；必要时补示例项目 |
| 新增配置项 / 默认值变化 | 官方文档配置说明 + 示例 `application.properties` |
| 依赖升级 / 行为变更 / 废弃 | 官方文档「五、版本更新日志」 |
| 版本号 | 根 `pom.xml` 与各子模块 parent 版本 |

- README 与官方文档的模块描述保持一致的措辞与顺序；不产生两套互相矛盾的说明。
- 文档示例代码必须可编译或明确标注伪代码；不在文档中留下过时 API。

### 7.2 版本与发布

- 开发中版本使用 `x.y.z-SNAPSHOT`；发布前去 `-SNAPSHOT`。
- 发布流程：更新版本号 → 更新更新日志 → 编译 + 测试 → 生成 source/javadoc → GPG 签名 → 发布 Maven Central（仅 `xxl-sso-core`）。
- 更新日志使用统一前缀：`【新增】`、`【优化】`、`【修复】`、`【升级】`、`【废弃】`，末条注明版本与日期。
- 破坏性变更必须提升主版本或明确在日志中标注迁移方式，禁止隐性破坏。
- 发布 profile 中的 `excludeArtifacts` 必须覆盖全部非发布模块，新增示例 / 服务端模块时同步补充。

---

## 八、需求与文档

- 非平凡需求（新增登录类型、认证组件、公共 API、存储实现、依赖或行为变更）先明确：使用场景、输入输出、空值/异常/并发语义、兼容性、验证范围，再编码。
- 需求结论优先落盘到与任务相关的 `doc/` 文档或 Issue，写清「选了什么、为什么、影响什么」；不堆砌过程性描述，未确认项标注「待确认」。
- 实现结果与最初结论不一致时，先更新结论再改代码，避免文档与实现脱节。
- 通用规范引用本文件章节号，不在需求文档中复制粘贴。
- 不写假数据：未执行的测试、未测量的性能、未确认的兼容性不得写成结论。

---

## 九、开发流程

1. **需求确认**：明确登录类型 / 容器 / 存储方式、输入输出、空值/异常/并发语义、是否引入依赖；确认能力归属 core、server 还是 samples。
2. **方案落位**：确定包路径与类名；先查重，避免与现有认证组件、Helper 方法、路径匹配能力重复。
3. **实现**：遵循第四、五章；公共 API 先写 Javadoc 契约，再写实现；认证矩阵（Filter / Interceptor / Reactive）保持同步。
4. **测试**：按第六章补断言与边界用例，本地执行通过。
5. **示例验证**：core 行为变更时至少选择一个对应示例完成登录 / 校验 / 注销主流程验证。
6. **文档**：按 7.1 同步 README、官方文档、配置说明与版本更新日志。
7. **自查**：逐项勾选第十章清单。
8. **提交**：只提交任务相关文件，不提交 `target/`、`.DS_Store`、IDE 配置、临时文件；提交信息简洁，遵循仓库风格（`feat: ...` / `fix: ...` / `docs: ...` / 中文简述均可，同一 PR 保持一致）。

AI 助手补充约定：

- 改动前先阅读同模块现有实现与测试，保持风格一致；不做与任务无关的重构与格式化。
- 不擅自新增/升级依赖；确需升级时更新父级 `pom.xml` 的版本属性，并同步版本更新日志。
- 不修改公共 API 语义；无法确认兼容性时先询问。
- 完成后必须给出：改动文件、验证命令与结果、文档同步情况。

---

## 十、验收清单

**设计**

- [ ] 能力归属正确（core / server / samples），未与现有组件或 `xxl-tool` 重复
- [ ] 认证矩阵（Web / Native / CAS × Filter / Interceptor / Reactive）保持同步
- [ ] 公共 API 命名符合约定，Javadoc 写清空值、异常、线程安全、单位与边界
- [ ] 未破坏既有签名/语义；废弃项已按流程标注

**实现**

- [ ] 无 `System.out` / `printStackTrace` / 空 `catch` / 死代码 / 无意义注释
- [ ] 日志使用占位符，无 token / 密码 / Cookie 等敏感信息
- [ ] 资源正确关闭；Jedis 连接归还连接池；`start/stop` 幂等
- [ ] 无静态可变状态；有状态组件已标注线程安全语义
- [ ] 安全默认值正确（签名校验、票据一次性、Cookie 安全属性、防开放重定向）

**依赖与构建**

- [ ] 依赖与插件版本集中在父级 `pom.xml`，版本精确
- [ ] core 可选依赖为 `provided`；未引入时不影响其他类加载
- [ ] `mvn -B package` 通过；`mvn -B -Dmaven.test.skip=false test` 通过（无 Redis 依赖用例）
- [ ] 未提交 `target/`、`.DS_Store` 等构建产物

**测试**

- [ ] 新增/修改逻辑有断言；覆盖 `null`、空值、过期、非法签名、票据、路径边界
- [ ] 认证组件与存储实现的异常分支已验证

**文档**

- [ ] README 与官方文档对应章节已同步
- [ ] 新增配置项 / 行为变更 / 依赖升级已写入版本更新日志
