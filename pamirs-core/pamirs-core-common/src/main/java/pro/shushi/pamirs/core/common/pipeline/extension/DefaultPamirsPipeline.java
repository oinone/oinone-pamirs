package pro.shushi.pamirs.core.common.pipeline.extension;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.ObjectHelper;
import pro.shushi.pamirs.core.common.directive.DirectiveHelper;
import pro.shushi.pamirs.core.common.pipeline.*;
import pro.shushi.pamirs.core.common.pipeline.constant.PamirsPipelineConstant;
import pro.shushi.pamirs.core.common.signature.extension.AbstractPamirsSignature;

import java.util.*;

/**
 * <h>默认管道</h>
 * <p>
 * 1、顺序执行所有加入到管道中的阀以及其他管道<br/>
 * 2、执行时，根据类型的不同按照如下类型顺序进行优先判断：<br/>
 * - 过滤阀 {@link PamirsFilterValve}<br/>
 * - 选择阀 {@link PamirsChooseValve}<br/>
 * - 循环阀 {@link PamirsLoopValve}<br/>
 * - 普通阀 {@link PamirsValve}<br/>
 * 具体执行顺序可重写一下方法进行自定义<br/>
 * {@link DefaultPamirsPipeline#invokeValveByType(PamirsValve, PamirsExchange, int)}<br/>
 * 3、执行时提供的中断逻辑判断<br/>
 * - 根据交换对象中的中断标记进行判断，与是否存在异常无关 {@link DefaultPamirsPipeline#isInterrupt(PamirsExchange)}<br/>
 * - 当异常未处理时，将进行强制中断 {@link DefaultPamirsPipeline#invokeValve(PamirsValve, PamirsExchange)}<br/>
 * 4、可以使用{@link PamirsPipeline.Feature}为管道添加特性
 * </p>
 *
 * @param <T> 任意交换对象类型
 * @author Adamancy Zhang on 2021-04-26 14:33
 */
public class DefaultPamirsPipeline<T extends PamirsExchange> extends AbstractPamirsSignature implements PamirsPipeline<T> {

    private final List<PamirsValve<T>> valves;

    private final int directive;

    private final Set<String> valveSignatureSet;

    public DefaultPamirsPipeline(Feature... features) {
        this(null, features);
    }

    public DefaultPamirsPipeline(String signature, Feature... features) {
        super(signature);
        this.valves = new ArrayList<>();
        this.directive = DirectiveHelper.enable(features);
        if (DirectiveHelper.isEnabled(directive, Feature.NON_REPEAT_VALVE)) {
            this.valveSignatureSet = new HashSet<>();
        } else {
            this.valveSignatureSet = null;
        }
    }

    @Override
    public void addValve(PamirsValve<T> valve) {
        if (verificationValve(valve)) {
            this.valves.add(valve);
        }
    }

    @Override
    public void include(PamirsPipeline<T> pipeline) {
        if (verificationPipeline(pipeline)) {
            this.valves.add(pipeline);
        }
    }

    @Override
    public void sort(Comparator<PamirsValve<T>> comparable) {
        this.valves.sort(comparable);
    }

    @Override
    public List<PamirsValve<T>> getValves() {
        return Collections.unmodifiableList(valves);
    }

    @Override
    public List<PamirsValve<T>> getAllValves() {
        List<PamirsValve<T>> valves = new ArrayList<>();
        for (PamirsValve<T> valve : this.valves) {
            if (valve instanceof PamirsPipeline) {
                valves.addAll(((PamirsPipeline<T>) valve).getAllValves());
            } else {
                valves.add(valve);
            }
        }
        return Collections.unmodifiableList(valves);
    }

    @Override
    public T invoke(T exchange) {
        exchange = prepareExchange(exchange);

        Collection<PamirsValve<T>> invokeValves = prepareInvokeValve();

        exchange = invoke(invokeValves, exchange);

        return exchange;
    }

    protected T prepareExchange(T exchange) {
        if (exchange == null) {
            throw new IllegalArgumentException("Invalid exchange.");
        }
        return exchange;
    }

