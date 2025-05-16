package pro.shushi.pamirs.middleware.schedule.directive;

/**
 * @author Adamancy Zhang
 * @date 2020-10-22 13:28
 */
public interface DirectiveEnumeration<SELF extends Enum<SELF> & DirectiveEnumeration<SELF>> extends Directive {
}
