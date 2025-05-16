package pro.shushi.pamirs.framework.orm.submit.domain;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 待处理记录上下文
 * <p>
 * 2022/5/11 5:37 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SubmitRecordContext {

    private Deque<SameDepthSubmitRecords> sameDepthSubmitRecordQueue;

    public SubmitRecordContext() {
        this.setSameDepthSubmitRecordQueue(new ArrayDeque<>());
    }

}
