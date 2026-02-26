package pro.shushi.pamirs.eip.jdbc.service.url;

/*
 * DefaultHanaComponent
 * @author : Haibo(xf.z@shushi.pro)
 * @date : 2025/7/9 18:33
 */
public class DefaultHanaComponent extends AbstractJdbcComponent {

    @Override
    public String dbType() {
        return "HANA";
    }

    @Override
    public String paramSeparator() {
        return "?";
    }

    @Override
    public String urlTemplate() {
        return "jdbc:sap://%s:%s/%s";
    }
}
