package pro.shushi.pamirs.user.api.model.tmodel;

import pro.shushi.pamirs.boot.base.model.Action;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.List;

/**
 * @author shier
 * date 2020/4/13
 */
@Base
@Model(displayName = "顶部栏的用户模块")
@Model.Advanced(name = "topBarUserBlock")
@Model.model(TopBarUserBlock.MODEL_MODEL)
public class TopBarUserBlock extends TransientModel {

    public static final String MODEL_MODEL = "user.TopBarUserBlock";

    @Field.many2one
    @Field(displayName = "用户")
    private PamirsUser pamirsUser;

    @Field.many2one
    @Field(displayName = "用户头像按钮")
    private Action userAvatarAction;

    @Field.one2many
    @Field(displayName = "按钮组列表")
    private List<TopBarActionGroup> actionGroups;
}
