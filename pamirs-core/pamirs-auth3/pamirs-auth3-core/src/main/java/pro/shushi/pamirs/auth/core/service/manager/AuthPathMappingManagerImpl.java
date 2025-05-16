package pro.shushi.pamirs.auth.core.service.manager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.auth.api.cache.service.AuthPathMappingCacheService;
import pro.shushi.pamirs.auth.api.entity.node.PermissionNode;
import pro.shushi.pamirs.auth.api.enumeration.AuthExpEnumerate;
import pro.shushi.pamirs.auth.api.enumeration.ResourcePermissionSubtypeEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.loader.PermissionNodeLoader;
import pro.shushi.pamirs.auth.api.loader.ResourcePermissionNodeLoader;
import pro.shushi.pamirs.auth.api.model.AuthPathMapping;
import pro.shushi.pamirs.auth.api.service.AuthPathMappingService;
import pro.shushi.pamirs.auth.api.service.manager.AuthPathMappingManager;
import pro.shushi.pamirs.core.common.DataShardingHelper;
import pro.shushi.pamirs.core.common.StringHelper;
import pro.shushi.pamirs.core.common.cache.MemoryListSearchCache;
import pro.shushi.pamirs.core.common.diff.DiffCollection;
import pro.shushi.pamirs.core.common.diff.DiffList;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.Fun;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;

/**
 * @author Adamancy Zhang at 16:28 on 2024-03-25
 */
@Service
@Fun(AuthPathMappingManager.FUN_NAMESPACE)
public class AuthPathMappingManagerImpl implements AuthPathMappingManager {

    @Autowired
    private AuthPathMappingService authPathMappingService;

    @Autowired
    private PermissionNodeLoader permissionNodeLoader;

    @Transactional(rollbackFor = Throwable.class)
    @Function
    @Override
    public List<AuthPathMapping> collectionPathMappings(List<AuthPathMapping> pathMappings) {
        Set<String> codes = verificationAndSetCodes(pathMappings);

        List<AuthPathMapping> existPathMappings = DataShardingHelper.build().collectionSharding(codes, (sublist) -> authPathMappingService.queryListByWrapper(Pops.<AuthPathMapping>lambdaQuery()
                .from(AuthPathMapping.MODEL_MODEL)
                .setBatchSize(-1)
                .select(AuthPathMapping::getId, AuthPathMapping::getCode, AuthPathMapping::getPermissionNodeId, AuthPathMapping::getPermissionNodeType, AuthPathMapping::getOriginPath)
                .in(AuthPathMapping::getCode, sublist)));

        DiffList<AuthPathMapping> diffPathMappings = savePathMappings(pathMappings, existPathMappings);

        refreshPathMappingCache(diffPathMappings);

        return diffPathMappings.getAll();
    }

    private Set<String> verificationAndSetCodes(List<AuthPathMapping> pathMappings) {
        Set<String> codes = new HashSet<>(pathMappings.size());
        for (AuthPathMapping pathMapping : pathMappings) {
            String path = pathMapping.getOriginPath();
            assertPath(path);
            assertPermissionNodeId(pathMapping.getPermissionNodeId());
            assertPermissionNodeType(pathMapping.getPermissionNodeType());
            String code = pathMapping.getCode();
            if (StringUtils.isBlank(code)) {
                code = AuthPathMapping.generatorCode(path);
                pathMapping.setCode(code);
            }
            codes.add(code);
        }
        return codes;
    }

//    private List<AuthPathMapping> verificationAndSetCodes(List<AuthPathMapping> pathMappings) {
//        Set<String> codes = new HashSet<>(pathMappings.size());
//        Map<String, Set<Long>> queryMap = new HashMap<>(4);
//        Map<String, AuthPathMapping> lazyPathMappingMap = new HashMap<>(8);
//        Iterator<AuthPathMapping> pathMappingIterator = pathMappings.iterator();
//        while (pathMappingIterator.hasNext()) {
//            AuthPathMapping pathMapping = pathMappingIterator.next();
//            String resModel = assertResModel(pathMapping.getResModel());
//            Long resId = assertResId(pathMapping.getResId());
//            queryMap.computeIfAbsent(resModel, k -> new HashSet<>(8)).add(resId);
//            String path = pathMapping.getOriginPath();
//            if (StringUtils.isBlank(path)) {
//                lazyPathMappingMap.put(generatorUniqueKey(resModel, resId), pathMapping);
//            } else {
//                String code = pathMapping.getCode();
//                if (StringUtils.isBlank(code)) {
//                    code = AuthPathMapping.generatorCode(path);
//                    pathMapping.setCode(code);
//                }
//                if (ObjectHelper.isRepeat(codes, code)) {
//                    pathMappingIterator.remove();
//                }
//            }
//        }
//        List<AuthPathMapping> existPathMappings = new ArrayList<>(pathMappings.size());
//        for (Map.Entry<String, Set<Long>> entry : queryMap.entrySet()) {
//            String resModel = entry.getKey();
//            existPathMappings.addAll(DataShardingHelper.build().collectionSharding(entry.getValue(), (sublist) -> authPathMappingService.queryListByWrapper(Pops.<AuthPathMapping>lambdaQuery()
//                    .from(AuthPathMapping.MODEL_MODEL)
//                    .setBatchSize(-1)
//                    .select(AuthPathMapping::getId, AuthPathMapping::getCode, AuthPathMapping::getResModel, AuthPathMapping::getResId, AuthPathMapping::getOriginPath)
//                    .eq(AuthPathMapping::getResModel, resModel)
//                    .in(AuthPathMapping::getResId, sublist))));
//        }
//        MemoryListSearchCache<String, AuthPathMapping> existPathMappingCache = new MemoryListSearchCache<>(existPathMappings, v -> generatorUniqueKey(v.getResModel(), v.getResId()));
//        for (Map.Entry<String, AuthPathMapping> entry : lazyPathMappingMap.entrySet()) {
//            AuthPathMapping existPathMapping = existPathMappingCache.get(entry.getKey());
//            if (existPathMapping == null) {
//                throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ERROR).errThrow();
//            }
//            AuthPathMapping pathMapping = entry.getValue();
//            String code = existPathMapping.getCode();
//            pathMapping.setCode(code);
//            pathMapping.setOriginPath(existPathMapping.getOriginPath());
//        }
//        return existPathMappings;
//    }

