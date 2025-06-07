package pro.shushi.pamirs.eip.api.thirdParty;

/**
 * @author Adamancy Zhang on 2021-02-06 16:31
 */
public class EipInterfaceUri {

    private final String name;

    private final String interfaceName;

    private final String uri;

    public EipInterfaceUri(String name, String interfaceName, String uri) {
        this.name = name;
        this.interfaceName = interfaceName;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getUri() {
        return uri;
    }
}
