package pro.shushi.pamirs.eip.core.service.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.InterfaceTypeEnum;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.eip.api.service.model.EipOpenInterfaceService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogDailyCountService;
import pro.shushi.pamirs.eip.api.strategy.service.EipLogStrategyService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.DateFormatEnum;
import pro.shushi.pamirs.meta.util.DateUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
@Fun(EipOpenInterfaceService.FUN_NAMESPACE)
public class EipOpenInterfaceServiceImpl implements EipOpenInterfaceService {

    @Autowired
    private EipService eipService;

    @Autowired
    private EipLogStrategyService eipLogStrategyService;

    @Autowired
    private EipLogDailyCountService eipLogDailyCountService;

    @Override
    @Transactional
    public EipOpenInterface create(EipOpenInterface data) {
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_REQUEST_ERROR).errThrow();
        }
        // check
        if (StringUtils.isEmpty(data.getUri()) || StringUtils.isEmpty(data.getInterfaceName())) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_CREATE_REQUEST_ERROR).errThrow();
        }
        if (Models.origin().count(
                Pops.<EipOpenInterface>lambdaQuery()
                        .from(EipOpenInterface.MODEL_MODEL)
                        .eq(EipOpenInterface::getUri, data.getUri())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_CREATE_REQUEST_ERROR).appendMsg("uri重复").errThrow();
        }
        // 处理忽略日志频率配置
        if (data.getIsIgnoreLogFrequency() != null) {
            if (data.getIsIgnoreLogFrequency()) {
                eipLogStrategyService.ignoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
            } else {
                eipLogStrategyService.cancelIgnoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
            }
        }
        data.construct();

        EipOpenInterface result = data.create();
        eipService.registerApi(result);
        return result;
    }

    @Override
    @Transactional
    public EipOpenInterface update(EipOpenInterface data) {
        if (data == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_REQUEST_ERROR).errThrow();
        }
        if (data.getId() == null && StringUtils.isNotEmpty(data.getInterfaceName())) {
            EipOpenInterface target = new EipOpenInterface().setInterfaceName(data.getInterfaceName()).queryOne();
            data.setId(target.getId());
        }
        if (data.getId() == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_REQUEST_ERROR).errThrow();
        }
        if (Models.origin().count(
                Pops.<EipOpenInterface>lambdaQuery()
                        .from(EipOpenInterface.MODEL_MODEL)
                        .ne(EipOpenInterface::getId, data.getId())
                        .eq(EipOpenInterface::getUri, data.getUri())
        ) > 0) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_CREATE_REQUEST_ERROR).appendMsg("uri重复").errThrow();
        }
        // 处理忽略日志频率配置
        Boolean isIgnoreLogFrequency = data.getIsIgnoreLogFrequency();
        if (isIgnoreLogFrequency != null) {
            if (isIgnoreLogFrequency) {
                eipLogStrategyService.ignoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
            } else {
                eipLogStrategyService.cancelIgnoreFrequency(data.getInterfaceName(), InterfaceTypeEnum.OPEN);
            }
        }
        // 不允许更新
        data.unsetInterfaceName();
        data.unsetDataStatus();

        data.updateById();

        EipOpenInterface result = data.queryById();
        eipService.registerApi(result);
        if (isIgnoreLogFrequency != null) {
            result.setIsIgnoreLogFrequency(isIgnoreLogFrequency);
        }
        return result;
    }

    @Override
    @Transactional
    public Boolean enable(EipOpenInterface data) {
        _changeStatus(data, DataStatusEnum.ENABLED, _target -> {
            eipService.registerApi(_target);
        });
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean disable(EipOpenInterface data) {
        _changeStatus(data, DataStatusEnum.DISABLED, _target -> {
            eipService.cancellationApi(_target);
        });
        return Boolean.TRUE;
    }

    private void _changeStatus(EipOpenInterface data, DataStatusEnum targetStatus, Consumer<EipOpenInterface> after) {
        EipOpenInterface target = null;
        if (data.getId() != null) {
            target = data.queryById();
        } else if (data.getInterfaceName() != null) {
            // interfaceName
            target = data.queryOne();
        }
        if (target == null) {
            throw PamirsException.construct(EipExpEnumerate.OPEN_INTERFACE_NOT_EXIST).errThrow();
        }
        EipOpenInterface update = new EipOpenInterface();
        update.setId(target.getId());
        update.setDataStatus(targetStatus);
        update.updateById();

        target.setDataStatus(targetStatus);
        after.accept(target);
    }

    @Override
    public Pagination<EipOpenInterface> queryPage(Pagination<EipOpenInterface> page, IWrapper<EipOpenInterface> queryWrapper) {
        Pagination<EipOpenInterface> result = Models.origin().queryPage(page, queryWrapper);
        List<EipOpenInterface> resultList = result.getContent();
        if (CollectionUtils.isEmpty(resultList)) {
            return result;
        }
        outConvert(resultList);
        Map<String,Object> data = queryWrapper.getQueryData();
        Date startDate = null, endDate = null;
        if(data.containsKey("searchDate")) {
            List<String> searchDatas = (List<String>) data.get("searchDate");
            startDate = Optional.ofNullable(searchDatas.get(0)).map(t-> DateUtils.formatDate(t, DateFormatEnum.DATE.value())).orElse( null);
            endDate = Optional.ofNullable(searchDatas.get(1)).map(t-> DateUtils.formatDate(t,DateFormatEnum.DATE.value())).orElse( null);
            if(startDate != null){
                startDate = new DateTime(startDate).withTimeAtStartOfDay().toDate();
            }
            if(endDate != null){
                endDate = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate();
            }
        }
        eipLogDailyCountService.fillOpenLogCountData(resultList, startDate, endDate);
        return result;
    }

    private void outConvert(List<EipOpenInterface> resultList) {
        List<String> interfaceNameList = resultList.stream()
                .map(EipOpenInterface::getInterfaceName)
                .collect(Collectors.toList());

        Set<String> ignoreFrequencyInterfaceNames = eipLogStrategyService
                .queryIgnoreFrequencyList(interfaceNameList, InterfaceTypeEnum.OPEN);

        for (EipOpenInterface eii : resultList) {
            eii.setIsIgnoreLogFrequency(ignoreFrequencyInterfaceNames.contains(eii.getInterfaceName()));
        }
    }
}
