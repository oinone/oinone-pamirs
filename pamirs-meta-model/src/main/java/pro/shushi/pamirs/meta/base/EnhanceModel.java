package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.EnhanceApi;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 增强模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.EnhanceModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "增强模型", summary = "增强模型")
public class EnhanceModel extends BaseModel implements EnhanceApi {

    @Function.Advanced(usage = FunctionUsageEnum.WRITE)
    @Function(summary = "数据同步函数")
    public <T> T synchronize(T data){
        return MetaApiFactory.getApi(EnhanceApi.class).synchronize(data);
    }

}
