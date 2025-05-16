package pro.shushi.pamirs.message.engine.message;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.gateways.graph.longpolling.PamirsLongPolling;
import pro.shushi.pamirs.framework.orm.json.PamirsDataUtils;
import pro.shushi.pamirs.message.MessageModule;
import pro.shushi.pamirs.message.enmu.*;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.message.tmodel.MessageGroup;
import pro.shushi.pamirs.message.tmodel.MessageLongPolling;
import pro.shushi.pamirs.message.tmodel.SystemMessage;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DefaultMessageSender implements MessageSender {
    /**
     * 广播系统消息，多个接收人
     *
     * @param systemMessage
     * @return
     */
    @Override
    public Boolean sendSystemMailBroadcast(SystemMessage systemMessage) {
        List<PamirsMessage> mapList = new ArrayList<>();
        List<PamirsMessage> messageList = systemMessage.getMessages();
        List<PamirsUser> partners = systemMessage.getPartners();

        if (CollectionUtils.isEmpty(messageList) || CollectionUtils.isEmpty(partners)) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_NO_PARTNER_OR_MESSAGE_ERROR).errThrow();
        }
        MessageChannel channel = null;
        //将这个消息写入第一个系统消息频道，已初始化进去
        try {
            List<MessageChannel> list = new MessageChannel().setName(MessageGroupTypeEnum.SYSTEM_MAIL_BROADCAST.getValue())
                    .setChannelType(MessageChannelTypeEnum.SYSTEM_MAIL_BROADCAST)
                    .queryList();
            channel = list.get(0);
            List<MessageChannel> channelList = new ArrayList<>();
            MessageChannel channelOnlyId = new MessageChannel();
            channelOnlyId.setId(channel.getId());
            channelList.add(channelOnlyId);
            for (PamirsMessage message : messageList) {
                message.setResModel(MessageChannel.MODEL_MODEL);
                message.setChannels(channelList);
                message.setMessageType(MessageTypeEnum.BROADCAST);
                mapList.add(message);
            }
            //看partner是否在系统消息频道，有就不加了，没就加进去
            for (PamirsUser partner : partners) {
                if (partner.getId() == null) {
                    throw PamirsException.construct(MessageExpEnumerate.MAIL_NO_PARTNER_OR_MESSAGE_ERROR).errThrow();
                }
                isInChannel(partner.getId(), channel.getId());

            }
        } catch (SQLException e) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        List<PamirsMessage> batch = new PamirsMessage().createBatch(mapList);
        ModelFieldConfig channels = PamirsSession.getContext().getModelField(PamirsMessage.MODEL_MODEL, "channels");
        String through = channels.getThrough();
        String left = channels.getThroughRelationFields().get(0);
        String right = channels.getThroughReferenceFields().get(0);
        if (CollectionUtils.isNotEmpty(batch)) {
            List<Map<String, Object>> create = new ArrayList<>();
            Map<Long, List<MessageChannel>> collect = batch.stream().collect(Collectors.toMap(_k -> _k.getId(), _v -> _v.getChannels(), (_o, _n) -> _n));
            collect.forEach((aLong, mailChannels) -> {
                for (MessageChannel messageChannel : mailChannels) {
                    Map<String,Object> map = new HashMap<>();
                    map.put(left,aLong);
                    map.put(right, messageChannel.getId());
                    map.put(VariableNameConstants.entityModel,through);
                    create.add(map);
                }
            });
            Models.origin().createOne(PamirsDataUtils.parseModelObject(through,JsonUtils.toJSONString(create)));
        }

        return true;
    }

    /**
     * 发系统消息 点对点
     *
     * @param systemMessage
     * @return
     */
    @Override
    public Boolean sendSystemMail(SystemMessage systemMessage) {
        if (CollectionUtils.isEmpty(systemMessage.getMessages()) || CollectionUtils.isEmpty(systemMessage.getPartners())) {
            throw PamirsException.construct(MessageExpEnumerate.MAIL_NO_PARTNER_OR_MESSAGE_ERROR).errThrow();
        }
        for (PamirsUser partner : systemMessage.getPartners()) {
            List<String> channelNameList = new ArrayList<>();
            channelNameList.add(MessageGroupTypeEnum.SYSTEM_MAIL.getValue());
            channelNameList.add(String.valueOf(partner.getId()));
            String channelName = channelNameList.get(0) + "-" + channelNameList.get(1);
            try {
                List<MessageChannel> existList = new MessageChannel().setName(channelName).queryList();
                MessageChannel channelMap;
                if (CollectionUtils.isEmpty(existList)) {
                    PamirsUser he = new PamirsUser();
                    he.setId(partner.getId());
                    List<PamirsUser> partners = new ArrayList<>();
                    partners.add(he);

                    MessageChannel channel = new MessageChannel();
                    //channel.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.SYSTEMMAILURL.getValue()).getValue());
                    channel.setName(channelName);
                    channel.setChannelType(MessageChannelTypeEnum.SYSTEM_MAIL);
                    channel.setPartners(partners);
                    channel.setOpenType(MessageChannelOpenTypeEnum.PRIVATE);
                    channelMap = channel.create();

                    channelMap.fieldSave(MessageChannel::getPartners);
                } else {
                    channelMap = existList.get(0);
                }

                List<MessageChannel> channelList = new ArrayList<>();
                MessageChannel channelOnlyId = new MessageChannel();
                channelOnlyId.setId(channelMap.getId());
                channelList.add(channelOnlyId);
                List<PamirsMessage> mapList = new ArrayList<>();

                for (PamirsMessage message : systemMessage.getMessages()) {
                    message.setResModel(MessageChannel.MODEL_MODEL);
                    message.setChannels(channelList);
                    message.setModule(Optional.ofNullable(message.getModule()).orElse(MessageModule.MODULE_MODULE));
                    message.unsetId();
                    if(!MessageTypeEnum.WORKFLOW.equals(message.getMessageType()) && !MessageTypeEnum.COPY.equals(message.getMessageType()) && !MessageTypeEnum.SHARING.equals(message.getMessageType())){
                        message.setMessageType(MessageTypeEnum.NOTIFICATION);
                    }
                    mapList.add(message);
                }

                ModelFieldConfig channels = PamirsSession.getContext().getModelField(PamirsMessage.MODEL_MODEL, "channels");
                String through = channels.getThrough();
                String left = channels.getThroughRelationFields().get(0);
                String right = channels.getThroughReferenceFields().get(0);
                List<PamirsMessage> batch = new PamirsMessage().createBatch(mapList);
                if (CollectionUtils.isNotEmpty(batch)) { // 处理m2m channels 的数据
                    Map<String, Object> create = new HashMap<>();
                    Map<Long, List<MessageChannel>> collect = batch.stream().collect(Collectors.toMap(_k -> _k.getId(), _v -> _v.getChannels(), (_o, _n) -> _n));
                    Optional.ofNullable(collect).ifPresent(longListMap -> {
                        longListMap.forEach((aLong, mailChannels) -> {
                            Optional.ofNullable(mailChannels).ifPresent(innerChannels -> {
                                innerChannels.stream().forEach(channel -> {
                                    create.put(left, aLong);
                                    create.put(right, channel.getId());
                                    create.put(VariableNameConstants.entityModel, through);
                                });
                            });
                        });
                    });
                    Models.origin().createOne(PamirsDataUtils.parseModelObject(through,JsonUtils.toJSONString(create)));

                    //2021年09月30日10:39:53 写入未读消息. 没有模型,不写入未读消息现在没地方展示, 不清楚原来是什么打算
                    List<UnreadMessage> unreadMessages = batch.stream().map(_message->{
                        UnreadMessage unreadMessage = new UnreadMessage();
                        unreadMessage.setUser(partner);
//                        needAction.setResModel();
//                        needAction.setResId();
                        unreadMessage.setModule(Optional.ofNullable(_message.getModule()).orElse(MessageModule.MODULE_MODULE));
                        unreadMessage.setSubject(_message.getSubject());
                        unreadMessage.setResId(_message.getResId());
                        unreadMessage.setMessage(_message);
                        unreadMessage.setMessageType(_message.getMessageType());
                        unreadMessage.construct();//默认值
                        return unreadMessage;
                    }).collect(Collectors.toList());
                    Models.origin().createBatch(unreadMessages);
                }

            } catch (Exception e) {
                throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
            }
        }
        return true;

    }

    /**
     * 发送频道消息
     *
     * @param groupTransient
     * @return
     */
    @Override
    public Boolean sendChannelMail(MessageGroup groupTransient) {
        return null;
    }

    /**
     * 发送模型消息
     *
     * @param messageList
     * @param partnerList
     * @return
     */
    @Override
    public Boolean sendModelMail(List<PamirsMessage> messageList, List<PamirsUser> partnerList) {
        List<PamirsMessage> mapList = new ArrayList<>();
        List<UnreadMessage> o2MCreate = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(messageList)) {
            for (PamirsMessage systemMessage : messageList) {
                List<UnreadMessage> needActionList = new ArrayList<>();
                //同时写入needAction表
                for (PamirsUser user : partnerList) {
                    UnreadMessage needAction = new UnreadMessage();
                    needAction.setUser(user);
                    needAction.setResModel(systemMessage.getResModel());
                    needAction.setResId(systemMessage.getResId());
                    needAction.setMessageType(systemMessage.getMessageType());
                    needAction.setModule(Optional.ofNullable(systemMessage.getModule()).orElse(MessageModule.MODULE_MODULE));
                    needAction.setSubject(systemMessage.getSubject());
                    needAction.construct();//默认值
                    needActionList.add(needAction);
                }
                //在消息表中写入模型消息
                systemMessage.setPartnerNeedAction(needActionList);
                //这里发的模型消息不要发送者
                mapList.add(systemMessage);
            }
        }
        List<PamirsMessage> messages = new PamirsMessage().createBatch(mapList);
        if (CollectionUtils.isNotEmpty(messages)) {
            //关闭longpolling
            Map<Long, List<UnreadMessage>> collect = messageList.stream().collect(Collectors.toMap(_k -> _k.getId(), _v -> _v.getPartnerNeedAction(), (_o, _n) -> _n));
            collect.forEach((aLong, mailMessagePartnerNeedActions) -> {
                Optional.ofNullable(mailMessagePartnerNeedActions).ifPresent(mailMessages1 -> mailMessages1.forEach(message -> {
                    PamirsMessage mailMessage = new PamirsMessage();
                    mailMessage.setId(aLong);
                    message.setMessage(mailMessage);
                }));
                new UnreadMessage().createBatch(mailMessagePartnerNeedActions);
            });
            for (PamirsUser partner : partnerList) {
                // todo zz 长连接
                PamirsLongPolling.close(MessageLongPolling.MODEL_MODEL, partner.getId().toString());
            }
        }
        return true;
    }

    //判断一个partner是否在一个频道中
    private void isInChannel(Long partnerId, Long channelId) throws SQLException {
        ModelFieldConfig partnersField = PamirsSession.getContext().getModelField(MessageChannel.MODEL_MODEL, "partners");
        // NamedApi api = CommonApiFactory.getApi(NamedApi.class);
        String m2mTable = partnersField.getThrough();
        String left = partnersField.getThroughRelationFields().get(0);
        String right = partnersField.getThroughReferenceFields().get(0);
        List<Map<String, Object>> resultList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(right), partnerId)
                .setModel(m2mTable));
        List<Long> idList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (Map<String, Object> m : resultList) {
                Long id = TypeUtils.createLong(m.get(left));
                if (id != null) {
                    idList.add(id);
                }
            }
        }
        if (!idList.contains(channelId)) {
            Map<String, Object> addMap = new HashMap<>();
            addMap.put(right, partnerId);
            addMap.put(left, channelId);
            addMap.put(VariableNameConstants.entityModel, m2mTable);
            Models.data().createOne(addMap);
        }
    }
}
