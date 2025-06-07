package pro.shushi.pamirs.meta.common.util;

import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

/**
 * TypeReferences
 *
 * @author yakir on 2023/07/25 18:55.
 */
public interface TypeReferences {

    TypeReference<Map<String, String>> TR_MAP_SS = new TypeReference<Map<String, String>>() {};
    TypeReference<Map<String, Object>> TR_MAP_SO = new TypeReference<Map<String, Object>>() {};
    TypeReference<List<Map<String, Object>>> TR_LIST_MAP_SO = new TypeReference<List<Map<String, Object>>>() {};
}
