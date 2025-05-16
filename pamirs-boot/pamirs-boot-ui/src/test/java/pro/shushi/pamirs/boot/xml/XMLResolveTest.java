package pro.shushi.pamirs.boot.xml;

import org.junit.jupiter.api.Test;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.framework.orm.xml.feature.PamirsXmlParserFeature;

/**
 * XML解析测试
 *
 * @author Adamancy Zhang at 11:38 on 2025-04-14
 */
public class XMLResolveTest {

    private static final String template1 = "<view model=\"testPrint.k2.Model0000000001\" type=\"table\">\n" +
            "    <metadata>\n" +
            "        <model model=\"testPrint.k2.Model0000000001\">\n" +
            "            <field field=\"field174437441409800984Ccb\" lname=\"field174437441409800984Ccb\" multi=\"false\" name=\"field174437441409800984Ccb\" related=\"field1744281119344gdhg,code\" relatedTtype=\"STRING\" relationStore=\"false\" store=\"false\" ttype=\"RELATED\"/>\n" +
            "            <field field=\"field06d745428f9d4fc0818cb14ff0b6d0a0\" lname=\"field06d745428f9d4fc0818cb14ff0b6d0a0\" multi=\"false\" name=\"field06d745428f9d4fc0818cb14ff0b6d0a0\" related=\"field00008,name\" relatedTtype=\"STRING\" ttype=\"RELATED\"/>\n" +
            "            <field field=\"field1744281119344gdhg\" lname=\"field1744281119344gdhg\" name=\"field1744281119344gdhg\" referenceFields=\"id\" references=\"testPrint.k2.Model0000000003\" relationFields=\"field00008id\" ttype=\"M2O\"/>\n" +
            "        </model>\n" +
            "    </metadata>\n" +
            "    <template slot=\"search\">\n" +
            "        <field allowClear=\"true\" allowSearch=\"true\" autoFillOptions=\"true\" colSpan=\"HALF\" data=\"field174437441409800984Ccb\" disabled=\"false\" invisible=\"false\" label=\"编码\" patternType=\"NONE\" readonly=\"false\" required=\"false\" showCount=\"false\" type=\"TEXT\" widget=\"Input\"/>\n" +
            "    </template>\n" +
            "    <template slot=\"tableGroup\"/>\n" +
            "    <template slot=\"actionBar\">\n" +
            "        <action disabled=\"false\" invisible=\"false\" keyboard-key=\"F1\" label=\"创建\" name=\"uiView87483bd729fb42d2a44c2ca1218a9dca\" readonly=\"false\" required=\"false\"/>\n" +
            "        <action disabled=\"false\" invisible=\"false\" label=\"导入\" name=\"internalGotoListImportDialog\" readonly=\"false\" required=\"false\"/>\n" +
            "        <action disabled=\"false\" invisible=\"false\" label=\"导出\" name=\"internalGotoListExportDialog\" readonly=\"false\" required=\"false\"/>\n" +
            "        <action disabled=\"false\" invisible=\"false\" label=\"添加一行\" name=\"uiCliented1a7a21b7154e19a1df5c9fe2e51406\" readonly=\"false\" required=\"false\" type=\"DEFAULT\"/>\n" +
            "        <action disabled=\"false\" invisible=\"false\" label=\"复制一行\" name=\"uiClient7ef0d02b22f34a5bba82aff27f056cd3\" readonly=\"false\" required=\"false\" type=\"DEFAULT\"/>\n" +
            "    </template>\n" +
            "    <template autoColumnWidth=\"false\" checkbox=\"true\" colSpan=\"FULL\" defaultPageSize=\"OPTION_2\" disabled=\"false\" editorCloseTrigger=\"AUTO\" editorMode=\"CELL\" editorTrigger=\"DBLCLICK\" enableSequence=\"false\" filter=\"\" inlineActiveCount=\"THREE\" invisible=\"false\" readonly=\"false\" required=\"false\" slot=\"table\">\n" +
            "        <field data=\"code\" disabled=\"false\" invisible=\"false\" readonly=\"false\" required=\"false\" widget=\"Input\"/>\n" +
            "        <field allowClear=\"true\" autoFillOptions=\"true\" colSpan=\"HALF\" data=\"field06d745428f9d4fc0818cb14ff0b6d0a0\" disabled=\"false\" independentlyEditable=\"true\" invisible=\"false\" label=\"名称名称名称名称名称名称名称名称名称名称\" patternType=\"NONE\" readonly=\"false\" required=\"false\" showCount=\"false\" type=\"TEXT\" widget=\"Input\"/>\n" +
            "        <field allowClear=\"true\" autoFillOptions=\"true\" colSpan=\"HALF\" data=\"createDate\" disabled=\"false\" invisible=\"false\" label=\"创建时间\" readonly=\"false\" required=\"false\" widget=\"DateTimePicker\"/>\n" +
            "        <field data=\"writeDate\" disabled=\"false\" invisible=\"false\" readonly=\"false\" required=\"false\" widget=\"DateTimePicker\"/>\n" +
            "        <template slot=\"rowActions\">\n" +
            "            <action disabled=\"false\" invisible=\"false\" label=\"编辑\" name=\"uiView53f469ddd2494139882654db21514499\" readonly=\"false\" required=\"false\"/>\n" +
            "            <action closeAllDialog=\"true\" closeAllDrawer=\"true\" closeDialog=\"true\" closeDrawer=\"true\" disabled=\"false\" goBack=\"false\" invisible=\"false\" label=\"删除\" name=\"delete\" readonly=\"false\" refreshData=\"true\" required=\"false\" validateForm=\"true\"/>\n" +
            "        </template>\n" +
            "        <field data=\"id\" invisible=\"true\"/>\n" +
            "    </template>\n" +
            "</view>\n";

    private static final String template = "<view name=\"formView\" type=\"FORM\" cols=\"2\" model=\"expenses.TestConstraintsModel\">\n" +
            "    <template slot=\"actions\" autoFill=\"true\"/>\n" +
            "    <template slot=\"fields\">\n" +
            "        <pack widget=\"group\" title=\"基础信息\">\n" +
            "            <field span=\"1\" invisible=\"true\" priority=\"5\" data=\"id\" label=\"ID\" readonly=\"true\"/>\n" +
            "            <field span=\"1\" priority=\"101\" data=\"name\" label=\"名称\" />\n" +
            "            <field span=\"1\" priority=\"102\" data=\"age\" label=\"年龄\" validator=\"!IS_NULL(activeRecord.age) &amp;&amp; (activeRecord.age &gt;=0 &amp;&amp; activeRecord.age &lt;= 200)\" validatorMessage=\"年龄为必填项，且年龄只能在0-200之间\"/>\n" +
            "            <field span=\"1\" invisible=\"true\" priority=\"200\" data=\"createDate\" label=\"创建时间\" readonly=\"true\"/>\n" +
            "            <field span=\"1\" invisible=\"true\" priority=\"210\" data=\"writeDate\" label=\"更新时间\" readonly=\"true\"/>\n" +
            "            <field span=\"1\" priority=\"220\" data=\"createUid\" label=\"创建人ID\"/>\n" +
            "            <field span=\"1\" priority=\"230\" data=\"writeUid\" label=\"更新人ID\"/>\n" +
            "        </pack>\n" +
            "    </template>\n" +
            "</view>";

    @Test
    public void test() {
        System.out.println(PamirsXmlParserFeature.class.getName());
        System.out.println(org.xmlpull.v1.XmlPullParserException.class.getName());
        UIView view = (UIView) ViewXmlUtils.fromXML(template);
        System.out.println(1);
    }
}
