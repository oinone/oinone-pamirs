package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.resource.api.enmu.BICEnum;
import pro.shushi.pamirs.resource.api.enmu.ResourceBankStatusEnum;

@Model.model(ResourceBank.MODEL_MODEL)
@Model(displayName = "银行", labelFields = "name")
public class ResourceBank extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceBank";

    @Field.String
    @Field(required = true, displayName = "名称")
    private String name;

    @Field.Enum
    @Field(invisible = true, displayName = "银行识别号码前缀", summary = "Bank Identifier Code, BIC 或者 Swift")
    private BICEnum bic;

    @Field.String
    @Field(required = true, displayName = "银行识别号码", summary = "Bank Identifier Code, BIC 或者 Swift")
    private String bicCode;

    @Field.many2one
    @Field(displayName = "银行LOGO")
    private PamirsFile logo;

    @Field.many2one
    @Field(displayName = "地址")
    private ResourceAddress address;

    @Field.String
    @Field(invisible = true, displayName = "邮箱地址")
    private String email;

    @Field.String
    @Field(invisible = true, displayName = "电话号码")
    private String phone;

    @Field.Enum
    @Field(required = true, displayName = "网点状态", defaultValue = "ENABLE")
    private ResourceBankStatusEnum status;

}
