package pro.shushi.pamirs.eip.api.circuitbreaker;

/**
 * 熔断-本地滑动窗口计数器
 *
 * @author yeshenyue on 2025/4/15 13:45.
 */
public class RollingWindowCounter {

    // 滑动窗口总长度-秒
    private final int windowSeconds;
    // 每个秒桶的总请求计数
    private final int[] totalCounts;
    // 每个秒桶的失败计数
    private final int[] failCounts;
    // 每个秒桶的慢请求计数
    private final int[] slowCounts;
    // 最后一次记录的时间秒
    private long lastTickSec;
    // 当前秒对应的桶索引
    private int currentIndex;
    private int totalSum;
    private int failSum;
    private int slowSum;

    public RollingWindowCounter(int windowSeconds) {
        this.windowSeconds = windowSeconds;
        this.totalCounts = new int[windowSeconds];
        this.failCounts = new int[windowSeconds];
        this.slowCounts = new int[windowSeconds];
        this.lastTickSec = System.currentTimeMillis() / 1000;
        this.currentIndex = 0;
        this.totalSum = this.failSum = this.slowSum = 0;
    }

    /**
     * 记录一次调用事件到滑动窗口中。
     *
     * @param success 此次调用是否成功
     * @param slow    此次调用是否慢调用
     */
    public synchronized void recordEvent(boolean success, boolean slow) {
        long nowSec = System.currentTimeMillis() / 1000;
        if (nowSec != lastTickSec) {
            // 时间推进，滑动窗口前进
            long diff = nowSec - lastTickSec;
            if (diff >= windowSeconds) {
                // 超过整个窗口大小，直接清空所有桶
                totalSum = failSum = slowSum = 0;
                for (int i = 0; i < windowSeconds; i++) {
                    totalCounts[i] = 0;
                    failCounts[i] = 0;
                    slowCounts[i] = 0;
                }
                // 重置当前索引到0
                currentIndex = 0;
            } else {
                // 滑动窗口逐步前移，清除过期桶的数据
                for (long i = 0; i < diff; i++) {
                    currentIndex = (currentIndex + 1) % windowSeconds;
                    // 移除当前索引桶的数据出总和
                    totalSum -= totalCounts[currentIndex];
                    failSum -= failCounts[currentIndex];
                    slowSum -= slowCounts[currentIndex];
                    // 清空该过期桶
                    totalCounts[currentIndex] = 0;
                    failCounts[currentIndex] = 0;
                    slowCounts[currentIndex] = 0;
                }
            }
            // 更新最后时间
            lastTickSec = nowSec;
        }
        // 将当前请求计入当前秒对应的桶
        totalCounts[currentIndex] += 1;
        totalSum += 1;
        if (!success) {
            failCounts[currentIndex] += 1;
            failSum += 1;
        }
        if (slow) {
            slowCounts[currentIndex] += 1;
            slowSum += 1;
        }
    }

    /**
     * 获取窗口内总请求次数
     */
    public synchronized int getTotalCount() {
        return totalSum;
    }

    /**
     * 获取窗口内失败请求次数
     */
    public synchronized int getFailCount() {
        return failSum;
    }

    /**
     * 获取窗口内慢请求次数
     */
    public synchronized int getSlowCount() {
        return slowSum;
    }

    /**
     * 重置计数器（清空所有统计数据）。
     */
    public synchronized void reset() {
        lastTickSec = System.currentTimeMillis() / 1000;
        currentIndex = 0;
        totalSum = failSum = slowSum = 0;
        for (int i = 0; i < windowSeconds; i++) {
            totalCounts[i] = 0;
            failCounts[i] = 0;
            slowCounts[i] = 0;
        }
    }
}
