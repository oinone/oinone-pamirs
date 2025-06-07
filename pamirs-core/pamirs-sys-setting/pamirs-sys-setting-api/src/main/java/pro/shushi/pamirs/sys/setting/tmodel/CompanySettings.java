package pro.shushi.pamirs.sys.setting.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * CompanySettings
 *
 * @author yakir on 2022/09/20 11:51.
 */
@Model(displayName = "企业设置")
@Model.model(CompanySettings.MODEL_MODEL)
public class CompanySettings extends TransientModel {

    private static final long serialVersionUID = 1163539484107476882L;

    public static final String MODEL_MODEL = "sysSetting.CompanySettings";

    @Field(displayName = "公司编码")
    @Field.String
    private String companyCode;

    @Field(displayName = "是否支持搜索")
    @Field.Boolean
    private Boolean searchable;

    @Field(displayName = "Logo")
    @Field.Text
    private String logo;

    @Field(displayName = "个性化域名")
    @Field.String
    private String domainName;

    @Field(displayName = "租户")
    @Field.String
    private String tenant;

}
