package pro.shushi.pamirs.eip.core.service.model;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.api.excel.EipExcel;
import pro.shushi.pamirs.eip.api.excel.EipExcelHead;
import pro.shushi.pamirs.eip.api.excel.EipExcelSheet;
import pro.shushi.pamirs.eip.api.model.EipCallParam;
import pro.shushi.pamirs.eip.api.enmu.connector.ConnType;
import pro.shushi.pamirs.eip.jdbc.enumeration.EipJdbcExpEnumeration;
import pro.shushi.pamirs.eip.api.model.connector.EipConnector;
import pro.shushi.pamirs.eip.api.model.connector.EipConnectorResource;
import pro.shushi.pamirs.eip.api.service.model.EipConnectorResourceService;
import pro.shushi.pamirs.eip.jdbc.util.DbConnectionUtils;
import pro.shushi.pamirs.eip.jdbc.util.EipPatternUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.TypeProcessor;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EIP连接器资源服务实现
 *
 * @author Adamancy Zhang at 21:54 on 2025-02-26
 */
@Slf4j
@Service
@Fun(EipConnectorResourceService.FUN_NAMESPACE)
public class EipConnectorResourceServiceImpl implements EipConnectorResourceService {

    @Function
    @Override
    public EipConnectorResource queryByInterfaceName(String interfaceName) {
        return Models.origin().queryOneByWrapper(Pops.<EipConnectorResource>lambdaQuery()
                .from(EipConnectorResource.MODEL_MODEL)
                .eq(EipConnectorResource::getInterfaceName, interfaceName));
    }

    @Function
    @Override
    public EipConnectorResource prepareCall(EipConnectorResource resource, List<EipCallParam> params) {
        if (StringUtils.isBlank(resource.getInterfaceName())) {
            throw PamirsException.construct(EipJdbcExpEnumeration.EIP_DESIGNER_CHOOSE_DB_API)
                    .errThrow();
        }
        List<ModelField> modelFields = prepareCall0(resource, params);
        resource.setFields(modelFields);
        return resource;
    }

    private List<ModelField> prepareCall0(EipConnectorResource connectorResource, List<EipCallParam> params) {
        String interfaceName = connectorResource.getInterfaceName();

        IWrapper<EipConnectorResource> qw = Pops.<EipConnectorResource>lambdaQuery()
                .from(EipConnectorResource.MODEL_MODEL)
                .eq(EipConnectorResource::getInterfaceName, interfaceName);

        List<EipConnectorResource> resourceList = new EipConnectorResource().queryList(qw);

        if (CollectionUtils.isEmpty(resourceList)) {
            throw PamirsException.construct(EipJdbcExpEnumeration.EIP_DESIGNER_CHOOSE_DB_API)
                    .errThrow();
        }

        EipConnectorResource resource = resourceList.get(0);

        EipConnector connector = new EipConnector().queryById(resource.getConnectorId());

        ConnType connType = connector.getType();

        Map<String, EipCallParam> _params = Optional.ofNullable(params)
                .map(List::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toMap(EipCallParam::getKey, a -> a, (_a, _b) -> _a));

        if (ConnType.DB.equals(connType)) {
            return prepareCallDB(connector, resource, _params);
        } else if (ConnType.EXCEL.equals(connType)) {
            return prepareCallExcel(resource, _params);
        }
        return new ArrayList<>();
    }

    private List<ModelField> prepareCallDB(EipConnector connector, EipConnectorResource resource, Map<String, EipCallParam> callParamMap) {
        String sql = StringUtils.trim(resource.getSql());

        for (Map.Entry<String, EipCallParam> entry : callParamMap.entrySet()) {
            String val = entry.getValue().getValue();
            ParamTypeEnum paramTypeEnum = entry.getValue().getParamType();
            if (Lists.newArrayList(ParamTypeEnum.String, ParamTypeEnum.Date).contains(paramTypeEnum)) {
                val = String.format("'%s'", val);
            }
            if (StringUtils.isAnyBlank(entry.getKey(), val)) {
                log.error("值传递异常:[{}:{}]", entry.getKey(), entry.getValue());
                continue;
            }

            sql = sql.replaceAll("\\{" + entry.getKey() + "\\}", val);
        }

        Matcher matcher = EipPatternUtils.matcher(sql);
        StringBuilder paramErr = new StringBuilder();
        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            for (int i = 1; i <= groupCount; i++) {
                String group = matcher.group(i);
                paramErr.append(group);
                if (i != groupCount) {
                    paramErr.append(",");
                }
            }
        }

