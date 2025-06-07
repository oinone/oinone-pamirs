package pro.shushi.pamirs.sequence.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.sequence.common.Result;
import pro.shushi.pamirs.sequence.manager.LeafSegment;

import javax.annotation.Nonnull;

/**
 * LeafSegmentService
 *
 * @author yakir on 2020/04/08 16:17.
 */
@Service
@DependsOn("leafSegment")
public class LeafSegmentService {

    private static final Logger log = LoggerFactory.getLogger(LeafSegmentService.class);

    @Autowired
    private LeafSegment leafSegment;

    public Result getId(@Nonnull String key) {
        return leafSegment.get(key, 1);
    }

    public Result getId(@Nonnull String key, int rstep) {
        return leafSegment.get(key, rstep);
    }

    public Result getOrderId(@Nonnull String key, @Nonnull Integer step) {
        return leafSegment.getOrderID(key, step);
    }

    public LeafSegment getLeaf() {
        return leafSegment;
    }
}
