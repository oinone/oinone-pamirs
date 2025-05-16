package pro.shushi.pamirs.auth.view.entity;

import pro.shushi.pamirs.auth.api.enmu.AuthGroupTypeEnum;
import pro.shushi.pamirs.auth.api.enmu.PermissionMateDataEnum;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;

import java.util.Arrays;
import java.util.List;

public class AuthGroupName {

    private static final int MIN_RESOLVE_NAMES_LENGTH = 3;

    private static final List<AuthGroupTypeEnum> SUPPORTED_TYPE = Arrays.asList(
            AuthGroupTypeEnum.RUNTIME,
            AuthGroupTypeEnum.MANAGEMENT
    );

    private static final List<PermissionMateDataEnum> SUPPORTED_METADATA_TYPE = Arrays.asList(
            PermissionMateDataEnum.MODULE,
            PermissionMateDataEnum.HOMEPAGE,
            PermissionMateDataEnum.MENU
    );

    private final AuthGroupTypeEnum type;

    private final PermissionMateDataEnum metadataType;

    private String module;

    private String name;

    public AuthGroupName(AuthGroupTypeEnum type, PermissionMateDataEnum metadataType) {
        this.type = type;
        this.metadataType = metadataType;
    }

    public AuthGroupTypeEnum getType() {
        return type;
    }

    public PermissionMateDataEnum getMetadataType() {
        return metadataType;
    }

    public String getModule() {
        return module;
    }

    private void setModule(String module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public static AuthGroupName resolveGroupName(String groupName) {
        String[] names = groupName.split(CharacterConstants.SEPARATOR_OCTOTHORPE);
        if (names.length < MIN_RESOLVE_NAMES_LENGTH) {
            return null;
        }
        PermissionMateDataEnum metadataType = resolveMetadataType(names[0]);
        if (metadataType == null) {
            return null;
        }
        AuthGroupTypeEnum type = resolveType(names[1]);
        if (type == null) {
            return null;
        }
        AuthGroupName name = new AuthGroupName(type, metadataType);
        if (resolveMetadata(name, groupName.substring(names[0].length() + names[1].length() + 2))) {
            return name;
        }
        return null;
    }

    private static AuthGroupTypeEnum resolveType(String name) {
        for (AuthGroupTypeEnum type : SUPPORTED_TYPE) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    private static PermissionMateDataEnum resolveMetadataType(String name) {
        for (PermissionMateDataEnum type : SUPPORTED_METADATA_TYPE) {
            if (type.name().equals(name)) {
                return type;
            }
        }
        return null;
    }

    private static boolean resolveMetadata(AuthGroupName name, String key) {
        if (PermissionMateDataEnum.MENU.equals(name.getMetadataType())) {
            int firstSplit = key.indexOf(CharacterConstants.SEPARATOR_OCTOTHORPE);
            if (firstSplit == -1) {
                return false;
            }
            String module = key.substring(0, firstSplit);
            String menuName = key.substring(firstSplit + 1);
            name.setModule(module);
            name.setName(menuName);
        } else {
            name.setModule(key);
        }
        return true;
    }
}