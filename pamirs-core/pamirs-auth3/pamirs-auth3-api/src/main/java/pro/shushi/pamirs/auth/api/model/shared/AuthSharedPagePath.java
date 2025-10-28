package pro.shushi.pamirs.auth.api.model.shared;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * 分享页面资源路径
 *
 * @author Adamancy Zhang at 09:52 on 2025-10-28
 */
@Base
@Model.model(AuthSharedPagePath.MODEL_MODEL)
@Model(displayName = "分享页面资源路径")
public class AuthSharedPagePath extends IdModel {

    private static final long serialVersionUID = 3998806876291969918L;

    public static final String MODEL_MODEL = "auth.AuthSharedPagePath";

    @Field.String(size = 64)
    @Field(displayName = "分享码")
    private String sharedCode;

    @Field.Text
    @Field(displayName = "资源路径")
    private String path;
}
