package pro.shushi.pamirs.framework.configure.annotation.core;

import pro.shushi.pamirs.meta.api.core.compute.Prioritized;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.ExecuteContext;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_DATA_SUBMIT_ERROR;
import static pro.shushi.pamirs.framework.configure.annotation.emnu.AnnotationExpEnumerate.BASE_DATA_SUBMIT_EXECUTE_ERROR;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/15 6:03 下午
 */
@SuppressWarnings({"rawtypes", "unused"})
public class SubmitComputer {

    @SuppressWarnings({"rawtypes", "unused", "unchecked"})
    public static <T extends Prioritized> Result<Void> submit(List<T> computers, Submitter<T> submitter) {
        Result result = new Result<>();
        ExecuteContext context = new ExecuteContext();
        int priority = -1;
        List<List<Integer>> barrierIndexList = new ArrayList<>();
        List<Integer> currentIndex = new ArrayList<>();
        int index = 0;
        for (T computer : computers) {
            int currentPriority = computer.priority();
            if (currentPriority != priority) {
                currentIndex = new ArrayList<>();
                barrierIndexList.add(currentIndex);
            }
            currentIndex.add(index);
            index++;
            priority = currentPriority;
        }
        CompletionService<Result> completionService;
        for (List<Integer> barrierIndex : barrierIndexList) {
            if (barrierIndex.size() > 1) {
                ExecutorService computeExecutor = Executors.newFixedThreadPool(9);
                completionService = new ExecutorCompletionService<>(computeExecutor);
                List<Future> futureList = new ArrayList<>();
                for (Integer pIndex : barrierIndex) {
                    Future<Result> future = completionService.submit(() -> submitter.compute(context, computers.get(pIndex)));
                    futureList.add(future);
                }
                for (Future future : futureList) {
                    try {
                        Result submitResult = completionService.take().get();
                        result.setData(submitResult.getData());
                        result.fill(submitResult);
                        if (!submitResult.isSuccess()) {
                            result.error();
                        }
                        if (context.isBroken()) {
                            computeExecutor.shutdownNow();
                            break;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        throw PamirsException.construct(BASE_DATA_SUBMIT_ERROR, e).errThrow();
                    }
                }
            } else {
                Result submitResult = submitter.compute(context, computers.get(barrierIndex.get(0)));
                result.setData(submitResult.getData());
                result.fill(submitResult);
                if (!submitResult.isSuccess()) {
                    result.error();
                }
                if (context.isBroken()) {
                    break;
                }
            }
        }
        return result;
    }

    public static <D, T> Result<Void> execute(Collection<D> dataList, Executor<D> executor) {
        ExecutorService computeExecutor = Executors.newFixedThreadPool(9);
        CompletionService<Result<Void>> completionService = new ExecutorCompletionService<>(computeExecutor);
        List<Future> futureList = new ArrayList<>();
        for (D data : dataList) {
            Future<Result<Void>> future = completionService.submit(() -> executor.compute(data));
            futureList.add(future);
        }
        for (Future future : futureList) {
            try {
                completionService.take().get();
            } catch (InterruptedException | ExecutionException e) {
                throw PamirsException.construct(BASE_DATA_SUBMIT_EXECUTE_ERROR, e).errThrow();
            }
        }
        return null;
    }

}
