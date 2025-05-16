package pro.shushi.pamirs.auth.view.manager;

import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;

/**
 * 资源节点
 *
 * @author Adamancy Zhang at 18:20 on 2024-01-19
 */
public interface ResourceNodeEntity {

    PermissionMateDataEnum getNodeType();

    Long getResourceId();

    String getPath();

}
