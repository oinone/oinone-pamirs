package pro.shushi.pamirs.locale.bundle;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Map Resource Bundle
 *
 * @author Adamancy Zhang at 15:37 on 2026-03-13
 */
public class MapResourceBundle extends ResourceBundle {

    private final Map<String, Object> lookup;

    public MapResourceBundle(Map<String, Object> lookup) {
        this.lookup = lookup;
    }

    @Override
    protected Object handleGetObject(String key) {
        return lookup.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(lookup.keySet());
    }
}
