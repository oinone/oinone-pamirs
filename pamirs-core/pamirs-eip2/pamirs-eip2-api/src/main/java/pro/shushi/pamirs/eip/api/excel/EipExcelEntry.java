package pro.shushi.pamirs.eip.api.excel;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;
import java.util.List;

/**
 * EipExcelEntry
 *
 * @author yakir on 2024/10/31 18:13.
 */
@Data
public class EipExcelEntry implements Serializable {

    private static final long serialVersionUID = -7003887477238139410L;

    private int index = 1;

    private List<String> data;

}
