package pro.shushi.pamirs.eip;

import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.eip.jdbc.check.exception.*;
import pro.shushi.pamirs.eip.jdbc.helper.SQLCheckHelper;

/**
 * SQLServer检查测试
 *
 * @author Adamancy Zhang at 09:38 on 2024-06-06
 */
@DisplayName("SQLServer检查测试")
public class SQLServerCheckHelperTest {

    @Test
    public void sqlServerSelectTest() {
        rawTest("select * from t1 where name like '%{a}%'");
        rawTest("select * from t1 where name not like '%{a}%'");
        rawTest("select * from t1 where name in ('{a}', '{b}')");
        rawTest("select * from t1 where id in ({a}, {b})");
        try {
            rawTest("select * from t1 where name like %{a}%");
            assert false;
        } catch (SQLCheckException e) {
            assert "ERROR. pos 34, line 1, column 34, token PERCENT".equals(e.getCause().getMessage());
        }
        rawTest("select * from t1 where id = {id}");
        rawTest("select * from ts1 where id > 0");
        try {
            rawTest("select * from ts1");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLSelectCheckException.createWhereIsNullException().getCode().equals(e.getCode()) : e.getCode();
        }
        try {
            rawTest("select * from base_action where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLCommonCheckException.createNotAllowOperationTableException("").getCode().equals(e.getCode()) : e.getCode();
        }
        try {
            rawTest("select * from (select * from base_action where id > 0) a where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLCommonCheckException.createNotAllowOperationTableException("").getCode().equals(e.getCode()) : e.getCode();
        }
        try {
            rawTest("select * from (select * from db1.base_action a where id > 0) a where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLCommonCheckException.createNotAllowOperationTableException("").getCode().equals(e.getCode()) : e.getCode();
        }
        try {
            rawTest("select * from (select * from [db1].[base_action] a where id > 0) a where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLCommonCheckException.createNotAllowOperationTableException("").getCode().equals(e.getCode()) : e.getCode();
        }
        rawTest("select * from t1 where id > 0 limit {pageSize} offset {currentPage}");
        rawTest("select field0175 as id, field0002 as jobNum,field0006 as name,field0200 as departmentId,field0340 as employeeNature,field0062 as inductionTutorId,field0059 as city,field0003 as enterDay,field0080 as internshipStartDate,field0081 as internshipEndDate,field0043 as laborContractStartDate,field0044 as laborContractEndDate,field0266 as renewContractNum,field0173 as fulltimeDate,field0020 as contactInfo,field0013 as idCard,field0007 as sex,field0016 as graduationDate,field0050 as socialSecurityAddr,field0192 as departureDate,field0301 as departureReason,field0058 as jobNature,field0196 as corpEmail from hrmView where field0175 is not null limit 0,500;");
        rawTest("select field0175 as id, field0002 as jobNum,field0006 as name,field0200 as departmentId,field0340 as employeeNature,field0062 as inductionTutorId,field0059 as city,field0003 as enterDay,field0080 as internshipStartDate,field0081 as internshipEndDate,field0043 as laborContractStartDate,field0044 as laborContractEndDate,field0266 as renewContractNum,field0173 as fulltimeDate,field0020 as contactInfo,field0013 as idCard,field0007 as sex,field0016 as graduationDate,field0050 as socialSecurityAddr,field0192 as departureDate,field0301 as departureReason,field0180zw as status,field0058 as jobNature,field0196 as corpEmail from hrmView where field0175 is not null;");
        rawTest("select id, name ,parentunitid,code,status from orgunitView where id!=0");
        rawTest("select oaid from test where oaid is not null");
    }

    @Test
    public void sqlServerInsertTest() {
        rawTest("insert into ts1(id, namea, nameb, age) values({id}, {namea}, {nameb}, {age})");
        rawTest("insert into ts1(id, namea, nameb, age)\n -- 这是一段注释\n" +
                "select id, namea, nameb, age from ts1 where id > 0;");
        try {
            rawTest("insert into ts1(id, namea, nameb, age)\n -- 这是一段注释\n" +
                    "select id, namea, nameb, age from ts1 where id > 0;\n" +
                    "select * from ts1 where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLParseCheckException.createSingleSQLException().getCode().equals(e.getCode()) : e.getCode();
        }
    }

    @Test
    public void sqlServerUpdateTest() {
        rawTest("update ts1 set namea = {namea} where id = {id}");
        try {
            rawTest("update ts1 set namea = {namea}");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLUpdateCheckException.createWhereIsNullException().getCode().equals(e.getCode()) : e.getCode();
        }
        rawTest("update formmain_2146 set field0197 = '-8269006013106663840' where ID = {oaId}");
        rawTest("update formmain_2146 set field0197 = '-7086773742814412536' where ID = {oaId}");
    }

    @Test
    public void sqlServerDeleteTest() {
        rawTest("delete from ts1 where id = {id}");
        try {
            rawTest("delete from ts1");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLDeleteCheckException.createWhereIsNullException().getCode().equals(e.getCode()) : e.getCode();
        }
    }

    @Test
    public void sqlServerExecTest() {
        rawTest("EXEC usp_dy_zbfx_hz '{year_s}','{year_e}','{zbid}','{yykey}','{kskey}','{yskey}','{sqlwhere}','{ylzkey}','{datatype}','{resultType}','{aax}';");
    }

    private String rawTest(String sql) {
        String target = SQLCheckHelper.checkSingle(sql, JdbcUtils.SQL_SERVER.name());
        assert !target.contains("#");
        System.out.println(target);
        return target;
    }
}
