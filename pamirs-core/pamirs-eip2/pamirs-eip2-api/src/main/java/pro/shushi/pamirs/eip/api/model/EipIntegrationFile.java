package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * EipIntegrationFile
 *
 * @author yakir on 2024/10/30 20:12.
 */
@Model.model(EipIntegrationFile.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName"})
@Model(displayName = "集成文件", labelFields = "name")
public class EipIntegrationFile extends IdModel {

    private static final long serialVersionUID = -5846048953053422401L;

    public static final String MODEL_MODEL = "pamirs.eip.EipIntegrationFile";

    @Field(displayName = "名称")
    @Field.String
    private String name;

    @Field.String
    @Field(displayName = "接口技术名称", required = true)
    private String interfaceName;

    @Field.String(size = 2048)
    @Field(displayName = "文件地址", required = true)
    private String url;

    @Field.Enum
    @Field(displayName = "数据状态", defaultValue = "ENABLED", required = true)
    private DataStatusEnum dataStatus;

    @Field.Text
    @Field(displayName = "接口描述", required = true)
    private String description;

}
