package pro.shushi.pamirs.file.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.enmu.ExcelValueTypeEnum;
import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

@Data
public class EasyExcelCellDefinition implements Serializable {

    private static final long serialVersionUID = -991696707291892451L;

    private String key;

    private String model;

    private String field;

    private String value;

    private ExcelValueTypeEnum type;

    private String format;

    private Boolean translate;

    @JSONField(serialize = false)
    private Boolean isFormatInit = Boolean.FALSE;

    @JSONField(serialize = false)
    private Object formatObject;

    private Boolean isStatic;

    private boolean isCollection;

    public EasyExcelCellDefinition setField(String field) {
        this.field = field;
        String currentField;
        int p = this.field.lastIndexOf(FileConstant.POINT_CHARACTER);
        if (p == -1) {
            currentField = field;
        } else {
            currentField = field.substring(p + 1);
        }
        this.isCollection = currentField.endsWith(FileConstant.LIST_FLAG_CHARACTER);
        return this;
    }

    public boolean getIsCollection() {
        return isCollection;
    }
}
