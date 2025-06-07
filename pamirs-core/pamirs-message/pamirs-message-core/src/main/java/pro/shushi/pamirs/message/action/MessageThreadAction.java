package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.base.resource.PamirsFile;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.DefaultAvatarEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.MessageMasterEnum;
import pro.shushi.pamirs.message.model.*;
import pro.shushi.pamirs.message.tmodel.MessageThread;
import pro.shushi.pamirs.message.tmodel.PartnerSubtypeRelation;
import pro.shushi.pamirs.message.utils.MessageUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.*;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/21
 */
//@pro.shushi.pamirs.meta.annotation.Fun(MessageThread.MODEL_MODEL)
@Model.model(MessageThread.MODEL_MODEL)
public class MessageThreadAction {

    // @Action(args = "mailThread", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.MailThread", isLongPolling = true, isExported = true)
    @Action
    public MessageThread fetchDeltaMessages(MessageThread messageThread) {// FIXME zl new functions
        List<PamirsMessage> messages = Fun.run(MessageThread.MODEL_MODEL, "fetchMessages", messageThread.getMaxMessageId());
        messageThread.setMessages(messages);
        return messageThread;
    }

    @Function(openLevel = FunctionOpenEnum.API, summary = "模型消息线程构造")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public MessageThread construct(MessageThread messageThread) {
        return messageThread
                .setFollowerCount(fetchFollowerCount(messageThread))
                .setFollowingState(fetchFollowingState(messageThread))
                .setMessages(fetchMessages(messageThread))
                .setMessageFollowers(fetchMailFollowers(messageThread))
                .setPartnerSubtypeRel(fetchPartnerSubtypeRel(messageThread));
    }

    public Boolean fetchFollowingState(MessageThread messageThread) {
        /**
         * 先找到当前登录用户所属的mail follower有哪些
         */
        List<MessageFollower> followerList = MessageUtils.getMailFollower4LoginUser(true, messageThread.getActiveModel(), messageThread.getActiveId());
        if (CollectionUtils.isEmpty(followerList)) {
            return false;
        }

        /**
         * 到mail follower和mail subtype关联表，count是否有关注
         */
        ModelFieldConfig subtypeField = PamirsSession.getContext().getModelField(Models.api().getModel(MessageFollower.class), "subtypes");
        String m2mTableName = subtypeField.getThrough();
        List<Long> followerIds = new ArrayList<>();
        for (MessageFollower follower : followerList) {
            followerIds.add(follower.getId());
        }
        Long count = Models.data().count(Pops.query()
                .in(subtypeField.getRelationFields().get(0), followerIds)
                .setModel(m2mTableName));
        return null != count && (count > 0L);
    }

    public Long fetchFollowerCount(MessageThread messageThread) {
        /**
         * 获取可关注当前模型的所有关注者和频道
         */
        return new MessageFollower().setResModel(messageThread.getActiveModel())
                .setResId(messageThread.getActiveId())
                .count();
    }

    public List<PamirsMessage> fetchMessages(MessageThread messageThread) {
        try {
            /**
             * 将need action中，所有未读消息删除
             */
            Models.data().deleteByEntity(new UnreadMessage()
                    .setResModel(messageThread.getActiveModel())
                    .setResId(messageThread.getActiveId())
                    .setUser((PamirsUser) new PamirsUser().setId(PamirsSession.getUserId())));

            //            JdbcRepository needActionReadRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailMessagePartnerNeedAction.class.getName(), DataOpTypeEnum.WRITE);
//            Map<String, Object> needActionQuery = new HashMap<>();
//            needActionQuery.put("resModel", PamirsEnvironment.getThreadLocal().getActiveModel());
//            needActionQuery.put("resId", PamirsEnvironment.getThreadLocal().getActiveId());
//            needActionQuery.put("partnerId", use.getId());
//            List<Map<String, Object>> needActionDeleteList = new ArrayList<>();
//            needActionDeleteList.add(needActionQuery);
            List<PamirsMessage> messages = new PamirsMessage().queryList(Pops.<PamirsMessage>lambdaQuery()
                    .from(PamirsMessage.MODEL_MODEL)
                    .eq(PamirsMessage::getResId, messageThread.getActiveId())
                    .eq(PamirsMessage::getResModel, messageThread.getActiveModel())
                    .ge(PamirsMessage::getId, messageThread.getMaxMessageId() != null ? messageThread.getMaxMessageId() : 0L));
            if (CollectionUtils.isNotEmpty(messages)) {
                for (PamirsMessage message : messages) {
                    //获取消息发送者
                    PamirsUser sendPartner = message.getSendPartner();
                    if (null != sendPartner) {
                        if (sendPartner.getId().equals(PamirsSession.getUserId())) {
                            message.setMailMaster(MessageMasterEnum.SELF);
                        } else {
                            message.setMailMaster(MessageMasterEnum.OTHER);
                        }

                        PamirsFile avatarFile = sendPartner.getAvatarBig() != null ? sendPartner.getAvatarBig() : sendPartner.getAvatarMedium();

                        if (avatarFile != null) {
                            sendPartner.setAvatarMedium(avatarFile);
                            message.setIconUrl(avatarFile.getUrl());
                        } else {
                            message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHATURL.getValue()).getValue());
                        }
                        message.setSendPartner(sendPartner);

                    } else {
                        //此时的模型消息为触发发送的
                        message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.MODELMAILURL.getValue()).getValue());
                        message.setMailMaster(MessageMasterEnum.OTHER);
                    }
                }
            }

            return messages;
