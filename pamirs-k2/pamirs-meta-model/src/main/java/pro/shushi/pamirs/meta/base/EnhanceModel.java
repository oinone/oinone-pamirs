package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.EnhanceApi;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.search.ElasticSearchApi;
import pro.shushi.pamirs.meta.enmu.FunctionCategoryEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.API;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.LOCAL;
import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.REMOTE;

/**
 * 增强模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Slf4j
@Base
@Model.model(EnhanceModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 46)
@Model(displayName = "增强模型", summary = "增强模型")
public class EnhanceModel extends IdModel implements EnhanceApi {

    private static final long serialVersionUID = -4514742829528617310L;

    public static final String MODEL_MODEL = "base.EnhanceModel";

    @Base
    @Field.Integer
    @Field(displayName = "逻辑删除")
    private Long isDeleted;

    @Override
    @Function(summary = "搜索函数", openLevel = {LOCAL, REMOTE, API})
    @Function.Advanced(type = FunctionTypeEnum.QUERY, category = FunctionCategoryEnum.QUERY_PAGE, managed = true)
    public <T> Pagination<T> search(Pagination<T> page, IWrapper<T> queryWrapper) {
        return CommonApiFactory.getApi(ElasticSearchApi.class)
                .search(page, queryWrapper);
    }

    @Override
    @Function.Advanced(displayName = "同步数据", type = FunctionTypeEnum.UPDATE)
    @Function(summary = "数据同步函数")
    public <T> List<T> synchronize(List<T> data) {
        return data;
    }
}
