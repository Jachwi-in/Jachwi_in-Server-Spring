package com.capstone.Jachwi_inServerSpring.config;

import com.capstone.Jachwi_inServerSpring.domain.dto.LocationDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaConfig {

    public static final String LOCATION_TOPIC = "user-location";

    // 토픽 자동 생성 (파티션 1개, 복제 1개 - 로컬 개발용)
    @Bean
    public NewTopic locationTopic() {
        return TopicBuilder.name(LOCATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new JsonMessageConverter();
    }
}
