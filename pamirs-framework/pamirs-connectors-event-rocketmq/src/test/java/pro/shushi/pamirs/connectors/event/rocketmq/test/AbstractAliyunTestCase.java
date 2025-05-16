package pro.shushi.pamirs.connectors.event.rocketmq.test;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.remoting.RPCHook;

/**
 * @author Adamancy Zhang
 * @date 2020-12-04 15:44
 */
public class AbstractAliyunTestCase implements AliyunProperties {

    protected static RPCHook getRpcHook() {
        return new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
    }
}
