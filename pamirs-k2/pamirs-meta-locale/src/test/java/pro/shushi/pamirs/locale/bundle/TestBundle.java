package pro.shushi.pamirs.locale.bundle;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import java.util.*;

public class TestBundle {
    public static void main(String[] args) throws Exception {
        MetadataResourceBundleHandler handler = new MetadataResourceBundleHandler();
        handler.scanResources(new PathMatchingResourcePatternResolver());
        ResourceBundle bundle = handler.loadBundle("pamirs/i18n", new Locale("en", "US"), "pamirs.metadata");
        System.out.println("Keys:");
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains("FullModel")) {
                System.out.println(key + " = " + bundle.getString(key));
            }
        }
    }
}
