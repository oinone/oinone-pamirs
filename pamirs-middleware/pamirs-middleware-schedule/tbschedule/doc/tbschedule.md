* [tbschedule主要概念](#tbschedule%E4%B8%BB%E8%A6%81%E6%A6%82%E5%BF%B5)
  * [TaskType任务类型:](#tasktype%E4%BB%BB%E5%8A%A1%E7%B1%BB%E5%9E%8B)
  * [ScheduleServer任务处理器](#scheduleserver%E4%BB%BB%E5%8A%A1%E5%A4%84%E7%90%86%E5%99%A8)
  * [TaskItem任务项](#taskitem%E4%BB%BB%E5%8A%A1%E9%A1%B9)
  * [TaskDealBean任务处理类](#taskdealbean%E4%BB%BB%E5%8A%A1%E5%A4%84%E7%90%86%E7%B1%BB)
  * [OwnSign环境区域](#ownsign%E7%8E%AF%E5%A2%83%E5%8C%BA%E5%9F%9F)
  * [单次调度执行次数](#%E5%8D%95%E6%AC%A1%E8%B0%83%E5%BA%A6%E6%89%A7%E8%A1%8C%E6%AC%A1%E6%95%B0)
  * [调度策略](#%E8%B0%83%E5%BA%A6%E7%AD%96%E7%95%A5)
  * [接口说明](#%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E)
  * [Sleep模式和NotSleep模式的区别](#sleep%E6%A8%A1%E5%BC%8F%E5%92%8Cnotsleep%E6%A8%A1%E5%BC%8F%E7%9A%84%E5%8C%BA%E5%88%AB)

## tbschedule主要概念

### TaskType任务类型: 
是任务调度分配处理的单位，例如：

1、将一张表中的所有状态为STS=’N’的所有数据提取出来发送给其它系统，同时将修改状态STS=’Y’,就是一种任务。TaskType=’DataDeal’

2、将一个目录以所有子目录下的所有文件读取出来入库，同时把文件移到对应的备份目录中，也是一种任务。TaskType=’FileDeal’。

3、可以为一个任务类型自定义一个字符串参数由应用自己解析。例如:"AREA=杭州,YEAR>30"
### ScheduleServer任务处理器

1、 是由一组线程【1..n个线程】构成的任务处理单元，每一任务处理器有一个唯一的全局标识，
一般以IP$UUID[例如192.168.1.100$0C78F0C0FA084E54B6665F4D00FA73DC]的形式出现。
一个任务类型的数据可以由1..n个任务处理器同时进行。

2、 这些任务处理器可以在同一个JVM中，也可以分布在不同主机的JVM中。任务处理器内部有一个心跳线程，
用于确定Server的状态和任务的动态分配，有一组工作线程，负责处理查询任务和具体的任务处理工作。

3、 目前版本所有的心跳信息都是存放在Zookeeper服务器中的，所有的Server都是对等的，
当一个Server死亡后，其它Server会接管起拥有的任务队列，
期间会有几个心跳周期的时延。后续可以用类似ConfigerServer类的存储。

4、 现有的工作线程模式分为Sleep模式和NotSleep模式。缺省是缺省是NOTSLEEP模式。在通常模式下，在通常情况下用Sleep模式。
在一些特殊情况需要用NotSleep模式。两者之间的差异在后续进行描述。
### TaskItem任务项

是对任务进行的分片划分。例如：

1、将一个数据表中所有数据的ID按10取模，就将数据划分成了0、1、2、3、4、5、6、7、8、9供10个任务项。

2、将一个目录下的所有文件按文件名称的首字母(不区分大小写)，就划分成了A、B、C、D、E、F、G、H、I、J、K、L、M、N、O、P、Q、R、S、T、U、V、W、X、Y、Z供26个队列。

3、将一个数据表的数据ID哈希后按1000取模作为最后的HASHCODE,我们就可以将数据按[0,100)、[100,200) 、[200,300)、[300,400) 、
[400,500)、[500,600)、[600,700)、[700,800)、[800,900)、 [900,1000)划分为十个任务项，当然你也可以划分为100个任务项，最多是1000个任务项。
任务项是进行任务分配的最小单位。一个任务项只能由一个ScheduleServer来进行处理。但一个Server可以处理任意数量的任务项。
例如任务被划分为了10个队列，可以只启动一个Server，所有的任务项都有这一个Server来处理；也可以启动两个Server，每个Sever处理5个任务项；
但最多只能启动10个Server，每一个ScheduleServer只处理一个任务项。如果在多，则第11个及之后的Server将不起作用，处于休眠状态。

4、可以为一个任务项自定义一个字符串参数由应用自己解析。例如:"TYPE=A,KIND=1"

### TaskDealBean任务处理类
是业务系统进行数据处理的实现类。要求实现Schedule的接口IScheduleTaskDealMulti或者IScheduleTaskDealSingle。
接口主要包括两个方法。一个是根据调度器分配到的队列查询数据的接口，一个是进行数据处理的接口。
运行时间：

1、可以指定任务处理的时间间隔，例如每天的1：00－3：00执行，或者每个月的第一天执行、每一个小时的第一分钟执行等等。
间格式与crontab相同。如果不指定就表示一致运行。【执行开始时间】、【执行结束时间】

2、可以指定如果没有数据了，休眠的时间间隔。【没有数据时休眠时长(秒)】

3、可以指定每处理完一批数据后休眠的时间间隔。【每次处理完数据后休眠时间(秒)】 

### OwnSign环境区域

是对运行环境的划分，进行调度任务和数据隔离。例如：开发环境、测试环境、预发环境、生产环境。

不同的开发人员需要进行数据隔离也可以用OwnSign来实现，避免不同人员的数据冲突，缺省配置的环境区域OwnSign='BASE'。

例如：TaskType='DataDeal'，配置的队列是0、1、2、3、4、5、6、7、8、9。缺省的OwnSign='BASE'。
此时如果再启动一个测试环境，则Schedule会动态生成一个TaskType='DataDeal-Test'的任务类型，环境会作为一个变量传递给业务接口，
由业务接口的实现类，在读取数据和处理数据的时候进行确定。业务系统一种典型的做法就是在数据表中增加一个OWN_SIGN字段。
在创建数据的时候根据运行环境填入对应的环境名称，在Schedule中就可以环境的区分了。

### 单次调度执行次数

如果没有设置【执行结束时间】，一旦开始执行，中间不会停止，直到selectTasks取不到数据为止。所以默认状态下，
Tbschedule处理的任务要求具有状态字段，每一个数据的处理成功后，必须更新状态为其它状态，否则会重复处理同一批数据。

如果单个调度周期，只想让作业执行一次，可以通过控制台页面上点击【任务管理】-【编辑】-【单次调度执行次数】进行设置

### 调度策略

是指某一个任务在调度集群上的分布策略，可以制定：

1、可以指定任务的机器IP列表。127.0.0.1和localhost表示所有机器上都可以执行

2、可以指定每个机器上能启动的线程组数量，0表示没有限制

3、可以指定所有机器上运行的线程组总数。

### 接口说明

包含三个业务接口：

1、IScheduleTaskDeal 调度器对外的基础接口，是一个基类，并不能被直接使用

2、IScheduleTaskDealSingle 单任务处理的接口,继承 IScheduleTaskDeal

3、IScheduleTaskDealMulti 可批处理的任务接口,继承 IScheduleTaskDeal

IScheduleTaskDeal 调度器对外的基础接口
```java
/**
 * 调度器对外的基础接口
 *
 * @param <T> 任务类型
 */
public interface IScheduleTaskDeal<T> {

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param taskParameter 任务的自定义参数
     * @param ownSign 当前环境名称
     * @param taskItemNum 当前任务类型的任务队列数量
     * @param taskItemList 当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     */
    public List<T> selectTasks(String taskParameter, String ownSign, int taskItemNum, List<TaskItemDefine> taskItemList,
        int eachFetchDataNum) throws Exception;

    /**
     * 获取任务的比较器,主要在NotSleep模式下需要用到
     */
    public Comparator<T> getComparator();

}

```
- IScheduleTaskDealSingle 单任务处理的接口
```java
/**
 * 单个任务处理的接口
 */
public interface IScheduleTaskDealSingle<T> extends IScheduleTaskDeal<T> {

    /**
     * 执行单个任务
     *
     * @param task Object
     * @param ownSign 当前环境名称
     */
    public boolean execute(T task, String ownSign) throws Exception;

}
```
- IScheduleTaskDealMulti 可批处理的任务接口
```java

/**
 * 可批处理的任务接口
 */
public interface IScheduleTaskDealMulti<T> extends IScheduleTaskDeal<T> {

    /**
     * 执行给定的任务数组。因为泛型不支持new 数组，只能传递OBJECT[]
     *
     * @param tasks 任务数组
     * @param ownSign 当前环境名称
     */
    public boolean execute(T[] tasks, String ownSign) throws Exception;
}
```
### Sleep模式和NotSleep模式的区别

1、ScheduleServer启动的工作线程组线程是共享一个任务池的。

2、在Sleep的工作模式：当某一个线程任务处理完毕，从任务池中取不到任务的时候，检查其它线程是否处于活动状态。如果是，则自己休眠；
 如果其它线程都已经因为没有任务进入休眠，当前线程是最后一个活动线程的时候，就调用业务接口，获取需要处理的任务，放入任务池中，
 同时唤醒其它休眠线程开始工作。

3、在NotSleep的工作模式：当一个线程任务处理完毕，从任务池中取不到任务的时候，立即调用业务接口获取需要处理的任务，放入任务池中。

4、Sleep模式在实现逻辑上相对简单清晰，但存在一个大任务处理时间长，导致其它线程不工作的情况。

5、在NotSleep模式下，减少了线程休眠的时间，避免大任务阻塞的情况，但为了避免数据被重复处理，增加了CPU在数据比较上的开销。
 同时要求业务接口实现对象的比较接口。

6、如果对任务处理不允许停顿的情况下建议用NotSleep模式，其它情况建议用sleep模式。