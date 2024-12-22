package com.qin.catcat.unite.config;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

/**
 * @Description ES日期转换配置.
 * 提供了 Date 和 Timestamp 之间的双向转换
 * 使用 Spring 的转换器机制
 * 注册为 ES 的自定义转换器
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-22 19:00
 */
@Configuration
public class DateConverterConfig {

    @Autowired
    private GenericConversionService conversionService;

    @Bean
    public GenericConversionService dateConverters() {
        conversionService.addConverter(new DateToTimestampConverter());
        conversionService.addConverter(new TimestampToDateConverter());
        return conversionService;
    }

    @Bean(name = "dateElasticsearchCustomConversions")
    @Primary
    public ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(
            Arrays.asList(new DateToTimestampConverter(), new TimestampToDateConverter())
        );
    }

    @ReadingConverter
    public static class DateToTimestampConverter implements Converter<Date, Timestamp> {
        @Override
        public Timestamp convert(Date source) {
            return source == null ? null : new Timestamp(source.getTime());
        }
    }

    @WritingConverter
    public static class TimestampToDateConverter implements Converter<Timestamp, Date> {
        @Override
        public Date convert(Timestamp source) {
            return source == null ? null : new Date(source.getTime());
        }
    }
} 