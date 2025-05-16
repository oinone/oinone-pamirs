package pro.shushi.pamirs.translate.pojo;

import pro.shushi.pamirs.resource.api.model.AbstractResourceTranslationItem;
import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.resource.api.model.ResourceTranslationItem;

import java.io.Serializable;
import java.util.Objects;

/**
 * TranslatePojo
 *
 * @author yakir on 2023/09/27 10:20.
 */
public class TranslatePojo implements Serializable {

    private static final long serialVersionUID = -7868615994016320970L;

    private String module;

    private String model;

    private String originLang;

    private String lang;

    private String origin;

    private String target;

    private String translateFor;

    private String scope;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getModule() {
        return module;
    }

    public TranslatePojo setModule(String module) {
        this.module = module;
        return this;
    }

    public String getModel() {
        return model;
    }

    public TranslatePojo setModel(String model) {
        this.model = model;
        return this;
    }

    public String getOriginLang() {
        return originLang;
    }

    public TranslatePojo setOriginLang(String originLang) {
        this.originLang = originLang;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TranslatePojo setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getOrigin() {
        return origin;
    }

    public TranslatePojo setOrigin(String origin) {
        this.origin = origin;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public TranslatePojo setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getTranslateFor() {
        return translateFor;
    }

    public TranslatePojo setTranslateFor(String translateFor) {
        this.translateFor = translateFor;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueKey());
    }

    public static String originUnique(ResourceTranslationItem item) {
        return item.getModule() + "#" + item.getModel() + "#" + item.getResLangCode() + "#" + item.getOrigin();
    }

    public static TranslatePojo of(AbstractResourceTranslationItem item) {
        TranslatePojo pojo = new TranslatePojo();
        pojo.setOriginLang(item.getResLangCode());
        pojo.setLang(item.getLangCode());
        pojo.setOrigin(item.getOriginCode());
        pojo.setModel(item.getModel());
        pojo.setTarget(item.getTarget());
        pojo.setModule(item.getModule());
        pojo.setScope(item.getScope().getValue());
        return pojo;
    }

    public ResourceTranslation toTranslation() {
        ResourceTranslation translation = new ResourceTranslation();
        translation.setModule(this.module);
        translation.setLangCode(this.lang);
        translation.setResLangCode(this.originLang);
        translation.setState(Boolean.TRUE);
        return translation;
    }

    public String uniqueKey() {
        return String.join(",", module, origin, originLang, lang, scope);
    }

    public static String uniqueKey(String module, String originLang, String lang, String model, String origin) {
        String itemKey = itemKey(module, originLang, lang);
        String itemHashKey = itemHashKey(model, origin);
        return itemKey + "$" + itemHashKey;
    }

    public String uniqueKeyTranslation() {
        return String.join(",", this.module, this.originLang, this.lang);
    }

    public String itemKey() {
        return "translationItem:" + this.module + ":" + this.originLang + "#" + this.lang;
    }

    public static String itemKey(String module, String originLang, String lang) {
        return "translationItem:" + module + ":" + originLang + "#" + lang;
    }

    public String itemHashKey() {
        return this.model + "#" + this.origin;
    }

    public static String itemHashKey(String model, String origin) {
        return model + "#" + origin;
    }

    @Override
    public String toString() {
        return "TranslatePojo{" +
                "module='" + module + '\'' +
                ", model='" + model + '\'' +
                ", originLang='" + originLang + '\'' +
                ", lang='" + lang + '\'' +
                ", origin='" + origin + '\'' +
                ", target='" + target + '\'' +
                ", translateFor='" + translateFor + '\'' +
                '}';
    }
}
