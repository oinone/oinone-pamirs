package pro.shushi.pamirs.boot.web.manager;

import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.ux.model.UIView;
import pro.shushi.pamirs.boot.web.utils.ViewXmlUtils;
import pro.shushi.pamirs.framework.common.utils.ObjectUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

/**
 * 界面IO管理器
 * <p>
 * 2022/5/14 12:15 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Component
public class UiIoManager {

    public <T extends Serializable> List<T> cloneDataList(List<T> dataList) {
        if (null == dataList) {
            return null;
        }
        return ObjectUtils.clone(dataList);
    }

    public <T extends Serializable> T cloneData(T data) {
        if (null == data) {
            return null;
        }
        return ObjectUtils.clone(data);
    }

    public UIView parseTemplate(String template) {
        return (UIView) ViewXmlUtils.fromXML(template);
    }

    public void logFindAction(String model, String name) {
        log.warn(MessageFormat.format("Cannot find corresponding action, model:{0}, name:{1}", model, name));
    }

}
