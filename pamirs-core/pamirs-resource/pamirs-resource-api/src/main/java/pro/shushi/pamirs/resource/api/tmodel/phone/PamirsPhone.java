package pro.shushi.pamirs.resource.api.tmodel.phone;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

@Model.model(PamirsPhone.MODEL_MODEL)
@Model(displayName = "手机或者座机号码")
public abstract class PamirsPhone extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsPhone";

    @Field
    @Field.String
    private String value;

    // TODO 考虑国际化

    public PamirsPhone(String phone) {
        if (StringUtils.isNotBlank(phone)) {
            // TODO 校验号码
        }
        setValue(phone);
    }
}
