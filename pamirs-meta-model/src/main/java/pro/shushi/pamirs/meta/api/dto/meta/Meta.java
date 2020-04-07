package pro.shushi.pamirs.meta.api.dto.meta;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 模块元数据，含依赖元数据
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:45 下午
 *
 */
@Data
public class Meta {

    private MetaData data = new MetaData();

    private MetaData dependentData = new MetaData();

    private Map<String/*model*/, String/*database*/> dependentModel = new HashMap<>();

    public ModelDefinition getModel(String model){
        ModelDefinition modelDefinition = data.getModel(model);
        if(null == modelDefinition){
            ModelDefinition dependentModelDefinition =  dependentData.getModel(model);
            if(null != dependentModelDefinition){
                dependentModel.put(model, dependentModelDefinition.getDatabase());
            }
            return dependentModelDefinition;
        }else{
            return modelDefinition;
        }
    }

}
