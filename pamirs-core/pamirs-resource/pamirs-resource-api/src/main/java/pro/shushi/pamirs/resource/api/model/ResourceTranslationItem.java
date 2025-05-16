package pro.shushi.pamirs.resource.api.model;

import pro.shushi.pamirs.meta.annotation.Model;

/**
 * ResourceTranslationItem
 *
 * @author yakir on 2019/06/27 17:17
 */
@Model.model(ResourceTranslationItem.MODEL_MODEL)
@Model.Advanced(unique = {"module,originCode,resLangCode,langCode,scope"})
@Model(displayName = "源术语翻译资源项", labelFields = "origin")
public class ResourceTranslationItem extends AbstractResourceTranslationItem {

    private static final long serialVersionUID = -8667024820426241422L;

    public static final String MODEL_MODEL = "resource.ResourceTranslationItem";

}
