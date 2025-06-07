package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * 分片文件
 *
 * @author Adamancy Zhang at 09:52 on 2024-05-31
 */
@Data
public class CdnChunkFile {

    private Integer partNumber;

    private Long fileSize;
}
