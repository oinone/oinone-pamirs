package pro.shushi.pamirs.meta.constant;

/**
 * 函数编码常量
 * <p>
 * 2020/7/29 8:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public interface FunctionConstants {

    String load = "load";

    String construct = "construct";

    /* read */

    String queryByPk = "queryByPk";

    String queryByEntity = "queryByEntity";

    String queryByWrapper = "queryByWrapper";

    String queryListByEntity = "queryListByEntity";

    String queryListByEntityWithBatchSize = "queryListByEntityWithBatchSize";

    String queryListByWrapper = "queryListByWrapper";

    String queryListByPage = "queryListByPage";

    String queryListByPageAndWrapper = "queryListByPageAndWrapper";

    String queryPage = "queryPage";

    String queryFilters = "queryFilters";

    String count = "count";

    String countByWrapper = "countByWrapper";

    /* write */

    String createOne = "createOne";

    String createOrUpdate = "createOrUpdate";

    String createOrUpdateWithResult = "createOrUpdateWithResult";

    String updateByPk = "updateByPk";

    String updateByUniqueField = "updateByUniqueField";

    String updateByEntity = "updateByEntity";

    String updateByWrapper = "updateByWrapper";

    String createBatch = "createBatch";

    String createBatchWithSize = "createBatchWithSize";

    String createOrUpdateBatch = "createOrUpdateBatch";

    String createOrUpdateBatchWithResult = "createOrUpdateBatchWithResult";

    String createOrUpdateBatchWithSize = "createOrUpdateBatchWithSize";

    String createOrUpdateBatchWithSizeWithResult = "createOrUpdateBatchWithSizeWithResult";

    String updateBatch = "updateBatch";

    String updateBatchWithSize = "updateBatchWithSize";

    String deleteByPk = "deleteByPk";

    String deleteByPks = "deleteByPks";

    String deleteByUniques = "deleteByUniques";

    String deleteByUniqueField = "deleteByUniqueField";

    String deleteByEntity = "deleteByEntity";

    String deleteByWrapper = "deleteByWrapper";

    /* write with field */

    String create = "create";

    String update = "update";

    String delete = "delete";

    String createWithFieldBatch = "createWithFieldBatch";

    String updateWithFieldBatch = "updateWithFieldBatch";

    String createOrUpdateWithField = "createOrUpdateWithField";

    String createOrUpdateWithFieldBatch = "createOrUpdateWithFieldBatch";

    String deleteWithFieldBatch = "deleteWithFieldBatch";

    /* id manager */

    String queryById = "queryById";

    String updateById = "updateById";

    String deleteById = "deleteById";

    String generateId = "generateId";

    /* code manager */

    String queryByCode = "queryByCode";

    String updateByCode = "updateByCode";

    String deleteByCode = "deleteByCode";

    String generateCode = "generateCode";

    /* validate */
    String validate = "validate";

    String execute = "execute";

    String validateList = "validateList";

    String executeList = "executeList";

}
