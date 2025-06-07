package pro.shushi.pamirs.eip.api.excel;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

/**
 * EipExcelHead
 *
 * @author yakir on 2024/10/31 18:13.
 */
@Data
public class EipExcelHead implements Serializable {

    private static final long serialVersionUID = -6163651327228260433L;

    private int index;

    private String name;

    private String type;

    private String format;

}
