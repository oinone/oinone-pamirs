package pro.shushi.pamirs.core.common.service;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.api.EditionService;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;

/**
 * CommunityEditionService
 *
 * @author yakir on 2025/05/14 16:15.
 */
@Service
@Fun(EditionService.FUN_NAMESPACE)
public class CommunityEditionService implements EditionService {

    @Override
    @Function
    public Boolean checkEdition() {
        return false;
    }
}
