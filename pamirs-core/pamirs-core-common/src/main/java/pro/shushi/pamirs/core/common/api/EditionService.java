package pro.shushi.pamirs.core.common.api;

import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * EditionService
 *
 * @author yakir on 2025/05/14 15:07.
 */
@Fun(EditionService.FUN_NAMESPACE)
public interface EditionService {

    String FUN_NAMESPACE = "common.EditionService";

    @Function
    Boolean checkEdition();

}
