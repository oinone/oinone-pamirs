package pro.shushi.pamirs.meta.api.dto.msg;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.enmu.ErrorTypeEnum;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.io.Serializable;
import java.util.List;

/**
 * 详细错误
 * <p>
 * 2021/3/13 10:37 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class ErrorExtension implements Serializable {

    private static final long serialVersionUID = 918047779937091191L;

    /**
     * 总错误提示
     */
    private String message;

    /**
     * 总错误码
     */
    private String code;

    /**
     * 总错误类型
     */
    private ErrorTypeEnum type;

    /**
     * 总信息级别
     */
    private InformationLevelEnum level;

    /**
     * 信息列表
     */
    private List<Message> messages;

}
