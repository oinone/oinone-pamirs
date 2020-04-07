package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.orm.ConstructApi;
import pro.shushi.pamirs.meta.enmu.FunctionUsageEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 传输模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.TransientModel")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "传输模型", summary = "传输模型")
public class TransientModel extends pro.shushi.pamirs.meta.base.D implements ConstructApi {

    @Function.Advanced(usage = FunctionUsageEnum.READ)
    @Function(summary = "数据构造函数")
    public <T> T construct(T data){
        return MetaApiFactory.getApi(ConstructApi.class).construct(data);
    }

}
