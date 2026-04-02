# 旅行攻略 App —— 移动软件测试工具体验（作业六）

> 软件测试与质量控制 · 作业六：移动软件测试工具体验
> 学号 / 姓名：202267330269 / 徐宇鸿

本项目是一个 **Android 旅行攻略 App（前台）+ Java Spring Boot 服务（后台）** 的前后台综合应用，用于体验移动端 APM / 崩溃监控测试工具，完成异常捕获、崩溃分析、用户行为分析的完整闭环。

---

## 一、作业要求与完成情况

| 作业要求 | 完成情况 |
| --- | --- |
| 上传应用有源代码 | ✅ Android 前台 + Java 后台全源码开源 |
| 后台应用必须 Java 实现 | ✅ Spring Boot 3.4.4 + Java 17 |
| 前台 Android 或 iOS | ✅ Android（旅行攻略 App）|
| 捕获后台 Java 应用异常 | ✅ 7 种异常接口 + 完整堆栈采集 |
| 程序异常 / 崩溃分析报告 | ✅ 腾讯 Bugly 实时采集 5 种崩溃（含 Native 崩溃）|
| 用户行为分析 | ✅ 后端会话级页面访问顺序（19 步路径）|
| 网站测试异常或不好用的功能点 | ✅ 原平台 Fasttestsuit 实测故障（证书过期 + 接口 500）|
| 加分项：前后台综合应用 | ✅ Android 前台 + Java 后台 + 第三方 APM 工具三端打通 |

---

## 二、测试工具：Fasttestsuit → 腾讯 Bugly

### 2.1 原计划：Fasttestsuit (i-test.com.cn)

按作业原始要求，本应使用 Fasttestsuit 作为移动端 APM 测试工具。实测发现该平台存在**导致核心功能完全不可用的严重问题**（详见 `作业六_移动软件测试工具体验_Fasttestsuit.md`）：

| 故障 | 实测证据 |
| --- | --- |
| **TLS 证书已过期半年多** | 证书有效期 `2025-09-08 ~ 2025-12-06`，截至测试日 `2026-06-19` 已过期 6 个多月。`curl` 报 `SSL certificate problem: certificate has expired`，SDK 数据无法通过 HTTPS 上报。 |
| **核心接口服务端崩溃** | SDK 启动调用的 `https://i-test.com.cn/PerformanceMonitorCenter/loadSDKConfig` 对**任意 appId**（含随机 UUID）均返回 **HTTP 500**，堆栈为 `NullPointerException at ConfigService.loadSDKConfigByappId(ConfigService.java:52)`。 |

以上两条客观故障既是本次作业无法继续使用原平台的根因，也是作业要求的"**网站测试异常或不好用的功能点**"的核心素材。

### 2.2 替代方案：腾讯 Bugly

为保证"移动端崩溃监控工具体验"的核心目标达成，改用国内主流同类工具 **腾讯 Bugly**（功能定位与 Fasttestsuit 完全一致）：

- 平台：https://bugly.qq.com
- AppID：`3f957d9605`
- SDK：`com.tencent.bugly:crashreport:4.1.9.3`

---

## 三、项目结构

```
demo/
├── src/main/java/com/example/demo/         # Java 后端（Spring Boot 3.4.4 + Java 17）
│   ├── DemoApplication.java                # 启动入口
│   ├── controller/
│   │   ├── TestExceptionController.java    # ★ 7 种后台异常触发接口
│   │   ├── UserBehaviorController.java     # ★ 用户行为记录 / 会话查询接口
│   │   ├── UserController.java             # 用户注册 / 登录
│   │   ├── DestinationController.java      # 目的地 CRUD
│   │   └── TravelGuideController.java      # 攻略 CRUD
│   ├── exception/
│   │   └── GlobalExceptionHandler.java     # ★ 全局异常处理 + printStackTrace
│   ├── model/  service/  repository/       # 数据层（JPA + H2）
│   └── config/DataInitializer.java         # 启动时初始化测试数据
│
├── android-app/                            # Android 前台（旅行攻略 App）
│   └── app/src/main/java/com/example/travelguide/
│       ├── TravelGuideApplication.java     # ★ Bugly SDK 初始化入口
│       ├── activity/                       # 登录 / 注册 / 主页 / 详情 / 搜索
│       ├── utils/
│       │   ├── CrashTester.java            # ★ 7 种崩溃主动触发工具
│       │   ├── BehaviorTracker.java        # 用户行为采集上报
│       │   └── SessionManager.java         # 会话管理
│       └── api/                            # Retrofit 接口封装
│
├── 截图/                                    # ★ 所有报告截图素材
│   ├── 控制台截图.png                       # 后端启动日志
│   ├── api 截图.png                         # 后端异常接口响应
│   ├── 出错堆栈.png                         # 后端异常堆栈
│   ├── 崩溃页列表.png                       # Bugly 崩溃列表（5 种崩溃）
│   ├── 截图素材_后端启动日志.txt
│   ├── 截图素材_后端异常堆栈.txt            # 7 种异常完整堆栈
│   ├── 截图素材_Bugly上报日志.txt           # SDK 初始化 + 崩溃上报成功日志
│   └── 截图素材_用户行为与页面访问顺序.txt  # 会话级页面访问路径
│
├── 作业六_移动软件测试工具体验_Fasttestsuit.md   # 作业要求 + Fasttestsuit 故障记录
├── 徐宇鸿 202267330269 作业二 移动软件测试工具体验.pdf   # 最终分析报告
└── README.md                               # 本文件
```

