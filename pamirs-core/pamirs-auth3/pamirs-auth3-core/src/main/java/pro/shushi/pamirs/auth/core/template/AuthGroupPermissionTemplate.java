package pro.shushi.pamirs.auth.core.template;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;

import java.util.List;

/**
 * group and all permissions excel import/export template
 *
 * @author Adamancy Zhang at 11:09 on 2024-03-19
 */
@Component
public class AuthGroupPermissionTemplate implements ExcelTemplateInit {

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        return null;
    }
}
