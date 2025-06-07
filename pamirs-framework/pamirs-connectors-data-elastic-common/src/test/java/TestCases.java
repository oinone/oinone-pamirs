import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.IndexNaming;

/**
 * TestCases
 *
 * @author yakir on 2020/05/01 00:44.
 */
public class TestCases {

    public static void main(String[] args) {
        String name = IndexNaming.alias("hahayakir.FooSearch");
        System.out.println(name);
        name = IndexNaming.alias("hahaYakir.FooSearch");
        System.out.println(name);
    }
}
