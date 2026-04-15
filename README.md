# 雅思背单词 (IeltsVocab)

一款基于雅思词汇的安卓背单词App，数据来源于《雅思词汇词根+联想记忆法（乱序便携版）》。

## 功能特性

- **每日学习计划**：可设置每日新词数量，按顺序学习新单词
- **艾宾浩斯遗忘曲线复习**：根据遗忘曲线算法自动安排复习时间（1天→2天→4天→7天→15天→30天）
- **单词读音**：使用Android系统TTS引擎离线发音
- **联想记忆法**：支持词根词缀拆解、谐音联想、情景联想
- **词库浏览**：按Word List分组浏览全部3569个雅思单词
- **学习统计**：学习数据统计、连续打卡天数

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose + Material 3
- **架构**: MVVM (ViewModel + Repository)
- **数据库**: Room (SQLite)
- **异步处理**: Kotlin Coroutines + Flow
- **依赖注入**: Hilt
- **导航**: Compose Navigation
- **发音**: Android TextToSpeech API (离线)

## 项目结构

```
app/src/main/java/com/ielts/vocab/
├── IeltsApp.kt              # Application类
├── MainActivity.kt          # 单Activity入口
├── algorithm/               # 艾宾浩斯遗忘曲线算法
├── data/
│   ├── local/               # Room数据库、Entity、DAO
│   ├── repository/          # 数据仓库层
│   └── importer/            # 首次启动数据导入
├── di/                      # Hilt依赖注入模块
├── service/                 # TTS发音服务
└── ui/
    ├── components/          # 通用UI组件
    ├── home/                # 首页
    ├── study/               # 学习页
    ├── review/              # 复习页
    ├── word/                # 单词详情页
    ├── wordlist/            # 词库浏览页
    ├── stats/               # 统计页
    ├── navigation/          # 导航路由
    └── theme/               # Material 3主题
```

## 构建与运行

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 运行应用

## 数据来源

- 单词列表：[IELTS Word List](https://github.com/fanhongtao/IELTS)
- 词汇来源：《雅思词汇词根+联想记忆法（乱序便携版）》（新东方出版）
