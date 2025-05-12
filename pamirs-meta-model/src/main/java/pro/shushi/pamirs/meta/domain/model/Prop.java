package pro.shushi.pamirs.meta.domain.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 属性
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model("base.Prop")
@Model(displayName = "属性", summary = "属性")
public class Prop extends TransientModel {

    private static final long serialVersionUID = -8976114973632386115L;

    @Base
    @Field(displayName = "名称", required = true)
    private String name;

    @Base
    @Field(displayName = "值", required = true)
    private Object value;

}
