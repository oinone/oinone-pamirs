package pro.shushi.pamirs.framework.connectors.data.autoconfigure;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.AbstractBaseTest;

/**
 * 数据保护测试
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020-01-09 14:05
 */
@DisplayName("数据保护测试")
public class DsUrlAESTest extends AbstractBaseTest {

    @Test
    @Order(0)
    @DisplayName("测试数据保护")
    public void testEncyptDsUrl() {
        String data = "jdbc:mysql://127.0.0.1:3306/pamirs?useSSL=false&allowPublicKeyRetrieval=true&useServerPrepStmts=true&cachePrepStmts=true&useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&autoReconnect=true&ttt=tenant1";

        // 生成 16 位随机 AES 密钥
        String randomKey = AES.generateRandomKey();

        // 随机密钥加密
        String result = AES.encrypt(data, randomKey);

        String data1 = AES.decrypt(result, randomKey);

        System.out.println("mpw.key---------------------> " + randomKey);
        System.out.println("url: mpw:" + result);
        System.out.println("username: mpw:" + AES.encrypt("root", randomKey));
        System.out.println("password: mpw:" + AES.encrypt("shushi@2019", randomKey));

        AssertionErrors.assertEquals("删除单条失败", data, data1);

        // 加密配置 mpw: 开头紧接加密内容（ 非数据库配置专用 YML 中其它配置也是可以使用的 ）
        /*
            spring:
                datasource:
                    url: mpw:qRhvCwF4GOqjessEB3G+a5okP+uXXr96wcucn2Pev6Bf1oEMZ1gVpPPhdDmjQqoM
                    password: mpw:Hzy5iliJbwDHhjLs1L0j6w==
                    username: mpw:Xb+EgsyuYRXw7U7sBJjBpA==
        */

        // Jar 启动参数（ idea 设置 Program arguments , 服务器可以设置为启动环境变量 ）
        // --mpw.key=d1104d7c3b616f0b
    }

}
