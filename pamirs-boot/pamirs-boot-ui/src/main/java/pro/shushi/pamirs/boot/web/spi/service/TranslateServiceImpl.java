package pro.shushi.pamirs.boot.web.spi.service;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Adamancy Zhang
 * @date 2021-01-11 11:59
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@SPI.Service
public class TranslateServiceImpl implements TranslateService {

    @Override
    public String getDefaultLang() {
        return I18nUtils.getDefaultLocale().toLanguageTag();
    }

    @Override
    public String getCurrentLang() {
        return PamirsSession.getLang();
    }

    @Override
    public String getCurrentLangIsoCode() {
        return PamirsSession.getLang();
    }

    @Override
    public <T> void simpleTranslate(String lang, List<T> list, Function<T, String> getter, BiConsumer<T, String> setter, String... models) {
        //do nothing
    }

    @Override
    public void generalDataTranslate(String lang, List<?> list, String model) {
        //do nothing
    }
}
