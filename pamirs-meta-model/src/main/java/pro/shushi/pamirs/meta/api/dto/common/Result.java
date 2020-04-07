package pro.shushi.pamirs.meta.api.dto.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

import java.util.ArrayList;
import java.util.List;

import static pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum.LEVEL.INFO;

/**
 * 返回结果类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 * @param <T> 返回数据类型
 */
@Slf4j
@Base
@Model.model("base.Result")
@Model(displayName = "返回结果")
public class Result<T> extends TransientModel {

    private static final long serialVersionUID = -2730211954416437780L;

    @Base
    @Field
    private Boolean success = Boolean.TRUE;

    @Base
    @Field.one2many
    @Field
    private List<Message> messages = new ArrayList<>();

    private T data;

    private String model;

    public boolean isSuccess() {
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
        this.messages.addAll(messages);
        return this;
    }

    public Result<T> addMessage(Message message){
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

    public Result<T> error(ExpBaseEnum expBaseEnum){
        this.setSuccess(Boolean.FALSE);
        this.addMessage(new Message().error(expBaseEnum));
        return this;
    }

    public Result<T> error(){
        this.setSuccess(Boolean.FALSE);
        return this;
    }

    public Result<T> fill(Result<?> dest){
        if(!dest.isSuccess()){
            this.error();
        }
        return this.addMessages(dest.getMessages());
    }

    public void logMessages(){
        if(CollectionUtils.isEmpty(messages)){
            return;
        }
        StringBuilder sb;
        for (Message message : messages) {
            sb = new StringBuilder();
            if(StringUtils.isNotBlank(message.getData())){
                sb.append(message.getData()).append(":");
            }
            if(StringUtils.isNotBlank(message.getMessage())){
                sb.append(message.getMessage());
            }
            if(null != message.getCode()){
                sb.append(",").append(message.getCode());
            }
            sb.append("\n");
            String showMessage = sb.toString();
            ExpBaseEnum.LEVEL level = null != message.getLevel()?message.getLevel():INFO;
            switch (level) {
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
                    log.error(showMessage);
                    break;
            }
        }
    }

}
