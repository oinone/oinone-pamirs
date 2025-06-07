package pro.shushi.pamirs.message.tmodel;

import pro.shushi.pamirs.message.model.MessageFollower;
import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.base.TransientModel;

import java.util.List;

@Slf4j
@Model(displayName = "模型消息线程")
@Model.model(MessageThread.MODEL_MODEL)
public class MessageThread extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.message.MessageThread";

    // todo defaultValue = "fetchFollowingState()"
    @Field.Boolean
    @Field(displayName = "关注状态")
    private Boolean followingState;

    // todo defaultValue = "fetchFollowerCount()"
    @Field.Integer
    @Field(displayName = "模型的关注partner和channel数")
    private Long followerCount;

    //todo zl compute字段
    @Field.Integer
    @Field(displayName = "消息最大ID")
    private Long maxMessageId;

    // todo defaultValue = "fetchMessages(maxMessageId)"
    @Field.one2many
    @Field(displayName = "模型的消息列表")
    private List<PamirsMessage> messages;

    // todo defaultValue = "fetchMailFollowers()"
    @Field.one2many
    @Field(displayName = "模型的关注者")
    private List<MessageFollower> messageFollowers;

    // todo defaultValue = "fetchPartnerSubtypeRel()"
    @Field.one2many
    @Field(displayName = "关注的消息类型")
    private List<PartnerSubtypeRelation> partnerSubtypeRel;


    @Field.String
    @Field(displayName = "activeModel")
    private String activeModel;

    @Field.Integer
    @Field(displayName = "activeId")
    private Long activeId;


