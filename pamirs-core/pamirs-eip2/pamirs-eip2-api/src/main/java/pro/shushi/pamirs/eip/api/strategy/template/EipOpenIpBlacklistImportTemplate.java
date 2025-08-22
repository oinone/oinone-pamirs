package pro.shushi.pamirs.eip.api.strategy.template;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.eip.api.model.EipApplication;
import pro.shushi.pamirs.eip.api.pmodel.EipOpenIpBlacklistProxy;
import pro.shushi.pamirs.eip.api.service.model.EipApplicationService;
import pro.shushi.pamirs.eip.api.util.EipIpUtil;
import pro.shushi.pamirs.file.api.builder.SheetDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.TypefaceDefinitionBuilder;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.file.api.enmu.ExcelVerticalAlignmentEnum;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelImportDataExtPointImpl;
import pro.shushi.pamirs.file.api.extpoint.ExcelImportDataExtPoint;
import pro.shushi.pamirs.file.api.format.RichTextFormat;
import pro.shushi.pamirs.file.api.model.ExcelImportTask;
import pro.shushi.pamirs.file.api.model.ExcelTypefaceDefinition;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.model.TaskMessage;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.Tx;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;

import java.util.*;

import static java.util.stream.Collectors.toSet;
import static pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate.*;
import static pro.shushi.pamirs.eip.api.pmodel.EipOpenIpBlacklistProxy.MODEL_MODEL;
import static pro.shushi.pamirs.file.api.enmu.ExcelAnalysisTypeEnum.FIXED_HEADER;
import static pro.shushi.pamirs.file.api.enmu.ExcelDirectionEnum.HORIZONTAL;
import static pro.shushi.pamirs.file.api.enmu.TaskMessageLevelEnum.ERROR;

/**
 * @author yeshenyue on 2025/5/9 19:37.
 */
@Slf4j
@Component
@Ext(ExcelImportTask.class)
public class EipOpenIpBlacklistImportTemplate extends AbstractExcelImportDataExtPointImpl<List<EipOpenIpBlacklistProxy>> implements ExcelTemplateInit, ExcelImportDataExtPoint<List<EipOpenIpBlacklistProxy>> {

    public static final String TEMPLATE_NAME = "开放应用黑名单导入";

    @Autowired
    private EipApplicationService eipApplicationService;

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        WorkbookDefinitionBuilder builder = WorkbookDefinitionBuilder.newInstance(MODEL_MODEL, TEMPLATE_NAME)
                .setDisplayName(TEMPLATE_NAME).setEachImport(false);
        SheetDefinitionBuilder sheetBuilder = builder.createSheet().setName(TEMPLATE_NAME);

        ExcelTypefaceDefinition boldType = TypefaceDefinitionBuilder.newInstance().setBold(true).build();
        ExcelTypefaceDefinition redType = TypefaceDefinitionBuilder.newInstance().setBold(true).setColor(0xa).build();

