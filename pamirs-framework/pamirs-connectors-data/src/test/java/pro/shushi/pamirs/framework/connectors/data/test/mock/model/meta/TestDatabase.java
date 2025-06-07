package pro.shushi.pamirs.framework.connectors.data.test.mock.model.meta;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;


/**
 * 库结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = "PAMIRS_DATABASES")
@Model.Persistence(capitalMode = true)
@Model.model(TestDatabase.MODEL_MODEL)
@Model
public class TestDatabase extends TransientModel {

    public final static String MODEL_MODEL = "system.Database";
    private static final long serialVersionUID = 1574479081412977202L;

    @Field(unique = true)
    private String schemaName;

    @Field
    private String defaultCharacterSetName;

    @Field
    private String defaultCollationName;

}