package pro.shushi.pamirs.eip.api.model.connector;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.common.CodeModel;

/**
 * ConnDbType
 *
 * @author yakir on 2025/06/16 17:48.
 */
@Model(displayName = "数据库类型", labelFields = "displayName")
@Model.model(ConnDbType.MODEL_MODEL)
public class ConnDbType extends CodeModel {

    private static final long serialVersionUID = 5928268510636212081L;

    public final static String MODEL_MODEL = "designer.ConnDbType";

    @Field(displayName = "显示名称")
    @Field.String
    private String displayName;

    @Field(displayName = "帮助")
    @Field.String
    private String help;

    @Field(displayName = "驱动")
    @Field.String
    private String driver;
}
