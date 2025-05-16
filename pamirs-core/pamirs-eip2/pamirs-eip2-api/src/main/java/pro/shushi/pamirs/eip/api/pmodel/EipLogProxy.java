package pro.shushi.pamirs.eip.api.pmodel;

import pro.shushi.pamirs.eip.api.model.AbstractSingleInterface;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.EipLog;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;


@Base
@Model.model(EipLogProxy.MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.PROXY)
@Model(displayName = "接口日志代理模型", summary = "应用代理模型")
public class EipLogProxy extends EipLog {
    private static final long serialVersionUID = -5511176524761177752L;
    public static final String MODEL_MODEL = "pamirs.eip.proxy.EipLogProxy";

    private AbstractSingleInterface eipInterface;

    @Base
    @Field.String
    @Field(displayName = "接口名称")
    private String interfaceDisplayName;

    @Base
    @Field.String
    @Field(displayName = "接口描述")
    private String interfaceDescription;

    @Base
    @Field.String
    @Field(displayName = "接口路由")
    private String interfaceUri;

    @Base
    @Field.many2one
    @Field(displayName = "模块")
    private ModuleDefinition interfaceModuleDefinition;

    @Base
    @Field.many2one
    @Field(displayName = "业务分组")
    private EipConnGroup interfaceConnGroup;

    @Base
    @Field.Integer
    @Field(displayName = "调用时长")
    private Long invokeMillisecond;
}
