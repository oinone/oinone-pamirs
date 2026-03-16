package pro.shushi.pamirs.file.api.template;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.core.common.MapHelper;
import pro.shushi.pamirs.file.api.builder.WorkbookDefinitionBuilder;
import pro.shushi.pamirs.file.api.context.ExcelDefinitionContext;
import pro.shushi.pamirs.file.api.enmu.*;
import pro.shushi.pamirs.file.api.entity.EasyExcelBlockDefinition;
import pro.shushi.pamirs.file.api.entity.EasyExcelSheetDefinition;
import pro.shushi.pamirs.file.api.extpoint.AbstractExcelExportFetchDataExtPointImpl;
import pro.shushi.pamirs.file.api.model.ExcelExportTask;
import pro.shushi.pamirs.file.api.model.ExcelWorkbookDefinition;
import pro.shushi.pamirs.file.api.util.ExcelHelper;
import pro.shushi.pamirs.file.api.util.ExcelTemplateInit;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Ext;
import pro.shushi.pamirs.meta.annotation.ExtPoint;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.base.manager.data.DataManager;
import pro.shushi.pamirs.resource.api.model.*;
import pro.shushi.pamirs.resource.api.tmodel.ResourceRegionProxyModel;

import java.util.*;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * {@link pro.shushi.pamirs.resource.api.model.ResourceRegion}导出模板
 *
 * @author Adamancy Zhang on 2021-06-08 15:45
 */
@Ext(ExcelExportTask.class)
@Component
public class ResourceRegionExportTemplate extends AbstractExcelExportFetchDataExtPointImpl implements ExcelTemplateInit {

