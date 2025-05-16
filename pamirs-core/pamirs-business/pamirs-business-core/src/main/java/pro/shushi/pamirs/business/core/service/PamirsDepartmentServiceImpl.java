package pro.shushi.pamirs.business.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.business.api.BusinessModule;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsDepartment;
import pro.shushi.pamirs.business.api.model.PamirsPosition;
import pro.shushi.pamirs.business.api.service.PamirsDepartmentService;
import pro.shushi.pamirs.core.common.behavior.impl.TreeCodeBehavior;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.enmu.SequenceEnum;

import java.time.Instant;
import java.util.List;

/**
 * {@link PamirsDepartmentService}实现
 *
 * @author Adamancy Zhang at 09:46 on 2021-08-31
 */
@Service
@Fun(PamirsDepartmentService.FUN_NAMESPACE)
public class PamirsDepartmentServiceImpl implements PamirsDepartmentService {

    @Function
    @Override
    public PamirsDepartment create(PamirsDepartment data) {
        if (StringUtils.isBlank(data.getCompanyCode())) {
            throw PamirsException.construct(BusinessExpEnumerate.COMPANY_CODE_EMPTY).errThrow();
        }
        if (null == data.getId()) {
            data.setPriority(Instant.now().toEpochMilli());
        }
        data.setPriority(Instant.now().toEpochMilli());

        if (StringUtils.isBlank(data.getDepartmentType())) {
            data.setDepartmentType(BusinessModule.DEFAULT_TYPE);
        }
        if (StringUtils.isBlank(data.getCode())) {
            data.setCode(CommonApiFactory.<String>getSequenceGenerator().generate(SequenceEnum.SEQ.value(), PamirsDepartment.MODEL_MODEL));
        }
        if (StringUtils.isNotBlank(data.getParentCode())) {
            PamirsDepartment dbDepartment = new PamirsDepartment().setCode(data.getParentCode()).queryByCode();
            if (StringUtils.isNotBlank(dbDepartment.getTreeCode())) {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getTreeCode(), data.getCode()));
            } else {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getCode(), data.getCode()));
            }
        } else {
            data.setTreeCode(data.getCode());
        }

        if(CollectionUtils.isNotEmpty(data.getPositionList())){
            data.getPositionList().forEach(K2::construct);
        }
        data.fieldSave(PamirsDepartment::getPositionList);
        return data.create();
    }

    @Override
    public PamirsDepartment queryOne(PamirsDepartment data) {
        PamirsDepartment department = data.queryById(data.getId());
        if (department == null) {
            return data;
        }
        department = department.fieldQuery(PamirsDepartment::getParent);
        department = department.fieldQuery(PamirsDepartment::getCompany);
        department = department.fieldQuery(PamirsDepartment::getPositionList);
        department = department.fieldQuery(PamirsDepartment::getEmployeeList);
        return department;
    }

    @Override
    public void update(PamirsDepartment data) {
        PamirsDepartment exist = this.queryOne(data);
        if (StringUtils.isNotBlank(data.getParentCode())) {
            PamirsDepartment dbDepartment = new PamirsDepartment().setCode(data.getParentCode()).queryByCode();
            if (StringUtils.isNotBlank(dbDepartment.getTreeCode())) {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getTreeCode(), data.getCode()));
            } else {
                data.setTreeCode(TreeCodeBehavior.concat(dbDepartment.getCode(), data.getCode()));
            }
        } else {
            data.setTreeCode(data.getCode());
        }

        data.updateById();

        if(CollectionUtils.isNotEmpty(data.getPositionList())){
//            data.getPositionList().stream().filter(i -> i.getId() == null).forEach(K2::construct);
            exist = exist.fieldQuery(PamirsDepartment::getPositionList);
            if(CollectionUtils.isNotEmpty(data.getPositionList())) {
                exist.relationDelete(PamirsDepartment::getPositionList);
            }
            List<PamirsPosition> positionList = data.getPositionList();
            String code = exist.getCode();
            positionList.forEach(t->t.setDepartmentCode(code));
            new PamirsPosition().createOrUpdateBatch(positionList);
//            data.fieldSave(PamirsDepartment::getPositionList);
        }
    }

    @Function
    @Override
    public void deleteByPk(PamirsDepartment data) {
        data.deleteByPk();
    }

    @Function
    @Override
    public void deleteByPks(List<PamirsDepartment> list) {
        new PamirsDepartment().deleteByPks(list);
    }

    @Function
    @Override
    public Pagination<PamirsDepartment> queryPage(Pagination<PamirsDepartment> page, IWrapper<PamirsDepartment> queryWrapper) {
        return new PamirsDepartment().queryPage(page, queryWrapper);
    }


}
