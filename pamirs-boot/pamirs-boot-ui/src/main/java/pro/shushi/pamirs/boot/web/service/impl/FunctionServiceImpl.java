package pro.shushi.pamirs.boot.web.service.impl;

import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.web.manager.UiIoManager;
import pro.shushi.pamirs.boot.web.service.FunctionService;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 函数服务实现
 *
 * @author Adamancy Zhang at 14:07 on 2024-08-19
 */
@Service
public class FunctionServiceImpl implements FunctionService {

    @Resource
    private UiIoManager uiIoManager;

    @Override
    public FunctionDefinition load(String namespace, String fun) {
        return Optional.ofNullable(PamirsSession.getContext().getFunctionAllowNull(namespace, fun))
                .map(Function::getFunctionDefinition)
                .map(uiIoManager::cloneData)
                .orElse(null);
    }

    @Override
    public FunctionDefinition loadByName(String namespace, String name) {
        return Optional.ofNullable(PamirsSession.getContext().getFunctionByName(namespace, name))
                .map(Function::getFunctionDefinition)
                .map(uiIoManager::cloneData)
                .orElse(null);
    }
}
