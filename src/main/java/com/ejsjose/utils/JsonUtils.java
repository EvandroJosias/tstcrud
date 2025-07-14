package com.ejsjose.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utilitário para conversão JSON usando Jackson
 * Centraliza toda a configuração de serialização/deserialização
 */
public class JsonUtils {
    
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        
        // Configurações gerais
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        
        // Incluir apenas propriedades não nulas
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // Suporte a Java Time API
        objectMapper.registerModule(new JavaTimeModule());
        
        // Formatação legível
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Converte objeto para JSON string
     * @param object objeto a ser convertido
     * @return JSON string
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }
    
    /**
     * Converte objeto para JSON string formatado
     * @param object objeto a ser convertido
     * @return JSON string formatado
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static String toJsonPretty(Object object) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
    
    /**
     * Converte JSON string para objeto
     * @param json JSON string
     * @param clazz classe do objeto
     * @param <T> tipo do objeto
     * @return objeto deserializado
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
    
    /**
     * Converte JSON string para lista de objetos
     * @param json JSON string
     * @param clazz classe dos objetos da lista
     * @param <T> tipo dos objetos
     * @return lista de objetos deserializados
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
    
    /**
     * Converte InputStream JSON para objeto
     * @param inputStream InputStream com JSON
     * @param clazz classe do objeto
     * @param <T> tipo do objeto
     * @return objeto deserializado
     * @throws IOException se ocorrer erro na leitura
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }
    
    /**
     * Clona objeto via JSON (deep copy)
     * @param object objeto a ser clonado
     * @param clazz classe do objeto
     * @param <T> tipo do objeto
     * @return cópia do objeto
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static <T> T clone(Object object, Class<T> clazz) throws JsonProcessingException {
        String json = toJson(object);
        return fromJson(json, clazz);
    }
    
    /**
     * Verifica se uma string é um JSON válido
     * @param json string a ser verificada
     * @return true se for JSON válido
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    /**
     * Converte objeto para JSON com formatação compacta
     * @param object objeto a ser convertido
     * @return JSON string compacto
     * @throws JsonProcessingException se ocorrer erro na conversão
     */
    public static String toJsonCompact(Object object) throws JsonProcessingException {
        ObjectMapper compactMapper = objectMapper.copy();
        compactMapper.disable(SerializationFeature.INDENT_OUTPUT);
        return compactMapper.writeValueAsString(object);
    }
    
    /**
     * Obtém o ObjectMapper configurado para uso direto
     * @return ObjectMapper configurado
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
