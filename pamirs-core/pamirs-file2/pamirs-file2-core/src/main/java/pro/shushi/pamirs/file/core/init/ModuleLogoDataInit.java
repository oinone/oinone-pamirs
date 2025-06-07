package pro.shushi.pamirs.file.core.init;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.model.UeModule;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.contants.ProfileEnum;
import pro.shushi.pamirs.boot.common.extend.MetaDataEditor;
import pro.shushi.pamirs.file.api.config.FileProperties;
import pro.shushi.pamirs.file.api.util.ResourceFileHelper;
import pro.shushi.pamirs.framework.connectors.cdn.client.FileClient;
import pro.shushi.pamirs.framework.connectors.cdn.factory.FileClientFactory;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Adamancy Zhang on 2021-05-08 13:40
 */
@Slf4j
@Component
public class ModuleLogoDataInit implements MetaDataEditor {

    private static final String LOGO_PATH_PREFIX = "logo/";

    private static final String LOGO_FILE_EXTENSION = ".png";

    private static final String DEFAULT_LOGO = LOGO_PATH_PREFIX + "default" + LOGO_FILE_EXTENSION;

    private static final String RESOURCE_PATTERN = "classpath*:/pamirs/init/" + LOGO_PATH_PREFIX + "*" + LOGO_FILE_EXTENSION;

    private static final Function<Resource, String> FILENAME_GENERATOR = resource -> LOGO_PATH_PREFIX + resource.getFilename();

    @Autowired
    private FileProperties fileProperties;

    @Override
    public void edit(AppLifecycleCommand command, Map<String, Meta> metaMap) {
        if (!fileProperties.getAutoUploadLogo() || ProfileEnum.DDL.equals(command.getProfile())) {
            log.info("禁用自动上传Logo功能，将不再进行文件上传和匹配操作");
            return;
        }
        FileClient fileClient = FileClientFactory.getClient();
        try {
            Map<String, String> uploadResult = ResourceFileHelper.uploadByResources(fileClient, RESOURCE_PATTERN, FILENAME_GENERATOR);
            List<UeModule> modules = new ArrayList<>();
            metaMap.values().stream()
                    .map(Meta::getCurrentModuleData)
                    .forEach(v -> Optional.ofNullable(v.<UeModule>getDataMap(UeModule.MODEL_MODEL))
                            .map(Map::values)
                            .ifPresent(modules::addAll));
            moduleLogoInit(modules, uploadResult);
        } catch (IOException e) {
            log.error("Logo文件上传失败", e);
        }
    }

    private void moduleLogoInit(List<UeModule> modules, Map<String, String> uploadResult) {
        for (UeModule module : modules) {
            if (StringUtils.isNotBlank(module.getLogo())) {
                continue;
            }
            String logo = uploadResult.get(LOGO_PATH_PREFIX + module.getModule() + LOGO_FILE_EXTENSION);
            if (StringUtils.isBlank(logo)) {
                logo = uploadResult.get(DEFAULT_LOGO);
            }
            module.setDefaultLogo(logo);
        }
    }
}
