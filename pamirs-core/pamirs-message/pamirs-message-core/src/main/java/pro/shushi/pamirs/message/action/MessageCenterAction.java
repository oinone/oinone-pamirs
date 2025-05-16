package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.DefaultAvatarEnum;
import pro.shushi.pamirs.message.enmu.MessageChannelTypeEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.MessageGroupTypeEnum;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.message.tmodel.MessageCenter;
import pro.shushi.pamirs.message.tmodel.MessageGroup;
import pro.shushi.pamirs.message.tmodel.SystemMessage;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.message.utils.MessageUtils;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.*;

@Component
@Slf4j
@Model.model(MessageCenter.MODEL_MODEL)
public class MessageCenterAction {

    @Function(openLevel = {FunctionOpenEnum.API,FunctionOpenEnum.REMOTE}, summary = "消息中心构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public MessageCenter construct(MessageCenter message) {
        return message.setLastMessageId(fetchLastId()).setCount(fetchUnreadCount()).setMessageGroups(fetchMessageGroups());
    }

    private Long fetchLastId() {
        return Optional.ofNullable(new PamirsMessage().queryList(Pops.<PamirsMessage>query()
                .from(PamirsMessage.MODEL_MODEL)
                .select("MAX(`id`) `maxId`")
                .setModel(PamirsMessage.MODEL_MODEL)))
                .filter(CollectionUtils::isNotEmpty)
                .map(maps -> maps.get(0))
                .map(D::get_d)
                .map(stringObjectMap -> (Long) stringObjectMap.get("maxId"))
                .orElse(0L);
    }

    private Long fetchUnreadCount() {
        return new UnreadMessage().setUser((PamirsUser) new PamirsUser().setId(PamirsSession.getUserId())).setReadDone(false).count();
    }

