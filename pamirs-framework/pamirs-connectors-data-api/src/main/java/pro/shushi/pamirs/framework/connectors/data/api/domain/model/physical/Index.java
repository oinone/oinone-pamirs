package pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 索引
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, priority = 53)
@Model.model(Index.MODEL_MODEL)
@Model("索引定义")
public class Index extends TransientModel {

    public final static String MODEL_MODEL = "system.Index";
    private static final long serialVersionUID = 7277034835610018249L;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String indexName;

    @Field
    private String columnName;

    @Field
    private Integer seqInIndex;

    @Field
    private Boolean unique;

    @Field
    private Boolean pk;

}
