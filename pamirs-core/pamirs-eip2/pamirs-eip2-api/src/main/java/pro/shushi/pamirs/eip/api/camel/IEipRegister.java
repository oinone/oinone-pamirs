package pro.shushi.pamirs.eip.api.camel;

import java.util.List;

/**
 * Eip组件注册
 *
 * @author Adamancy Zhang at 20:04 on 2021-07-27
 */
public interface IEipRegister {

    /**
     * 获取注册组件列表
     *
     * @return 组件列表
     */
    List<RegistryComponentBody> registers();
}
