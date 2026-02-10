# MyEnglishNote
实现流程（RAG Pipeline）
用户在记事本里写下学习内容（比如单词 apple），比如老婆生日12月29日。

系统把这条笔记转成向量，存入向量数据库mysql。

用户提问（比如“帮我复习水果相关的单词”）。  老婆生日还有几天

系统在向量库里检索相关笔记（找到 apple, banana, orange）。

把检索结果拼接到 prompt，交给deepseek 开发者 api。

deepseek 开发者 api 生成带上下文的回答（例句、测验题、解释）。

前端展示结果，并允许用户继续记录。

🛠️ 技术选型建议
前端：Flutter（跨平台，适合移动端记事本应用）

后端：Spring Boot （提供 API）

数据库：mysql（既能存笔记，又能做向量检索）

🚀 扩展功能
间隔重复（SRS）算法：像 Anki 一样，根据遗忘曲线推送复习。

语音输入/朗读：结合 TTS/ASR，做听说练习。

多模态笔记：支持图片（比如截图单词卡），也能做向量检索。

👉 总结：你需要一个 数据库 + 向量检索 + LLM 的三层架构。记事本就是数据源，RAG 用来把笔记和模型结合，最终实现“边记边学，边复习”的效果。 我这里git git 是spring 后台，帮我完成这个项目可以吗
向量模型选择：
 Deepseek 的 embedding API？
MySQL 8.0+ 可以用 JSON 存向量，用自定义函数计算余弦相似度 用这个。
JWT token？ 我的spring 是ruoyi的后台，本来就自带这个
部署方式：先本地，以后打包到服务器ubuntu才用docker compose
一次完成 创建完整的 Spring Boot 项目模板 和目录结构 在我的项目上面新建一个分支来做
编写 SQL 初始化脚本
实现第一个 API 端点（笔记创建 + 向量存储）。我已经购买的deepseek的 api token 
