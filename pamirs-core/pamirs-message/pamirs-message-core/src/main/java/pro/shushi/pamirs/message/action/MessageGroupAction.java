package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.gateways.graph.longpolling.PamirsLongPolling;
import pro.shushi.pamirs.message.engine.MessageEngine;
import pro.shushi.pamirs.message.engine.third.ThirdSender;
import pro.shushi.pamirs.message.enmu.*;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.third.ThirdMailMessageTransientModel;
import pro.shushi.pamirs.message.tmodel.MessageGroup;
import pro.shushi.pamirs.message.tmodel.MessageLongPolling;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.message.utils.MessageUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.*;

import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.SYSTEM_ERROR;

@Slf4j
@Model.model(MessageGroup.MODEL_MODEL)
public class MessageGroupAction {

    //@Action(args = "group", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.MailMessageGroupTransient", isExported = true)
    //添加一个消息聊天组
    @Action
    public MessageGroup constructGroup(MessageGroup group) {
        Optional.ofNullable(group.getGroupType()).orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_CLIENT_ARG_ERROR).errThrow());
        Optional.ofNullable(group.getId()).orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.MAIL_CLIENT_ARG_ERROR).errThrow());

        MessageChannel channel = null;
        PamirsUser chatPartner = null;
        //如果传来的是类型是chat，则id是个人用户的
        if (group.getGroupType().getValue().equalsIgnoreCase(MessageGroupTypeEnum.CHAT.getValue())) {
            chatPartner = new PamirsUser();
            chatPartner.setId(group.getId());
        } else if (group.getGroupType().getValue().equalsIgnoreCase(MessageGroupTypeEnum.CHANNEL.getValue())) {
            channel = new MessageChannel();
            channel.setId(group.getId());
            channel = channel.queryById();
            if (null == channel) {
                throw PamirsException.construct(MessageExpEnumerate.CHANNEL_ISNOT_EXIST).errThrow();
            }
        }
        try {
            if (chatPartner != null && channel == null) {
                chatPartner = new PamirsUser().setId(group.getId()).queryById();
                List<String> names = new ArrayList<>();
                names.add(PamirsSession.getUserId() + "");
                names.add(chatPartner.getId() + "");
                Collections.sort(names);
                String channelName = names.get(0) + "-" + names.get(1);
                List<MessageChannel> existList = new MessageChannel().setName(channelName).queryList();
                if (CollectionUtils.isEmpty(existList)) {
                    channel = new MessageChannel();
                    channel.setName(channelName);
                    List<PamirsUser> partners = new ArrayList<>();
                    PamirsUser mine = new PamirsUser();
                    mine.setId(PamirsSession.getUserId());
                    partners.add(mine);
                    PamirsUser he = new PamirsUser();
                    he.setId(chatPartner.getId());
                    partners.add(he);
                    channel.setPartners(partners);
                    channel.setChannelType(MessageChannelTypeEnum.CHAT);
                    channel.setOpenType(MessageChannelOpenTypeEnum.PRIVATE);
                    channel = channel.create();

                    channel.fieldSave(MessageChannel::getPartners);
                } else {
                    channel = existList.get(0);
                }
            } else if (chatPartner == null && MessageChannelTypeEnum.CHAT.getValue().equalsIgnoreCase(channel.getChannelType().getValue())) {
                //todo 现在没有这种情况了 zz
//                pro.shushi.pamirs.base.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.meta.Model channelModel = PamirsCacheRead.getModel(MailChannel.class.getName());
//                pro.shushi.pamirs.base.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.meta.ModelField m2mChannelPartnerField = ModelFieldUtils.findModelField("partners", channelModel);
//                JdbcRepository m2mChannelPartnerRepo = JdbcRepositoryFactory.getRelationRepository(m2mChannelPartnerField);
//
//                Map<String, Object> m2mChannelPartnerQuery = new HashMap<>();
//                m2mChannelPartnerQuery.put(m2mChannelPartnerField.getColumn1(), channel.getId());
//                List<Map<String, Object>> m2mChannelPartnerMapList = m2mChannelPartnerRepo.findAll(m2mChannelPartnerQuery);
//                if (CollectionUtils.isNotEmpty(m2mChannelPartnerMapList)) {
//                    for (Map<String, Object> m2mChannelPartner : m2mChannelPartnerMapList) {
//                        Long chatPartnerId = ParamUtils.createLong(m2mChannelPartner.get(NamedUtils.clomn2FieldName(m2mChannelPartnerField.getColumn2())));
//                        if (!chatPartnerId.equals(user.getId())) {
//                            Map<String, Object> chatPartnerMap = partnerRepo.findOne(chatPartnerId);
//                            if (chatPartnerMap != null) {
//                                chatPartner = ObjectMapUtils.mapToModel(chatPartnerMap, ResourcePartner.class.getName());
//                                break;
//                            }
//                        }
//                    }
//                }
            }

            group.setResModel(MessageChannel.MODEL_MODEL);
            //todo 可优化，少查一次db
            MessageChannel one = new MessageChannel().setId(channel.getId()).queryById();
            if (one == null) {
                throw PamirsException.construct(MessageExpEnumerate.MAIL_CLIENT_ARG_ERROR).errThrow();
            }
            group.setResId(channel.getId());
            channel = one;
            group.setResName(channel.getName());
            group.setChannel(channel);
            if (chatPartner != null && channel.getChannelType().getValue().equalsIgnoreCase(MessageChannelTypeEnum.CHAT.getValue())) {
                group.setTitle(chatPartner.getName());
//                if (chatPartner.getAvatarBig() != null && chatPartner.getAvatarBig().getId() != null) {
//                    JdbcRepository fileRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(ResourceFile.class.getName(), DataOpTypeEnum.READ);
//                    Map<String, Object> fileMap = fileRepo.findOne(chatPartner.getAvatarBig().getId());
//                    ResourceFile file = ObjectMapUtils.mapToModel(fileMap, ResourceFile.class.getName());
//                    group.setIconUrl(file.getUrl());
//                }
            } else {
                group.setTitle(channel.getName());
                group.setIconUrl(channel.getIconUrl());
            }
        } catch (Exception e) {
            throw PamirsException.construct(SYSTEM_ERROR, e).errThrow();
        }
        return group;
    }

    //@Action(args = "group", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.MailMessageGroupTransient", isExported = true)
    //点开聊天窗口，并获取消息
    @Action
    public MessageGroup fetchMessages(MessageGroup group) {
        try {
            if (group.getResModel().equalsIgnoreCase(MessageChannel.MODEL_MODEL)) {
                //频道消息查询

                ModelFieldConfig channels = PamirsSession.getContext().getModelField(PamirsMessage.MODEL_MODEL, "channels");
                String through = channels.getThrough();
                String leftName = channels.getThroughRelationFields().get(0);
                String rightName = channels.getThroughReferenceFields().get(0);
                List<Map<String, Object>> m2mList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                        .ge(CaseFormatUtil.lowerCamelToLowerUnderscore(leftName), group.getLastMessageId() != null ? group.getLastMessageId() : 0L)
                        .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(rightName), group.getResId())
                        .setModel(through));
                if (CollectionUtils.isNotEmpty(m2mList)) {
                    List<Long> messageIdList = new ArrayList<>();
                    for (Map<String, Object> map : m2mList) {
                        messageIdList.add(TypeUtils.createLong(map.get(leftName)));
                    }
                    List<PamirsMessage> messages = new PamirsMessage().queryList(Pops.<PamirsMessage>lambdaQuery().in(PamirsMessage::getId, messageIdList));
                    if (CollectionUtils.isNotEmpty(messageIdList)) {
                        //为消息设置头像
                        if (CollectionUtils.isNotEmpty(messages)) {
                            for (PamirsMessage message : messages) {
                                //获取消息发送者
                                PamirsUser sendPartner = message.getSendPartner();
                                if (null != sendPartner) {
                                    sendPartner = new PamirsUser().setId(sendPartner.getId()).queryById();
                                    //设置消息显示位置
                                    if (sendPartner.getId().equals(PamirsSession.getUserId())) {
                                        message.setMailMaster(MessageMasterEnum.SELF);
                                    } else {
                                        message.setMailMaster(MessageMasterEnum.OTHER);
                                    }
                                    //先取大图，没有再拿中图
                                    PamirsFile avatarFile = sendPartner.getAvatarBig() != null ? sendPartner.getAvatarBig() : sendPartner.getAvatarMedium();
                                    //设置消息发送者头像
                                    if (avatarFile != null && null != avatarFile.getId()) {
                                        avatarFile = new PamirsFile().setId(avatarFile.getId()).queryById();
                                        sendPartner.setAvatarMedium(avatarFile);
                                        //塞头像
                                        message.setIconUrl(avatarFile.getUrl());
                                    } else {
                                        message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHATURL.getValue()).getValue());
                                    }
                                    if (group.getIconUrl() == null) {
                                        group.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHANNELURL.getValue()).getValue());
                                    }
                                    message.setSendPartner(sendPartner);
                                } else if (MessageTypeEnum.NOTIFICATION.equals(message.getMessageType())) {
                                    //系统通知聊天窗口消息处理
                                    message.setMailMaster(MessageMasterEnum.OTHER);
                                    message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.SYSTEMMAILURL.getValue()).getValue());
                                    PamirsUser partner = new PamirsUser();
                                    partner.setName(MessageGroupTypeEnum.SYSTEM_MAIL.getDisplayName());
                                    message.setSendPartner(partner);
                                }
                                //广播消息聊天窗口消息处理
                                if (MessageTypeEnum.BROADCAST == message.getMessageType()) {
                                    PamirsUser partner = new PamirsUser();
                                    partner.setName(MessageGroupTypeEnum.SYSTEM_MAIL_BROADCAST.getDisplayName());
                                    message.setSendPartner(partner);
                                    message.setMailMaster(MessageMasterEnum.OTHER);
                                    message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.SYSTEMMAILURL.getValue()).getValue());
                                }
                            }
                        }
                        group.setMessages(messages);
                        group.setGroupType(MessageGroupTypeEnum.CHANNEL);
                    }


                }
            } else {
                //模型消息查询
                //fixme zl 模型消息是跳转到模型的详情页
                if (group.getIconUrl() == null) {
                    group.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.MODELMAILURL.getValue()).getValue());
                }
            }
        } catch (Exception e) {
            throw PamirsException.construct(SYSTEM_ERROR, e).errThrow();
        }
        return group;
    }

    //@Action(args = "group", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.pro.shushi.pamirs.user.core.base.model.MailMessageGroupTransient", isExported = true)
    //聊天窗口发送消息
    @Action
    public MessageGroup postMessage(MessageGroup group) {
        if (StringUtils.isBlank(group.getMessageInput())) {
            return group;
        }
        List<MessageChannel> channels = new ArrayList<>();
        MessageChannel channelOnlyId = new MessageChannel();
        if (group.getChannel() != null) {
            channelOnlyId.setId(group.getChannel().getId());
            channels.add(channelOnlyId);
            List<Long> longs = Optional.ofNullable(MessageUtils.fetchPartnerIdsByChannel(group.getChannel().getId())).orElse(new ArrayList<>());
            MessageEngine.<ThirdSender>get(MessageEngineTypeEnum.THIRD_PUSH).get(null).send(new ThirdMailMessageTransientModel(PamirsSession.getUserId(), longs, group.getMessageInput()));
//            channels.add(group.getChannel());
        } else if (group.getChatPartner() != null) {
            MessageEngine.<ThirdSender>get(MessageEngineTypeEnum.THIRD_PUSH).get(null).send(new ThirdMailMessageTransientModel(PamirsSession.getUserId(), Arrays.asList(group.getChatPartner().getId()), group.getMessageInput()));
            MessageChannel channel = new MessageChannel();
            channel.setChannelType(MessageChannelTypeEnum.CHAT);
            Long id = PamirsSession.getUserId();
            List<String> namePair = new ArrayList<>();
            namePair.add(id + "");
            namePair.add(group.getChatPartner().getId() + "");
            Collections.sort(namePair);
            String name = namePair.get(0) + "-" + namePair.get(1);
            channel.setName(name);
            channel.setOpenType(MessageChannelOpenTypeEnum.PRIVATE);
            List<PamirsUser> partners = new ArrayList<>();
            partners.add(group.getChatPartner());
            PamirsUser loginPartner = new PamirsUser();
            loginPartner.setId(id);
            partners.add(loginPartner);
            channel.setPartners(partners);

            MessageChannel messageChannel = new MessageChannel().setName(name).queryOne();
            if (Objects.nonNull(messageChannel)) {
                channel = messageChannel;
            } else {
                MessageChannel messageChannel1 = channel.create();
                messageChannel1.fieldSave(MessageChannel::getPartners);
                if (Objects.nonNull(messageChannel1)) {
                    channel = messageChannel1;
                }
            }

            group.setChannel(channel);
            channels.add(channel);
        }

        PamirsUser partner = new PamirsUser();
        partner.setId(PamirsSession.getUserId());

        PamirsMessage message = new PamirsMessage();
        message.setResModel(MessageChannel.MODEL_MODEL);
        message.setResId(group.getChannel().getId());
        message.setResName(group.getChannel().getName());
        message.setSubject("");
        message.setBody(group.getMessageInput());
        message.setMessageType(MessageTypeEnum.COMMENT);
        message.setChannels(channels);
        message.setSendPartner(partner);
        message = message.create();
        message.fieldSave(PamirsMessage::getChannels);
        if (null != message) {
            List<PamirsMessage> messages = new ArrayList<>();
            messages.add(message);
            group.setMessages(messages);
            //如果是频道,频道里的所有partner都关闭
            if (group.getChannel() != null) {

                ModelFieldConfig partners = PamirsSession.getContext().getModelField(MessageChannel.MODEL_MODEL, "partners");
                String through = partners.getThrough();
                String left = partners.getThroughRelationFields().get(0);
                String right = partners.getThroughReferenceFields().get(0);
                List<Map<String, Object>> m2mChannelPartnerMapList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                        .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(left), group.getChannel().getId())
                        .setModel(through));
                if (CollectionUtils.isNotEmpty(m2mChannelPartnerMapList)) {
                    for (Map<String, Object> m2mChannelPartner : m2mChannelPartnerMapList) {
                        Long chatPartnerId = (Long) m2mChannelPartner.get(right);
                        PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, chatPartnerId.toString());
                    }
                }
            }

            return group;
        }
        return group;
    }
}
