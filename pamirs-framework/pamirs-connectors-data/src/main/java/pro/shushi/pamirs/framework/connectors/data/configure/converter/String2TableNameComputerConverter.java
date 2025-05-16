package pro.shushi.pamirs.framework.connectors.data.configure.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.TableNameComputer;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_TABLE_NAME_COMPUTER_ERROR;

/**
 * String to TableNameComputer bean 转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:57 上午
 */
public class String2TableNameComputerConverter implements Converter<String, TableNameComputer> {

    @Override
    public TableNameComputer convert(@Nullable String s) {
        try {
            return (TableNameComputer) Class.forName(s).newInstance();
        } catch (Exception e) {
            throw PamirsException.construct(BASE_TABLE_NAME_COMPUTER_ERROR, e).errThrow();
        }
    }

}
