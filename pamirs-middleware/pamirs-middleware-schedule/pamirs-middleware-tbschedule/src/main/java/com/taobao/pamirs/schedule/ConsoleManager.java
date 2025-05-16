package com.taobao.pamirs.schedule;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.IScheduleDataManager;
import com.taobao.pamirs.schedule.zk.ScheduleStrategyDataManager4ZK;
import com.taobao.pamirs.schedule.zk.ZKManager;

public class ConsoleManager {

    public static final String CONFIG_FILE = System.getProperty("user.dir") + File.separator
            + "pamirsScheduleConfig.properties";

    private static final transient Logger LOG = LoggerFactory.getLogger(ConsoleManager.class);

    private static TBScheduleManagerFactory scheduleManagerFactory;

    public static boolean isInitial() {
        return scheduleManagerFactory != null;
    }

    public static boolean initial() throws Exception {
        if (scheduleManagerFactory != null) {
            return true;
        }
        File file = new File(CONFIG_FILE);
        scheduleManagerFactory = new TBScheduleManagerFactory();
        scheduleManagerFactory.start = false;

        if (file.exists()) {
            // Console不启动调度能力
            Properties p = new Properties();
            FileReader reader = new FileReader(file);
            p.load(reader);
            reader.close();
            scheduleManagerFactory.init(p);
            LOG.info("加载Schedule配置文件：" + CONFIG_FILE);
            return true;
        } else {
            return false;
        }
    }

    public static TBScheduleManagerFactory getScheduleManagerFactory() throws Exception {
        if (!isInitial()) {
            initial();
        }
        return scheduleManagerFactory;
    }

    public static IScheduleDataManager getScheduleDataManager() throws Exception {
        if (!isInitial()) {
            initial();
        }
        return scheduleManagerFactory.getScheduleDataManager();
    }

    public static ScheduleStrategyDataManager4ZK getScheduleStrategyManager() throws Exception {
        if (!isInitial()) {
            initial();
        }
        return scheduleManagerFactory.getScheduleStrategyManager();
    }

    public static Properties loadConfig() throws IOException {
        File file = new File(CONFIG_FILE);
        Properties properties;
        if (!file.exists()) {
            properties = ZKManager.createProperties();
        } else {
            properties = new Properties();
            FileReader reader = new FileReader(file);
            properties.load(reader);
            reader.close();
        }
        return properties;
    }

    public static void saveConfigInfo(Properties p) throws Exception {
        FileWriter writer = new FileWriter(CONFIG_FILE);
        p.store(writer, "");
        writer.close();
        if (scheduleManagerFactory == null) {
            initial();
        } else {
            scheduleManagerFactory.reInit(p);
        }
    }

    public static void setScheduleManagerFactory(TBScheduleManagerFactory scheduleManagerFactory) {
        ConsoleManager.scheduleManagerFactory = scheduleManagerFactory;
    }

}
