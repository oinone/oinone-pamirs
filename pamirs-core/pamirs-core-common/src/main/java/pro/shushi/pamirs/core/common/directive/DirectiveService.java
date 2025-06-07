package pro.shushi.pamirs.core.common.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-12-18 11:46
 */
public interface DirectiveService<T extends Directive> {

    /**
     * 获取指令
     *
     * @return 指令
     */
    T getDirective();
}
