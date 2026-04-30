package pro.shushi.pamirs.file.api.easyexcel.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.*;
import pro.shushi.pamirs.file.api.config.FileConstant;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.context.ExcelImportContext;
import pro.shushi.pamirs.file.api.easyexcel.ExcelAnalysisEventListener;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelCellDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.exception.ExcelRuntimeException;
import pro.shushi.pamirs.file.api.function.ExcelReadCallback;
import pro.shushi.pamirs.file.api.model.*;
import pro.shushi.pamirs.file.api.util.EasyExcelHelper;
import pro.shushi.pamirs.file.api.util.analysis.ExcelFixedHeaderAnalysisHelper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.framework.gateways.util.BooleanHelper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.enmu.ExpEnumerate;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static pro.shushi.pamirs.meta.common.util.TypeReferences.TR_MAP_SS;

/**
 * @author Adamancy Zhang
 * @date 2021-01-23 15:12
 */
@Slf4j
public class DefaultExcelAnalysisEventListener extends AnalysisEventListener<Map<Integer, String>> implements ExcelAnalysisEventListener {

    private final ExcelImportContext importContext;

    private final int currentSheetNumber;

    private final EasyExcelSheetDefinition currentSheet;

    private final List<EasyExcelBlockDefinition> currentBlocks = new ArrayList<>();

    private EasyExcelBlockDefinition currentBlock;

    private final AtomicBoolean hasNext = new AtomicBoolean(true);

    private final Map<Integer, Map<String, Object>> currentBlockData = new HashMap<>();

    private final Map<Integer, AtomicInteger> emptyRowSizeMap;

    private final Map<Integer, Queue<Map<Integer, String>>> currentBlockQueue = new HashMap<>();

    private final ExcelReadCallback callback;

    private final ExcelImportTask importTask;

    private final boolean eachImport;

    private final int maxErrorLength;

    private int realRowIndex;

    public DefaultExcelAnalysisEventListener(ExcelImportContext importContext, int currentSheetNumber, EasyExcelSheetDefinition currentSheet, ExcelReadCallback callback) {
        this.importContext = importContext;
        this.currentSheetNumber = currentSheetNumber;
        this.currentSheet = currentSheet;
        this.callback = callback;
        this.importTask = importContext.getImportTask();
        this.eachImport = importTask.getEachImport();
        this.maxErrorLength = importTask.getMaxErrorLength();
        this.emptyRowSizeMap = ExcelFixedHeaderAnalysisHelper.fetchEmptyRowSizeMap(this.currentSheet.getBlockDefinitions());
        this.realRowIndex = -1;
    }

    @Override
    public ExcelImportContext getImportContext() {
        return importContext;
    }

    @Override
    public EasyExcelSheetDefinition getCurrentSheet() {
        return currentSheet;
    }

    @Override
    public List<EasyExcelBlockDefinition> getCurrentBlocks() {
        return currentBlocks;
    }

    @Override
    public EasyExcelBlockDefinition getCurrentBlock() {
        return currentBlock;
    }

    @Override
    public Map<Integer, Map<String, Object>> getCurrentBlockData() {
        return currentBlockData;
    }

    @Override
    public Map<Integer, Queue<Map<Integer, String>>> getCurrentBlockQueue() {
        return currentBlockQueue;
    }

    @Override
    public ExcelReadCallback getCallback() {
        return callback;
    }

    @Override
    public boolean hasNext() {
        return this.hasNext.get();
    }

    @Override
    public void interrupt() {
        this.hasNext.set(false);
    }

