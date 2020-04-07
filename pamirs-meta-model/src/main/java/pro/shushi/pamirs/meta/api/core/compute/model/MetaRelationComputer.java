package pro.shushi.pamirs.meta.api.core.compute.model;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.api.dto.meta.MetaRelation;

import java.util.List;
import java.util.Map;

/**
 * 元数据关系计算器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:11 下午
 *
 */
public interface MetaRelationComputer extends CommonApi {

    /**
     * 计算
     *
     * 计算关联关系字段和继承关系
     *
     * @param metaList
     * @return
     */
    Result<Map<String/*module*/, MetaRelation>> compute(List<Meta> metaList);

    /**
     * 读取所有已安装模块
     *
     * @return
     */
    Result<List<Map<String, Object>>> fetchInstallModules();

    /**
     * 读取元数据关系
     *
     * @param module
     * @return
     */
    Result<MetaRelation> read(String module);

    /**
     * 写元数据关系
     *
     * @param relationMap
     * @return
     */
    Result<Void> write(Map<String/*module*/, MetaRelation> relationMap);

}
