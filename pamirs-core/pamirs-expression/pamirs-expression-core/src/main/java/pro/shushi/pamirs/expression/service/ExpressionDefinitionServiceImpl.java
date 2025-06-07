package pro.shushi.pamirs.expression.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.expression.api.ExpressionDefinitionService;
import pro.shushi.pamirs.expression.enmu.ExpressionBlockType;
import pro.shushi.pamirs.expression.enmu.ExpressionCellType;
import pro.shushi.pamirs.expression.enmu.ExpressionExpEnumerate;
import pro.shushi.pamirs.expression.enmu.ExpressionRowType;
import pro.shushi.pamirs.expression.model.ExpressionDefine;
import pro.shushi.pamirs.expression.tmodel.ExpressionBlock;
import pro.shushi.pamirs.expression.tmodel.ExpressionCell;
import pro.shushi.pamirs.expression.tmodel.ExpressionDisplay;
import pro.shushi.pamirs.expression.tmodel.ExpressionRow;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author drome
 * @date 2021/8/129:58 上午
 */
@Base
@Service
@Fun(ExpressionDefinitionService.FUN_NAMESPACE)
public class ExpressionDefinitionServiceImpl implements ExpressionDefinitionService {

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public Integer createOrUpdate(ExpressionDefine data) {
        createOrUpdateBefore(data);
        return data.createOrUpdate();
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public List<ExpressionDefine> createOrUpdateBatch(List<ExpressionDefine> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return dataList;
        }
        for (ExpressionDefine data : dataList) {
            createOrUpdateBefore(data);
        }
        return Models.origin().createOrUpdateBatchWithResult(dataList).getData();
    }

