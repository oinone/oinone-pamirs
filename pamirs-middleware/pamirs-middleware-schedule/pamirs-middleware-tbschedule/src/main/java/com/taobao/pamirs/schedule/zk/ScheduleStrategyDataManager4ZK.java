package com.taobao.pamirs.schedule.zk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taobao.pamirs.schedule.ScheduleUtil;
import com.taobao.pamirs.schedule.strategy.ManagerFactoryInfo;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategyRunntime;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.middleware.schedule.spring.ScheduleSystemInfo;

import java.io.Writer;
import java.sql.Timestamp;
import java.util.*;

public class ScheduleStrategyDataManager4ZK {

    private ZKManager zkManager;
    private String PATH_Strategy;
    private String PATH_ManagerFactory;
    private Gson gson;

    /**
     * 在Spring对象创建完毕后，创建内部对象
     */
    public ScheduleStrategyDataManager4ZK(ZKManager aZkManager) throws Exception {
        this.zkManager = aZkManager;
        gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.PATH_Strategy = this.zkManager.getRootPath() + "/strategy";
        this.PATH_ManagerFactory = this.zkManager.getRootPath() + "/factory";

        if (this.getZooKeeper().exists(this.PATH_Strategy, false) == null) {
            ZKTools.createPath(getZooKeeper(), this.PATH_Strategy, CreateMode.PERSISTENT, this.zkManager.getAcl());
        }
        if (this.getZooKeeper().exists(this.PATH_ManagerFactory, false) == null) {
            ZKTools
                    .createPath(getZooKeeper(), this.PATH_ManagerFactory, CreateMode.PERSISTENT, this.zkManager.getAcl());
        }
    }

    public ScheduleStrategy loadStrategy(String strategyName) throws Exception {
        String zkPath = this.PATH_Strategy + "/" + strategyName;
        if (this.getZooKeeper().exists(zkPath, false) == null) {
            return null;
        }
        String valueString = new String(this.getZooKeeper().getData(zkPath, false, null));
        ScheduleStrategy result = this.gson.fromJson(valueString, ScheduleStrategy.class);
        return result;
    }

    public void createScheduleStrategy(ScheduleStrategy scheduleStrategy) throws Exception {
        String zkPath = this.PATH_Strategy + "/" + scheduleStrategy.getStrategyName();
        String valueString = this.gson.toJson(scheduleStrategy);
        if (this.getZooKeeper().exists(zkPath, false) == null) {
            this.getZooKeeper().create(zkPath, valueString.getBytes(), this.zkManager.getAcl(), CreateMode.PERSISTENT);
        } else {
            throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.strategy_exists") + scheduleStrategy.getStrategyName()
                    + I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.strategy_exists_suffix"));
        }
    }

    public void updateScheduleStrategy(ScheduleStrategy scheduleStrategy) throws Exception {
        String zkPath = this.PATH_Strategy + "/" + scheduleStrategy.getStrategyName();
        String valueString = this.gson.toJson(scheduleStrategy);
        if (this.getZooKeeper().exists(zkPath, false) == null) {
            this.getZooKeeper().create(zkPath, valueString.getBytes(), this.zkManager.getAcl(), CreateMode.PERSISTENT);
        } else {
            this.getZooKeeper().setData(zkPath, valueString.getBytes(), -1);
        }

    }

    public void deleteMachineStrategy(String taskType) throws Exception {
        deleteMachineStrategy(taskType, false);
    }

    public void pause(String strategyName) throws Exception {
        ScheduleStrategy strategy = this.loadStrategy(strategyName);
        strategy.setSts(ScheduleStrategy.STS_PAUSE);
        this.updateScheduleStrategy(strategy);
    }

    public void resume(String strategyName) throws Exception {
        ScheduleStrategy strategy = this.loadStrategy(strategyName);
        strategy.setSts(ScheduleStrategy.STS_RESUME);
        this.updateScheduleStrategy(strategy);
    }

