package pro.shushi.pamirs.sys.setting.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.sys.setting.enmu.LoginTypeEnum;
import pro.shushi.pamirs.sys.setting.enmu.TenantDomainEnum;

/**
 * SysSetting
 *
 * @author yakir on 2022/11/08 10:18.
 */
@Model(displayName = "系统配置")
@Model.model(SysSettings.MODEL_MODEL)
public class SysSettings extends IdModel {

    private static final long serialVersionUID = 2306992410723959272L;

    public static final String MODEL_MODEL = "sysSetting.SysSettings";

    @Field.Enum
    @Field(displayName = "登录方式", defaultValue = "PHONE_MA", summary = "平台系统配置可以设置平台支持的登录方式")
    private LoginTypeEnum loginType;

    @Field.Boolean
    @Field(displayName = "开放注册", defaultValue = "true", summary = "平台系统配置可以设置是否开放注册")
    private Boolean openReg;

    @Field.Boolean
    @Field(displayName = "开放域名设置", defaultValue = "false", summary = "平台系统配置可以设置是否开放域名设置")
    private Boolean openDomainSetting;

    @Field.Boolean
    @Field(displayName = "开放租户名设置", defaultValue = "false", summary = "平台系统配置可以设置是否开放租户名设置")
    private Boolean openTenantSetting;

    @Field.Boolean
    @Field(displayName = "注册是否需要邀请码", defaultValue = "false", summary = "注册表单增加「邀请码」输入框并在提交后校验邀请码")
    private Boolean regInvite;

    @Field.Boolean
    @Field(displayName = "创建团队是否需要邀请码", defaultValue = "true", summary = "注册表单增加「邀请码」输入框并在提交后校验邀请码")
    private Boolean regTeamInvite;

    @Field.Boolean
    @Field(displayName = "创建团队是否同时创建租户", defaultValue = "true",
            summary = "创建团队是否同时创建租户。企业平台创建团队如果不创建租户，则隐藏企业的三级域名设置且域名与企业平台域名一致。")
    private Boolean regTeamTenant;

    @Field.Enum
    @Field(displayName = "租户域名访问方式", defaultValue = "L3_DOMAIN", summary = "https://三级域名.oinone.top | https://www.oinone.top/顶级路径/")
    private TenantDomainEnum tenantDomain;

    @Field.Integer
    @Field(displayName = "邀请码有效期")
    private Integer invitationCodeValidTime = 60 * 24 * 10;    //单位：分钟；默认有效期10天

    @Field.Integer
    @Field(displayName = "试用时效")
    private Integer trialDays = 7;    //默认试用7天

    @Field.Boolean
    @Field(displayName = "首次登录必须修改初始密码", defaultValue = "false", summary = "首次登录必须修改初始密码")
    private Boolean needModifyInitialPassword;

}
