package pro.shushi.pamirs.framework.connectors.data.api.ddl;

import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.Map;
import java.util.Set;

/**
 * 数据表计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface TableComputer extends CommonApi {

    /**
     * 计算 create table sql
     *
     * @param meta          元数据
     * @param logicTableMap 数据表schema
     * @param supportDrop   支持删除字段和表
     * @return 返回值
     */
    DdlResult compute(Meta meta, Map<String/*schema#table*/, LogicTable> logicTableMap, boolean supportDrop);

    /**
     * 计算 create table sql
     *
     * @param meta           元数据
     * @param includeModules 包含模块
     * @param logicTableMap  数据表schema
     * @param supportDrop    支持删除字段和表
     * @return 返回值
     */
    DdlResult compute(Meta meta, Set<String> includeModules, Map<String/*schema#table*/, LogicTable> logicTableMap, boolean supportDrop);

    /**
     * 计算 单个模型的 create table sql(含分库分表处理)
     *
     * @param modelTableContext 模型表映射
     * @param modelDefinition   单个模型元数据
     * @return 返回值
     */
    DdlResult compute(ModelTableContext modelTableContext, ModelDefinition modelDefinition);

    /**
     * 计算 单个模型的 create table sql(不含分库分表)
     *
     * @param schemaTableKey  当前库表
     * @param modelDefinition 单个模型元数据
     * @param logicTable      数据表schema
     * @return 返回值
     */
    TableResult compute(SchemaTableKey schemaTableKey, ModelWrapper modelDefinition, LogicTable logicTable);

    /**
     * 计算 单个模型的 create table sql(不含分库分表)
     *
     * @param schemaTableKey  当前库表
     * @param modelDefinition 单个模型元数据
     * @param logicTable      数据表schema
     * @return 返回值
     */
    TableResult compute(SchemaTableKey schemaTableKey, ModelDefinition modelDefinition, LogicTable logicTable);

    /**
     * 计算 单个模型的 create table sql(不含分库分表)
     *
     * @param module          模块名称
     * @param schemaTableKey  当前库表
     * @param modelDefinition 单个模型元数据
     * @param logicTable      数据表schema
     * @return 返回值
     */
    TableResult compute(String module, SchemaTableKey schemaTableKey, ModelConfig modelDefinition, LogicTable logicTable);

    /**
     * 获取锁表命令前缀
     *
     * @param dsKey 数据源
     * @return 锁表命令前缀
     */
    @SuppressWarnings("unused")
    String lockTableCommandPrefix(String dsKey);

    /**
     * 获取解锁表命令前缀
     *
     * @param dsKey 数据源
     * @return 解锁表命令前缀
     */
    String unlockTableCommandPrefix(String dsKey);

}
