package pro.shushi.pamirs.sequence.dao;

import pro.shushi.pamirs.sequence.model.LeafAlloc;

import java.util.List;

/**
 * ISequenceDao
 *
 * @author yakir on 2020/04/08 15:06.
 */
public interface ISequenceDao {

    LeafAlloc getLeafAllocOne(LeafAlloc leafAlloc);

    LeafAlloc getLeafAllocOneByRequired(LeafAlloc leafAlloc);

    List<LeafAlloc> getAllLeafAllocs();

    LeafAlloc getLeafAllocAndUpdateMaxId(LeafAlloc leafAlloc);

    LeafAlloc updateMaxIdAndGetLeafAlloc(LeafAlloc leafAlloc);

    LeafAlloc getLeafAllocAndUpdateMaxIdByCustomStep(LeafAlloc leafAlloc);

    LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

    LeafAlloc updateMaxIdByCustomStepAndGetLeafAllocByRequired(LeafAlloc leafAlloc);

    List<String> getAllTags();

}
