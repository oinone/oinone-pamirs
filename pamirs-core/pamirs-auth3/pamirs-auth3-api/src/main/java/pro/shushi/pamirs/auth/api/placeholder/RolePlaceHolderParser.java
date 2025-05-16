package pro.shushi.pamirs.auth.api.placeholder;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.auth.api.runtime.session.AuthRoleSession;
import pro.shushi.pamirs.core.common.placeholder.AbstractPlaceHolderParser;
import pro.shushi.pamirs.meta.api.core.faas.hook.PlaceHolderParser;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RolePlaceHolderParser extends AbstractPlaceHolderParser implements PlaceHolderParser {

    @Override
    protected String value() {
        Set<Long> roles = AuthRoleSession.getCurrentRoles();
        if (roles == null) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        return CharacterConstants.LEFT_BRACKET +
                roles.stream().map(String::valueOf).collect(Collectors.joining(CharacterConstants.SEPARATOR_COMMA)) +
                CharacterConstants.RIGHT_BRACKET;
    }

    @Override
    public String namespace() {
        return "${currentRoles}";
    }

    @Override
    public Integer priority() {
        return 0;
    }

    @Override
    public Boolean active() {
        return Boolean.TRUE;
    }
}