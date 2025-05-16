package pro.shushi.pamirs.eip;

import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.eip.jdbc.check.exception.*;
import pro.shushi.pamirs.eip.jdbc.helper.SQLCheckHelper;

/**
 * PG检查测试
 *
 * @author Adamancy Zhang at 09:38 on 2024-06-06
 */
@DisplayName("PG检查测试")
public class PGCheckHelperTest {

    @Test
    public void pgSelectTest() {
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
        rawTest("select * from ts1 where id = {id}");
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
            rawTest("select * from (select * from \"db1\".\"base_action\" a where id > 0) a where id > 0");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLCommonCheckException.createNotAllowOperationTableException("").getCode().equals(e.getCode()) : e.getCode();
        }
        rawTest("select * from t1 where id > 0 limit {pageSize} offset {currentPage}");
        rawTest("select apply_id::varchar as applyId from ts1 where is_sync = 0 and report_url is not nul and bk_status = 3");
        rawTest("select report_url,apply_id::varchar as applyId from curriculum_vitae where is_sync = 0 and report_url is not null and bk_status = 3");
        rawTest("update curriculum_vitae set report_url = {reportUrl},bk_status = 3 where arrangement_id = {arrangementId}");
        rawTest("update curriculum_vitae set report_url = {reportUrl},score = {score},total_score = {totalScore},bk_status = 3,bk_result = {bkResult} where arrangement_id = {arrangementId}");
        rawTest("select arrangement_id from curriculum_vitae where arrangement_id is not null and bk_status = 2");
        rawTest("update curriculum_vitae set arrangement_id = {arrangementId},bk_status = 2 where id = {id}");
        rawTest("select id,aims,name,mobile_phone,email from curriculum_vitae where bk_status = 1");
        rawTest("select id,name,gender,mobile_phone,email,id_number,aims,apply_id,order_no,report_url,score,total_score,bk_status,bk_result from curriculum_vitae where apply_id={applyId}");
        rawTest("INSERT INTO curriculum_vitae(name,gender,mobile_phone,email,aims,apply_id,bk_status) VALUES ({name},{gender},{mobilePhone},{email},{aims},{applyId},{bkStatus})");
    }

    @Test
    public void pgInsertTest() {
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
    public void pgUpdateTest() {
        rawTest("update ts1 set namea = {namea} where id = {id}");
        try {
            rawTest("update ts1 set namea = {namea}");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLUpdateCheckException.createWhereIsNullException().getCode().equals(e.getCode()) : e.getCode();
        }
    }

    @Test
    public void pgDeleteTest() {
        rawTest("delete from ts1 where id = {id}");
        try {
            rawTest("delete from ts1");
            assert false;
        } catch (SQLCheckException e) {
            assert SQLDeleteCheckException.createWhereIsNullException().getCode().equals(e.getCode()) : e.getCode();
        }
    }

    @Test
    public void mergeInfoTest() {
        rawTest("MERGE INTO business_pamirs_employee_copy1 AS target\n" +
                "USING (SELECT * FROM business_pamirs_employee_copy1 where id > 0) AS source ON (target.id = id)\n" +
                "WHEN MATCHED THEN\n" +
                "UPDATE SET target.code = {code}\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT(id,code) VALUES ({id},{code});");

        rawTest("merge into a_merge a\n" +
                "using (select b.aid,b.name,b.year from b_merge b where id > 0) c on (a.id=c.aid)\n" +
                "when matched then\n" +
                "    update set year=c.year\n" +
                "when not matched then\n" +
                "    insert values(c.aid,c.name,c.year);");
    }

    private String rawTest(String sql) {
        String target = SQLCheckHelper.checkSingle(sql, JdbcUtils.POSTGRESQL);
        assert !target.contains("#");
        System.out.println(target);
        return target;
    }
}
