package pro.shushi.pamirs.sid.worker;

import org.apache.commons.lang3.RandomUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.shushi.pamirs.framework.connectors.data.api.datasource.DsHintApi;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.sid.enmu.WorkerNodeType;
import pro.shushi.pamirs.sid.model.WorkerNode;
import pro.shushi.pamirs.sid.utils.DockerUtils;
import pro.shushi.pamirs.sid.utils.HostUtils;

import java.util.Date;


/**
 * Represents an implementation of {@link WorkerIdAssigner},
 * the worker id will be discarded after assigned to the UidGenerator
 */
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {

    private static final Logger log = LoggerFactory.getLogger(DisposableWorkerIdAssigner.class);

    private SqlSessionTemplate getSqlSessionTemplate(String model) {
        return BeanDefinitionUtils.getBean(SqlSessionTemplate.class);
    }

    /**
     * Assign worker id base on database.<p>
     * If there is host name & port in the environment, we considered that the node runs in Docker container<br>
     * Otherwise, the node runs on an actual machine.
     *
     * @return assigned worker id
     */
//    @Transactional // todo
    public long assignWorkerId() {
        // build worker node entity
        WorkerNode workerNode = buildWorkerNode();
        try (DsHintApi ignored = DsHintApi.model(WorkerNode.MODEL_MODEL)) {
            // 数据库操作
            workerNode.create();
        }
//        added = workerNode.queryOne(workerNode);
        return workerNode.getId();
    }

    /**
     * Build worker node entity by IP and PORT
     */
    private WorkerNode buildWorkerNode() {
        WorkerNode workerNode = new WorkerNode();
        if (DockerUtils.isDocker()) {
            workerNode.setType(WorkerNodeType.CONTAINER.getValue());
            workerNode.setHostName(DockerUtils.getDockerHost());
            workerNode.setPort(DockerUtils.getDockerPort());
            workerNode.setLaunchDate(new Date());

        } else {
            workerNode.setType(WorkerNodeType.ACTUAL.getValue());
            workerNode.setHostName(HostUtils.getLocalAddress());
            workerNode.setPort(System.currentTimeMillis() + "-" + RandomUtils.nextInt(0, 100000));
            workerNode.setLaunchDate(new Date());
        }

        return workerNode;
    }

}
