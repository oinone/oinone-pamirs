package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.orm.clone.ReferenceUtils;
import pro.shushi.pamirs.meta.base.manager.construct.ConstructManager;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import static pro.shushi.pamirs.meta.base.K2.MODEL_MODEL;

/**
 * 模型基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 31)
@Model(displayName = "模型基类", summary = "模型基类")
@SuppressWarnings("unchecked")
public class K2 extends D {

    public static final String MODEL_MODEL = "base.K2";
    private static final long serialVersionUID = -8932738637958832347L;

    /**
     * 数据构造器
     */
    protected final static ConstructManager constructor = ConstructManager.getInstance();

    /**
     * 为模型数据填充默认值
     * <br/>
     * 为值为空的字段填充默认值
     *
     * @param <T> 模型类型
     * @return 模型数据
     */
    public <T> T construct() {
        T result = constructor.construct((T) this);
        ReferenceUtils.deal(result, this);
        return (T) this;
    }

}
