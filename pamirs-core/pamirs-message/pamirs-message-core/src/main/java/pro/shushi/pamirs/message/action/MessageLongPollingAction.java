package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.DefaultAvatarEnum;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.MessageMasterEnum;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.tmodel.MessageLongPolling;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 描述
 * @Author junwei
 * @Date 2020/4/19
 */
@Slf4j
@Model.model(MessageLongPolling.MODEL_MODEL)
public class MessageLongPollingAction {

    @Function(openLevel = FunctionOpenEnum.API)
    @Function.Advanced(isLongPolling = true,longPollingTimeout = 40000,longPollingKey = "userId")
    public MessageLongPolling longPolling(MessageLongPolling pollingTransient) {
        //获取一个频道的增量消息,并添加到大消息list中
        if (null == PamirsSession.getUserId() || null == new PamirsUser().setId(PamirsSession.getUserId()).queryById()){
//            throw PamirsException.construct(pro.shushi.pamirs.user.api.enmu.ExpEnumerate.USER_NOT_LOGIN_ERROR).appendMsg("请重新登录").errThrow();
            log.warn("没有用户信息。可能是用户退出登录 导致 ");
            return pollingTransient;
        }
        String[] channelIdList = null;
        String channels = pollingTransient.getChannelIds();
        List<PamirsMessage> channelMessageList = new ArrayList<>();
        if (StringUtils.isNotBlank(channels)){
            for (String channelId : channels.split(",")) {
                channelMessageList.addAll(fetchChannelMessages(Long.valueOf(channelId), pollingTransient.getLastMessageId()));
            }
        }
        //获取模型消息
        Long activeId = pollingTransient.getActiveId();
        String activeModel = pollingTransient.getActiveModel();
        List<PamirsMessage> modelMessageList = new ArrayList<>();
        if (activeId!=null && activeModel!=null){
            modelMessageList.addAll(fetchModelMessages(activeModel,activeId));
        }
        //获取未读消息数
        Long countUnRead = fetchUnreadCount();

        //设置此时lastId
        Long fetchLastId = fetchLastId();

        pollingTransient.setChannelMessages(channelMessageList);
        pollingTransient.setModelMessages(modelMessageList);
        pollingTransient.setCount(countUnRead);
        pollingTransient.setLastMessageId(fetchLastId);

        //返回消息增量临时模型
        return pollingTransient;
    }

    //获取频道消息
    private List<PamirsMessage> fetchChannelMessages(Long channelId, Long lastMessageId) {
        try {

            Long userId = PamirsSession.getUserId();
            //频道消息查询
            ModelFieldConfig fieldConfig = PamirsSession.getContext().getModelField(PamirsMessage.MODEL_MODEL, "channels");
            List<Map<String,Object>> list = Models.data().queryListByWrapper(Pops.<Map<String,Object>>query()
                    .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(fieldConfig.getThroughReferenceFields().get(0)),channelId)
                    .ge(CaseFormatUtil.lowerCamelToLowerUnderscore(fieldConfig.getThroughRelationFields().get(0)),lastMessageId)
                    .setModel(fieldConfig.getThrough()));
            if (CollectionUtils.isNotEmpty(list)) {
                List<Long> messageIdList = new ArrayList<>();
                list.forEach(v -> messageIdList.add((Long) v.get(fieldConfig.getThroughRelationFields().get(0))));
                List<PamirsMessage> messages = new PamirsMessage().queryList(Pops.<PamirsMessage>lambdaQuery().in(PamirsMessage::getId, messageIdList));
                //为消息设置头像
                if (CollectionUtils.isNotEmpty(messages)) {
                    for (PamirsMessage message : messages) {
                        //获取消息发送者
                        PamirsUser sendPartner = message.getSendPartner();
                        if (null != sendPartner) {
                            sendPartner = new PamirsUser().setId(sendPartner.getId()).queryById();
                            //设置消息显示位置
                            if (sendPartner.getId().equals(userId)) {
                                message.setMailMaster(MessageMasterEnum.SELF);
                            } else {
                                message.setMailMaster(MessageMasterEnum.OTHER);
                            }
                            //先取大图，没有再拿中图
                            //设置消息发送者头像
                            message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHATURL.getValue()).getValue());
                            message.setSendPartner(sendPartner);
                        }
                        //为这个消息设置频道
                        List<MessageChannel> channels = new ArrayList<>();
                        MessageChannel channel = new MessageChannel();
                        channel.setId(channelId);
                        channels.add(channel);
                        message.setChannels(channels);
                    }
                    return messages;
                }
            }

        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
        return new ArrayList<PamirsMessage>();
    }


    //获取模型消息
    private List<PamirsMessage> fetchModelMessages(String activeModel, Long activeId) {
        try {
            List<PamirsMessage> modelMessages = new PamirsMessage().setResModel(activeModel).setResId(activeId).queryList();
            if (CollectionUtils.isNotEmpty(modelMessages)){
                for (PamirsMessage modelMessage : modelMessages) {
                    if (modelMessage.getIconUrl()==null){
                        modelMessage.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.MODELMAILURL.getValue()).getValue());
                    }
                }
                return modelMessages;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw PamirsException.construct(MessageExpEnumerate.SYSTEM_ERROR, e).errThrow();
        }
    }

    //获取未读消息数
    public Long fetchUnreadCount() {
        return new UnreadMessage().setReadDone(false).setUser((PamirsUser)new PamirsUser().setId(PamirsSession.getUserId())).count();
    }

    private Long fetchLastId(){
        Long maxId = 0L;
        List<PamirsMessage> list = new PamirsMessage().queryList(Pops.<PamirsMessage>query()
                .select("MAX(`id`) `maxId`")
                .setModel(PamirsMessage.MODEL_MODEL));
        if (CollectionUtils.isNotEmpty(list)){
            PamirsMessage message = list.get(0);
            if (null != message){
                Map<String, Object> d = message.get_d();
                maxId = (Long) d.get("maxId");
            }
        }
        return maxId;
    }
}
