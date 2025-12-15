package pro.shushi.pamirs.business.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.spi.CurrentDepartmentFetcher;
import pro.shushi.pamirs.framework.faas.spi.api.fun.ContextFunctionBizApi;
import pro.shushi.pamirs.meta.common.spi.SPI;

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
        return CurrentDepartmentFetcher.get().fetch();
    }

    @Override
    public String currentUserDepartCode() {
        PamirsDepartment department = CurrentDepartmentFetcher.get().fetch();
        if (department == null) {
            return null;
        }
        return department.getCode();
    }
}
