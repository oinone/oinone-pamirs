package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;


/**
 * 库实例结构
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 49)
@Model.model(DatabaseInstance.MODEL_MODEL)
@Model("数据库实例")
public class DatabaseInstance extends TransientModel {

    public final static String MODEL_MODEL = "system.DatabaseInstance";
    private static final long serialVersionUID = -7084065212419355554L;

    @Field
    private String productName;

    @Field
    private String productVersion;

    @Field
    private String userName;

    @Field
    private String url;

    @Field
    private String driverName;

    @Field
    private String driverVersion;

    @Field
    private Integer driverMajorVersion;

    @Field
    private Integer driverMinorVersion;

    @Field
    private Integer jdbcMajorVersion;

    @Field
    private Integer jdbcMinorVersion;

    @Field
    private Boolean readOnly;

    @Field
    private Boolean supportsTransactions;

}