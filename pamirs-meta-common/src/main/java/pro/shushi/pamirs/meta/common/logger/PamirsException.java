package pro.shushi.pamirs.meta.common.logger;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PamirsException extends IThrowException implements GraphQLError {

    public PamirsException(int code, String type, String msg, Object extend) {
        super(code, type, msg, extend);
    }

    public PamirsException(int code, String type, String msg, Object extend, Throwable e) {
        super(code, type, msg, extend, e);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("errorCode", code);
        attributes.put("errorType", type);
        attributes.put("errorMessage", msg);
        attributes.put("extend", extend);
        return attributes;
    }

    public static <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder construct(T expEnum) {
        return new ThrowExceptionBuilder(expEnum, null);
    }

    public static <T extends Enum & ExpBaseEnum> ThrowExceptionBuilder construct(T expEnum, Throwable e) {
        return new ThrowExceptionBuilder(expEnum, e);
    }
}
