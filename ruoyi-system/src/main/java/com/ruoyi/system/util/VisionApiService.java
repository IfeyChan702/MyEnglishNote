package com.ruoyi.system.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * 通用 Vision API 图片识别服务
 *
 * 支持多种 API 提供商（兼容 OpenAI 格式）：
 * 1. OpenAI (GPT-4o) - 推荐，效果最好
 * 2. 通义千问 (Qwen-VL) - 国内推荐，性价比高
 * 3. 智谱AI (GLM-4V) - 国内可用
 * 4. 硅基流动 (SiliconFlow) - 托管 DeepSeek-VL2 等开源模型
 * 5. 其他兼容 OpenAI 格式的 API
 *
 * 注意：DeepSeek 官方 API (api.deepseek.com) 目前不支持图片输入！
 *       deepseek-chat 和 deepseek-reasoner 都是纯文本模型。
 *       如果一定要用 DeepSeek-VL2，需要通过第三方平台（如硅基流动）调用。
 */
public class VisionApiService {

    private static final Logger log = LoggerFactory.getLogger(VisionApiService.class);

    // ==================== 配置项 ====================

    private String apiKey;
    private String baseUrl;
    private String model;
    private int maxRetries = 3;
    private int connectTimeout = 30000;  // 30秒
    private int readTimeout = 60000;     // 60秒

    // ==================== 预设的 API 提供商配置 ====================

    /**
     * 提供商枚举 - 方便快速切换
     */
    public enum Provider {
        /**
         * OpenAI GPT-4o - 效果最好，需要海外访问
         */
        OPENAI("https://api.openai.com/v1", "gpt-4o"),

        /**
         * 通义千问 VL - 国内推荐，阿里云
         * 文档: https://help.aliyun.com/zh/model-studio/
         */
        QWEN("https://dashscope.aliyuncs.com/compatible-mode/v1", "qwen-vl-max"),

        /**
         * 智谱AI GLM-4V - 国内可用
         * 文档: https://open.bigmodel.cn/
         */
        ZHIPU("https://open.bigmodel.cn/api/paas/v4", "glm-4v-flash"),

        /**
         * 硅基流动 SiliconFlow - 托管多种开源 Vision 模型
         * 包括 DeepSeek-VL2、Qwen2-VL 等
         * 文档: https://docs.siliconflow.cn/
         */
        SILICONFLOW("https://api.siliconflow.cn/v1", "deepseek-ai/deepseek-vl2"),

        /**
         * 火山引擎（字节跳动）- 托管 DeepSeek 等模型
         */
        VOLCENGINE("https://ark.cn-beijing.volces.com/api/v3", "deepseek-vl2");

        private final String baseUrl;
        private final String defaultModel;

        Provider(String baseUrl, String defaultModel) {
            this.baseUrl = baseUrl;
            this.defaultModel = defaultModel;
        }

        public String getBaseUrl() { return baseUrl; }
        public String getDefaultModel() { return defaultModel; }
    }

    // ==================== 构造方法 ====================

    /**
     * 使用预设提供商快速构造
     *
     * @param provider API提供商
     * @param apiKey   API密钥
     */
    public VisionApiService(Provider provider, String apiKey) {
        this.apiKey = apiKey;
        this.baseUrl = provider.getBaseUrl();
        this.model = provider.getDefaultModel();
    }

    /**
     * 自定义配置构造
     *
     * @param baseUrl API基础URL（不含 /chat/completions）
     * @param model   模型名称
     * @param apiKey  API密钥
     */
    public VisionApiService(String baseUrl, String model, String apiKey) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    // ==================== 核心方法：图片识别 ====================

    /**
     * 识别图片中的物体（返回物品名称列表）
     *
     * @param imageSource 图片来源：Base64字符串 或 URL
     * @param imageType   "base64" 或 "url"
     * @return 识别出的物品名称列表
     */
    public List<String> analyzeImage(String imageSource, String imageType) {
        String prompt = "请识别这张图片中所有可见的物体和物品。" +
                "以简单的中文名词列出它们，用英文逗号分隔。" +
                "只需要列出物品名称，不需要描述或解释。" +
                "例如：苹果,手机,书本,杯子";

        String responseContent = callVisionApi(imageSource, imageType, prompt);
        return parseObjectList(responseContent);
    }

