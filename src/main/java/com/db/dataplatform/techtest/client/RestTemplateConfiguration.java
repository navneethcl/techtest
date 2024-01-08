package com.db.dataplatform.techtest.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate;
    }

}
