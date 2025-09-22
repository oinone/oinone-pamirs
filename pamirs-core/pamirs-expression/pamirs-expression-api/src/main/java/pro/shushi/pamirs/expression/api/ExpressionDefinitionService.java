package pro.shushi.pamirs.expression.api;

import pro.shushi.pamirs.expression.model.ExpressionDefine;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import java.util.List;
import java.util.Map;

/**
 * DesignerFunctionDefinition
 *
 * @author yakir on 2021/08/09 11:38.
 */
@Base
@Fun(ExpressionDefinitionService.FUN_NAMESPACE)
public interface ExpressionDefinitionService {

    String FUN_NAMESPACE = "expression.ExpressionDefinitionService";

    @Function
    Integer createOrUpdate(ExpressionDefine data);

    @Function
    List<ExpressionDefine> createOrUpdateBatch(List<ExpressionDefine> dataList);

    @Function
    List<ExpressionDefine> createBatch(List<ExpressionDefine> dataList);

    @Function
    Boolean deleteByCode(ExpressionDefine data);

    @Function
    List<ExpressionDefine> queryList(IWrapper<ExpressionDefine> wrapper);

    @Function
    List<ExpressionDefine> queryByKeyPrefix(String searchKeyPrefix);

    @Function
    List<ExpressionDefine> queryByKeyPrefixes(List<String> keyPrefixes);

    /**
     * 根据key的前缀,复制数据
     *
     * @param searchKeyPrefix 用于匹配的key前缀
     * @param copiedKeyPrefix 复制后替换的key前缀
     * @return
     */
    @Function
    List<ExpressionDefine> copyByKeyPrefix(String searchKeyPrefix, String copiedKeyPrefix);

    @Function
    Boolean deleteByKeyPrefix(String searchKeyPrefix);

    @Function
    ExpressionDefine queryModelLabelExpByModel(ExpressionDefine data);

    @Function
    List<ExpressionDefine> cloneWithNodeIdRemap(Map<String/*oldKey*/, String/*newKey*/> keyMappings);
}
