package pro.shushi.pamirs.framework.gateways.hook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * 解释Rsql
 *
 * date 2023/06/17
 */
@Base
@Slf4j
@Component
public class RsqlDecodeHook implements HookBefore {

    // @ see:pro.shushi.pamirs.file.api.action.AbstractExcelExportTaskAction.RSQL_ENCODE_PREFIX
    private static final String RSQL_ENCODE_PREFIX = "base64:";

    @Override
    @Hook(priority = 0 )
    public Object run(Function function, Object... args) {
        if (null != args && args.length > 0) {
            int index = 0;
            while (index < args.length && null != args[index]) {
                if (args[index] instanceof AbstractWrapper) {
                    AbstractWrapper wrapper = (AbstractWrapper) args[index];
                    String rsql = wrapper.getRsql();
                    if (StringUtils.isNotBlank(rsql)) {
                        if (rsql.startsWith(RSQL_ENCODE_PREFIX)) {
                            rsql = rsql.substring(RSQL_ENCODE_PREFIX.length());
                            log.debug("rsql解密,密文:{}", rsql);
                            try {
                                rsql = new String(new BASE64Decoder().decodeBuffer(rsql));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            log.debug("rsql解密,明文:{}", rsql);
                            wrapper.setRsql(rsql);
                        }
                    }
                    break;
                }
                index++;
            }
        }
        return function;
    }
}
