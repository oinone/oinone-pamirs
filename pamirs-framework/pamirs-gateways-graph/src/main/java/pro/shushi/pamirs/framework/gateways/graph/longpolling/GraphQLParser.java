package pro.shushi.pamirs.framework.gateways.graph.longpolling;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

@Slf4j
public class GraphQLParser {

    public static Function getServerAction(String gql) {

        try {
            if (StringUtils.isNotBlank(gql)) {
                gql = StringUtils.substringAfter(gql, "{");
                String modelName = StringUtils.substringBefore(gql, "{");
                gql = StringUtils.substringAfter(gql, "{");
                String serverName = StringUtils.substringBefore(gql, "(");
                if (StringUtils.isNotBlank(modelName) && StringUtils.isNotBlank(serverName)) {
                    modelName = modelName.replace("\n", "").replace("Mutation", "").trim();
                    serverName = serverName.replace("\n", "").trim();
                    ModelConfig modelConfig = PamirsSession.getContext().getModelCache().getByName(modelName);
                    if (null != modelConfig) {
                        return PamirsSession.getContext().getFunction(modelConfig.getModel(), serverName);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("parser error", e);
        }

        return null;
    }
}
