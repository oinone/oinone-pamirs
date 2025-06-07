package pro.shushi.pamirs.framework.gateways.rsql;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

/**
 * @author shier
 * date  2020/8/25 8:47 下午
 */
public class RsqlParseHelper {

    public static String parseRsql2Sql(ModelConfig modelConfig, String rsql) {
        Node parse = new RSQLParser(RsqlSearchOperation.getOperators()).parse(rsql);
        RsqlQuery query = parse.accept(new PamirsRsqlVisitor(), modelConfig);
        return query.getWhere().toString();
    }

    public static String parseRsql2Sql(String model, String rsql) {
        if (StringUtils.isBlank(rsql)) {
            return rsql;
        }
        return parseRsql2Sql(PamirsSession.getContext().getSimpleModelConfig(model), rsql);
    }
}
