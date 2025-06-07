package pro.shushi.pamirs.core.common.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 13:28
 */
public interface DirectiveEnumeration<SELF extends Enum<SELF> & DirectiveEnumeration<SELF>> extends IntValueEnumeration<SELF>, Directive {
}
