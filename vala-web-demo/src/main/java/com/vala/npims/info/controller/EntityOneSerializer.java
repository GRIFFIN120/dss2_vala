package com.vala.npims.info.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vala.base.entity.BaseEntity;
import com.vala.npims.info.bean.ArticleBean;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Map;

@JsonComponent
public class EntityOneSerializer {

    public static class ListSerializer
            extends JsonSerializer<BaseEntity> {
        @Override
        public void serialize(BaseEntity bean, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(bean.getName());
        }
    }

    public static class ListDeserializer
            extends JsonDeserializer<ArticleBean> {

        @Override
        public ArticleBean deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {


            Map<String,Object> map = jsonParser.getCodec().readValue(jsonParser, Map.class);
            Integer id = (Integer) map.get("id");
            ArticleBean entity = new ArticleBean();
            entity.setId(id);

            return entity;
        }
    }


}
