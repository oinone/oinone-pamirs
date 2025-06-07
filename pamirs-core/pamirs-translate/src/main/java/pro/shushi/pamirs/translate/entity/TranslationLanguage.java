package pro.shushi.pamirs.translate.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 翻译语言
 *
 * @author Adamancy Zhang at 15:03 on 2024-06-22
 */
public class TranslationLanguage implements Serializable {

    private static final long serialVersionUID = -8158723169816273978L;

    private final String langCode;

    private final String isoCode;

    public TranslationLanguage(String langCode, String isoCode) {
        this.langCode = langCode;
        this.isoCode = isoCode;
    }

    public String getLangCode() {
        return langCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TranslationLanguage)) {
            return false;
        }
        TranslationLanguage language = (TranslationLanguage) o;
        return langCode.equals(language.langCode) &&
                isoCode.equals(language.isoCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(langCode, isoCode);
    }
}
