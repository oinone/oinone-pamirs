package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.Map;

/**
 * 完成分片上传数据
 *
 * @author Adamancy Zhang at 20:43 on 2024-05-30
 */
@Data
public class CdnCompleteUploadData extends CdnSingleUploadData {

    /**
     * 完成上传的 XML 数据模板
     */
    private String uploadData;

    /**
     * 每个分片的 XML 数据模板
     */
    private String uploadPartData;

    /**
     * 上传部件上下文，用于替换 XML 模板中的占位符
     */
    private Map<String, String> uploadPartContext;
}
