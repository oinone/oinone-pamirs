package pro.shushi.pamirs.resource.api.tmodel;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.util.regex.Pattern;

@Model.model(PamirsEmail.MODEL_MODEL)
@Model(displayName = "E-mail")
public abstract class PamirsEmail extends TransientModel {

    public static final String MODEL_MODEL = "base.PamirsEmail";

    private static final Pattern EMAIL_CHECKER = Pattern.compile("^[a-z0-9A-Z]+[-|a-z0-9A-Z._]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$"); // 邮箱以数字或者字母开头

    @Field
    @Field.String
    private String value;

    public PamirsEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            if (!EMAIL_CHECKER.matcher(email).matches()) {
                throw PamirsException.construct(ExpEnumerate.EMAIL_INVALID).errThrow();
            }
        }
        setValue(email);
    }
}
