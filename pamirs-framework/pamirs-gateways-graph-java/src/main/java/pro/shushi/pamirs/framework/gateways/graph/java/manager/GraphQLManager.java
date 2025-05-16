package pro.shushi.pamirs.framework.gateways.graph.java.manager;

import graphql.schema.*;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import graphql.util.TreeTransformerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.GraphQLSdlConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.request.DefaultRequestExecutor;
import pro.shushi.pamirs.framework.gateways.graph.java.service.QueryAndMutationBinder;
import pro.shushi.pamirs.framework.gateways.graph.java.service.SummaryGenerator;
import pro.shushi.pamirs.framework.gateways.graph.java.session.GraphqlEnumContext;
import pro.shushi.pamirs.framework.gateways.graph.java.session.GraphqlFieldContext;
import pro.shushi.pamirs.framework.gateways.graph.java.strategy.fetcher.PamirsDefaultDataFetcherFactory;
import pro.shushi.pamirs.framework.gateways.graph.util.GraphQLUtils;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.fun.VarType;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.api.session.RequestContext;
import pro.shushi.pamirs.meta.domain.model.DataDictionary;
import pro.shushi.pamirs.meta.domain.model.DataDictionaryItem;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 内存中的GraphQL管理器
 *
 * @author shier
 * @author cpc
 * date  2021/9/8 4:27 下午
 */
@Slf4j
@Component
public class GraphQLManager extends AbstractGraphQLManager {

