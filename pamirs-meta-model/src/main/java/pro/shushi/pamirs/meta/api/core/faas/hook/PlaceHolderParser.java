package pro.shushi.pamirs.meta.api.core.faas.hook;

/**
 * @author shier
 * date  2020/5/7 3:24 下午
 */
public interface PlaceHolderParser {

    Object[] parse(Object... parse);

    Integer priority();

    Boolean active();

    String namespace();

}
