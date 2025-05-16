package pro.shushi.pamirs.middleware.schedule.core.dao.sharding;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TableSeg {

    //表名
    String tableName();

    //根据什么字段分表
    String shardBy();

}