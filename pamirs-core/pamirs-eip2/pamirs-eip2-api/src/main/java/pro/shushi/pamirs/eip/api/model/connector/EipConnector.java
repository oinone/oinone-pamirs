package pro.shushi.pamirs.eip.api.model.connector;

import pro.shushi.pamirs.eip.api.enmu.connector.ConnAuthStatus;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnAuthType;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnCryptType;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnDBType;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.api.enmu.connector.TestConnStatus;
import pro.shushi.pamirs.eip.api.enmu.MetaOrigin;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import java.util.List;

/**
 * EipConnector
 *
 * @author yakir on 2023/03/29 14:56.
 */
@Base
@Model(displayName = "连接器", labelFields = "name")
@Model.model(EipConnector.MODEL_MODEL)
@Model.Advanced(unique = {"name"})
public class EipConnector extends IdModel {

    private static final long serialVersionUID = 2539314755671778755L;

    public final static String MODEL_MODEL = "designer.EipConnector";

    @Field(displayName = "应用名称")
    @Field.String
    private String name;

    @Field(displayName = "连接器类型")
    @Field.Enum
    private ConnType type;

    @Field(displayName = "业务域Code")
    @Field.String
    private String groupCode;

    @Field(displayName = "业务域")
    @Field.many2one
    @Field.Relation(relationFields = "groupCode", referenceFields = "code")
    private EipConnGroup group;

    @Field(displayName = "认证方式Id")
    @Field.Integer
    private Long connectorAuthId;

    @Field(displayName = "认证方式")
    @Field.many2one
    @Field.Relation(relationFields = "connectorAuthId", referenceFields = "id")
    private EipConnectorAuth connectorAuth;

    @Field(displayName = "服务器地址")
    @Field.String(size = 256)
    private String host;

    @Field(displayName = "认证方式")
    @Field.Enum
    private ConnAuthType authType;

    @Field(displayName = "认证状态", defaultValue = "UNAUTH")
    @Field.Enum
    private ConnAuthStatus authStatus;

    @Field(displayName = "自定义认证函数命名空间")
    @Field.String
    private String authNamespace;

    @Field(displayName = "自定义认证函数Fun")
    @Field.String
    private String authFun;

    @Field(displayName = "自定义认证函数")
    @Field.many2one
    @Field.Relation(relationFields = {"authNamespace", "authFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition authFunc;

    @Field(displayName = "加密方式")
    @Field.Enum
    private ConnCryptType connCryptType;

    @Field(displayName = "应用描述")
    @Field.Text
    private String desc;

    @Field.String(size = 512)
    @Field(displayName = "Logo")
    private String logo;

    /* 数据库/文件集连接器 */
    @Field(displayName = "数据库/文件类型")
    @Field.Enum
    private ConnDBType connDBType;

    @Field(displayName = "端口")
    @Field.Integer
    private Integer port;

    @Field(displayName = "扩展参数")
    @Field.String(size = 1024)
    private String extParam;

    @Field(displayName = "源数据库名称")
    @Field.String
    private String database;

    @Field(displayName = "SID", summary = "oracle数据库SID")
    @Field.String
    private String sid;

    @Field(displayName = "帐号")
    @Field.String
    private String user;

    @Field(displayName = "密码")
    @Field.String
    private String password;

    @Field(displayName = "IP白名单", multi = true, serialize = Field.serialize.COMMA)
    @Field.String(size = 1024)
    private List<String> ipWhites;

    @Field(displayName = "来源", defaultValue = "Custom")
    @Field.Enum
    private MetaOrigin origin;

    @Field(displayName = "测试连接状态")
    @Field.Enum
    private TestConnStatus testConnStatus;
}