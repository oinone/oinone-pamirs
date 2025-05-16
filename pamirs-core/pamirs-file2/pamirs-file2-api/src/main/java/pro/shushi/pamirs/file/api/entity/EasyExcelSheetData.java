package pro.shushi.pamirs.file.api.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyExcel工作表数据
 *
 * @author Adamancy Zhang at 18:50 on 2023-09-06
 */
public class EasyExcelSheetData {

    private List<Object> dataList;

    public EasyExcelSheetData() {
        this(null);
    }

    public EasyExcelSheetData(List<Object> dataList) {
        this.dataList = dataList;
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }

    public EasyExcelSheetData addData(Object data) {
        if (this.dataList == null) {
            this.dataList = new ArrayList<>();
        }
        this.dataList.add(data);
        return this;
    }
}
