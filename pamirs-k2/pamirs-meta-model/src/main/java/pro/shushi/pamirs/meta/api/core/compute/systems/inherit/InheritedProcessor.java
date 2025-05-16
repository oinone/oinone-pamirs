package pro.shushi.pamirs.meta.api.core.compute.systems.inherit;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.fun.Argument;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.fun.Type;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 继承处理系统
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface InheritedProcessor extends CommonApi {

    String inheritedTag = "Inherited";

    String TYPE_DISPLAY_NAME = "类型";

    /**
     * 按继承关系排序
     *
     * @param module              模块
     * @param modelDefinitionList 模型列表
     * @return 排序结果
     */
    List<ModelDefinition> sortModelByInherited(String module, List<ModelDefinition> modelDefinitionList);

    /**
     * 递归处理继承模型
     *
     * @param data            模型
     * @param completedModel  已计算模型
     * @param currentConsumer 当前模型消费者
     * @param parentConsumer  父模型消费者
     */
    void recursionModelForInherited(ModelDefinition data, Set<String> completedModel,
                                    Consumer<ModelDefinition/*current*/> currentConsumer,
                                    BiConsumer<ModelDefinition/*current*/, ModelDefinition/*parent*/> parentConsumer);

    /**
     * 为多表继承生成一对一关联关系字段
     *
     * @param modelConfig     模型配置
     * @param existModelField 已存在字段
     * @param superModel      父模型
     * @param pkList          主键列表
     * @return 生成字段
     */
    ModelField makeOneToOneFieldForInherited(ModelDefinition modelConfig, ModelField existModelField,
                                             ModelDefinition superModel, List<String> pkList);

    /**
     * 生成多表继承隐式一对一字段名
     *
     * @param superModelName 父模型名称
     * @return 字段名
     */
    String multiTableInheritedFieldName(String superModelName);

    /**
     * 处理代理字段
     *
     * @param modelDefinition 当前模型
     */
    void dealProxyField(ModelDefinition modelDefinition);

    /**
     * 处理多表继承类型字段
     *
     * @param context          上下文
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealInheritedTypeField(ComputeContext context, Meta meta,
                                ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 添加继承字段
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealInheritedField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 添加跨模型继承字段
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealCrossingInheritedField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 扩展继承扩展父类字段
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealSuperField(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 继承父类方法
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealInheritedMethod(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 扩展父类方法
     *
     * @param meta             元数据
     * @param modelDefinition  当前模型
     * @param superModel       父模型
     * @param systemSourceEnum 来源
     */
    void dealSuperMethod(Meta meta, ModelDefinition modelDefinition, ModelDefinition superModel, SystemSourceEnum systemSourceEnum);

    /**
     * 添加字段
     *
     * @param meta             元数据
     * @param self             当前模型
     * @param otherField       参考字段
     * @param systemSourceEnum 来源
     */
    void addField(Meta meta, ModelDefinition self, ModelField otherField, SystemSourceEnum systemSourceEnum);

    /**
     * 处理多表继承关系字段
     *
     * @param meta          元数据
     * @param self          当前模型
     * @param superModel    父模型
     * @param oneToOneField 一对一字段
     */
    void dealFieldForMultiTableInherited(Meta meta, ModelDefinition self, ModelDefinition superModel, ModelField oneToOneField);

    /**
     * 生成多表继承类型字段
     *
     * @param context         上下文
     * @param meta            元数据
     * @param modelDefinition 当前模型
     */
    void addTypeFieldForMultiTableInherited(ComputeContext context, Meta meta, ModelDefinition modelDefinition);

    /**
     * 添加函数
     *
     * @param meta             元数据
     * @param self             当前模型
     * @param otherFunction    参考函数
     * @param systemSourceEnum 来源
     */
    void addFunction(Meta meta, ModelDefinition self, FunctionDefinition otherFunction, SystemSourceEnum systemSourceEnum);

    /**
     * 将继承函数的父类模型转为当前模型
     *
     * @param argumentList 参数定义列表
     * @param returnType   返回值类型
     * @param currentModel 当前模型
     */
    void convertSuperModelToCurrentModelForFunction(List<Argument> argumentList, Type returnType, ModelDefinition currentModel);

    /**
     * 将继承函数的父类模型转为当前模型
     *
     * @param meta         元数据
     * @param argumentList 参数定义列表
     * @param returnType   返回值类型
     * @param currentModel 当前模型
     */
    void convertSuperModelToCurrentModelForFunction(Meta meta, List<Argument> argumentList, Type returnType, ModelDefinition currentModel);

    /**
     * 根据子模型获取所有祖先模型编码
     *
     * @param superModels 祖先模型容器
     * @param modelConfig 初始子模型
     */
    void collectAllSuperModels(List<String> superModels, ModelConfig modelConfig);

    /**
     * 是否继承
     *
     * @param meta            元数据
     * @param modelDefinition 待判断子模型
     * @param model           祖先模型
     * @return 是否继承自model
     */
    boolean isSuperModel(Meta meta, ModelDefinition modelDefinition, String model);

}
