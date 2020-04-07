package pro.shushi.pamirs.meta.domain;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 应用分类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@Base
@Model.model("base.MetaRelation")
@Model(displayName = "元数据关系", summary = "元数据关系", labelFields = "module")
public class MetaRelation extends IdModel {

    private static final long serialVersionUID = 841872998152617405L;

    @Base
    @Field.String
    @Field(displayName = "模块", unique = true, required = true)
    private String module;

    @Base
    @Field.String
    @Field(displayName = "版本", required = true)
    private String version;

    @Base
    @Field.Text
    @Field(displayName = "关系", required = true)
    private String relation;

    @Base
    @Field.Integer
    @Field.field("opt")
    @Field(displayName = "乐观锁", defaultValue = "0", required = true)
    private Long optVersion;

}