//    @Function
//    public Boolean fetchFollowingState() {
//        /**
//         * 先找到当前登录用户所属的mail follower有哪些
//         */
//        List<MailFollower> followerList = MailUtils.getMailFollower4LoginUser(true);
//        if (CollectionUtils.isEmpty(followerList)) {
//            return false;
//        }
//
//        /**
//         * 到mail follower和mail subtype关联表，count是否有关注
//         */
//        ModelConfig model = PamirsSession.getContext().getModelConfig(MailFollower.class.getName());
//        pro.shushi.pamirs.base.model.meta.ModelField subtypeField = ModelFieldUtils.findModelField("subtypes", model);
//        JdbcRepository m2mRepo = JdbcRepositoryFactory.getRelationRepository(subtypeField);
//        List<Long> followerIds = new ArrayList<>();
//        for (MailFollower follower : followerList) {
//            followerIds.add(follower.getId());
//        }
//        Map<String, Object> m2mQuery = new HashMap<>();
//        m2mQuery.put(subtypeField.getColumn1(), followerIds);
//        try {
//            long count = m2mRepo.count(m2mQuery);
//            return count > 0L ? true : false;
//        } catch (SQLException e) {
//            throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//        }
//    }
//
//    @Function
//    public Long fetchFollowerCount() {
//        /**
//         * 获取可关注当前模型的所有关注者和频道
//         */
//        Map<String, Object> queryMap = new HashMap<>();
//        queryMap.put("resModel", PamirsEnvironment.getThreadLocal().getActiveModel());
//        queryMap.put("resId", PamirsEnvironment.getThreadLocal().getActiveId());
//
//        JdbcRepository repo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailFollower.class.getName(), DataOpTypeEnum.READ);
//        try {
//            long count = repo.count(queryMap);
//            return count;
//        } catch (SQLException e) {
//            throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//        }
//    }
//
//    @Function
//    public List<MailMessage> fetchMessages(Long maxMessageId) {
//        ResourceUser use = MailUtils.getLoginUser();
//        try {
//            /**
//             * 将need action中，所有未读消息删除
//             */
//            JdbcRepository needActionReadRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailMessagePartnerNeedAction.class.getName(), DataOpTypeEnum.WRITE);
//            Map<String, Object> needActionQuery = new HashMap<>();
//            needActionQuery.put("resModel", PamirsEnvironment.getThreadLocal().getActiveModel());
//            needActionQuery.put("resId", PamirsEnvironment.getThreadLocal().getActiveId());
//            needActionQuery.put("partnerId", use.getId());
//            List<Map<String, Object>> needActionDeleteList = new ArrayList<>();
//            needActionDeleteList.add(needActionQuery);
//            needActionReadRepo.deleteInBatch(needActionDeleteList);
//            String where = " WHERE res_model=? and res_id=? and id>?";
//            List<Object> paramList = new ArrayList<>();
//            paramList.add(PamirsEnvironment.getThreadLocal().getActiveModel());
//            paramList.add(PamirsEnvironment.getThreadLocal().getActiveId());
//            paramList.add(maxMessageId != null ? maxMessageId : 0L);
//            JdbcRepository repository = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailMessage.class.getName(), DataOpTypeEnum.READ);
//            List<Map<String, Object>> mapList = repository.findAll(where, paramList);
//            if (CollectionUtils.isEmpty(mapList)) {
//                return new ArrayList<>();
//            }
//            List<MailMessage> messages = ObjectMapUtils.mapToModel(mapList, MailMessage.class.getName());
//            if (CollectionUtils.isNotEmpty(messages)) {
//                for (MailMessage message : messages) {
//                    //获取消息发送者
//                    ResourcePartner sendPartner = message.getSendPartner();
//                    if (null != sendPartner) {
//                        JdbcRepository partnerRepo1 = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(ResourcePartner.class.getName(), DataOpTypeEnum.READ);
//                        Map<String, Object> partnerMap = partnerRepo1.findOne(sendPartner.getId());
//                        sendPartner = ObjectMapUtils.mapToModel(partnerMap, ResourcePartner.class.getName());
//                        if (use.getId().equals(sendPartner.getId())) {
//                            message.setMailMaster(MailMasterEnum.SELF.getValue());
//                        } else {
//                            message.setMailMaster(MailMasterEnum.OTHER.getValue());
//                        }
//                        ResourceFile avatarFile = sendPartner.getAvatarBig() != null ? sendPartner.getAvatarBig() : sendPartner.getAvatarMedium();
//                        JdbcRepository fileRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(ResourceFile.class.getName(), DataOpTypeEnum.READ);
//                        if (avatarFile != null) {
//                            Map<String, Object> fileMap = fileRepo.findOne(avatarFile.getId());
//                            avatarFile = ObjectMapUtils.mapToModel(fileMap, ResourceFile.class.getName());
//                            sendPartner.setAvatarMedium(avatarFile);
//                            message.setIconUrl(avatarFile.getUrl());
//                        } else {
//                            message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.CHATURL.getValue()).getValue());
//                        }
//                        message.setSendPartner(sendPartner);
//                    } else {
//                        //此时的模型消息为触发发送的
//                        message.setIconUrl(ResourceConfig.fetchConfigValue(DefaultAvatarEnum.MODELMAILURL.getValue()).getValue());
//                        message.setMailMaster(MailMasterEnum.OTHER.getValue());
//                    }
//                }
//            }
//            return messages;
//        } catch (SQLException e) {
//            throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//        } catch (Exception e) {
//            throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR, e).errThrow();
//        }
//    }
//
//    @Function
//    public List<MailFollower> fetchMailFollowers() {
//        /**
//         * 获取模型的所有follower
//         */
//        Map<String, Object> followerQuery = new HashMap<>();
//        JdbcRepository followerRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailFollower.class.getName(), DataOpTypeEnum.READ);
//        followerQuery.put("resModel", PamirsEnvironment.getThreadLocal().getActiveModel());
//        followerQuery.put("resId", PamirsEnvironment.getThreadLocal().getActiveId());
//        List<MailFollower> followerList;
//        try {
//            List<Map<String, Object>> followerMapList = followerRepo.findAll(followerQuery);
//            if (CollectionUtils.isEmpty(followerMapList)) {
//                return new ArrayList<>();
//            }
//            followerList = ObjectMapUtils.mapToModel(followerMapList, MailFollower.class.getName());
//        } catch (SQLException e) {
//            throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//        } catch (Exception e) {
//            throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR, e).errThrow();
//        }
//        /**
//         * 查询模型的所有follower，对应的关注partner或者channel是谁
//         */
//        List<Long> partnerIds = new ArrayList<>();
//        List<Long> channelIds = new ArrayList<>();
//        for (MailFollower f : followerList) {
//            if (f.getPartner() != null && f.getPartner().getId() != null) {
//                partnerIds.add(f.getPartner().getId());
//            }
//            if (f.getChannel() != null && f.getChannel().getId() != null) {
//                channelIds.add(f.getChannel().getId());
//            }
//        }
//        Map<Long, ResourcePartner> partnerMap = new HashMap<>();
//        Map<Long, MailChannel> channelMap = new HashMap<>();
//        if (CollectionUtils.isNotEmpty(partnerIds)) {
//            Map<String, Object> partnerQuery = new HashMap<>();
//            partnerQuery.put(SqlConstants.ID, partnerIds);
//            JdbcRepository partnerRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(ResourcePartner.class.getName(), DataOpTypeEnum.READ);
//            try {
//                List<Map<String, Object>> partnerMapList = partnerRepo.findAll(partnerQuery);
//                if (CollectionUtils.isNotEmpty(partnerMapList)) {
//                    List<ResourcePartner> partners = ObjectMapUtils.mapToModel(partnerMapList, ResourcePartner.class.getName());
//                    for (ResourcePartner p : partners) {
//                        partnerMap.put(p.getId(), p);
//                    }
//                }
//            } catch (SQLException e) {
//                throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//            } catch (Exception e) {
//                throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR, e).errThrow();
//            }
//        }
//        if (CollectionUtils.isNotEmpty(channelIds)) {
//            Map<String, Object> channelQuery = new HashMap<>();
//            channelQuery.put(SqlConstants.ID, channelIds);
//            JdbcRepository channelRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailChannel.class.getName(), DataOpTypeEnum.READ);
//            try {
//                List<Map<String, Object>> channelMapList = channelRepo.findAll(channelQuery);
//                if (CollectionUtils.isNotEmpty(channelMapList)) {
//                    List<MailChannel> channels = ObjectMapUtils.mapToModel(channelMapList, MailChannel.class.getName());
//                    for (MailChannel mc : channels) {
//                        channelMap.put(mc.getId(), mc);
//                    }
//                }
//            } catch (SQLException e) {
//                throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//            } catch (Exception e) {
//                throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR, e).errThrow();
//            }
//        }
//        /**
//         * 将mail follower中的关系模型字段设值
//         */
//        for (MailFollower f : followerList) {
//            if (f.getPartner() != null && f.getPartner().getId() != null) {
//                ResourcePartner p = partnerMap.get(f.getPartner().getId());
//                if (p != null) {
//                    f.setPartner(p);
//                }
//            }
//            if (f.getChannel() != null && f.getChannel().getId() != null) {
//                MailChannel mc = channelMap.get(f.getChannel().getId());
//                if (mc != null) {
//                    f.setChannel(mc);
//                }
//            }
//        }
//        return followerList;
//    }
//
//    @Function
//    public List<MailParnterSubtypeRelationTransient> fetchPartnerSubtypeRel() {
//        /**
//         * 先拿到模型消息的所有子类型
//         */
//        Map<String, Object> subtypeQuery = new HashMap<>();
//        String subtypeModel = (String) PamirsEnvironment.getThreadLocal().getActiveModel();
//        pro.shushi.pamirs.base.model.meta.Model model = PamirsCacheRead.getModel(subtypeModel);
//        subtypeQuery.put("modelId", model.getId());
//        JdbcRepository subtypeRepo = (JdbcRepository) DataEngine.get(DataEngineTypeEnum.JDBC).get(MailSubtype.class.getName(), DataOpTypeEnum.READ);
//        List<MailSubtype> subtypeList;
//        try {
//            List<Map<String, Object>> subtypeMapList = subtypeRepo.findAll(subtypeQuery);
//            if (CollectionUtils.isEmpty(subtypeMapList)) {
//                return new ArrayList<>();
//            }
//            subtypeList = ObjectMapUtils.mapToModel(subtypeMapList, MailSubtype.class.getName());
//        } catch (SQLException e) {
//            throw PamirsException.construct(ExpEnumerate.MAIL_SQL_ERROR, e).errThrow();
//        } catch (Exception e) {
//            throw PamirsException.construct(ExpEnumerate.SYSTEM_ERROR, e).errThrow();
//        }
//
//        /**
//         * 获取当前登录用户关注的所有模型消息类型
//         */
//        List<Long> myFollowSubtypeIds = MailUtils.getAllMailSubtypeId4LoginUser(true);
//
//        List<MailParnterSubtypeRelationTransient> retList = new ArrayList<>();
//        for (MailSubtype s : subtypeList) {
//            MailParnterSubtypeRelationTransient pss = new MailParnterSubtypeRelationTransient();
//            pss.setPartnerId(MailUtils.getLoginUser().getId());
//            pss.setSubtype(s);
//            pss.setResModel((String) PamirsEnvironment.getThreadLocal().getActiveModel());
//            pss.setResId(ParamUtils.createLong(PamirsEnvironment.getThreadLocal().getActiveId()));
//            if (CollectionUtils.isNotEmpty(myFollowSubtypeIds) && myFollowSubtypeIds.contains(s.getId())) {
//                pss.setSelectState(true);
//            } else {
//                pss.setSelectState(false);
//            }
//            retList.add(pss);
//        }
//        return retList;
//    }

}
