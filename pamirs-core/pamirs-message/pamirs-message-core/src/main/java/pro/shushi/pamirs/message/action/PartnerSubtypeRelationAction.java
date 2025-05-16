package pro.shushi.pamirs.message.action;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.model.MessageFollower;
import pro.shushi.pamirs.message.tmodel.PartnerSubtypeRelation;
import pro.shushi.pamirs.message.model.MessageSubtype;
import pro.shushi.pamirs.message.utils.CaseFormatUtil;
import pro.shushi.pamirs.message.utils.MessageUtils;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.VariableNameConstants;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Model.model(PartnerSubtypeRelation.MODEL_MODEL)
public class PartnerSubtypeRelationAction {

    // @Action(args = "relation", model = "pro.shushi.pamirs.mail.pro.shushi.pamirs.user.core.model.MailParnterSubtypeRelationTransient", isExported = true)
    @Action
    public PartnerSubtypeRelation assignState(PartnerSubtypeRelation relation) {
        String activeModel = relation.getActiveModel();
        Long activeId = relation.getActiveId();

        List<MessageFollower> messageFollowers = MessageUtils.getMailFollower4LoginUser(true, activeModel, activeId);
        relation.setResModel(StringUtils.isNotBlank(relation.getResModel()) ? relation.getResModel() : activeModel);
        relation.setResId(relation.getResId() != null ? relation.getResId() : activeId);
        Long loginId = PamirsSession.getUserId();

        if (messageFollowers.size() < 1 && relation.getSelectState()) {
            makeFollower(loginId,activeId,activeModel);
            return relation;
        }
        MessageFollower follower = messageFollowers.get(0);

        // 先查中间表，找到id再去查 sub表
        ModelFieldConfig subtypes = PamirsSession.getContext().getModelField(MessageFollower.MODEL_MODEL, "subtypes");
        String through = subtypes.getThrough();
        String leftName = subtypes.getThroughRelationFields().get(0);
        String rightName = subtypes.getThroughReferenceFields().get(0);
        List<Map<String, Object>> all = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(leftName), follower.getId())
                .setModel(through));
        if (CollectionUtils.isEmpty(all)){
            makeFollower(loginId,activeId,activeModel);
            return relation;
        }
        Map<String, Object> m2mQuery = new HashMap<>();
        m2mQuery.put(leftName, follower.getId());
        m2mQuery.put(rightName, all.get(0).get(rightName));
        List<Map<String, Object>> followerSubtypeMapList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(leftName), follower.getId())
                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(rightName), all.get(0).get(rightName))
                .setModel(through));

        if (CollectionUtils.isNotEmpty(followerSubtypeMapList) && !relation.getSelectState()) {
            followerSubtypeMapList.forEach(stringObjectMap -> stringObjectMap.put(VariableNameConstants.entityModel,through));
            Models.data().deleteByPks(followerSubtypeMapList);
            follower.deleteById();
        }else if (CollectionUtils.isEmpty(followerSubtypeMapList) && relation.getSelectState()) {
            m2mQuery.put(VariableNameConstants.entityModel,through);
            Models.data().createOne(m2mQuery);
        }
        return relation;


    }


    //todo subtype没传 如果中间表为空或者follower为空，新建
    private void makeFollower(Long loginId,Long activeId,String activeModel){
        MessageFollower messageFollower = new MessageFollower();
        PamirsUser resourcePartner = new PamirsUser();
        resourcePartner.setId(loginId);
        messageFollower.setPartner(resourcePartner);
        messageFollower.setResId(activeId);
        messageFollower.setResModel(activeModel);
        MessageSubtype messageSubtype = new MessageSubtype();
        //todo subtype暂时没用着,先设置个name
        messageSubtype.setName("hello");
        ArrayList<MessageSubtype> subtypes = new ArrayList<>();
        subtypes.add(messageSubtype);
        messageFollower.setSubtypes(subtypes);
        messageFollower.create();
    }
}
