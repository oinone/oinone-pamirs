package pro.shushi.pamirs.framework.connectors.data.configure.converter;

import org.springframework.core.convert.converter.Converter;
import pro.shushi.pamirs.meta.api.core.configure.yaml.data.LogicColumnFetcher;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import static pro.shushi.pamirs.framework.connectors.data.enmu.DataExpEnumerate.BASE_LOGIC_COLUMN_FETCHER_ERROR;

/**
 * String to LogicColumnFetcher bean 转换器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 12:57 上午
 */
public class String2LogicColumnFetcherConverter implements Converter<String, LogicColumnFetcher> {

    @Override
    public LogicColumnFetcher convert(String s) {
        try {
            return (LogicColumnFetcher) Class.forName(s).newInstance();
        } catch (Exception e) {
            throw PamirsException.construct(BASE_LOGIC_COLUMN_FETCHER_ERROR, e).errThrow();
        }
    }

}
