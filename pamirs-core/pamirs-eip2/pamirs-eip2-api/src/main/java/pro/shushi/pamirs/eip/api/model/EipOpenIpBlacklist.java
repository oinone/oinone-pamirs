package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

/**
 * @author yeshenyue on 2025/5/9 17:20.
 */
@Model(displayName = "开放应用IP黑名单")
@Model.model(EipOpenIpBlacklist.MODEL_MODEL)
@Model.Advanced(unique = {"applicationCode,ip"})
public class EipOpenIpBlacklist extends IdModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipOpenIpBlacklist";
    private static final long serialVersionUID = 4534458163380452907L;

    @Field.String
    @Field(displayName = "开放应用编码")
    private String applicationCode;

    @Field.many2one
    @Field.Relation(relationFields = {"applicationCode"}, referenceFields = {"code"})
    @Field(displayName = "开放应用")
    private EipApplication application;

    @Field.String
    @Field(displayName = "IP/IP网段")
    private String ip;

    @Field.Integer
    @Field(displayName = "响应状态码", summary = "被IP黑名单规则限制时的响应状态码")
    private Integer httpCode;

    @Field.String(size = 1024)
    @Field(displayName = "响应结果", summary = "被IP黑名单规则限制时的响应结果")
    private String httpResult;

    @Field.many2one
    @Field(displayName = "创建人")
    @Field.Relation(relationFields = {"createUid"}, referenceFields = {"id"})
    private PamirsUser createUser;
}
