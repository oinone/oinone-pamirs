package pro.shushi.pamirs.framework.connectors.data.api.domain.model.system;

import org.apache.commons.collections4.MapUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

/**
 * 索引结构（含模块、模型编码）
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base(SystemSourceEnum.KERNEL)
@Model.Static
@Model.Advanced(table = ModuleIndex.TABLE_NAME,
        unique = "indexName,tableName,tableSchema,dsKey",
        index = {"module", "tableName,tableSchema,dsKey", "tableSchema,isDeleted"}
)
@Model.Persistence(capitalMode = true)
@Model.model(ModuleIndex.MODEL_MODEL)
@Model
public class ModuleIndex extends IdModel {

    public final static String MODEL_MODEL = "system.ModuleIndex";
    public final static String TABLE_NAME = "pamirs_module_index";
    private static final long serialVersionUID = -1109757472764690277L;

    @Field
    private String dsKey;

    @Field
    private String tableSchema;

    @Field
    private String tableName;

    @Field
    private String indexName;

    @Field
    private String module;

    @Field
    private String model;

    private Boolean changed;

    private Boolean using;

    public static boolean isEmpty(ModuleIndex moduleIndex) {
        return null == moduleIndex || moduleIndex.isEmpty();
    }

    public boolean isEmpty() {
        return MapUtils.isEmpty(this.get_d());
    }

}
