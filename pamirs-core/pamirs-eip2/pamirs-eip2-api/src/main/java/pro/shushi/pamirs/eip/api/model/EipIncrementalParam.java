package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.eip.api.IEipIncrementalParam;
import pro.shushi.pamirs.eip.api.enmu.ContextTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 增量参数
 *
 * @author Adamancy Zhang at 09:01 on 2021-02-02
 */
@Base
@Model.model(EipIncrementalParam.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName,tags,inParam"})
@Model(displayName = "增量参数")
public class EipIncrementalParam extends IdModel implements IEipIncrementalParam {

    private static final long serialVersionUID = 7096000045901307595L;

    public static final String MODEL_MODEL = "pamirs.eip.EipIncrementalParam";

    public EipIncrementalParam() {
    }

    public EipIncrementalParam(String inParam, String outParam) {
        this.setInParam(inParam);
        this.setOutParam(outParam);
    }

    @Field.many2one
    @Field.Relation(relationFields = {"interfaceName"}, referenceFields = {"interfaceName"})
    @Field(displayName = "集成接口")
    private EipIntegrationInterface integrationInterface;

    @Field.String
    @Field(displayName = "接口名称", required = true)
    private String interfaceName;

    @Field.String
    @Field(displayName = "增量标签", invisible = true, summary = "用于标记该增量参数属于哪个调用链路，设置无效")
    private String tags;

    @Field.String
    @Field(displayName = "入参", required = true)
    private String inParam;

    @Field.String
    @Field(displayName = "出参", required = true)
    private String outParam;

    @Field.String
    @Field(displayName = "源上下文类型", defaultValue = "INTERFACE", required = true)
    private ContextTypeEnum originContextType;

    @Field.String
    @Field(displayName = "目标上下文类型", defaultValue = "INTERFACE", required = true)
    private ContextTypeEnum targetContextType;

    @Field.String
    @Field(displayName = "初始值", required = true)
    private String initializationValue;

    @Field.String
    @Field(displayName = "当前值")
    private String currentValue;

    @Override
    public void setCurrentValue(Object value) {
        this._d.put("currentValue", value);
    }

    @Override
    public void setTags(String tags) {
        this._d.put("tags", tags);
    }
}
