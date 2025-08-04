package pro.shushi.pamirs.middleware.schedule.core.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.IScheduleTaskDealSingle;
import com.taobao.pamirs.schedule.ScheduleUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.middleware.schedule.constant.ScheduleConstant;
import pro.shushi.pamirs.middleware.schedule.core.condition.ScheduleSwitchCondition;
import pro.shushi.pamirs.middleware.schedule.core.conf.ScheduleSystemInfo;
import pro.shushi.pamirs.middleware.schedule.core.conf.TaskNodeConfiguration;
import pro.shushi.pamirs.middleware.schedule.core.conf.TaskNodeStrategyParams;
import pro.shushi.pamirs.middleware.schedule.core.conf.TaskNodeValueParams;
import pro.shushi.pamirs.middleware.schedule.core.exception.TaskConfigNodeException;
import pro.shushi.pamirs.middleware.schedule.core.tasks.*;
import pro.shushi.pamirs.middleware.schedule.core.util.ScheduleFileHelper;
import pro.shushi.pamirs.middleware.schedule.core.verification.util.VerificationHelper;
import pro.shushi.pamirs.middleware.schedule.eunmeration.TaskType;
import pro.shushi.pamirs.middleware.zookeeper.service.SpringContextManager;
import pro.shushi.pamirs.middleware.zookeeper.service.ZookeeperService;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@DependsOn({TBScheduleManagerBean.TB_SCHEDULE_MANAGER_FACTORY_BEAN_NAME, ScheduleSystemInfo.BEAN_NAME})
@Conditional(ScheduleSwitchCondition.class)
public class ScheduleTaskManager {

    private static final BigDecimalValueFilter BIG_DECIMAL_VALUE_FILTER = new BigDecimalValueFilter();

    private static final Logger log = LoggerFactory.getLogger(ScheduleTaskManager.class);

    private static final Class<?>[] ALLOW_SCHEDULE_INTERFACE_CLASSES = new Class<?>[]{IScheduleTaskDeal.class, IScheduleTaskDealSingle.class, IScheduleTaskDealMulti.class};

    @Value("${" + ScheduleConstant.SCHEDULE_AUTO_INIT_DATA_CONFIG + ":true}")
    private boolean isAutoInitData;

    @Value("${" + ScheduleConstant.SCHEDULE_AUTO_CREATE_CONFIG_FILE + ":false}")
    private boolean isAutoCreateConfigFile;

    @Autowired
    private SpringContextManager contextManager;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private ScheduleTaskActionManager actionManager;

    @PostConstruct
    public void init() throws Exception {
        //读取任务配置
        List<TaskNodeConfiguration> configNodes = readTaskConfigNodes();
        for (TaskNodeConfiguration configNode : configNodes) {
            //校验节点配置
            verificationAndSetTaskConfigNode(configNode);
            //初始化任务Bean
            initTaskBean(configNode);
            if (isAutoInitData) {
                try {
                    //创建Zookeeper节点
                    initZookeeperNode(configNode);
                } catch (Exception e) {
                    log.error("create zookeeper node error.", e);
                }
            }
        }
    }

    private List<TaskNodeConfiguration> readTaskConfigNodes() {
        List<TaskNodeConfiguration> configNodes = new ArrayList<>();
        try {
            ScheduleFileHelper.readJsonConfig(data -> {
                List<Map<String, Object>> list = JSON.parseObject(data, new TypeReference<List<Map<String, Object>>>() {
                }.getType());
                for (Map<String, Object> item : list) {
                    //noinspection unchecked
                    configNodes.add(new TaskNodeConfiguration(TaskType.valueOf((String) item.get(ScheduleConstant.TASK_NODE_CONFIG_TASK_TYPE_KEY)))
                            .setBeanClassName((String) item.get(ScheduleConstant.TASK_NODE_CONFIG_BEAN_CLASS_NAME))
                            .setBeanNames((String) item.get(ScheduleConstant.TASK_NODE_CONFIG_BEAN_NAMES))
                            .setValues((Map<String, Object>) item.get(ScheduleConstant.TASK_NODE_CONFIG_VALUES_KEY))
                            .setStrategy((Map<String, Object>) item.get(ScheduleConstant.STRATEGY_KEY)));
                }
            }, isAutoCreateConfigFile);
        } catch (IOException e) {
            log.error("任务配置读取失败", e);
        }
        return configNodes;
    }

