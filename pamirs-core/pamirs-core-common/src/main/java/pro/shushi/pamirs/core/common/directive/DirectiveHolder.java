package pro.shushi.pamirs.core.common.directive;

/**
 * 指令更新
 *
 * @author Adamancy Zhang at 21:56 on 2024-07-01
 */
public class DirectiveHolder {

    private final int initialValue;

    private int value;

    public DirectiveHolder(int value) {
        this.initialValue = value;
        this.value = value;
    }

    public void enable(int value) {
        if (this.value == this.initialValue) {
            this.value = value;
        } else {
            this.value = DirectiveHelper.enable(this.value, value);
        }
    }

    public void enable(Boolean condition, int value) {
        if (Boolean.TRUE.equals(condition)) {
            enable(value);
        }
    }

    public void disable(int value) {
        if (this.value == this.initialValue) {
            this.value = value;
        } else {
            this.value = DirectiveHelper.disable(this.value, value);
        }
    }

    public void disable(Boolean condition, int value) {
        if (Boolean.TRUE.equals(condition)) {
            disable(value);
        }
    }

    public int get() {
        return value;
    }
}