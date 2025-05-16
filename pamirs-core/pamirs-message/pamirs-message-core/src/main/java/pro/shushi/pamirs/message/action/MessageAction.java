package pro.shushi.pamirs.message.action;

import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.gateways.graph.longpolling.PamirsLongPolling;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.MessageTypeEnum;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.message.tmodel.MessageLongPolling;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Model.model(PamirsMessage.MODEL_MODEL)
public class MessageAction {

    //@Action(args = "message", pro.shushi.pamirs.user.core.model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.MailMessage", isExported = true)
//    @Action
//    public MailMessage postMessage1(MailMessage message) {
//        try {
//            MailMessage retMessage = new MailMessage().create(message);
//            if (retMessage != null) {
//                //关闭long polling-0
//                PamirsLongPolling.close(MailLongPollingTransient.class.getName(), PamirsSession.getUserId().toString());
//                //关闭艾特人的long polling
//                List<MailMessagePartnerNeedAction> needActions = message.getPartnerNeedAction();
//                if (CollectionUtils.isNotEmpty(needActions)){
//                    for (MailMessagePartnerNeedAction needAction : needActions) {
//                        if (needAction.getPartner() != null && needAction.getPartner().getId() != null){
//                            PamirsLongPolling.close(MailLongPollingTransient.class.getName(), needAction.getPartner().getId().toString());
//                        }
//                    }
//                }
//                //若消息中带了频道，关闭每个channel里的partner
//                List<MailChannel> channels = message.getChannels();
//                if (CollectionUtils.isNotEmpty(channels)){
//                    for (MailChannel channel : channels) {
//                        PageCondition pageCondition = new PageCondition(MailMessage.class);
//                        pageCondition.setWrapper(new QueryWrapper().eq("message_id",channel.getId()));
//                        List<MailChannel> m2mChannelPartnerMapList = new MailChannel().queryList(pageCondition);
//                        if (CollectionUtils.isNotEmpty(m2mChannelPartnerMapList)) {
//                            for (MailChannel m2mChannelPartner : m2mChannelPartnerMapList) {
//                                Long chatPartnerId = m2mChannelPartner.getId();
//                                PamirsLongPolling.close(MailLongPollingTransient.class.getName(), chatPartnerId.toString());
//                            }
//                        }
//                    }
//                }
//                return retMessage;
//            }
//        } catch (Exception ex) {
//            log.error("{}", ExpEnumerate.SYSTEM_ERROR.msg(),ex);
//            throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR).errThrow();
//        }
//        return null;
//    }

    //@Action(args = "message", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.MailMessage", isExported = true)
    @Action
    public PamirsMessage postMessage(PamirsMessage message) {
        PamirsMessage retMessage = message.create();
        return Optional.ofNullable(retMessage)
                .map(_retMessage -> {
                    //关闭long polling-0
                    PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, PamirsSession.getUserId().toString());
                    List<UnreadMessage> needActions = message.getPartnerNeedAction();
                    Optional.ofNullable(needActions)
                            .map(_needActions -> _needActions.stream())
                            .orElse(Stream.empty())
                            .forEach(needAction -> Optional.ofNullable(needAction)
                                    .map(_needAction -> {
                                        return _needAction.getUser();
                                    })
                                    .map(_partner -> _partner.getId())
                                    .ifPresent(_partnerId -> PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, _partnerId.toString())));
                    //若消息中带了频道，关闭每个channel里的partner
                    Optional.ofNullable(message)
                            .map(_message -> _message.getChannels())
                            .map(_channels -> _channels.stream())
                            .orElse(Stream.empty())
                            .forEach(_channel -> {
                                        ModelFieldConfig m2mField = PamirsSession.getContext().getModelField(MessageChannel.MODEL_MODEL, "partners");
                                        String through = m2mField.getThrough();
                                        String left = m2mField.getThroughRelationFields().get(0);
                                        String right = m2mField.getThroughReferenceFields().get(0);
                                        List<Map<String, Object>> m2mChannelPartnerMapList = Models.data().queryListByWrapper(Pops.<Map<String,Object>>query()
                                                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(left), _channel.getId())
                                                .setModel(through));
                                        Optional.ofNullable(m2mChannelPartnerMapList)
                                                .ifPresent(_m2mChannelPartnerMapList
                                                        -> _m2mChannelPartnerMapList.forEach(_m2mChannelPartner -> {
                                                            Long partnerId = TypeUtils.createLong(_m2mChannelPartner.get(right));
                                                            PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, partnerId.toString());
                                                        }));
                                    }
                            );
                    return _retMessage;
                })
                .orElse(null);
    }

    @Action
    public PamirsMessage readMail(PamirsMessage message) {
        Long resId = message.getId();
        if (resId != null) {
            message = message.queryById();
            if(MessageTypeEnum.WORKFLOW.equals(message.getMessageType())) return message;
            UnreadMessage needAction = new UnreadMessage()
                    .setReadDone(Boolean.TRUE);
            needAction.updateByWrapper(needAction, Pops.<UnreadMessage>lambdaQuery().from(UnreadMessage.class).eq(UnreadMessage::getResId, resId));
            PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, PamirsSession.getUserId().toString());
        } else {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_CLIENT_ARG_ERROR).errThrow();
        }
        return message;
    }
}