    /**
     * 识别图片中的物体（返回英文名称列表）
     *
     * @param imageSource 图片来源：Base64字符串 或 URL
     * @param imageType   "base64" 或 "url"
     * @return 识别出的物品英文名称列表
     */
    public List<String> analyzeImageEnglish(String imageSource, String imageType) {
        String prompt = "Please identify all objects, items, and things visible in this image. " +
                "List them as simple English nouns, separated by commas. " +
                "Only include the object names, no descriptions or explanations. " +
                "Example: apple, phone, book, cup";

        String responseContent = callVisionApi(imageSource, imageType, prompt);
        return parseObjectList(responseContent);
    }

    /**
     * 对图片进行自定义问答
     *
     * @param imageSource 图片来源
     * @param imageType   "base64" 或 "url"
     * @param question    自定义问题
     * @return 模型回答内容
     */
    public String askAboutImage(String imageSource, String imageType, String question) {
        return callVisionApi(imageSource, imageType, question);
    }

    /**
     * 从文件路径分析图片
     *
     * @param filePath 本地文件路径
     * @return 识别出的物品名称列表
     */
    public List<String> analyzeImageFromFile(String filePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(new File(filePath).toPath());
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return analyzeImage(base64, "base64");
    }

    // ==================== 内部实现 ====================

    /**
     * 调用 Vision API（兼容 OpenAI 格式）
     */
    private String callVisionApi(String imageSource, String imageType, String prompt) {
        // 1. 构建 content 数组
        List<Map<String, Object>> content = new ArrayList<>();

        // 文本部分
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        content.add(textPart);

        // 图片部分
        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        if ("url".equalsIgnoreCase(imageType)) {
            imageUrl.put("url", imageSource);
        } else {
            // Base64 格式
            String dataUri = imageSource;
            if (!imageSource.startsWith("data:image")) {
                String mimeType = detectMimeType(imageSource);
                dataUri = "data:image/" + mimeType + ";base64," + imageSource;
            }
            imageUrl.put("url", dataUri);
        }
        imagePart.put("image_url", imageUrl);
        content.add(imagePart);

        // 2. 构建 messages
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        messages.add(userMessage);

        // 3. 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 500);

        // 4. 发送请求（带重试）
        String url = baseUrl + "/chat/completions";
        String responseBody = executeWithRetry(url, requestBody);

        if (responseBody == null) {
            throw new RuntimeException("Failed to get response from Vision API: " + url);
        }

        // 5. 解析响应
        JSONObject response = JSON.parseObject(responseBody);
        JSONArray choices = response.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Empty choices in API response. Response: " + responseBody);
        }

        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");
        String responseContent = message.getString("content");

