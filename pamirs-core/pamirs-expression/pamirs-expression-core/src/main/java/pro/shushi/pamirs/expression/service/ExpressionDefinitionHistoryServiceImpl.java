package pro.shushi.pamirs.expression.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.expression.api.ExpressionDefinitionHistoryService;
import pro.shushi.pamirs.expression.api.ExpressionDefinitionService;
import pro.shushi.pamirs.expression.model.ExpressionDHistory;
import pro.shushi.pamirs.expression.model.ExpressionDefine;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.utils.ArgUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author drome
 * @date 2022/2/114:28 下午
 */
@Slf4j
@Base
@Service
@Fun(ExpressionDefinitionHistoryService.FUN_NAMESPACE)
public class ExpressionDefinitionHistoryServiceImpl implements ExpressionDefinitionHistoryService {

    @Autowired
    private ExpressionDefinitionService expressionDefinitionService;

    @Override
    public List<ExpressionDHistory> createByKeyPrefix(String keyPrefix, String version) {
        List<ExpressionDefine> existed = expressionDefinitionService.queryByKeyPrefix(keyPrefix);
        if (CollectionUtils.isEmpty(existed)) {
            return new ArrayList<>();
        }

        log.debug("保存表达式历史表数据，keyPrefix:{}, existed-size:{}", keyPrefix, existed.size());
        //根据表达式,构造历史记录
        List<ExpressionDHistory> histories = existed.stream()
                .map(exp -> {
                    ExpressionDHistory history = ArgUtils.convert(ExpressionDefine.MODEL_MODEL, ExpressionDHistory.MODEL_MODEL, exp);
                    //置空id
                    history.unsetId();
                    //版本
                    history.setVersion(version);
                    return history;
                }).collect(Collectors.toList());
        // 在从基线(标品)同步目标环境的设计器时，history表存在(code+version)的记录已经存在的情况
        Models.origin().createOrUpdateBatch(histories);
        return histories;
    }

    @Override
    public Boolean restoreByKeyPrefix(String keyPrefix, String version) {
        List<ExpressionDHistory> histories = Models.origin().queryListByWrapper(
                Pops.<ExpressionDHistory>lambdaQuery()
                        .from(ExpressionDHistory.MODEL_MODEL)
                        .eq(ExpressionDHistory::getVersion, version)
                        .likeRight(ExpressionDHistory::getKey, keyPrefix)
        );
        if (CollectionUtils.isEmpty(histories)) {
            return Boolean.TRUE;
        }

        //根据历史记录,构造表达式对象
        List<ExpressionDefine> expressionDefines = histories
                .stream()
                .map(his -> {
                    ExpressionDefine exp = ArgUtils.convert(ExpressionDHistory.MODEL_MODEL, ExpressionDefine.MODEL_MODEL, his);
                    //置空id
                    exp.unsetId();
                    return exp;
                })
                .collect(Collectors.toList());
        //表达式的code是唯一索引,通过该字段更新or创建
        Models.origin().createOrUpdateBatch(expressionDefines);
        return Boolean.TRUE;
    }
}
