package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

/**
 * @author shier
 * date 2020/4/13
 */
@Base
@Model(displayName = "顶部栏的按钮组")
@Model.Advanced(name = "TopBarActionGroup")
@Model.model(TopBarActionGroup.MODEL_MODEL)
public class TopBarActionGroup extends TransientModel {

    public static final String MODEL_MODEL = "user.TopBarActionGroup";

    @Field.one2many
    @Field(displayName = "按钮列表")
    List<Action> actions;

    private Integer priority;
}