    /**
     * 只给当前的模型构建schema
     *
     * @return
     */
    public GraphQLSchema buildSchemaByFunction(Function function) {
        GraphQLCodeRegistry.Builder codeRegistry = GraphQLCodeRegistry.newCodeRegistry();
        codeRegistry.defaultDataFetcher(PamirsDefaultDataFetcherFactory.INSTANCE);
        boolean isQuery = FunctionTypeEnum.QUERY.in(function.getType());
        String namespace = function.getNamespace();
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(namespace);
        GraphQLFieldDefinition functionField = generateQueryOrMutation(function, codeRegistry);

        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, modelConfig.getDisplayName(), isQuery ? GraphQLSdlConstants.QUERY_DISPLAY_NAME : GraphQLSdlConstants.MUTATION_DISPLAY_NAME,
                modelConfig.getSummary() + (isQuery ? GraphQLSdlConstants.QUERY_DISPLAY_NAME : GraphQLSdlConstants.MUTATION_DISPLAY_NAME), null);
        String modelFunObjectName = isQuery ? generateModelQueryApiName(modelConfig.getName()) : generateModelMutationApiName(modelConfig.getName());
        String modelFunFieldName = modelConfig.getName() +
                (isQuery ? GraphQLSdlConstants.CAPITAL_QUERY : GraphQLSdlConstants.CAPITAL_MUTATION);
        FieldCoordinates coordinates = FieldCoordinates.coordinates(isQuery ? GraphQLSdlConstants.QUERY_TYPE : GraphQLSdlConstants.MUTATION_TYPE, modelFunFieldName);
        codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.wiringDataFetcher(Boolean.TRUE));

        GraphQLObjectType modelType = GraphQLObjectType.newObject()
                .name(modelFunObjectName)
                .description(summaryBuilder.toString())
                .field(functionField)
                .build();
        GraphQLFieldDefinition modelFieldType = GraphQLFieldDefinition.newFieldDefinition()
                .name(modelFunFieldName)//designerFunctionDefinitionMutation
                .type(modelType)
                .description(summaryBuilder.toString())
                .build();
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name(GraphQLSdlConstants.QUERY_TYPE);
        GraphQLObjectType.Builder mutationType = GraphQLObjectType.newObject().name(GraphQLSdlConstants.MUTATION_TYPE);
        if (FunctionTypeEnum.QUERY.in(function.getType())) {
            queryType.field(modelFieldType);
        } else {
            mutationType.field(modelFieldType);
        }
        return GraphQLSchema.newSchema()
                .query(queryType)
                .mutation(mutationType)
                .codeRegistry(codeRegistry.build()).build();
    }

    /**
     * 只给当前的模型构建schema
     *
     * @param modelDefinition
     * @param codeRegistry
     * @return
     */
    public GraphQLSchema buildSchemaByModel(ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        Map<String, GraphQLType> typeMap = generateModelObjectType(modelDefinition, codeRegistry);
        GraphQLObjectType queryType = buildRootQueryType(typeMap, modelDefinition, codeRegistry);
        GraphQLObjectType mutationType = buildRootMutationType(typeMap, modelDefinition, codeRegistry);
        return GraphQLSchema.newSchema()
                .query(queryType)
                .mutation(mutationType)
                .codeRegistry(codeRegistry.build()).build();
    }

    /**
     * 只给当前模型构建mutationType
     *
     * @param modelDefinition
     * @param codeRegistry
     * @return
     */
    private GraphQLObjectType buildRootMutationType(Map<String, GraphQLType> typeMap, ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        GraphQLObjectType modelMutation = generateMutationType(typeMap, modelDefinition, codeRegistry);
        String fieldName = modelMutation.getName().substring(0, 1).toLowerCase() + modelMutation.getName().substring(1);//首字母小些
        GraphQLFieldDefinition.Builder description = GraphQLFieldDefinition.newFieldDefinition().name(fieldName).type(modelMutation).description(modelMutation.getDescription());
        return GraphQLObjectType.newObject().name(GraphQLSdlConstants.MUTATION_TYPE).field(description).build();
    }

    /**
     * 只给当前模型构建queryType
     *
     * @param modelDefinition
     * @param codeRegistry
     * @return
     */
    private GraphQLObjectType buildRootQueryType(Map<String, GraphQLType> typeMap, ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        GraphQLObjectType modelQuery = generateQueryType(typeMap, modelDefinition, codeRegistry);
        String fieldName = modelQuery.getName().substring(0, 1).toLowerCase() + modelQuery.getName().substring(1);//首字母小些
        GraphQLFieldDefinition.Builder modelFunction = GraphQLFieldDefinition.newFieldDefinition().name(fieldName).type(modelQuery).description(modelQuery.getDescription());
        return GraphQLObjectType.newObject().name(GraphQLSdlConstants.QUERY_TYPE).field(modelFunction).build();
    }

    /**
     * 修改模型
     * 修改模型的描述和字段
     * 默认为修改模型全部信息(模型和字段)
     *
     * @param origin 原始数据
     * @param data   修改后的数据
     */
    public void changeModel(ModelDefinition origin, ModelDefinition data) {
        changeModel(origin, data, Boolean.FALSE);
    }

    /**
     * 修改模型
     * 修改模型的描述和字段
     *
     * @param origin          原始数据
     * @param data            修改后的数据
     * @param onlyChangeModel 是否只修改模型
     */
    public synchronized void changeModel(ModelDefinition origin, ModelDefinition data, Boolean onlyChangeModel) {

        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        if (originSchema == null) {
            log.debug("Default graphql schema not built.");
            return;
        }

        DefaultRequestExecutor.addSchema(SchemaTransformer.transformSchema(originSchema, new GraphQLTypeVisitorStub() {

            private String mutationFieldName;

            private Boolean mutationFieldComputed = false;

            private String queryFieldName;

            private Boolean queryFieldComputed = false;

            private GraphqlFieldContext context = new GraphqlFieldContext().compute(origin.getModelFields(), data.getModelFields());

            @Override
            public TraversalControl visitGraphQLFieldDefinition(GraphQLFieldDefinition originFieldDefinition, TraverserContext<GraphQLSchemaElement> context) {
                if (!mutationFieldComputed && (mutationFieldName != null && mutationFieldName.equals(originFieldDefinition.getName())
                        || (mutationFieldName = generateModelMutationFieldApiName(origin.getName())).equals(originFieldDefinition.getName()))) {
                    GraphQLFieldDefinition newObjectType = buildChangedField(originFieldDefinition, data.getName() + GraphQLSdlConstants.CAPITAL_MUTATION);
                    mutationFieldComputed = true;
                    registerDataFetcher(false, context, originFieldDefinition, newObjectType.getName());
                    return TreeTransformerUtil.changeNode(context, newObjectType);
                }
                if (!queryFieldComputed && (queryFieldName != null && queryFieldName.equals(originFieldDefinition.getName())
                        || (queryFieldName = generateModelQueryFieldApiName(origin.getName())).equals(originFieldDefinition.getName()))) {
                    GraphQLFieldDefinition newObjectType = buildChangedField(originFieldDefinition, data.getName() + GraphQLSdlConstants.CAPITAL_QUERY);
                    queryFieldComputed = true;
                    registerDataFetcher(true, context, originFieldDefinition, newObjectType.getName());
                    return TreeTransformerUtil.changeNode(context, newObjectType);
                }
                return TraversalControl.CONTINUE;
            }

            private GraphQLFieldDefinition buildChangedField(GraphQLFieldDefinition objectType, String newName) {
                return objectType.transform(builder -> builder.description(data.getDisplayName()).name(newName));
            }

            private void registerDataFetcher(Boolean isQuery, TraverserContext<GraphQLSchemaElement> context, GraphQLFieldDefinition originFieldDefinition, String newFieldName) {
                if (!originFieldDefinition.getName().equals(newFieldName)) {
                    GraphQLCodeRegistry.Builder codeRegistry = context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
                    FieldCoordinates coordinates = FieldCoordinates.coordinates(isQuery ? GraphQLSdlConstants.QUERY_TYPE : GraphQLSdlConstants.MUTATION_TYPE, originFieldDefinition.getName());
                    FieldCoordinates newCoordinates = FieldCoordinates.coordinates(isQuery ? GraphQLSdlConstants.QUERY_TYPE : GraphQLSdlConstants.MUTATION_TYPE, newFieldName);
                    codeRegistry.dataFetcher(newCoordinates, codeRegistry.getDataFetcher(coordinates, originFieldDefinition));
                }
            }

            private <T extends GraphQLNamedSchemaElement> List<T> computeFields(List<T> fieldDefinitions, Boolean isInputType) {
                List<T> graphQLFieldDefinitions = new ArrayList<T>();
                List<ModelField> sameFields = context.getSameFields();
                if (CollectionUtils.isNotEmpty(sameFields)) {
                    List<String> sameFieldList = sameFields.stream().map(ModelField::getName).collect(Collectors.toList());
                    List<T> same = fieldDefinitions.stream().filter(v -> sameFieldList.contains(v.getName())).collect(Collectors.toList());
                    graphQLFieldDefinitions.addAll(same);
                }
                for (ModelField field : context.getAddFields()) {
                    String type = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), origin.getName(),
                            field.getTtype().value(), field.getLtype(), field.getSize(), Boolean.FALSE,
                            field.getReferences(), field.getDictionary(), field.getRequestSerialize(), isInputType);
                    GraphQLType simpleGraphqlType = originSchema.getType(type);
                    if (null == simpleGraphqlType && TtypeEnum.ENUM.equals(field.getTtype())) {
                        //被引用的数据字典首次创建类型
                        DataDictionary dictionary = PamirsSession.getContext().getDictionary(field.getDictionary());
                        GraphQLEnumType graphQLEnumType = generateDictionary(dictionary);
                        simpleGraphqlType = graphQLEnumType;
                    }
                    StringBuilder summary = new StringBuilder();
                    SummaryGenerator.generate(summary, field.getDisplayName(), "字段", field.getSummary(), field.getInvisible());

                    if (!isInputType) {
                        GraphQLFieldDefinition newField = GraphQLFieldDefinition.newFieldDefinition()
                                .name(field.getName())
                                .description(summary.toString())
                                .type(null != field.getMulti() && field.getMulti() ? new GraphQLList(simpleGraphqlType) :
                                        simpleGraphqlType instanceof GraphQLScalarType ?
                                                (GraphQLScalarType) simpleGraphqlType :
                                                simpleGraphqlType instanceof GraphQLEnumType ?
                                                        (GraphQLEnumType) simpleGraphqlType : (GraphQLObjectType) simpleGraphqlType
                                ).build();
                        graphQLFieldDefinitions.add((T) newField);
                    } else {
                        GraphQLInputObjectField newField = GraphQLInputObjectField.newInputObjectField()
                                .name(field.getName())
                                .description(summary.toString())
                                .type(null != field.getMulti() && field.getMulti() ? new GraphQLList(simpleGraphqlType) :
                                        simpleGraphqlType instanceof GraphQLScalarType ?
                                                (GraphQLScalarType) simpleGraphqlType :
                                                simpleGraphqlType instanceof GraphQLEnumType ?
                                                        (GraphQLEnumType) simpleGraphqlType : (GraphQLInputType) simpleGraphqlType
                                ).build();
                        graphQLFieldDefinitions.add((T) newField);
                    }
                }
                return graphQLFieldDefinitions;
            }

            private void addField(List<GraphQLFieldDefinition> graphQLFieldDefinitions, ModelField field) {
                String type = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), origin.getName(),
                        field.getTtype().value(), field.getLtype(), field.getSize(), Boolean.FALSE,
                        field.getReferences(), field.getDictionary(), field.getRequestSerialize(), Boolean.FALSE);
                GraphQLType simpleGraphqlType = originSchema.getType(type);
                if (null == simpleGraphqlType && TtypeEnum.ENUM.equals(field.getTtype())) {
                    //被引用的数据字典首次创建类型
                    DataDictionary dictionary = PamirsSession.getContext().getDictionary(field.getDictionary());
                    GraphQLEnumType graphQLEnumType = generateDictionary(dictionary);
                    simpleGraphqlType = graphQLEnumType;
                }
                GraphQLType fileGraphqlType = simpleGraphqlType;
                if (null != field.getMulti() && field.getMulti()) {
                    fileGraphqlType = new GraphQLList(simpleGraphqlType);
                }
                GraphQLFieldDefinition newField = GraphQLFieldDefinition.newFieldDefinition()
                        .name(field.getName()).type(
                                fileGraphqlType instanceof GraphQLScalarType ?
                                        (GraphQLScalarType) fileGraphqlType :
                                        fileGraphqlType instanceof GraphQLList ? (GraphQLList) fileGraphqlType :
                                                fileGraphqlType instanceof GraphQLEnumType ?
                                                        (GraphQLEnumType) fileGraphqlType : (GraphQLObjectType) fileGraphqlType
                        ).build();
                graphQLFieldDefinitions.add(newField);
            }

            private void addFieldInput(List<GraphQLInputObjectField> graphQLFieldDefinitions, ModelField field) {
                String type = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), origin.getName(),
                        field.getTtype().value(), field.getLtype(), field.getSize(), Boolean.FALSE,
                        field.getReferences(), field.getDictionary(), field.getRequestSerialize(), Boolean.TRUE);
                GraphQLType simpleGraphqlType = originSchema.getType(type);
                if (null == simpleGraphqlType && TtypeEnum.ENUM.equals(field.getTtype())) {
                    //被引用的数据字典首次创建类型
                    DataDictionary dictionary = PamirsSession.getContext().getDictionary(field.getDictionary());
                    GraphQLEnumType graphQLEnumType = generateDictionary(dictionary);
                    simpleGraphqlType = graphQLEnumType;
                }
                GraphQLType fileGraphqlType = simpleGraphqlType;
                if (null != field.getMulti() && field.getMulti()) {
                    fileGraphqlType = new GraphQLList(simpleGraphqlType);
                }
                GraphQLInputObjectField newField = GraphQLInputObjectField.newInputObjectField()
                        .name(field.getName()).type(
                                fileGraphqlType instanceof GraphQLScalarType ?
                                        (GraphQLScalarType) fileGraphqlType :
                                        fileGraphqlType instanceof GraphQLList ? (GraphQLList) fileGraphqlType :
                                                fileGraphqlType instanceof GraphQLEnumType ?
                                                        (GraphQLEnumType) fileGraphqlType : (GraphQLInputType) fileGraphqlType
                        ).build();
                graphQLFieldDefinitions.add(newField);
            }
        }));
    }

    /**
     * 修改数据字典
     * 修改数据字典的名称（name）展示名称（displayName）
     * 修改数据字典的可选项
     *
     * @param originDictionary
     * @param changedDictionary
     */
    public synchronized void changeDictionary(DataDictionary originDictionary, DataDictionary changedDictionary) {

        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        if (originSchema == null) {
            log.debug("Default graphql schema not built.");
            return;
        }

        DefaultRequestExecutor.addSchema(SchemaTransformer.transformSchema(originSchema, new GraphQLTypeVisitorStub() {
            @Override
            public TraversalControl visitGraphQLEnumType(GraphQLEnumType node, TraverserContext<GraphQLSchemaElement> context) {

                GraphQLCodeRegistry.Builder codeRegistry = context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
                if (isChangedEnum(node)) {
                    GraphQLEnumType newObjectType = buildChangedEnumType(node, codeRegistry);
                    return TreeTransformerUtil.changeNode(context, newObjectType);
                }
                return TraversalControl.CONTINUE;
            }

            private boolean isChangedEnum(GraphQLEnumType node) {
                return generateModelObjectApiName(node.getName()).equals(originDictionary.getName());
            }

            private GraphQLEnumType buildChangedEnumType(GraphQLEnumType node, GraphQLCodeRegistry.Builder codeRegistry) {
                GraphqlEnumContext context = new GraphqlEnumContext().compute(originDictionary.getOptions(), changedDictionary.getOptions());
                ArrayList<GraphQLEnumValueDefinition> graphQLFieldDefinitions = new ArrayList<>();
                List<GraphQLEnumValueDefinition> fieldDefinitions = node.getValues();
                List<DataDictionaryItem> sameFields = context.getSameItems();
                if (CollectionUtils.isNotEmpty(sameFields)) {
                    List<String> sameFieldList = sameFields.stream().map(DataDictionaryItem::getName).collect(Collectors.toList());
                    List<GraphQLEnumValueDefinition> same = fieldDefinitions.stream().filter(v -> sameFieldList.contains(v.getName())).collect(Collectors.toList());
                    graphQLFieldDefinitions.addAll(same);
                }
                for (DataDictionaryItem item : context.getAddItems()) {
                    StringBuilder summary = new StringBuilder();
                    SummaryGenerator.generate(summary, item.getDisplayName(), GraphQLSdlConstants.ENUM_ITEM_DISPLAY_NAME, item.getHelp(), null);
                    GraphQLEnumValueDefinition newEnumValueDefinition = GraphQLEnumValueDefinition.newEnumValueDefinition().name(item.getName()).description(summary.toString()).build();
                    graphQLFieldDefinitions.add(newEnumValueDefinition);
                }
                String apiName = generateModelObjectApiName(changedDictionary.getName());
                StringBuilder summary = new StringBuilder();
                SummaryGenerator.generate(summary, changedDictionary.getDisplayName(), GraphQLSdlConstants.ENUM_DISPLAY_NAME, changedDictionary.getSummary(), null);
                return node.transform(builder -> builder.replaceValues(graphQLFieldDefinitions).name(apiName).description(summary.toString()));
            }
        }));
    }

    /**
     * 创建模型
     *
     * @param modelDefinition
     */
    public synchronized void createModel(ModelDefinition modelDefinition) {

        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        if (originSchema == null) {
            log.debug("Default graphql schema not built.");
            return;
        }

        DefaultRequestExecutor.addSchema(SchemaTransformer.transformSchema(originSchema, new GraphQLTypeVisitorStub() {

            private Map<String, GraphQLType> typeMap = new HashMap<>();

            private String insertQueryPoint;

            private String insertMutationPoint;

            {
                insertQueryPoint = ((GraphQLFieldDefinition) originSchema.getType(GraphQLSdlConstants.QUERY_TYPE).getChildren().get(0)).getName();
                insertMutationPoint = ((GraphQLFieldDefinition) originSchema.getType(GraphQLSdlConstants.MUTATION_TYPE).getChildren().get(0)).getName();
            }

            @Override
            public TraversalControl visitGraphQLFieldDefinition(GraphQLFieldDefinition node, TraverserContext<GraphQLSchemaElement> context) {
                if (null != insertQueryPoint && node.getName().equals(insertQueryPoint)) {
                    this.typeMap = initTypeMap(modelDefinition, typeMap, context);
                    GraphQLCodeRegistry.Builder codeRegiestry = context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
                    GraphQLObjectType queryType = generateQueryType(this.typeMap, modelDefinition, codeRegiestry);
                    String fieldName = queryType.getName().substring(0, 1).toLowerCase() + queryType.getName().substring(1);
                    GraphQLFieldDefinition.Builder description = GraphQLFieldDefinition.newFieldDefinition().name(fieldName).type(queryType).description(queryType.getDescription());
                    return TreeTransformerUtil.insertAfter(context, description.build());
                } else if (null != insertMutationPoint && node.getName().equals(insertMutationPoint)) {
                    this.typeMap = initTypeMap(modelDefinition, typeMap, context);
                    GraphQLCodeRegistry.Builder codeRegistry = context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
                    GraphQLObjectType mutationType = generateMutationType(this.typeMap, modelDefinition, codeRegistry);
                    String fieldName = mutationType.getName().substring(0, 1).toLowerCase() + mutationType.getName().substring(1);
                    GraphQLFieldDefinition.Builder description = GraphQLFieldDefinition.newFieldDefinition().name(fieldName).type(mutationType).description(mutationType.getDescription());
                    return TreeTransformerUtil.insertAfter(context, description.build());
                }
                return TraversalControl.CONTINUE;
            }

            private Map<String, GraphQLType> initTypeMap(ModelDefinition modelDefinition, Map<String, GraphQLType> typeMap, TraverserContext context) {
                if (MapUtils.isEmpty(typeMap)) {
                    GraphQLCodeRegistry.Builder codeRegistry = (GraphQLCodeRegistry.Builder) context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
                    typeMap = generateModelObjectType(modelDefinition, codeRegistry);
                }
                return typeMap;
            }

        }));
    }

    /**
     * 修改函数列表
     *
     * @param createOrUpdateFunctions 创建/修改的函数
     */
    public void changeFunctions(List<Function> createOrUpdateFunctions) {
        changeFunctions(createOrUpdateFunctions, new ArrayList<>());
    }

    /**
     * 修改函数列表
     *
     * @param functions        创建/修改的函数
     * @param deletedFunctions 删除的函数
     */
    public synchronized void changeFunctions(List<Function> functions, List<Function> deletedFunctions) {

        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        if (originSchema == null) {
            log.debug("Default graphql schema not built.");
            return;
        }

        DefaultRequestExecutor.addSchema(SchemaTransformer.transformSchema(originSchema, new GraphQLTypeVisitorStub() {

            private Map<String/*插入的父节点*/, Function> insertMap;

            private Map<String/*修改的父节点*/, Function> changeMap;

            private Map<String/*删除的父节点*/, List<Function>> deleteMap;

            {
                a:
                for (Function function : functions) {
                    if (FunctionOpenEnum.API.in(function.getOpen())) {
                        Boolean isQuery = FunctionTypeEnum.QUERY.in(function.getType());
                        String modelName = PamirsSession.getContext().getModelConfig(function.getNamespace()).getName();
                        String parentNodeName = isQuery ? generateModelQueryApiName(modelName) : generateModelMutationApiName(modelName);
                        List<GraphQLSchemaElement> children = originSchema.getType(parentNodeName).getChildren();
                        //判断当前的function是否已经构建过了
                        for (GraphQLSchemaElement element : children) {
                            if (((GraphQLFieldDefinition) element).getName().equals(function.getName())) {
                                if (null == changeMap) {
                                    changeMap = new HashMap<>();
                                }
                                changeMap.put(parentNodeName, function);
                                continue a;
                            }
                        }
                        if (null == insertMap) {
                            insertMap = new HashMap<>();
                        }
                        insertMap.put(parentNodeName, function);
                    }
                }
                for (Function function : deletedFunctions) {
                    Boolean isQuery = FunctionTypeEnum.QUERY.in(function.getType());
                    String modelName = PamirsSession.getContext().getModelConfig(function.getNamespace()).getName();
                    String parentNodeName = isQuery ? generateModelQueryApiName(modelName) : generateModelMutationApiName(modelName);
                    List<GraphQLSchemaElement> children = originSchema.getType(parentNodeName).getChildren();
                    for (GraphQLSchemaElement element : children) {
                        if (((GraphQLFieldDefinition) element).getName().equals(function.getName())) {
                            if (null == deleteMap) {
                                deleteMap = new HashMap<>();
                            }
                            if (deleteMap.get(function.getName()) == null) {
                                deleteMap.put(parentNodeName, new ArrayList<>());
                            }
                            deleteMap.get(parentNodeName).add(function);
                        }
                    }
                }
            }

            @Override
            public TraversalControl visitGraphQLObjectType(GraphQLObjectType node, TraverserContext<GraphQLSchemaElement> context) {
                if (insertMap != null && insertMap.containsKey(node.getName())) {
                    //创建function
                    Function queryFunction = insertMap.get(node.getName());
                    GraphQLFieldDefinition fieldDefinition = generateQueryOrMutation(node.getName(), queryFunction, context);

                    GraphQLObjectType transform = node.transform(builder -> builder.field(fieldDefinition));
                    return TreeTransformerUtil.changeNode(context, transform);
                } else if (changeMap != null && changeMap.containsKey(node.getName())) {
                    //修改function
                    Function queryFunction = changeMap.get(node.getName());
                    GraphQLFieldDefinition fieldDefinition = generateQueryOrMutation(node.getName(), queryFunction, context);
                    List<GraphQLFieldDefinition> newFields = new ArrayList<>();
                    for (GraphQLSchemaElement child : node.getChildren()) {
                        if (((GraphQLFieldDefinition) child).getName().equals(queryFunction.getName())) {
                            newFields.add(fieldDefinition);
                            continue;
                        }
                        newFields.add((GraphQLFieldDefinition) child);
                    }
                    GraphQLObjectType transform = node.transform(builder -> builder.replaceFields(newFields));
                    return TreeTransformerUtil.changeNode(context, transform);
                } else if (deleteMap != null && deleteMap.containsKey(node.getName())) {
                    //删除function的schema
                    //由于function不是API级别的/function被删除
                    List<Function> queryFunction = deleteMap.get(node.getName());
                    List<String> funNames = queryFunction.stream().map(v -> v.getName()).collect(Collectors.toList());
                    List<GraphQLFieldDefinition> newFields = new ArrayList<>();
                    for (GraphQLSchemaElement child : node.getChildren()) {
                        if (funNames.contains(((GraphQLFieldDefinition) child).getName())) {
                            continue;
                        }
                        newFields.add((GraphQLFieldDefinition) child);
                    }
                    GraphQLObjectType transform = node.transform(builder -> builder.replaceFields(newFields));
                    return TreeTransformerUtil.changeNode(context, transform);
                }
                return TraversalControl.CONTINUE;
            }
        }));
    }

    private GraphQLEnumType generateDictionary(DataDictionary dataDictionary) {

        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, dataDictionary.getDisplayName(), GraphQLSdlConstants.ENUM_DISPLAY_NAME, dataDictionary.getSummary(), null);
        String name = generateModelObjectApiName(dataDictionary.getName());

        List<GraphQLEnumValueDefinition> graphQLEnumValueDefinitions = new ArrayList<>();
        for (DataDictionaryItem item : dataDictionary.getOptions()) {
            StringBuilder itemSummaryBuilder = new StringBuilder();
            SummaryGenerator.generate(itemSummaryBuilder, item.getDisplayName(), GraphQLSdlConstants.ENUM_ITEM_DISPLAY_NAME, item.getHelp(), null);
            String itemName = item.getName();
            graphQLEnumValueDefinitions.add(
                    GraphQLEnumValueDefinition.newEnumValueDefinition()
                            .name(itemName)
                            .description(itemSummaryBuilder.toString())
                            .value(item.getName()).build()
            );
        }

        return GraphQLEnumType.newEnum()
                .name(name)
                .description(summaryBuilder.toString())
                .values(graphQLEnumValueDefinitions)
                .build();
    }

    private GraphQLObjectType generateModelType(ModelDefinition modelConfig, GraphQLCodeRegistry.Builder codeRegistry) {
        String name = generateModelObjectApiName(modelConfig.getName());
        StringBuilder summary = new StringBuilder();
        SummaryGenerator.generate(summary, modelConfig.getDisplayName(), "模型", modelConfig.getSummary(), null);
        return generateType(name, summary.toString(), modelConfig, Boolean.FALSE, codeRegistry);
    }

    private GraphQLObjectType generateModelPageType(ModelDefinition modelConfig, GraphQLObjectType modelObject, GraphQLCodeRegistry.Builder codeRegistry) {
        String name = generateModelPageApiName(modelConfig.getName());
        StringBuilder summary = new StringBuilder();
        SummaryGenerator.generate(summary, modelConfig.getDisplayName(), "分页模型", modelConfig.getSummary(), null);
        GraphQLObjectType paginationObjectType = generateType(name, summary.toString(), PamirsSession.getContext().getModelConfig(Pagination.MODEL_MODEL).getModelDefinition(), Boolean.FALSE, codeRegistry);
        GraphQLList graphQLList = new GraphQLList(modelObject);

        StringBuilder contentSummary = new StringBuilder();
        SummaryGenerator.generate(contentSummary, "内容", "字段", "查询结果列表", null);

        return GraphQLObjectType.newObject(paginationObjectType).field(
                GraphQLFieldDefinition.newFieldDefinition()
                        .description(contentSummary.toString())
                        .name(GraphQLSdlConstants.CONTENT)
                        .type(graphQLList)
                        .build()
        ).build();
    }

    private GraphQLInputObjectType generateModelInputType(ModelDefinition modelConfig) {
        String name = generateModelInputApiName(modelConfig.getName());
        StringBuilder summary = new StringBuilder();
        SummaryGenerator.generate(summary, modelConfig.getDisplayName(), "请求模型", modelConfig.getSummary(), null);

        return GraphQLInputObjectType.newInputObject()
                .name(name)
                .description(summary.toString())
                .fields(generateInputFields(new ModelConfig(modelConfig)))
                .build();
    }

    private GraphQLInputObjectType generateModelCondType(ModelDefinition modelConfig, GraphQLInputObjectType modelInputObject) {
        String name = generateModelCondInputApiName(modelConfig.getName());
        StringBuilder summary = new StringBuilder();
        SummaryGenerator.generate(summary, modelConfig.getDisplayName(), "条件模型", modelConfig.getSummary(), null);

        StringBuilder updateSummary = new StringBuilder();
        SummaryGenerator.generate(updateSummary, "条件", "字段", "条件", null);
        GraphQLInputObjectField updateEntityField = GraphQLInputObjectField.newInputObjectField()
                .description(updateSummary.toString())
                .name(GraphQLSdlConstants.UPDATE_ENTITY)
                .type(modelInputObject)
                .build();
        return GraphQLInputObjectType.newInputObject()
                .name(name)
                .description(summary.toString())
                .fields(generateInputFields(PamirsSession.getContext().getModelConfig(IWrapper.MODEL_MODEL)))
                .field(updateEntityField)
                .build();
    }


    private GraphQLObjectType generateType(String name, String summary, ModelDefinition modelConfig, boolean input, GraphQLCodeRegistry.Builder codeRegistry) {
        return GraphQLObjectType.newObject()
                .name(name)
                .description(summary)
                .fields(generateFields(name, modelConfig, input, codeRegistry))
                .build();
    }

