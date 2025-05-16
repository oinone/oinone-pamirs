package pro.shushi.pamirs.translate.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.TranslateService;
import pro.shushi.pamirs.boot.web.spi.holder.TranslateServiceHolder;
import pro.shushi.pamirs.framework.gateways.graph.spi.TranslateErrorService;
import pro.shushi.pamirs.meta.api.dto.protocol.PamirsRequestResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.translate.hook.TranslateErrorManager;
import pro.shushi.pamirs.translate.hook.TranslateSuccessManager;

import java.util.List;
import java.util.Map;

/**
 * TranslateErrorServiceImpl
 *
 * @author yakir on 2023/09/19 11:43.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@SPI.Service
public class TranslateErrorServiceImpl implements TranslateErrorService {

    @Autowired
    private TranslateErrorManager translateErrorManager;
    @Autowired
    private TranslateSuccessManager translateSuccessManager;

    @Override
    public void translateError(PamirsRequestResult result) {

        TranslateService translateService = TranslateServiceHolder.get();
        if (translateService.needTranslate()) {
            List<Map<String, Object>> errors = result.getErrors();
            if (CollectionUtils.isEmpty(errors)) {
                translateSuccessManager.translateSuccess(result);
            } else {
                translateErrorManager.translateError(result);
            }
        }
    }
}