    private List<MessageGroup> fetchMessageGroups() {

        List<MessageGroup> messageGroups = new ArrayList<>();
        /**
         * 设置所有频道相关的组消息
         */
        List<MessageChannel> channelList = MessageUtils.getChannels();
        if (CollectionUtils.isNotEmpty(channelList)) {
            ModelFieldConfig message = PamirsSession.getContext().getModelField(PamirsMessage.MODEL_MODEL, "channels");
            ModelFieldConfig channels = PamirsSession.getContext().getModelField(MessageChannel.MODEL_MODEL, "partners");
//            channels.
            String through = message.getThrough();
            ModelConfig mailChannel = PamirsSession.getContext().getModelConfig(MessageChannel.MODEL_MODEL);
            for (MessageChannel channel : channelList) {
                MessageGroup channelGroup = new MessageGroup();
                channelGroup.setResModel(mailChannel.getModel());
                channelGroup.setResId(channel.getId());
                channelGroup.setUnreadCount(0);
                channelGroup.setTitle(channel.getName());
                Optional.ofNullable(Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                        .select("MAX(`" + CaseFormatUtil.lowerCamelToLowerUnderscore(message.getThroughRelationFields().get(0)) + "`) `maxId`")
                        .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(message.getThroughReferenceFields().get(0)), channel.getId())
                        .setModel(through)))
                        .filter(CollectionUtils::isNotEmpty)
                        .map(maps -> maps.get(0))
                        .map(map -> (Long) map.get("maxId"))
                        .map(mailMessageId -> new PamirsMessage().setId(mailMessageId).<PamirsMessage>queryById())
                        .ifPresent(o -> {
                            channelGroup.setLastMessageBody(o.getBody())
                                    .setLastMessageTime(o.getCreateDate())
                                    .setLastMessageId(o.getId());
                        });
                channelGroup.setChannel(channel);
                String channelThrough = channels.getThrough();
                List<Map<String, Object>> list = Models.origin().queryListByWrapper(Pops.<Map<String, Object>>query()
                        .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(channels.getThroughRelationFields().get(0)), channel.getId())
                        .from(channelThrough));
                if (MessageChannelTypeEnum.CHAT.equals(channel.getChannelType())) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        for (Map<String, Object> m2mChannelPartnerMap : list) {
                            Long chatPartnerId = TypeUtils.createLong(m2mChannelPartnerMap.get(channels.getThroughReferenceFields().get(0)));
                            if (!chatPartnerId.equals(PamirsSession.getUserId())) {
                                PamirsUser partner = new PamirsUser().setId(chatPartnerId).queryById();
                                if (Objects.nonNull(partner)) {
                                    channelGroup.setChatPartner(partner);
                                    if (partner.getAvatarBig() != null && partner.getAvatarBig().getId() != null) {
                                        channelGroup.setIconUrl(new PamirsFile().setId(partner.getAvatarBig().getId()).<PamirsFile>queryById().getUrl());
                                    }
                                }
                                break;
                            }
                        }
                    }
                    //设置title
                    if (channelGroup.getChatPartner() != null) {
                        channelGroup.setTitle(channelGroup.getChatPartner().getName());
                    }
                    //设置消息grouptype为chat
                    channelGroup.setGroupType(MessageGroupTypeEnum.CHAT);
                    try {
                        channelGroup.setIconUrl(channel.getIconUrl() == null ? (ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHATURL.getValue()).getValue()) : channel.getIconUrl());
                    } catch (Exception e) {
                        throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR).errThrow();
                    }
                } else {
                    //设置消息grouptype为channel
                    channelGroup.setGroupType(MessageGroupTypeEnum.CHANNEL);
                    try {
                        channelGroup.setIconUrl(channel.getIconUrl() == null ? (ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHANNELURL.getValue()).getValue()) : channel.getIconUrl());
                        //对系统消息特殊处理
                        if (MessageChannelTypeEnum.SYSTEM_MAIL.getValue().equalsIgnoreCase(channel.getChannelType().getValue())) {
                            channel.setName(MessageChannelTypeEnum.SYSTEM_MAIL.getDisplayName());
                            channelGroup.setTitle(MessageGroupTypeEnum.SYSTEM_MAIL.getDisplayName());
                            if (channel.getIconUrl() == null) {
                                ResourceConfig resourceConfig = ResourceConfig.fetchConfigValue(DefaultAvatarEnum.SYSTEMMAILURL.getValue());
                                if (null != resourceConfig) {
                                    channelGroup.setIconUrl(resourceConfig.getValue());
                                }
                            } else {
                                channelGroup.setIconUrl(channel.getIconUrl());
                            }
                        }
                        //对广播特殊处理
                        if (MessageChannelTypeEnum.SYSTEM_MAIL_BROADCAST.getValue().equalsIgnoreCase(channel.getChannelType().getValue())) {
                            channel.setName(MessageChannelTypeEnum.SYSTEM_MAIL_BROADCAST.getDisplayName());
                            channelGroup.setTitle(MessageGroupTypeEnum.SYSTEM_MAIL_BROADCAST.getDisplayName());
                            channelGroup.setIconUrl(channel.getIconUrl() == null ? (ResourceConfig.fetchConfigValue(DefaultAvatarEnum.SYSTEMMAILURL.getValue()).getValue()) : channel.getIconUrl());
                        }
                    } catch (Exception e) {
                        throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR).errThrow();
                    }
                }
                //设置当前用户名
                channelGroup.setCurrentUserName(PamirsSession.getUserName());
                messageGroups.add(channelGroup);
            }
        }
        /**
         * 设置模型消息
         */
        List<UnreadMessage> needActionList = new UnreadMessage()
                .setUser((PamirsUser) new PamirsUser().setId(PamirsSession.getUserId()))
                .setReadDone(false)
                .queryList();
        if (CollectionUtils.isNotEmpty(needActionList)) {
            Map<String, MessageGroup> groupMap = new HashMap<>();
            Map<String, PamirsMessage> latestMessageMap = new HashMap<>();
            for (UnreadMessage needAction : needActionList) {
                if (!SystemMessage.MODEL_MODEL.equalsIgnoreCase(needAction.getResModel())) {
                    String key = needAction.getResModel() + needAction.getResId();
                    MessageGroup messageGroup = groupMap.get(key);
                    if (messageGroup == null) {
                        messageGroup = new MessageGroup();
                        messageGroup.setUnreadCount(0);
                        messageGroup.setResModel(needAction.getResModel());
                        messageGroup.setResId(needAction.getResId());
                        //设置模型消息组标题
                        ModelConfig model = PamirsSession.getContext().getModelConfig(needAction.getResModel());
//                            messageGroup.setTitle(model.getDisplayName());
//                        messageGroup.setTitle(needAction.getResModel() + " " + needAction.getResId());
                        groupMap.put(key, messageGroup);
                    }
                    messageGroup.setUnreadCount(messageGroup.getUnreadCount() + 1);
                    PamirsMessage latestMessage = latestMessageMap.get(key);
                    if (latestMessage == null) {
                        latestMessageMap.put(key, needAction.getMessage());
                    } else {
                        if (latestMessage.getId() < needAction.getMessage().getId()) {
                            latestMessageMap.put(key, needAction.getMessage());
                        }
                    }
                }
            }
            for (Map.Entry<String, MessageGroup> entry : groupMap.entrySet()) {
                String key = entry.getKey();
                MessageGroup messageGroup = entry.getValue();
                PamirsMessage latestMessage = latestMessageMap.get(key);
                if (latestMessage != null) {
                    PamirsMessage mmm = new PamirsMessage().setId(latestMessage.getId()).queryById();
                    messageGroup.setLastMessageBody(mmm.getBody());
                    messageGroup.setLastMessageTime(mmm.getCreateDate());
                    messageGroup.setLastMessageId(mmm.getId());
                }
                //设置消息grouptype为模型消息
                messageGroup.setGroupType(MessageGroupTypeEnum.MODEL_MAIL);
                //设置名字
                messageGroup.setCurrentUserName(PamirsSession.getUserName());
                //设置模型消息组头像
//                try {
//                    messageGroup.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.MODELMAILURL.getValue()).getValue());
//                } catch (Exception e) {
//                    throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR).errThrow();
//                }
                messageGroups.add(messageGroup);
            }
        }
        ;
        //去重title为一样的
        if (CollectionUtils.isNotEmpty(messageGroups)) {
//            messageGroups = removeDuplicate(messageGroups);
        }
        return messageGroups;
    }


    private List<MessageGroup> removeDuplicate(List<MessageGroup> groupTransients) {
        Set<MessageGroup> set = new TreeSet<MessageGroup>(new Comparator<MessageGroup>() {
            @Override
            public int compare(MessageGroup o1, MessageGroup o2) {
                String o1Title = o1.getTitle();
                String o2Title = o2.getTitle();
                if (null != o1Title && null != o2Title) {
                    return o1Title.compareTo(o2Title);
                }
                return 1;
            }
        });
        set.addAll(groupTransients);
        return new ArrayList<MessageGroup>(set);
    }
}
