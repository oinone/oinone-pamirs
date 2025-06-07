package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.WrapperHelper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.LambdaQueryWrapper;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.enmu.MessageTypeEnum;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.message.model.UnreadMessage;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.manager.data.OriginDataManager;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.util.ListUtils;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.constant.SqlConstants;
import pro.shushi.pamirs.meta.enmu.ActionContextTypeEnum;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;
import static pro.shushi.pamirs.user.api.constants.UserConstants.ADMIN_USER_ID;

@Slf4j
@Base
@Model.model(UnreadMessage.MODEL_MODEL)
public class UnreadMessageAction {

    public static final Integer LIMIT_99 = 99;

    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    public static final String DOT_DOT_DOT = "...";

    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    @Function.fun(FunctionConstants.queryPage)
    @Function(openLevel = {FunctionOpenEnum.API})
    public Pagination<UnreadMessage> queryPage(Pagination<UnreadMessage> page, IWrapper<UnreadMessage> queryWrapper) {
        LambdaQueryWrapper<UnreadMessage> wrapper = WrapperHelper.lambda(queryWrapper);
        Long userId = PamirsSession.getUserId();
        if (!ADMIN_USER_ID.equals(userId)) {
            wrapper.eq(UnreadMessage::getUserId, userId);
        }
        UnreadMessage query = new UnreadMessage();
        Pagination<UnreadMessage> pagination = query.queryPage(page, wrapper);
        List<UnreadMessage> unreadMessages = pagination.getContent();
        if (CollectionUtils.isEmpty(unreadMessages)) {
            return pagination;
        }
        query.listFieldQuery(unreadMessages, UnreadMessage::getMessage);
        unreadMessages.forEach(this::unreadMessageConsumer);

        return pagination;
    }

    private void unreadMessageConsumer(UnreadMessage unreadMessage) {

        if (unreadMessage == null) {
            return;
        }
        PamirsMessage message = unreadMessage.getMessage();
        if (message == null || StringUtils.isBlank(message.getBody())) {
            return;
        }

        String originBody = message.getBody();
        String messageSummary = originBody.replaceAll(RE_HTML_MARK, "");
        if (StringUtils.length(messageSummary) > 32) {
            messageSummary = messageSummary.substring(0, 32).concat(DOT_DOT_DOT);
        }
        if (StringUtils.isBlank(message.getSubject())) {
            message.setSubject(StringUtils.isNotBlank(message.getName()) ? message.getName() : messageSummary);
        }
        unreadMessage.setMessageSummary(messageSummary);
        unreadMessage.setSubject(message.getSubject());
    }

    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
    @Function.fun(FunctionConstants.queryByEntity)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public UnreadMessage queryOne(UnreadMessage query) {
        UnreadMessage unreadMessage = query.queryOne();
        if (null == unreadMessage) {

        }

        query.fieldQuery(UnreadMessage::getMessage);
        unreadMessageConsumer(unreadMessage);
        if (MessageTypeEnum.NOTIFICATION.equals(unreadMessage.getMessageType())) {
            this.readWorkflowMessage(unreadMessage);
        }
        return unreadMessage;
    }

    @Action(displayName = "已读", bindingType = ViewTypeEnum.TABLE, contextType = ActionContextTypeEnum.SINGLE)
    public UnreadMessage readBatch(UnreadMessage needAction) {
        LambdaQueryWrapper<UnreadMessage> wrapper = Pops.<UnreadMessage>lambdaQuery()
                .from(UnreadMessage.MODEL_MODEL)
                .eq(UnreadMessage::getId, needAction.getId())
                .eq(UnreadMessage::getReadDone, false);

        needAction = needAction.queryOneByWrapper(wrapper);
        if (needAction == null) {
            //id不存在/指定id的消息不是未读状态
            return null;
        }
        needAction.setReadDone(true);
        needAction.createOrUpdate();
        return needAction;
    }

    private List<Long> fetchListActivePks(String activePksString, String msg) {
        List<Long> activeIds = new ArrayList<>();
        List<Map<String, Object>> activePks = JsonUtils.parseMapList(activePksString);
        activePks.forEach(t -> activeIds.add(Long.parseLong(t.get(SqlConstants.ID).toString())));
        Optional.ofNullable(activeIds).filter(CollectionUtils::isNotEmpty)
                .orElseThrow(() -> PamirsException.construct(MessageExpEnumerate.BIZ_ERROR, msg).errThrow());
        return activeIds;
    }


    @Function.Advanced(displayName = "查询未读消息列表", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, LOCAL, REMOTE})
    public List<UnreadMessage> unreadWorkflowList(UnreadMessage data) {
        OriginDataManager dataManager = Models.origin();
        LambdaQueryWrapper<UnreadMessage> wrapper = Pops.<UnreadMessage>lambdaQuery()
                .from(UnreadMessage.MODEL_MODEL)
                .eq(UnreadMessage::getUserId, PamirsSession.getUserId())
                .in(UnreadMessage::getMessageType, MessageTypeEnum.WORKFLOW, MessageTypeEnum.NOTIFICATION, MessageTypeEnum.COPY, MessageTypeEnum.SHARING)
                .eq(UnreadMessage::getReadDone, false);
        List<UnreadMessage> list = dataManager.queryListByWrapper(new Pagination<UnreadMessage>().setCurrentPage(1).setSize(100L), wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > LIMIT_99) {
                list = ListUtils.sub(list, 0, LIMIT_99);
            }
            List<Long> ids = list.stream().map(UnreadMessage::getMessageId).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                List<PamirsMessage> messages = dataManager.queryListByWrapper(Pops.<PamirsMessage>lambdaQuery()
                        .from(PamirsMessage.MODEL_MODEL).setBatchSize(-1)
                        .in(PamirsMessage::getId, ids));
                for (UnreadMessage unreadMessage : list) {
                    Long mid = Optional.ofNullable(unreadMessage.getMessageId()).orElse(0L);
                    unreadMessage.setMessage(messages.stream().filter(t -> t.getId().equals(mid)).findFirst().orElse(null));
                }
            }
        }
        return list;
    }

    @Function.Advanced(displayName = "查询未读消息数", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, LOCAL, REMOTE})
    public Long unreadCount(UnreadMessage data) {
        OriginDataManager dataManager = Models.origin();
        LambdaQueryWrapper<UnreadMessage> wrapper = Pops.<UnreadMessage>lambdaQuery()
                .from(UnreadMessage.MODEL_MODEL)
                .eq(UnreadMessage::getUserId, PamirsSession.getUserId())
                .in(UnreadMessage::getMessageType, MessageTypeEnum.WORKFLOW, MessageTypeEnum.NOTIFICATION, MessageTypeEnum.COPY, MessageTypeEnum.SHARING)
                .eq(UnreadMessage::getReadDone, false);
        return dataManager.count(wrapper);
    }

    @Function.Advanced(displayName = "查询工作流消息", type = FunctionTypeEnum.QUERY)
    @Function(openLevel = {FunctionOpenEnum.API, LOCAL, REMOTE})
    public UnreadMessage readWorkflowMessage(UnreadMessage data) {
        if (data != null && data.getId() != null) {
            UnreadMessage needAction = new UnreadMessage();
            needAction.setId(data.getId());
            needAction.setReadDone(Boolean.TRUE);
            needAction.updateById();
        }
        return data;
    }
}
