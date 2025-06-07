package pro.shushi.pamirs.meta.api.dto.msg;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.core.orm.path.ClientExecutionPath;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.protocol.RequestInfo;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 消息核心
 * <p>
 * 2021/3/13 10:35 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class MessageHub implements Serializable {

    private static final long serialVersionUID = -4825136784796890882L;

    /**
     * 是否成功（不包含ERROR级别的消息）
     */
    private boolean success;

    /**
     * 是否异常
     */
    private boolean exception;

    /**
     * 详细信息
     */
    private DataExtension dataExtension;

    /**
     * 详细错误
     */
    private ErrorExtension errorExtension;

    /**
     * 执行路径
     */
    private ClientExecutionPath path;

    public MessageHub() {
        this.success = true;
    }

    public Map<Object, Object> getExtensions() {
        if (null == dataExtension) {
            return null;
        }
        return dataExtension.getExtensions();
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        if (null != dataExtension && null != dataExtension.getMessages()) {
            messages.addAll(dataExtension.getMessages());
        }
        if (null != errorExtension && null != errorExtension.getMessages()) {
            messages.addAll(errorExtension.getMessages());
        }
        return messages;
    }

    public List<Message> getDataMessages() {
        if (null == dataExtension) {
            return null;
        }
        return dataExtension.getMessages();
    }

    public List<Message> getErrorMessages() {
        if (null == errorExtension) {
            return null;
        }
        return errorExtension.getMessages();
    }

    public MessageHub extensions(Map<Object, Object> extensions) {
        if (null == dataExtension) {
            setDataExtension(new DataExtension());
        }
        if (null == dataExtension.getExtensions()) {
            dataExtension.setExtensions(new HashMap<>());
        }
        dataExtension.getExtensions().putAll(extensions);
        return this;
    }

    public MessageHub extensions(Object key, Object value) {
        if (null == dataExtension) {
            setDataExtension(new DataExtension());
        }
        if (null == dataExtension.getExtensions()) {
            dataExtension.setExtensions(new HashMap<>());
        }
        dataExtension.getExtensions().put(key, value);
        return this;
    }

    public Set<String> getDirectives() {
        if (null == dataExtension) {
            return null;
        }
        return dataExtension.getDirectives();
    }

    public MessageHub directives(Set<String> directives) {
        if (null == dataExtension) {
            setDataExtension(new DataExtension());
        }
        if (null == dataExtension.getDirectives()) {
            dataExtension.setDirectives(new LinkedHashSet<>());
        }
        dataExtension.getDirectives().addAll(directives);
        return this;
    }

    public MessageHub directives(String directive) {
        if (null == dataExtension) {
            setDataExtension(new DataExtension());
        }
        if (null == dataExtension.getDirectives()) {
            dataExtension.setDirectives(new LinkedHashSet<>());
        }
        dataExtension.getDirectives().add(directive);
        return this;
    }

    public MessageHub fill(boolean success, ErrorExtension errorExtension) {
        if (!success) {
            this.setSuccess(false);
        }
        if (null == errorExtension) {
            return this;
        }
        if (null != errorExtension.getMessages()) {
            this.msg(errorExtension.getMessages());
        }
        if (null != errorExtension.getMessage()) {
            this.errorExtension.setMessage(errorExtension.getMessage());
        }
        if (null != errorExtension.getCode()) {
            this.errorExtension.setCode(errorExtension.getCode());
        }
        if (null != errorExtension.getLevel()) {
            this.errorExtension.setLevel(errorExtension.getLevel());
        }
        if (null != errorExtension.getType()) {
            this.errorExtension.setType(errorExtension.getType());
        }
        return this;
    }

    public MessageHub totalError(ExpBaseEnum error) {
        initErrorExtension();
        error();
        errorExtension.setMessage(error.msg()).setCode(error.code() + "")
                .setLevel(InformationLevelEnum.ERROR).setType(ErrorTypeEnum.valueOf(error.type().getType()));
        return this;
    }

    public MessageHub msg(Message message) {
        if (null == message) {
            return this;
        }
        if (null == message.getLevel() || InformationLevelEnum.ERROR.equals(message.getLevel())) {
            error();
            initErrorExtension();
            errorExtension.getMessages().add(message);
        } else {
            initDataExtension();
            dataExtension.getMessages().add(message);
        }
        return this;
    }

    public MessageHub msg(List<Message> messages) {
        if (null == messages) {
            return this;
        }
        messages.forEach(this::msg);
        return this;
    }

    public MessageHub msg(Supplier<?> messageSupplier) {
        return deliver(messageSupplier, this::msg);
    }

    public MessageHub error() {
        this.success = false;
        return this;
    }

    public MessageHub error(String message) {
        if (null == message) {
            return this;
        }
        initErrorExtension();
        error();
        errorExtension.getMessages().add(Message.init().setMessage(message)
                .setLevel(InformationLevelEnum.ERROR).setErrorType(ErrorTypeEnum.BIZ_ERROR));
        return this;
    }

    public MessageHub error(ExpBaseEnum error) {
        initErrorExtension();
        error();
        errorExtension.getMessages().add(Message.init().setMessage(error.msg()).setCode(error.code() + "")
                .setLevel(InformationLevelEnum.ERROR).setErrorType(ErrorTypeEnum.valueOf(error.type().getType())));
        return this;
    }

    public MessageHub error(List<Message> messages) {
        if (null == messages) {
            return this;
        }
        messages.forEach(v -> {
            v.setLevel(InformationLevelEnum.ERROR);
            msg(v);
        });
        return this;
    }

    public MessageHub info(String message) {
        return appendMessage(InformationLevelEnum.INFO, message);
    }

    public MessageHub warn(String message) {
        return appendMessage(InformationLevelEnum.WARN, message);
    }

    public MessageHub success(String message) {
        return appendMessage(InformationLevelEnum.SUCCESS, message);
    }

    private MessageHub appendMessage(InformationLevelEnum level, String message) {
        if (null == message) {
            return this;
        }
        initDataExtension();
        dataExtension.getMessages().add(Message.init().setMessage(message)
                .setLevel(level).setErrorType(null));
        return this;
    }

    public MessageHub appendPath(String segment) {
        if (null == segment) {
            return this;
        }
        this.path = this.path.segment(segment);
        return this;
    }

    public MessageHub appendPath(Integer segment) {
        if (null == segment) {
            return this;
        }
        this.path = this.path.segment(segment);
        return this;
    }

    public <I extends D, R, E extends Enum<E> & ExpBaseEnum> PamirsException error(Getter<I, R> getter, E expEnum) {
        return error(getter, expEnum, null);
    }

    public <I extends D, R, E extends Enum<E> & ExpBaseEnum> PamirsException error(Getter<I, R> getter, E expEnum, String extMsg) {
        this.path = this.path.segment(LambdaUtil.fetchFieldName(getter));
        this.error(expEnum);
        PamirsException.Builder<E> expBuilder = PamirsException.construct(expEnum);
        if (null != extMsg) {
            expBuilder.appendMsg(extMsg);
        }
        return expBuilder.errThrow();
    }

    @SuppressWarnings("unchecked")
    public MessageHub deliver(Supplier<?> messageSupplier, Consumer<Message> messageConsumer) {
        RequestInfo requestStrategy = PamirsSession.getRequestVariables().getRequestInfo();
        InformationLevelEnum informationLevel = null == requestStrategy
                ? InformationLevelEnum.ERROR : requestStrategy.getMsgLevel();
        Object messageObject = messageSupplier.get();
        if (messageObject instanceof List) {
            ((List<Message>) messageObject).forEach(v -> {
                if (filter(informationLevel, v)) {
                    messageConsumer.accept(v);
                }
            });
        } else {
            Message v = (Message) messageObject;
            if (filter(informationLevel, v)) {
                messageConsumer.accept(v);
            }
        }
        return this;
    }

    private boolean filter(InformationLevelEnum informationLevel, Message message) {
        InformationLevelEnum level = message.getLevel();
        if (null == level) {
            level = InformationLevelEnum.ERROR;
        }
        return level.ordinal() >= informationLevel.ordinal();
    }

    private void initDataExtension() {
        if (null == dataExtension) {
            this.setDataExtension(new DataExtension());
        }
        if (null == dataExtension.getMessages()) {
            dataExtension.setMessages(new ArrayList<>());
        }
    }

    private void initErrorExtension() {
        if (null == errorExtension) {
            this.setErrorExtension(new ErrorExtension());
        }
        if (null == errorExtension.getMessages()) {
            errorExtension.setMessages(new ArrayList<>());
        }
    }

    public MessageHub clear() {
        this.success = true;
        this.setPath(null);
        clearDataExtension();
        clearErrorExtension();
        return this;
    }

    public MessageHub clearDataExtension() {
        if (null != getDataExtension()) {
            setDataExtension(null);
        }
        return this;
    }

    public MessageHub clearErrorExtension() {
        if (null != getErrorExtension()) {
            setErrorExtension(null);
        }
        return this;
    }

    public static <T> T closure(Supplier<T> supplier) {
        PamirsSession.getMessageHub().clear();
        try {
            return supplier.get();
        } finally {
            PamirsSession.getMessageHub().clear();
        }
    }

}
