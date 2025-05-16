package pro.shushi.pamirs.meta.api.core.orm;

import java.util.List;
import java.util.Map;

/**
 * 名称映射API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
public interface NamedApi {

    Map<String, Object> nameToLname(String model, Map<String, Object> origin);

    @SuppressWarnings("unused")
    List<Map<String, Object>> nameToLname(String model, List<Map<String, Object>> origin);

    Map<String, Object> lnameToName(String model, Map<String, Object> origin);

    @SuppressWarnings("unused")
    List<Map<String, Object>> lnameToName(String model, List<Map<String, Object>> origin);

    Map<String, Object> lnameToColumn(String model, Map<String, Object> origin);

    @SuppressWarnings("unused")
    List<Map<String, Object>> lnameToColumn(String model, List<Map<String, Object>> origin);

    Map<String, Object> columnToLname(String model, Map<String, Object> origin);

    List<Map<String, Object>> columnToLname(String model, List<Map<String, Object>> origin);

}
