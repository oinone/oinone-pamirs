package pro.shushi.pamirs.sso.api.dto;


import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

@Data
public class SsoUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String clientId;

    private String state;

    private String redirectUri;
}
