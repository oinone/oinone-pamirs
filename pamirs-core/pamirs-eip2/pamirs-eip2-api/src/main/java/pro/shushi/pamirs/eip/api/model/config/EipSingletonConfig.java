package pro.shushi.pamirs.eip.api.model.config;

import pro.shushi.pamirs.eip.api.IEipSingletonConfig;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

@Model.model(EipSingletonConfig.MODEL_MODEL)
@Model(displayName = "Eip单配置存储基类", summary = "Eip单配置存储基类")
public class EipSingletonConfig extends IdModel implements IEipSingletonConfig<EipSingletonConfig> {

    public static final String MODEL_MODEL = "pamirs.eip.EipSingletonConfig";

    @Field.String
    @Field(displayName = "schema")
    private String schema;

    @Function(openLevel = FunctionOpenEnum.API, summary = "Libra开放对接信息")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public EipSingletonConfig construct(EipSingletonConfig config) {
        EipSingletonConfig config1 = config.singletonModel();
        if (config1 != null) {
            return config1;
        }
        return config.construct();
    }

}
