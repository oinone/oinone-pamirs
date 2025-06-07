package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * @author yeshenyue on 2024/9/27 11:36.
 */
@Model.Advanced(unique = {"interfaceName,interfaceType"})
@Model.model(EipLogStrategy.MODEL_MODEL)
@Model(displayName = "日志记录策略")
public class EipLogStrategy extends IdModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipLogStrategy";
    private static final long serialVersionUID = -57831942997738044L;

    @Field.String
    @Field(displayName = "接口技术名称", required = true)
    private String interfaceName;

    @Field.Enum
    @Field(displayName = "接口类型", required = true)
    private InterfaceTypeEnum interfaceType;

    @Field.Boolean
    @Field(displayName = "是否日志频率限制", required = true, defaultValue = "true")
    private Boolean isIgnoreFrequency;
}