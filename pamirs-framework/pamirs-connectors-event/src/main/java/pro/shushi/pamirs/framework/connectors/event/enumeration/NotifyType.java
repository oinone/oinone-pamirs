package pro.shushi.pamirs.framework.connectors.event.enumeration;

public enum NotifyType {

    /**
     * @deprecated 即将移除. 使用 ROCKETMQ替代
     */
    @Deprecated
    ROCKET_MQ,


    ROCKETMQ,
    KAFKA,
    RABBITMQ,
    PULSAR,

    ;
}
