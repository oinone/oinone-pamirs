package pro.shushi.pamirs.eip.api.model.alarm;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.io.Serializable;

/**
 * EipAlarmStat
 *
 * @author yakir on 2026/04/09 11:22.
 */
@Data
public class EipAlarmStat implements Serializable {

    private static final long serialVersionUID = -572897288983301861L;

    private String ruleName;

    private String interfaceName;

    private String interfaceTechName;

    private String ruleTechName;

    private long start;

    private long end;

    private long totalSum;

    private long failSum;

    public static EipAlarmStat of() {
        return new EipAlarmStat();
    }

}
