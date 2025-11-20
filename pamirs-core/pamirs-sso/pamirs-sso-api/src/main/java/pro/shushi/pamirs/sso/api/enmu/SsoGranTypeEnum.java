package pro.shushi.pamirs.sso.api.enmu;

public enum SsoGranTypeEnum {

    CREDENTIALS("client_credentials", "凭证登录"),
    CODE("authorization_code", "授权码登录"),
    PASSWORD("password", "密码登录"),
    REFRESHTOKEN("refresh_token", "刷新ACCESS TOEKN");

    private final String type;

    private final String help;

    SsoGranTypeEnum(String type, String help) {
        this.type = type;
        this.help = help;
    }

    public String getType() {
        return type;
    }

    public String getHelp() {
        return help;
    }
}
