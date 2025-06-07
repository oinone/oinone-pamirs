package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;


@Model.model(ResourceMajorConfig.MODEL_MODEL)
@Model(displayName = "系统基础配置信息", summary = "系统基础配置信息")
public class ResourceMajorConfig extends IdModel implements SingletonModel<ResourceMajorConfig> {

    private static final long serialVersionUID = -8813811110743840983L;

    public static final String MODEL_MODEL = "resource.major.ResourceMajorConfig";

    @Field.String
    @Field(required = true, displayName = "主伙伴名称")
    private String partnerName;

    @Field.String
    @Field(required = true, displayName = "主伙伴code")
    private String partnerCode;

    @Field.String
    @Field(required = true, displayName = "主伙伴id")
    private String partnerId;

    @Field.String
    @Field(required = true, displayName = "主伙伴uid")
    private String partnerUid;

    @Field.String
    @Field(required = true, displayName = "账户默认密码")
    private String defaultPassword;

    @Field.String
    @Field(required = true, displayName = "主伙伴店铺Code")
    private String partnerShopCode;

    @Field(displayName = "租户logo", summary = "建议尺寸220x74，登录页")
    @Field.String(size = 512)
    private String tenantLogo;

    @Field(displayName = "租户的浏览器ico图", summary = "建议尺寸64x64")
    @Field.String(size = 512)
    private String tenantLogoFaviconIco;

    @Field(displayName = "租户logo小尺寸", summary = "建议尺寸220x74，顶部")
    @Field.String(size = 512)
    private String tenantLogoSmall;

    @Field(displayName = "user.login的前缀")
    @Field.String(size = 128)
    private String userLoginPrefix;

    @Field(displayName = "默认用户头像", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String defaultAvatarUrl;

    @Field(displayName = "默认店铺logo", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String defaultShopLogo;

    @Field(displayName = "登录页logo", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String loginPageLogo;

    @Field(displayName = "浏览器小icon", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String browserIcon;

    @Field(displayName = "浏览器默认title ", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String browserTitle;

    @Field(displayName = "应用旁边logo", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String appSideLogo;

    @Field(displayName = "默认应用logo", summary = "建议尺寸256x256，通过参数动态裁剪大小")
    @Field.String(size = 512)
    private String defaultAppLogo;

    @Field.String
    @Field(displayName = "默认CSV导出编码", defaultValue = "GB2312", required = true)
    private String defaultCsvCharset;

    @Function(openLevel = FunctionOpenEnum.API, summary = "系统基础配置信息构造方法")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public ResourceMajorConfig construct(ResourceMajorConfig config) {
        ResourceMajorConfig config1 = config.singletonModel();
        if (config1 != null) {
            return config1;
        }
        return config.construct();
    }

    public static ResourceMajorConfig singelResourceMajorConfig() {
        ResourceMajorConfig config1 = new ResourceMajorConfig().singletonModel();
        if (config1 != null) {
            return config1;
        } else {

            throw PamirsException.construct(ExpEnumerate.RESOURCE_NO_DEFAULT_BASE_MAJOR_ERROR).errThrow();
        }
    }


    @Override
    public void initSystem() {
    }
}
