package pro.shushi.pamirs.business.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionBizApi;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 获取表达式session上下文SPI默认实现
 * <p>
 * 2021/3/4 11:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@Component
@SPI.Service
public class DefaultContextFunctionBizApi implements ContextFunctionBizApi {

    @Override
    public Object currentPartner() {
        return null;
    }

    @Override
    public Long currentPartnerId() {
        return null;
    }

    @Override
    public Object currentUserDepart() {
        String departmentCode = this.currentUserDepartCode();
        if (StringUtils.isBlank(departmentCode)) {
            return null;
        }
        return new PamirsDepartment().setCode(departmentCode).queryByCode();
    }

    @Override
    public String currentUserDepartCode() {
        Long userId = PamirsSession.getUserId();
        if (userId == null) {
            return null;
        }
        List<PamirsEmployee> employeeList = new PamirsEmployee().queryList(Pops.<PamirsEmployee>lambdaQuery()
                .from(PamirsEmployee.MODEL_MODEL).eq(PamirsEmployee::getBindingUserId, userId));
        if (CollectionUtils.isEmpty(employeeList)) {
            return null;
        }

        return employeeList.get(0).getDepartmentCode();
    }
}
