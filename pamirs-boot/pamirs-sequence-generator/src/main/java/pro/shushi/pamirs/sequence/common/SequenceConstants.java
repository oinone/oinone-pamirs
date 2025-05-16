package pro.shushi.pamirs.sequence.common;

/**
 * SequenceConstants
 *
 * @author yakir on 2020/05/11 16:58.
 */
public class SequenceConstants {

    // IDCache未初始化成功时的异常码
    public static final long EXCEPTION_ID_IDCACHE_INIT_FALSE    = -1;
    // key不存在时的异常码
    public static final long EXCEPTION_ID_KEY_NOT_EXISTS        = -2;
    // SegmentBuffer中的两个Segment均未从DB中装载时的异常码
    public static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;

}
