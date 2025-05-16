package pro.shushi.pamirs.framework.connectors.data.autoconfigure.pamirs;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.stereotype.Component;

@Component
public class PamirsIdentifierGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        throw new UnsupportedOperationException("Unsupported");
    }
}
