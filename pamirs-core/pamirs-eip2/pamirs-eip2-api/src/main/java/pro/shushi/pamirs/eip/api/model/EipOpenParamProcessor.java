package pro.shushi.pamirs.eip.api.model;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipOpenParamProcessor;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;

/**
 * 参数处理器
 *
 * @author Adamancy Zhang at 19:23 on 2021-06-09
 */
@Base
@Model.model(EipOpenParamProcessor.MODEL_MODEL)
@Model(displayName = "开放接口参数处理器", labelFields = "name")
public class EipOpenParamProcessor extends AbstractEipParamConverterProcessor implements IEipOpenParamProcessor<SuperMap> {

    private static final long serialVersionUID = 2868839507876356928L;

    public static final String MODEL_MODEL = "pamirs.eip.EipOpenParamProcessor";

    @Override
    public IEipOpenParamProcessor<SuperMap> afterProperty() {
        return null;
    }
}
