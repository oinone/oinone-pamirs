package pro.shushi.pamirs.eip.api.strategy.entity;

import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;

import java.io.Serializable;

/**
 * @author Adamancy Zhang at 17:08 on 2025-08-16
 */
public class EipLogStrategyEntity implements Serializable {

    private static final long serialVersionUID = -1901419292475600783L;

    private InterfaceTypeEnum interfaceType;

    private String interfaceName;

    private boolean enabled;

    private boolean isIgnoreLogFrequency;

    private double frequency;

    public InterfaceTypeEnum getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceTypeEnum interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isIgnoreLogFrequency() {
        return isIgnoreLogFrequency;
    }

    public void setIgnoreLogFrequency(boolean ignoreLogFrequency) {
        isIgnoreLogFrequency = ignoreLogFrequency;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
