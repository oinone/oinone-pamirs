package pro.shushi.pamirs.meta.api.dto.meta;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.ModelData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 跨模块元数据
 * <p>
 * 2021/2/25 11:18 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class MetaCrossing {

    // 启动模块列表
    private Set<String> bootModuleSet;

    // 跨模块扩展来源表
    private Map<String/*modelData code*/, String/*source module*/> metaCrossingMap = new HashMap<>();

    public void put(Map<String/*modelData code*/, String/*source module*/> extendMap) {
        int oldSize = metaCrossingMap.size();
        int additionalSize = extendMap.size();
        metaCrossingMap.putAll(extendMap);
        int newSize = metaCrossingMap.size();
        if (newSize != oldSize + additionalSize) {
            throw new RuntimeException("There is same crossing meta.");
        }
    }

    public boolean isCrossing(String group, String sign) {
        return metaCrossingMap.containsKey(ModelData.generateCode(group, sign));
    }

    public boolean isCrossingFromIgnoreModule(String group, String sign) {
        String crossingModule = metaCrossingMap.get(ModelData.generateCode(group, sign));
        return null != crossingModule && !bootModuleSet.contains(crossingModule);
    }

}
