package pro.shushi.pamirs.framework.connectors.data.elastic.common;

import pro.shushi.pamirs.framework.connectors.data.elastic.common.domain.ElasticIndex;

import java.util.List;
import java.util.Map;

/**
 * ElasticIndicesApi
 *
 * @author yakir on 2020/04/14 21:20.
 */
public interface ElasticIndicesApi {

    /// --------  index

    String init(ElasticIndex elasticIndex);

    String create(String index);

    String create(ElasticIndex elasticIndex);

    String get(String index);

    Boolean isExist(String index);

    boolean close(String index);

    /// --------  index alias

    Boolean existAlias(String alias);

    Boolean existAlias(String index, String alias);

    String moveAlias(String indexR, String indexA, String alias);

    String createAlias(String index, String alias);

    String deleteAlias(String alias);

    /// --------  index mapping

    String getMapping(String index);

    String createMapping(String index, String modelModel, List<Map<String, String>> analyzers);

}
