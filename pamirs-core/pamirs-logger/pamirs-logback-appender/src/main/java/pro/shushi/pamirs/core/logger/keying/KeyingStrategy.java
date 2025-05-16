package pro.shushi.pamirs.core.logger.keying;

/**
 * KeyingStrategy
 *
 * @author yakir on 2023/12/27 17:09.
 */
public interface KeyingStrategy<E> {

    default String createKey(E e) {
        return null;
    }

}
