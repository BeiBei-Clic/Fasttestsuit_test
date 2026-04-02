# 作业六：移动软件测试工具体验

## 原始要求（Fasttestsuit）

**网站：** www.i-test.com.cn

登录注册，上传应用（Android 或 iOS APP），下载 SDK，安装 Jar 包，编译运行，登录网站查看运行结果，给出分析报告，记录访问发掘页面顺序，捕获后台 Java 应用异常，展示呈现数据结果。

具体要求：
1. **上传应用**：需要有源代码，后台应用必须使用 Java 实现，前台可以是 Android 或 iOS 版本。
2. **运行测试**：在各自手机终端运行后给出程序异常、崩溃、用户行为分析报告。
3. **报告内容**：附主要操作过程、主要源代码、网站测试异常或不好用的功能点。
4. **加分项**：鼓励提交测试前后台综合应用。

---

## 重要变更说明：工具替换为腾讯 Bugly

### 替换原因（也是作业要求的"网站测试异常"实测发现）

按原要求体验 Fasttestsuit (www.i-test.com.cn) 时，实测发现该平台存在**导致无法使用的严重问题**，经多轮排查确认属实：

| 问题 | 实测证据 |
| --- | --- |
| **SSL/TLS 证书已过期半年** | 证书有效期 2025-09-08 至 2025-12-06，当前 2026-06-19 已过期。`curl` 报 `SSL certificate problem: certificate has expired`，SDK 数据无法通过 HTTPS 上报。 |
| **核心接口服务端崩溃** | SDK 启动调用的 `https://i-test.com.cn/PerformanceMonitorCenter/loadSDKConfig` 接口对**任意 appId**（含随机 UUID）均返回 **HTTP 500**，堆栈为 `NullPointerException at ConfigService.loadSDKConfigByappId(ConfigService.java:52)`。SDK 拿不到配置 → 后续所有崩溃/行为数据上报全部失败。 |

以上两条构成了本次作业"**网站测试异常或不好用的功能点**"的核心素材。

### 替代方案

由于原平台核心功能无法使用，为保证作业"**移动端 APM / 崩溃监控工具体验**"的核心目标达成，改用国内主流的同类工具 **腾讯 Bugly**（功能定位与 Fasttestsuit 完全一致：移动端崩溃/异常/ANR 采集与分析）完成剩余体验。

- 工具：腾讯 Bugly (https://bugly.qq.com)
- 被测应用：本仓库现有的 Android 旅行攻略 App（`android-app/`）+ Java Spring Boot 后端（`src/`）
- 后台 Java 异常：仍由 `TestExceptionController` 的 7 个接口触发并采集

作业要求的全部要素（上传应用有源码、后台 Java 实现、前台 Android、程序异常/崩溃/用户行为分析、操作过程、源代码、网站异常反馈）均在 Bugly 侧完成，并补充原平台 Fasttestsuit 的故障实测记录。
