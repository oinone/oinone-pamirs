package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

@Model.model(ResourceConfigTranslatable.MODEL_MODEL)
@Model(displayName = "配置翻译表")
public class ResourceConfigTranslatable extends IdModel {

    public static final String MODEL_MODEL = "resource.ResourceConfigTranslatable";

    @Field.String
    @Field(displayName = "作用域")
    private String namespace;

    @Field.String
    @Field(unique = true, displayName = "键")
    private String key;

    @Field.Text
    @Field(translate = true, displayName = "值")
    private String value;

}
