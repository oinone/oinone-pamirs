package pro.shushi.pamirs.core.common.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-11-30 15:41
 */
public enum ForeachDirective implements DirectiveEnumeration<ForeachDirective> {

    EXECUTE(1),
    BREAK(2),
    CONTINUE(4);

    private final int value;

    ForeachDirective(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }
}
