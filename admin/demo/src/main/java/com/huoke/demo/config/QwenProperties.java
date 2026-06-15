package com.huoke.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "qwen")
public class QwenProperties {

    private String apiKey;

    private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    private String model = "qwen-plus";
}
