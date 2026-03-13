package pro.shushi.pamirs.channel.core.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.channel.core.config.ChannelConfig;
import pro.shushi.pamirs.channel.enmu.DumpStateEnum;
import pro.shushi.pamirs.channel.enmu.IncrementEnum;
import pro.shushi.pamirs.channel.meta.Analyzer;
import pro.shushi.pamirs.channel.meta.Enhance;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.util.IndexNaming;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.EnhanceModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EnhanceModelScanner
 *
 * @author yakir on 2022/09/06 13:41.
 */
@Slf4j
@Component
public class EnhanceModelScanner {

    @Autowired
    private ChannelConfig channelConfig;

    public List<ChannelModel> enhanceModel() {

        List<String> basePackages = channelConfig.basePackages();
        Set<Class<?>> klassSet = ClassUtils.getClassesByPacks(basePackages);
        List<ChannelModel> channelModels = new ArrayList<>();
        for (Class<?> klass : klassSet) {
            if (null == klass || klass.isMemberClass() || klass.isAnonymousClass() || klass.isAnnotation()
                    || klass.isArray() || klass.isEnum() || klass.isInterface()) {
                continue;
            }

            String modelModel = null;
            try {
                Field field = klass.getDeclaredField("MODEL_MODEL");
                modelModel = (String) field.get(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                continue;
            }

            if (null == modelModel) {
                continue;
            }

            Enhance enhance = AnnotationUtils.getAnnotation(klass, Enhance.class);
            if (null == enhance) {
                continue;
            }
            Model.Advanced advanced = AnnotationUtils.getAnnotation(klass, Model.Advanced.class);
            if (null == advanced) {
                continue;
            }
            String[] inheritedArr = advanced.inherited();
            String inherited = Optional.ofNullable(inheritedArr)
                    .filter(_arr -> _arr.length > 0)
                    .map(_arr -> _arr[0])
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

            if (null == inherited || !StringUtils.equalsAnyIgnoreCase(EnhanceModel.MODEL_MODEL, inherited)) {
                continue;
            }

            ModelConfig modelCfg = PamirsSession.getContext().getModelConfig(modelModel);

            if (null == modelCfg) {
                continue;
            }

            if (null == modelCfg.getType() || !ModelTypeEnum.PROXY.equals(modelCfg.getType())) {
                log.error("Enhance model type must be proxy model");
                continue;
            }

            log.warn("Enhance Model Inherited: [{}] [{}]", modelModel, inherited);

            String origin = Optional.ofNullable(modelCfg.getSuperModels())
                    .filter(_arr -> !_arr.isEmpty())
                    .map(_arr -> _arr.get(0))
                    .filter(StringUtils::isNotBlank)
                    .orElse(null);

            String alias = Optional.ofNullable(enhance.alias())
                    .filter(StringUtils::isNotBlank)
                    .orElse(IndexNaming.aliasByModel(modelModel));
            String index = Optional.ofNullable(enhance.index())
                    .filter(StringUtils::isNotBlank)
                    .orElse(IndexNaming.aliasByModel(modelModel));
            String replicas = enhance.replicas();
            String shards = enhance.shards();
            Boolean reAlias = enhance.reAlias();
            IncrementEnum increment = enhance.increment();
            long batchSize = enhance.batchSize();
            Analyzer[] analyzers = Optional.ofNullable(enhance.analyzers()).orElse(new Analyzer[]{});

            Map<String, Map<String, String>> anzMap = new HashMap<>();
            for (Analyzer analyzer : analyzers) {
                String field = analyzer.value();
                if (StringUtils.isBlank(field)) {
                    continue;
                }
                String anz = analyzer.analyzer();
                String searchAnz = analyzer.searchAnalyzer();

                Map<String, String> map = new HashMap<>();
                map.put("field", field);
                map.put("analyzer", anz);
                map.put("searchAnalyzer", searchAnz);
                anzMap.put(field, map);
            }

            ChannelModel channel = new ChannelModel();
            channel.setModel(modelModel);
            channel.setIndex(index);
            channel.setOrigin(origin);
            channel.setAlias(alias);
            channel.setReplicas(replicas);
            channel.setShards(shards);
            channel.setReAlias(reAlias);
            channel.setIncrement(increment);
            channel.setBatchSize(batchSize);
            channel.setDisplayName(modelCfg.getDisplayName());
            channel.setSystemSource(SystemSourceEnum.MANUAL);
            channel.setDumpState(DumpStateEnum.INIT);
            channel.setModule(modelCfg.getModule());
            channel.setAnalyzers(new ArrayList<>(anzMap.values()));
            channelModels.add(channel);
        }

        Set<String> models = channelModels.stream()
                .map(ChannelModel::getModel)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(models)) {
            return channelModels;
        }

        IWrapper<ChannelModel> qw = Pops.<ChannelModel>lambdaQuery().from(ChannelModel.MODEL_MODEL).in(ChannelModel::getModel, models);
        List<ChannelModel> channelModelList = new ChannelModel().queryList(qw);
        if (CollectionUtils.isEmpty(channelModelList)) {
            return channelModels;
        }
        Map<String, ChannelModel> channelModelMap = channelModelList.stream()
                .collect(Collectors.toMap(ChannelModel::getModel, Function.identity(), (_a, _b) -> _a));

        for (ChannelModel channelModel : channelModels) {
            ChannelModel fromDb = channelModelMap.get(channelModel.getModel());
            if (null != fromDb) {
                channelModel.setDumpState(fromDb.getDumpState());
                channelModel.setIncrement(fromDb.getIncrement());
            }
        }

        return channelModels;
    }

}
