package pro.shushi.pamirs.locale.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.shushi.pamirs.locale.PamirsLocaleTestApplication;

import java.util.Locale;

/**
 * @author Adamancy Zhang at 16:11 on 2026-05-07
 */
@SpringBootTest(classes = PamirsLocaleTestApplication.class)
public class I18nMessageTest {

    @BeforeEach
    public void setUp() {
        // Set default locale to US for testing
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    public void test() {
        try {
            System.out.println(I18nUtils.getMessage("test1", "1", "2", "3"));
            assert false;
        } catch (Exception e) {
            e.printStackTrace();
            assert true;
        }
        System.out.println(I18nUtils.getMessage("test2", "1", "2", "3"));
    }
}
