package pro.shushi.pamirs.locale.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import pro.shushi.pamirs.locale.PamirsLocaleTestApplication;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PamirsLocaleTestApplication.class)
public class I18nUtilsTest {

    @BeforeEach
    public void setUp() {
        // Set default locale to US for testing
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    public void testTranslateModel() {
        String result = I18nUtils.translateModel("testModule", "base.Model", "displayName", "Default");
        assertEquals("Model", result);

        String summary = I18nUtils.translateModel("testModule", "base.Model", "summary", "Default");
        assertEquals("Model", summary);
    }

    @Test
    public void testTranslateModelFull() {
        String result = I18nUtils.translateModel("testModule", "FullModel", "displayName", "Default");
        assertEquals("Full Model Name", result);

        String summary = I18nUtils.translateModel("testModule", "FullModel", "summary", "Default");
        assertEquals("Full Model Summary", summary);

        String field = I18nUtils.translateField("testModule", "FullModel", "fullField", "displayName", "Default");
        assertEquals("Full Field Name", field);
    }

    @Test
    public void testTranslateModelShorthand() {
        // Source: model.json (Shorthand)
        String result = I18nUtils.translateModel("testModule", "ShortModel", "displayName", "Default");
        assertEquals("Short Model Name", result);

        String field = I18nUtils.translateField("testModule", "ShortModel", "shortField", "displayName", "Default");
        assertEquals("Short Field Name", field);
    }

    @Test
    public void testTranslateFunctionFull() {
        String result = I18nUtils.translateFunction("testModule", "TestNamespace", "FullFunction", "displayName", "Default");
        assertEquals("Full Function Name", result);
    }

    @Test
    public void testTranslateFunctionShorthand() {
        String result = I18nUtils.translateFunction("testModule", "TestNamespace", "ShortFunction", "displayName", "Default");
        assertEquals("Short Function Name", result);
    }

    @Test
    public void testTranslateServerActionFull() {
        String result = I18nUtils.translateServerAction("testModule", "TestModel", "FullAction", "displayName", "Default");
        assertEquals("Full Action Name", result);
    }

    @Test
    public void testTranslateServerActionShorthand() {
        String result = I18nUtils.translateServerAction("testModule", "TestModel", "ShortAction", "displayName", "Default");
        assertEquals("Short Action Name", result);
    }

    @Test
    public void testTranslateViewActionFull() {
        String result = I18nUtils.translateViewAction("testModule", "TestModel", "FullViewAction", "displayName", "Default");
        assertEquals("Full ViewAction Name", result);
    }

    @Test
    public void testTranslateViewActionShorthand() {
        String result = I18nUtils.translateViewAction("testModule", "TestModel", "ShortViewAction", "displayName", "Default");
        assertEquals("Short ViewAction Name", result);
    }

    @Test
    public void testTranslateClientActionFull() {
        String result = I18nUtils.translateClientAction("testModule", "TestModel", "FullClientAction", "displayName", "Default");
        assertEquals("Full ClientAction Name", result);
    }

    @Test
    public void testTranslateClientActionShorthand() {
        String result = I18nUtils.translateClientAction("testModule", "TestModel", "ShortClientAction", "displayName", "Default");
        assertEquals("Short ClientAction Name", result);
    }

    @Test
    public void testTranslateUrlActionFull() {
        String result = I18nUtils.translateUrlAction("testModule", "TestModel", "FullUrlAction", "displayName", "Default");
        assertEquals("Full UrlAction Name", result);
    }

    @Test
    public void testTranslateUrlActionShorthand() {
        String result = I18nUtils.translateUrlAction("testModule", "TestModel", "ShortUrlAction", "displayName", "Default");
        assertEquals("Short UrlAction Name", result);
    }

    @Test
    public void testTranslateDataDictionaryFull() {
        String result = I18nUtils.translateDataDictionary("testModule", "FullDict", "displayName", "Default");
        assertEquals("Full Dict Name", result);

        String item = I18nUtils.translateDataDictionaryItem("testModule", "FullDict", "ITEM1", "displayName", "Default");
        assertEquals("Full Item 1", item);
    }

    @Test
    public void testTranslateDataDictionaryShorthand() {
        String result = I18nUtils.translateDataDictionary("testModule", "ShortDict", "displayName", "Default");
        assertEquals("Short Dict Name", result);

        String item = I18nUtils.translateDataDictionaryItem("testModule", "ShortDict", "ITEM2", "displayName", "Default");
        assertEquals("Short Item 2", item);
    }

    @Test
    public void testTranslateErrorDefinitionFull() {
        String result = I18nUtils.translateErrorDefinition("com.example.FullError", "displayName", "Default");
        assertEquals("Full Error Name", result);

        String msg = I18nUtils.translateErrorDefinitionItem("com.example.FullError", "ERR_001", "msg", "Default");
        assertEquals("Full Error Msg 001", msg);
    }

    @Test
    public void testTranslateErrorDefinitionShorthand() {
        // Source: error.json (Shorthand)
        String result = I18nUtils.translateErrorDefinition("com.example.ShortError", "displayName", "Default");
        assertEquals("Short Error Name", result);

        String msg = I18nUtils.translateErrorDefinitionItem("com.example.ShortError", "ERR_002", "msg", "Default");
        assertEquals("Short Error Msg 002", msg);
    }

    @Test
    public void testTranslateModule() {
        String result = I18nUtils.translateModule("testModule", "displayName", "Default");
        assertEquals("Module Display Name", result);
    }

    @Test
    public void testTranslateMenuFull() {
        String result = I18nUtils.translateMenu("testModule", "FullMenu", "label", "Default");
        assertEquals("Full Menu Label", result);
    }

    @Test
    public void testTranslateMenuShorthand() {
        String result = I18nUtils.translateMenu("testModule", "ShortMenu", "displayName", "Default");
        assertEquals("Short Menu Label", result);
    }

    @Test
    public void testTranslateTriggerFull() {
        String result = I18nUtils.translateTrigger("testModule", "TestNamespace", "FullTrigger", "displayName", "Default");
        assertEquals("Full Trigger Name", result);
    }

    @Test
    public void testTranslateTriggerShorthand() {
        String result = I18nUtils.translateTrigger("testModule", "TestNamespace", "ShortTrigger", "displayName", "Default");
        assertEquals("Short Trigger Name", result);
    }

    @Test
    public void testTranslateScheduleFull() {
        String result = I18nUtils.translateSchedule("testModule", "TestNamespace", "FullSchedule", "displayName", "Default");
        assertEquals("Full Schedule Name", result);
    }

    @Test
    public void testTranslateScheduleShorthand() {
        String result = I18nUtils.translateSchedule("testModule", "TestNamespace", "ShortSchedule", "displayName", "Default");
        assertEquals("Short Schedule Name", result);
    }

    @Test
    public void testTranslateHookFull() {
//        String result = I18nUtils.translateHook("testModule", "FullHook", "displayName", "Default");
//        assertEquals("Full Hook Name", result);
    }

    @Test
    public void testTranslateHookShorthand() {
//        String result = I18nUtils.translateHook("testModule", "ShortHook", "displayName", "Default");
//        assertEquals("Short Hook Name", result);
    }

    @Test
    public void testTranslateExtPointFull() {
        String result = I18nUtils.translateExtPoint("testModule", "TestNamespace1", "FullExtPoint", "displayName", "Default");
        assertEquals("Full ExtPoint Name", result);
    }

    @Test
    public void testTranslateExtPointShorthand() {
        String result = I18nUtils.translateExtPoint("testModule", "TestNamespace2", "ShortExtPoint", "displayName", "Default");
        assertEquals("Short ExtPoint Name", result);
    }

    // Existing legacy tests...
    @Test
    public void testTranslateErrorDefinitionSpecialCase() {
        // Source: error.json (Shorthand, but Special Case for ErrorDefinition)
        // Key: base.ErrorDefinition#com.example.Error#msg (NO MODULE PREFIX)
        String result = I18nUtils.translateErrorDefinition("com.example.FullError", "displayName", "Default");
        assertEquals("Full Error Name", result);
    }

    @Test
    public void testTranslateServerActionFullKey() {
        // Source: full.json (Full Key)
        // Key: testModule#base.ServerAction#TestModel#MyAction#displayName
        String result = I18nUtils.translateServerAction("testModule", "TestModel", "MyAction", "displayName", "Default");
        assertEquals("Action Display Name", result);
    }

    @Test
    public void testTranslateMenuZhCN() {
        // Source: menu.json (Shorthand)
        // Key: testModule#base.Menu#MyMenu#label
        LocaleContextHolder.setLocale(Locale.CHINA);
        String result = I18nUtils.translateMenu("testModule", "MyMenu", "label", "Default");
        assertEquals("菜单标签", result);
    }

    @Test
    public void testFallbackToDefault() {
        // Key not found
        String result = I18nUtils.translateModel("testModule", "NonExistentModel", "displayName", "Default Value");
        assertEquals("Default Value", result);
    }

    @Test
    public void testLocaleSwitching() {
        // 1. Initial State (US)
        LocaleContextHolder.setLocale(Locale.US);
        String resultUS = I18nUtils.translateModel("testModule", "MyModel", "displayName", "Default");
        assertEquals("My Model Display Name", resultUS);

        // 2. Switch to Language-Only Locale (French)
        LocaleContextHolder.setLocale(Locale.FRENCH); // "fr"
        String resultFR = I18nUtils.translateModel("testModule", "MyModel", "displayName", "Default");
        assertEquals("Modèle d'affichage", resultFR);

        // 3. Switch back to US
        LocaleContextHolder.setLocale(Locale.US);
        String resultBackToUS = I18nUtils.translateModel("testModule", "MyModel", "displayName", "Default");
        assertEquals("My Model Display Name", resultBackToUS);
    }

    @Test
    public void testLanguageOnlyLocale() {
        // Set locale to "fr" (no country code)
        LocaleContextHolder.setLocale(new Locale("fr"));

        // Should load from .../fr/model.json
        String result = I18nUtils.translateModel("testModule", "MyModel", "displayName", "Default");
        assertEquals("Modèle d'affichage", result);
    }

    @Test
    public void testTranslateView() {
        String result = I18nUtils.translateView("TestModel", "TestView", "title", "Default");
        assertEquals("Test View Title", result);

        String resultShort = I18nUtils.translateView("TestModel", "ShortView", "title", "Default");
        assertEquals("Short View Title", resultShort);
    }

    @Test
    public void testTranslateMask() {
        String result = I18nUtils.translateMask("TestMask", "displayName", "Default");
        assertEquals("Test Mask Name", result);

        String resultShort = I18nUtils.translateMask("ShortMask", "title", "Default");
        assertEquals("Short Mask Name", resultShort);
    }

    @Test
    public void testTranslateLayout() {
        String result = I18nUtils.translateLayout("TestLayout", "displayName", "Default");
        assertEquals("Test Layout Name", result);

        String resultShort = I18nUtils.translateLayout("ShortLayout", "title", "Default");
        assertEquals("Short Layout Name", resultShort);
    }

    @Test
    public void testTranslateViewTemplateField() {
        String result = I18nUtils.translateFieldInViewTemplate(
                "auth.AuthGroupDataPermissionProxy",
                "AuthGroupDataPermissionTable",
                "displayName",
                "label",
                "Default");
        assertEquals("Name", result);

        String result2 = I18nUtils.translateFieldInViewTemplate(
                "auth.AuthGroupDataPermissionProxy",
                "AuthGroupDataPermissionTable",
                "comment",
                "label",
                "Default");
        assertEquals("Description", result2);

        String result3 = I18nUtils.translateFieldInViewTemplate(
                "auth.AuthGroupDataPermissionProxy",
                "AuthGroupDataPermissionTable",
                "moduleDefinition.displayName",
                "label",
                "Default");
        assertEquals("Resource Module - Display Name", result3);
    }
}
