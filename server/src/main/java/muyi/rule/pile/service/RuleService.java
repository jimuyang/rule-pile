package muyi.rule.pile.service;


import muyi.rule.pile.dto.RuleBeanDTO;
import muyi.rule.pile.dto.RuleDTO;
import muyi.rule.pile.dto.RuleDefinitionDTO;
import muyi.rule.pile.dto.RuleResultDTO;
import muyi.rule.pile.exception.RuleException;

import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-06-26
 * @desc:
 */
public interface RuleService {
    /**
     * get
     */
    RuleDTO get(Long ruleId) throws RuleException;

    /**
     * mget
     */
    List<RuleDTO> mget(List<Long> ruleIds) throws RuleException;

    /**
     * 场景可用的规则列表
     */
    List<RuleDTO> availableRules(Long sceneId) throws RuleException;

    /**
     * 场景独占的规则列表
     */
    List<RuleDTO> exclusiveRules(Long sceneId) throws RuleException;

    /**
     * 新增一条规则
     */
    RuleDTO newRule(RuleDTO ruleDTO) throws RuleException;

    /**
     * 更新规则名称
     */
    void updateRuleName(RuleDTO ruleDTO) throws RuleException;

    /**
     * get 规则定义
     */
    RuleDefinitionDTO getDefinition(Long definitionId) throws RuleException;

    /**
     * 通过ruleId获取最新的definition
     */
    RuleDefinitionDTO getDefinitionByRuleId(Long ruleId) throws RuleException;

    /**
     * get 规则实例
     */
    RuleBeanDTO getBean(Long beanId) throws RuleException;

    /**
     * mget 规则实例
     */
    List<RuleBeanDTO> mgetBean(List<Long> beanIds) throws RuleException;

    /**
     * 获取规则定义的最新实例
     */
    RuleBeanDTO getBeanByDefinitionId(Long definitionId) throws RuleException;

    /**
     * 场景下的规则定义
     *
     * @param exclusive 是否独占规则
     */
    List<RuleDefinitionDTO> mgetDefinitionBySceneId(Long sceneId, boolean exclusive) throws RuleException;

    /**
     * 规则可引用的规则
     */
    List<RuleDefinitionDTO> partnerDefinitions(Long ruleId) throws RuleException;

    /**
     * 保存editor
     */
    Long saveEditor(String editorJsor) throws RuleException;

    /**
     * 发布规则
     */
    Long publishRule(Long ruleId, String editorJson, String remark) throws RuleException;

    /**
     * 前端传入的editor解析落库
     */
    Long parseAndSaveDefinition(Long ruleId, String editorJson) throws RuleException;

    /**
     * 生成可执行的rule实例groovy
     */
    String buildLatestRuleScript(Long ruleId) throws RuleException;

    /**
     * 生成可执行的rule实例groovy
     */
    String buildRuleScript(Long definitionId) throws RuleException;

    /**
     * 生成并保存规则实例
     */
    Long buildAndSaveBean(Long definitionId, String remark) throws RuleException;

    /**
     * 运行规则实例
     */
    RuleResultDTO runBean(Long beanId, String input) throws RuleException;

    /**
     * 单纯运行一段脚本 执行main方法
     */
    Object runGroovy(String groovyScript, String input) throws RuleException;
}
