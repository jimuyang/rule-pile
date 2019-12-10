package muyi.rule.pile.service.impl;

import com.alibaba.fastjson.JSON;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import muyi.rule.pile.component.GGEditorParser;
import muyi.rule.pile.component.RuleBeanFactory;
import muyi.rule.pile.dao.RuRuleDao;
import muyi.rule.pile.dto.*;
import muyi.rule.pile.dto.editor.EditorParseResult;
import muyi.rule.pile.dto.editor.GGEditor;
import muyi.rule.pile.dto.editor.RuleOption;
import muyi.rule.pile.exception.RuleException;
import muyi.rule.pile.exception.RuleParseException;
import muyi.rule.pile.po.*;
import muyi.rule.pile.service.RuleService;
import muyi.rule.pile.util.CollectionUtil;
import muyi.rule.pile.util.NumberUtil;
import muyi.rule.pile.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Yang Fan
 * @date: 2019-06-24
 * @desc:
 */
@Service("ruleService")
public class RuleServiceImpl implements RuleService, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceImpl.class);

    private static final String RULE_BEAN_MAIN_METHOD = "main";

    private static final String GROOVY_FUNC_TEMPLATE = "def %s (def input, def _ %s) { %s } \n";

    private static final String JSON_PARSE_CODE = "input = new JsonSlurper().parseText(input);";

    private static final String GROOVY_MAIN_FUNC_WITH_JSON = "" +
            "import groovy.json.JsonSlurper;" +
            "def main (def input) {" +
            JSON_PARSE_CODE +
            "def _ = new HashMap();" +
            "_.GLOBAL = new HashMap();" +
            "def result = %s (input, _ %s);" +
            "if (result == null) throw new RuntimeException(\"无结果\");" +
            "result.extra = _.GLOBAL;" +
            "return result; } \n";

    @Autowired
    private RuleBeanFactory ruleBeanFactory;

    @Autowired
    private RuRuleDao ruleDao;

    /**
     * spring 用于提供给
     */
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * get
     */
    @Override
    public RuleDTO get(Long ruleId) throws RuleException {
        RuRule ruRule = this.ruleDao.selectRule(ruleId);
        if (ruRule == null) {
            throw new RuleException("rule not found");
        }
        return convert(ruRule);
    }

    /**
     * mget
     */
    @Override
    public List<RuleDTO> mget(List<Long> ruleIds) throws RuleException {
        if (CollectionUtil.isNullOrEmpty(ruleIds)) {
            return Collections.emptyList();
        }
        List<RuRule> rules = this.ruleDao.batchSelectRule(ruleIds);
        if (CollectionUtil.isNullOrEmpty(rules)) {
            return Collections.emptyList();
        }
        return rules.stream().map(RuleServiceImpl::convert).collect(Collectors.toList());
    }

    /**
     * 场景可用的规则列表
     */
    @Override
    public List<RuleDTO> availableRules(Long sceneId) throws RuleException {
        List<RuRule> rules;
        if (NumberUtil.nonZero(sceneId)) {
            rules = this.ruleDao.availableRulesBySceneId(sceneId);
        } else {
            rules = this.ruleDao.commonRules();
        }
        if (CollectionUtil.isNullOrEmpty(rules)) {
            return Collections.emptyList();
        }
        return rules.stream().map(RuleServiceImpl::convert).collect(Collectors.toList());
    }

    /**
     * 场景独占的规则列表
     */
    @Override
    public List<RuleDTO> exclusiveRules(Long sceneId) throws RuleException {
        if (NumberUtil.nullOrZero(sceneId)) {
            throw new RuleException("sceneId cannot be null");
        }
        List<RuRule> rules = this.ruleDao.exclusiveRulesBySceneId(sceneId);
        if (CollectionUtil.isNullOrEmpty(rules)) {
            return Collections.emptyList();
        }
        return rules.stream().map(RuleServiceImpl::convert).collect(Collectors.toList());
    }

    private static RuleDTO convert(RuRule rule) {
        if (rule == null) {
            return null;
        }
        RuleDTO to = new RuleDTO();
        to.setId(rule.getId());
        to.setCode(rule.getCode());
        to.setName(rule.getName());
        to.setLimitScenes(rule.getLimitScenes());
        return to;
    }

    /**
     * 新增一条规则
     */
    @Override
    public RuleDTO newRule(RuleDTO ruleDTO) throws RuleException {
        // ruleCode 不能重复
        RuRule existCodeRule = this.ruleDao.selectRuleByCode(ruleDTO.getCode());
        if (existCodeRule != null) {
            throw new RuleException("duplicate rule code");
        }

        RuRule ruRule = new RuRule();
        ruRule.setCode(ruleDTO.getCode());
        ruRule.setName(ruleDTO.getName());
        ruRule.setLimitScenes(ruleDTO.getLimitScenes());
        ruRule.setSceneId(ruleDTO.getSceneId()); // 未落库
        int i = this.ruleDao.insertRule(ruRule);
        if (i != 1) {
            throw new RuleException("insert new rule failed");
        }
        ruleDTO.setId(ruRule.getId());
        return ruleDTO;
    }

    /**
     * 更新规则名称
     */
    @Override
    public void updateRuleName(RuleDTO ruleDTO) throws RuleException {
        RuRule rule = new RuRule();
        rule.setId(ruleDTO.getId());
        rule.setName(ruleDTO.getName());
        int i = this.ruleDao.updateRuleName(rule);
        if (i != 1) {
            throw new RuleException("update rule name failed");
        }

    }

    /**
     * get 规则定义
     */
    @Override
    public RuleDefinitionDTO getDefinition(Long definitionId) throws RuleException {
        RuleDefinitionDTO result = new RuleDefinitionDTO();

        // ruleDefinition
        RuRuleDefinition ruleDefinition = this.ruleDao.selectDefinition(definitionId);
        if (ruleDefinition == null) {
            throw new RuleException("cannot find rule definition");
        }
        result.setDefinitionId(ruleDefinition.getId());
        result.setRuleId(ruleDefinition.getRuleId());
        result.setStoreId(ruleDefinition.getStoreId());
        result.setLogic(ruleDefinition.getLogic());
        result.setOptions(ruleDefinition.getOptions());
        result.setContent(ruleDefinition.getContent());

        // rule
        Long ruleId = ruleDefinition.getRuleId();
        if (NumberUtil.nonZero(ruleId)) {
            RuRule rule = this.ruleDao.selectRule(ruleId);
            if (rule == null) {
                throw new RuleException("cannot find rule");
            }
            result.setRuleCode(rule.getCode());
            result.setRuleName(rule.getName());
            result.setLimitScenes(rule.getLimitScenes());
        }

        // store
        Long storeId = ruleDefinition.getStoreId();
        if (NumberUtil.nonZero(storeId)) {
            RuEditorStore store = this.ruleDao.selectStore(storeId);
            if (store == null) {
                throw new RuleException("cannot find rule store");
            }
            result.setEditor(store.getContent());
        }
        return result;
    }

    /**
     * 通过ruleId获取最新的definition
     */
    @Override
    public RuleDefinitionDTO getDefinitionByRuleId(Long ruleId) throws RuleException {
        // rule
        RuRule rule = this.ruleDao.selectRule(ruleId);
        if (rule == null) {
            throw new RuleException("cannot find rule");
        }

        RuleDefinitionDTO definitionDTO = new RuleDefinitionDTO();
        definitionDTO.setRuleId(ruleId);
        definitionDTO.setRuleCode(rule.getCode());
        definitionDTO.setRuleName(rule.getName());
        definitionDTO.setLimitScenes(rule.getLimitScenes());

        // ruleDefinition
        RuRuleDefinition ruleDefinition = this.ruleDao.latestDefinitionByRuleId(ruleId);
        if (ruleDefinition == null) {
            LOGGER.info("rule no definition, ruleId:" + ruleId);
            return definitionDTO;
        }
        definitionDTO.setDefinitionId(ruleDefinition.getId());
        definitionDTO.setStoreId(ruleDefinition.getStoreId());
        definitionDTO.setLogic(ruleDefinition.getLogic());
        definitionDTO.setOptions(ruleDefinition.getOptions());
        definitionDTO.setContent(ruleDefinition.getContent());

        // store
        Long storeId = ruleDefinition.getStoreId();
        // noinspection Duplicates
        RuEditorStore store = this.ruleDao.selectStore(storeId);
        if (store == null) {
            throw new RuleException("cannot find rule store");
        }
        definitionDTO.setEditor(store.getContent());
        return definitionDTO;
    }

    /**
     * get 规则实例
     */
    @Override
    public RuleBeanDTO getBean(Long beanId) throws RuleException {
        if (NumberUtil.nullOrZero(beanId)) {
            throw new RuleException("beanId cannot be null");
        }
        RuRuleBean ruleBean = this.ruleDao.selectBean(beanId);
        if (ruleBean == null) {
            throw new RuleException("cannot find rule store");
        }
        RuleBeanDTO beanDTO = new RuleBeanDTO();
        beanDTO.setBeanId(ruleBean.getId());
        beanDTO.setBeanType(ruleBean.getType());
        beanDTO.setContent(ruleBean.getContent());
        beanDTO.setDefinitionId(ruleBean.getDefinitionId());

        // rule definition
        if (NumberUtil.nullOrZero(beanDTO.getDefinitionId())) {
            throw new RuleException("definitionId cannot be null");
        }
        RuRuleDefinition ruleDefinition = this.ruleDao.selectDefinition(beanDTO.getDefinitionId());
        if (ruleDefinition == null) {
            throw new RuleException("cannot find rule definition");
        }
        beanDTO.setRuleId(ruleDefinition.getRuleId());
//        beanDTO.setLogic(ruleDefinition.getLogic());
        beanDTO.setDefOptions(ruleDefinition.getOptions());

        // rule
        RuRule rule = this.ruleDao.selectRule(ruleDefinition.getRuleId());
        if (rule == null) {
            throw new RuleException("cannot find rule");
        }
        beanDTO.setRuleName(rule.getName());

        return beanDTO;
    }

    /**
     * mget 规则实例
     */
    @Override
    public List<RuleBeanDTO> mgetBean(List<Long> beanIds) throws RuleException {
        if (CollectionUtil.isNullOrEmpty(beanIds)) {
            return Collections.emptyList();
        }
        Set<Long> beanIdSet = new HashSet<>(beanIds);
        List<RuleBeanDTO> result = new ArrayList<>(beanIdSet.size());
        // 循环sql
        for (Long beanId : beanIdSet) {
            try {
                result.add(this.getBean(beanId));
            } catch (Exception ignore) {
            }
        }
        return result;
    }

    /**
     * 获取规则定义的最新实例
     */
    @Override
    public RuleBeanDTO getBeanByDefinitionId(Long definitionId) throws RuleException {
        if (NumberUtil.nullOrZero(definitionId)) {
            throw new RuleException("definitionId cannot be null");
        }
        RuRuleBean ruleBean = this.ruleDao.latestBeanByDefinitionId(definitionId);
        if (ruleBean == null) {
            throw new RuleException("cannot find the latest bean of definition");
        }
        return this.getBean(ruleBean.getId());
    }

    /**
     * 场景下的规则定义
     *
     * @param exclusive 是否独占规则
     */
    @Override
    public List<RuleDefinitionDTO> mgetDefinitionBySceneId(Long sceneId, boolean exclusive) throws RuleException {
        List<RuleDTO> rules;
        if (exclusive) {
            rules = this.exclusiveRules(sceneId);
        } else {
            rules = this.availableRules(sceneId);
        }
        if (CollectionUtil.isNullOrEmpty(rules)) {
            return Collections.emptyList();
        }
        List<RuleDefinitionDTO> result = new ArrayList<>(rules.size());
        for (RuleDTO rule : rules) {
            RuleDefinitionDTO definition;
            try {
                definition = this.getDefinitionByRuleId(rule.getId());
            } catch (Exception e) {
                continue;
            }
            if (NumberUtil.nonZero(definition.getDefinitionId())) {
                result.add(definition);
            }
        }
        return result;
    }

    /**
     * 规则可引用的规则 默认独占
     */
    @Override
    public List<RuleDefinitionDTO> partnerDefinitions(Long ruleId) throws RuleException {
        RuleDTO ruleDTO = this.get(ruleId);
        try {
            Long sceneId = Long.parseLong(ruleDTO.getLimitScenes());
            List<RuleDefinitionDTO> list = this.mgetDefinitionBySceneId(sceneId, true);
            List<RuleDefinitionDTO> commonList = list.stream()
                    .filter(dto -> dto.getRuleName().contains("通用"))
                    .collect(Collectors.toList());
            List<RuleDefinitionDTO> otherList = list.stream()
                    .filter(dto -> !dto.getRuleName().contains("通用"))
                    .collect(Collectors.toList());
            commonList.addAll(otherList);
            return commonList;
        } catch (Exception e) {
            LOGGER.error("query partner rule definitions error", e);
            return Collections.emptyList();
        }
    }

    /**
     * 保存editor
     */
    @Override
    public Long saveEditor(String editorJson) throws RuleException {
        // save in ru_editor_store
        RuEditorStore store = new RuEditorStore();
//        store.setContent(JSON.toJSONString(editor));
        store.setContent(editorJson);
        int i = this.ruleDao.insertStore(store);
        if (i != 1) {
            throw new RuleException("save editor store failed");
        }
        return store.getId();
    }

    /**
     * 解析editor
     */
    private EditorParseResult parseEditor(GGEditor editor) throws RuleException {
        GGEditorParser parser = new GGEditorParser();
        return parser.parse(editor);
    }

    private EditorParseResult parseEditor(String editorJson) throws RuleException {
        GGEditor editor;
        try {
            editor = JSON.parseObject(editorJson, GGEditor.class);
        } catch (Exception e) {
            throw new RuleException("GGEditor JSON deserialize failed");
        }
        return this.parseEditor(editor);
    }

    /**
     * 发布规则
     */
    @Override
    public Long publishRule(Long ruleId, String editorJson, String remark) throws RuleException {
        Long definitionId = this.parseAndSaveDefinition(ruleId, editorJson);
        return this.buildAndSaveBean(definitionId, remark);
    }

    /**
     * 前端传入的editor解析落库
     */
    @Override
    public Long parseAndSaveDefinition(Long ruleId, String editorJson) throws RuleException {
        // save in ru_editor_store
        Long storeId = this.saveEditor(editorJson);

        if (NumberUtil.nullOrZero(ruleId)) {
            throw new RuleParseException("ruleId cannot be null");
        }
        // rule
        RuRule ruRule = this.ruleDao.selectRule(ruleId);
        if (ruRule == null) {
            throw new RuleException("cannot find the rule");
        }

        // parse editor
        EditorParseResult parseResult = this.parseEditor(editorJson);

        // rename codec rule
        String mainLogic = parseResult.getMainLogic();
        mainLogic = mainLogic.replaceAll("_codec_", ruRule.getCode() + "_codec_");

        // build rule definition content
        RuleDefContentDTO content = new RuleDefContentDTO();
        content.setStartNode(parseResult.getStartNode());
        content.setOutputNodes(parseResult.getOutputNodes());
        content.setHasCodec(CollectionUtil.isNotEmpty(parseResult.getCodecRules()));

        // insert main rule definition
        RuRuleDefinition mainDefinition = new RuRuleDefinition();
        mainDefinition.setRuleId(ruRule.getId());
        mainDefinition.setRuleCode(ruRule.getCode());
        mainDefinition.setStoreId(storeId);
        mainDefinition.setOptions(JSON.toJSONString(parseResult.getMainOptions()));
        mainDefinition.setLogic(mainLogic);
        mainDefinition.setInput(parseResult.getMainInput());
        mainDefinition.setContent(JSON.toJSONString(content));
        int i = this.ruleDao.insertDefinition(mainDefinition);
        if (i != 1) {
            throw new RuleException("save main rule definition failed");
        }

        /* handle dependencies */
        List<RuRuleDependency> dependencies = parseResult.getDependencyRules();
        // update dependency definitionId to Latest
        for (RuRuleDependency dependency : dependencies) {
            RuleDefinitionDTO latestDefinition = this.getDefinitionByRuleId(dependency.getDepRuleId());
            dependency.setDepDefinitionId(latestDefinition.getDefinitionId());
        }

        // codec rule definition
        for (RuRuleDefinition codecRuleDefinition : parseResult.getCodecRules()) {
            // rename codec rule
            codecRuleDefinition.setRuleCode(ruRule.getCode() + codecRuleDefinition.getRuleCode());
            i = this.ruleDao.insertDefinition(codecRuleDefinition);
            if (i != 1) {
                throw new RuleException("save codec rule definition failed");
            }
            // add dependency to codec rule
            RuRuleDependency newDependency = new RuRuleDependency();
            newDependency.setDepRuleId(codecRuleDefinition.getRuleId());
            newDependency.setDepDefinitionId(codecRuleDefinition.getId());
            dependencies.add(newDependency);
        }

        // save rule dependencies
        for (RuRuleDependency dependency : dependencies) {
            // set main rule definitionId
            dependency.setDefinitionId(mainDefinition.getId());
            i = this.ruleDao.insertDependency(dependency);
            if (i != 1) {
                throw new RuleException("save rule dependency failed");
            }
        }
        return mainDefinition.getId();
    }

    /**
     * 生成可执行的rule实例groovy
     */
    @Override
    public String buildLatestRuleScript(Long ruleId) throws RuleException {
        RuRuleDefinition definition = this.ruleDao.latestDefinitionByRuleId(ruleId);
        return this.buildRuleScript(definition.getId());
    }

    /**
     * 生成可执行的rule实例groovy
     */
    @Override
    public String buildRuleScript(Long definitionId) throws RuleException {
        if (NumberUtil.nullOrZero(definitionId)) {
            throw new RuleException("definitionId cannot be null");
        }
        // 这里bind_master来保证找到刚保存的定义和依赖
        RuRuleDefinition mainRule = this.ruleDao.selectDefinitionBM(definitionId);
        // dependencyMap <ruleCode, ruleDefinition>
        Map<String, RuRuleDefinition> dependencyMap = new HashMap<>();
        dependencyMap.putIfAbsent(mainRule.getRuleCode(), mainRule);
        // dependencies
        List<RuRuleDependency> dependencies = this.ruleDao.dependenciesBM(mainRule.getId());
        for (RuRuleDependency dependency : dependencies) {
            this.recursiveDependency(dependency.getDepDefinitionId(), dependencyMap);
        }
        return this.buildGroovyScript(mainRule, dependencyMap.values());
    }

    /**
     * 生成并保存规则实例
     */
    @Override
    public Long buildAndSaveBean(Long definitionId, String remark) throws RuleException {
        String script = this.buildRuleScript(definitionId);

        RuRuleBean ruleBean = new RuRuleBean();
        ruleBean.setContent(script);
        ruleBean.setRemark(remark);
        ruleBean.setDefinitionId(definitionId);
        ruleBean.setType(0); // groovy

        int i = this.ruleDao.insertBean(ruleBean);
        if (i != 1) {
            throw new RuleException("insert rule bean failed");
        }
        return ruleBean.getId();
    }

    /**
     * 运行规则实例
     */
    @Override
    public RuleResultDTO runBean(Long beanId, String input) throws RuleException {
        GroovyObject groovyObject = this.ruleBeanFactory.getGroovyObject(beanId);
        if (groovyObject == null) {
            throw new RuleException("cannot get groovy object");
        }
        // 注入springContext
        try {
            groovyObject.setProperty("springContext", applicationContext);
        } catch (Exception e) {
            LOGGER.error("注入springContext",e);
        }
        Map result;
        try {
            result = (Map) groovyObject.invokeMethod(RULE_BEAN_MAIN_METHOD, input);
        } catch (Exception e) {
            throw new RuleException("groovy object run error: " + e.getMessage());
        }
        RuleResultDTO resultDTO = new RuleResultDTO();
        resultDTO.setOutput(String.valueOf(result.get("output")));
        resultDTO.setOutputNode(String.valueOf(result.get("node")));
        resultDTO.setHit(result.get("hit") != null ? (int) result.get("hit") : -1);
        try {
            if (result.get("extra") != null) {
                @SuppressWarnings("unchecked")
                Map<String, String> extra = (Map<String, String>) result.get("extra");
                resultDTO.setExtra(extra);
            } else {
                resultDTO.setExtra(Collections.emptyMap());
            }
        } catch (Exception e) {
            resultDTO.setExtra(Collections.emptyMap());
        }
        return resultDTO;
    }

    /**
     * 单纯运行一段脚本 执行main方法
     */
    @Override
    public Object runGroovy(String groovyScript, String input) throws RuleException {
        GroovyClassLoader loader = new GroovyClassLoader();
        try {
            Class clazz = loader.parseClass(groovyScript);
            GroovyObject groovyObject = (GroovyObject) clazz.newInstance();
            return groovyObject.invokeMethod(RULE_BEAN_MAIN_METHOD, input);
        } catch (Exception e) {
            throw new RuleException("groovy script run error:" + e.getMessage());
        }
    }

    /**
     * 递归处理依赖
     * 这里有循环执行sql 但问题不大
     */
    private void recursiveDependency(Long definitionId, Map<String, RuRuleDefinition> dependencyMap) {
        RuRuleDefinition ruleDefinition = this.ruleDao.selectDefinition(definitionId);
        dependencyMap.putIfAbsent(ruleDefinition.getRuleCode(), ruleDefinition);
        // dependencies
        List<RuRuleDependency> dependencies = this.ruleDao.dependencies(ruleDefinition.getId());
        for (RuRuleDependency dependency : dependencies) {
            this.recursiveDependency(dependency.getDepDefinitionId(), dependencyMap);
        }
    }

    /**
     * 生成最终的可执行groovy脚本
     *
     * @param mainRule     主规则定义
     * @param dependencies 依赖的规则
     */
    private String buildGroovyScript(RuRuleDefinition mainRule, Collection<RuRuleDefinition> dependencies) throws RuleException {
        String template = GROOVY_MAIN_FUNC_WITH_JSON;
        if (StringUtil.isNotEmpty(mainRule.getInput()) && !mainRule.getInput().trim().startsWith("{")) {
            // 认为输入不是json 去掉json解析的代码
            template = template.replace(JSON_PARSE_CODE, "");
        }
        // 使用模版生成函数main方法 脚本入口
        StringBuilder resultScript = new StringBuilder(String.format(template,
                mainRule.getRuleCode(), this.buildFuncOptions(mainRule.getOptions(), true)));
        // 将依赖的规则函数体放入
        for (RuRuleDefinition dependency : dependencies) {
            this.expandResultScript(resultScript, dependency);
        }
        return resultScript.toString();
    }

    /**
     * 扩充最终脚本
     */
    private void expandResultScript(StringBuilder resultScript, RuRuleDefinition dependency) throws RuleException {
        // 函数体放入
        resultScript.append(this.buildGroovyFunc(dependency));
        // import块插入
        try {
            if (StringUtil.isNotEmpty(dependency.getContent())) {
                RuleDefContentDTO contentDTO = JSON.parseObject(dependency.getContent(), RuleDefContentDTO.class);
                if (StringUtil.isNotEmpty(contentDTO.getImportBlock())) {
                    resultScript.insert(0, contentDTO.getImportBlock());
                }
            }
        } catch (Exception e) {
            throw new RuleException("insert import block error:" + e.getMessage());
        }
    }

    private String buildGroovyFunc(RuRuleDefinition ruleDefinition) throws RuleException {
        return String.format(GROOVY_FUNC_TEMPLATE,
                ruleDefinition.getRuleCode(),
                this.buildFuncOptions(ruleDefinition.getOptions(), false),
                ruleDefinition.getLogic());
    }

    private String buildFuncOptions(String optionStr, boolean useValue) throws RuleException {
        if (StringUtil.isNotEmpty(optionStr)) {
            List<RuleOption> options;
            try {
                options = JSON.parseArray(optionStr, RuleOption.class);
            } catch (Exception e) {
                throw new RuleException("parse dependency rule option json failed");
            }
            StringBuilder optionBuilder = new StringBuilder();
            for (RuleOption option : options) {
                if (useValue) {
                    if (StringUtil.isEmpty(option.getValue())) {
                        optionBuilder.append(", null");
                    } else {
                        optionBuilder.append(", \"").append(option.getValue()).append("\"");
                    }
                } else {
                    optionBuilder.append(", def $").append(option.getKey());
                }
            }
            return optionBuilder.toString();
        }
        return "";
    }

}
