# TLIAS 教务管理系统

基于 **Spring Boot 4.1.0 + MyBatis + MySQL + Redis** 的企业级教务管理系统后端。

## 项目结构

```
tlias-parent/                        # 父 POM，统一依赖版本管理
├── tlias-pojo/                      # 数据模型模块（POJO）
├── tlias-utlis/                     # 工具模块（JWT、异常诊断等）
└── tlias-web-management/            # Web 管理模块（主模块）
    └── src/main/java/org/example/
        ├── controller/              # 控制器层
        ├── service/                 # 业务逻辑层
        ├── mapper/                  # 数据访问层
        ├── config/                  # 配置类（CORS、MyBatis、Redis）
        ├── interceptor/             # 拦截器（Token 校验）
        ├── aop/                     # AOP 切面（操作日志）
        ├── exception/               # 全局异常处理
        └── anno/                    # 自定义注解

aliyun-oss-spring-boot-starter/      # 阿里云 OSS Starter（自定义）
aliyun-oss-spring-boot-autoconfigure/# 阿里云 OSS 自动配置
frontend/                            # 前端静态页面（Vite 构建）
├── index.html
├── favicon.ico
├── assets/                          # JS/CSS/图片资源
└── nginx.conf.example               # Nginx 反向代理配置示例
```

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.1.0 | 基础框架 |
| JDK | 25 | 编译运行环境 |
| MyBatis | 3.5.19 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 7.2+ | 缓存 + Token 黑名单 |
| JWT (jjwt) | 0.12.6 | 无状态认证 |
| PageHelper | 6.1.0 | 分页插件 |
| Aliyun OSS | 3.17.4 | 文件存储 |

## 前端说明

项目包含一个 Vite 构建的 Vue 前端（在 `frontend/` 目录），使用 Nginx 部署：

```bash
# 启动 Nginx 托管前端（端口 90）
nginx -c /path/to/nginx.conf
```

Nginx 配置将 `/api/` 请求反向代理到后端 `localhost:8080`，解决跨域问题。

## 功能模块

### 核心业务
- **部门管理** — 部门 CRUD，支持树形展示
- **员工管理** — 员工 CRUD + 工作经历（一对多），分页查询，条件筛选
- **班级管理** — 班级 CRUD，班主任关联，状态自动计算（未开班/在读/已结课）
- **学员管理** — 学员 CRUD，批量删除，按班级/学历筛选

### 系统功能
- **登录认证** — JWT Token 无状态认证，支持 JSON/表单登录
- **Token 登出** — Redis 黑名单实现 Token 主动失效
- **操作日志** — AOP 切面自动记录接口调用日志
- **统计报表** — 员工职位分布、性别统计（ECharts 数据源）
- **文件上传** — 阿里云 OSS 文件存储
- **CORS 跨域** — 已配置跨域支持

### 性能优化
- **Redis 缓存** — 声明式缓存（`@Cacheable` / `@CacheEvict`）
- 缓存部门列表、员工下拉列表、班级列表、报表数据
- 写操作自动驱逐缓存，保证数据一致

## 快速启动

### 前置条件
- JDK 25+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

### 1. 创建数据库

```sql
CREATE DATABASE tlias DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行项目根目录的 `tlias.sql` 初始化表结构和示例数据。

### 2. 配置数据库连接

修改 `tlias-web-management/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/tlias?serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 你的密码
```

### 3. 配置 Redis

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

### 4. 启动应用

```bash
cd tlias-web-management
mvn spring-boot:run
```

或直接在 IDEA 中运行 `TliasWebManagementApplication.java`

### 5. 访问 API

```
http://localhost:8080
```

## API 接口

| 方法 | 路径 | 说明 | 需 Token |
|------|------|------|---------|
| POST | `/login` | 登录认证 | ❌ |
| POST | `/logout` | 登出（Token 拉黑） | ✅ |
| GET | `/depts` | 部门列表 | ✅ |
| POST | `/depts` | 新增部门 | ✅ |
| PUT | `/depts` | 修改部门 | ✅ |
| DELETE | `/depts/{id}` | 删除部门 | ✅ |
| GET | `/emps` | 员工分页查询 | ✅ |
| GET | `/emps/{id}` | 员工详情 | ✅ |
| POST | `/emps` | 新增员工 | ✅ |
| PUT | `/emps` | 修改员工 | ✅ |
| DELETE | `/emps/{ids}` | 批量删除 | ✅ |
| GET | `/clazzs` | 班级分页查询 | ✅ |
| GET | `/students` | 学员分页查询 | ✅ |
| POST | `/upload` | 文件上传（OSS） | ✅ |
| GET | `/report/*` | 统计报表 | ✅ |

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |
| zhangsan | 123456 | 讲师 |
| lisi | 123456 | 学工主管 |

## 环境变量

| 变量 | 说明 |
|------|------|
| `ALIYUN_OSS_ACCESS_KEY_ID` | 阿里云 OSS AccessKey |
| `ALIYUN_OSS_ACCESS_KEY_SECRET` | 阿里云 OSS AccessSecret |

