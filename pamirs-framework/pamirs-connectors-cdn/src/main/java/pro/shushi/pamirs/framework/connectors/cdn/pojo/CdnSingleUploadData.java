package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

/**
 * 单文件上传
 *
 * @author Adamancy Zhang at 12:22 on 2024-05-30
 */
@Data
public class CdnSingleUploadData {

    /**
     * Http请求方式
     */
    private String httpMethod;

    /**
     * 客户端直传URL
     */
    private String uploadUrl;

    /**
     * 请求头
     */
    private Map<String, String> uploadHeaders;

    /**
     * FormData数据
     */
    private Map<String, String> uploadFormData;

    /**
     * 分片序号
     */
    private Integer partNumber;
}
