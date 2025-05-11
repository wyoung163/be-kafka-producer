package com.example.oliveyoungbe.config.kafka;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-server}")
    private String kafkaBootstrapServer;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer); // Kafka 서버 주소
        config.put(AdminClientConfig.CLIENT_ID_CONFIG, "spring-kafka-admin-client");
        return AdminClient.create(config);
    }
}

