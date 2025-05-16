package pro.shushi.pamirs.sso.api.dto;


import org.apache.ibatis.type.Alias;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.io.Serializable;

@Data
public class SsoUserVo implements Serializable {

    private String username;

    private String password;

    private String clientId;

    private String state;

    private String redirectUri;
}
