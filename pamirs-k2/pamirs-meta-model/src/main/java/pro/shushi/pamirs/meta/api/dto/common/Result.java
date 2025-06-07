package pro.shushi.pamirs.meta.api.dto.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.meta.api.dto.common.Result.MODEL_MODEL;
import static pro.shushi.pamirs.meta.enmu.InformationLevelEnum.INFO;

/**
 * 返回结果类
 *
 * @param <T> 返回数据类型
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Slf4j
@Base
@Model.model(MODEL_MODEL)
@Model(displayName = "返回结果")
public class Result<T> extends TransientModel {

    public static final String MODEL_MODEL = "base.Result";

    private static final long serialVersionUID = -2730211954416437780L;

    @Base
    @Field
    private Boolean success = Boolean.TRUE;

    @Base
    @Field.one2many
    @Field
    private List<Message> messages = new ArrayList<>();

    private T data;

    private int effectRows;

    public boolean isSuccess() {
        return success;
    }

    public boolean getSuccess() {
        return success;
    }

    public Result<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @SuppressWarnings("unused")
    public Result<T> setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }

    public Result<T> addMessages(List<Message> messages) {
        if (null == messages) {
            return this;
        }
        this.messages.addAll(messages);
        return this;
    }

    public Result<T> addMessage(Message message) {
        if (null == message) {
            return this;
        }
        this.messages.add(message);
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Result<T> error(ExpBaseEnum expBaseEnum) {
        this.setSuccess(Boolean.FALSE);
        this.addMessage(new Message().error(expBaseEnum));
        return this;
    }

    public Result<T> error() {
        this.setSuccess(Boolean.FALSE);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <R> R error(Message message) {
        this.error().addMessage(message);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public <R> R msg(Result<Void> result) {
        this.fill(result);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public <R> R msg(Message message) {
        this.addMessage(message);
        return (R) this;
    }

    public Result<T> fill(Result<?> origin) {
        if (!origin.isSuccess()) {
            this.error();
        }
        return this.addMessages(origin.getMessages());
    }

    public void logMessages(InformationLevelEnum level) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        StringBuilder sb;
        for (Message message : messages) {
            sb = new StringBuilder();
            if (StringUtils.isNotBlank(message.getData())) {
                sb.append(message.getData()).append(",");
            }
            if (StringUtils.isNotBlank(message.getModel())) {
                sb.append("model:").append(message.getModel()).append(",");
            }
            if (StringUtils.isNotBlank(message.getField())) {
                sb.append("field:").append(message.getField()).append(",");
            }
            if (StringUtils.isNotBlank(message.getMessage())) {
                sb.append(message.getMessage());
            }
            if (null != message.getCode()) {
                sb.append(",").append(message.getCode());
            }
            sb.append("\n");
            String showMessage = sb.toString();
            InformationLevelEnum currentLevel = null != message.getLevel() ? message.getLevel() : INFO;
            int currentOrdinal = currentLevel.ordinal();
            int levelOrdinal = level.ordinal();
            if (currentOrdinal < levelOrdinal) {
                continue;
            }
            switch (currentLevel) {
                case DEBUG:
                    log.debug(showMessage);
                    break;
                case INFO:
                    log.info(showMessage);
                    break;
                case WARN:
                    log.warn(showMessage);
                    break;
                case ERROR:
                default:
                    log.error(showMessage);
                    break;
            }
        }
    }

    public Result<T> setModel(String model) {
        Models.api().setDataModel(model, this);
        return this;
    }

    public String getModel() {
        return Models.api().getDataModel(this);
    }

    public int getEffectRows() {
        return effectRows;
    }

    public void setEffectRows(int effectRows) {
        this.effectRows = effectRows;
    }

}
