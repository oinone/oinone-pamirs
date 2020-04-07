package pro.shushi.pamirs.meta.api.core.systems.inherit;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.List;

/**
 * 继承处理系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
public interface InheritedProcessor extends CommonApi {

    /**
     * 为多表继承生成一对一关联关系字段
     *
     * @param modelConfig
     * @param superModel
     * @param pkList
     * @return
     */
    ModelField makeOneToOneFieldForInherited(ModelDefinition modelConfig, ModelDefinition superModel, List<String> pkList);

    /**
     * 添加继承字段
     *
     * @param meta
     * @param modelDefinition
     * @param superModel
     */
    void dealInheritedField(MetaData meta, ModelDefinition modelDefinition, ModelDefinition superModel);

    /**
     * 扩展继承扩展父类字段
     *
     * @param meta
     * @param modelDefinition
     * @param superModel
     */
    void dealSuperField(MetaData meta, ModelDefinition modelDefinition, ModelDefinition superModel);

    /**
     * 继承父类方法
     *
     * @param meta
     * @param modelDefinition
     * @param superModel
     */
    void dealInheritedMethod(MetaData meta, ModelDefinition modelDefinition, ModelDefinition superModel);

    /**
     * 扩展父类方法
     *
     * @param meta
     * @param modelDefinition
     * @param superModel
     */
    void dealSuperMethod(MetaData meta, ModelDefinition modelDefinition, ModelDefinition superModel);

    /**
     * 扩展传递字段
     *
     * @param meta
     * @param self
     * @param otherField
     */
    void addField(MetaData meta, ModelDefinition self, ModelField otherField);

    /**
     * 生成多表继承关系字段
     *
     * @param meta
     * @param self
     * @param superModel
     * @param oneToOneField
     */
    void addFieldForMultiTableInherited(MetaData meta, ModelDefinition self, ModelDefinition superModel, ModelField oneToOneField);

    /**
     * 添加函数
     *
     * @param meta
     * @param self
     * @param otherFunction
     */
    void addFunction(MetaData meta, ModelDefinition self, FunctionDefinition otherFunction);

}
