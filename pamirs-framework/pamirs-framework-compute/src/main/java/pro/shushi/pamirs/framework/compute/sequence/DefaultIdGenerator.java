package pro.shushi.pamirs.framework.compute.sequence;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.id.UidGeneratorFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.IdGenerator;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.util.Objects;

@Slf4j
@SPI.Service
@Component
public class DefaultIdGenerator<T> implements IdGenerator<T> {
    @Override
    public T generate(String keyGenerator) {
        KeyGeneratorEnum enumerate = KeyGeneratorEnum.valueOf(keyGenerator);
        switch (enumerate) {
            case NON:
            case AUTO_INCREMENT:
                break;
            default: {
                Function function = Objects.requireNonNull(PamirsSession.getContext())
                        .getFunctionAllowNull(NamespaceConstants.sequence, keyGenerator);
                if (null == function) {
                    //noinspection unchecked
                    return (T) Long.valueOf(UidGeneratorFactory.getCachedUidGenerator().getUID());
                }
                return Models.directive().run(() -> Fun.run(NamespaceConstants.sequence, keyGenerator));
            }
        }
        return null;
    }
}
