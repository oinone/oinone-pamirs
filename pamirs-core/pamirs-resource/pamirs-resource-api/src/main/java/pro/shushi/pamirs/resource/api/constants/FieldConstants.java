package pro.shushi.pamirs.resource.api.constants;

import java.util.HashSet;
import java.util.Set;

public interface FieldConstants {

    String _dFieldName = "_d";

    String _FieldSetName = "_fieldSet";

    String _ctxMap = "_ctxMap";

    Set<String> excludeModelFields = new HashSet<String>() {
        {
            add(_dFieldName);
            add(_FieldSetName);
            add(_ctxMap);
        }
    };

    String countField = "count";

    String jsonFieldPostFix = "json";
}
