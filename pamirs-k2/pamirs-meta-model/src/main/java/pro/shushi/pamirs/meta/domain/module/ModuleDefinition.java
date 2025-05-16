package pro.shushi.pamirs.meta.domain.module;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaModel;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.core.compute.systems.type.gen.VersionGenerator;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.constant.MetaCheckConstants;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pro.shushi.pamirs.meta.annotation.Field.serialize.COMMA;
import static pro.shushi.pamirs.meta.domain.module.ModuleDefinition.MODEL_MODEL;
import static pro.shushi.pamirs.meta.domain.module.ModuleDefinition.TABLE_NAME;

/**
 * 模块定义
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@MetaSimulator(onlyBasicTypeField = false)
@MetaModel(priority = 1, core = Class.class)
@Base
@Model.Advanced(name = "moduleDefinition", table = TABLE_NAME, priority = 7, unique = {"module"})
@Model.model(MODEL_MODEL)
@Model(displayName = "模块", summary = "模块", labelFields = "displayName")
public class ModuleDefinition extends MetaBaseModel implements MetaCheckConstants {

    public static final String MODEL_MODEL = "base.Module";

    public static final String UE_MODEL_MODEL = "base.UeModule";

    public static final String TABLE_NAME = "base_module";

    private static final long serialVersionUID = 3984873153752667371L;

    @Base
    @Field(displayName = "显示名称", translate = true, required = true)
    private String displayName;

    @Base
    @Validation(check = checkModelName)
    @Field(displayName = "api名称", unique = true, required = true)
    private String name;

    @Base
    @Validation(check = checkModuleModule)
    @Field(displayName = "模块编码", unique = true, required = true)
    private String module;

    @Base
    @Validation(check = checkModuleAbbr)
    @Field(displayName = "模块简称", summary = "模块简称仅支持小写字母和数字且不能超过8位字符", required = true)
    private String abbr;

    @Base
    @Field(displayName = "逻辑数据源名", invisible = true)
    private String dsKey;

    @Base
    @Field.String
    @Field(displayName = "简介", summary = "描述摘要")
    private String summary;

    @Base
    @Field.Text
    @Field(displayName = "描述", summary = "描述详情")
    private String description;

    @Base
    @Field(displayName = "状态", index = true, required = true, defaultValue = "uninstalled")
    private ModuleStateEnum state;

    @Base
    @Field(displayName = "引导模块", defaultValue = "false")
    private Boolean boot;

    @Base
    @Field(displayName = "是否应用", required = true, defaultValue = "true")
    private Boolean application;

    @Base
    @Validation(check = checkModuleVersion)
    @Field(displayName = "安装版本")
    private String latestVersion;

    @Base
    @Validation(check = checkModuleVersion)
    @Field(displayName = "最新平台版本")
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
    @Field(displayName = "默认分类编码", invisible = true)
    private String defaultCategory;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultCategory"})
    @Field(summary = "分类编码", invisible = true, store = NullableBoolEnum.TRUE)
    private String category;

    @Base
    @Field.one2many
    @Field.Relation(store = false)
    @Field(displayName = "中间件列表", store = NullableBoolEnum.TRUE, serialize = Field.serialize.JSON)
    private List<ModuleMiddleWare> moduleMiddleWareList;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "module")
    @Field(displayName = "依赖模块列表")
    private List<ModuleDependency> moduleDependencyList;

    @Base
    @Field.String(size = 512)
    @Field.Related(related = {"moduleDependencyList", "dependencyModule"})
    @Field(summary = "依赖模块编码列表", serialize = COMMA, invisible = true, store = NullableBoolEnum.TRUE)
    private List<String> moduleDependencies;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "module")
    @Field(displayName = "互斥模块列表")
    private List<ModuleExclusion> moduleExclusionList;

    @Base
    @Field.String(size = 512)
    @Field.Related(related = {"moduleExclusionList", "excludeModule"})
    @Field(summary = "互斥模块编码列表", serialize = COMMA, invisible = true, store = NullableBoolEnum.TRUE)
    private List<String> moduleExclusions;

    @Base
    @Field.one2many
    @Field.Relation(relationFields = "module")
    @Field(displayName = "上游模块列表")
    private List<ModuleUpstream> moduleUpstreamList;

    @Base
    @Field.String(size = 512)
    @Field.Related(related = {"moduleUpstreamList", "upstreamModule"})
    @Field(summary = "上游模块编码列表", serialize = COMMA, invisible = true, store = NullableBoolEnum.TRUE)
    private List<String> moduleUpstreams;

    @Base
    @Field.String(size = 1024)
    @Field(summary = "排除拦截器列表", serialize = COMMA, invisible = true, store = NullableBoolEnum.TRUE)
    private List<String> excludeHooks;

    @Base
    @Field(displayName = "优先级", summary = "决定了模块在展示和首页默认跳转的顺序，默认100，排序时默认按priority正序", defaultValue = "100")
    private Long priority;

    @Base
    @Field(displayName = "站点")
    private String website;

    @Base
    @Field(displayName = "module的作者", defaultValue = PackageConstants.author)
    private String author;

    @Base
    @Field(displayName = "演示应用", required = true, defaultValue = "false")
    private Boolean demo;

    @Base
    @Field(displayName = "web应用", required = true, defaultValue = "true")
    private Boolean web;

    @Base
    @Field(displayName = "许可证", defaultValue = "PEEL1")
    private SoftwareLicenseEnum license;

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
    @Field(displayName = "自建应用", required = true, defaultValue = "false")
    private Boolean selfBuilt;

    @Base
    @Field(displayName = "元数据来源", defaultValue = "CODE")
    private MetaSourceEnum metaSource;

    @Base
    @Field.Enum
    @Field(displayName = "应用支持的客户端类型列表", multi = true, defaultValue = "3")
    private List<ClientTypeEnum> clientTypes;

    @Base
    @Field.Enum
    @Field(displayName = "可见", defaultValue = "true")
    private ActiveEnum show;

    @Base
    @Field(displayName = "默认主页模型编码", invisible = true)
    private String defaultHomePageModel;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultHomePageModel"})
    @Field(displayName = "主页模型编码", invisible = true, store = NullableBoolEnum.TRUE)
    private String homePageModel;

    @Base
    @Field(displayName = "默认主页动作名称", invisible = true)
    private String defaultHomePageName;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultHomePageName"})
    @Field(displayName = "主页动作名称", invisible = true, store = NullableBoolEnum.TRUE)
    private String homePageName;

    @Base
    @Field.String(size = 512)
    @Field(displayName = "默认图标", invisible = true)
    private String defaultLogo;

    @Base
    @Field.Related.Internal(store = false)
    @Field.Related({"defaultLogo"})
    @Field.String(size = 512)
    @Field(displayName = "图标", store = NullableBoolEnum.TRUE)
    private String logo;

    private Class<?> moduleClazz;

    private String[] packagePrefix;

    private Map<String, String[]> dependentPackagePrefix;

    public String getCategory() {
        String category = (String) _d.get("category");
        if (category == null) {
            category = (String) _d.get("defaultCategory");
        }
        return category;
    }

    public String getHomePageModel() {
        String homePageModel = (String) _d.get("homePageModel");
        if (homePageModel == null) {
            homePageModel = (String) _d.get("defaultHomePageModel");
        }
        return homePageModel;
    }

    public String getHomePageName() {
        String homePageName = (String) _d.get("homePageName");
        if (homePageName == null) {
            homePageName = (String) _d.get("defaultHomePageName");
        }
        return homePageName;
    }

    public String getLogo() {
        String logo = (String) _d.get("logo");
        if (logo == null) {
            logo = (String) _d.get("defaultLogo");
        }
        return logo;
    }

    @Function.Advanced(displayName = "初始化数据", type = FunctionTypeEnum.QUERY)
    @Function
    public ModuleDefinition construct(ModuleDefinition moduleDefinition) {
        if (null == moduleDefinition) {
            return null;
        }
        if (null == moduleDefinition.getName()) {
            return moduleDefinition;
        }
        // displayName
        moduleDefinition.setDisplayName(Optional.of(moduleDefinition).map(ModuleDefinition::getDisplayName).orElse(moduleDefinition.getName()));
        // version
        VersionGenerator versionGenerator = CommonApiFactory.getVersionGenerator();
        String defaultVersion = versionGenerator.generate();
        moduleDefinition.setLatestVersion(Optional.of(moduleDefinition).map(ModuleDefinition::getLatestVersion).orElse(defaultVersion));
        return moduleDefinition;
    }

    @Override
    public String getSignModel() {
        return MODEL_MODEL;
    }

    @Override
    public String stringify() {
        return DiffUtils.stringify(this, "homePage", "homePageModel", "homePageName", "moduleCategory", "category", "logo");
    }

}
