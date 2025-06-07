package pro.shushi.pamirs.sequence.provider;

import pro.shushi.pamirs.sequence.model.LeafAlloc;

/**
 * 查询sql生成器
 * <p>
 * 2021/9/23 1:24 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SequenceSelectProvider extends AbstractSequenceSqlProvider {

    public static String getAllLeafAllocs() {
        return "SELECT `code`, `max_id`, `step`, `update_time` FROM `" + fetchTable() + "` where is_deleted = 0";
    }

    public static String getLeafAlloc(LeafAlloc leafAlloc) {
        return "SELECT `code`, `max_id`, `step` FROM `" + fetchTable() + "` WHERE `code` = #{code} and is_deleted = 0 FOR UPDATE";
    }

    public static String getAllTags() {
        return "SELECT `code` FROM `" + fetchTable() + "` where is_deleted = 0 and (need_load = 1 or need_load is null)";
    }

}
