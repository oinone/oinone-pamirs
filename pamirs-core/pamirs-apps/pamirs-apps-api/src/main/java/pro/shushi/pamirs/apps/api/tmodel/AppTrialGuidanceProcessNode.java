package pro.shushi.pamirs.apps.api.tmodel;

import pro.shushi.pamirs.auth.api.model.AuthRole;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;

/**
 * 内页 引导节点
 *
 * <p> 内页 引导节点 </p>
 *
 * @author syj@shushi.pro
 * @version 1.0.0
 * date 2021-04-28 21:22
 */
@Base
@Model.model(AppTrialGuidanceProcessNode.MODEL_MODEL)
@Model(displayName = "内页 引导节点")
public class AppTrialGuidanceProcessNode extends TransientModel {

    public static final String MODEL_MODEL = "app.AppTrialGuidanceProcessNode";

    @Field.String
    @Field(displayName = "节点名称", translate = true)
    private String nodeName;

    @Field.Integer
    @Field(displayName = "节点位置")
    private Integer nodeDepth;

    @Field.many2one
    @Field(displayName = "使用角色")
    private AuthRole role;

    @Field.String
    @Field(displayName = "跳转链接")
    private String nodeLink;

    @Field.String
    @Field(displayName = "访问token")
    private String accessToken;


}
