package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.base.model.ViewAction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 企业设置
 * 应用设置
 * @author shier
 * date  2022/9/1 12:12 下午
 */
@Base
@Model(displayName = "应用/企业设置模型")
@Model.model(AppConfigTransient.MODEL_MODEL)
public class AppConfigTransient extends TransientModel {

    public static final String MODEL_MODEL = "app.AppConfigTransient";

    @Field(displayName = "企业编码")
    @Field.String
    private String companyCode;

    @Field.String(size = 512)
    @Field(displayName = "Logo")
    private String logo;

    @Field.many2one
    @Field(displayName = "门户应用",summary = "门户设置中的首个应用的设置")
    private UeModule gatewayModule;

    @Field.String
    @Field(displayName = "域名设置", summary = "当前的服务的域名设置")
    private String domain;

    @Field.Integer
    @Field(displayName = "剩余修改域名次数", summary = "剩余修改域名次数")
    private Long domainTimes;

    @Field.Boolean
    @Field(displayName = "将工作台设置为首页",summary = "是否将工作台设置为首页")
    private Boolean workbenchAsFist;

    @Field.many2one
    @Field(displayName = "门户首页",summary = "门户中的首页设置")
    private AppMenu gatewayMenu;

    @Field.many2one
    @Field(displayName = "应用首页")
    private ViewAction homepage;

    @Field(displayName = "是否支持搜索")
    @Field.Boolean
    private Boolean searchable;

    @Field(displayName = "租户")
    @Field.String
    private String tenant;

}
