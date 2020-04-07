package pro.shushi.pamirs.meta.api.dto.meta;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.*;

/**
 *
 * 模块元数据关联
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
@Data
public class MetaRelation {

    private String version;

    private Map<String/*model*/, Set<String/*super model*/>> superModels = new HashMap<>();

    private Map<String/*model*/, Set<String/*relation model*/>> relationModels = new HashMap<>();

    public void addAllSuperModels(String model, List<String> modelSuperModels){
        Set<String> existModelSuperModels = superModels.get(model);
        if(null == existModelSuperModels){
            existModelSuperModels = new HashSet<>();
            superModels.put(model, existModelSuperModels);
        }
        existModelSuperModels.addAll(modelSuperModels);
    }

    public void addAllRelationModels(String model, List<String> modelRelationModels){
        Set<String> existModelRelationModels = relationModels.get(model);
        if(null == existModelRelationModels){
            existModelRelationModels = new HashSet<>();
            relationModels.put(model, existModelRelationModels);
        }
        existModelRelationModels.addAll(modelRelationModels);
    }

}
