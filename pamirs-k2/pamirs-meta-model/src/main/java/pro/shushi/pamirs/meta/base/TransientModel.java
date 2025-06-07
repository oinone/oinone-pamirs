package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import static pro.shushi.pamirs.meta.base.TransientModel.MODEL_MODEL;

/**
 * 传输模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 44)
@Model(displayName = "传输模型基类", summary = "传输模型基类")
public class TransientModel extends K2 {

    public static final String MODEL_MODEL = "base.TransientModel";
    private static final long serialVersionUID = 6945520838130991182L;

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function(summary = "数据构造函数", openLevel = FunctionOpenEnum.API)
    public <T> T construct(T data) {
        return Models.constructor().construct(data);
    }

}
