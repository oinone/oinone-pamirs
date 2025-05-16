package pro.shushi.pamirs.middleware.schedule.core.dao.mapper;

import com.baomidou.mybatisplus.annotation.SqlParser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 清空表数据dao
 */
@Mapper
public interface ScheduleItemTruncateMapper {

    @SqlParser(filter = true)
    @Select(
            "truncate table pamirs_schedule_${tableNum}"
    )
    void deleteBySeparate(@Param("tableNum") String tableNum);

}
