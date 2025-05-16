package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.ElasticsearchCatClient;
import co.elastic.clients.elasticsearch.cat.HealthResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.configration.ElasticsearchProperty;

import java.io.IOException;

/**
 * TestElasticRestClientBuild
 *
 * @author yakir on 2020/04/14 00:13.
 */
public class TestElasticRestClientBuild {

    private static final Logger log = LoggerFactory.getLogger(TestElasticRestClientBuild.class);

    private ElasticsearchProperty elasticsearchProperty;

    @BeforeEach
    public void beforeEach() {
        elasticsearchProperty = new ElasticsearchProperty();
        elasticsearchProperty.setUrl("192.168.1.149:9200");
        elasticsearchProperty.setUser("admin");
        elasticsearchProperty.setPassword("admin");
        elasticsearchProperty.setTrustSelfSigned(true);
        elasticsearchProperty.setUseSSL(true);
    }

    @AfterEach
    public void afterEach(){

    }


    @Test
    public void testEsJdbcBuilder() {
        try (RestClient restClient = RestClientBuilderFactory.builder(elasticsearchProperty).build();
             RestClientTransport restClientTransport = new RestClientTransport(restClient, new JacksonJsonpMapper())) {
            ElasticsearchClient    client   = new ElasticsearchClient(restClientTransport);
            ElasticsearchCatClient cat      = client.cat();
            HealthResponse         response = cat.health();
            log.info("{}", response);
        } catch (IOException e) {
            log.error("error", e);
        }
    }

}