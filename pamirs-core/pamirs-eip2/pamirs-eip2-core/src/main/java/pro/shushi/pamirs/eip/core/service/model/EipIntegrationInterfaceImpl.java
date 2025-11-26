package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrationInterfaceService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Fun(EipIntegrationInterfaceService.FUN_NAMESPACE)
public class EipIntegrationInterfaceImpl implements EipIntegrationInterfaceService {

    @Resource
    private EipLogStrategyService eipLogStrategyService;
    @Resource
    private EipLogDailyCountService eipLogDailyCountService;


    @Override
    @Function
    public Integer createOrUpdate(EipIntegrationInterface eipIntegrationInterface) {
        return eipIntegrationInterface.createOrUpdate();
    }

    @Override
    @Function
    public EipIntegrationInterface create(EipIntegrationInterface eipIntegrationInterface) {
        return eipIntegrationInterface.create();
    }

    @Override
    @Function
    public EipIntegrationInterface queryById(Long id) {
        return new EipIntegrationInterface().queryById(id);
    }

    @Override
    public EipIntegrationInterface queryOne(EipIntegrationInterface one) {
        return one.queryOne();
    }

    @Override
    public List<EipIntegrationInterface> queryListByWrapper(IWrapper<EipIntegrationInterface> wrapper) {
        return new EipIntegrationInterface().queryList(wrapper);
    }

    @Override
    @Function
    public Integer updateById(EipIntegrationInterface update) {
        return update.updateById();
    }

    @Override
    @Function
    public Pagination<EipIntegrationInterface> queryPage(Pagination<EipIntegrationInterface> page, IWrapper<EipIntegrationInterface> queryWrapper) {
        Pagination<EipIntegrationInterface> result = Models.origin().queryPage(page, queryWrapper);
        List<EipIntegrationInterface> resultList = result.getContent();
        if (CollectionUtils.isEmpty(resultList)) {
            return result;
        }
        outConvert(resultList);

        // 集成接口日志统计
        eipLogDailyCountService.fillIntegrationLogCountData(resultList);

        // 填充应用名称
        fillEipIntegrate(resultList);
        return result;
    }

    private void fillEipIntegrate(List<EipIntegrationInterface> resultList) {
        Set<Long> integrateIds = resultList.stream()
                .map(EipIntegrationInterface::getIntegrateId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (CollectionUtils.isNotEmpty(integrateIds)) {
            List<EipIntegrate> eipIntegrates = Models.data().queryListByWrapper(Pops.<EipIntegrate>lambdaQuery()
                    .from(EipIntegrate.MODEL_MODEL)
                    .in(EipIntegrate::getId, integrateIds)
                    .select(EipIntegrate::getName, EipIntegrate::getId)
            );

            Map<Long, EipIntegrate> integrateMap = eipIntegrates.stream().collect(
                    Collectors.toMap(EipIntegrate::getId, i -> i));
            for (EipIntegrationInterface eipIntegrationInterface : resultList) {
                Long integrateId = eipIntegrationInterface.getIntegrateId();
                if (integrateId == null) {
                    continue;
                }
                EipIntegrate eipIntegrate = integrateMap.get(integrateId);
                if (eipIntegrate != null) {
                    eipIntegrationInterface.setEipIntegrate(eipIntegrate);
                }
            }
        }
    }

    @Override
    @Function
    public EipIntegrationInterface queryByInterfaceName(String interfaceName) {
        return new EipIntegrationInterface().setInterfaceName(interfaceName).queryOne();
    }

    @Override
    @Function
    public List<EipIntegrationInterface> queryByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return new EipIntegrationInterface().queryList(Pops.<EipIntegrationInterface>lambdaQuery()
                .from(EipIntegrationInterface.MODEL_MODEL)
                .in(EipIntegrationInterface::getId, ids)
        );
    }

    private void outConvert(List<EipIntegrationInterface> resultList) {
        List<String> interfaceNameList = resultList.stream()
                .map(EipIntegrationInterface::getInterfaceName)
                .collect(Collectors.toList());

        Set<String> ignoreFrequencyInterfaceNames = eipLogStrategyService
                .queryIgnoreFrequencyList(interfaceNameList, InterfaceTypeEnum.INTEGRATION);

        for (EipIntegrationInterface eii : resultList) {
            eii.setIsIgnoreLogFrequency(ignoreFrequencyInterfaceNames.contains(eii.getInterfaceName()));
        }
    }
}