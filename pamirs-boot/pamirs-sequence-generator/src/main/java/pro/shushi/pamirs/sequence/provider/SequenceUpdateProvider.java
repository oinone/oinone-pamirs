package pro.shushi.pamirs.sequence.provider;

import pro.shushi.pamirs.sequence.model.LeafAlloc;

/**
 * 更新sql生成器
 * <p>
 * 2021/9/23 1:24 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class SequenceUpdateProvider extends AbstractSequenceSqlProvider {

    public static String updateMaxId(LeafAlloc leafAlloc) {
        return "UPDATE `" + fetchTable() + "` set `max_id` = `max_id` + `step` WHERE `code` = #{code}";
    }

    public static String updateMaxIdByCustomStep(LeafAlloc leafAlloc) {
        return "UPDATE `" + fetchTable() + "` SET `max_id` = `max_id` + #{step} WHERE `code` = #{code}";
    }

}
