package pro.shushi.pamirs.eip.api.model.connector;

import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.MetaOrigin;
import pro.shushi.pamirs.eip.api.enmu.connector.*;
import pro.shushi.pamirs.eip.api.tmodel.EipExcelTypeTransform;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * EipConnectorResource
 *
 * @author yakir on 2023/03/29 14:56.
 */
@Base
@Model(displayName = "连接器资源")
@Model.model(EipConnectorResource.MODEL_MODEL)
@Model.Advanced(unique = {"connectorId,name"}, index = {"interfaceName"})
public class EipConnectorResource extends IdModel {

    private static final long serialVersionUID = 6627865818048431970L;

    public final static String MODEL_MODEL = "designer.EipConnectorResource";

    @Field(displayName = "连接器ID")
    @Field.Integer
    private Long connectorId;

    @Field(displayName = "应用/DB")
    @Field.many2one
    @Field.Relation(relationFields = "connectorId", referenceFields = "id")
    private EipConnector connector;

    @Field(displayName = "连接器类型", store = NullableBoolEnum.FALSE)
    @Field.Enum
    @Field.Related(related = {"connector", "type"})
    private ConnType connType;

    @Field(displayName = "技术名称")
    @Field.String
    private String interfaceName;

    @Field(displayName = "API名称")
    @Field.String
    private String name;

    @Field(displayName = "API类型", summary = "Restful, WebService")
    @Field.Enum
    private ConnApiType type;

    @Field(displayName = "连接器API协议")
    @Field.Enum
    private ApiSchema schema;

    @Field(displayName = "连接器API请求方式")
    @Field.Enum
    private ApiMethod method;

    @Field(displayName = "请求路径")
    @Field.String(size = 256)
    private String path;

    @Field(displayName = "启用状态", defaultValue = "ENABLED")
    @Field.Enum
    private DataStatusEnum dataStatus;

    @Field(displayName = "Api类别", defaultValue = "Custom", summary = "来源")
    @Field.Enum
    private MetaOrigin origin;

    @Field(displayName = "输入输出转换器")
    @Field.many2one
    @Field.Relation(relationFields = {"inOutConverterNamespace", "inOutConverterFun"}, referenceFields = {"namespace", "fun"})
    private FunctionDefinition inOutConverterFunDefine;

    @Field(displayName = "输入输出转换器Namespace")
    @Field.String
    private String inOutConverterNamespace;

    @Field(displayName = "输入输出转换器Fun")
    @Field.String
    private String inOutConverterFun;

    @Field(displayName = "Api描述")
    @Field.String(size = 256)
    private String desc;

    @Field(displayName = "Query参数Json")
    @Field.Text
    private String queryParamsJson;

    @Field(displayName = "Path参数Json")
    @Field.Text
    private String pathParamsJson;

    @Field(displayName = "Header参数Json")
    @Field.Text
    @Field.Advanced(columnDefinition = "MEDIUMTEXT")
    private String headerParamsJson;

    @Field(displayName = "Content Type")
    @Field.Enum
    private ApiContentType contentType;

    @Field(displayName = "JSON格式类型")
    @Field.Enum
    private ApiJsonType jsonType;

    @Field(displayName = "命名空间")
    @Field.String
    private String declaration;

    @Field(displayName = "Body参数Json")
    @Field.Text
    @Field.Advanced(columnDefinition = "MEDIUMTEXT")
    private String bodyParamsJson;

    @Field(displayName = "响应状态码", summary = "多个使用,分隔")
    @Field.String
    private String statusCode;

    @Field(displayName = "响应Json")
    @Field.Text
    @Field.Advanced(columnDefinition = "MEDIUMTEXT")
    private String responseJson;

    @Field.Boolean
    @Field(displayName = "是否为单个结果集")
    private Boolean isSingleResultSet;

    @Field(displayName = "sql", summary = "sql，当connector为db时才使用")
    @Field.String(size = 4096)
    private String sql;

    @Field(displayName = "是否授权Api")
    @Field.Boolean
    private Boolean isAuthApi;

    @Field(displayName = "数据行数", summary = "文件集类型")
    @Field.Integer
    private Long total;

    @Field(displayName = "预览")
    @Field.Text
    private String preview;

    /* 非存储 */
    @Field(displayName = "创建人", store = NullableBoolEnum.FALSE)
    @Field.many2one
    @Field.Relation(relationFields = "createUid", referenceFields = "id", store = false)
    private PamirsUser creator;

    @Field.Boolean
    @Field(displayName = "是否忽略日志记录频率限制", store = NullableBoolEnum.FALSE)
    private Boolean isIgnoreLogFrequency;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "元数据字段")
    private List<ModelField> fields;

    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "excel类型转换")
    private EipExcelTypeTransform excelTypeTransform;

    @Field.Text
    @Field(displayName = "数据", store = NullableBoolEnum.FALSE)
    private String dataList;

}