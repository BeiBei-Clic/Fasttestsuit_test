# 旅行攻略应用 - 移动软件测试作业

本项目是一个旅行攻略应用，包含Java后端和Android前端，用于完成移动软件测试工具体验作业。

## 项目结构

```
demo/
├── src/main/java/com/example/demo/    # Java后端代码
│   ├── model/                         # 实体类
│   ├── controller/                    # REST API控制器
│   ├── service/                       # 业务逻辑服务
│   ├── repository/                    # 数据访问层
│   ├── exception/                     # 异常处理
│   └── config/                        # 配置类
│
└── android-app/                       # Android前端
    └── app/src/main/
        ├── java/                      # Java代码
        └── res/                       # 资源文件
```

## 后端 (Java Spring Boot)

### 主要功能

1. **用户管理** - 注册、登录、信息管理
2. **目的地管理** - 目的地CRUD、搜索、评分
3. **攻略管理** - 攻略CRUD、浏览、点赞
4. **用户行为追踪** - 记录用户在应用中的行为
5. **异常测试接口** - 专门用于测试工具捕获异常

### API接口

#### 用户相关
- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `POST /api/users/logout` - 用户登出
- `GET /api/users/me` - 获取当前用户信息

#### 目的地相关
- `GET /api/destinations` - 获取所有目的地
- `GET /api/destinations/{id}` - 获取目的地详情
- `GET /api/destinations/search?location=xxx` - 搜索目的地
- `GET /api/destinations/popular` - 获取热门目的地

#### 攻略相关
- `GET /api/guides` - 获取所有攻略
- `GET /api/guides/{id}` - 获取攻略详情
- `GET /api/guides/search?keyword=xxx` - 搜索攻略
- `POST /api/guides/{id}/like` - 点赞攻略

#### 用户行为分析
- `POST /api/behavior/page-view` - 记录页面访问
- `POST /api/behavior/record` - 记录用户行为
- `GET /api/behavior/user/{userId}` - 获取用户行为记录
- `GET /api/behavior/session/{sessionId}` - 获取会话行为记录

#### 异常测试接口（供测试工具使用）
- `GET /api/test` - 获取所有测试接口列表
- `GET /api/test/null-pointer?trigger=true` - 触发空指针异常
- `GET /api/test/index-out-of-bounds?index=10` - 触发数组越界异常
- `GET /api/test/arithmetic?divisor=0` - 触发算术异常
- `GET /api/test/number-format?number=abc` - 触发数字格式异常
- `GET /api/test/resource-not-found?id=999` - 触发资源未找到异常
- `GET /api/test/business-error?trigger=true` - 触发业务异常

### 启动后端

```bash
# 进入项目目录
cd demo

# 编译运行
./gradlew bootRun

# 或者使用IDE运行 DemoApplication.java
```

后端启动后访问：
- API地址: http://localhost:8080
- H2数据库控制台: http://localhost:8080/h2-console

## Android前端

### 主要功能

1. **用户登录/注册**
2. **目的地列表展示**
3. **目的地详情查看**
4. **攻略浏览和详情**
5. **搜索功能**
6. **用户行为追踪** - 自动记录页面访问和用户操作

### 构建Android应用

1. 使用Android Studio打开 `android-app` 目录
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击Run按钮安装应用

### 配置后端地址

如果后端不在本机，需要修改 `ApiClient.java` 中的 `BASE_URL`：

```java
// 模拟器访问本机
private static final String BASE_URL = "http://10.0.2.2:8080/";

// 真机访问（改为实际IP）
private static final String BASE_URL = "http://192.168.1.xxx:8080/";
```

## 测试作业使用指南

### 1. 准备工作

1. 启动Java后端服务
2. 安装Android应用到手机或模拟器
3. 注册并登录Fasttestsuit网站 (www.i-test.com.cn)

### 2. 上传应用

1. 将Android项目打包成APK
2. 在Fasttestsuit网站上上传APK文件

### 3. 下载SDK并集成

1. 从Fasttestsuit下载SDK
2. 将JAR包放入 `android-app/app/libs/` 目录
3. 在 `build.gradle` 中添加依赖

### 4. 运行测试

1. 编译运行集成SDK后的应用
2. 在应用中进行各种操作
3. 登录Fasttestsuit网站查看测试结果

### 5. 测试要点

#### 用户行为分析
- 应用自动记录页面访问顺序
- 记录点击、搜索、点赞等行为
- 可通过 `/api/behavior/session/{sessionId}` 查看访问顺序

#### 后台Java异常捕获
后端提供了专门的异常测试接口：

```bash
# 测试空指针异常
curl "http://localhost:8080/api/test/null-pointer?trigger=true"

# 测试数组越界
curl "http://localhost:8080/api/test/index-out-of-bounds?index=10"

# 测试除零异常
curl "http://localhost:8080/api/test/arithmetic?divisor=0"
```

这些异常会被后端日志记录，测试工具可以捕获分析。

## 预置测试数据

系统启动时会自动初始化以下数据：

### 用户
- 用户名: testuser, 密码: 123456
- 用户名: admin, 密码: admin123

### 目的地
- 北京、上海、广州、成都、杭州

### 攻略
- 北京三日游攻略
- 成都美食之旅

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.5
- Spring Data JPA
- H2 Database

### 前端
- Android SDK
- Retrofit (网络请求)
- Material Design

## 注意事项

1. 后端使用H2内存数据库，重启后数据会重置
2. Android应用需要网络权限
3. 测试时确保手机和后端在同一网络
4. 异常测试接口仅供测试使用，不要在生产环境使用
