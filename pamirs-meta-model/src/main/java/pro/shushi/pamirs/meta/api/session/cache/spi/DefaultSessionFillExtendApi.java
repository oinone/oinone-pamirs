package pro.shushi.pamirs.meta.api.session.cache.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.meta.api.dto.meta.MetaData;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.base.common.MetaBaseModel;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.List;

/**
 * 填充Session补充操作入口
 *
 * @author wx@shushi.pro
 * @version 1.0.0
 * 2022/8/12
 */
@SPI.Service
@Component
@Order(Integer.MAX_VALUE) //默认优先级最低，业务配置需要配置成为优先级高
public class DefaultSessionFillExtendApi implements SessionFillExtendApi {

    @Override
    public void fillAllMetaData(RequestContext context, List<MetaData> metaDataList, Boolean loadMeta) {

    }

    @Override
    public <T extends MetaBaseModel> void updateMetaData(String model, List<T> dataList) {

    }

    @Override
    public <T extends MetaBaseModel> void deleteMetaData(String model, List<T> dataList) {

    }

}