    private void verificationAndSetTaskConfigNode(TaskNodeConfiguration configNode) throws TaskConfigNodeException {
        if (configNode == null) {
            throw TaskConfigNodeException.create(TaskConfigNodeException.Code.NODE_NULL);
        }
        TaskType type = configNode.getTaskType();
        if (type == null) {
            throw TaskConfigNodeException.create(TaskConfigNodeException.Code.NODE_TYPE_NULL);
        }
        Map<String, Object> values = configNode.getValues();
        VerificationHelper.required(values, "values is null");
        VerificationHelper.verify(values, TaskNodeValueParams.class);
        Map<String, Object> strategy = configNode.getStrategy();
        VerificationHelper.required(strategy, "strategy is null");
        VerificationHelper.verify(strategy, TaskNodeStrategyParams.class);
    }

    private void initTaskBean(TaskNodeConfiguration configNode) {
        TaskType taskType = configNode.getTaskType();
        AbstractPamirsScheduleTaskDealSingle<?> scheduleTaskDealSingle;
        boolean isCustom = false;
        switch (taskType) {
            case REMOTE_SCHEDULE_TASK:
                scheduleTaskDealSingle = new RemoteScheduleTask();
                break;
            case BASE_SCHEDULE_TASK:
                scheduleTaskDealSingle = new BaseScheduleTask();
                break;
            case BASE_SCHEDULE_NO_TRANSACTION_TASK:
                scheduleTaskDealSingle = new BaseScheduleNoTransactionTask();
                break;
            case CYCLE_SCHEDULE_NO_TRANSACTION_TASK:
                scheduleTaskDealSingle = new CycleScheduleNoTransactionTask();
                break;
            case SERIAL_REMOTE_SCHEDULE_TASK:
                scheduleTaskDealSingle = new SerialRemoteScheduleTask();
                break;
            case SERIAL_BASE_SCHEDULE_TASK:
                scheduleTaskDealSingle = new SerialBaseScheduleTask();
                break;
            case SERIAL_BASE_SCHEDULE_NO_TRANSACTION_TASK:
                scheduleTaskDealSingle = new SerialBaseScheduleNoTransactionTask();
                break;
            case SERIAL_BASE_SCHEDULE_TYPES_TASK:
                scheduleTaskDealSingle = new SerialBaseScheduleTypesTask();
                break;
            case DELAY_MSG_TRANSFER_SCHEDULE_TASK:
                scheduleTaskDealSingle = new DelayMsgTransferScheduleTask();
                break;
            case DELETE_TRANSFER_SCHEDULE_TASK:
                scheduleTaskDealSingle = new DeleteTransferScheduleTask();
                break;
            case CUSTOM:
                scheduleTaskDealSingle = null;
                isCustom = true;
                break;
            default:
                throw new IllegalArgumentException("Invalid task type.");
        }
        String beanNames = configNode.getBeanNames();
        List<String> finalBeanNameList = new ArrayList<>();
        List<String> beanNameList = new ArrayList<>();
        if (StringUtils.isNotBlank(beanNames)) {
            String[] beanNameSplitResults = beanNames.split(",");
            for (String beanName : beanNameSplitResults) {
                beanNameList.add(beanName.trim());
            }
        }
        if (isCustom) {
            String beanClassName = configNode.getBeanClassName();
            boolean hasBeanClassName = StringUtils.isNotBlank(beanClassName);
            Class<?> beanClass = null;
            if (hasBeanClassName) {
                try {
                    beanClass = Class.forName(beanClassName);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(String.format("Invalid bean class name. beanClassName=%s", beanClassName), e);
                }
                boolean isAllowRegisterInterfaceClass = false;
                for (Class<?> allowRegisterInterfaceClass : ALLOW_SCHEDULE_INTERFACE_CLASSES) {
                    if (allowRegisterInterfaceClass.isAssignableFrom(beanClass)) {
                        isAllowRegisterInterfaceClass = true;
                    }
                }
                if (!isAllowRegisterInterfaceClass) {
                    throw new IllegalArgumentException(String.format("Invalid bean class name. you must be implements schedule interface. beanClassName=%s", beanClassName));
                }
            }
            for (String beanName : beanNameList) {
                if (isNotRegisterBean(beanName)) {
                    if (!hasBeanClassName) {
                        log.error("自定义任务需要注册Spring Bean，但未配置beanClassName，schedule将不会进行该任务的调度");
                        continue;
                    }
                    Object customScheduleTaskDealSingle = contextManager.registerBean(beanClass, beanName, builder -> {
                        AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
                        beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
                        beanDefinition.setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
                    });
                    if (customScheduleTaskDealSingle instanceof AbstractPamirsScheduleTaskDealSingle) {
                        ((AbstractPamirsScheduleTaskDealSingle<?>) customScheduleTaskDealSingle).setTaskType(beanName);
                    }
                }
                finalBeanNameList.add(beanName);
            }
        } else {
            scheduleTaskDealSingle.setTaskType(taskType.getValue());
            if (beanNameList.isEmpty()) {
                beanNameList.add(taskType.getValue());
            }
            for (String beanName : beanNameList) {
                contextManager.registerBean(scheduleTaskDealSingle.getClass(), beanName, builder -> {
                    AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();
                    beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
                    beanDefinition.setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
                    beanDefinition.setInstanceSupplier(() -> scheduleTaskDealSingle);
                });
                finalBeanNameList.add(beanName);
            }
        }
        configNode.setBeanNames(String.join(",", finalBeanNameList));
    }

