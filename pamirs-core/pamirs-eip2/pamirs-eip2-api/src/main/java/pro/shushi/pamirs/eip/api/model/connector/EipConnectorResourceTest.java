package pro.shushi.pamirs.eip.api.model.connector;

import pro.shushi.pamirs.eip.api.enmu.connector.ConnApiType;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * @author Adamancy Zhang at 20:27 on 2025-08-11
 */
@Model.model(EipConnectorResourceTest.MODEL_MODEL)
@Model(displayName = "连接器测试")
public class EipConnectorResourceTest extends TransientModel {

    private static final long serialVersionUID = -7311742636632155492L;

    public static final String MODEL_MODEL = "designer.EipConnectorResourceTest";

    @Field(displayName = "技术名称")
    private String interfaceName;

    @Field(displayName = "API名称")
    private String name;

    @Field(displayName = "API URL")
    private String path;

    @Field(displayName = "API类型")
    private ConnApiType type;
}
