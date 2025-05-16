package pro.shushi.pamirs.eip.core.service.model;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.core.common.enmu.DataStatusEnum;
import pro.shushi.pamirs.eip.api.model.EipIntegrate;
import pro.shushi.pamirs.eip.api.service.model.EipIntegrateService;
import pro.shushi.pamirs.meta.annotation.Fun;

/**
 * EipIntegrateServiceImpl
 *
 * @author yakir on 2023/04/12 16:40.
 */
@Fun(EipIntegrateService.FUN_NAMESPACE)
@Service
public class EipIntegrateServiceImpl implements EipIntegrateService {

    @Override
    public EipIntegrate createOrUpdate(EipIntegrate data) {
        data.createOrUpdate();
        return data;
    }

    @Override
    public EipIntegrate queryOne(EipIntegrate data) {
        return data.queryOne();
    }

    @Override
    public EipIntegrate delete(EipIntegrate data) {

        data = data.queryOne();
        data.deleteById();
        return data;
    }

    @Override
    public EipIntegrate changeStatus(EipIntegrate data) {
        data = data.queryById();
        DataStatusEnum status = data.getDataStatus();
        EipIntegrate   update = new EipIntegrate();
        update.setId(data.getId());
        switch (status) {
            case ENABLED:
                update.setDataStatus(DataStatusEnum.DISABLED);
                break;
            case DISABLED:
                update.setDataStatus(DataStatusEnum.ENABLED);
                break;
        }

        // todo 启用禁用所有API

        update.updateById();
        return update;
    }
}
