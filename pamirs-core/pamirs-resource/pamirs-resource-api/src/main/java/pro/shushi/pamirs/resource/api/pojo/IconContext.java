package pro.shushi.pamirs.resource.api.pojo;

import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.resource.api.tmodel.ResourceIconUpload;

import java.util.List;

/**
 * 上传文件上下文
 */
@Data
public class IconContext {

    List<String> cssUrls;

    List<String> jsUrls;

    List<String> fontUrls;

    String secondaryDirectory;

    ResourceIconUpload resourceIconUpload;

    JsonRootBean parse;

}
