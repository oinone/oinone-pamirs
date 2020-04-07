package pro.shushi.pamirs.meta.base;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.Date;

/**
 * 超级抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.Constraints({
        @Model.Constraint(foreignKey = "createUser", relationFields = "createUid", references = "base.User", referenceFields = "id"),
        @Model.Constraint(foreignKey = "writeUser", relationFields = "writeUid", references = "base.User", referenceFields = "id")
})
@Model.model("pamirs")
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, name = "pamirs")
@Model(displayName = "抽象基类", summary = "道生一")
public abstract class AbstractModel extends pro.shushi.pamirs.meta.base.D {

    @Base
    @Field.Advanced(priority = 200, columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP")
    @Field(displayName = "创建时间")
    private Date createDate;

    @Base
    @Field.Advanced(priority = 201, columnDefinition = "DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Field(displayName = "更新时间")
    private Date writeDate;

    @Base
    @Field.Advanced(priority = 202)
    @Field(displayName = "创建人")
    private Long createUid;

    @Base
    @Field.Advanced(priority = 203)
    @Field(displayName = "更新人")
    private Long writeUid;

    @Base
    @Field.Text
    @Field(displayName = "聚合", store = NullableBoolEnum.FALSE)
    private String aggs;

    @Base
    @Field.String
    @Field(displayName = "数据签名", store = NullableBoolEnum.FALSE)
    private String sign;

}
