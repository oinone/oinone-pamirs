package pro.shushi.pamirs.eip.api.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EipExcel
 *
 * @author yakir on 2024/10/31 18:15.
 */
@Data
public class EipExcel implements Serializable {

    private static final TypeReference<EipExcel> TR = new TypeReference<EipExcel>() {};

    private static final long serialVersionUID = 145804318174047894L;

    private Long total = 0L;

    private List<EipExcelSheet> sheets = new ArrayList<>();

    private List<EipExcelSheet> originSheets = new ArrayList<>();

    public EipExcelSheet computeIfAbsentSheet(String sheetName) {
        for (EipExcelSheet sheet : sheets) {
            if (StringUtils.equals(sheet.getName(), sheetName)) {
                return sheet;
            }
        }

        EipExcelSheet sheet = new EipExcelSheet();
        sheet.setName(sheetName);
        sheets.add(sheet);
        return sheet;
    }

    public EipExcelSheet getSheet(String sheetName) {
        for (EipExcelSheet sheet : sheets) {
            if (StringUtils.equals(sheet.getName(), sheetName)) {
                return sheet;
            }
        }

        return null;
    }

    public void addTotal(Integer num) {
        if (null == num) {
            num = 100000;
        }
        total = total + num - 1;
    }

    public static EipExcel fromJson(String json) {
        return JSON.parseObject(json, TR);
    }

}
