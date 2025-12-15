package pro.shushi.pamirs.framework.configure.annotation.core.converter.module;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.configure.annotation.core.check.MetaUniqueChecker;
import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.Module;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Boot;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelConverter;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelReflectSigner;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.api.dto.meta.MetaNames;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.*;
import pro.shushi.pamirs.meta.util.SystemSourceUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_MODULE_CONFIG_CONFLICT_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_MODULE_NO_MODULE_ERROR;

/**
 * 模块注解转化器
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/18 2:59 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
@Slf4j
@Component
public class ModuleConverter implements ModelConverter<ModuleDefinition, Class> {

    @Override
    public int priority() {
        return 1;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public Result validate(ExecuteContext context, MetaNames names, Class source) {
        // 可以在这里进行注解配置建议
        Module moduleAnnotation = AnnotationUtils.getAnnotation(source, Module.class);
        Module.module moduleModuleAnnotation = AnnotationUtils.getAnnotation(source, Module.module.class);
        Module.Advanced moduleAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Module.Advanced.class);
        Result result = new Result();

        if (null == moduleAnnotation) {
            if (null != moduleModuleAnnotation || null != moduleAdvancedAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_MODULE_NO_MODULE_ERROR)
                        .append(MessageFormat
                                .format("请为模块类配置@Module，否则系统会忽略该模块，class:{0}",
                                        source.getName())));
                context.error();
            }
            context.broken();
            return result.error();
        }
        {
            Model modelAnnotation = AnnotationUtils.getAnnotation(source, Model.class);
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(source, Model.model.class);
            Model.Advanced modelAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Model.Advanced.class);
            Model.Constraints modelConstraintsAnnotation = AnnotationUtils.getAnnotation(source, Model.Constraints.class);
            Fun funAnnotation = AnnotationUtils.getAnnotation(source, Fun.class);
            Dict dictAnnotation = AnnotationUtils.getAnnotation(source, Dict.class);
            if (null != modelAnnotation || null != modelModelAnnotation || null != modelAdvancedAnnotation || null != modelConstraintsAnnotation
                    || null != funAnnotation || null != dictAnnotation) {
                result.addMessage(new Message().setLevel(InformationLevelEnum.ERROR)
                        .error(BASE_MODULE_CONFIG_CONFLICT_ERROR)
                        .append(MessageFormat
                                .format("请不要在模块类上配置@Model、@Model.model、@Model.Advanced、@Model.Constraints、@Fun、@Dict注解，class:{0}",
                                        source.getName())));
                context.error().broken();
                return result.error();
            }
        }

        // 模块编码重复检查
        @SuppressWarnings("unchecked")
        String module = Spider.getExtension(ModelReflectSigner.class, ModuleDefinition.MODEL_MODEL).sign(names, source);
        MetaUniqueChecker.check(context, result, ModuleDefinition.MODEL_MODEL, module, source.getName());
        return result;
    }

    @Override
    public ModuleDefinition convert(MetaNames names, Class source, ModuleDefinition moduleDefinition) {
        Module moduleAnnotation = AnnotationUtils.getAnnotation(source, Module.class);
        Module.Advanced moduleAdvancedAnnotation = AnnotationUtils.getAnnotation(source, Module.Advanced.class);
        Module.Ds moduleDsAnnotation = AnnotationUtils.getAnnotation(source, Module.Ds.class);
        Module.Hook hookAnnotation = AnnotationUtils.getAnnotation(source, Module.Hook.class);
        Boot bootAnnotation = AnnotationUtils.getAnnotation(source, Boot.class);

        // 填充模型
        @SuppressWarnings("unchecked")
        String module = Spider.getExtension(ModelReflectSigner.class, ModuleDefinition.MODEL_MODEL).sign(names, source);
        names.setModule(module);
        moduleDefinition.setModule(module);
        assert moduleAnnotation != null;
        String name = Optional.of(moduleAnnotation.name()).filter(StringUtils::isNotBlank).orElse(module);
        names.setModuleName(name);
        String abbr = Optional.ofNullable(moduleAdvancedAnnotation).map(Module.Advanced::abbr).filter(StringUtils::isNotBlank).orElse(null);
        if (null != abbr) {
            names.setModuleAbbr(abbr);
        }
        String displayName = Optional.of(moduleAnnotation.displayName()).orElse(name);
        String dsKey = Optional.ofNullable(DsApi.get()).map(DsApi::fetchModuleDsMap).map(v -> v.get(module)).orElse(null);
        if (null == dsKey) {
            dsKey = Optional.ofNullable(moduleDsAnnotation).map(Module.Ds::value).filter(StringUtils::isNotBlank).orElse(null);
        }
        names.setDsKey(dsKey);
        String summary = moduleAnnotation.summary();
        String description = Optional.ofNullable(moduleAdvancedAnnotation).map(Module.Advanced::description).filter(StringUtils::isNotBlank).orElse(null);
        List<ClientTypeEnum> clientTypeEnums = Arrays.stream(moduleAnnotation.clientTypes()).collect(Collectors.toList());
        ActiveEnum show = moduleAnnotation.show();
        Long priority = moduleAnnotation.priority();
        String latestVersion = moduleAnnotation.version();
        String category = Optional.of(moduleAnnotation).map(Module::category).filter(StringUtils::isNotBlank).orElse(null);
        List<String> moduleDependencies = Optional.of(moduleAnnotation.dependencies()).map(PStringUtils::trim).orElseGet(ArrayList::new);
        if (!ModuleConstants.MODULE_BASE.equals(module) && !moduleDependencies.contains(ModuleConstants.MODULE_BASE)) {
            moduleDependencies.add(0, ModuleConstants.MODULE_BASE);
        }
        List<String> moduleExclusions = PStringUtils.trim(moduleAnnotation.exclusions());
        List<String> moduleUpstreams = PStringUtils.trim(moduleAnnotation.upstreams());
        List<String> excludeHooks = null == hookAnnotation ? null : PStringUtils.trim(hookAnnotation.excludes());
        Boolean application = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.application();
        String author = Optional.ofNullable(moduleAdvancedAnnotation).map(Module.Advanced::author).filter(StringUtils::isNotBlank).orElse(null);
        String contributors = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.contributors();
        Boolean demo = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.demo();
        SoftwareLicenseEnum license = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.license();
        String maintainer = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.maintainer();
        Boolean selfBuilt = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.selfBuilt();
        Boolean toBuy = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.toBuy();
        String url = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.url();
        Boolean web = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.web();
        String website = null == moduleAdvancedAnnotation ? null : moduleAdvancedAnnotation.website();
        Boolean boot = null == bootAnnotation ? Boolean.FALSE : bootAnnotation.value();
        SystemSourceEnum systemSource = SystemSourceUtils.fetch(source);

        moduleDefinition.setModule(module)
                .setName(name)
                .setAbbr(abbr)
                .setDisplayName(displayName)
                .setDsKey(dsKey)
                .setSummary(summary)
                .setDescription(description)
                .setClientTypes(clientTypeEnums)
                .setShow(show)
                .setPriority(priority)
                .setLatestVersion(latestVersion)
                .setDefaultCategory(category)
                .setModuleDependencies(moduleDependencies)
                .setModuleExclusions(moduleExclusions)
                .setModuleUpstreams(moduleUpstreams)
                .setExcludeHooks(excludeHooks)
                .setApplication(application)
                .setAuthor(author)
                .setContributors(contributors)
                .setDemo(demo)
                .setLicense(license)
                .setMaintainer(maintainer)
                .setSelfBuilt(selfBuilt)
                .setToBuy(toBuy)
                .setUrl(url)
                .setWeb(web)
                .setWebsite(website)
                .setBoot(boot)
                .setDefaultHomePageModel(null)
                .setDefaultHomePageName(null)
                .setDefaultLogo(null)
                .setSystemSource(systemSource)
        ;
        return moduleDefinition;
    }

    @Override
    public String group() {
        return ModuleDefinition.MODEL_MODEL;
    }

    @Override
    public Class<?> metaModelClazz() {
        return ModuleDefinition.class;
    }

}
