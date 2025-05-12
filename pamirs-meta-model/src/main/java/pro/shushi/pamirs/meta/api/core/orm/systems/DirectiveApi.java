package pro.shushi.pamirs.meta.api.core.orm.systems;

import org.springframework.core.annotation.Order;
import pro.shushi.pamirs.meta.api.core.orm.systems.directive.SystemDirectiveEnum;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.bit.SessionMetaBit;
import pro.shushi.pamirs.meta.common.spi.SPI;

/**
 * 指令接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Order
@SPI
public interface DirectiveApi {

    /**
     * 设置前端调用栈指令
     */
    default <T> T request(DirectiveConsumer<T> consumer) {
        return run(consumer, SystemDirectiveEnum.getInitValue());
    }

    /**
     * 设置前端调用栈指令
     */
    default void requestWithoutResult(DirectiveVoidConsumer consumer) {
        request(() -> {
            consumer.consume();
            return null;
        });
    }

    /**
     * 通过指令枚举设置调用栈指令
     */
    default <T> T run(DirectiveConsumer<T> consumer, SystemDirectiveEnum... directiveEnums) {
        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        if (null == sessionMetaBit) {
            return consumer.consume();
        }
        Long originValue = sessionMetaBit.bitValue();
        try {
            sessionMetaBit.initMetaBit(directiveEnums);
            return consumer.consume();
        } finally {
            sessionMetaBit.initMetaBit(originValue);
        }
    }

    /**
     * 通过指令枚举设置调用栈指令
     */
    default void runWithoutResult(DirectiveVoidConsumer consumer, SystemDirectiveEnum... directiveEnums) {
        run(() -> {
            consumer.consume();
            return null;
        }, directiveEnums);
    }

    /**
     * 通过数字设置调用栈指令
     */
    default <T> T run(DirectiveConsumer<T> consumer, Long directive) {
        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        if (null == sessionMetaBit) {
            return consumer.consume();
        }
        Long originValue = sessionMetaBit.bitValue();
        try {
            sessionMetaBit.initMetaBit(directive);
            return consumer.consume();
        } finally {
            sessionMetaBit.initMetaBit(originValue);
        }
    }

    /**
     * 通过指令枚举设置调用栈指令
     */
    default void runWithoutResult(DirectiveVoidConsumer consumer, Long directive) {
        run(() -> {
            consumer.consume();
            return null;
        }, directive);
    }

    /**
     * 保护调用栈指令
     */
    default <T> T protect(DirectiveConsumer<T> consumer) {
        SessionMetaBit sessionMetaBit = PamirsSession.directive();
        if (null == sessionMetaBit) {
            return consumer.consume();
        }
        Long originValue = sessionMetaBit.bitValue();
        try {
            sessionMetaBit.initMetaBit(originValue);
            return consumer.consume();
        } finally {
            sessionMetaBit.initMetaBit(originValue);
        }
    }

    /**
     * 保护调用栈指令
     */
    default void protectWithoutResult(DirectiveVoidConsumer consumer) {
        protect(() -> {
            consumer.consume();
            return null;
        });
    }

    interface DirectiveConsumer<T> {
        T consume();
    }

    interface DirectiveVoidConsumer {
        void consume();
    }

}
