package pro.shushi.pamirs.meta.api.dto.common;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.enmu.ExpBaseEnum;

/**
 * 错误类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.Message")
@Model(displayName = "返回消息")
public class Message extends TransientModel {

    private static final long serialVersionUID = -2098313822861289785L;

    private ExpBaseEnum.LEVEL level = ExpBaseEnum.LEVEL.INFO;

    @Base
    @Field
    private String type;

    @Base
    @Field
    private Integer code;

    @Base
    @Field
    private String message;

    @Base
    @Field
    private String data;

    public Message error(ExpBaseEnum error) {
        this.setCode(error.code());
        this.setMessage(error.msg());
        this.setType(error.type().type);
        return this;
    }

    public Message append(String moreMessage) {
        this.setMessage((StringUtils.isBlank(this.getMessage())?"":this.getMessage()) + moreMessage);
        return this;
    }

}