    private DiffList<AuthPathMapping> savePathMappings(List<AuthPathMapping> pathMappings, List<AuthPathMapping> existPathMappings) {
        MemoryListSearchCache<String, AuthPathMapping> existPathMappingCache = new MemoryListSearchCache<>(existPathMappings, AuthPathMapping::getCode);
        List<AuthPathMapping> createPathMappings = new ArrayList<>();
        List<AuthPathMapping> updatePathMappings = new ArrayList<>();
        List<AuthPathMapping> refreshCreatePathMappings = new ArrayList<>();
        List<AuthPathMapping> refreshUpdatePathMappings = new ArrayList<>();
        List<AuthPathMapping> refreshDeletePathMappings = new ArrayList<>();
        for (AuthPathMapping pathMapping : pathMappings) {
            AuthPathMapping existPathMapping = existPathMappingCache.get(pathMapping.getCode());
            if (existPathMapping == null) {
                createPathMappings.add(pathMapping);
            } else {
                pathMapping.setId(existPathMapping.getId());
                pathMapping.setCode(existPathMapping.getCode());
                updatePathMappings.add(pathMapping);

                if (StringHelper.equals(pathMapping.getOriginPath(), existPathMapping.getOriginPath())) {
                    refreshUpdatePathMappings.add(pathMapping);
                } else {
                    refreshDeletePathMappings.add(existPathMapping);
                    refreshCreatePathMappings.add(pathMapping);
                }
            }
            loadTargetPathList(pathMapping);
        }
        if (!createPathMappings.isEmpty()) {
            List<AuthPathMapping> createdPathMappings = authPathMappingService.createBatch(createPathMappings);
            refreshCreatePathMappings.addAll(createdPathMappings);
        }
        if (!updatePathMappings.isEmpty()) {
            authPathMappingService.updateBatch(updatePathMappings);
        }
        return DiffCollection.list(pathMappings, refreshCreatePathMappings, refreshUpdatePathMappings, refreshDeletePathMappings);
    }

    private void loadTargetPathList(AuthPathMapping pathMapping) {
        List<String> targetPathList = pathMapping.getTargetPathList();
        Set<String> targetPathSet;
        if (targetPathList == null) {
            targetPathSet = new HashSet<>();
        } else {
            targetPathSet = new HashSet<>(targetPathList);
        }
        ResourcePermissionNodeLoader loader = permissionNodeLoader.getManagementLoader();
        PermissionNode node = pathMapping.parseNode();
        if (node == null) {
            return;
        }
        List<PermissionNode> nodes = loader.buildNextPermissions(node);
        if (CollectionUtils.isNotEmpty(nodes)) {
            targetPathSet.addAll(resolvePermissionNodes(nodes));
        }
        pathMapping.setTargetPathList(new ArrayList<>(targetPathSet));
    }

    private Set<String> resolvePermissionNodes(List<PermissionNode> nodes) {
        Set<String> targetPaths = new HashSet<>(nodes.size());
        for (PermissionNode node : nodes) {
            targetPaths.add(node.getPath());
            List<PermissionNode> children = node.getNodes();
            if (CollectionUtils.isNotEmpty(children)) {
                targetPaths.addAll(resolvePermissionNodes(children));
            }
        }
        return targetPaths;
    }

    private void refreshPathMappingCache(DiffList<AuthPathMapping> diffPathMappings) {
        AuthPathMappingCacheService cacheService = AuthApiHolder.getAuthPathMappingCacheService();
        Map<String, Set<String>> createPathMappings = collectionPathMapping(diffPathMappings.getCreate());
        Map<String, Set<String>> updatePathMappings = collectionPathMapping(diffPathMappings.getUpdate());
        Map<String, Set<String>> deletePathMappings = collectionPathMapping(diffPathMappings.getDelete());
        if (!createPathMappings.isEmpty()) {
            cacheService.add(createPathMappings);
        }
        if (!updatePathMappings.isEmpty()) {
            cacheService.set(updatePathMappings);
        }
        if (!deletePathMappings.isEmpty()) {
            cacheService.delete(deletePathMappings.keySet());
        }
    }

    private Map<String, Set<String>> collectionPathMapping(List<AuthPathMapping> pathMappings) {
        Map<String, Set<String>> cacheMap = new HashMap<>(pathMappings.size());
        for (AuthPathMapping pathMapping : pathMappings) {
            List<String> targetPathList = pathMapping.getTargetPathList();
            if (targetPathList != null) {
                cacheMap.computeIfAbsent(pathMapping.getCode(), k -> new HashSet<>()).addAll(targetPathList);
            }
        }
        return cacheMap;
    }

    private void assertPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_PATH_ERROR).errThrow();
        }
    }

    private void assertPermissionNodeId(String permissionNodeId) {
        if (StringUtils.isBlank(permissionNodeId)) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_ID_ERROR).errThrow();
        }
    }

    private void assertPermissionNodeType(ResourcePermissionSubtypeEnum nodeType) {
        if (nodeType == null) {
            throw PamirsException.construct(AuthExpEnumerate.AUTH_INVALID_RESOURCE_TYPE_ERROR).errThrow();
        }
    }
}
