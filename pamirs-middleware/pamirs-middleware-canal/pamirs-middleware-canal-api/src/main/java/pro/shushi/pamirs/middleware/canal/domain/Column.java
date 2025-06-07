package pro.shushi.pamirs.middleware.canal.domain;

import java.io.Serializable;

/**
 * Column
 *
 * @author yakir on 2020/05/25 14:26.
 */
public class Column implements Serializable {

    private static final long serialVersionUID = -9092054484658521073L;

    private String name;
    private Object value;
    private boolean isUpdate;
    private boolean isNull;
    private boolean isKey;
    private String ltype; // java 类型
    private String stype; // db类型

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Column setValue(Object value) {
        this.value = value;
        return this;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public Column setUpdate(boolean update) {
        isUpdate = update;
        return this;
    }

    public boolean isNull() {
        return isNull;
    }

    public Column setNull(boolean aNull) {
        isNull = aNull;
        return this;
    }

    public boolean isKey() {
        return isKey;
    }

    public Column setKey(boolean key) {
        isKey = key;
        return this;
    }

    public String getLtype() {
        return ltype;
    }

    public Column setLtype(String ltype) {
        this.ltype = ltype;
        return this;
    }

    public String getStype() {
        return stype;
    }

    public Column setStype(String stype) {
        this.stype = stype;
        return this;
    }

    public Column setIsKey() {
        this.isKey = true;
        return this;
    }

    public Column setIsNull() {
        this.isNull = true;
        return this;
    }

    public Column setIsUpdate() {
        this.isUpdate = true;
        return this;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", isUpdate=" + isUpdate +
                ", isNull=" + isNull +
                ", isKey=" + isKey +
                ", ltype='" + ltype + '\'' +
                ", stype='" + stype + '\'' +
                '}';
    }
}