    @Override
    public List<ExcelWorkbookDefinition> generator() {
        return Collections.singletonList(WorkbookDefinitionBuilder.newInstance(ResourceRegionProxyModel.MODEL_MODEL, "ResourceRegionExportTemplate")
                .setType(ExcelTemplateTypeEnum.EXPORT)
                .createSheet().setName("国家")
                .createBlock(ResourceCountry.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 14)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("code").and()
                .createCell().setField("name").and()
                .createCell().setField("completeName").and()
                .createCell().setField("vatLabel").setType(ExcelValueTypeEnum.ENUMERATION)
                .setFormat(JSON.toJSONString(MapHelper.newInstance()
                        .put("VAT", "VAT")
                        .put("GST", "GST")
                        .build())).and()
                .createCell().setField("phoneCode").and()
                .createCell().setField("currency.code").and()
                .createCell().setField("currency.name").and()
                .createCell().setField("currency.symbol").and()
                .createCell().setField("lang.code").and()
                .createCell().setField("lang.name").and()
                .createCell().setField("countryGroup.code").and()
                .createCell().setField("countryGroup.name").and()
                .createCell().setField("addrFormat").and()
                .createCell().setField("namePosition").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("编码").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("全称").and()
                .createCell().setValue("消费税").and()
                .createCell().setValue("长途区号").and()
                .createCell().setValue("货币编码").and()
                .createCell().setValue("货币名称").and()
                .createCell().setValue("货币符号").and()
                .createCell().setValue("语言编码").and()
                .createCell().setValue("语言名称").and()
                .createCell().setValue("洲编码").and()
                .createCell().setValue("洲名称").and()
                .createCell().setValue("地址显示格式").and()
                .createCell().setValue("姓名显示规则").and()
                .and().and().and()


                .createSheet().setName("省")
                .createBlock(ResourceProvince.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 4)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("sourceType").and()
                .createCell().setField("name").and()
                .createCell().setField("country.code").and()
                .createCell().setField("country.name").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("来源类型").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("国家编码").and()
                .createCell().setValue("国家名称").and()
                .and().and().and()
                .createSheet().setName("市")
                .createBlock(ResourceCity.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 8)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("sourceType").and()
                .createCell().setField("name").and()
                .createCell().setField("phoneCode").and()
                .createCell().setField("zipCode").and()
                .createCell().setField("country.code").and()
                .createCell().setField("country.name").and()
                .createCell().setField("province.code").and()
                .createCell().setField("province.name").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("来源类型").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("长途区号").and()
                .createCell().setValue("邮政编码").and()
                .createCell().setValue("国家编码").and()
                .createCell().setValue("国家名称").and()
                .createCell().setValue("省编码").and()
                .createCell().setValue("省名称").and()
                .and().and().and()
                .createSheet().setName("区")
                .createBlock(ResourceDistrict.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 9)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("sourceType").and()
                .createCell().setField("name").and()
                .createCell().setField("zipCode").and()
                .createCell().setField("country.code").and()
                .createCell().setField("country.name").and()
                .createCell().setField("province.code").and()
                .createCell().setField("province.name").and()
                .createCell().setField("city.code").and()
                .createCell().setField("city.name").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("来源类型").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("邮政编码").and()
                .createCell().setValue("国家编码").and()
                .createCell().setValue("国家名称").and()
                .createCell().setValue("省编码").and()
                .createCell().setValue("省名称").and()
                .createCell().setValue("市编码").and()
                .createCell().setValue("市名称").and()
                .and().and().and()
                .createSheet().setName("街道")
                .createBlock(ResourceStreet.MODEL_MODEL, ExcelAnalysisTypeEnum.FIXED_HEADER, ExcelDirectionEnum.HORIZONTAL,
                        0, 1, 0, 10)
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle()).setIsConfig(Boolean.TRUE)
                .createCell().setField("sourceType").and()
                .createCell().setField("name").and()
                .createCell().setField("phoneCode").and()
                .createCell().setField("zipCode").and()
                .createCell().setField("country.code").and()
                .createCell().setField("country.name").and()
                .createCell().setField("province.code").and()
                .createCell().setField("province.name").and()
                .createCell().setField("city.code").and()
                .createCell().setField("city.name").and()
                .createCell().setField("district.code").and()
                .createCell().setField("district.name").and()
                .and()
                .createHeader().setStyleBuilder(ExcelHelper.createDefaultStyle(typeface -> typeface.setBold(Boolean.TRUE)).setHorizontalAlignment(ExcelHorizontalAlignmentEnum.CENTER))
                .createCell().setValue("来源类型").and()
                .createCell().setValue("名称").and()
                .createCell().setValue("长途区号").and()
                .createCell().setValue("邮政编码").and()
                .createCell().setValue("国家编码").and()
                .createCell().setValue("国家名称").and()
                .createCell().setValue("省编码").and()
                .createCell().setValue("省名称").and()
                .createCell().setValue("区/县编码").and()
                .createCell().setValue("区/县名称").and()
                .and().and().and()
                .build());
    }

    @Override
    @ExtPoint.Implement(expression = "context.model == \"" + ResourceRegionProxyModel.MODEL_MODEL + "\"", priority = 998)
    public List<Object> fetchExportData(ExcelExportTask exportTask, ExcelDefinitionContext context) {
        return super.fetchExportData(exportTask, context);
    }

    @Override
    protected List<Object> queryList(ExcelExportTask exportTask, ExcelDefinitionContext context, EasyExcelBlockDefinition blockDefinition, IWrapper<?> wrapper) {
        ExcelWorkbookDefinition workbookDefinition = exportTask.getWorkbookDefinition();
        List<ResourceRegion> regionDataList = cast(Models.data().queryListByWrapper(wrapper.setModel(ResourceRegion.MODEL_MODEL)));
        List<Object> finalResult = new ArrayList<>();
        List<String> countryCodes = new ArrayList<>();
        List<String> provinceCodes = new ArrayList<>();
        List<String> cityCodes = new ArrayList<>();
        List<String> districtCodes = new ArrayList<>();
        List<List<String>> streetCodeLists = new ArrayList<>();
        List<String> streetCodes = new ArrayList<>();
        for (ResourceRegion item : regionDataList) {
            switch (item.getType()) {
                case Country:
                    countryCodes.add(item.getCode());
                    break;
                case Province:
                    provinceCodes.add(item.getCode());
                    break;
                case City:
                    cityCodes.add(item.getCode());
                    break;
                case District:
                    districtCodes.add(item.getCode());
                    break;
                case Street:
                    streetCodes.add(item.getCode());
                    if (streetCodes.size() == 1000) {
                        streetCodeLists.add(streetCodes);
                        streetCodes = new ArrayList<>();
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Invalid region type.");
            }
        }
        if (!streetCodes.isEmpty()) {
            streetCodeLists.add(streetCodes);
        }
        DataManager dataManager = Models.data();
        List<EasyExcelSheetDefinition> sheetDefinitionList = context.getSheetList();
        boolean clearExportStyle = fetchClearExportStyle(workbookDefinition);
        int maxSupportLength = fetchMaxSupportLength(workbookDefinition, clearExportStyle);

        //国家
        List<ResourceCountry> countryList;
        if (CollectionUtils.isNotEmpty(countryCodes)) {
            countryList = dataManager.queryListByWrapper(Pops.<ResourceCountry>lambdaQuery()
                    .from(ResourceCountry.MODEL_MODEL)
                    .in(ResourceCountry::getCode, countryCodes));
            selectRelationField(dataManager, ResourceCountry.MODEL_MODEL, sheetDefinitionList.get(0).getBlockDefinitions().get(0).getFieldNodeList(), countryList, maxSupportLength, countryList.size());
        } else {
            countryList = Collections.emptyList();
        }
        finalResult.add(countryList);

        //省
        List<ResourceProvince> provinceList;
        if (CollectionUtils.isNotEmpty(provinceCodes)) {
            provinceList = dataManager.queryListByWrapper(Pops.<ResourceProvince>lambdaQuery()
                    .from(ResourceProvince.MODEL_MODEL)
                    .in(ResourceProvince::getCode, provinceCodes));
            selectRelationField(dataManager, ResourceProvince.MODEL_MODEL, sheetDefinitionList.get(1).getBlockDefinitions().get(0).getFieldNodeList(), provinceList, maxSupportLength, provinceList.size());
        } else {
            provinceList = Collections.emptyList();
        }
        finalResult.add(provinceList);

        //市
        List<ResourceCity> cityList;
        if (CollectionUtils.isNotEmpty(cityCodes)) {
            cityList = dataManager.queryListByWrapper(Pops.<ResourceCity>lambdaQuery()
                    .from(ResourceCity.MODEL_MODEL)
                    .in(ResourceCity::getCode, cityCodes));
            selectRelationField(dataManager, ResourceCity.MODEL_MODEL, sheetDefinitionList.get(2).getBlockDefinitions().get(0).getFieldNodeList(), cityList, maxSupportLength, cityList.size());
        } else {
            cityList = Collections.emptyList();
        }
        finalResult.add(cityList);

        //区/县
        List<ResourceDistrict> districtList;
        if (CollectionUtils.isNotEmpty(districtCodes)) {
            districtList = dataManager.queryListByWrapper(Pops.<ResourceDistrict>lambdaQuery()
                    .from(ResourceDistrict.MODEL_MODEL)
                    .in(ResourceDistrict::getCode, districtCodes));
            selectRelationField(dataManager, ResourceDistrict.MODEL_MODEL, sheetDefinitionList.get(3).getBlockDefinitions().get(0).getFieldNodeList(), districtList, maxSupportLength, districtList.size());
        } else {
            districtList = Collections.emptyList();
        }
        finalResult.add(districtList);

        //街道
        List<ResourceStreet> streetList = new ArrayList<>();
        for (List<String> streetCodesItem : streetCodeLists) {
            if (CollectionUtils.isNotEmpty(streetCodesItem)) {
                streetList.addAll(dataManager.queryListByWrapper(Pops.<ResourceStreet>lambdaQuery()
                        .from(ResourceStreet.MODEL_MODEL)
                        .in(ResourceStreet::getCode, streetCodesItem)));
            }
        }
        if (!streetList.isEmpty()) {
            selectRelationField(dataManager, ResourceStreet.MODEL_MODEL, sheetDefinitionList.get(4).getBlockDefinitions().get(0).getFieldNodeList(), streetList, maxSupportLength, streetList.size());
        }
        finalResult.add(streetList);
        matchAll(countryList, provinceList, cityList, districtList, streetList);
        return finalResult;
    }

    private void matchAll(List<ResourceCountry> countryList, List<ResourceProvince> provinceList, List<ResourceCity> cityList, List<ResourceDistrict> districtList, List<ResourceStreet> streetList) {
        Map<String, ResourceCountry> countryCache = new HashMap<>();
        Map<String, ResourceProvince> provinceCache = new HashMap<>();
        Map<String, ResourceCity> cityCache = new HashMap<>();
        Map<String, ResourceDistrict> districtCache = new HashMap<>();
        for (ResourceCountry country : countryList) {
            countryCache.put(country.getCode(), country);
        }
        for (ResourceProvince province : provinceList) {
            provinceCache.put(province.getCode(), province);
            province.setCountry(countryCache.get(province.getCountryCode()));
        }
        for (ResourceCity city : cityList) {
            cityCache.put(city.getCode(), city);
            city.setCountry(countryCache.get(city.getCountryCode()));
            city.setProvince(provinceCache.get(city.getProvinceCode()));
        }
        for (ResourceDistrict district : districtList) {
            districtCache.put(district.getCode(), district);
            district.setCountry(countryCache.get(district.getCountryCode()));
            district.setProvince(provinceCache.get(district.getProvinceCode()));
            district.setCity(cityCache.get(district.getCityCode()));
        }
        for (ResourceStreet street : streetList) {
            street.setCountry(countryCache.get(street.getCountryCode()));
            street.setProvince(provinceCache.get(street.getProvinceCode()));
            street.setCity(cityCache.get(street.getCityCode()));
            street.setDistrict(districtCache.get(street.getDistrictCode()));
        }
    }
}
