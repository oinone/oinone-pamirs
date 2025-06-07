package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 模版定义的抽象基类
 *
 * @author Adamancy Zhang at 19:17 on 2021-06-09
 */
@Base
@Model.model("pamirs.eip.AbstractTemplate")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, unique = {"code", "name"})
@Model(displayName = "模版定义的抽象基类", labelFields = "name")
public abstract class AbstractTemplate extends CodeModel {

    private static final long serialVersionUID = -7813366818775364733L;

    @Field.String
    @Field(displayName = "模板名称", required = true)
    private String name;
}
