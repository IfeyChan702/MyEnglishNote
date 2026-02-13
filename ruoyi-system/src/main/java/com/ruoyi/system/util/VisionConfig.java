package com.ruoyi.system.util;

// ============================================================
// 如果你用的是 Spring Boot 项目，以下是集成方式
// ============================================================

// ===================== application.yml =====================
/*
vision:
  # 选择你要用的提供商，取消对应注释即可

  # 方案1: 通义千问 VL（国内推荐，注册即送免费额度）
  # 注册地址: https://dashscope.console.aliyun.com/
  api-key: sk-your-api-key
  base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
  model: qwen-vl-max

  # 方案2: 硅基流动 SiliconFlow（可用 DeepSeek-VL2）
  # 注册地址: https://cloud.siliconflow.cn/
  # api-key: sk-your-api-key
  # base-url: https://api.siliconflow.cn/v1
  # model: deepseek-ai/deepseek-vl2

  # 方案3: 智谱AI GLM-4V
  # 注册地址: https://open.bigmodel.cn/
  # api-key: your-api-key
  # base-url: https://open.bigmodel.cn/api/paas/v4
  # model: glm-4v-flash

  # 方案4: OpenAI GPT-4o（需要海外访问）
  # api-key: sk-your-api-key
  # base-url: https://api.openai.com/v1
  # model: gpt-4o
*/

// =================== VisionConfig.java ===================
/*
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "vision")
public class VisionConfig {

    private String apiKey;
    private String baseUrl;
    private String model;

    @Bean
    public VisionApiService visionApiService() {
        return new VisionApiService(baseUrl, model, apiKey);
    }

    // getter & setter
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
*/

// =============== 在你现有代码中替换使用 ===============
/*
@Service
public class YourExistingService {

    @Autowired
    private VisionApiService visionApiService;

    public List<String> recognizeObjects(String imageBase64, String imageType) {
        // 直接替换你原来的 analyzeImage 方法调用
        return visionApiService.analyzeImage(imageBase64, imageType);
    }

    public List<String> recognizeObjectsFromUrl(String imageUrl) {
        return visionApiService.analyzeImage(imageUrl, "url");
    }

    public List<String> recognizeObjectsFromFile(String filePath) throws IOException {
        return visionApiService.analyzeImageFromFile(filePath);
    }
}
*/