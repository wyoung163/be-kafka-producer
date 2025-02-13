package com.example.oliveyoungbe.config.kafka;

import com.example.oliveyoungbe.dto.TicketRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaCosumerConfig {
    @Value("${spring.kafka.bootstrap-server}")
    private String kafkaBootstrapServer;

    @Value("${spring.kafka.consumer.group-id")
    private String groupId;

    @Bean
    public ConsumerFactory<String, TicketRequest> pushEntityConsumerFactory() {
        JsonDeserializer<TicketRequest> deserializer = gcmPushEntityJsonDeserializer();
        return new DefaultKafkaConsumerFactory<>(
                cosumerFactoryConfig(deserializer),
                new StringDeserializer(),
                deserializer);
    }

    private Map<String, Object> cosumerFactoryConfig(JsonDeserializer<TicketRequest> deserializer) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        return configProps;
    }

    private JsonDeserializer<TicketRequest> gcmPushEntityJsonDeserializer() {
        JsonDeserializer<TicketRequest> deserializer = new JsonDeserializer<>(TicketRequest.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TicketRequest>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TicketRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pushEntityConsumerFactory());
        return factory;
    }
}
