package pro.shushi.pamirs.framework.connectors.data.elastic.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JacksonMapper
 *
 * @author yakir on 2020/04/20 14:47.
 */
public class JacksonMapper {

    public static <T extends ObjectMapper> Builder<T> builder(T mapper) {
        return new Builder<T>(mapper);
    }

    public static Builder<ObjectMapper> builder() {
        return new Builder<ObjectMapper>(new ObjectMapper());
    }

    public static class Builder<T extends ObjectMapper> {

        private final T mapper;

        public Builder(T mapper) {
            this.mapper = mapper;
        }

        /**
         * 允许JSON中包含注释
         *
         * @return Builder
         */
        public Builder<T> allowComments() {
            this.mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
            return this;
        }

        /**
         * 允许Json中使用单引号
         *
         * @return Builder
         */
        public Builder<T> allowSingleQuote() {
            this.mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
            return this;
        }

        /// format local date / local date time ---------------------------------------------

        public Builder<T> dateFormat(String pattern) {
            this.mapper.setDateFormat(new SimpleDateFormat(pattern));
            return this;
        }

        /**
         * 注册 LocalDateTime
         *
         * @param pattern 时间格式表达式
         * @return Builder
         */
        public Builder<T> addJavaTimeModule(String pattern) {
            JavaTimeModule    jtm = new JavaTimeModule();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
            jtm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dtf));
            jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dtf));
            this.mapper.registerModule(jtm);
            return this;
        }

        /// PropertyNamingStrategy ----------------------------------------------------------

        /**
         * 驼峰转小写下划线
         *
         * @return Builder
         */
        public Builder<T> snakeCase() {
            this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            return this;
        }

        /**
         * 首字母大写驼峰
         *
         * @return Builder
         */
        public Builder<T> upperCamelCase() {
            this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
            return this;
        }

        /**
         * 首字母小写驼峰
         *
         * @return Builder
         */
        public Builder<T> lowerCamelCase() {
            this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
            return this;
        }

        /**
         * 驼峰转全小写(没有驼峰)
         *
         * @return Builder
         */
        public Builder<T> lowerCase() {
            this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE);
            return this;
        }

        /**
         * 驼峰转分隔符, 类似于下划线(下划线变成了分隔符)
         *
         * @return Builder
         */
        public Builder<T> kebabCase() {
            this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
            return this;
        }

        /// DeserializationFeature ----------------------------------------------------------

        public Builder<T> deDisableFailOnUnknowPropertis() {
            this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return this;
        }

        public Builder<T> deDisableFailOnIgnoredProperties() {
            this.mapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
            return this;
        }

        /// SerializationFeature ------------------------------------------------------------

        public Builder<T> seDisableDatesAsTimestamps() {
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return this;
        }

        public Builder<T> seDisableFailOnEmptyBeans() {
            this.mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            return this;
        }

        public T mapper() {
            return this.mapper;
        }

    }

}
