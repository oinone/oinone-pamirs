package pro.shushi.pamirs.eip;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.ReadSheet;
import pro.shushi.pamirs.eip.api.excel.EipExcel;
import pro.shushi.pamirs.eip.api.excel.EipExcelReadListener;
import pro.shushi.pamirs.eip.api.excel.EipExcelSheet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * TestEipIntegrationFile
 *
 * @author yakir on 2024/12/19 13:44.
 */
public class TestEipIntegrationFile {

    public static void main(String[] args) {

        String file = "/Users/yakir/Desktop/用例-模型设计器_1735267000902.csv";

        ExcelReader reader = null;
        try {
            InputStream is = new FileInputStream(file);

            // 限制读取
            EipExcelReadListener readListener = new EipExcelReadListener();
            ExcelReaderBuilder readerBuilder = EasyExcel.read(is, readListener);

            reader = readerBuilder.build();

            List<ReadSheet> sheetList = reader.excelExecutor().sheetList();
            for (ReadSheet readSheet : sheetList) {
                reader.read(readSheet);
            }

            EipExcel excel = readListener.getExcel();
            List<EipExcelSheet> sheets = excel.getSheets();
            long total = excel.getTotal();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
    }
}
