package pro.shushi.pamirs.meta.constant;

/**
 * 表达式上下文常量
 * <p>
 * 2021/3/5 11:40 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface ExpressionContextConstants {

    /**
     * @deprecated please using {@link ExpressionContextConstants#ACTIVE_RECORD}
     */
    @Deprecated
    String ACTIVE_VALUE = "activeValue";

    String ACTIVE_RECORD = "activeRecord";

    String ACTIVE_MODEL = "activeModel";

    String ACTIVE_FIELD = "activeField";

    String CONTEXT = "context";

}
