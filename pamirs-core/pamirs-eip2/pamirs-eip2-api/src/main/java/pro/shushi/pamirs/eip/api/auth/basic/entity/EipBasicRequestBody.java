package pro.shushi.pamirs.eip.api.auth.basic.entity;

import pro.shushi.pamirs.eip.api.entity.AbstractEipHttpRequestBody;

import java.io.Serializable;

/**
 * @author Adamancy Zhang on 2021-02-05 17:18
 */
public class EipBasicRequestBody extends AbstractEipHttpRequestBody implements Serializable {

    private static final long serialVersionUID = 5617545628094640163L;

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
