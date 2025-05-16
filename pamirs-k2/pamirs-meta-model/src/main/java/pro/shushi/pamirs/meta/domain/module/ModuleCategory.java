package pro.shushi.pamirs.meta.domain.module;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;

import static pro.shushi.pamirs.meta.domain.module.ModuleCategory.MODEL_MODEL;

/**
 * 应用分类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@Base
@Model.Advanced(unique = "code")
@Model.model(MODEL_MODEL)
@Model(displayName = "应用分类", summary = "应用分类", labelFields = "name")
public class ModuleCategory extends IdModel {

    public final static String MODEL_MODEL = "base.ModuleCategory";
    private static final long serialVersionUID = 7900744640411971993L;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "parentCode", referenceFields = "code")
    @Field(displayName = "父分类")
    private ModuleCategory parent;

    @Base
    @Field.String
    @Field(displayName = "分类名称", unique = true, required = true, translate = true)
    private String name;

    @Base
    @Field.String
    @Field(displayName = "分类唯一编码", unique = true, required = true)
    private String code;

    @Base
    @Field.Integer
    @Field(displayName = "优先级", defaultValue = "100")
    private Integer sequence;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field.Boolean
    @Field(displayName = "是否可见")
    private Boolean visible;

    @Base
    @Field.Boolean
    @Field(displayName = "业务大屏是否可见", defaultValue = "true")
    private Boolean screenVisible;

    @Base
    @Field.Boolean
    @Field(displayName = "是否高级")
    private Boolean exclusive;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "code", referenceFields = "category")
    @Field(displayName = "模块")
    private List<ModuleDefinition> modules;

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function
    public ModuleCategory construct(ModuleCategory moduleCategory) {
        if (StringUtils.isBlank(moduleCategory.getCode())) {
            moduleCategory.setCode(moduleCategory.getId() + "");
        }
        return moduleCategory;
    }


}
