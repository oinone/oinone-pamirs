package pro.shushi.pamirs.framework.connectors.data.elastic.utils;

import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.constant.Constants;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.domain.ElasticIndex;

import java.util.Optional;

/**
 * IndicesUtil
 *
 * @author yakir on 2022/08/30 16:45.
 */
public class IndicesUtil {

    public static IndexSettings defaultIndexSettings() {
        return new IndexSettings.Builder()
                .numberOfShards(Constants.DEFAULT_NUMBER_OF_SHARDS)
                .numberOfReplicas(Constants.DEFAULT_NUMBER_OF_REPLICAS)
                .build();

    }

    public static IndexSettings indexSettings(ElasticIndex elasticIndex) {
        String shards   = Optional.ofNullable(elasticIndex.getShards()).orElse(Constants.DEFAULT_NUMBER_OF_SHARDS);
        String replicas = Optional.ofNullable(elasticIndex.getReplicas()).orElse(Constants.DEFAULT_NUMBER_OF_REPLICAS);
        return new IndexSettings.Builder()
                .numberOfShards(shards)
                .numberOfReplicas(replicas)
                .build();

    }

    public static CreateIndexRequest cir(String index) {
        return new CreateIndexRequest.Builder()
                .index(index)
                .settings(defaultIndexSettings())
                .build();
    }

    public static CreateIndexRequest cir(ElasticIndex elasticIndex) {
        return new CreateIndexRequest.Builder()
                .index(elasticIndex.relIndex())
                .settings(defaultIndexSettings())
                .aliases(elasticIndex.getAlias(),
                        new Alias.Builder()
                                .searchRouting(elasticIndex.getAlias())
                                .isWriteIndex(true)
                                .build())
                .build();
    }
}
