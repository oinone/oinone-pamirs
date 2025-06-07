package pro.shushi.pamirs.framework.connectors.cdn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Adamancy Zhang
 * @date 2020-12-02 09:58
 */
@SpringBootApplication(scanBasePackages = {
        "pro.shushi.pamirs.framework.connectors.cdn",
        "pro.shushi.pamirs.meta.common.spring",
})
public class CDNApplication {

    public static void main(String[] args) {
        SpringApplication.run(CDNApplication.class);
    }
}
