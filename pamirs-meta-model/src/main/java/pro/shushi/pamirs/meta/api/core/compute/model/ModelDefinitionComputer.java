package pro.shushi.pamirs.meta.api.core.compute.model;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;

import java.util.List;

/**
 * 数据计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface ModelDefinitionComputer extends CommonApi {


    /**
     * 获取元数据配置
     *
     * @param metaDataList
     * @return
     */
    Result<RequestContext> fetchRequestContext(List<MetaData> metaDataList);

    /**
     * 将元数据填充到上下文
     *
     * @param metaDataList
     * @return
     */
    Result<Void> fillSession(List<MetaData> metaDataList);

    /**
     * 计算
     *
     * 计算默认值、计算字段、反转、序列生成、约束函数和表达式检查
     * 处理继承、补充关系字段、多对多关联模型
     *
     * @param meta
     * @return
     */
    Result<Void> compute(MetaData meta);

}
