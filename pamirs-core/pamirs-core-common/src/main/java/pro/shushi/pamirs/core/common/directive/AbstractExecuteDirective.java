package pro.shushi.pamirs.core.common.directive;

import java.util.Objects;

/**
 * @author Adamancy Zhang
 * @date 2020-12-18 14:46
 */
public abstract class AbstractExecuteDirective implements Directive {

    private final int intValue;

    protected AbstractExecuteDirective(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractExecuteDirective)) return false;
        AbstractExecuteDirective that = (AbstractExecuteDirective) o;
        return intValue == that.intValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intValue);
    }
}
