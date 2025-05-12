package pro.shushi.pamirs.meta.api.core.configure.yaml.data;

import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestVariables;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源路由参数接口
 * <p>
 * 2020/6/22 9:11 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI
public interface DynamicDsKeyComputer {

    String VAR = "var.";

    String PARAM = "param.";

    String HEAD = "head.";

    default Map<String, Object> context() {
        Map<String, Object> context = new HashMap<>();
        PamirsRequestVariables pamirsRequestVariables = PamirsSession.getRequestVariables();
        if (null != pamirsRequestVariables) {
            Map<String, Object> variables = pamirsRequestVariables.getVariables();
            if (null != variables) {
                for (String key : variables.keySet()) {
                    context.put(VAR + key, variables.get(key));
                }
            }
            Map<String, String[]> parameterMap = pamirsRequestVariables.getParameterMap();
            if (null != parameterMap) {
                for (String key : parameterMap.keySet()) {
                    String[] values = parameterMap.get(key);
                    if (null != values && values.length != 0) {
                        if (values.length == 1) {
                            context.put(PARAM + key, values[0]);
                        } else {
                            int i = 0;
                            for (String value : values) {
                                context.put(PARAM + key + "[" + i + "]", value);
                                i++;
                            }
                        }
                    }
                }
            }
            Map<String, String> headers = pamirsRequestVariables.getHeaders();
            if (null != headers) {
                for (String key : headers.keySet()) {
                    context.put(HEAD + key, headers.get(key));
                }
            }
        }
        return context;
    }

}