        log.info("Vision API response: {}", responseContent);
        return responseContent;
    }

    /**
     * 检测 Base64 编码图片的 MIME 类型
     */
    private String detectMimeType(String base64Data) {
        if (base64Data.startsWith("/9j/")) return "jpeg";
        if (base64Data.startsWith("iVBORw0KGgo")) return "png";
        if (base64Data.startsWith("R0lGOD")) return "gif";
        if (base64Data.startsWith("UklGR")) return "webp";
        return "jpeg"; // 默认
    }

    /**
     * 解析逗号分隔的物品列表
     */
    private List<String> parseObjectList(String responseContent) {
        List<String> objects = new ArrayList<>();
        if (responseContent == null || responseContent.trim().isEmpty()) {
            return objects;
        }

        // 清理可能的 Markdown 格式（有些模型会返回带序号或符号的列表）
        String cleaned = responseContent
                .replaceAll("\\d+\\.\\s*", "")       // 去掉 "1. " 格式
                .replaceAll("[\\-\\*]\\s*", "")       // 去掉 "- " 或 "* " 格式
                .replaceAll("\\n", ",")               // 换行替换为逗号
                .replaceAll("、", ",")                // 中文顿号替换
                .replaceAll("，", ",");               // 中文逗号替换

        String[] items = cleaned.split(",");
        for (String item : items) {
            String trimmed = item.trim()
                    .replaceAll("^[\"']|[\"']$", ""); // 去掉引号
            if (!trimmed.isEmpty() && trimmed.length() < 50) { // 过滤过长的项
                objects.add(trimmed);
            }
        }

        log.info("Identified {} objects from image", objects.size());
        return objects;
    }

    /**
     * 带重试的 HTTP POST 请求
     */
    private String executeWithRetry(String urlStr, Map<String, Object> requestBody) {
        String jsonBody = JSON.toJSONString(requestBody);

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Vision API call attempt {}/{} to {}", attempt, maxRetries, urlStr);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
                conn.setDoOutput(true);

                // 写入请求体
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                int responseCode = conn.getResponseCode();

                // 读取响应
                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                String response;
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    response = sb.toString();
                }

                if (responseCode >= 200 && responseCode < 300) {
                    return response;
                }

                log.warn("Vision API returned status {}: {}", responseCode, response);

                // 429 或 5xx 才重试
                if (responseCode != 429 && responseCode < 500) {
                    throw new RuntimeException("Vision API error (" + responseCode + "): " + response);
                }

            } catch (RuntimeException e) {
                throw e; // 非重试异常直接抛出
            } catch (Exception e) {
                log.error("Vision API call failed on attempt {}: {}", attempt, e.getMessage());
            }

            // 指数退避
            if (attempt < maxRetries) {
                try {
                    long sleepMs = (long) (1000 * Math.pow(2, attempt - 1));
                    Thread.sleep(sleepMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return null;
    }

    // ==================== Setter 方法 ====================

    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public void setConnectTimeout(int ms) { this.connectTimeout = ms; }
    public void setReadTimeout(int ms) { this.readTimeout = ms; }
    public void setModel(String model) { this.model = model; }

    // ==================== 使用示例 ====================

    public static void main(String[] args) throws Exception {

        // ============ 方案1: 使用通义千问 VL（国内推荐） ============
        VisionApiService qwenService = new VisionApiService(
                Provider.QWEN,
                "sk-your-dashscope-api-key"
        );

        // 通过URL识别图片
        List<String> objects1 = qwenService.analyzeImage(
                "https://example.com/photo.jpg", "url");
        System.out.println("通义千问识别结果: " + objects1);

        // ============ 方案2: 使用硅基流动的 DeepSeek-VL2 ============
        VisionApiService dsService = new VisionApiService(
                Provider.SILICONFLOW,
                "sk-your-siliconflow-api-key"
        );
        // 可以切换为其他模型
        // dsService.setModel("Qwen/Qwen2-VL-72B-Instruct");

        // 通过本地文件识别
        List<String> objects2 = dsService.analyzeImageFromFile("/path/to/image.jpg");
        System.out.println("DeepSeek-VL2识别结果: " + objects2);

        // ============ 方案3: 使用 OpenAI GPT-4o ============
        VisionApiService openaiService = new VisionApiService(
                Provider.OPENAI,
                "sk-your-openai-api-key"
        );

        List<String> objects3 = openaiService.analyzeImage(
                "https://example.com/photo.jpg", "url");
        System.out.println("GPT-4o识别结果: " + objects3);

        // ============ 方案4: 完全自定义 API 地址 ============
        VisionApiService customService = new VisionApiService(
                "https://your-custom-api.com/v1",  // 基础URL
                "your-vision-model-name",           // 模型名
                "your-api-key"                      // 密钥
        );

        // 自定义图片问答
        String answer = customService.askAboutImage(
                "https://example.com/photo.jpg",
                "url",
                "这张图片中有几个人？他们在做什么？"
        );
        System.out.println("自定义问答: " + answer);
    }
}