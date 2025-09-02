package pro.shushi.pamirs.framework.gateways.hook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.AbstractWrapper;
import pro.shushi.pamirs.framework.connectors.data.sql.ISqlSegment;
import pro.shushi.pamirs.framework.connectors.data.sql.segments.MergeSegments;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLToSQLNodeConnector;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookBefore;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.Optional;

/**
 * 解释Rsql
 *
 * @author shier
 * date 2020/4/20
 */
@Base
@Slf4j
@Component
public class RsqlParseHook implements HookBefore {

    @Override
    @Hook(priority = 40)
    public Object run(Function function, Object... args) {
        if (null != args && args.length > 0) {
            int index = 0;
            while (index < args.length && null != args[index]) {
                if (args[index] instanceof AbstractWrapper) {
                    String namespace = function.getNamespace();
                    if ("base.Grouping".equals(namespace)) {
                        break;
                    }
                    if (PamirsSession.getContext().getSimpleModelConfig(namespace) != null) {
                        parse((AbstractWrapper<?, ?, ?>) args[index], namespace);
                    }
                    break;
                }
                index++;
            }
        }
        return function;
    }

    private static void parse(AbstractWrapper<?, ?, ?> wrapper, String model) {
        Optional.ofNullable(wrapper.getExpression())
                .map(MergeSegments::getNormal)
                .filter(v -> !v.isEmpty())
                .ifPresent(segments -> {
                    for (ISqlSegment segment : segments) {
                        if (segment instanceof AbstractWrapper) {
                            parse((AbstractWrapper<?, ?, ?>) segment, model);
                        }
                    }
                });
        String rsql = wrapper.getRsql();
        if (StringUtils.isNotBlank(rsql)) {
            wrapper.setOriginRsql(rsql);
            wrapper.apply(RSQLHelper.toTargetString(RSQLHelper.parse(model, wrapper.getRsql()), RSQLToSQLNodeConnector.INSTANCE));
            wrapper.unsetRsql();
        }
    }
}
