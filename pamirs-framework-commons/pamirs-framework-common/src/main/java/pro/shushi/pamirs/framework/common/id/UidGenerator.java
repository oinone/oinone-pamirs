package pro.shushi.pamirs.framework.common.id;

import pro.shushi.pamirs.framework.common.id.exception.UidGenerateException;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * Represents a unique id generator.
 */
@SPI
public interface UidGenerator {

    /**
     * Get a unique ID
     *
     * @return UID
     */
    long getUID() throws UidGenerateException;

    /**
     * Parse the UID into elements which are used to generate the UID. <br>
     * Such as timestamp & workerId & sequence...
     *
     * @param uid UID
     * @return Parsed info
     */
    String parseUID(long uid);

}
