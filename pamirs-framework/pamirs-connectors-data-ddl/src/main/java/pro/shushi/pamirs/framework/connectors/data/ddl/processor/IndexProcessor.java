package pro.shushi.pamirs.framework.connectors.data.ddl.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicTable;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.physical.Column;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.check.IndexChecker;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.ColumnComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.IndexComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.component.TableComponent;
import pro.shushi.pamirs.framework.connectors.data.ddl.model.DdlContext;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 索引处理
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Component
public class IndexProcessor {

    @Resource
    private TableComponent tableComponent;

    @Resource
    private ColumnComponent columnComponent;

    @Resource
    private IndexComponent indexComponent;

    @Resource
    private IndexChecker indexChecker;

    public void changeIndex(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        List<LogicIndex> existIndexList = Lists.newArrayList(table.getIndexMap().values());
        boolean existPrimary = Boolean.FALSE;
        if (!CollectionUtils.isEmpty(existIndexList)) {
            String primaryIndexName = indexComponent.primaryIndexName(table.getDsKey(), table.getTableName());
            for (LogicIndex index : existIndexList) {
                if (null == index.getColumn()) {
                    continue;
                }
                boolean noIndexMeta = ddlContext.fetchIndex(index.getIndexName()).isEmpty();
                boolean canDrop = indexChecker.drop(index.getIndexName(), table, modelDefinition.getModel());
                String indexColumn = StringUtils.join(index.getColumn(), CharacterConstants.SEPARATOR_COMMA);
                if (primaryIndexName.equals(index.getIndexName())) {
                    existPrimary = Boolean.TRUE;
                    boolean canChange = indexChecker.change(index.getIndexName(), table, modelDefinition.getModel());
                    // 主键
                    if (!CollectionUtils.isEmpty(modelDefinition.getPk())) {
                        String primary = indexComponent.generatePrimaryColumn(table.getDsKey(), modelDefinition, Boolean.FALSE, index);
                        indexColumn = changeStringIfSetContain(Sets.newHashSet(primary), indexColumn);
                        if (!indexColumn.equals(primary)) {
                            if (!canChange) {
                                continue;
                            }
                            // 先去除auto_increment
                            removeAutoIncrement(ddlList, table, indexColumn);

                            // 更改主键
                            indexComponent.dropIndex(ddlContext, ddlList, index);
                            indexComponent.addPrimaryKey(ddlContext, modelDefinition, ddlList);
                        } else {
                            ddlContext.unDropIndex(index.getIndexName());
                            if (noIndexMeta) {
                                indexComponent.createIndexMeta(ddlContext, modelDefinition, index.getIndexName(), index);
                            } else {
                                ddlContext.updateModuleIndexIfChanged(index);
                            }
                        }
                    } else {
                        if (!canDrop) {
                            continue;
                        }
                        // 先去除auto_increment
                        removeAutoIncrement(ddlList, table, indexColumn);

                        // 去除主键
                        indexComponent.dropIndex(ddlContext, ddlList, index);
                    }
                } else {
                    indexColumn = changeStringIfSetContain(ddlContext.getIndexColumnSet(), indexColumn);
                    indexColumn = changeStringIfSetContain(ddlContext.getUniqueColumnSet(), indexColumn);
                    if (!ddlContext.getUniqueColumnSet().contains(indexColumn) && !ddlContext.getIndexColumnSet().contains(indexColumn)) {
                        if (!canDrop) {
                            continue;
                        }
                        indexComponent.dropIndex(ddlContext, ddlList, index);
                    } else {
                        ddlContext.unDropIndex(index.getIndexName());
                        if (noIndexMeta) {
                            indexComponent.createIndexMeta(ddlContext, modelDefinition, index.getIndexName(), index);
                        } else {
                            ddlContext.updateModuleIndexIfChanged(index);
                        }
                    }
                    ddlContext.getIndexColumnSet().remove(indexColumn);
                    ddlContext.getUniqueColumnSet().remove(indexColumn);
                }
            }
        }
        // 添加主键
        if (CollectionUtils.isNotEmpty(modelDefinition.getPk()) && !existPrimary) {
            indexComponent.addPrimaryKey(ddlContext, modelDefinition, ddlList);
        }
        // 添加索引
        indexComponent.createIndex(ddlContext, modelDefinition, ddlContext.getIndexColumnSet(),
                ddlList, Boolean.FALSE, ddlContext.getLogicDeleteColumn());
        // 添加唯一索引
        indexComponent.createIndex(ddlContext, modelDefinition, ddlContext.getUniqueColumnSet(),
                ddlList, Boolean.TRUE, ddlContext.getLogicDeleteColumn());
    }