    private void createOrUpdateBefore(ExpressionDefine data) {
        if (data.getId() == null && StringUtils.isBlank(data.getCode())) {
            //创建 or 根据作用域字段来更新
            baseCheck0(data);
            data.setCode(buildCode0(data));
        } else {
            //更新. 不允许更新定位表达式作用域的字段
            data.unsetModel();
            data.unsetField();
            data.unsetKey();
        }
        String originalExp = genExpressionStr(data, ExpressionDisplay::getOriginal, ExpressionDisplay::setOriginal);
        String translationExp = genExpressionStr(data, ExpressionDisplay::getTranslation, ExpressionDisplay::setTranslation);
        ExpressionDisplay expressionDisplay = new ExpressionDisplay();
        expressionDisplay.setOriginal(originalExp);
        expressionDisplay.setTranslation(translationExp);
        data.setExpressionDisplay(expressionDisplay);
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public List<ExpressionDefine> createBatch(List<ExpressionDefine> dataList) {
        for (ExpressionDefine data : dataList) {
            baseCheck0(data);
            data.setCode(buildCode0(data));
        }
        return Models.origin().createBatch(dataList);
    }


    private void baseCheck0(ExpressionDefine data) {
        if (StringUtils.isBlank(data.getModel())) {
            throw PamirsException.construct(ExpressionExpEnumerate.EXPRESSION_OBJ_IS_NULL).errThrow();
        }
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public Boolean deleteByCode(ExpressionDefine data) {
        if (StringUtils.isBlank(data.getCode())) {
            String genCode = buildCode0(data);
            data.setCode(genCode);
        }
        return data.deleteByUnique();
    }


    /**
     * 根据表达式作用的对象/字段/属性 构造一个唯一编码
     *
     * @param data
     * @return
     */
    private String buildCode0(ExpressionDefine data) {
        return Stream.of(data.getModel(), data.getField(), data.getKey())
                .map(i -> StringUtils.isBlank(i) ? null : i)
                .collect(Collectors.joining(CharacterConstants.SEPARATOR_UNDERLINE));
    }

    private String genExpressionStr(ExpressionDefine data, Function<ExpressionDisplay, String> disPlay, BiConsumer<ExpressionDisplay, String> putDisplay) {
        List<ExpressionRow> rowList = data.getRowList();
        if (rowList == null) return null;
        List<String> rowStrList = new ArrayList<>();
        for (ExpressionRow row : rowList) {
            List<ExpressionBlock> blockList = row.getBlockList();
            if (blockList == null) continue;
            String rowStr;
            List<String> blockStrList = new ArrayList<>();
            //fixme 最后一行不需要拼连接符
            if (ExpressionRowType.LEFT_BRACKET.equals(row.getRowType())) {
                rowStr = CharacterConstants.LEFT_BRACKET;
            } else if (ExpressionRowType.RIGHT_BRACKET.equals(row.getRowType())) {
                rowStr = CharacterConstants.RIGHT_BRACKET;
            } else {
                for (ExpressionBlock block : blockList) {
                    String singleBlockStr;
                    if (ExpressionBlockType.FUN.equals(block.getBlockType())) {
                        singleBlockStr = genFunBlockStr(block, disPlay, putDisplay);
                        putDisplay.accept(block, singleBlockStr);
                    } else if (ExpressionBlockType.CONNECTOR.equals(block.getBlockType())) {
                        ExpressionCell connector = block.getConnector();
                        singleBlockStr = Optional.ofNullable(connector).map(disPlay).orElse(CharacterConstants.SEPARATOR_EMPTY);
                        if (StringUtils.isBlank(singleBlockStr)) {
                            singleBlockStr = Optional.ofNullable(block).map(disPlay).orElse(CharacterConstants.SEPARATOR_EMPTY);
                        }
                    } else {
                        singleBlockStr = genCommonBlockStr(block, disPlay, putDisplay);
                    }
                    blockStrList.add(Optional.ofNullable(singleBlockStr).orElse(CharacterConstants.SEPARATOR_EMPTY));
                }
                rowStr = wrapBracket(StringUtils.join(blockStrList, CharacterConstants.SEPARATOR_BLANK));
                rowStr += CharacterConstants.SEPARATOR_BLANK + (Optional.ofNullable(row.getConnector()).map(disPlay).orElse(CharacterConstants.SEPARATOR_EMPTY));
            }
            rowStrList.add(rowStr);
            putDisplay.accept(row, rowStr);
        }
        return StringUtils.join(rowStrList, CharacterConstants.SEPARATOR_BLANK);
    }

    private String genCommonBlockStr(ExpressionBlock block, Function<ExpressionDisplay, String> display, BiConsumer<ExpressionDisplay, String> putDisplay) {
        String str = Optional.ofNullable(block.getCellList()).filter(CollectionUtils::isNotEmpty)
                .map(_cellList -> {
                    if (_cellList.size() <= 1) {
                        return Optional.ofNullable(_cellList.get(0))
                                .map(display)
                                .orElse(CharacterConstants.SEPARATOR_EMPTY);
                    } else {
                        return wrapBracket(_cellList.stream()
                                .map(display)
                                .filter(StringUtils::isNotEmpty)
                                .collect(Collectors.joining("+")));
                    }
                })
                .orElse(CharacterConstants.SEPARATOR_EMPTY);
        putDisplay.accept(block, str);
        return str;
    }

    private String genFunBlockStr(ExpressionBlock block, Function<ExpressionDisplay, String> disPlay, BiConsumer<ExpressionDisplay, String> putDisplay) {
        StringBuilder funBlockBuilder = new StringBuilder();
        String str = Optional.ofNullable(block.getFun()).map(disPlay).map(_fun -> {
            funBlockBuilder.append(_fun);
            List<ExpressionBlock> funArgList = block.getFunArgList();
            Optional.ofNullable(funArgList).filter(CollectionUtils::isNotEmpty).ifPresent(_argList -> {
                List<String> finalArgList = new ArrayList<>();
                for (ExpressionBlock argBlock : _argList) {
                    finalArgList.add(genCommonBlockStr(argBlock, disPlay, putDisplay));
                }
                String finalArgStr = StringUtils.join(finalArgList, CharacterConstants.SEPARATOR_COMMA);
                funBlockBuilder.append(wrapBracket(finalArgStr));
            });
            return funBlockBuilder.toString();
        }).orElseThrow(() -> PamirsException.construct(ExpressionExpEnumerate.EXPRESSION_BLOCK_FUN_BLANK).errThrow());
        putDisplay.accept(block, str);
        return str;
    }

    private String prettify(String str) {
        return Optional.ofNullable(str).orElse(CharacterConstants.SEPARATOR_EMPTY);
    }


    private String wrapBracket(String target) {
        return CharacterConstants.LEFT_BRACKET +
                target +
                CharacterConstants.RIGHT_BRACKET;
    }

    @Override
    @pro.shushi.pamirs.meta.annotation.Function
    public List<ExpressionDefine> queryList(IWrapper<ExpressionDefine> wrapper) {
        return Models.origin().queryListByWrapper(wrapper);
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public List<ExpressionDefine> queryByKeyPrefix(String searchKeyPrefix) {
        return queryList(
                Pops.<ExpressionDefine>lambdaQuery()
                        .from(ExpressionDefine.MODEL_MODEL)
                        .likeRight(ExpressionDefine::getKey, searchKeyPrefix)
        );
    }

    @Override
    @pro.shushi.pamirs.meta.annotation.Function
    public List<ExpressionDefine> copyByKeyPrefix(String searchKeyPrefix, String copiedKeyPrefix) {
        // TODO: 2022/2/11 考虑独立一个业务编码的字段用于搜索. 但是在复制时,需要替换code中的内容,因此没加
        List<ExpressionDefine> existed = queryByKeyPrefix(searchKeyPrefix);
        if (CollectionUtils.isEmpty(existed)) {
            return existed;
        }

        for (ExpressionDefine expressionDefine : existed) {
            expressionDefine.unsetId();
            expressionDefine.unsetCode();

            //key前缀替换
            expressionDefine.setKey(
                    expressionDefine.getKey().replaceFirst(searchKeyPrefix, copiedKeyPrefix)
            );
            //替换表达式里面的变量
            if (CollectionUtils.isNotEmpty(expressionDefine.getRowList())) {
                for (ExpressionRow expressionRow : expressionDefine.getRowList()) {
                    if (CollectionUtils.isEmpty(expressionRow.getBlockList())) continue;
                    for (ExpressionBlock expressionBlock : expressionRow.getBlockList()) {
                        if (CollectionUtils.isEmpty(expressionBlock.getCellList())) continue;
                        if (StringUtils.isNotBlank(expressionBlock.getOriginal()))
                            expressionBlock.setOriginal(expressionBlock.getOriginal().replace(searchKeyPrefix, copiedKeyPrefix));

                        for (ExpressionCell expressionCell : expressionBlock.getCellList()) {
                            if (ExpressionCellType.VARIABLE.equals(expressionCell.getCellType()) && StringUtils.isNotBlank(expressionCell.getOriginal()))
                                expressionCell.setOriginal(expressionCell.getOriginal().replace(searchKeyPrefix, copiedKeyPrefix));
                            if (ExpressionCellType.VARIABLE.equals(expressionCell.getCellType()) && StringUtils.isNotBlank(expressionCell.getValue()))
                                expressionCell.setValue(expressionCell.getValue().replace(searchKeyPrefix, copiedKeyPrefix));
                        }
                    }
                    if (StringUtils.isNotBlank(expressionRow.getOriginal()))
                        expressionRow.setOriginal(expressionRow.getOriginal().replace(searchKeyPrefix, copiedKeyPrefix));

                }
            }
            //构造code
            expressionDefine.setCode(buildCode0(expressionDefine));
        }
        Models.origin().createOrUpdateBatch(existed);
        return existed;
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public Boolean deleteByKeyPrefix(String searchKeyPrefix) {
        Models.origin().deleteByWrapper(
                Pops.<ExpressionDefine>lambdaQuery()
                        .from(ExpressionDefine.MODEL_MODEL)
                        .likeRight(ExpressionDefine::getKey, searchKeyPrefix)
        );
        return Boolean.TRUE;
    }

    @pro.shushi.pamirs.meta.annotation.Function
    @Override
    public ExpressionDefine queryModelLabelExpByModel(ExpressionDefine data) {
        if (StringUtils.isNotBlank(data.getKey())) {
            //key前端出model,这里转换为id
            String model = data.getKey();
            Long id = Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                    .map(ModelConfig::getModelDefinition)
                    .map(ModelDefinition::getId)
                    .orElse(null);
            if (id == null) {
                ModelDefinition modelDefinition = new ModelDefinition().setModel(model).queryOne();
                if (modelDefinition != null) {
                    id = modelDefinition.getId();
                }
            }
            if (id == null) {
                return null;
            }
            data.setKey(id.toString());
        }
        return data.queryOne();
    }
}
