package pro.shushi.pamirs.user.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model
@Model.model("resource.ResourceUserLog")
@Model.Advanced(name = "resourceUserLog")
public class ResourceUserLog extends IdModel {

    @Field.many2one
    @Field(summary = "用户")
    private PamirsUser user;

}
