package pro.shushi.pamirs.sid.worker;

/**
 * Represents a worker id assigner for {@link pro.shushi.pamirs.sid.impl.DefaultUidGenerator}
 */
public interface WorkerIdAssigner {

    /**
     * Assign worker id for {@link pro.shushi.pamirs.sid.impl.DefaultUidGenerator}
     *
     * @return assigned worker id
     */
    long assignWorkerId();

}
