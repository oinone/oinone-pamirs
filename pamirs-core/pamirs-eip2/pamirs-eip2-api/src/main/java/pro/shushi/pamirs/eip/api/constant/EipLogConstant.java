package pro.shushi.pamirs.eip.api.constant;

/**
 * @author yeshenyue on 2026/4/7 11:42
 */
public interface EipLogConstant {

    String MULTIPART_MSG = "通过 multipart/form-data 方式传输的数据会转化成流的形式进行传递为二进制数据，不在此展示";
    String FREQUENCY_LOG_MSG_PREFIX = "本次请求因频率限制未记录请求和返回报文日志详情，当前频率：";
}
