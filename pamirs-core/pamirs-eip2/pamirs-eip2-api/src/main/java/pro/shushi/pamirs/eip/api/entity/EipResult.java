package pro.shushi.pamirs.eip.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * EIP继承接口调用结果
 *
 * @author Adamancy Zhang at 12:28 on 2021-08-04
 */
public class EipResult<T> implements Serializable {

    private static final long serialVersionUID = 5369382784912446028L;

    @JSONField(serialize = false)
    private final IEipContext<T> context;

    private final Boolean success;

    private final String errorCode;

    private final String errorMessage;

    private final Object result;

    private EipResult(IEipContext<T> context, Boolean success, String errorCode, String errorMessage, Object result) {
        this.context = context;
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.result = result;
    }

    public static <T> EipResult<T> success(IEipContext<T> context, Object result) {
        return new EipResult<>(context, true, null, null, result);
    }

    public static <T> EipResult<T> error(IEipContext<T> context, String errorCode, String errorMessage, Object result) {
        return new EipResult<>(context, false, errorCode, errorMessage, result);
    }

    public IEipContext<T> getContext() {
        return context;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @SuppressWarnings("unchecked")
    public <V> V getResult() {
        return (V) result;
    }

    @SuppressWarnings("unchecked")
    public <V> V getResult(Type type) {
        String resultString;
        if (result instanceof String) {
            resultString = (String) result;
            if (String.class.equals(type)) {
                return (V) resultString;
            }
            if (JsonUtils.isJSONString(resultString)) {
                return JsonUtils.parseObject(resultString, type);
            } else {
                throw new UnsupportedOperationException("不被支持的结果类型");
            }
        } else {
            resultString = JsonUtils.toJSONString(result);
            if (String.class.equals(type)) {
                return (V) resultString;
            }
            return JsonUtils.parseObject(resultString, type);
        }
    }

    public <E extends Enum<E> & ExpBaseEnum> PamirsException.Builder<E> fetchException(E exceptionEnumeration) {
        Object exception = getResult();
        PamirsException.Builder<E> exceptionBuilder;
        if (exception instanceof Throwable) {
            exceptionBuilder = PamirsException.construct(exceptionEnumeration, (Throwable) exception);
        } else {
            exceptionBuilder = PamirsException.construct(exceptionEnumeration);
        }
        String errorMessage = getErrorMessage();
        if (StringUtils.isNotBlank(errorMessage)) {
            exceptionBuilder.appendMsg(errorMessage);
        }
        return exceptionBuilder;
    }
}