    protected Collection<PamirsValve<T>> prepareInvokeValve() {
        if (DirectiveHelper.isEnabled(directive, Feature.SERIAL)) {
            return getAllValves();
        } else {
            return getValves();
        }
    }

    protected T invoke(Collection<PamirsValve<T>> valves, T exchange) {
        for (PamirsValve<T> valve : valves) {
            exchange = invokeValveByType(valve, exchange, 0);
            if (isInterrupt(exchange)) {
                return exchange;
            }
        }
        return exchange;
    }

    protected boolean isInterrupt(T exchange) {
        return exchange != null && exchange.isInterrupted();
    }

    protected T invokeValveByType(PamirsValve<T> valve, T exchange, int chooseNumber) {
        if (valve instanceof PamirsFilterValve) {
            return invokeFilterValve((PamirsFilterValve<T>) valve, exchange);
        } else if (valve instanceof PamirsChooseValve) {
            return invokeChooseValve((PamirsChooseValve<T>) valve, exchange, chooseNumber);
        } else if (valve instanceof PamirsLoopValve) {
            return invokeLoopValve((PamirsLoopValve<T>) valve, exchange);
        } else {
            return invokeValve(valve, exchange);
        }
    }

    protected T invokeFilterValve(PamirsFilterValve<T> valve, T exchange) {
        if (valve.filter(exchange)) {
            exchange = invokeValve(valve, exchange);
        }
        return exchange;
    }

    protected T invokeChooseValve(PamirsChooseValve<T> valve, T exchange, int chooseNumber) {
        if (chooseNumber >= PamirsPipelineConstant.MAX_CHOOSE_NUMBER) {
            exchange.setThrowable(new IllegalArgumentException("The choose valve has not been executed for more than " + PamirsPipelineConstant.MAX_CHOOSE_NUMBER + " times. Force Interrupted."));
            exchange.interrupt();
            return exchange;
        }
        PamirsValve<T> chosenValve = valve.choose(exchange);
        if (chosenValve == null) {
            return exchange;
        }
        if (valve.equals(chosenValve)) {
            exchange = invokeValve(valve, exchange);
        } else {
            exchange = invokeValveByType(chosenValve, exchange, chooseNumber + 1);
        }
        return exchange;
    }

    protected T invokeLoopValve(PamirsLoopValve<T> valve, T exchange) {
        while (valve.loop(exchange)) {
            exchange = invokeValve(valve, exchange);
            if (isInterrupt(exchange)) {
                return exchange;
            }
        }
        return exchange;
    }

    protected T invokeValve(PamirsValve<T> valve, T exchange) {
        try {
            exchange = valve.invoke(exchange);
        } catch (Throwable e) {
            exchange.setThrowable(e);
            exchange.interrupt();
        }
        return exchange;
    }

    protected boolean verificationValve(PamirsValve<T> valve) {
        if (valve == null) {
            throw new IllegalArgumentException("Invalid valve.");
        }
        if (valve instanceof PamirsPipeline) {
            throw new IllegalArgumentException("Please use include method add pipeline.");
        }
        if (DirectiveHelper.isEnabled(directive, Feature.NON_REPEAT_VALVE)) {
            String signature = valve.signature();
            if (StringUtils.isBlank(signature)) {
                throw new IllegalArgumentException("Invalid valve signature.");
            }
            if (ObjectHelper.isRepeat(valveSignatureSet, signature)) {
                throw new IllegalArgumentException("Valve signature is repeat. signature=" + signature);
            }
        }
        return true;
    }

    protected boolean verificationPipeline(PamirsPipeline<T> pipeline) {
        if (pipeline == null) {
            throw new IllegalArgumentException("Invalid pipeline.");
        }
        if (DirectiveHelper.isEnabled(directive, Feature.NON_REPEAT_VALVE)) {
            String signature = pipeline.signature();
            if (StringUtils.isBlank(signature)) {
                throw new IllegalArgumentException("Invalid pipeline signature.");
            }
            if (ObjectHelper.isRepeat(valveSignatureSet, signature)) {
                throw new IllegalArgumentException("Pipeline signature is repeat. signature=" + signature);
            }
        }
        return true;
    }
}