        sheetBuilder.createBlock(MODEL_MODEL, FIXED_HEADER, HORIZONTAL, "A1:D3")
                .createMergeRange("A1:D1")
                .setPresetNumber(10)
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle()
                        .setWrapText(true)
                        .setVerticalAlignment(ExcelVerticalAlignmentEnum.TOP)
                        .setHeight(1100))
                .createCell()
                .setValue("暂不支持IPv6地址。\n" +
                        "状态码：被IP黑名单规则限制时的响应状态码，默认403。\n" +
                        "响应消息：被IP黑名单规则限制时的响应消息。")
                .and().createCell().and().createCell().and().createCell().and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle())
                .setIsConfig(true)
                .createCell()
                .setField(LambdaUtil.fetchFieldName(EipOpenIpBlacklistProxy::getApplicationCode))
                .setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell()
                .setField(LambdaUtil.fetchFieldName(EipOpenIpBlacklistProxy::getIp))
                .setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell()
                .setField(LambdaUtil.fetchFieldName(EipOpenIpBlacklistProxy::getHttpCode))
                .setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .createCell()
                .setField(LambdaUtil.fetchFieldName(EipOpenIpBlacklistProxy::getHttpResult))
                .setStyleBuilder(ExcelHelper.createDefaultStyle().setWidth(6000)).and()
                .and()
                .createHeader()
                .setStyleBuilder(ExcelHelper.createDefaultStyle())
                .createCell()
                .setValue("*开放应用编码")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING)
                .setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 7, boldType))
                        .add(new RichTextFormat(0, 1, redType))
                        .build()))
                .and()
                .createCell()
                .setValue("*IP/IP网段")
                .setType(ExcelValueTypeEnum.RICH_TEXT_STRING)
                .setFormat(JSONArray.toJSONString(CollectionHelper.<RichTextFormat>newInstance()
                        .add(new RichTextFormat(0, 8, boldType))
                        .add(new RichTextFormat(0, 1, redType))
                        .build()))
                .and()
                .createCell()
                .setValue("响应状态码")
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(true)))
                .and()
                .createCell()
                .setValue("响应结果")
                .setStyleBuilder(ExcelHelper.createDefaultStyle(v -> v.setBold(true)))
                .and()
                .createCell();
        return Collections.singletonList(builder.build());
    }

    @Override
    @ExtPoint.Implement(expression = "importContext.definitionContext.model == \"" + EipOpenIpBlacklistProxy.MODEL_MODEL + "\" && importContext.definitionContext.name == \"" + TEMPLATE_NAME + "\"")
    public Boolean importData(ExcelImportContext importContext, List<EipOpenIpBlacklistProxy> dataList) {
        ExcelImportTask importTask = importContext.getImportTask();
        if (CollectionUtils.isEmpty(dataList)) {
            importTask.addTaskMessage(ERROR, "导入内容不能为空");
            return Boolean.FALSE;
        }

        // 校验表格数据
        boolean valid = validateRows(importTask, dataList);
        if (!valid) {
            return Boolean.FALSE;
        }

        // 校验应用是否存在
        if (!verifyApplications(importTask, dataList)) {
            return Boolean.FALSE;
        }

        List<EipOpenIpBlacklistProxy> latestRows = deduplicateKeepLast(dataList);

        // 批量创建/更新
        Tx.build().executeWithoutResult(status -> Models.data().createOrUpdateBatch(latestRows));
        return Boolean.TRUE;
    }

    private boolean validateRows(ExcelImportTask importTask, List<EipOpenIpBlacklistProxy> rows) {
        boolean isValid = true;
        for (int i = 0; i < rows.size(); i++) {
            EipOpenIpBlacklistProxy row = rows.get(i);

            if (StringUtils.isBlank(row.getApplicationCode())) {
                addTaskMessage(importTask, buildRowErrorMsg(i, "开放应用编码不能为空"), i);
                isValid = false;
            } else {
                row.setApplicationCode(row.getApplicationCode().trim());
            }

            if (StringUtils.isBlank(row.getIp())) {
                addTaskMessage(importTask, buildRowErrorMsg(i, EIP_IP_NULL_ERROR.getMsg()), i);
                isValid = false;
            } else if (EipIpUtil.isIllegalIp(row.getIp())) {
                addTaskMessage(importTask, buildRowErrorMsg(i, EIP_IP_CIDR_ILLEGAL.getMsg()), i);
                isValid = false;
            } else {
                row.setIp(row.getIp().trim());
            }

            if (row.getHttpCode() != null && (row.getHttpCode() < 100 || row.getHttpCode() > 599)) {
                addTaskMessage(importTask, buildRowErrorMsg(i, EIP_HTTP_CODE_VALUE_ILLEGAL.getMsg()), i);
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean verifyApplications(ExcelImportTask importTask, List<EipOpenIpBlacklistProxy> rows) {
        Set<String> codes = rows.stream().map(EipOpenIpBlacklistProxy::getApplicationCode)
                .filter(StringUtils::isNotBlank).map(String::trim).collect(toSet());

        List<EipApplication> apps = eipApplicationService.queryByCodes(new ArrayList<>(codes));
        Set<String> found = apps.stream().map(EipApplication::getCode).collect(toSet());

        for (String code : codes) {
            if (!found.contains(code)) {
                Integer idx = fetchCodeErrorIndex(code, rows);
                addTaskMessage(importTask, String.format("应用编码【%s】未查询到开放应用", code), idx);
                return false;
            }
        }
        return true;
    }

    private List<EipOpenIpBlacklistProxy> deduplicateKeepLast(List<EipOpenIpBlacklistProxy> rows) {
        Map<String, EipOpenIpBlacklistProxy> map = new LinkedHashMap<>();
        for (EipOpenIpBlacklistProxy row : rows) {
            String key = row.getApplicationCode() + "|" + row.getIp();
            map.put(key, row);
        }
        return new ArrayList<>(map.values());
    }

    private static String buildRowErrorMsg(int idx, String errorMsg) {
        return "第" + (idx + 3) + "行" + errorMsg;
    }

    private Integer fetchCodeErrorIndex(String code, List<EipOpenIpBlacklistProxy> rows) {
        for (int i = 0; i < rows.size(); i++) {
            if (code.equals(rows.get(i).getApplicationCode())) {
                return i;
            }
        }
        return null;
    }

    private void addTaskMessage(ExcelImportTask importTask, String message, Integer rowIndex) {
        List<TaskMessage> taskMessages = importTask.getMessages();
        if (taskMessages == null) {
            taskMessages = new ArrayList<>();
            importTask.setMessages(taskMessages);
        }
        TaskMessage taskMessage = new TaskMessage().setLevel(ERROR)
                .setMessage(message)
                .setRecordDate(new Date());
        if (rowIndex != null) {
            taskMessage.setRowIndex(rowIndex + 3);
        }
        taskMessages.add(taskMessage);
    }
}
