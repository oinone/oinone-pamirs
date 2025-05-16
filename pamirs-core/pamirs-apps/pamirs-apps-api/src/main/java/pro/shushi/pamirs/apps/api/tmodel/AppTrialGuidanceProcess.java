package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.apps.api.enmu.AppTrialProcessStateEnum;
import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * 内页 试用引导过程
 *
 * <p> 内页 试用引导过程 </p>
 *
 * @author syj@shushi.pro
 * @version 1.0.0
 * date 2021-04-28 21:22
 */
@Base
@Model.model(AppTrialGuidanceProcess.MODEL_MODEL)
@Model(displayName = "内页 试用引导过程")
public class AppTrialGuidanceProcess extends TransientModel {

    public static final String MODEL_MODEL = "app.AppTrialGuidanceProcess";

    @Field.String
    @Field(displayName = "过程名称")
    private String processName;

    @Field.Enum
    @Field(displayName = "状态")
    private AppTrialProcessStateEnum state;

    @Field.Boolean
    @Field(displayName = "是否默认展示")
    private Boolean isDefault;

    @Field.one2many
    @Field(displayName = "角色")
    private List<AuthRole> roles;

    @Field.one2many
    @Field(displayName = "流程节点")
    private List<AppTrialGuidanceProcessNode> nodeList;


}
