//package pro.shushi.pamirs.framework.gateways.graph.java.request;
//
//import javafx.concurrent.Task;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.async.DeferredResult;
//import pro.shushi.pamirs.framework.gateways.graph.longpolling.DeferredTask;
//import pro.shushi.pamirs.framework.gateways.graph.longpolling.DeferredTaskSet;
//import pro.shushi.pamirs.meta.api.dto.fun.Function;
//import pro.shushi.pamirs.meta.api.session.PamirsSession;
//import pro.shushi.pamirs.meta.common.util.UUIDUtil;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.concurrent.locks.ReadWriteLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//
/// **
// * 长轮询延迟任务实现
// *
// * @author d@shushi.pro
// * @version 1.0.0
// * date 2020/1/17 1:09 上午
// */
//@Component
//public class DeferredExecutor {
//
//    @Resource
//    private DeferredTaskSet taskSet;
//
//    private static ReadWriteLock lock = new ReentrantReadWriteLock();
//
//    public DeferredResult<String> addTask(String model, String fun) {
//
//        Function function = PamirsSession.getContext().getFunction(model, fun);
//
//        //建立DeferredResult对象，设置超时时间，以及超时返回超时结果
//        DeferredResult<String> result = new DeferredResult(function.getTimeout(), null);
//
//        result.onTimeout(() -> {
//            lock.writeLock().lock();
//            try {
//                taskSet.getSet().remove(result);
//            } finally {
//                lock.writeLock().unlock();
//            }
//        });
//
//        result.onCompletion(() -> {
//            lock.writeLock().lock();
//            try {
//                taskSet.getSet().remove(result);
//            } finally {
//                lock.writeLock().unlock();
//            }
//        });
//
//        DeferredTask task = new DeferredTask();
//        task.setFunction(function).setTaskId(UUIDUtil.getUUIDNumberString()).setTaskResult(result);
//        lock.writeLock().lock();
//        try {
//            taskSet.getSet().add(task);
//        } finally {
//            lock.writeLock().unlock();
//        }
//
//        return result;
//    }
//
//    /**
//     * 初始化启动
//     */
//    @PostConstruct
//    public void init() {
//        new Thread(this::execute).start();
//    }
//
//    /**
//     * 持续处理
//     * 返回执行结果
//     */
//    private void execute() {
//
//        while (true) {
//
//            try {
//
//                //取出任务
//                Task task;
//
//                synchronized (taskQueue) {
//
//                    task = taskQueue.take();
//
//                }
//
//                if (task != null) {
//
//                    //设置返回结果
//                    String randomStr = getRandomStr(DEFAULT_STR_LEN);
//
//                    ResponseMsg<String> responseMsg = new ResponseMsg<String>(0, "success", randomStr);
//
//                    log.info("返回结果:{}", responseMsg);
//
//                    task.getTaskResult().setResult(responseMsg);
//                }
//
//                int time = random.nextInt(10);
//
//                log.info("处理间隔：{}秒", time);
//
//                Thread.sleep(time * 1000L);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//}
