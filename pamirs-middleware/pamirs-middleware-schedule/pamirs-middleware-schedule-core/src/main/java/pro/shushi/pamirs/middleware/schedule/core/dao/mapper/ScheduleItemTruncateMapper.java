package pro.shushi.pamirs.middleware.schedule.core.dao.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.*;

/**
 * 清空表数据dao
 */
@Mapper
public interface ScheduleItemTruncateMapper {

    @InterceptorIgnore(
            tenantLine = "true",
            dynamicTableName = "true",
            blockAttack = "true",
            illegalSql = "true",
            dataPermission = "true"
    )
    @Select(
            "truncate table pamirs_schedule_${tableNum}"
    )
    void deleteBySeparate(@Param("tableNum") String tableNum);

}
