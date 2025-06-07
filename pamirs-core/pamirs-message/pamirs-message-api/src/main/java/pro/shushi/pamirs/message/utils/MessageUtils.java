package pro.shushi.pamirs.message.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.model.MessageChannel;
import pro.shushi.pamirs.message.model.MessageFollower;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.util.TypeUtils;
import pro.shushi.pamirs.user.api.model.PamirsUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MessageUtils {
//    public static PamirsUser getLoginUser() {
//        Long userId = PamirsSession.getUserId();
//        if (userId == null) {
//            throw PamirsException.construct(pro.shushi.pamirs.user.api.enmu.ExpEnumerate.USER_NOT_LOGIN_ERROR).errThrow();
//        }
//        PamirsUser user = new PamirsUser().queryById(userId);
//        if (user == null) {
//            throw PamirsException.construct(pro.shushi.pamirs.user.api.enmu.ExpEnumerate.USER_NOT_LOGIN_ERROR).errThrow();
//        }
//        return user;
//    }

    public static List<MessageChannel> getChannels() {
        List<Long> channelIds = getChannelIds();
        if (CollectionUtils.isEmpty(channelIds))
            return new ArrayList<>();
        return new MessageChannel().queryList(Pops.<MessageChannel>lambdaQuery().from(MessageChannel.MODEL_MODEL).in(MessageChannel::getId, channelIds));
    }

    public static List<Long> getChannelIds() {
        ModelFieldConfig partners = PamirsSession.getContext().getModelField(MessageChannel.MODEL_MODEL, "partners");
        String through = partners.getThrough();
        List<Map<String, Object>> resultList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query()
                .eq(CaseFormatUtil.lowerCamelToLowerUnderscore(partners.getThroughReferenceFields().get(0)), PamirsSession.getUserId())
                .setModel(through));
        List<Long> idList = new ArrayList<>();
        Optional.ofNullable(resultList).ifPresent(_resultList -> {
            _resultList.forEach(map -> {
                Long id = TypeUtils.createLong(map.get(partners.getThroughRelationFields().get(0)));
                Optional.ofNullable(id).ifPresent(_id -> {
                    idList.add(id);
                });
            });
        });
//            if (CollectionUtils.isNotEmpty(resultList)) {
//                for (Map<String, Object> m : resultList) {
//                    Long id = ParamUtils.createLong(m.get(NamedUtils.clomn2FieldName(partnersField.getColumn1())));
//                    if (id != null) {
//                        idList.add(id);
//                    }
//                }
//            }
        return idList;
    }

    /**
     * 获取当前登录用户关联的partner以及用户所属频道关联的partner
     *
     * @return
     */
//    public static List<Long> getAllPartnerByLoginUser() {
//        ResourceUser user = getLoginUser();
//        List<Long> idList = new ArrayList<>();
//        idList.add(user.getId());
//        idList.addAll(getChannelIds());
//        return idList;
//    }
    public static List<Long> getAllMailSubtypeId4LoginUser(boolean isDirect, String activeModel, Long activeId) {
        List<Long> myFollowSubtypeIds = new ArrayList<>();
        List<MessageFollower> followerList = MessageUtils.getMailFollower4LoginUser(isDirect, activeModel, activeId);
        List<Long> followerIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(followerList)) {
            for (MessageFollower f : followerList) {
                followerIds.add(f.getId());
            }
        }
        ModelFieldConfig subtypes = PamirsSession.getContext().getModelField(Models.api().getModel(MessageFollower.class), "subtypes");
        String through = subtypes.getThrough();
        List<Map<String, Object>> m2mList = Models.data().queryListByWrapper(Pops.<Map<String, Object>>query().setModel(through));
        if (CollectionUtils.isNotEmpty(m2mList)) {
            for (Map<String, Object> m : m2mList) {
                myFollowSubtypeIds.add(TypeUtils.createLong(m.get(subtypes.getReferenceFields().get(0))));
            }
        }
        return myFollowSubtypeIds;
    }

    public static List<MessageFollower> getMailFollower4LoginUser(boolean isDirect, String activeModel, Long activeId) {
        /**
         * step1.1 查找直接关联
         */
        List<MessageFollower> followerList = new MessageFollower().setResModel(activeModel)
                .setResId(activeId)
                .setPartner((PamirsUser) new PamirsUser().setId(PamirsSession.getUserId()))
                .queryList();
        //通过所属channel关联查找

        /**
         * step1.2 通过所属channel关联查找
         */
        if (!isDirect) {
            List<MessageFollower> l = new MessageFollower().queryList(Pops.<MessageFollower>lambdaQuery().eq(MessageFollower::getResModel, activeModel)
                    .eq(MessageFollower::getResId, activeId)
                    .in(MessageFollower::getPartner, MessageUtils.getChannelIds()));
            if (CollectionUtils.isNotEmpty(l)) {
                if (followerList == null) {
                    followerList = l;
                } else {
                    followerList.addAll(l);
                }
            }
        }
        return followerList;
    }

    public static List<Long> fetchPartnerIdsByChannel(Long channelId) {
        MessageChannel one = new MessageChannel().setId(channelId).fieldQuery(MessageChannel::getPartners);
        List<PamirsUser> partners = one.getPartners();
        if (CollectionUtils.isNotEmpty(partners)) {
            List<Long> partnerIds = new ArrayList<>();
            for (PamirsUser partner : partners) {
                if (!partner.getId().equals(PamirsSession.getUserId()))
                    partnerIds.add(partner.getId());
            }
            return partnerIds;
        }
        return null;
    }
}
