package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesAsyncClient;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.condition.ElasticsearchCondition;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.configration.ElasticsearchProperty;

import static pro.shushi.pamirs.meta.constant.FieldConstants._dFieldName;

/**
 * ElasticsearchClientsFactory
 *
 * @author yakir on 2020/04/14 20:02.
 */
@Configuration
@Conditional(ElasticsearchCondition.class)
public class ElasticsearchClientsFactory {

    private static final Logger log = LoggerFactory.getLogger(RestClientBuilderFactory.class);

    @Bean(destroyMethod = "close")
    public RestClient restClient(@Nullable @Autowired ElasticsearchProperty elasticsearchProperty) {
        log.info("Elastic Properties: [{}]", elasticsearchProperty);
        if (null == elasticsearchProperty) {
            throw new RuntimeException("没有配置Elasticsearch连接参数。");
        }
        return RestClientBuilderFactory.builder(elasticsearchProperty).build();
    }

    @Bean(destroyMethod = "close")
    public ElasticsearchTransport elasticsearchTransport(@Autowired RestClient restClient, @Autowired ElasticsearchProperty elasticsearchProperty) {
        RestClientTransport clientTransport = null;
        JacksonJsonpMapper jacksonJsonpMapper = new JacksonJsonpMapper();
        jacksonJsonpMapper.objectMapper()
                .setFilterProvider(new SimpleFilterProvider()
                        .addFilter(_dFieldName, SimpleBeanPropertyFilter.serializeAllExcept(_dFieldName)));
        if (StringUtils.startsWith(elasticsearchProperty.getVersion(), "7")) {
            RequestOptions requestOptions = RequestOptions.DEFAULT.toBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .setHttpAsyncResponseConsumerFactory(
                            new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(1024 * 1024 * 1024)
                    )
                    .build();
            TransportOptions clientOptions = new RestClientOptions(requestOptions);
            clientTransport = new RestClientTransport(restClient, jacksonJsonpMapper, clientOptions);
        } else {
            clientTransport = new RestClientTransport(restClient, jacksonJsonpMapper);
        }
        return clientTransport;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(@Autowired ElasticsearchTransport elasticsearchTransport) {
        return new ElasticsearchClient(elasticsearchTransport);
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient(@Autowired ElasticsearchTransport elasticsearchTransport) {
        return new ElasticsearchAsyncClient(elasticsearchTransport);
    }

    @Bean
    public ElasticsearchIndicesClient elasticsearchIndicesClient(@Autowired ElasticsearchClient elasticsearchClient) {
        return elasticsearchClient.indices();
    }

    @Bean
    public ElasticsearchIndicesAsyncClient elasticsearchIndicesAsyncClient(@Autowired ElasticsearchAsyncClient elasticsearchAsyncClient) {
        return elasticsearchAsyncClient.indices();
    }
}
