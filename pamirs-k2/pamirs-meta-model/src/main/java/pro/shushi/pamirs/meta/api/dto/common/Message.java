package pro.shushi.pamirs.meta.api.dto.common;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.orm.path.ClientExecutionPath;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;

import java.util.List;

/**
 * 消息
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base
@Model.model("base.Message")
@Model(displayName = "消息")
public class Message extends TransientModel {

    private static final long serialVersionUID = -2098313822861289785L;

    @Base
    @Field.String
    @Field(displayName = "内容", summary = "内容", required = true, translate = true)
    private String message;

    @Base
    @Field(displayName = "级别", summary = "级别", defaultValue = "error", translate = true)
    private InformationLevelEnum level;

    @Base
    @Field(displayName = "错误类型", summary = "如果消息级别为错误，则可指定错误类型", defaultValue = "BIZ_ERROR", translate = true)
    private ErrorTypeEnum errorType;

    @Base
    @Field(displayName = "编码", summary = "标识编码或者错误码")
    private String code;

    @Base
    @Field.String
    @Field(displayName = "相关模型", summary = "相关模型", store = NullableBoolEnum.FALSE, invisible = true)
    private String model;

    @Base
    @Field.String
    @Field(displayName = "相关字段", summary = "相关字段", store = NullableBoolEnum.FALSE, invisible = true)
    private String field;

    @Base
    @Field.String
    @Field(displayName = "数据路径", summary = "数据路径", store = NullableBoolEnum.FALSE, invisible = true)
    private List<Object> path;

    @Base
    @Field.String
    @Field(displayName = "数据参考", summary = "与该消息关联的数据", store = NullableBoolEnum.FALSE, invisible = true)
    private String data;

    public Message() {
        ClientExecutionPath path = PamirsSession.getMessageHub().getPath();
        if (null != path) {
            this.setPath(path.toList());
        }
    }

    public static Message init() {
        return new Message();
    }

    public Message msg(ExpBaseEnum error) {
        this.setCode(error.code() + "");
        this.setMessage(error.msg());
        this.setErrorType(ErrorTypeEnum.valueOf(error.type().getType()));
        return this;
    }

    public Message msg(String newMessage) {
        if (null != newMessage) {
            this.setMessage(newMessage);
        }
        return this;
    }

    public Message error(ExpBaseEnum error) {
        this.msg(error);
        this.setLevel(InformationLevelEnum.ERROR);
        return this;
    }

    public Message append(String moreMessage) {
        if (null == this.getMessage() && null != moreMessage) {
            this.setMessage(moreMessage);
        } else if (null != moreMessage) {
            this.setMessage(this.getMessage() + moreMessage);
        }
        return this;
    }

    /**
     * set field sync set field error code.
     *
     * @param field fieldName
     * @see 10050009 {@link FwExpEnumerate#BASE_CHECK_DATA_ERROR}
     */
    public Message setField(String field) {
        if (!this.get_d().containsKey("code")) {
            this.get_d().put("code", "10050009");
        }
        this.get_d().put("field", field);
        return this;
    }
}
