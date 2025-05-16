package pro.shushi.pamirs.message.extpoint;

import pro.shushi.pamirs.message.model.PamirsMessage;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.extpoint.DefaultReadWriteExtPoint;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Ext(PamirsMessage.class)
public class MessageExtPoint extends DefaultReadWriteExtPoint<PamirsMessage> {

    @Override
    @ExtPoint.Implement(displayName = "创建后置扩展点", priority = 999)
    public PamirsMessage createAfter(PamirsMessage data) {
        setCurrentUser(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "创建列表后置扩展点", priority = 999)
    public List<PamirsMessage> createBatchAfter(List<PamirsMessage> data) {
        setCurrentUser(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "更新后置扩展点", priority = 999)
    public PamirsMessage updateAfter(PamirsMessage data) {
        setCurrentUser(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "更新列表回调扩展点", priority = 999)
    public List<PamirsMessage> updateBatchCallback(List<PamirsMessage> data) {
        setCurrentUser(data);
        return data;
    }

    @Override
    @ExtPoint.Implement(displayName = "更新条件回调扩展点", priority = 999)
    public PamirsMessage updateConditionCallback(PamirsMessage data, IWrapper<PamirsMessage> queryWrapper) {
        setCurrentUser(data);
        return data;
    }

    private PamirsMessage setCurrentUser(PamirsMessage data) {
        Optional.ofNullable(data).map(mailMessage -> mailMessage.setCurrentUserName(PamirsSession.getUserName()));
        return data;
    }

    private List<PamirsMessage> setCurrentUser(List<PamirsMessage> datas) {
        return Optional.ofNullable(datas).map(Collection::stream).orElse(Stream.empty()).filter(Objects::nonNull).map(this::setCurrentUser).collect(Collectors.toList());
    }

}
