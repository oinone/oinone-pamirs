package pro.shushi.pamirs.framework.rsql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLHelper;
import pro.shushi.pamirs.framework.gateways.rsql.RSQLNodeInfo;
import pro.shushi.pamirs.framework.gateways.rsql.RsqlSearchOperation;
import pro.shushi.pamirs.framework.gateways.rsql.connector.RSQLNodeConnector;
import pro.shushi.pamirs.framework.gateways.rsql.visitor.NormalRSQLParseVisitor;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;

import java.util.ArrayList;
import java.util.List;

/**
 * RSQL帮助类测试
 *
 * @author Adamancy Zhang at 09:24 on 2021-10-21
 */
@DisplayName("RSQL帮助类测试")
public class RSQLHelperTest {

    @Test
    public void test1() {
        String rsql = "1==1 and createDate >= '2021-06-02 11:46:36' and createDate <= '2021-06-02 11:46:36' or (writeDate >= '2021-06-02 11:46:36' and writeDate <= '2021-06-02 11:46:36')";

        ModelDefinition modelDefinition = new ModelDefinition();
        List<ModelField> modelFields = new ArrayList<>();
        modelFields.add((ModelField) new ModelField().setField("createDate"));
        modelFields.add((ModelField) new ModelField().setField("writeDate"));
        modelDefinition.setModelFields(modelFields);

        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), null);
        System.out.println(rsql);
        rsql = RSQLHelper.toRsql(root);
        System.out.println(rsql);
    }

    @Test
    public void test2() {
        String rsql = "((1==1) and ((1==1) and (unStore=like='%老邓头%'))) and ((1==1))";

        ModelDefinition modelDefinition = new ModelDefinition();
        List<ModelField> modelFields = new ArrayList<>();
        modelFields.add((ModelField) new ModelField().setField("createDate"));
        modelFields.add((ModelField) new ModelField().setField("writeDate"));
        modelDefinition.setModelFields(modelFields);

        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), null);
        System.out.println(rsql);
        rsql = RSQLHelper.toTargetString(root, new RSQLNodeConnector() {
            @Override
            public String comparisonConnector(RSQLNodeInfo nodeInfo) {
                if ("unStore".equals(nodeInfo.getField())) {
                    nodeInfo.setField("name");
                    nodeInfo.setOperator(RsqlSearchOperation.IN.getOperator());
                    nodeInfo.getArguments().add("cpc");
                }
                return super.comparisonConnector(nodeInfo);
            }
        });
        System.out.println(rsql);
        root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), null);
        System.out.println(RSQLHelper.toRsql(root));
    }

    @Test
    public void test3() {
        String rsql = "(name=in=('SysSettingMenus_GlobalMenu_CorporateImageMenu') and module == 'sys_setting')";
        TreeNode<RSQLNodeInfo> root = RSQLHelper.parse(rsql, new NormalRSQLParseVisitor<>(), rsql);
        String sql = RSQLHelper.toSql(root);
        System.out.println(sql);
    }
}