//    private GraphQLObjectType generateType(String name, String summary, ModelDefinition modelConfig, boolean input, TraverserContext context) {
//        return GraphQLObjectType.newObject()
//                .name(name)
//                .description(summary)
//                .fields(generateFields(name,modelConfig, input,context))
//                .build();
//    }
//

    // 生成字段
    private List<GraphQLFieldDefinition> generateFields(String parentNodeName, ModelDefinition modelConfig, boolean input, GraphQLCodeRegistry.Builder codeRegistry) {
        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        List<GraphQLFieldDefinition> graphQLFieldDefinitions = new ArrayList<>(modelConfig.getModelFields().size());
        for (ModelField fieldConfig : modelConfig.getModelFields()) {
            StringBuilder summary = new StringBuilder();
            SummaryGenerator.generate(summary, fieldConfig.getDisplayName(), "字段", fieldConfig.getSummary(), fieldConfig.getInvisible());
            String ttype = fieldConfig.getTtype().value();
            if (TtypeEnum.isRelatedType(ttype)) {
                ttype = fieldConfig.getRelatedTtype().value();
            }
            String type = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelConfig.getName(),
                    ttype, fieldConfig.getLtype(), fieldConfig.getSize(), false,
                    fieldConfig.getReferences(), fieldConfig.getDictionary(), fieldConfig.getRequestSerialize(), input);
            GraphQLType graphQLType = originSchema.getType(type);

            FieldCoordinates coordinates = FieldCoordinates.coordinates(parentNodeName, fieldConfig.getName());
            Optional.ofNullable(QueryAndMutationBinder.dataFetcher(new ModelFieldConfig(fieldConfig)))
                    .ifPresent(v -> codeRegistry.dataFetcher(coordinates, v));

            if (null != fieldConfig.getMulti() && fieldConfig.getMulti()) {
                GraphQLList graphQLList = new GraphQLList(graphQLType);
                graphQLFieldDefinitions.add(GraphQLFieldDefinition.newFieldDefinition()
                        .description(summary.toString())
                        .name(fieldConfig.getName())
                        .type(graphQLList)
                        .build()
                );
            } else {
                graphQLFieldDefinitions.add(GraphQLFieldDefinition.newFieldDefinition()
                        .description(summary.toString())
                        .name(fieldConfig.getName())
                        .type((GraphQLOutputType) (graphQLType))
                        .build()
                );
            }
        }
        return graphQLFieldDefinitions;
    }

    // 生成字段
    private List<GraphQLInputObjectField> generateInputFields(ModelConfig modelConfig) {
        GraphQLSchema originSchema = DefaultRequestExecutor.getSchema();

        List<GraphQLInputObjectField> graphQLFieldDefinitions = new ArrayList<>(modelConfig.getModelFieldConfigList().size());
        for (ModelFieldConfig fieldConfig : modelConfig.getModelFieldConfigList()) {
            StringBuilder summary = new StringBuilder();
            SummaryGenerator.generate(summary, fieldConfig.getDisplayName(), "字段", fieldConfig.getSummary(), fieldConfig.getInvisible());
            String ttype = fieldConfig.getTtype();
            if (TtypeEnum.isRelatedType(ttype)) {
                ttype = fieldConfig.getRelatedTtype();
            }
            String type = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelConfig.getName(),
                    ttype, fieldConfig.getLtype(), fieldConfig.getSize(), false,
                    fieldConfig.getReferences(), fieldConfig.getDictionary(), fieldConfig.getRequestSerialize(), Boolean.TRUE);
            GraphQLType graphQLType = originSchema.getType(type);
            if (null != fieldConfig.getMulti() && fieldConfig.getMulti()) {
                GraphQLList graphQLList = new GraphQLList(graphQLType);
                graphQLFieldDefinitions.add(GraphQLInputObjectField.newInputObjectField()
                        .description(summary.toString())
                        .name(fieldConfig.getName())
                        .type(graphQLList)
                        .build()
                );
            } else {
                graphQLFieldDefinitions.add(GraphQLInputObjectField.newInputObjectField()
                        .description(summary.toString())
                        .name(fieldConfig.getName())
                        .type((GraphQLInputType) (graphQLType))
                        .build()
                );
            }
        }
        return graphQLFieldDefinitions;
    }

    private GraphQLObjectType generateQueryType(Map<String, GraphQLType> typeMap, ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, modelDefinition.getDisplayName(), GraphQLSdlConstants.QUERY_DISPLAY_NAME, modelDefinition.getSummary() + GraphQLSdlConstants.QUERY_DISPLAY_NAME, null);
        String name = generateModelQueryApiName(modelDefinition.getName());
        List<GraphQLFieldDefinition> queryGraphQLs = generateQueryOrMutation(typeMap, PamirsSession.getContext(), new ModelConfig(modelDefinition), Boolean.TRUE, codeRegistry);

        FieldCoordinates coordinates = FieldCoordinates.coordinates(GraphQLSdlConstants.QUERY_TYPE, modelDefinition.getName() + GraphQLSdlConstants.CAPITAL_QUERY);
        codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.wiringDataFetcher(Boolean.TRUE));

        return GraphQLObjectType.newObject()
                .name(name)
                .description(summaryBuilder.toString())
                .fields(queryGraphQLs)
                .build();
    }

    private GraphQLObjectType generateMutationType(Map<String, GraphQLType> typeMap, ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, modelDefinition.getDisplayName(), GraphQLSdlConstants.MUTATION_DISPLAY_NAME, modelDefinition.getSummary() + GraphQLSdlConstants.MUTATION_DISPLAY_NAME, null);
        String name = generateModelMutationApiName(modelDefinition.getName());
        List<GraphQLFieldDefinition> queryGraphQLs = generateQueryOrMutation(typeMap, PamirsSession.getContext(), new ModelConfig(modelDefinition), Boolean.FALSE, codeRegistry);

        FieldCoordinates coordinates = FieldCoordinates.coordinates(GraphQLSdlConstants.MUTATION_TYPE, modelDefinition.getName() + GraphQLSdlConstants.CAPITAL_MUTATION);
        codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.wiringDataFetcher(Boolean.FALSE));

        return GraphQLObjectType.newObject()
                .name(name)
                .description(summaryBuilder.toString())
                .fields(queryGraphQLs)
                .build();
    }


    private GraphQLFieldDefinition generateQueryOrMutation(Function function, GraphQLCodeRegistry.Builder codeRegistry) {
        //处理方法名和方法描述
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(function.getNamespace());
        String modelName = modelConfig.getName();
        Map map = generateModelObjectType(modelConfig.getModelDefinition(), codeRegistry);
        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, function.getDisplayName(), null, function.getSummary(), null);
        List<GraphQLArgument> arguments = new ArrayList<>();
        //处理函数入参
        if (CollectionUtils.isNotEmpty(function.getArguments())) {
            for (Arg arg : function.getArguments()) {
                String argType;
                if (IWrapper.MODEL_MODEL.equals(arg.getModel())) {
                    argType = generateModelCondInputApiName(modelName);
                } else {
                    argType = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelName,
                            arg.getTtype(), arg.getLtype(), arg.getSize(), Boolean.FALSE,
                            arg.getModel(), arg.getDictionary(), null, Boolean.TRUE);
                }
                GraphQLType graphQLArgType = getTypeF(map, argType, arg.getMulti());
                arguments.add(
                        GraphQLArgument.newArgument()
                                .type(arg.getMulti() ?
                                        (GraphQLList) graphQLArgType : graphQLArgType instanceof GraphQLScalarType ?
                                        (GraphQLScalarType) graphQLArgType : (GraphQLInputType) graphQLArgType
                                ).name(arg.getName())
                                .build()
                );
            }
        }
        // 处理返回值类型
        VarType returnType = function.getReturnType();
        String varType;
        if (StringUtils.isNotBlank(returnType.getModel()) && Pagination.MODEL_MODEL.equals(returnType.getModel())) {
            varType = generateModelPageApiName(modelConfig.getName());
        } else {
            varType = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelName,
                    returnType.getTtype(), returnType.getLtype(), returnType.getSize(), Boolean.FALSE,
                    returnType.getModel(), returnType.getDictionary(), null, Boolean.FALSE);
        }
        GraphQLType graphQLReturnType = getTypeF(map, varType, returnType.getMulti());
        String parentNodeName = FunctionTypeEnum.QUERY.in(function.getType()) ? generateModelQueryApiName(modelName) : generateModelMutationApiName(modelName);
        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentNodeName, function.getName());
        codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.dataFetcher(function, modelConfig));

        return GraphQLFieldDefinition.newFieldDefinition()
                .arguments(arguments)//函数的入参
                .type(returnType.getMulti() ?
                        (GraphQLList) graphQLReturnType : graphQLReturnType instanceof GraphQLScalarType ?
                        (GraphQLScalarType) graphQLReturnType : (GraphQLObjectType) graphQLReturnType)//函数的出参
                .name(function.getName())
                .description(summaryBuilder.toString())
                .build();
    }

    private GraphQLFieldDefinition generateQueryOrMutation(String parentNodeName, Function function, TraverserContext context) {
        //处理方法名和方法描述
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(function.getNamespace());
        StringBuilder summaryBuilder = new StringBuilder();
        SummaryGenerator.generate(summaryBuilder, function.getDisplayName(), null, function.getSummary(), null);
        List<GraphQLArgument> arguments = new ArrayList<>();
        //处理函数入参
        if (CollectionUtils.isNotEmpty(function.getArguments())) {
            for (Arg arg : function.getArguments()) {
                String argType;
                if (IWrapper.MODEL_MODEL.equals(arg.getModel())) {
                    argType = generateModelCondInputApiName(modelConfig.getName());
                } else {
                    argType = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelConfig.getName(),
                            arg.getTtype(), arg.getLtype(), arg.getSize(), Boolean.FALSE,
                            arg.getModel(), arg.getDictionary(), null, Boolean.TRUE);
                }
                GraphQLType graphQLArgType = getType(argType, arg.getMulti());
                arguments.add(
                        GraphQLArgument.newArgument()
                                .type(arg.getMulti() ?
                                        (GraphQLList) graphQLArgType : graphQLArgType instanceof GraphQLScalarType ?
                                        (GraphQLScalarType) graphQLArgType : (GraphQLInputType) graphQLArgType
                                ).name(arg.getName())
                                .build()
                );
            }
        }
        // 处理返回值类型
        VarType returnType = function.getReturnType();
        String varType;
        if (StringUtils.isNotBlank(returnType.getModel()) && Pagination.MODEL_MODEL.equals(returnType.getModel())) {
            varType = generateModelPageApiName(modelConfig.getName());
        } else {
            varType = GraphQLUtils.fetchGraphqlType(PamirsSession.getContext(), modelConfig.getName(),
                    returnType.getTtype(), returnType.getLtype(), returnType.getSize(), Boolean.FALSE,
                    returnType.getModel(), returnType.getDictionary(), null, Boolean.FALSE);
        }
        GraphQLType graphQLReturnType = getType(varType, returnType.getMulti());

        FieldCoordinates coordinates = FieldCoordinates.coordinates(parentNodeName, function.getName());
        GraphQLCodeRegistry.Builder codeRegistry = (GraphQLCodeRegistry.Builder) context.getVarFromParents(GraphQLCodeRegistry.Builder.class);
        codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.dataFetcher(function, modelConfig));

        return GraphQLFieldDefinition.newFieldDefinition()
                .arguments(arguments)//函数的入参
                .type(returnType.getMulti() ?
                        (GraphQLList) graphQLReturnType : graphQLReturnType instanceof GraphQLScalarType ?
                        (GraphQLScalarType) graphQLReturnType : (GraphQLObjectType) graphQLReturnType)//函数的出参
                .name(function.getName())
                .description(summaryBuilder.toString())
                .build();
    }

    private List<GraphQLFieldDefinition> generateQueryOrMutation(Map<String, GraphQLType> typeMap, RequestContext requestContext,
                                                                 ModelConfig modelConfig, boolean query, GraphQLCodeRegistry.Builder codeRegistry) {
        List<GraphQLFieldDefinition> result = new ArrayList<>();
        List<Function> functionList = new ArrayList<>();
        for (Function function : modelConfig.getFunctionList()) {
            if ((FunctionTypeEnum.QUERY.in(function.getType()) != query) || !FunctionOpenEnum.API.in(function.getOpen())) {
                continue;
            }
            functionList.add(function);
        }
        if (CollectionUtils.isEmpty(functionList)) {
            return result;
        }
        String parentNodeName = query ? generateModelQueryApiName(modelConfig.getName()) : generateModelMutationApiName(modelConfig.getName());
        for (Function function : functionList) {
            //处理方法名和方法描述
            StringBuilder summaryBuilder = new StringBuilder();
            SummaryGenerator.generate(summaryBuilder, function.getDisplayName(), null, function.getSummary(), null);
            List<GraphQLArgument> arguments = new ArrayList<>();
            //处理函数入参
            if (CollectionUtils.isNotEmpty(function.getArguments())) {
                for (Arg arg : function.getArguments()) {
                    String argType;
                    if (IWrapper.MODEL_MODEL.equals(arg.getModel())) {
                        argType = generateModelCondInputApiName(modelConfig.getName());
                    } else {
                        argType = GraphQLUtils.fetchGraphqlType(requestContext, modelConfig.getName(),
                                arg.getTtype(), arg.getLtype(), arg.getSize(), Boolean.FALSE,
                                arg.getModel(), arg.getDictionary(), null, Boolean.TRUE);
                    }
                    GraphQLType graphQLArgType = getType(typeMap, argType, arg.getMulti());
                    arguments.add(
                            GraphQLArgument.newArgument()
                                    .type(arg.getMulti() ?
                                            (GraphQLList) graphQLArgType : graphQLArgType instanceof GraphQLScalarType ?
                                            (GraphQLScalarType) graphQLArgType : (GraphQLInputType) graphQLArgType
                                    ).name(arg.getName())
                                    .build()
                    );
                }
            }
            // 处理返回值类型
            VarType returnType = function.getReturnType();
            String varType;
            if (StringUtils.isNotBlank(returnType.getModel()) && Pagination.MODEL_MODEL.equals(returnType.getModel())) {
                varType = generateModelPageApiName(modelConfig.getName());
            } else {
                varType = GraphQLUtils.fetchGraphqlType(requestContext, modelConfig.getName(),
                        returnType.getTtype(), returnType.getLtype(), returnType.getSize(), Boolean.FALSE,
                        returnType.getModel(), returnType.getDictionary(), null, Boolean.FALSE);
            }
            GraphQLType graphQLReturnType = getType(typeMap, varType, returnType.getMulti());

            GraphQLFieldDefinition functionField = GraphQLFieldDefinition.newFieldDefinition()
                    .arguments(arguments)//函数的入参
                    .type(returnType.getMulti() ?
                            (GraphQLList) graphQLReturnType : graphQLReturnType instanceof GraphQLScalarType ?
                            (GraphQLScalarType) graphQLReturnType : (GraphQLObjectType) graphQLReturnType)//函数的出参
                    .name(function.getName())
                    .description(summaryBuilder.toString())
                    .build();
            FieldCoordinates coordinates = FieldCoordinates.coordinates(parentNodeName, function.getName());
            codeRegistry.dataFetcher(coordinates, QueryAndMutationBinder.dataFetcher(function, modelConfig));
            result.add(functionField);

        }
        return result;
    }

    private GraphQLType getTypeF(Map<String, GraphQLType> typeMap, String type, Boolean isMulti) {
        GraphQLType graphQLType = typeMap.get(type);
        if (isMulti) {
            return new GraphQLList(graphQLType);
        }
        return graphQLType;
    }

    private GraphQLType getType(Map<String, GraphQLType> typeMap, String type, Boolean isMulti) {
        GraphQLType graphQLType = DefaultRequestExecutor.getSchema().getType(type);
        if (null == graphQLType) {
            graphQLType = typeMap.get(type);
        }
        if (isMulti) {
            return new GraphQLList(graphQLType);
        }
        return graphQLType;
    }

    private GraphQLType getType(String type, Boolean isMulti) {
        GraphQLType graphQLType = DefaultRequestExecutor.getSchema().getType(type);
        if (isMulti) {
            return new GraphQLList(graphQLType);
        }
        return graphQLType;
    }

    private Map generateModelObjectType(ModelDefinition modelDefinition, GraphQLCodeRegistry.Builder codeRegistry) {
        Map<String, GraphQLType> typeMap = new HashMap<>();

        GraphQLObjectType modelObject = generateModelType(modelDefinition, codeRegistry);
        GraphQLInputObjectType modelInputType = generateModelInputType(modelDefinition);
        GraphQLInputObjectType modelCondType = generateModelCondType(modelDefinition, modelInputType);
        GraphQLObjectType modelPageType = generateModelPageType(modelDefinition, modelObject, codeRegistry);

        typeMap.put(modelObject.getName(), modelObject);
        typeMap.put(modelCondType.getName(), modelCondType);
        typeMap.put(modelInputType.getName(), modelInputType);
        typeMap.put(modelPageType.getName(), modelPageType);

        return typeMap;
    }

}
