package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.util.Date;

/**
 * Eip 接口调用日志
 *
 * @author Adamancy Zhang
 * @date 2020-11-05 13:33
 */
@Base
@Model.Advanced(index = {"interfaceName", "createDate"}, ordering = "createDate DESC, id DESC")
@Model.model(EipLog.MODEL_MODEL)
@Model(displayName = "接口调用日志")
public class EipLog extends IdModel {

    private static final long serialVersionUID = -8964274044520461374L;

    public static final String MODEL_MODEL = "pamirs.eip.EipLog";

    @Base
    @Field.PrimaryKey(keyGenerator = KeyGeneratorEnum.AUTO_INCREMENT)
    @Field.Integer
    @Field(displayName = "ID", summary = "ID字段，唯一自增索引", required = true, priority = 5)
    private Long id;

    @Base
    @Field.Enum
    @Field(displayName = "接口类型")
    private InterfaceTypeEnum interfaceType;

    @Base
    @Field.String
    @Field(displayName = "接口技术名称")
    private String interfaceName;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "请求头数据")
    private String requestHeaderData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "原始请求数据")
    private String requestOriginData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "真实请求数据")
    private String requestTargetData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "响应头数据")
    private String responseHeaderData;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "响应数据")
    private String responseData;

    @Base
    @Field.Boolean
    @Field(displayName = "是否成功调用")
    private Boolean isSuccess;

    @Base
    @Field.Text
    @Field.Advanced(columnDefinition = "LONGTEXT")
    @Field(displayName = "异常信息")
    private String errorMsg;

    @Base
    @Field.Date(fraction = 3)
    @Field(displayName = "调用时间")
    private Date invokeDate;

    @Base
    @Field.Date(fraction = 3)
    @Field(displayName = "调用完成时间")
    private Date invokeEndDate;

}
