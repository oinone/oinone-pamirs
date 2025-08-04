package pro.shushi.pamirs.framework.orm.api;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.core.orm.NamedApi;
import pro.shushi.pamirs.meta.api.core.orm.processor.NameProcessor;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 默认名称转换服务
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/18 6:35 下午
 */
@Component
public class DefaultNamedApi implements NamedApi {

    @Resource
    private NameProcessor nameToLnameProcessor;

    @Resource
    private NameProcessor lnameToNameProcessor;

    @Resource
    private NameProcessor lnameToColumnProcessor;

    @Resource
    private NameProcessor columnToLnameProcessor;

    @Override
    public Map<String, Object> nameToLname(String model, Map<String, Object> origin) {
        return nameToLnameProcessor.convert(model, origin);
    }

    @Override
    public List<Map<String, Object>> nameToLname(String model, List<Map<String, Object>> list) {
        return nameToLnameProcessor.convert(model, list);
    }

    @Override
    public Map<String, Object> lnameToName(String model, Map<String, Object> origin) {
        return lnameToNameProcessor.convert(model, origin);
    }

    @Override
    public List<Map<String, Object>> lnameToName(String model, List<Map<String, Object>> list) {
        return lnameToNameProcessor.convert(model, list);
    }

    @Override
    public Map<String, Object> lnameToColumn(String model, Map<String, Object> origin) {
        return lnameToColumnProcessor.convert(model, origin);
    }

    @Override
    public List<Map<String, Object>> lnameToColumn(String model, List<Map<String, Object>> list) {
        return lnameToColumnProcessor.convert(model, list);
    }

    @Override
    public Map<String, Object> columnToLname(String model, Map<String, Object> origin) {
        return columnToLnameProcessor.convert(model, origin);
    }

    @Override
    public List<Map<String, Object>> columnToLname(String model, List<Map<String, Object>> list) {
        return columnToLnameProcessor.convert(model, list);
    }

}
