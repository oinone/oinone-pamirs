package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.api.core.orm.clone.ReferenceUtils;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.base.BaseModel.MODEL_MODEL;

/**
 * 无id模型定义，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 34)
@Model(displayName = "基础模型基类", summary = "基础模型基类")
public class BaseModel extends AbstractModel {

    public static final String MODEL_MODEL = "base.BaseModel";

    private static final long serialVersionUID = -6111914084667478752L;

    private transient String draftCode;

    /**
     * 为模型数据列表填充默认值
     *
     * <p>为值为空的字段填充默认值
     *
     * @param <T> 模型类型
     * @return 模型数据列表
     */
    public static <T> List<T> construct(List<T> dataList) {
        List<T> result = constructor.construct(dataList);
        ReferenceUtils.dealList(result, dataList);
        return dataList;
    }

}
