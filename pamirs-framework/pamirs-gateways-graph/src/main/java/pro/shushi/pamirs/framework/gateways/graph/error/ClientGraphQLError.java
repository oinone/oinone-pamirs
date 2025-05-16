package pro.shushi.pamirs.framework.gateways.graph.error;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.msg.ErrorExtension;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pamirs客户端错误类
 * <p>
 * 2021/3/18 1:56 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ClientGraphQLError implements GraphQLError {

    private static final long serialVersionUID = 2399239083526453364L;

    public static final String MESSAGE = "message";
    public static final String EXTENSIONS = "extensions";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_TYPE = "errorType";
    public static final String EXTRA = "extra";
    public static final String LEVEL = "level";
    public static final String MESSAGES = "messages";
    public static final String EXTEND = "extend";
    public static final String STACKTRACEKEY = "stackTraceKey";
    public static final String STACKTRACEMSG = "stackTraceMsg";

    private ErrorExtension errorExtension;
    private String stackTraceKey;
    private String stackTraceMsg;

    public static ClientGraphQLError build(ErrorExtension errorExtension) {
        ClientGraphQLError clientGraphQLError = new ClientGraphQLError();
        clientGraphQLError.errorExtension = errorExtension;
        return clientGraphQLError;
    }

    @SuppressWarnings("unchecked")
    public static ClientGraphQLError build(PamirsException exception) {
        ClientGraphQLError clientGraphQLError = new ClientGraphQLError();
        ErrorExtension errorExtension = new ErrorExtension();
        errorExtension.setMessage(exception.getMessage());
        errorExtension.setLevel(InformationLevelEnum.valueOf(exception.getLevel()));
        errorExtension.setCode(exception.getCode() + CharacterConstants.SEPARATOR_EMPTY);
        errorExtension.setType(ErrorTypeEnum.valueOf(exception.getType()));
        errorExtension.setMessages((List<Message>) exception.getMsgDetail());
        clientGraphQLError.errorExtension = errorExtension;
        return clientGraphQLError;
    }

    public static ClientGraphQLError build(Pair<String,String> stackTraceKey, String stackTraceMsg) {
        ClientGraphQLError clientGraphQLError = new ClientGraphQLError();
        ErrorExtension errorExtension = new ErrorExtension();
        errorExtension.setCode("stackTrace");
        errorExtension.setMessage(stackTraceKey.getRight());
        clientGraphQLError.errorExtension = errorExtension;
        clientGraphQLError.stackTraceKey = stackTraceKey.getLeft();
        clientGraphQLError.stackTraceMsg = stackTraceMsg;
        return clientGraphQLError;
    }

    @Override
    public String getMessage() {
        return errorExtension.getMessage();
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(ERROR_CODE, errorExtension.getCode());
        attributes.put(ERROR_TYPE, errorExtension.getType());
        attributes.put(EXTRA, null);
        attributes.put(LEVEL, errorExtension.getLevel());
        attributes.put(MESSAGES, errorExtension.getMessages());
        attributes.put(EXTEND, null);
        attributes.put(STACKTRACEKEY, stackTraceKey);
        attributes.put(STACKTRACEMSG, stackTraceMsg);
        return attributes;
    }

    public static Map<String, Object> getExtensions(ExpBaseEnum expBaseEnum) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(ERROR_CODE, expBaseEnum.code());
        attributes.put(ERROR_TYPE, expBaseEnum.type().getType());
        attributes.put(LEVEL, InformationLevelEnum.ERROR.name());
        return attributes;
    }

    public static Map<String, Object> getExtensions(PamirsException exception) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put(ERROR_CODE, exception.getCode());
        attributes.put(ERROR_TYPE, exception.getType());
        attributes.put(LEVEL, InformationLevelEnum.ERROR.name());
        return attributes;
    }
}
