package pro.shushi.pamirs.user.core.base.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.user.api.model.PamirsUser;
import pro.shushi.pamirs.user.core.base.pmodel.PamirsUserProxy;

import java.util.Collections;
import java.util.List;

/**
 * @author: Wuxin
 * @createTime: 2024/06/11 17:29
 */
@Component
public class PamirsUserTemplate implements ExcelTemplateInit {
    public static final String TEMPLATE_NAME = "pamirsUserTemplate";

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(PamirsUser.MODEL_MODEL, TEMPLATE_NAME).setDisplayName("用户-导入导出");
        PamirsUserTemplate.createRoleTypeSheet(builder);
        return Collections.singletonList(builder.build());
    }

    public static void createRoleTypeSheet(WorkbookDefinitionBuilder builder) {
        builder.setEachImport(true)
                .createSheet().setName("用户")
                .createBlock(PamirsUserProxy.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL, "A1:H3")
                .createMergeRange("A1:H1")
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("name").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("gender").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("NULL", "未知")
                        .put("MALE", "男")
                        .put("FEMALE", "女")
                        .build())).and()
                .createCell().setField("login").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("active").setType(ExcelValueTypeEnum.BOOLEAN).setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("initialPassword").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("contactPhone").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("contactEmail").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell().setField("roleCode").setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and().and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP).setHeight(2200))
                .createCell().setValue("1.红色为必填字段。\n" +
                        "2.若存在多个“角色编码”用英文 “;” 分割，请确保填写的每个被绑定的编码都是系统中已存在的有效编码。\n" +
                        "3.非中国号码务必加上国家代码以及+号，例如美国号码：+1**********。\n" +
                        "4.是否激活账号，填写“是”或留空表示激活，填写“否”表示不激活。\n" +
                        "5.初始密码需为8至32位；如未填写，则默认为“Abcd@1234”。\n" +
                        "6.此处的联系电话和邮箱可用于手机验证码登录和邮箱登录。\n" +
                        "7.登录账号和初始密码的作用是用于创建新用户或绑定现有用户。")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 234, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).build()))
                        .add(new RichTextFormat(0, 9, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .createCell().and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(Boolean.TRUE))
                        .setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("名称")
                .setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setSize(18)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 2, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("性别").and()
                .createCell().setValue("登录账号")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING).setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 4, TypefaceDefinitionBuilder.newInstance().setBold(Boolean.TRUE).setColor(0xa).build()))
                        .build()))
                .and()
                .createCell().setValue("是否激活账号").and()
                .createCell().setValue("初始密码").and()
                .createCell().setValue("联系电话").and()
                .createCell().setValue("联系邮箱").and()
                .createCell().setValue("角色编码");
    }

}
