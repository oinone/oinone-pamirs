package pro.shushi.pamirs.eip.api.excel;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * EipExcelSheet
 *
 * @author yakir on 2024/10/31 18:16.
 */
@Data
public class EipExcelSheet implements Serializable {

    private static final long serialVersionUID = 6303202300968845540L;

    private String name;

    private boolean headReady = false;

    private List<EipExcelHead> headers = new ArrayList<>();

    private List<EipExcelEntry> data = new ArrayList<>();

    public void addHead(EipExcelHead head) {
        this.headers.add(head);
    }

    public EipExcelHead getHead(int index) {
        for (EipExcelHead head : this.headers) {
            if (index == head.getIndex()) {
                return head;
            }
        }
        return null;
    }

    public void addEntry(EipExcelEntry Entry) {
        this.data.add(Entry);
    }

    public boolean headReady() {
        return headReady;
    }

    public void setHeadReady() {
        this.headReady = true;
    }
}
