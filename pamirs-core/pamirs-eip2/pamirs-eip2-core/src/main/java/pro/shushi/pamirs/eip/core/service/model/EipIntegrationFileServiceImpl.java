package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.excel.*;
import pro.shushi.pamirs.eip.api.model.EipIntegrationFile;
import pro.shushi.pamirs.eip.api.service.EipIntegrationFileService;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationFileHeader;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeConversionService;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeConvertError;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.enmu.BaseEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate.EIP_FILE_EXCEL_FMT_ERROR;

/**
 * EipIntegrationFileServiceImpl
 *
 * @author yakir on 2024/10/30 20:20.
 */
@Slf4j
@Fun(EipIntegrationFileService.FUN_NAMESPACE)
@Component
public class EipIntegrationFileServiceImpl implements EipIntegrationFileService {

    @Autowired
    private ExcelTTypeConversionService conversionService;

    @Override
    @Function
    public EipIntegrationFile createOrUpdate(EipIntegrationFile data) {

        IWrapper<EipIntegrationFile> qw = Pops.<EipIntegrationFile>lambdaQuery()
                .from(EipIntegrationFile.MODEL_MODEL)
                .eq(EipIntegrationFile::getInterfaceName, data.getInterfaceName());

        EipIntegrationFile query = new EipIntegrationFile();
        EipIntegrationFile existed = query.queryOneByWrapper(qw);

        if (null != existed && existed.getId() > 0) {
            data.setId(existed.getId());
            data.updateById();
        } else {

            data = data.create();
        }

        return data;
    }

    @Override
    @Function
    public EipIntegrationFile queryById(Long id) {
        return new EipIntegrationFile().queryById(id);
    }

    @Override
    @Function
    public EipIntegrationFile queryByInterfaceName(String interfaceName) {
        IWrapper<EipIntegrationFile> qw = Pops.<EipIntegrationFile>lambdaQuery()
                .from(EipIntegrationFile.MODEL_MODEL)
                .eq(EipIntegrationFile::getInterfaceName, interfaceName);
        return new EipIntegrationFile().queryOneByWrapper(qw);
    }

    @Override
    @Function
    public EipExcel fetchData(String interfaceName, String sheet) {

        EipIntegrationFile eipFile = queryByInterfaceName(interfaceName);

        if (null == eipFile) {
            return null;
        }

        String excelUrl = eipFile.getUrl();
        ExcelReader reader = null;
        try {
            String ossName = excelUrl.replaceAll("https://", "")
                    .replaceAll("http://", "");
            ossName = StringUtils.substring(ossName, StringUtils.indexOf(ossName, "/") + 1);
            InputStream is = FileClientFactory.getClient().getDownloadStream(ossName);

            // 限制读取
            EipExcelReadListener readListener = new EipExcelReadListener();
            ExcelReaderBuilder readerBuilder = EasyExcel.read(is, readListener);

            reader = readerBuilder.build();

            List<ReadSheet> sheetList = reader.excelExecutor().sheetList();
            for (ReadSheet readSheet : sheetList) {
                if (StringUtils.isNotBlank(sheet)) {
                    if (!StringUtils.equals(readSheet.getSheetName(), sheet)) {
                        continue;
                    }
                }
                ExcelReader sheetReader = reader.read(readSheet);
            }

            EipExcel excel = readListener.getExcel();
            EipIntegrationFileHeader fileHeader = eipFile.getFileHeader();
            EipExcel fileHeaderEipExcel;
            if (fileHeader != null && (fileHeaderEipExcel = fileHeader.fetchEipExcel()) != null) {
                List<ExcelTTypeConvertError> convertErrorMessageHub = new ArrayList<>();

                for (EipExcelSheet excelSheet : excel.getSheets()) {
                    EipExcelSheet fileHeaderSheet = fileHeaderEipExcel.getSheet(excelSheet.getName());
                    if (fileHeaderSheet == null) continue;
                    List<EipExcelHead> headers = excelSheet.getHeaders();
                    List<EipExcelHead> dbHeaders = fileHeaderSheet.getHeaders();
                    Set<Integer> canReplaceHeaderIndex = new HashSet<>();
                    for (int row = 0; row < excelSheet.getData().size(); row++) {
                        EipExcelEntry data = excelSheet.getData().get(row);
                        String sheetName = excelSheet.getName();
                        List<String> dataValueList = data.getData();
                        for (int col = 0; col < dataValueList.size(); col++) {
                            EipExcelHead head = headers.get(col);
                            if (col >= dbHeaders.size()) continue;
                            EipExcelHead dbHead = dbHeaders.get(col);
                            if (!StringUtils.equals(dbHead.getName(), head.getName())) continue;

                            String dataValue = dataValueList.get(col);
                            ExcelTTypeDescriptor descriptor = ExcelTTypeDescriptor.valueOf(dataValue, head.getName(), head.getType(), dbHead.getType(), head.getFormat());
                            descriptor.setErrorMessageHub(convertErrorMessageHub);
                            descriptor.setSheetName(sheetName);
                            descriptor.setRowIndex(row);
                            descriptor.setColumnIndex(col);
                            dataValue = conversionService.convert(descriptor);
                            dataValueList.set(col, dataValue);
                            canReplaceHeaderIndex.add(col);
                        }

                        canReplaceHeaderIndex.forEach(i -> headers.set(i, dbHeaders.get(i)));
                    }

                    if (CollectionUtils.isNotEmpty(convertErrorMessageHub)) {
                        Map<String, List<ExcelTTypeConvertError>> sheetErrorMsgMap = convertErrorMessageHub.stream().collect(Collectors.groupingBy(ExcelTTypeConvertError::getSheetName));
                        sheetErrorMsgMap.forEach((sheetName, sheetErrorMsgList) -> {
                            if (CollectionUtils.isEmpty(sheetErrorMsgList)) {
                                return;
                            }
                            Map<String, List<ExcelTTypeConvertError>> colErrorMsgMap = sheetErrorMsgList.stream().collect(Collectors.groupingBy(ExcelTTypeConvertError::getName));
                            colErrorMsgMap.forEach((name, colErrorMsgList) -> {
                                if (CollectionUtils.isEmpty(colErrorMsgList)) {
                                    return;
                                }
                                String targetType = colErrorMsgList.get(0).getTargetType();
                                TtypeEnum targetTtype = BaseEnum.getEnumByValue(TtypeEnum.class, targetType);
                                String warnMsg = String.format("Sheet【%s】字段【%s】识别为【%s】失败：共 %s 条数据中有 %s 条格式不符合，已填充为 null",
                                        sheetName,
                                        name, targetTtype != null ? targetTtype.displayName() : targetType,
                                        excelSheet.getData().size(), colErrorMsgList.size()
                                );
                                PamirsSession.getMessageHub().warn("字段识别异常提醒：\n" + warnMsg);
                            });
                        });
                        convertErrorMessageHub.clear();
                    }
                }
            }

            return excel;
        } catch (ExcelRuntimeException ee) {
            log.error("EasyExcel转换异常", ee);
            throw PamirsException.construct(EIP_FILE_EXCEL_FMT_ERROR)
                    .errThrow();
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
    }
}
