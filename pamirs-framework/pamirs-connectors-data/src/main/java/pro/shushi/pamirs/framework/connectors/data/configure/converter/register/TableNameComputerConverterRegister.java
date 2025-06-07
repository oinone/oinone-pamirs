package pro.shushi.pamirs.framework.connectors.data.configure.converter.register;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.configure.converter.String2TableNameComputerConverter;
import pro.shushi.pamirs.meta.api.core.configure.yaml.SpringTypeConverterRegister;

/**
 * TableNameComputer bean转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:56 上午
 */
@SuppressWarnings("unused")
@Component
public class TableNameComputerConverterRegister implements SpringTypeConverterRegister {

    @SuppressWarnings("rawtypes")
    @Override
    public Converter register() {
        return new String2TableNameComputerConverter();
    }
}
