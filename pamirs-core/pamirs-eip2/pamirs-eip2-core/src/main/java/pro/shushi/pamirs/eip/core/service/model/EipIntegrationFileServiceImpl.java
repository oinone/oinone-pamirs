package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.excel.*;
import pro.shushi.pamirs.eip.api.model.EipIntegrationFile;
import pro.shushi.pamirs.eip.api.service.EipIntegrationFileService;
import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationFileHeader;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeConversionService;
import pro.shushi.pamirs.eip.api.type.ExcelTTypeDescriptor;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                for (EipExcelSheet excelSheet : excel.getSheets()) {
                    EipExcelSheet fileHeaderSheet = fileHeaderEipExcel.getSheet(excelSheet.getName());
                    if (fileHeaderSheet == null) continue;
                    List<EipExcelHead> headers = excelSheet.getHeaders();
                    List<EipExcelHead> dbHeaders = fileHeaderSheet.getHeaders();
                    Set<Integer> canReplaceHeaderIndex = new HashSet<>();
                    for (EipExcelEntry data : excelSheet.getData()) {
                        List<String> dataValueList = data.getData();
                        for (int i = 0; i < dataValueList.size(); i++) {
                            EipExcelHead head = headers.get(i);
                            if (i >= dbHeaders.size()) continue;
                            EipExcelHead dbHead = dbHeaders.get(i);
                            if (!StringUtils.equals(dbHead.getName(), head.getName())) continue;

                            String dataValue = dataValueList.get(i);
                            dataValue = conversionService.convert(ExcelTTypeDescriptor.valueOf(dataValue != null ? dataValue : "", head.getType(), dbHead.getType(), head.getFormat()));
                            dataValueList.set(i, dataValue);
                            canReplaceHeaderIndex.add(i);
                        }

                        canReplaceHeaderIndex.forEach(i -> headers.set(i, dbHeaders.get(i)));
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
