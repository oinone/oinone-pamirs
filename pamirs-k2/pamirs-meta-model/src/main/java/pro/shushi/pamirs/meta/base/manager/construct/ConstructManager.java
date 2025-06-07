package pro.shushi.pamirs.meta.base.manager.construct;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.ModelsHelper;
import pro.shushi.pamirs.meta.api.core.orm.systems.ConstructApi;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

/**
 * 存储模型数据构造器
 * <p>
 * 2020/5/7 11:58 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Fun(BaseModel.MODEL_MODEL)
@Component
public class ConstructManager implements ConstructApi {

    public static ConstructManager getInstance() {
        return ModelsHelper.constructor();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(summary = "数据构造函数", openLevel = {FunctionOpenEnum.API, FunctionOpenEnum.REMOTE})
    @Override
    public <T> T construct(T data) {
        if (data instanceof List) {
            return (T) constructBatch((List) data);
        }
        return CommonApiFactory.getApi(ConstructApi.class).construct(data);
    }

    @Function.Advanced(displayName = "批量初始化数据")
    @Function
    public <T> List<T> constructBatch(List<T> dataList) {
        for (T data : dataList) {
            CommonApiFactory.getApi(ConstructApi.class).construct(data);
        }
        return dataList;
    }

}