    @Override
    public void invoke(int rowIndex, Map<Integer, String> data) {
        if (realRowIndex == -1) {
            realRowIndex = rowIndex;
        } else {
            realRowIndex++;
        }
        importContext.setCurrentListener(this);
        importContext.setCurrentSheetNumber(this.currentSheetNumber);
        //初始化上下文属性
        Integer lastedRowIndex = importTask.getLastedRowIndex();
        if (lastedRowIndex == null) {
            lastedRowIndex = realRowIndex;
            importTask.setLastedRowIndex(realRowIndex);
        }
        importContext.setCurrentRow(realRowIndex);
        List<EasyExcelBlockDefinition> lastedBlocks = fetchCurrentBlocks(data, realRowIndex);
        if (lastedBlocks != null) {
            for (EasyExcelBlockDefinition blockDefinition : lastedBlocks) {
                int blockNumber = blockDefinition.getBlockNumber();
                Map<String, Object> blockData = currentBlockData.get(blockNumber);
                if (blockData != null) {
                    callback.process(importContext, blockDefinition.getBindingModel(), blockData);
                    currentBlockData.remove(blockNumber);
                }
            }
        }
        ExcelDefinitionContext definitionContext = importContext.getDefinitionContext();
        for (EasyExcelBlockDefinition blockDefinition : this.currentBlocks) {
            this.currentBlock = blockDefinition;
            int blockNumber = this.currentBlock.getBlockNumber();
            importContext.setCurrentBlockNumber(blockNumber);
            int verificationValue;
            ExcelAnalysisTypeEnum analysisType = blockDefinition.getAnalysisType();
            switch (analysisType) {
                case FIXED_HEADER:
                    //固定表头-表头行验证
                    verificationValue = fixedHeaderVerificationHeader(definitionContext, data, realRowIndex);
                    switch (verificationValue) {
                        case 1:
                            //表头行过滤
                            importTask.setLastedRowIndex(null);
                            return;
                        case -1:
                            this.hasNext.set(false);
                            throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("表头验证失败，中断导入过程").errThrow();
                        default:
                            break;
                    }
                    AtomicInteger aci = this.emptyRowSizeMap.get(blockNumber);
                    if (aci != null) {
                        int emptySize = aci.decrementAndGet();
                        if (emptySize < 0) {
                            ExcelFixedHeaderAnalysisHelper.afterRowCreateInCurrentBlock(currentSheet, currentBlock, realRowIndex);
                        } else {
                            ExcelFixedHeaderAnalysisHelper.scale(currentSheet, currentBlock, 1);
                        }
                    }
                    fixedHeaderBlockImportProcess(definitionContext, blockDefinition, realRowIndex, lastedRowIndex, data);
                    break;
                case FIXED_FORMAT:
                    fixedFormatBlockImportProcess(definitionContext, blockDefinition, realRowIndex, lastedRowIndex, data);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid block analysis type. value=" + analysisType);
            }
            this.currentBlock = null;
            importContext.setCurrentBlockNumber(-1);
        }
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        ReadRowHolder rowHolder = context.readRowHolder();
        int rowIndex = rowHolder.getRowIndex();
        invoke(rowIndex, data);
    }

