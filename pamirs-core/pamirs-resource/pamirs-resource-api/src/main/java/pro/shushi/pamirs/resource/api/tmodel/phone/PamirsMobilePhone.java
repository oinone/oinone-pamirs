package pro.shushi.pamirs.resource.api.tmodel.phone;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(PamirsMobilePhone.MODEL_MODEL)
@Model(displayName = "手机号码", summary = "只能是手机号")
public abstract class PamirsMobilePhone extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsMobilePhone";

    @Field
    @Field.String
    private String value;

    // TODO 考虑国际化

    public PamirsMobilePhone(String mobilePhone) {
        if (StringUtils.isNotBlank(mobilePhone)) {
            // TODO 校验手机号
        }
        setValue(mobilePhone);
    }
}
