package com.yuchen.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: xiaozhennan
 * @Date: 2022/12/2 9:54
 * @Package: com.yuchen.common.utils
 * @ClassName: JsonExtractTool
 * @Description:
 **/
public class JsonExtractTool {
    private ObjectMapper objectMapper;
    private static JsonExtractTool jsonExtractTool;

    private JsonExtractTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Object extract(String json, String jsonPath) {
        DocumentContext parse = JsonPath.parse(json);
        return parse.read(jsonPath);
    }

    private void init() {
        this.objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.objectMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
        this.objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
            private final MappingProvider mappingProvider = new JacksonMappingProvider(objectMapper);

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                EnumSet<Option> options = EnumSet.noneOf(Option.class);
                //抑制json异常
                options.add(Option.SUPPRESS_EXCEPTIONS);
                options.add(Option.DEFAULT_PATH_LEAF_TO_NULL);
                return options;
            }
        });
    }


    public static JsonExtractTool getInstance(ObjectMapper objectMapper) {
        if (jsonExtractTool == null) {
            synchronized (JsonExtractTool.class) {
                if (jsonExtractTool == null) {
                    jsonExtractTool = new JsonExtractTool(objectMapper);
                    jsonExtractTool.init();
                }
            }
        }
        return jsonExtractTool;
    }

    public static JsonExtractTool getInstance() {
        if (jsonExtractTool == null) {
            synchronized (JsonExtractTool.class) {
                if (jsonExtractTool == null) {
                    getInstance(new ObjectMapper());
                }
            }
        }
        return jsonExtractTool;
    }
}
