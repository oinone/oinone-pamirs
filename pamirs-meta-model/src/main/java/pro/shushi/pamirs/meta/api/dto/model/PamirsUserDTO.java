package pro.shushi.pamirs.meta.api.dto.model;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

/**
 * @author shier
 * date 2020/4/7
 */
@Data
public class PamirsUserDTO implements Serializable {

    private static final long serialVersionUID = -5836703468053655937L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 用户编码
     */
    private String userCode;

    /**
     * 账号
     */
    private String login;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    @Deprecated
    private String password;

    /**
     * email
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 当前用户语言
     */
    private String langCode;

    /**
     * 当前SessionKey
     */
    private String sessionKey;
}