    private void initZookeeperNode(TaskNodeConfiguration configNode) throws Exception {
        String[] beanNames = configNode.getBeanNames().split(",");
        Map<String, Object> values = configNode.getValues();
        Map<String, Object> strategy = configNode.getStrategy();
        for (String beanName : beanNames) {
            //创建任务节点
            values.put(ScheduleConstant.BASE_TASK_TYPE_KEY, beanName);
            values.put(ScheduleConstant.BASE_TASK_TYPE_DEAL_BEAN_NAME_KEY, beanName);
            String nodePath = "/" + ScheduleConstant.BASE_TASK_TYPE_KEY + "/" + beanName;
            zookeeperService.createOrUpdateData(nodePath, JSON.toJSONString(values, BIG_DECIMAL_VALUE_FILTER, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat).getBytes());

            //创建策略节点
            String ownSignBeanName = ScheduleUtil.getTaskTypeByBaseAndOwnSign(beanName, ScheduleSystemInfo.STATIC_OWN_SIGN_VALUE);
            strategy.put(ScheduleConstant.STRATEGY_NAME_KEY, ownSignBeanName);
            strategy.put(ScheduleConstant.STRATEGY_TASK_NAME_KEY, ownSignBeanName);
            nodePath = "/" + ScheduleConstant.STRATEGY_KEY + "/" + ownSignBeanName;
            zookeeperService.createOrUpdateData(nodePath, JSON.toJSONString(strategy, BIG_DECIMAL_VALUE_FILTER, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat).getBytes());
        }
    }

    private boolean isNotRegisterBean(String beanName) {
        for (Class<?> allowRegisterInterfaceClass : ALLOW_SCHEDULE_INTERFACE_CLASSES) {
            if (contextManager.getBean(allowRegisterInterfaceClass, beanName) != null) {
                return false;
            }
        }
        return true;
    }

    private static class BigDecimalValueFilter implements ValueFilter {

        @Override
        public Object process(Object object, String key, Object value) {
            if (value instanceof BigDecimal) {
                BigDecimal bigDecimalValue = (BigDecimal) value;
                if (new BigDecimal(bigDecimalValue.intValue()).compareTo(bigDecimalValue) == 0) {
                    return bigDecimalValue.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
            return value;
        }
    }
}
