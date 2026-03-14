package pro.shushi.pamirs.locale.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.shushi.pamirs.locale.PamirsLocaleTestApplication;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Adamancy Zhang at 16:10 on 2026-03-23
 */
@SpringBootTest(classes = PamirsLocaleTestApplication.class)
public class I18nUtilsTest {

    @BeforeEach
    public void setUp() {
        // Set default locale to US for testing
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    public void testTranslateModel() {
        String result = I18nUtils.translateModel("base", "base.Model", "displayName", "Default");
        assertEquals("Model", result);

        String summary = I18nUtils.translateModel("base", "base.Model", "summary", "Default");
        assertEquals("Model", summary);

        String remark = I18nUtils.translateModel("base", "base.Model", "remark", "Default");
        assertEquals("Model", remark);
    }

    @Test
    public void testTranslateFunction() {
        String result = I18nUtils.translateFunction("base", "base.Function", "construct", "displayName", "Default");
        assertEquals("Initialize data", result);
    }
}
