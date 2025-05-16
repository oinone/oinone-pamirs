package pro.shushi.pamirs.bizauth.core.spi;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.bizauth.api.session.BusinessCodeSession;
import pro.shushi.pamirs.business.api.entity.PamirsCompany;
import pro.shushi.pamirs.business.api.enumeration.BusinessExpEnumerate;
import pro.shushi.pamirs.business.api.model.PamirsEmployee;
import pro.shushi.pamirs.business.api.service.PamirsEmployeeService;
import pro.shushi.pamirs.business.api.service.entity.PamirsCompanyService;
import pro.shushi.pamirs.core.common.business.spi.ControlShowPartnersApi;
import pro.shushi.pamirs.core.common.business.tmodel.CurrentPartner;
import pro.shushi.pamirs.core.common.business.tmodel.ShowPartners;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Order(88)
@Component
@SPI.Service
public class ControlShowPartnersApiImpl implements ControlShowPartnersApi {

    @Autowired
    private PamirsEmployeeService pamirsEmployeeService;

    @Autowired
    private PamirsCompanyService pamirsCompanyService;

    /*
    查询全部公司
     */
    @Override
    public ShowPartners queryAllPartners() {
        Long userId = PamirsSession.getUserId();
        String companyCode = BusinessCodeSession.getCode();

        if (userId == null) {
            return new ShowPartners();
        }

        List<CurrentPartner> currentPartnerList = getCurrentPartnerList(userId);
        if (CollectionUtils.isEmpty(currentPartnerList)) {
            return new ShowPartners();
        }

        //如果companyCode为空，则返回列表里的第一个公司,否则，返回companyCode指向的公司
        if (StringUtils.isBlank(companyCode)) {
            CurrentPartner currentPartner = new CurrentPartner();
            currentPartner.setCode(currentPartnerList.get(0).getCode());
            currentPartner.setName(currentPartnerList.get(0).getName());

            //移除列表里当前展示的公司
            currentPartnerList.remove(0);

            ShowPartners showPartners = new ShowPartners();
            showPartners.setPartner(currentPartner);
            showPartners.setPartnerList(currentPartnerList);
            return showPartners;
        } else {
            //填值：返回companyCode指向的公司
            CurrentPartner currentPartner = new CurrentPartner();
            currentPartner.setCode(companyCode);
            String name = pamirsCompanyService.queryOneByWrapper(Pops.<PamirsCompany>lambdaQuery()
                            .from(PamirsCompany.MODEL_MODEL)
                            .eq(PamirsCompany::getCode, companyCode))
                    .getName();
            //判断name是否为空,若为空，抛出异常
            if (StringUtils.isBlank(name)) {
                throw PamirsException.construct(BusinessExpEnumerate.COMPANY_NOT_FIND).errThrow();
            }
            currentPartner.setName(name);
            ShowPartners showPartners = new ShowPartners();
            showPartners.setPartner(currentPartner);

            //移除列表里当前展示的公司
            Iterator<CurrentPartner> iterator = currentPartnerList.iterator();
            while (iterator.hasNext()) {
                CurrentPartner existingPartner = iterator.next();
                // 检查内容是否相同但引用不同的对象
                if (existingPartner.getCode().equals(currentPartner.getCode()) &&
                        existingPartner.getName().equals(currentPartner.getName())) {
                    iterator.remove();
                    break;
                }
            }

            showPartners.setPartnerList(currentPartnerList);
            return showPartners;
        }
    }

    /*
    点击切换合作伙伴
     */
    @Override
    public ShowPartners changePartner(CurrentPartner currentPartner) {
        ShowPartners showPartner = queryAllPartners();
        ShowPartners showPartners = new ShowPartners();
        showPartners.setPartner(currentPartner);
        showPartners.setPartnerList(showPartner.getPartnerList());
        return showPartners;
    }

    protected List<CurrentPartner> getCurrentPartnerList(Long userId) {
        //通过userid拿到员工列表 查employee表获取多个员工
        List<PamirsEmployee> pamirsEmployeeList = pamirsEmployeeService.queryListByUid(userId);
        if (CollectionUtils.isEmpty(pamirsEmployeeList)) {
            return null;
        }

        //通过员工列表去拿到公司列表
        List<String> collect = pamirsEmployeeList.stream()
                .map(PamirsEmployee::getCompanyCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }

        List<PamirsCompany> pamirsCompanies = pamirsCompanyService.queryListByWrapper(Pops.<PamirsCompany>lambdaQuery()
                .from(PamirsCompany.MODEL_MODEL)
                .in(PamirsCompany::getCode, collect));

        if (CollectionUtils.isEmpty(pamirsCompanies)) {
            return null;
        }

        //获取 List<CurrentPartner>
        List<CurrentPartner> currentPartnerList = new ArrayList<>();
        for (int i = 0; i < pamirsCompanies.size(); i++) {
            CurrentPartner cp = new CurrentPartner();
            cp.setCode(pamirsCompanies.get(i).getCode());
            cp.setName(pamirsCompanies.get(i).getName());
            currentPartnerList.add(cp);
        }
        return currentPartnerList;
    }
}
