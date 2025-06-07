package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.VersionModel;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.behavior.IUserNameModel;


@Base
@Model.model(BizVersionIdModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "带乐观锁的id业务模型", summary = "带id模型业务抽象基类")
public class BizVersionIdModel extends VersionModel implements IUserNameModel {

    public static final String MODEL_MODEL = "business.BizVersionIdModel";

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "创建人", store = NullableBoolEnum.FALSE, priority = 201)
    private String createUserName;

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "修改人", store = NullableBoolEnum.FALSE, priority = 211)
    private String writeUserName;
}
