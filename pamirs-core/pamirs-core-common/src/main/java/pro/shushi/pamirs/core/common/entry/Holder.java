package pro.shushi.pamirs.core.common.entry;

/**
 * @deprecated 6.x please using {@link pro.shushi.pamirs.ux.common.entity.Holder}
 */
@Deprecated
public class Holder<T> extends pro.shushi.pamirs.ux.common.entity.Holder<T> {

    public Holder() {
        super();
    }

    public Holder(T value) {
        super(value);
    }
}
