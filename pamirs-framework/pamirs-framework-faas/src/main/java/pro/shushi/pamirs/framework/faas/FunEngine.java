package pro.shushi.pamirs.framework.faas;

import com.google.common.collect.Sets;
import pro.shushi.pamirs.framework.common.api.SceneAnalysisDebugTraceApi;
import pro.shushi.pamirs.framework.faas.debug.FunChainDebugTrace;
import pro.shushi.pamirs.framework.faas.debug.FunExceptionDebugTrace;
import pro.shushi.pamirs.framework.faas.debug.MainFunDebugTrace;
import pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.core.faas.computer.FilterContext;
import pro.shushi.pamirs.meta.api.core.faas.computer.FunctionComputer;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.enmu.ScriptType;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.util.ClassUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.*;

import static pro.shushi.pamirs.framework.faas.enmu.FaasExpEnumerate.BASE_FUNCTION_INSTANCE_ERROR;

/**
 * 函数调用引擎
 *
 * @author d
 * @version 2019-04-26
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
public class FunEngine<R> {

    private static final List<FunctionComputer> computers;

    static {
        computers = new ArrayList<>();
        Set<Class<?>> classes = ClassUtils.getClasses(FunEngine.class.getPackage().getName());
        for (Class clazz : classes) {
            if (!clazz.isInterface() && FunctionComputer.class.isAssignableFrom(clazz)) {
                try {
                    computers.add((FunctionComputer) clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw PamirsException.construct(BASE_FUNCTION_INSTANCE_ERROR, e).errThrow();
                }
            }
        }
        computers.sort(Comparator.comparingInt(v -> ((Prioritized) v).priority()));
    }

    private Set<ScriptType> includeComputers;

    private Set<ScriptType> excludeComputers;

    public R run(Function function, Object... args) {
        if (null == function.getArguments()) {
            function.setArguments(new ArrayList<>());
        }
        FunctionComputer computer = null;
        FunctionComputer unHitLast = null;
        FilterContext filterContext = new FilterContext().setExcludeTypes(excludeComputers);
        for (FunctionComputer iterator : computers) {
            if (null == excludeComputers || !excludeComputers.contains(iterator.type())) {
                unHitLast = iterator;
            }
            if (null != includeComputers && !includeComputers.contains(iterator.type())
                    || null != excludeComputers && excludeComputers.contains(iterator.type())) {
                continue;
            }
            if (null != filterContext.getHintType() && !iterator.type().equals(filterContext.getHintType())) {
                continue;
            }
            if (iterator.filter(filterContext, function)) {
                computer = iterator;
                break;
            }
        }
        if (null == computer) {
            computer = unHitLast;
        }
        if (null == computer) {
            log.error("There is no adaptive caller for {}", JsonUtils.toJSONString(function));
            throw PamirsException.construct(FaasExpEnumerate.SYSTEM_ERROR).errThrow();
        }

        return run(computer, function, args);
    }

    private R run(FunctionComputer computer, Function function, Object[] args) {
        R result = null;
        if (!SceneAnalysisDebugTraceApi.isDebug()) {
            return (R) computer.compute(function, args);
        }
        //增加追踪信息
        long start = System.currentTimeMillis();
        int anchorIndex = FunChainDebugTrace.anchor();
        try {
            FunChainDebugTrace.push();
            result = (R) computer.compute(function, args);
        } catch (Throwable e) {
            FunExceptionDebugTrace.debug(function, computer, null, args);
            throw e;
        } finally {
            FunChainDebugTrace.debug(function, computer, start, anchorIndex);
            MainFunDebugTrace.debug(function, computer, result, args);
            FunChainDebugTrace.pop();
        }
        return result;
    }

    public static <R> FunEngine<R> get() {
        return new FunEngine<>();
    }

    public static <R> FunEngine<R> get(ScriptType scriptType) {
        return (FunEngine<R>) get().include(Sets.newHashSet(scriptType));
    }

    @SuppressWarnings("unused")
    public FunEngine<R> exclude(Set<ScriptType> excludeComputers) {
        this.excludeComputers = excludeComputers;
        return this;
    }

    public FunEngine<R> exclude(ScriptType excludeComputer) {
        if (null == this.excludeComputers) {
            this.excludeComputers = new HashSet<>();
        }
        this.excludeComputers.add(excludeComputer);
        return this;
    }

    public FunEngine<R> include(Set<ScriptType> includeComputers) {
        this.includeComputers = includeComputers;
        return this;
    }

    @SuppressWarnings("unused")
    public FunEngine<R> include(ScriptType includeComputer) {
        if (null == this.includeComputers) {
            this.includeComputers = new HashSet<>();
        }
        this.includeComputers.add(includeComputer);
        return this;
    }

}
