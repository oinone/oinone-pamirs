package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.business.api.model.BizCodeModel;
import pro.shushi.pamirs.core.common.behavior.IDataStatus;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.IEipDecryptProcessor;
import pro.shushi.pamirs.eip.api.IEipEncryptionProcessor;
import pro.shushi.pamirs.eip.api.model.strategy.EipOpenIpBlacklist;
import pro.shushi.pamirs.eip.api.pamirs.DefaultDecryptFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultEncryptionFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

import java.util.List;

/**
 * 开放应用
 *
 * @author Adamancy Zhang at 19:17 on 2021-06-09
 */
@Base
@Model.model(EipApplication.MODEL_MODEL)
@Model.Advanced(unique = {"appKey", "name"})
@Model(displayName = "开放应用", labelFields = "name", summary = "用于系统与系统间的数据交互")
@Model.Code(sequence = "ORDERLY_SEQ", prefix = "APP", size = 5, initial = 1)
public class EipApplication extends BizCodeModel implements IDataStatus {

    private static final long serialVersionUID = -1190114015590923516L;

    public static final String MODEL_MODEL = "pamirs.eip.EipApplication";
    public static final String APPLICATION_REL_OPENINTERFACE_MODEL_MODEL = "ApplicationRelOpenInterface";
    public static final String APPLICATION_REL_OPENINTERFACE_FILED_APP = "appKey";
    public static final String APPLICATION_REL_OPENINTERFACE_FILED_API = "interfaceName";

    @Field.String
    @Field(displayName = "应用名称")
    private String name;

    @Field.String(size = 32)
    @Field(displayName = "应用appKey")
    private String appKey;

    @Field.Text
    @Field(displayName = "应用简介")
    private String description;

    @Field.String
    @Field(displayName = "应用Logo")
    private String logo;

    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "DISABLED")
    private DataStatusEnum dataStatus;

    @Field.many2one
    @Field(displayName = "认证信息")
    @Field.Relation(relationFields = "appKey", referenceFields = "appKey")
    private EipAuthentication authentication;

    /**
     * 权限相关的字段不允许放在这里
     *
     * @deprecated 2.3.0
     */
    @Base
    @Field.many2many(through = APPLICATION_REL_OPENINTERFACE_MODEL_MODEL, relationFields = {APPLICATION_REL_OPENINTERFACE_FILED_APP}, referenceFields = {APPLICATION_REL_OPENINTERFACE_FILED_API})
    @Field.Relation(relationFields = {APPLICATION_REL_OPENINTERFACE_FILED_APP}, referenceFields = {APPLICATION_REL_OPENINTERFACE_FILED_API})
    @Field(displayName = "开放接口列表", summary = "接口权限组中开放接口列表总和")
    private List<EipOpenInterface> openInterfaceList;

    @Field.Text
    @Field(displayName = "IP白名单")
    private String ipWhiteList;

    @Field.Integer
    @Field(displayName = "响应状态码", summary = "被IP白名单规则限制时的响应状态码")
    private Integer ipWhiteRespHttpCode;

    @Field.String(size = 1024)
    @Field(displayName = "响应结果", summary = "被IP白名单规则限制时的响应结果")
    private String ipWhiteHttpResult;

    @Field.String
    @Field(displayName = "请求预处理函数命名空间")
    private String requestDecryptNamespace;

    @Field.String
    @Field(displayName = "请求预处理函数名称")
    private String requestDecryptFun;

    @Field.String
    @Field(displayName = "响应预处理函数命名空间")
    private String responseEncryptionNamespace;

    @Field.String
    @Field(displayName = "响应预处理函数名称")
    private String responseEncryptionFun;

    @Field.one2many
    @Field.Relation(relationFields = "code", referenceFields = "applicationCode")
    @Field(displayName = "ip黑名单")
    private List<EipOpenIpBlacklist> ipBlackList;

    @JSONField(serialize = false)
    public IEipDecryptProcessor getRequestDecryptProcessor() {
        String namespace = getRequestDecryptNamespace();
        String fun = getRequestDecryptFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultDecryptFunction(namespace, fun);
    }

    @JSONField(serialize = false)
    public IEipEncryptionProcessor getResponseEncryptionProcessor() {
        String namespace = getResponseEncryptionNamespace();
        String fun = getResponseEncryptionFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultEncryptionFunction(namespace, fun);
    }
}
