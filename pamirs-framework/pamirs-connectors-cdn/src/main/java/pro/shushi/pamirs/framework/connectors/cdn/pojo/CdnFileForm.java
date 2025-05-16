package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/15
 */
@Data
public class CdnFileForm {

    /**
     * 下载URL
     */
    private String downloadUrl;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 单文件上传数据
     *
     * @see CdnSingleUploadData
     */
    private String singleUploadJson;

    /**
     * 分片上传/断点续传数据
     *
     * @see CdnMultipartUploadData
     */
    private String multipartUploadJson;

    /**
     * 文件名
     *
     * @deprecated please using {@link CdnFileForm#filename}.
     */
    @Deprecated
    private String fileName;

    /**
     * 上传URL
     *
     * @deprecated please using {@link CdnFileForm#singleUploadJson}
     */
    @Deprecated
    private String uploadUrl;


    /**
     * FormData数据
     *
     * @deprecated please using {@link CdnFileForm#singleUploadJson}
     */
    @Deprecated
    private String formDataJson;
}
