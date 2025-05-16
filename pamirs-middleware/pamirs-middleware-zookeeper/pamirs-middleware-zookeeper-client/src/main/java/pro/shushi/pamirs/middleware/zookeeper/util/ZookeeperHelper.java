package pro.shushi.pamirs.middleware.zookeeper.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import pro.shushi.pamirs.middleware.zookeeper.config.ZKConfigurationConstant;

import java.security.NoSuchAlgorithmException;

public class ZookeeperHelper {

    public static String repairPath(String path) {
        if (path == null) {
            return null;
        }
        path = path.trim();
        if (path.charAt(0) != ZKConfigurationConstant.SEPARATOR_SLASH_CHAR) {
            path = ZKConfigurationConstant.SEPARATOR_SLASH + path;
        }
        if (path.charAt(path.length() - 1) == ZKConfigurationConstant.SEPARATOR_SLASH_CHAR) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getAuthString(String username, String password) {
        //防止使用空密码或用户名创建节点导致的权限异常问题
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            return username + ZKConfigurationConstant.SEPARATOR_COLON + password;
        }
        return null;
    }

    /**
     * <h>zookeeper使用Super用户删除带权限节点方法</h>
     * <p>
     * 1. 运行main方法，生成密钥【super:g9oN2HttPfn8MMWJZ2r45Np/LIA=】（密码一致的情况下无需重新生成）
     * 2. vim zkEnv.sh
     * 3. 将以下内容粘贴至文件尾
     * SERVER_JVMFLAGS="-Dzookeeper.DigestAuthenticationProvider.superDigest=super:g9oN2HttPfn8MMWJZ2r45Np/LIA= $SERVER_JVMFLAGS"
     * 4. 重启zookeeper server
     * 5. 连接至zookeeper server ./zkCli.sh
     * 6. 添加权限 addauth digest super:superpw
     * 7. 可直接删除带权限的节点
     * </p>
     */
    public static void main(String[] args) {
        try {
            System.out.println(DigestAuthenticationProvider.generateDigest("super:superpw"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
