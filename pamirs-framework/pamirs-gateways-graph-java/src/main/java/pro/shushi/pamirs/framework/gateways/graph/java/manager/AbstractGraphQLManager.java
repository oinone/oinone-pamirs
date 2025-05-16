package pro.shushi.pamirs.framework.gateways.graph.java.manager;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;

public abstract class AbstractGraphQLManager {

    protected String generateModelObjectApiName(String name) {
        return StringUtils.capitalize(name);
    }

    protected String generateModelMutationFieldApiName(String name) {
        return name + GraphQLSdlConstants.CAPITAL_MUTATION;
    }

    protected String generateModelMutationApiName(String name) {
        return StringUtils.capitalize(name) + GraphQLSdlConstants.CAPITAL_MUTATION;
    }

    protected String generateModelQueryFieldApiName(String name) {
        return name + GraphQLSdlConstants.CAPITAL_QUERY;
    }

    protected String generateModelQueryApiName(String name) {
        return StringUtils.capitalize(name) + GraphQLSdlConstants.CAPITAL_QUERY;
    }

    protected String generateModelInputApiName(String name) {
        return StringUtils.capitalize(name) + GraphQLSdlConstants.CAPITAL_INPUT;
    }

    protected String generateModelCondInputApiName(String name) {
        return StringUtils.capitalize(name) + GraphQLSdlConstants.COND_INPUT;
    }

    protected String generateModelPageApiName(String name) {
        return StringUtils.capitalize(name) + GraphQLSdlConstants.CAPITAL_PAGE;
    }

}
