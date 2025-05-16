package pro.shushi.pamirs.boot.common.extend;

import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.util.Map;

/**
 * 编程式待计算元数据编辑接口
 * <p>
 * 2020/8/27 5:03 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface MetaDataPreEditor {

    void edit(AppLifecycleCommand command, Map<String/*module*/, Meta> metaMap);

}