    private void removeAutoIncrement(List<String> ddlList, LogicTable table, String column) {
        Column existAutoIncrementColumn = columnComponent
                .fetchExistAutoIncrementColumn(table.getDsKey(), table.getColumnMap(), column);
        if (null != existAutoIncrementColumn) {
            String modifyPkColumnDefinition = columnComponent.fetchModifyColumnDdl(table.getDsKey(), table.getTableName(),
                    existAutoIncrementColumn.getColumnName(), existAutoIncrementColumn.getColumnName(),
                    columnComponent.columnDefinition(table.getDsKey(), existAutoIncrementColumn,
                            false, false),
                    existAutoIncrementColumn.getColumnComment(), null);
            ddlList.add(modifyPkColumnDefinition);
        }
    }


    private String changeStringIfSetContain(Set<String> sets, String key) {
        // 检查是否简单匹配
        if (sets.contains(key)) {
            return key;
        }

        // 分割输入的key为多个索引项
        String[] keys = key.split(",");

        // 遍历集合中的每个元素
        for (String set : sets) {
            String[] setList = set.split(",");

            // 忽略非联合索引
            if (setList.length <= 1) {
                continue;
            }

            // 使用HashSet检查keys中的所有元素是否都存在于setList中
            Set<String> keySet = new HashSet<>(Arrays.asList(keys));
            Set<String> setListSet = new HashSet<>(Arrays.asList(setList));

            // 如果keys中的所有元素都在setList中，则返回true
            if (setListSet.containsAll(keySet) && setListSet.size() == keySet.size()) {
                return set;
            }
        }

        return key;
    }

    public void deleteIndex(DdlContext ddlContext, List<String> ddlList, ModelWrapper modelDefinition, LogicTable table) {
        List<LogicIndex> existIndexList = Lists.newArrayList(table.getIndexMap().values());
        if (CollectionUtils.isEmpty(existIndexList)) {
            return;
        }
        String primaryIndexName = indexComponent.primaryIndexName(table.getDsKey(), table.getTableName());
        for (LogicIndex index : existIndexList) {
            if (null == index.getColumn()) {
                continue;
            }
            if (!primaryIndexName.equals(index.getIndexName())) {
                continue;
            }
            boolean noIndexMeta = ddlContext.fetchIndex(index.getIndexName()).isEmpty();
            boolean canDrop = indexChecker.drop(index.getIndexName(), table, modelDefinition.getModel());
            String indexColumn = StringUtils.join(index.getColumn(), CharacterConstants.SEPARATOR_COMMA);
            boolean canChange = indexChecker.change(index.getIndexName(), table, modelDefinition.getModel());
            // 主键
            if (CollectionUtils.isNotEmpty(modelDefinition.getPk())) {
                String primary = indexComponent.generatePrimaryColumn(table.getDsKey(), modelDefinition, Boolean.FALSE, index);
                indexColumn = changeStringIfSetContain(Sets.newHashSet(primary), indexColumn);
                if (!indexColumn.equals(primary)) {
                    if (!canChange) {
                        continue;
                    }
                    // 先去除auto_increment
                    removeAutoIncrement(ddlList, table, indexColumn);

                    // 去除主键
                    indexComponent.dropIndex(ddlContext, ddlList, index);
                } else {
                    ddlContext.unDropIndex(index.getIndexName());
                    if (noIndexMeta) {
                        indexComponent.createIndexMeta(ddlContext, modelDefinition, index.getIndexName(), index);
                    } else {
                        ddlContext.updateModuleIndexIfChanged(index);
                    }
                }
            } else {
                if (!canDrop) {
                    continue;
                }
                // 先去除auto_increment
                removeAutoIncrement(ddlList, table, indexColumn);

                // 去除主键
                indexComponent.dropIndex(ddlContext, ddlList, index);
            }
        }
    }
}