    @SuppressWarnings("unchecked")
    private void fixedHeaderBlockImportProcess(ExcelDefinitionContext definitionContext, EasyExcelBlockDefinition blockDefinition, Integer rowIndex, Integer lastedRowIndex, Map<Integer, String> data) {
        if (ExcelDirectionEnum.VERTICAL.equals(blockDefinition.getDirection())) {
            throw new UnsupportedOperationException();
        }
        Map<Integer, EasyExcelCellDefinition> columnFieldCells = blockDefinition.getColumnFieldCells();
        List<TreeNode<EasyExcelCellDefinition>> fieldNodeList = blockDefinition.getFieldNodeList();

        //获取当前块正在构造的属性对象
        int blockNumber = importContext.getCurrentBlockNumber();
        Map<String, Object> currentData = currentBlockData.computeIfAbsent(blockNumber, k -> new HashMap<>(64));
        Queue<Map<Integer, String>> blockQueue = getBlockQueue(blockNumber);
        blockQueue.add(data);
        if (currentData.isEmpty()) {
            allFieldFill(definitionContext, fieldNodeList, columnFieldCells, currentData, data);
            return;
        }
        Map<String, Object> temporaryData = new HashMap<>();
        allFieldFill(definitionContext, fieldNodeList, columnFieldCells, temporaryData, data);

        Set<String> uniqueDefinitions = currentSheet.getUniqueDefinitions().get(blockDefinition.getBindingModel());
        MergePredict mergePredict = mergePredict(uniqueDefinitions, currentData, temporaryData);
        if (mergePredict.isNeedMerge) {
            //如果需要合并，但数据不一致，中断读取操作
            if (mergePredict.isEqual) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : mergePredict.needMergeList.entrySet()) {
                    String key = entry.getKey();
                    List<Map<String, Object>> originValue = (List<Map<String, Object>>) currentData.get(key);
                    originValue.addAll(entry.getValue());
                }
            } else {
                log.error("导入中检查出需要合并，但数据并不完全一致，请检查唯一键是否设置合理 sheet: {}; rowIndex: {}", currentSheet.getName(), rowIndex);
                interrupt();
                currentBlockData.remove(blockNumber);
                importTask.setRowIndex(lastedRowIndex);
                throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg("相邻数据行不一致，导入被中断，请矫正数据后重试").errThrow();
            }
        } else {
            importTask.setRowIndex(lastedRowIndex);
            importTask.setLastedRowIndex(rowIndex);
            currentBlockData.put(blockNumber, temporaryData);

            //如果不需要合并，则直接进行回调，并更换当前块正在构造的属性对象
            callback.process(importContext, blockDefinition.getBindingModel(), currentData);

            blockQueue.clear();
            blockQueue.add(data);
        }
    }

    private Queue<Map<Integer, String>> getBlockQueue(int blockNumber) {
        return currentBlockQueue.computeIfAbsent(blockNumber, k -> new LinkedList<>());
    }

    @SuppressWarnings("unchecked")
    private MergePredict mergePredict(Set<String> uniqueDefinitions, Map<String, Object> currentData, Map<String, Object> temporaryData) {
        boolean isNeedMerge, isEqual = true;
        Map<String, List<Map<String, Object>>> needMergeList;
        if (CollectionUtils.isEmpty(uniqueDefinitions)) {
            isNeedMerge = false;
            needMergeList = null;
        } else {
            needMergeList = new HashMap<>();
            isNeedMerge = true;
            for (Map.Entry<String, Object> entry : temporaryData.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Object originValue = currentData.get(key);
                if (uniqueDefinitions.contains(key)) {
                    if (!value.equals(originValue)) {
                        isNeedMerge = false;
                        break;
                    }
                }
                if (value instanceof List && originValue instanceof List) {
                    needMergeList.put(key, (List<Map<String, Object>>) value);
                } else if (isEqual) {
                    if (!(value instanceof Map && originValue instanceof Map)) {
                        //非Map字段判断是否相等
                        if (value == null) {
                            if (originValue != null) {
                                isEqual = false;
                            }
                        } else {
                            if (!value.equals(originValue)) {
                                isEqual = false;
                            }
                        }
                    }
                }
            }
        }
        return new MergePredict(isNeedMerge, isEqual, needMergeList);
    }

    private void fixedFormatBlockImportProcess(ExcelDefinitionContext definitionContext, EasyExcelBlockDefinition blockDefinition, Integer rowIndex, Integer lastedRowIndex, Map<Integer, String> data) {
        ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
        int beginRowIndex = currentRange.getBeginRowIndex(),
                endRowIndex = currentRange.getEndRowIndex(),
                beginColumnIndex = currentRange.getBeginColumnIndex(),
                endColumnIndex = currentRange.getEndColumnIndex();
        List<ExcelRowDefinition> rowDefinitionList = blockDefinition.getRowDefinitionList();
        ExcelRowDefinition currentRowDefinition = rowDefinitionList.get(rowIndex - beginRowIndex);
        List<ExcelCellDefinition> cellList = currentRowDefinition.getCellList();
        Map<String, EasyExcelCellDefinition> fieldCells = blockDefinition.getFieldCells();
        List<TreeNode<EasyExcelCellDefinition>> fieldNodeList = blockDefinition.getFieldNodeList();
        Map<Integer, EasyExcelCellDefinition> columnFieldCells = new HashMap<>();
        int i = beginColumnIndex;
        for (ExcelCellDefinition cell : cellList) {
            Boolean isFieldValue = cell.getIsFieldValue();
            if (isFieldValue == null) {
                isFieldValue = false;
                cell.setIsFieldValue(false);
            }
            if (isFieldValue) {
                EasyExcelCellDefinition fieldCell = fieldCells.get(cell.getField());
                if (fieldCell == null) {
                    throw new NullPointerException();
                }
                columnFieldCells.put(i, fieldCell);
            }
            i++;
            if (i > endColumnIndex) {
                break;
            }
        }

        if (columnFieldCells.isEmpty()) {
            return;
        }

        //获取当前块正在构造的属性对象
        int blockNumber = importContext.getCurrentBlockNumber();
        Map<String, Object> currentData = currentBlockData.computeIfAbsent(blockNumber, k -> new HashMap<>(64));
        //填充数据
        allFieldFill(definitionContext, fieldNodeList, columnFieldCells, currentData, data);

        if (rowIndex == endRowIndex) {
            importTask.setRowIndex(lastedRowIndex);
            importTask.setLastedRowIndex(rowIndex);
            callback.process(importContext, blockDefinition.getBindingModel(), currentData);
            currentBlockData.remove(blockNumber);
        }
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return this.hasNext.get() && ExcelTaskStateEnum.PROCESSING.equals(importTask.getState());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (eachImport) {
            log.error("import exist error.", exception);
            String errorMessage = EasyExcelHelper.getErrorMessage(exception);
            int blockNumber = importContext.getCurrentBlockNumber();
            Map<Integer, String> data = Optional.ofNullable(currentBlockQueue.get(blockNumber))
                    .map(Queue::poll)
                    .orElse(null);
            if (data == null) {
                data = (Map<Integer, String>) context.readRowHolder().getCurrentRowAnalysisResult();
            }
            data.put(currentBlock.getDesignRange().getEndColumnIndex() + 1, errorMessage);
            collectionErrorData(blockNumber, data);
            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, errorMessage, Boolean.FALSE);
            importContext.setCurrentRow(-1);
            importTask.setRowIndex(null);
        } else {
            throw exception;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        try {
            if (this.currentSheet.getOnceFetchData()) {
                processAllBlockData(context);
                this.hasNext.set(false);
                try {
                    callback.process(importContext, this.currentSheet.getBlockDefinitions().get(0).getBindingModel(), null);
                } catch (Exception exception) {
                    onException(exception, context);
                }
            } else {
                this.hasNext.set(false);
                processAllBlockData(context);
            }
        } catch (Exception e) {
            throw new ExcelRuntimeException(e);
        }
        importContext.setCurrentRow(-1);
        importTask.setRowIndex(null);
    }

    /**
     * 处理当前工作表中所有块的数据
     */
    private void processAllBlockData(AnalysisContext context) throws Exception {
        List<EasyExcelBlockDefinition> blockDefinitions = this.currentSheet.getBlockDefinitions();
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            int blockNumber = blockDefinition.getBlockNumber();
            this.currentBlock = blockDefinition;
            importContext.setCurrentBlockNumber(blockNumber);
            Map<String, Object> data = currentBlockData.get(blockNumber);
            if (data != null) {
                Integer lastedRowIndex = importTask.getLastedRowIndex();
                if (lastedRowIndex != null) {
                    importTask.setRowIndex(lastedRowIndex);
                }
                try {
                    callback.process(importContext, blockDefinition.getBindingModel(), data);
                } catch (Exception exception) {
                    onException(exception, context);
                }
            }
        }
    }

    private void allFieldFill(ExcelDefinitionContext definitionContext, List<TreeNode<EasyExcelCellDefinition>> fieldNodeList, Map<Integer, EasyExcelCellDefinition> columnFieldCells, Map<String, Object> currentData, Map<Integer, String> data) {
        for (Map.Entry<Integer, String> item : data.entrySet()) {
            String value = item.getValue();
            if (value == null) {
                continue;
            }
            EasyExcelCellDefinition cellDefinition = columnFieldCells.get(item.getKey());
            if (cellDefinition == null) {
                continue;
            }
            //获取根属性名
            String rootField = cellDefinition.getKey();
            //如果根属性名是一级的，则直接获取属性值，添加到当前数据中；否则截取首个属性名进行关联属性解析；
            String[] rootFields = rootField.split(FileConstant.SEPARATION_CHARACTER);
            if (rootFields.length == 1) {
                Object dataValue = fetchDataValue(definitionContext, cellDefinition, value, item.getKey());
                if (dataValue != null) {
                    currentData.put(rootField, dataValue);
                }
            } else {
                rootField = rootFields[0];
            }
            //从树节点中查找对应根节点
            TreeNode<EasyExcelCellDefinition> rootNode = null;
            for (TreeNode<EasyExcelCellDefinition> nodeItem : fieldNodeList) {
                if (rootField.equals(nodeItem.getKey())) {
                    rootNode = nodeItem;
                    break;
                }
            }
            //如果没有对应的根节点，则不进行处理
            if (rootNode == null) {
                continue;
            }
            relationFieldFill(definitionContext, rootNode, rootFields, currentData, value, item.getKey());
        }
    }

    @SuppressWarnings("unchecked")
    private void relationFieldFill(ExcelDefinitionContext definitionContext, TreeNode<EasyExcelCellDefinition> rootNode, String[] rootFields, Map<String, Object> currentData, String value, int currentColumn) {
        String fieldKey = rootNode.getKey();
        int level = rootNode.getLevel();
        String field = rootFields[level - 1];
        int li = field.indexOf("["), ri = field.indexOf("]"), index = -1;
        if (li != -1 && ri != -1) {
            index = Integer.parseInt(field.substring(li + 1, ri));
            field = field.substring(0, li);
        }
        if (rootNode.isLeaf()) {
            Object dataValue = fetchDataValue(definitionContext, rootNode.getValue(), value, currentColumn);
            if (dataValue != null) {
                currentData.put(field, dataValue);
            }
        } else {
            String nextKey = fieldKey + FileConstant.POINT_CHARACTER + rootFields[level];
            Map<String, Object> mapObject;
            EasyExcelCellDefinition cellDefinition = rootNode.getValue();
            if (!cellDefinition.getIsCollection() && index == -1) {
                mapObject = (Map<String, Object>) currentData.get(field);
                if (mapObject == null) {
                    mapObject = new HashMap<>();
                    currentData.put(field, mapObject);
                }
                TreeNode<EasyExcelCellDefinition> nextNode = fetchNextNode(nextKey, rootNode.getChildren());
                if (nextNode != null) {
                    relationFieldFill(definitionContext, nextNode, rootFields, mapObject, value, currentColumn);
                }
            } else {
                List<Object> listObject = (List<Object>) currentData.get(field);
                if (listObject == null) {
                    listObject = new ArrayList<>();
                    currentData.put(field, listObject);
                }
                AtomicInteger aci = null;
                if (index == -1) {
                    aci = (AtomicInteger) rootNode.getExtend();
                    if (aci == null) {
                        aci = new AtomicInteger(0);
                        rootNode.setExtend(aci);
                    }
                    index = aci.getAndIncrement();
                }
                while (listObject.size() <= index) {
                    listObject.add(null);
                }
                mapObject = (Map<String, Object>) listObject.get(index);
                if (mapObject == null) {
                    mapObject = new HashMap<>();
                    listObject.set(index, mapObject);
                }
                TreeNode<EasyExcelCellDefinition> nextNode = fetchNextNode(nextKey, rootNode.getChildren());
                if (nextNode != null) {
                    relationFieldFill(definitionContext, nextNode, rootFields, mapObject, value, currentColumn);
                }
                if (aci != null) {
                    aci.set(0);
                }
            }
        }
    }

    private TreeNode<EasyExcelCellDefinition> fetchNextNode(String nextKey, List<TreeNode<EasyExcelCellDefinition>> children) {
        TreeNode<EasyExcelCellDefinition> nextNode = null;
        for (TreeNode<EasyExcelCellDefinition> childNode : children) {
            if (nextKey.equals(childNode.getKey())) {
                nextNode = childNode;
                break;
            }
        }
        return nextNode;
    }

    private Object fetchDataValue(ExcelDefinitionContext definitionContext, EasyExcelCellDefinition cellDefinition, String value, int currentColumn) {
        String stringValue = StringHelper.valueOf(value);
        if (CharacterConstants.SEPARATOR_EMPTY.equals(stringValue)) {
            return CharacterConstants.SEPARATOR_EMPTY;
        }
        String format = cellDefinition.getFormat();
        ExcelValueTypeEnum valueType = cellDefinition.getType();
        boolean isFormat = StringUtils.isNotBlank(format);
        switch (valueType) {
            case INTEGER:
                try {
                    stringValue = stringValue.trim();
                    if (stringValue.startsWith("'")) {
                        stringValue = stringValue.substring(1);
                    }

                    // 使用 NumberFormat 解析数字
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    Number parsedNumber = numberFormat.parse(stringValue);

                    BigDecimal integerValue = NumberHelper.valueOfNullable(parsedNumber.longValue());
                    if (integerValue == null) {
                        return null;
                    }
                    return integerValue.longValue();
                } catch (NumberFormatException | ParseException e) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(String.format("第%s行 第%s列 无法将 [%s] 转换为整数", importContext.getCurrentRow(), currentColumn, stringValue)).errThrow();
                }
            case NUMBER:
                try {
                    stringValue = stringValue.trim();
                    if (stringValue.startsWith("'")) {
                        stringValue = stringValue.substring(1);
                    }
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    Number parsedNumber = numberFormat.parse(stringValue);

                    return BigDecimal.valueOf(parsedNumber.doubleValue());
                } catch (NumberFormatException | ParseException e) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(String.format("第%s行 第%s列 无法将 [%s] 转换为数字", importContext.getCurrentRow(), currentColumn, stringValue)).errThrow();
                }
            case DATETIME:
                stringValue = stringValue.trim();
                if (!isFormat) {
                    format = valueType.getDefaultFormat();
                }
                try {
                    return DateHelper.parse(stringValue, format);
                } catch (ParseException e) {
                    throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(String.format("第%s行 第%s列 无法将 [%s] 按照 [%s] 日期格式进行解析", importContext.getCurrentRow(), currentColumn, stringValue, format)).errThrow();
                }
            case BOOLEAN:
            case ENUMERATION:
                if (isFormat) {
                    Map<String, String> enumerationMap = JSON.parseObject(format, TR_MAP_SS.getType(), Feature.OrderedField);
                    for (Map.Entry<String, String> entry : enumerationMap.entrySet()) {
                        String enumerationValue = entry.getValue();
                        if (enumerationValue.equals(stringValue) || definitionContext.translate(enumerationValue).equals(stringValue)) {
                            stringValue = entry.getKey();
                            break;
                        }
                    }
                }
                if (ExcelValueTypeEnum.BOOLEAN.equals(valueType)) {
                    return BooleanHelper.toBoolean(stringValue);
                }
                return stringValue;
            case COMMENT://暂不支持
            case HYPER_LINK://暂不支持
            case CALENDAR://暂不支持
            case RICH_TEXT_STRING:
            case FORMULA:
            case STRING:
            default:
                return stringValue;
        }
    }

    private int fixedHeaderVerificationHeader(ExcelDefinitionContext definitionContext, Map<Integer, String> data, int rowIndex) {
        int result = 0;
        for (EasyExcelBlockDefinition blockDefinition : this.currentBlocks) {
            ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
            Integer beginRowIndex = currentRange.getBeginRowIndex(), endRowIndex = currentRange.getEndRowIndex(),
                    beginColumnIndex = currentRange.getBeginColumnIndex(), endColumnIndex = currentRange.getEndColumnIndex();
            int headerRowIndex = rowIndex - beginRowIndex;
            if (rowIndex >= beginRowIndex && rowIndex <= endRowIndex) {
                //在块的行设计范围内
                List<ExcelHeaderDefinition> headerDefinitionList = blockDefinition.getHeaderDefinitionList();
                if (headerRowIndex < headerDefinitionList.size()) {
                    //在表头定义范围内
                    ExcelHeaderDefinition headerDefinition = headerDefinitionList.get(headerRowIndex);
                    int i = beginColumnIndex;
                    for (ExcelCellDefinition cellDefinition : headerDefinition.getCellList()) {
                        String templateValue = definitionContext.translate(cellDefinition.getValue());
                        String fileValue = data.get(i);
                        if (!StringHelper.equals(templateValue, fileValue)) {
                            if (result == 0) {
                                importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, "导入文件的表头行与模板不符");
                            }
                            importTask.addTaskMessage(TaskMessageLevelEnum.ERROR, String.format("第%s行 第%s列 模板内容为: %s; 文件内容为: %s", rowIndex, LetterHelper.getUpperByIndex(i + 1), templateValue, fileValue));
                            return -1;
                        }
                        i++;
                    }
                    if (result == 0) {
                        result = 1;
                    }
                }
            }
        }
        return result;
    }

    private boolean isNextBlock(EasyExcelBlockDefinition blockDefinition, Map<Integer, String> data) {
        int beginColumnIndex,
                endColumnIndex;
        List<ExcelCellDefinition> cellList;
        ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
        ExcelAnalysisTypeEnum analysisType = blockDefinition.getAnalysisType();
        switch (analysisType) {
            case FIXED_HEADER:
                ExcelDirectionEnum direction = blockDefinition.getDirection();
                switch (direction) {
                    case HORIZONTAL:
                        beginColumnIndex = currentRange.getBeginColumnIndex();
                        endColumnIndex = currentRange.getEndColumnIndex();
                        cellList = blockDefinition.getHeaderDefinitionList().get(0).getCellList();
                        break;
                    case VERTICAL:
                        beginColumnIndex = currentRange.getBeginColumnIndex();
                        endColumnIndex = beginColumnIndex + blockDefinition.getHeaderDefinitionList().size() - 1;
                        cellList = new ArrayList<>();
                        List<ExcelHeaderDefinition> headerDefinitionList = blockDefinition.getHeaderDefinitionList();
                        for (ExcelHeaderDefinition headerDefinition : headerDefinitionList) {
                            cellList.add(headerDefinition.getCellList().get(0));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid excel block direction type. value=" + direction);
                }
                break;
            case FIXED_FORMAT:
                beginColumnIndex = currentRange.getBeginColumnIndex();
                endColumnIndex = currentRange.getEndColumnIndex();
                cellList = blockDefinition.getRowDefinitionList().get(0).getCellList();
                break;
            default:
                throw new IllegalArgumentException("Invalid block analysis type. value=" + analysisType);
        }
        for (int i = beginColumnIndex; i <= endColumnIndex; i++) {
            int realIndex = i - beginColumnIndex;
            if (cellList.size() <= realIndex) {
                return false;
            }
            ExcelCellDefinition cell = cellList.get(realIndex);
            Boolean isFieldValue = cell.getIsFieldValue();
            if (isFieldValue == null) {
                isFieldValue = false;
                cell.setIsFieldValue(false);
            }
            if (!isFieldValue && !StringHelper.equals(cell.getValue(), data.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void collectionErrorData(int blockNumber, Map<Integer, String> data) {
        List<Map<Integer, String>> dataList = CollectionHelper.getAndAddNewInstance(importContext.getErrorDataList(), blockNumber, ArrayList::new);
        if (data != null) {
            if (maxErrorLength == -1 || dataList.size() <= maxErrorLength) {
                dataList.add(data);
            } else {
                interrupt();
                throw PamirsException.construct(ExpEnumerate.BIZ_ERROR).appendMsg(String.format("错误行数超过%s行，导入过程被强制中断，请检查文件内容后重试", maxErrorLength)).errThrow();
            }
        }
    }

    private List<EasyExcelBlockDefinition> fetchCurrentBlocks(Map<Integer, String> data, int rowIndex) {
        fetchCurrentBlocks(rowIndex);
        boolean isResetScale = false;
        for (EasyExcelBlockDefinition currentBlock : this.currentBlocks) {
            List<EasyExcelBlockDefinition> influenceBlockList = currentBlock.getInfluenceTranslationBlockList();
            if (CollectionUtils.isNotEmpty(influenceBlockList)) {
                for (EasyExcelBlockDefinition influenceBlock : influenceBlockList) {
                    if (isNextBlock(influenceBlock, data)) {
                        isResetScale = true;
                        break;
                    }
                }
            }
            if (isResetScale) {
                ExcelFixedHeaderAnalysisHelper.scale(currentSheet, currentBlock, -1);
                List<EasyExcelBlockDefinition> lastedBlocks = new ArrayList<>(this.currentBlocks);
                fetchCurrentBlocks(rowIndex);
                return lastedBlocks;
            }
        }
        return null;
    }

    private void fetchCurrentBlocks(int rowIndex) {
        this.currentBlocks.clear();
        List<EasyExcelBlockDefinition> blockDefinitions = currentSheet.getBlockDefinitions();
        for (EasyExcelBlockDefinition blockDefinition : blockDefinitions) {
            ExcelCellRangeDefinition currentRange = blockDefinition.getCurrentRange();
            if (rowIndex >= currentRange.getBeginRowIndex() && rowIndex <= currentRange.getEndRowIndex()) {
                this.currentBlocks.add(blockDefinition);
            }
        }
        if (this.currentBlocks.isEmpty()) {
            importTask.setRowIndex(rowIndex);
            throw new IllegalArgumentException("Invalid current blocks. value is empty.");
        }
    }

    private static class MergePredict {

        private final boolean isNeedMerge;

        private final boolean isEqual;

        private final Map<String, List<Map<String, Object>>> needMergeList;

        public MergePredict(boolean isNeedMerge, boolean isEqual, Map<String, List<Map<String, Object>>> needMergeList) {
            this.isNeedMerge = isNeedMerge;
            this.isEqual = isEqual;
            this.needMergeList = needMergeList;
        }
    }
}