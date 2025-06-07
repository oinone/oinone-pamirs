package pro.shushi.pamirs.middleware.zookeeper.auth;

import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import pro.shushi.pamirs.middleware.zookeeper.config.ZKConfigurationConstant;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class PamirsACLProvider implements ACLProvider {

    private final List<ACL> aclList;

    public PamirsACLProvider(String authString) throws NoSuchAlgorithmException {
        this.aclList = new ArrayList<>();
        this.aclList.add(new ACL(ZooDefs.Perms.ALL, new Id(ZKConfigurationConstant.DIGEST, DigestAuthenticationProvider.generateDigest(authString))));
        this.aclList.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    @Override
    public List<ACL> getDefaultAcl() {
        return aclList;
    }

    @Override
    public List<ACL> getAclForPath(String path) {
        return aclList;
    }
}
