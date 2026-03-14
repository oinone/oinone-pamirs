package pro.shushi.pamirs.locale.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.shushi.pamirs.file.api.FileModule;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.locale.configure.PamirsMessageSourceConfigure;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Adamancy Zhang at 16:10 on 2026-03-23
 */
@SpringBootTest(classes = {PamirsMessageSourceConfigure.class, I18nUtils.class})
public class I18nUtilsTest {

    @BeforeEach
    public void setUp() {
        // Set default locale to US for testing
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    public void testTranslateDataDictionaryItem() {
        String defaultFormat = I18nUtils.translateDataDictionaryItem(FileModule.MODULE_MODULE, ExcelValueTypeEnum.dictionary, ExcelValueTypeEnum.BOOLEAN.name(), "defaultFormat", "Default");
        assertEquals("{\"true\":\"Yes\",\"false\":\"No\"}", defaultFormat);
    }
}