---

## 四、测试结果概览

### 4.1 后台 Java 应用异常（7 种）

通过 `GET /api/test/*` 系列接口触发，由 `GlobalExceptionHandler` 统一捕获并通过 `printStackTrace()` 输出堆栈：

| 异常类型 | 接口 | HTTP | 错误信息 |
| --- | --- | --- | --- |
| NullPointerException | `/null-pointer` | 500 | `Cannot invoke "String.length()" because "nullString" is null` |
| ArrayIndexOutOfBoundsException | `/index-out-of-bounds` | 500 | `Index 10 out of bounds for length 5` |
| ArithmeticException | `/arithmetic` | 500 | `/ by zero` |
| NumberFormatException | `/number-format` | 400 | `无法将 'abc' 转换为数字` |
| BusinessException | `/business-error` | 400 | `这是一个测试业务异常的消息` |
| IllegalArgumentException | `/illegal-argument` | 400 | `参数值不能为负数: -1` |
| ResourceNotFoundException | `/resource-not-found` | 404 | `测试资源 未找到，id: '999'` |

> 完整堆栈见 `截图/截图素材_后端异常堆栈.txt`

### 4.2 移动端崩溃采集（腾讯 Bugly）

App 内置"崩溃测试"菜单（`CrashTester.java`），可触发 7 类崩溃。Bugly 控制台实时采集到 **5 种**：

| 崩溃 | 类型 | 来源 |
| --- | --- | --- |
| #2 | `java.lang.NullPointerException` | CrashTester 自定义 |
| #1002 | `java.lang.ArrayIndexOutOfBoundsException` | CrashTester 自定义 |
| #4 | `java.lang.ArithmeticException` | CrashTester 自定义 |
| #6 | `java.lang.RuntimeException` | Bugly 官方测试 |
| #1004 | `SIGABRT`（Native 崩溃） | Bugly 官方测试 |

> 上报成功证据（logcat）见 `截图/截图素材_Bugly上报日志.txt`：`response code 200` + `Success: crash`

### 4.3 用户行为与页面访问顺序

App 端 `BehaviorTracker` 将用户每次页面访问 / 点击上报后端，后端按 `sessionId` 聚合为有序路径。一次完整会话的访问序列（共 19 步）：

```
启动页 → 登录页 → 目的地列表 → 目的地详情(北京) → 攻略详情(北京三日游)
→ 点赞 → 返回列表 → 搜索页 → 搜索结果(上海) → 目的地详情(上海)
→ 分享 → 返回列表 → 退出
```

> 完整 JSON 与访问路径表见 `截图/截图素材_用户行为与页面访问顺序.txt`

---

## 五、测试过程中发现并修复的缺陷

本次测试不仅是"用工具"，还顺带定位并修复了 2 个真实缺陷（测试 → 定位 → 修复的完整闭环）：

| # | 位置 | 缺陷 | 修复 |
| --- | --- | --- | --- |
| 1 | `GlobalExceptionHandler.java` | `ResourceNotFoundException` / `BusinessException` / `IllegalArgumentException` 三个 `@ExceptionHandler` 分支只返回错误响应、**未调用 `printStackTrace()`**，导致这些异常在控制台看不到堆栈 | 给三个分支补上 `ex.printStackTrace()` |
| 2 | `UserBehaviorController.recordBehavior()` | `sessionId = request.getSession().getId()` 无视 App 上报的 sessionId，**每个请求都被当作独立会话**，无法聚合页面访问顺序 | 改为优先使用客户端上报的 sessionId |

---

## 六、运行方式

### 6.1 后端

```bash
# 需要 Java 17
cd demo
./gradlew bootRun
# 启动后访问 http://localhost:8080
#   /api/test/*     —— 触发后台异常
#   /api/behavior/* —— 用户行为查询
#   /h2-console     —— H2 数据库控制台
```

### 6.2 Android 前台

```bash
cd demo/android-app
./gradlew assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

或用 Android Studio 打开 `android-app/` 目录，选择模拟器后点 Run。

### 6.3 触发崩溃测试

App 登录后 → 右上角菜单 → **崩溃测试** → 选择要触发的崩溃类型，Bugly 即可实时采集并在控制台展示。

---

## 七、技术栈

| 层 | 技术 |
| --- | --- |
| 后台 | Spring Boot 3.4.4 · Java 17 · Spring Data JPA · H2 Database · Embedded Tomcat |
| 前台 | Android (Java) · AppCompat · Material Design · Retrofit 2 · OkHttp |
| 测试工具 | 腾讯 Bugly（崩溃采集）· 后端自研行为追踪接口 |
| 构建 | Gradle 8.x |
