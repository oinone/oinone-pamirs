package pro.shushi.pamirs.framework.connectors.cdn.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/15
 */
@Data
public class CdnFile {

    // 名称
    private String name;

    // 路径
    private String url;

    // 类型
    private String type;

    // 大小
    private Long size;

    // MIME
    private String mime;

    // 是否公开
    private Boolean isPublic;

    // 资源id
    private Long resId;

    // 显示格式
    private String format;
}
