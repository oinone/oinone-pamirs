package pro.shushi.pamirs.core.common.tmodel;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * MetaBizData
 *
 * @author yakir on 2024/06/21 16:41.
 */
@Model(displayName = "元数据导入导出业务数据载体")
@Model.model(MetaBizData.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class MetaBizData extends TransientModel {

    private static final long serialVersionUID = 160327679152029083L;

    public final static String MODEL_MODEL = "core.common.MetaBizData";

    @Field(displayName = "业务数据模型")
    @Field.String
    private String model;

    @Field(displayName = "唯一标记")
    @Field.String
    private String uniqueKey;

    private String data;
}
