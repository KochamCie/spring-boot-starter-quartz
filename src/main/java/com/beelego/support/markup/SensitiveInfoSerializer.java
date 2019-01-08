package com.beelego.support.markup;

import com.beelego.enums.SensitiveType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * @author : hama
 * @since : created in  2018/7/2
 */
@Slf4j
@NoArgsConstructor
public class SensitiveInfoSerializer extends JsonSerializer<Date> implements ContextualSerializer {

    private SensitiveType type;

    public SensitiveInfoSerializer(final SensitiveType type) {
        this.type = type;
    }

    @Override
    public void serialize(Date s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        switch (this.type) {
            case TODATESTR: {
                jsonGenerator.writeString(SensitiveInfoUtil.dateMarkup(s));
                break;
            }
        }

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // target is String.class decorated by SensitiveInfo
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), Date.class)) {
                SensitiveInfo sensitiveInfo = beanProperty.getAnnotation(SensitiveInfo.class);
                if (sensitiveInfo == null) {
                    sensitiveInfo = beanProperty.getContextAnnotation(SensitiveInfo.class);
                }
                if (sensitiveInfo != null) {
                    return new SensitiveInfoSerializer(sensitiveInfo.type());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}

