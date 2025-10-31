package pro.shushi.pamirs.framework.configure.sys;

import com.google.common.collect.Sets;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.framework.configure.MetaConfiguration;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.configure.MetaModelFetcher;
import pro.shushi.pamirs.meta.api.dto.meta.MetaModel;
import pro.shushi.pamirs.meta.common.constants.PackageConstants;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.util.ClassUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Consumer;

import static pro.shushi.pamirs.meta.common.constants.PackageConstants.*;

/**
 * 获取系统元模型
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@Slf4j
@Component
public class DefaultMetaModelFetcher implements MetaModelFetcher {

    @Resource
    private MetaConfiguration metaConfiguration;

    private static final HoldKeeper<List<MetaModel>> fetchMetaModelListHolder = new HoldKeeper<>();

    @Override
    public List<MetaModel> fetchMetaModelList() {
        return fetchMetaModelListHolder.supply(() -> {
            List<MetaModel> metaModels = new ArrayList<>();
            collectMetaList(metaModels,
                    (t, e, m) -> t.add(new MetaModel().setGroup(m.value()).setCore(Sets.newHashSet(e.core())).setPriority(e.priority())),
                    t -> t.sort(Comparator.comparing(MetaModel::getPriority)));
            return metaModels;
        });
    }

    private static final HoldKeeper<List<String>> fetchMetaModelsHolder = new HoldKeeper<>();

    @Override
    public List<String/*model*/> fetchMetaModels() {
        return fetchMetaModelsHolder.supply(() -> {
            List<String> metaModels = new ArrayList<>();
            collectMetaList(metaModels, (t, e, m) -> t.add(m.value()), null);
            return metaModels;
        });
    }

    private static final HoldKeeper<Set<Class<?>>> fetchMetaClassesHolder = new HoldKeeper<>();

    @Override
    public Set<Class<?>> fetchMetaClasses() {
        return fetchMetaClassesHolder.supply(() -> {
            List<String> metaPackages = new ArrayList<>();
            if (!CollectionUtils.isEmpty(metaConfiguration.getMetaPackages())) {
                metaPackages.addAll(metaConfiguration.getMetaPackages());
            }
            metaPackages.add(0, PackageConstants.PACKAGE_META);
            metaPackages.add(PACKAGE_META_ABSTRACT);
            metaPackages.add(PACKAGE_META_BASE);
            metaPackages.add(PACKAGE_META_BASE_RES);
            metaPackages.add(PACKAGE_SID_MODEL);
            return ClassUtils.getClassesByPacks(metaPackages.toArray(new String[0]));
        });
    }

    private static final HoldKeeper<Map<String, Integer>> fetchMetaModelPriorityMapHolder = new HoldKeeper<>();

    @Override
    public Map<String/*model*/, Integer> fetchMetaModelPriorityMap() {
        return fetchMetaModelPriorityMapHolder.supply(() -> {
            Map<String, Integer> metaModelPriorityMap = new HashMap<>();
            collectMetaList(metaModelPriorityMap, (t, e, m) -> t.put(m.value(), e.priority()), null);
            return metaModelPriorityMap;
        });
    }

    private <T> void collectMetaList(T container, AnnotationConsumer<T> consumer, Consumer<T> listConsumer) {
        Set<Class<?>> metaClasses = fetchMetaClasses();
        for (Class<?> clazz : metaClasses) {
            pro.shushi.pamirs.meta.annotation.sys.MetaModel metaModelAnnotation = AnnotationUtils.getAnnotation(clazz, pro.shushi.pamirs.meta.annotation.sys.MetaModel.class);
            Model.model modelModelAnnotation = AnnotationUtils.getAnnotation(clazz, Model.model.class);
            if (null != metaModelAnnotation && null != modelModelAnnotation) {
                consumer.accept(container, metaModelAnnotation, modelModelAnnotation);
            }
        }
        if (null != listConsumer) {
            listConsumer.accept(container);
        }
    }

    interface AnnotationConsumer<T> {
        void accept(T t, pro.shushi.pamirs.meta.annotation.sys.MetaModel metaModelAnnotation, Model.model modelModelAnnotation);
    }

}
