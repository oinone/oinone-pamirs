package pro.shushi.pamirs.auth.api.model.shared;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.Date;
import java.util.List;

/**
 * 分享页面记录
 *
 * @author Adamancy Zhang at 12:46 on 2024-04-12
 */
@Base
@Model.model(AuthSharedPageRecord.MODEL_MODEL)
@Model.Advanced(unique = {"sharedCode"})
@Model(displayName = "分享页面记录", labelFields = {"linkText"})
public class AuthSharedPageRecord extends IdModel {

    private static final long serialVersionUID = -3067000193485321333L;

    public static final String MODEL_MODEL = "auth.SharedPageRecord";

    @Field.String(size = 64)
    @Field(displayName = "分享码")
    private String sharedCode;

    @Field.String(size = 64)
    @Field(displayName = "授权码")
    private String authorizationCode;

    @Field.String
    @Field(displayName = "被分享模块")
    private String sharedModule;

    @Field.String
    @Field(displayName = "被分享动作模型")
    private String sharedModel;

    @Field.String
    @Field(displayName = "被分享动作名称")
    private String sharedAction;

    @Field.Text
    @Field(displayName = "被分享动作参数")
    private String sharedParameters;

    @Field(displayName = "浏览器标题")
    private String browserTitle;

    @Field(displayName = "当前语言")
    private String language;

    @Field(displayName = "当前语言ISO编码")
    private String languageIsoCode;

    @Field.Integer
    @Field(displayName = "超时时间")
    private Integer timeout;

    @Field.String
    @Field(displayName = "超时时间单位")
    private String timeoutUnit;

    @Field.Date
    @Field(displayName = "失效时间")
    private Date invalidTime;

    @Field.Text
    @Field(displayName = "分享链接")
    private String url;

    @Field.Text
    @Field(displayName = "链接文本")
    private String linkText;

    @Field.one2many
    @Field.Relation(relationFields = {"sharedCode"}, referenceFields = {"sharedCode"})
    @Field(displayName = "资源路径")
    private List<AuthSharedPagePath> sharedPaths;
}
