package com.example.oliveyoungbe.config.kafka;

import com.example.oliveyoungbe.dto.TicketBookingDto;
import com.example.oliveyoungbe.dto.TicketRequestDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-server}")
    private String kafkaBootstrapServer;

    @Bean
    public ProducerFactory<String, TicketRequestDto> ticketRequestProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        return new DefaultKafkaProducerFactory<>(config);
    }

    // TicketBooking ProducerFactory 설정
    @Bean
    public ProducerFactory<String, TicketBookingDto> ticketBookingProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }


    // TicketRequest KafkaTemplate 설정
    @Bean
    public KafkaTemplate<String, TicketRequestDto> ticketRequestKafkaTemplate() {
        return new KafkaTemplate<>(ticketRequestProducerFactory());
    }

    // TicketBooking KafkaTemplate 설정
    @Bean
    public KafkaTemplate<String, TicketBookingDto> ticketBookingKafkaTemplate() {
        return new KafkaTemplate<>(ticketBookingProducerFactory());
    }
}
