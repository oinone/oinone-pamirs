package pro.shushi.pamirs.channel.core.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.SystemBootAfterInit;
import pro.shushi.pamirs.channel.core.manager.EnhanceModelScanner;
import pro.shushi.pamirs.channel.model.ChannelModel;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;

import java.util.List;

/**
 * ChannelSystemBootAfterInit
 *
 * @author yakir on 2020/04/27 18:05.
 */
@Slf4j
@Component
public class ChannelSystemBootAfterInit implements SystemBootAfterInit {

    @Autowired
    private EnhanceModelScanner enhanceModelScanner;

    @Override
    public boolean init(AppLifecycleCommand command) {

        log.info("Channel Converter");
        log.warn("🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎");
        List<ChannelModel> channelModels = enhanceModelScanner.enhanceModel();
        Models.origin().createOrUpdateBatch(channelModels);
        log.warn("🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎🔍🔎");

        return true;
    }

    @Override
    public int priority() {
        return 99999;
    }
}
