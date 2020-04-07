package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.api.MetaApiFactory;
import pro.shushi.pamirs.meta.api.core.systems.type.gen.VersionGenerator;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.SequenceConfig;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;
import pro.shushi.pamirs.meta.enumclass.ModuleStateEnumCls;
import pro.shushi.pamirs.meta.enumclass.SoftwareLicenseEnumCls;
import pro.shushi.pamirs.meta.enumclass.SystemSourceEnumCls;

import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;

/**
 * 模块定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaModel(priority = 1, core = true)
@Base
@Model.model("base.Module")
@Model(displayName = "模块", summary = "模块", labelFields = "displayName")
public class ModuleDefinition extends IdModel {

    @Base
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Field(displayName = "技术名称", check = "checkModelName", unique = true, required = true)
    private String name;

    @Base
    @Field(displayName = "模块编码", check = "checkModelModel", unique = true, required = true, immutable = true)
    private String module;

    @Base
    @Field(displayName = "数据库名")
    private String database;

    @Base
    @Field(displayName = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述")
    private String description;

    @Base
    @Field(displayName = "状态", index = true, required = true, defaultValue = "uninstalled")
    private ModuleStateEnumCls state;

    @Base
    @Field(displayName = "系统来源", index = true, defaultValue = "MANUAL")
    private SystemSourceEnumCls source;

    @Base
    @Field(displayName = "引导模块", defaultValue = "false")
    private Boolean boot;

    @Base
    @Field(displayName = "是否应用", required = true, defaultValue = "true")
    private Boolean application;

    @Base
    @Field(displayName = "安装版本", check = "checkModuleVersion")
    private String latestVersion;

    @Base
    @Field(displayName = "最新平台版本", check = "checkModuleVersion", required = true)
    private String platformVersion;

    @Base
    @Field(displayName = "最新发布版本", defaultValue = "0")
    private Long publishedVersion;

    @Base
    @Field(displayName = "发布总次数", defaultValue = "0")
    private Long publishCount;

    @Base
    @Field.many2one
    @Field.Relation(relationFields = "category", referenceFields = "code")
    @Field(displayName = "分类")
    private ModuleCategory moduleCategory;

    @Base
    @Field(summary = "分类编码", invisible = true)
    private String category;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "module")
    @Field(displayName = "依赖模块列表")
    private List<ModuleDependency> moduleDependencyList;

    @Base
    @Field.Related(related = {"moduleDependencyList","dependencyModule"})
    @Field(summary = "依赖模块编码列表", serialize = COMMA, invisible = true)
    private List<String> moduleDependencies;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "module")
    @Field(displayName = "互斥模块列表")
    private List<ModuleExclusion> moduleExclusionList;

    @Base
    @Field.Related(related = {"moduleExclusionList","excludeModule"})
    @Field(summary = "互斥模块编码列表", serialize = COMMA, invisible = true)
    private List<String> moduleExclusions;

    @Base
    @Field(displayName = "排序", summary = "决定了模块在展示和首页默认跳转的顺序，默认100，排序时默认按priority和name排序", defaultValue = "100")
    private Long priority;

    @Base
    @Field(displayName = "站点")
    private String website;

    @Base
    @Field(displayName = "module的作者", defaultValue = PackageConstants.author)
    private String author;

//    @Base
//    @Field.many2one
//    @Field.Relation(references = "resource.ResourceFile", relationFields = "icon")
//    @Field(displayName = "icon")
//    private Map iconFile;

    @Base
    private String icon;

    @Base
    @Field(displayName = "演示应用", required = true, defaultValue = "false")
    private Boolean demo;

    @Base
    @Field(displayName = "web应用", required = true, defaultValue = "false")
    private Boolean web;

    @Base
    @Field(displayName = "许可证", defaultValue = "PEEL1")
    private SoftwareLicenseEnumCls license;

    @Base
    @Field(displayName = "需要购买", defaultValue = "false")
    private Boolean toBuy;

    @Base
    @Field(displayName = "维护者", defaultValue = PackageConstants.maintainer)
    private String maintainer;

    @Base
    @Field.Text
    @Field(displayName = "贡献者列表")
    private String contributors;

    @Base
    @Field(displayName = "代码库的地址")
    private String url;

    @Base
    @Field(displayName = "自建应用", required = true , defaultValue = "false")
    private Boolean selfBuilt;

    @Function
    public ModuleDefinition construct(ModuleDefinition moduleDefinition){
        // name
        String defaultModule = (String)MetaApiFactory.getSequenceProcessor().generate(SequenceEnum.UUID.value(), new SequenceConfig().setPrefix(Module.class.getSimpleName()).setSeparator(CharacterConstants.SEPARATOR_UNDERLINE));
        moduleDefinition.setModule(Optional.ofNullable(moduleDefinition).map(v->v.getModule()).orElse(defaultModule))
                .setName(Optional.ofNullable(moduleDefinition).map(v->v.getName()).orElse(moduleDefinition.getModule()))
                .setDisplayName(Optional.ofNullable(moduleDefinition).map(v->v.getDisplayName()).orElse(moduleDefinition.getName()))
                .setDatabase(Optional.ofNullable(moduleDefinition).map(v->v.getDatabase()).orElse(PStringUtils.fieldName2Column(moduleDefinition.getName())))
        ;
        // version
        VersionGenerator versionGenerator = MetaApiFactory.getVersionGenerator();
        String defaultVersion = versionGenerator.generate();
        moduleDefinition.setLatestVersion(Optional.ofNullable(moduleDefinition).map(v->v.getLatestVersion()).orElse(defaultVersion));
        return moduleDefinition;
    }

}
