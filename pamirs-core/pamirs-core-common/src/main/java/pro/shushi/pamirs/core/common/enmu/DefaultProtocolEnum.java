package pro.shushi.pamirs.core.common.enmu;

/**
 * @author Adamancy Zhang on 2021-02-20 00:33
 */
public enum DefaultProtocolEnum {

    HTTP("http", 80),
    HTTPS("https", 443),
    ;

    DefaultProtocolEnum(String protocol, int port) {
        this.protocol = protocol;
        this.port = port;
    }

    private final String protocol;

    private final int port;

    public String getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    public static DefaultProtocolEnum valueOfByProtocol(String protocol) {
        for (DefaultProtocolEnum defaultProtocol : DefaultProtocolEnum.values()) {
            if (defaultProtocol.protocol.equals(protocol)) {
                return defaultProtocol;
            }
        }
        return null;
    }

    public static boolean isDefaultProtocol(String protocol, int port) {
        for (DefaultProtocolEnum defaultProtocol : DefaultProtocolEnum.values()) {
            if (defaultProtocol.protocol.equals(protocol) && port == defaultProtocol.port) {
                return true;
            }
        }
        return false;
    }
}
