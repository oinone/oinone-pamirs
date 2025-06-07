package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * 分片上传/断点续传
 *
 * @author Adamancy Zhang at 10:02 on 2024-05-30
 */
@Data
public class CdnMultipartUploadData {

    /**
     * 存储单个上传数据的列表
     */
    private List<CdnSingleUploadData> uploadDataList;

    /**
     * 完成上传后的数据
     */
    private CdnCompleteUploadData completeUploadData;

    /**
     * 暂停上传的 URL
     */
    private String pauseUrl;

    /**
     * 暂停上传的请求头信息
     */
    private String pauseHeaders;

    /**
     * 恢复上传的 URL
     */
    private String resumeUrl;

    /**
     * 恢复上传的请求头信息
     */
    private String resumeHeaders;
}
