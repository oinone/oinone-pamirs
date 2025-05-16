package pro.shushi.pamirs.framework.compute.sequence;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.SequenceGenerator;
import pro.shushi.pamirs.meta.common.constants.NamespaceConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;

@Slf4j
@SPI.Service
@Component
public class DefaultSequenceGenerator<T> implements SequenceGenerator<T> {

    @Override
    public T generate(String sequence, String configCode) {

        return Models.directive().run(() -> Fun.run(NamespaceConstants.sequence, sequence, configCode));

    }

}
