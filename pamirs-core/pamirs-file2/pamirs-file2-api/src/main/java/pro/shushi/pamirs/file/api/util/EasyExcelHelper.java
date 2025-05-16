package pro.shushi.pamirs.file.api.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.CollectionHelper;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.easyexcel.converter.EasyExcelSqlDateConverter;
import pro.shushi.pamirs.file.api.easyexcel.converter.EasyExcelTimeConverter;
import pro.shushi.pamirs.file.api.easyexcel.converter.EasyExcelTimestampConverter;
import pro.shushi.pamirs.file.api.easyexcel.impl.DefaultEasyExcelWriteHandler;
import pro.shushi.pamirs.file.api.enmu.ExcelTypeCharsetEnum;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.model.ResourceMajorConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.*;

/**
 * @author Adamancy Zhang
 * @date 2021-01-14 21:50
 */
public class EasyExcelHelper {

    private static final List<Converter<?>> CUSTOM_CONVERTER_LIST = CollectionHelper.<Converter<?>>newInstance()
            .add(new EasyExcelSqlDateConverter())
            .add(new EasyExcelTimestampConverter())
            .add(new EasyExcelTimeConverter())
            .build();

    public static ExcelWriterBuilder generatorWriteBuilder(OutputStream outputStream, InputStream templateInputStream, DefaultEasyExcelWriteHandler writeHandler) {
        ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream)
                .registerWriteHandler(writeHandler)
                .withTemplate(templateInputStream);
        for (Converter<?> customConverter : CUSTOM_CONVERTER_LIST) {
            writerBuilder.registerConverter(customConverter);
        }
        writeHandler.extendBuilder(writerBuilder);
        return writerBuilder;
    }

    public static ExcelWriterBuilder cloneRegister(ExcelWriterBuilder builder, ExcelWriter writer) {
        WriteWorkbook writeWorkbook = writer.writeContext().writeWorkbookHolder().getWriteWorkbook();
        List<WriteHandler> writeHandlers = writeWorkbook.getCustomWriteHandlerList();
        if (CollectionUtils.isNotEmpty(writeHandlers)) {
            for (WriteHandler writeHandler : writeHandlers) {
                builder.registerWriteHandler(writeHandler);
            }
        }
        List<Converter<?>> converters = writeWorkbook.getCustomConverterList();
        if (CollectionUtils.isNotEmpty(converters)) {
            for (Converter<?> converter : converters) {
                builder.registerConverter(converter);
            }
        }
        return builder;
    }

    public static String getErrorMessage(Throwable throwable) {
        String errorMessage;
        Throwable cause;
        if (throwable instanceof ExcelAnalysisException || throwable.getCause() instanceof ExcelAnalysisException) {
            cause = throwable.getCause();
        } else {
            cause = throwable;
        }
        if (cause instanceof PamirsException) {
            int code = ((PamirsException) cause).getCode();
            if (BASE_FUNCTION_MANAGEMENT_ERROR.value().equals(code) ||
                    BASE_FUNCTION_MANAGEMENT2_ERROR.value().equals(code) ||
                    BASE_FUNCTION_MANAGEMENT3_ERROR.value().equals(code) ||
                    BASE_FUNCTION_MANAGEMENT4_ERROR.value().equals(code)) {
                cause = cause.getCause();
            }
        }
        if (cause instanceof PamirsException) {
            PamirsException pamirsException = (PamirsException) cause;
            errorMessage = pamirsException.getType() + "-" + pamirsException.getCode() + ": " + pamirsException.getMessage();
        } else {
            errorMessage = cause.getMessage();
        }
        return errorMessage;
    }

    public static ExcelReaderBuilder generatorReadBuilder(InputStream inputStream) throws IOException {
        ExcelReaderBuilder readerBuilder = EasyExcel.read(inputStream);
        ExcelTypeCharsetEnum typeCharset = ExcelTypeCharsetEnum.recognitionExcelType(inputStream);
        if (typeCharset != null) {
            readerBuilder.excelType(typeCharset.type());
            Charset charset = typeCharset.charset();
            if (charset == null) {
                if (ExcelTypeCharsetEnum.CSV.equals(typeCharset)) {
                    readerBuilder.charset(Charset.forName(Optional.ofNullable(new ResourceMajorConfig().singletonModel())
                            .map(ResourceMajorConfig::getDefaultCsvCharset)
                            .filter(StringUtils::isNotBlank)
                            .orElse(FileConstant.CSV_IMPORT_CHARSET)));
                }
            } else {
                readerBuilder.charset(charset);
            }
        }
        return readerBuilder;
    }
}
