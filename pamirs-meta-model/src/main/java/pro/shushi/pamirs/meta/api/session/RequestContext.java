package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.config.TxConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 请求上下文
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:41 下午
 */
@Data
public class RequestContext implements Serializable {

    private static final long serialVersionUID = 726467652569824666L;

    private volatile Map<String/*model*/, ModelConfig> modelMap;

    private volatile Map<String/*namespace.fun*/, TxConfig> txConfigMap;

    private volatile Map<String/*dictionary*/, DataDictionary> dict;

    public ModelConfig getModel(String model){
        return modelMap.get(model);
    }

    public void addModel(ModelConfig modelConfig){
        modelMap.putIfAbsent(modelConfig.getModel(), modelConfig);
    }

    public ModelFieldConfig getModelField(String model, String field){
        ModelConfig modelConfig = modelMap.get(model);
        if(null == modelConfig){
            return null;
        }
        List<ModelFieldConfig> modelFieldConfigList = modelConfig.getModelFieldConfigList();
        for(ModelFieldConfig modelFieldConfig : modelFieldConfigList){
            if(modelFieldConfig.getField().equals(field)){
                return modelFieldConfig;
            }
        }
        return null;
    }

    public Function getFunction(String fun){
        return getFunction(NamespaceConstants.pamirs, fun);
    }

    public Function getFunction(String namespace, String fun){
        ModelConfig modelConfig = modelMap.get(namespace);
        if(null == modelConfig){
            return null;
        }
        List<Function> functionList = modelConfig.getFunctionList();
        for(Function function : functionList){
            if(function.getFun().equals(fun)){
                return function;
            }
        }
        return null;
    }

    public DataDictionary getDictionary(String dictionary){
        return dict.get(dictionary);
    }

    public void addDictionary(DataDictionary dataDictionary){
        dict.putIfAbsent(dataDictionary.getDictionary(), dataDictionary);
    }

    public TxConfig getTxConfig(String namespace, String fun){
        return txConfigMap.get(namespace + CharacterConstants.SEPARATOR_DOT + fun);
    }

    public void addTxConfig(TxConfig txConfig){
        txConfigMap.putIfAbsent(txConfig.getNamespace() + CharacterConstants.SEPARATOR_DOT + txConfig.getFun(), txConfig);
    }

    public void clear(){
        if(null != modelMap) modelMap.clear();
        if(null != dict) dict.clear();
        modelMap = null;
        dict = null;
    }

}
