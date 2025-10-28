package com.wechat.checkin.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * JSON工具类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Slf4j
public final class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 注册Java 8时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        
        // 禁用将日期写为时间戳
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 忽略未知属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 忽略空值属性
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * 获取ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 对象转格式化的JSON字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("对象转格式化JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON字符串转对象（支持泛型）
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.error("JSON转对象失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON字符串转List
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, 
                OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (JsonProcessingException e) {
            log.error("JSON转List失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON字符串转Map
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON转Map失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON字符串转指定类型的Map
     */
    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, 
                OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (JsonProcessingException e) {
            log.error("JSON转Map失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("对象转Map失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Map转对象
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(map, clazz);
        } catch (Exception e) {
            log.error("Map转对象失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 深拷贝对象
     */
    public static <T> T deepCopy(T obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        try {
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("深拷贝对象失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 判断字符串是否为有效的JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}