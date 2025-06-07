package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.api.core.orm.batch.BatchSizeWrapper;
import pro.shushi.pamirs.meta.api.dto.ds.DsWrapper;
import pro.shushi.pamirs.meta.api.dto.msg.MessageHub;
import pro.shushi.pamirs.meta.api.enmu.BatchCommitTypeEnum;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * pamirs内核线程变量
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/14 10:41 下午
 */
public class PamirsKernelThreadLocal implements SessionMetaBit, Serializable {

    private static final long serialVersionUID = 1947870669282325515L;

    private boolean staticConfig = false;

    private final Deque<DsWrapper> dsKey = new ArrayDeque<>();

    private final Deque<BatchSizeWrapper> batchSize = new ArrayDeque<>();

    private Long META_BIT;

    private BatchCommitTypeEnum batchOperation;

    private MessageHub messageHub = new MessageHub();

    private Map<String, String> extend = new HashMap<>();

    public boolean isStaticConfig() {
        return staticConfig;
    }

    public void setStaticConfig(boolean staticConfig) {
        this.staticConfig = staticConfig;
    }

    public Object getDsKey() {
        if (this.dsKey.isEmpty()) {
            return null;
        }
        return this.dsKey.peek().getDs();
    }

    public Object popDsKey() {
        if (this.dsKey.isEmpty()) {
            return null;
        }
        return this.dsKey.pop().getDs();
    }

    public void pushDsKey(Object dsKey) {
        this.dsKey.push(DsWrapper.wrap(dsKey));
    }

    public Integer getBatchSize() {
        if (this.batchSize.isEmpty()) {
            return null;
        }
        return this.batchSize.peek().getBatchSize();
    }

    public Integer popBatchSize() {
        if (this.batchSize.isEmpty()) {
            return null;
        }
        return this.batchSize.pop().getBatchSize();
    }

    public void pushBatchSize(Integer batchSize) {
        this.batchSize.push(BatchSizeWrapper.wrap(batchSize));
    }

    public BatchCommitTypeEnum getBatchOperation() {
        return batchOperation;
    }

    public void setBatchOperation(BatchCommitTypeEnum batchOperation) {
        this.batchOperation = batchOperation;
    }

    public Long getMETA_BIT() {
        return META_BIT;
    }

    public void setMETA_BIT(Long META_BIT) {
        this.META_BIT = META_BIT;
    }

    public MessageHub getMessageHub() {
        return messageHub;
    }

    public void setMessageHub(MessageHub messageHub) {
        this.messageHub = messageHub;
    }

    public Map<String, String> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, String> extend) {
        this.extend = extend;
    }
}
