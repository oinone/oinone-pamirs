package pro.shushi.pamirs.sequence.mapper;

import org.apache.ibatis.annotations.*;
import pro.shushi.pamirs.sequence.model.LeafAlloc;
import pro.shushi.pamirs.sequence.provider.SequenceSelectProvider;
import pro.shushi.pamirs.sequence.provider.SequenceUpdateProvider;

import java.util.List;

/**
 * ISequenceMapper
 *
 * @author yakir on 2020/04/08 15:08.
 */
@Mapper
public interface ISequenceMapper {

    @SelectProvider(type = SequenceSelectProvider.class, method = "getAllLeafAllocs")
    @Results(value = {
            @Result(column = "code", property = "code"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step"),
            @Result(column = "update_time", property = "updateTime")
    })
    List<LeafAlloc> getAllLeafAllocs();

    @SelectProvider(type = SequenceSelectProvider.class, method = "getLeafAlloc")
    @Results(value = {
            @Result(column = "code", property = "code"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step")
    })
    LeafAlloc getLeafAlloc(LeafAlloc leafAlloc);

    @UpdateProvider(type = SequenceUpdateProvider.class, method = "updateMaxId")
    void updateMaxId(LeafAlloc leafAlloc);

    @UpdateProvider(type = SequenceUpdateProvider.class, method = "updateMaxIdByCustomStep")
    void updateMaxIdByCustomStep(LeafAlloc leafAlloc);

    @SelectProvider(type = SequenceSelectProvider.class, method = "getAllTags")
    List<String> getAllTags();

}
