package pro.shushi.pamirs.sequence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.meta.annotation.sys.Ds;
import pro.shushi.pamirs.sequence.mapper.ISequenceMapper;
import pro.shushi.pamirs.sequence.model.LeafAlloc;

import java.util.List;

/**
 * ISequenceDao
 *
 * @author yakir on 2020/04/08 15:06.
 */
@Ds(model = LeafAlloc.MODEL_MODEL)
@Component
public class SequenceDao implements ISequenceDao {

    @Autowired
    private ISequenceMapper iSequenceMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public LeafAlloc getLeafAllocOne(LeafAlloc leafAlloc) {
        return iSequenceMapper.getLeafAlloc(leafAlloc);
    }

    @Transactional
    @Override
    public LeafAlloc getLeafAllocOneByRequired(LeafAlloc leafAlloc) {
        return iSequenceMapper.getLeafAlloc(leafAlloc);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        return iSequenceMapper.getAllLeafAllocs();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public LeafAlloc getLeafAllocAndUpdateMaxId(LeafAlloc leafAlloc) {
        LeafAlloc result = iSequenceMapper.getLeafAlloc(leafAlloc);
        if (result == null) {
            return null;
        }
        iSequenceMapper.updateMaxId(leafAlloc);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(LeafAlloc leafAlloc) {
        iSequenceMapper.updateMaxId(leafAlloc);
        return iSequenceMapper.getLeafAlloc(leafAlloc);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public LeafAlloc getLeafAllocAndUpdateMaxIdByCustomStep(LeafAlloc leafAlloc) {
        LeafAlloc result = iSequenceMapper.getLeafAlloc(leafAlloc);
        if (result == null) {
            return null;
        }
        iSequenceMapper.updateMaxIdByCustomStep(leafAlloc);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        iSequenceMapper.updateMaxIdByCustomStep(leafAlloc);
        return iSequenceMapper.getLeafAlloc(leafAlloc);
    }

    @Transactional
    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAllocByRequired(LeafAlloc leafAlloc) {
        iSequenceMapper.updateMaxIdByCustomStep(leafAlloc);
        return iSequenceMapper.getLeafAlloc(leafAlloc);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<String> getAllTags() {
        return iSequenceMapper.getAllTags();
    }
}
