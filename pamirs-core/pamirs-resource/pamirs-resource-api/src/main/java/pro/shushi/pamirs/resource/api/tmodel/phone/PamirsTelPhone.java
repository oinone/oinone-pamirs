package pro.shushi.pamirs.resource.api.tmodel.phone;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(PamirsTelPhone.MODEL_MODEL)
@Model(displayName = "座机号码", summary = "只能是座机")
public abstract class PamirsTelPhone extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsTelPhone";

    @Field
    @Field.String
    private String value;

    // TODO 考虑国际化

    public PamirsTelPhone(String telPhone) {
        if (StringUtils.isNotBlank(telPhone)) {
            // TODO 校验号码
        }
        setValue(telPhone);
    }
}
