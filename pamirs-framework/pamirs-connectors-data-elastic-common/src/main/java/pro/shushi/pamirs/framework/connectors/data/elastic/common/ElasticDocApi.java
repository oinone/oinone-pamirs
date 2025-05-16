package pro.shushi.pamirs.framework.connectors.data.elastic.common;

import java.util.List;
import java.util.Map;

/**
 * ElasticDocApi
 *
 * @author yakir on 2020/04/14 21:20.
 */
public interface ElasticDocApi {

    String ID          = "id";
    String BULK_INDEX  = "BULK_INDEX";
    String BULK_UPDATE = "BULK_UPDATE";
    String BULK_DELETE = "BULK_DELETE";

    List<Map<String, Object>> bulkIndex(final String index, final List<Map<String, Object>> entries);

    List<Map<String, Object>> bulkDelete(final String index, final List<Map<String, Object>> entries);

    List<Map<String, Object>> bulkUpdate(final String index, final List<Map<String, Object>> entries);

    Long count(final String index);

}
