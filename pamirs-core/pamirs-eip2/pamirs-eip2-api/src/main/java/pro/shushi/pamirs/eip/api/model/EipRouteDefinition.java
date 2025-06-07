package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 路由定义
 *
 * @author Adamancy Zhang at 19:23 on 2021-06-09
 */
@Base
@Model.model(EipRouteDefinition.MODEL_MODEL)
@Model.Advanced(unique = {"interfaceName"})
@Model(displayName = "路由定义", labelFields = "name", summary = "所有接口定义在Camel上下文中的最终形式，不做接口定义，仅定义流程")
public class EipRouteDefinition extends AbstractEipApi implements IEipApi {

    private static final long serialVersionUID = 3192331352075578667L;

    public static final String MODEL_MODEL = "pamirs.eip.EipRouteDefinition";

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "组件定义列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    @Field.Advanced(columnDefinition = "text")
    private List<EipComponentDefinition> definitions;

    @JSONField(serialize = false)
    @Override
    public String getUri() {
        throw new UnsupportedOperationException("路由定义无URI");
    }
}
