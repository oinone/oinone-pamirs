package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.List;

/**
 * 上传文件请求
 *
 * @author Adamancy Zhang at 10:44 on 2024-06-01
 */
@Data
public class CdnUploadFileRequest {

    private String filename;

    private String uploadFilename;

    private Long size;

    private String accept;

    private String contentType;

    private List<CdnChunkFile> chunkFiles;
}
