package pro.shushi.pamirs.eip;

import org.apache.camel.ExtendedExchange;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipInOutConverter;
import pro.shushi.pamirs.eip.api.builder.DefaultEipInterfaceBuilder;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.context.EipInterfaceContext;
import pro.shushi.pamirs.eip.api.entity.DefaultEipConvertParam;
import pro.shushi.pamirs.eip.api.entity.EipResult;
import pro.shushi.pamirs.eip.jdbc.camel.EipSqlPrepareStatementStrategy;
import pro.shushi.pamirs.eip.jdbc.helper.SQLPrepareHelper;
import pro.shushi.pamirs.eip.jdbc.manager.EipDataSourceManager;

/**
 * @author Adamancy Zhang at 14:29 on 2024-03-20
 */
public class CamelTest {

    private static final String SQL_PARAMETER_KEY = "__pamirs_eip_sql__";

    public static void main(String[] args) throws Exception {
//        openApiTest();
        sqlTest();
//        jdbcTest();
    }

    private static void sqlTest() throws Exception {
        EipCamelContext context = EipCamelContext.getContext();
        context.getCamelContext().getRegistry().bind(EipSqlPrepareStatementStrategy.NAME, EipSqlPrepareStatementStrategy.INSTANCE);

        EipDataSourceManager.register("testDB", () -> EipDataSourceManager.buildSimpleDataSource(
                "jdbc:mysql://127.0.0.1:3306/zbh?useSSL=false&allowPublicKeyRetrieval=true&useServerPrepStmts=true&cachePrepStmts=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&autoReconnect=true&allowMultiQueries=true",
                "com.mysql.cj.jdbc.Driver",
                "root",
                "shushi@2019"));

//        EipInitializationUtil.newInstance().from(EipFunctionConstant.EMPTY.apply(context, "testSQL"))
//                .to(DefaultEipInterfaceBuilder.newInstance("testSQL-sql", "sql:"))
//                .end();

//        String sql = "insert into ts1(id,namea,nameb,age) values(:#id,:#namea,:#nameb,:#age)";
//        String sql = "update ts1 set namea = :#namea, nameb = :#nameb, age = :#age where id = :#id";
        String sql = "delete from ts1 where id = :#id";
//        String sql = "${__sql__}";

//        String sql = "select id, namea, nameb, age\n" +
//                "from ts1\n" +
//                "where id > 0";

        sql = SQLPrepareHelper.prepareSQL(sql);

        EipInterfaceContext.putInterface(DefaultEipInterfaceBuilder.newInstance(context, "testSQL",
                "sql:" + sql + "?" +
                        "batch=true&" +
                        "dataSource=" + EipDataSourceManager.generatorId("testDB") + "&" +
                        "prepareStatementStrategy=#" + EipSqlPrepareStatementStrategy.NAME)
                .createRequestParamProcessor()
                .setFinalResultKey("result")
                .addConvertParam(new DefaultEipConvertParam<>("id", "result.id"))
                .addConvertParam(new DefaultEipConvertParam<>("namea", "result.namea"))
                .addConvertParam(new DefaultEipConvertParam<>("nameb", "result.nameb"))
                .addConvertParam(new DefaultEipConvertParam<>("age", "result.age"))
                .setInOutConverter(new IEipInOutConverter() {
                    @Override
                    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
                        return inObject;
                    }
                })
                .and()
                .createResponseParamProcessor()
                .setInOutConverter(new IEipInOutConverter() {
                    @Override
                    public Object exchangeObject(ExtendedExchange exchange, Object inObject) throws Exception {
                        return inObject;
                    }
                })
                .and()
                .createExceptionParamProcessor()
                .and()
                .build());

        EipInterfaceContext.routeInitialization(context);

        context.start();

//        EipResult<SuperMap> result = EipInterfaceContext.call("testSQL", MapHelper.newInstance()
//                .put("sql", "select * from ts1")
//                .build());

        EipResult<SuperMap> result = EipInterfaceContext.call("testSQL", MapHelper.newInstance()
                .put("id", 1)
                .put("namea", "d")
                .put("nameb", "c")
                .build());

        System.out.println(result.getSuccess());

        // Sleeping until the end of time
        Thread.sleep(100000000000000000L);

        context.stop();
    }
}
