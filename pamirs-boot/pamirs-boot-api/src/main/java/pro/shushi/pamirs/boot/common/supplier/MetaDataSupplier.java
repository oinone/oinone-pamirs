package pro.shushi.pamirs.boot.common.supplier;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wx@shushi.pro
 * @date 2023/12/12
 */
public class MetaDataSupplier {

    private static Set<String/*Model*/> diffModel = new HashSet<>();

    public static void addDiffModel(String model) {
        diffModel.add(model);
    }

    public static Set<String/*Model*/> getDiffModel() {
        return diffModel;
    }

    public static void clear() {
        diffModel.clear();
        diffModel = null;
    }

}
