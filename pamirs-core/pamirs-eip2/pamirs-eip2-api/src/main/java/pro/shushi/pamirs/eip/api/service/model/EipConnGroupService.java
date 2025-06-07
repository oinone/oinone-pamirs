package pro.shushi.pamirs.eip.api.service.model;

import pro.shushi.pamirs.eip.api.model.EipConnGroup;

import java.util.List;


public interface EipConnGroupService {

    <T extends EipConnGroup> T create(T data);

    <T extends EipConnGroup> List<T> delete(List<T> list);

    <T extends EipConnGroup> T deleteOne(T data);

}
