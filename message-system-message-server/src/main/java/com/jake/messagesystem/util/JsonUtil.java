package com.jake.messagesystem.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> Optional<T> fromJson(String json, Class<T> clazz) {
        try {
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (Exception e) {
            logger.error("Failed JSON to Object: {}", e.getMessage());

            return Optional.empty();
        }
    }

    public <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return objectMapper.readerForListOf(clazz).readValue(json);
        } catch (Exception e) {
            logger.error("Failed JSON to List: {}", e.getMessage());

            return Collections.emptyList();
        }
    }

    public Optional<String> toJson(Object object) {
        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            logger.error("Failed Object to Json: {}", e.getMessage());

            return Optional.empty();
        }
    }

    public Optional<String> addValue(String json, String key, String value) {
        try {
            ObjectNode node = (ObjectNode) objectMapper.readTree(json);
            node.put(key, value);

            return Optional.of(objectMapper.writeValueAsString(node));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
