package pro.shushi.pamirs.business.util;

import pro.shushi.pamirs.business.api.model.BizCodeModel;
import pro.shushi.pamirs.business.api.model.DepartmentRelEmployee;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yeshenyue on 2025/7/9 14:09.
 */
public class DepartmentRelEmployeeHelper {

    /**
     * 将详情页通过relationQuery查询的关系字段中的主管信息，填充到返回的数据中
     */
    public static <T extends BizCodeModel> boolean fillSupervisorInfo(List<T> entities, Map<String, Object> queryData,
                                                                   String dataKey,
                                                                   java.util.function.Function<DepartmentRelEmployee, String> keyExtractor,
                                                                   java.util.function.BiConsumer<T, DepartmentRelEmployee> supervisorSetter) {
        if (queryData == null || !queryData.containsKey(dataKey)) {
            return false;
        }
        Object departmentListData = queryData.get(dataKey);
        if (!isDepartmentRelEmployeeList(departmentListData)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        List<DepartmentRelEmployee> relList = (List<DepartmentRelEmployee>) departmentListData;
        new DepartmentRelEmployee().listFieldQuery(relList, DepartmentRelEmployee::getImmediateSupervisor);

        Map<String, DepartmentRelEmployee> relMap = relList.stream().collect(Collectors.toMap(keyExtractor, v -> v));
        for (T entity : entities) {
            DepartmentRelEmployee rel = relMap.get(entity.getCode());
            if (rel != null) {
                supervisorSetter.accept(entity, rel);
            }
        }
        return true;
    }

    private static boolean isDepartmentRelEmployeeList(Object obj) {
        if (!(obj instanceof List)) {
            return false;
        }
        List<?> list = (List<?>) obj;
        if (list.isEmpty()) {
            return false;
        }
        return list.get(0) instanceof DepartmentRelEmployee;
    }
}