    public void deleteMachineStrategy(String taskType, boolean isForce) throws Exception {
        String zkPath = this.PATH_Strategy + "/" + taskType;
        if (isForce == false && this.getZooKeeper().getChildren(zkPath, null).size() > 0) {
            throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.cannot_delete_strategy") + taskType + I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.cannot_delete_strategy_middle"));
        }
        ZKTools.deleteTree(this.getZooKeeper(), zkPath);
    }

    public List<ScheduleStrategy> loadAllScheduleStrategy() throws Exception {
        String zkPath = this.PATH_Strategy;
        List<ScheduleStrategy> result = new ArrayList<>();
        List<String> names = this.getZooKeeper().getChildren(zkPath, false);
        Collections.sort(names);
        for (String name : names) {
            result.add(this.loadStrategy(name));
        }
        return result;
    }

    /**
     * 注册ManagerFactory
     *
     * @return 需要全部注销的调度，例如当IP不在列表中
     */
    public List<String> registerManagerFactory(TBScheduleManagerFactory managerFactory) throws Exception {

        if (managerFactory.getUuid() == null) {
            String uuid =
                    managerFactory.getIp() + "$" + managerFactory.getHostName() + "$" + UUID.randomUUID().toString()
                            .replaceAll("-", "").toUpperCase();
            String zkPath = this.PATH_ManagerFactory + "/" + uuid + "$";
            zkPath = this.getZooKeeper().create(zkPath, null, this.zkManager.getAcl(), CreateMode.EPHEMERAL_SEQUENTIAL);
            managerFactory.setUuid(zkPath.substring(zkPath.lastIndexOf("/") + 1));
        } else {
            String zkPath = this.PATH_ManagerFactory + "/" + managerFactory.getUuid();
            if (this.getZooKeeper().exists(zkPath, false) == null) {
                this.getZooKeeper().create(zkPath, null, this.zkManager.getAcl(), CreateMode.EPHEMERAL);
            }
        }

        List<String> result = new ArrayList<>();
        for (ScheduleStrategy scheduleStrategy : loadAllScheduleStrategy()) {
            boolean isFind = false;

            if (ScheduleStrategy.STS_PAUSE.equalsIgnoreCase(scheduleStrategy.getSts()) == false
                    && scheduleStrategy.getIPList() != null) {
                String currentOwnSign = ScheduleUtil.splitOwnsignFromTaskType(scheduleStrategy.getTaskName());
                if (!ScheduleUtil.OWN_SIGN_BASE.equals(currentOwnSign) && !currentOwnSign.equals(ScheduleSystemInfo.STATIC_OWN_SIGN_VALUE)) {
                    continue;
                }
                for (String ip : scheduleStrategy.getIPList()) {
                    if (ip.equals("127.0.0.1") || ip.equalsIgnoreCase("localhost") || ip.equals(managerFactory.getIp())
                            || ip.equalsIgnoreCase(managerFactory.getHostName())) {
                        // 添加可管理TaskType
                        String zkPath =
                                this.PATH_Strategy + "/" + scheduleStrategy.getStrategyName() + "/" + managerFactory
                                        .getUuid();
                        if (this.getZooKeeper().exists(zkPath, false) == null) {
                            this.getZooKeeper()
                                    .create(zkPath, null, this.zkManager.getAcl(), CreateMode.EPHEMERAL);
                        }
                        isFind = true;
                        break;
                    }
                }
            }
            // 暂停或者不在IP范围，清除原来注册的Factory
            if (isFind == false) {
                String zkPath =
                        this.PATH_Strategy + "/" + scheduleStrategy.getStrategyName() + "/" + managerFactory.getUuid();
                if (this.getZooKeeper().exists(zkPath, false) != null) {
                    ZKTools.deleteTree(this.getZooKeeper(), zkPath);
                    result.add(scheduleStrategy.getStrategyName());
                }
            }
        }
        return result;
    }

    /**
     * 注销服务，停止调度
     */
    public void unRregisterManagerFactory(TBScheduleManagerFactory managerFactory) throws Exception {
        for (String taskName : this.getZooKeeper().getChildren(this.PATH_Strategy, false)) {
            String zkPath = this.PATH_Strategy + "/" + taskName + "/" + managerFactory.getUuid();
            if (this.getZooKeeper().exists(zkPath, false) != null) {
                ZKTools.deleteTree(this.getZooKeeper(), zkPath);
            }
        }
    }

    public ScheduleStrategyRunntime loadScheduleStrategyRunntime(String strategyName, String uuid) throws Exception {
        String zkPath = this.PATH_Strategy + "/" + strategyName + "/" + uuid;
        ScheduleStrategyRunntime result = null;
        if (this.getZooKeeper().exists(zkPath, false) != null) {
            byte[] value = this.getZooKeeper().getData(zkPath, false, null);
            if (value != null) {
                String valueString = new String(value);
                result = this.gson.fromJson(valueString, ScheduleStrategyRunntime.class);
                if (null == result) {
                    throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.gson_null_obj"));
                }
                if (null == result.getStrategyName()) {
                    throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.gson_null_strategy_name"));
                }
                if (null == result.getUuid()) {
                    throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.gson_null_uuid"));
                }
            } else {
                result = new ScheduleStrategyRunntime();
                result.setStrategyName(strategyName);
                result.setUuid(uuid);
                result.setRequestNum(0);
                result.setMessage("");
            }
        }
        return result;
    }

    /**
     * 装载所有的策略运行状态
     */
    public List<ScheduleStrategyRunntime> loadAllScheduleStrategyRunntime() throws Exception {
        List<ScheduleStrategyRunntime> result = new ArrayList<>();
        String zkPath = this.PATH_Strategy;
        for (String taskType : this.getZooKeeper().getChildren(zkPath, false)) {
            for (String uuid : this.getZooKeeper().getChildren(zkPath + "/" + taskType, false)) {
                result.add(loadScheduleStrategyRunntime(taskType, uuid));
            }
        }
        return result;
    }

    public List<ScheduleStrategyRunntime> loadAllScheduleStrategyRunntimeByUUID(String managerFactoryUUID)
            throws Exception {
        List<ScheduleStrategyRunntime> result = new ArrayList<>();
        String zkPath = this.PATH_Strategy;

        List<String> taskTypeList = this.getZooKeeper().getChildren(zkPath, false);
        Collections.sort(taskTypeList);
        for (String taskType : taskTypeList) {
            if (this.getZooKeeper().exists(zkPath + "/" + taskType + "/" + managerFactoryUUID, false) != null) {
                result.add(loadScheduleStrategyRunntime(taskType, managerFactoryUUID));
            }
        }
        return result;
    }

    public List<ScheduleStrategyRunntime> loadAllScheduleStrategyRunntimeByTaskType(String strategyName)
            throws Exception {
        List<ScheduleStrategyRunntime> result = new ArrayList<>();
        String zkPath = this.PATH_Strategy;
        if (this.getZooKeeper().exists(zkPath + "/" + strategyName, false) == null) {
            return result;
        }
        List<String> uuidList = this.getZooKeeper().getChildren(zkPath + "/" + strategyName, false);
        // 排序
        Collections.sort(uuidList, new Comparator<String>() {
            @Override
            public int compare(String u1, String u2) {
                return u1.substring(u1.lastIndexOf("$") + 1).compareTo(u2.substring(u2.lastIndexOf("$") + 1));
            }
        });

        for (String uuid : uuidList) {
            result.add(loadScheduleStrategyRunntime(strategyName, uuid));
        }
        return result;
    }

    /**
     * 更新请求数量
     */
    public void updateStrategyRunntimeReqestNum(String strategyName, String manangerFactoryUUID, int requestNum)
            throws Exception {
        String zkPath = this.PATH_Strategy + "/" + strategyName + "/" + manangerFactoryUUID;
        ScheduleStrategyRunntime result = null;
        if (this.getZooKeeper().exists(zkPath, false) != null) {
            result = this.loadScheduleStrategyRunntime(strategyName, manangerFactoryUUID);
        } else {
            result = new ScheduleStrategyRunntime();
            result.setStrategyName(strategyName);
            result.setUuid(manangerFactoryUUID);
            result.setRequestNum(requestNum);
            result.setMessage("");
        }
        result.setRequestNum(requestNum);
        String valueString = this.gson.toJson(result);
        this.getZooKeeper().setData(zkPath, valueString.getBytes(), -1);
    }

    /**
     * 更新调度过程中的信息
     */
    public void updateStrategyRunntimeErrorMessage(String strategyName, String manangerFactoryUUID, String message)
            throws Exception {
        String zkPath = this.PATH_Strategy + "/" + strategyName + "/" + manangerFactoryUUID;
        ScheduleStrategyRunntime result;
        if (this.getZooKeeper().exists(zkPath, false) != null) {
            result = this.loadScheduleStrategyRunntime(strategyName, manangerFactoryUUID);
        } else {
            result = new ScheduleStrategyRunntime();
            result.setStrategyName(strategyName);
            result.setUuid(manangerFactoryUUID);
            result.setRequestNum(0);
        }
        result.setMessage(message);
        String valueString = this.gson.toJson(result);
        this.getZooKeeper().setData(zkPath, valueString.getBytes(), -1);
    }

    public void updateManagerFactoryInfo(String uuid, boolean isStart) throws Exception {
        String zkPath = this.PATH_ManagerFactory + "/" + uuid;
        if (this.getZooKeeper().exists(zkPath, false) == null) {
            throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.task_manager_not_exist") + uuid);
        }
        this.getZooKeeper().setData(zkPath, Boolean.toString(isStart).getBytes(), -1);
    }

    public ManagerFactoryInfo loadManagerFactoryInfo(String uuid) throws Exception {
        String zkPath = this.PATH_ManagerFactory + "/" + uuid;
        if (this.getZooKeeper().exists(zkPath, false) == null) {
            throw new Exception(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.task_manager_not_exist") + uuid);
        }
        byte[] value = this.getZooKeeper().getData(zkPath, false, null);
        ManagerFactoryInfo result = new ManagerFactoryInfo();
        result.setUuid(uuid);
        if (value == null) {
            result.setStart(true);
        } else {
            result.setStart(Boolean.parseBoolean(new String(value)));
        }
        return result;
    }

    /**
     * 导入配置信息【目前支持baseTaskType和strategy数据】
     */
    public void importConfig(String config, Writer writer, boolean isUpdate) throws Exception {
        ConfigNode configNode = gson.fromJson(config, ConfigNode.class);
        if (configNode != null) {
            String path = configNode.getRootPath() + "/" + configNode.getConfigType();
            ZKTools.createPath(getZooKeeper(), path, CreateMode.PERSISTENT, zkManager.getAcl());
            String y_node = path + "/" + configNode.getName();
            if (getZooKeeper().exists(y_node, false) == null) {
                writer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.import_success"));
                getZooKeeper()
                        .create(y_node, configNode.getValue().getBytes(), zkManager.getAcl(), CreateMode.PERSISTENT);
            } else if (isUpdate) {
                writer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.config_exists_forced_update"));
                getZooKeeper().setData(y_node, configNode.getValue().getBytes(), -1);
            } else {
                writer.append(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.config_exists_need_force"));
            }
        }
        writer.append(configNode.toString());
    }

    /**
     * 输出配置信息【目前备份baseTaskType和strategy数据】
     */
    public StringBuffer exportConfig(String rootPath, Writer writer) throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (String type : new String[]{"baseTaskType", "strategy"}) {
            if (type.equals("baseTaskType")) {
                writer.write(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.basic_task_config_list"));
            } else {
                writer.write(I18nUtils.getMessage("pamirs-middleware-tbschedule.ScheduleStrategyDataManager4ZK.basic_strategy_config_list"));
            }
            String bTTypePath = rootPath + "/" + type;
            List<String> fNodeList = getZooKeeper().getChildren(bTTypePath, false);
            for (int i = 0; i < fNodeList.size(); i++) {
                String fNode = fNodeList.get(i);
                ConfigNode configNode = new ConfigNode(rootPath, type, fNode);
                configNode.setValue(new String(this.getZooKeeper().getData(bTTypePath + "/" + fNode, false, null)));
                buffer.append(gson.toJson(configNode));
                buffer.append("\n");
                writer.write(configNode.toString());
            }
            writer.write("\n\n");
        }
        if (buffer.length() > 0) {
            String str = buffer.toString();
            return new StringBuffer(str.substring(0, str.length() - 1));
        }
        return buffer;
    }

    public List<ManagerFactoryInfo> loadAllManagerFactoryInfo() throws Exception {
        String zkPath = this.PATH_ManagerFactory;
        List<ManagerFactoryInfo> result = new ArrayList<>();
        List<String> names = this.getZooKeeper().getChildren(zkPath, false);
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String u1, String u2) {
                return u1.substring(u1.lastIndexOf("$") + 1).compareTo(u2.substring(u2.lastIndexOf("$") + 1));
            }
        });
        for (String name : names) {
            ManagerFactoryInfo info = new ManagerFactoryInfo();
            info.setUuid(name);
            byte[] value = this.getZooKeeper().getData(zkPath + "/" + name, false, null);
            if (value == null) {
                info.setStart(true);
            } else {
                info.setStart(Boolean.parseBoolean(new String(value)));
            }
            result.add(info);
        }
        return result;
    }

    public void printTree(String path, Writer writer, String lineSplitChar) throws Exception {
        ZKTools.printTree(this.getZooKeeper(), path, writer, lineSplitChar);
    }

    public void deleteTree(String path) throws Exception {
        ZKTools.deleteTree(this.getZooKeeper(), path);
    }

    public ZooKeeper getZooKeeper() throws Exception {
        return this.zkManager.getZooKeeper();
    }

    public String getRootPath() {
        return this.zkManager.getRootPath();
    }
}
