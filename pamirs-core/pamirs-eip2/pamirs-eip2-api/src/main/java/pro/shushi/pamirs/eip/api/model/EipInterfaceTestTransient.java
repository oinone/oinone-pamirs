package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 接口测试
 *
 * @author Adamancy Zhang at 19:18 on 2021-06-09
 */
@Base
@Model.model(EipInterfaceTestTransient.MODEL_MODEL)
@Model(displayName = "接口测试")
public class EipInterfaceTestTransient extends TransientModel {

    private static final long serialVersionUID = -7522154607594787899L;

    public static final String MODEL_MODEL = "pamirs.eip.EipInterfaceTestTransient";

    @Field.Boolean
    @Field(displayName = "是否为开发测试", defaultValue = "false")
    private Boolean isDevelopment;

    @Field.many2one
    @Field(displayName = "集成接口")
    private EipIntegrationInterface integrationInterface;

    @Field.String
    @Field(displayName = "接口名称")
    private String interfaceName;

    @Field.String
    @Field(displayName = "提示信息")
    private String tip;

    @Field.Text
    @Field(displayName = "执行上下文", summary = "执行上下文采用JSON序列化方式")
    private String executeContextData;

    @Field.Text
    @Field(displayName = "请求参数示例")
    private String requestParams;

    @Field.Text
    @Field(displayName = "Body参数")
    private String requestData;

    @Field.Text
    @Field(displayName = "Query参数")
    private String requestQueryData;

    @Field.Text
    @Field(displayName = "Path参数")
    private String requestPathData;

    @Field.Text
    @Field(displayName = "Header参数")
    private String requestHeaderData;

    @Field.Text
    @Field(displayName = "真实请求数据")
    private String actualRequestData;

    @Field.Text
    @Field(displayName = "响应数据")
    private String responseData;
}
