package pro.shushi.pamirs.boot.web.service.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.MaskDefinition;
import pro.shushi.pamirs.boot.web.service.MaskService;

/**
 * 母版处理服务
 * <p>
 * 2022/2/23 9:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Service
public class MaskServiceImpl implements MaskService {

    @Override
    public MaskDefinition load(MaskDefinition mask) {
        return null;
    }

    @Override
    public MaskDefinition compile(MaskDefinition maskDefinition) {
        return maskDefinition;
    }

    @Override
    public MaskDefinition layout(MaskDefinition mask) {
        return null;
    }

    @Override
    public MaskDefinition auth(MaskDefinition maskDefinition) {
        // TODO
        return maskDefinition;
    }

    @Override
    public MaskDefinition internationalization(MaskDefinition maskDefinition) {
        // TODO
        return maskDefinition;
    }

}
