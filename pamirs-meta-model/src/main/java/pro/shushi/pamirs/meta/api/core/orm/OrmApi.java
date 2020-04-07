package pro.shushi.pamirs.meta.api.core.orm;

import com.alibaba.fastjson.TypeReference;
import pro.shushi.pamirs.meta.base.D;

import java.util.List;
import java.util.Map;

/**
 * 对象关系映射API
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface OrmApi {

    /**
     * 给模型设置对应的model变量
     *
     * @param model
     * @param obj
     * @param <T>
     * @return
     */
    <T> T modeling(String model, T obj);

    /**
     * 模型转Map
     *
     * @param model
     * @param obj
     * @return
     */
    <T> T mapping(String model, Object obj);

    /**
     * Map转模型
     *
     * @param model
     * @param map
     * @return
     */
    <T> T objecting(String model, Object map);

    Map<String, Object> nameToLname(String model, Map<String, Object> origin);

    List<Map<String, Object>> nameToLname(String model, List<Map<String, Object>> origin);

    Map<String, Object> lnameToName(String model, Map<String, Object> origin);

    List<Map<String, Object>> lnameToName(String model, List<Map<String, Object>> origin);

    Map<String, Object> lnameToColumn(String model, Map<String, Object> origin);

    List<Map<String, Object>> lnameToColumn(String model, List<Map<String, Object>> origin);

    Map<String, Object> columnToLname(String model, Map<String, Object> origin);

    List<Map<String, Object>> columnToLname(String model, List<Map<String, Object>> origin);

}
