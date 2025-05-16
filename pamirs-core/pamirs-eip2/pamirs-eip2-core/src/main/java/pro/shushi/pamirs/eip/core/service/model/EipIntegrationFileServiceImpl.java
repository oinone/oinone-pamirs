package pro.shushi.pamirs.eip.core.service.model;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.exception.ExcelRuntimeException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.excel.EipExcel;
import pro.shushi.pamirs.eip.api.excel.EipExcelReadListener;
import pro.shushi.pamirs.eip.api.model.EipIntegrationFile;
import pro.shushi.pamirs.eip.api.service.EipIntegrationFileService;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.io.InputStream;
import java.util.List;

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
            return readListener.getExcel();
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
