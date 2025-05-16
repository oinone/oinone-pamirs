package pro.shushi.pamirs.business.api.model;

import pro.shushi.pamirs.boot.base.ux.annotation.field.UxWidget;
import pro.shushi.pamirs.boot.base.ux.annotation.view.UxForm;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.constant.ExpConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.behavior.IUserNameModel;


@Base
@Model.model(BizCodeModel.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
@Model(displayName = "基础业务模型", summary = "提供业务表单中所需要使用的ID，编码，创建人名称，创建时间等业务基础能力")
public class BizCodeModel extends CodeModel implements IUserNameModel {

    public static final String MODEL_MODEL = "business.BizCodeModel";

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "创建人名称", store = NullableBoolEnum.FALSE, priority = 201)
    private String createUserName;

    @Base
    @Field.String
    @UxForm.FieldWidget(@UxWidget(readonly = "true", invisible = ExpConstants.idValueNotExist))
    @Field(displayName = "修改人名称", store = NullableBoolEnum.FALSE, priority = 211)
    private String writeUserName;

}