        if (paramErr.length() > 0) {
            log.error("param parse err, current sql:{}", sql);
            throw PamirsException.construct(EipJdbcExpEnumeration.EIP_DESIGNER_PRECALL_DB_API_ERROR)
                    .errThrow();
        }

        List<ModelField> mockFields = new ArrayList<>();
        try {
            String jdbcUrl = DbConnectionUtils.buildUrl(connector);
            Class.forName(connector.getConnDBType().getDriver());
            DriverManager.setLoginTimeout(50);
            Map<String, String> colMap = new HashMap<>();
            try (Connection conn = DriverManager.getConnection(jdbcUrl, connector.getUser(), connector.getPassword());
                 PreparedStatement statement = (StringUtils.containsIgnoreCase(sql, "call "))
                         ? conn.prepareCall(sql)
                         : conn.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery();) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                log.info("ResultSetMetaData:{}", metaData);
                int colCount = metaData.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    String colName = metaData.getColumnLabel(i);
                    String colType = metaData.getColumnClassName(i);
                    colMap.put(colName, colType);
                }
            }

            TypeProcessor typeProcessor = Spider.getDefaultExtension(TypeProcessor.class);

            for (Map.Entry<String, String> entry : colMap.entrySet()) {
                String ttypeStr = typeProcessor.defaultTtypeFromLtype(entry.getValue(), null, null);
                ModelField field = new ModelField();
                field = field.construct();
                String fieldName = entry.getKey();
                field.setName(fieldName);
                field.setField(fieldName);
                TtypeEnum ttype = TtypeEnum.getEnumByValue(TtypeEnum.class, ttypeStr);
                field.setTtype(ttype);
                mockFields.add(field);
            }

            return mockFields;

        } catch (ClassNotFoundException | SQLException e) {
            log.error("获取数据库连接失败！", e);
            return new ArrayList<>();
        }
    }

    private List<ModelField> prepareCallExcel(EipConnectorResource resource, Map<String, EipCallParam> callParamMap) {

        String preview = resource.getPreview();
        if (StringUtils.isBlank(preview)) {
            return new ArrayList<>();
        }

        String sheet = Optional.ofNullable(callParamMap.get("sheet"))
                .map(EipCallParam::getValue)
                .orElse(null);

        EipExcel excel = EipExcel.fromJson(preview);

        List<EipExcelSheet> sheets = excel.getSheets();

        if (CollectionUtils.isEmpty(sheets)) {
            return new ArrayList<>();
        }

        EipExcelSheet currentSheet = null;
        if (StringUtils.isBlank(sheet)) {
            currentSheet = sheets.get(0);
        } else {
            currentSheet = excel.getSheet(sheet);
        }

        if (null == currentSheet) {
            return new ArrayList<>();
        }

        List<EipExcelHead> headers = currentSheet.getHeaders();
        TypeProcessor typeProcessor = Spider.getDefaultExtension(TypeProcessor.class);

        List<ModelField> mockFields = new ArrayList<>();
        for (EipExcelHead head : headers) {
            String ttypeStr = typeProcessor.defaultTtypeFromLtype(head.getType(), null, null);
            ModelField field = new ModelField();
            field = field.construct();
            String fieldName = head.getName();
            field.setName(fieldName);
            field.setField(fieldName);
            TtypeEnum ttype = TtypeEnum.getEnumByValue(TtypeEnum.class, ttypeStr);
            field.setTtype(ttype);
            mockFields.add(field);
        }
        return mockFields;
    }
}
