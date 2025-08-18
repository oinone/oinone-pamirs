package pro.shushi.pamirs.eip.api.service.distribution;

/**
 * @author Adamancy Zhang at 12:16 on 2025-08-18
 */
public interface EipDistributionService {

    /**
     * 开启分布式支持
     */
    void start() throws Exception;

    /**
     * 关闭分布式支持
     */
    void close();
}