//            needActionReadRepo.deleteInBatch(needActionDeleteList);
        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

    public List<MessageFollower> fetchMailFollowers(MessageThread messageThread) {
        /**
         * 获取模型的所有follower
         */
        List<MessageFollower> followerList = new MessageFollower()
                .setResModel(messageThread.getActiveModel())
                .setResId(messageThread.getActiveId())
                .queryList();
        if (CollectionUtils.isEmpty(followerList)) {
            return followerList;
        }

        List<Long> partnerIds = new ArrayList<>();
        List<Long> channelIds = new ArrayList<>();
        for (MessageFollower f : followerList) {
            if (f.getPartner() != null && f.getPartner().getId() != null) {
                partnerIds.add(f.getPartner().getId());
            }
            if (f.getChannel() != null && f.getChannel().getId() != null) {
                channelIds.add(f.getChannel().getId());
            }
        }


        Map<Long, PamirsUser> partnerMap = new HashMap<>();
        Map<Long, MessageChannel> channelMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(partnerIds)) {
            List<PamirsUser> pamirsUsers = new PamirsUser().queryList(Pops.<PamirsUser>lambdaQuery().in(PamirsUser::getId, partnerIds));
            if (CollectionUtils.isNotEmpty(pamirsUsers)) {
                for (PamirsUser p : pamirsUsers) {
                    partnerMap.put(p.getId(), p);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(channelIds)) {
            List<MessageChannel> channels = new MessageChannel().queryList(Pops.<MessageChannel>lambdaQuery().in(MessageChannel::getId, channelIds));
            for (MessageChannel mc : channels) {
                channelMap.put(mc.getId(), mc);
            }
        }
        /**
         * 将mail follower中的关系模型字段设值
         */
        for (MessageFollower f : followerList) {
            if (f.getPartner() != null && f.getPartner().getId() != null) {
                PamirsUser p = partnerMap.get(f.getPartner().getId());
                if (p != null) {
                    f.setPartner(p);
                }
            }
            if (f.getChannel() != null && f.getChannel().getId() != null) {
                MessageChannel mc = channelMap.get(f.getChannel().getId());
                if (mc != null) {
                    f.setChannel(mc);
                }
            }
        }
        return followerList;
    }

    public List<PartnerSubtypeRelation> fetchPartnerSubtypeRel(MessageThread messageThread) {
        /**
         * 先拿到模型消息的所有子类型
         */
        Map<String, Object> subtypeQuery = new HashMap<>();
        String subtypeModel = messageThread.getActiveModel();
        ModelDefinition modelDefinition = new ModelDefinition().setModel(subtypeModel).queryOne();
        if (Objects.isNull(modelDefinition)) {
            return null;
            // TODO: 2022/4/18 ???
//            throw PamirsException.construct(MessageExpEnumerate.MAIL_SQL_ERROR).errThrow();
        }
        List<MessageSubtype> subtypeList = new MessageSubtype()
                .setModel((ModelDefinition) new ModelDefinition().setId(modelDefinition.getId()))
                .queryList();
        if (CollectionUtils.isEmpty(subtypeList)) {
            return new ArrayList<>();
        }

        /**
         * 获取当前登录用户关注的所有模型消息类型
         */
        List<Long> myFollowSubtypeIds = MessageUtils.getAllMailSubtypeId4LoginUser(true, messageThread.getActiveModel(), messageThread.getActiveId());

        List<PartnerSubtypeRelation> retList = new ArrayList<>();
        for (MessageSubtype s : subtypeList) {
            PartnerSubtypeRelation pss = new PartnerSubtypeRelation();
            pss.setPartnerId(PamirsSession.getUserId());
            pss.setSubtype(s);
            pss.setResModel(messageThread.getActiveModel());
            pss.setResId(TypeUtils.createLong(messageThread.getActiveId()));
            if (CollectionUtils.isNotEmpty(myFollowSubtypeIds) && myFollowSubtypeIds.contains(s.getId())) {
                pss.setSelectState(true);
            } else {
                pss.setSelectState(false);
            }
            retList.add(pss);
        }
        return retList;
    }
}
