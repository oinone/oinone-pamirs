package pro.shushi.pamirs.middleware.canal.domain;

import java.io.Serializable;

/**
 * DBInfo
 *
 * @author yakir on 2020/05/25 14:36.
 */
public class DBInfo implements Serializable {

    private static final long serialVersionUID = -3482206290584088750L;

    private String address;
    private Integer port;
    private String userName;
    private String password;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DBInfo{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}