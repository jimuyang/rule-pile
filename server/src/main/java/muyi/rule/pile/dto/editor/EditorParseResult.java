package muyi.rule.pile.dto.editor;

import lombok.Data;
import muyi.rule.pile.po.RuRuleDefinition;
import muyi.rule.pile.po.RuRuleDependency;

import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-06-24
 * @desc:
 */
@Data
public class EditorParseResult {

    /**
     * start node
     *
     */
    private Node startNode;

    /**
     * output nodes
     */
    private List<Node> outputNodes;

    /**
     * 自身主逻辑
     */
    private String mainLogic;

    /**
     * 自身options
     */
    private List<RuleOption> mainOptions;

    /**
     * 自身实例输入
     */
    private String mainInput;

    /**
     * 新写的codec规则
     */
    private List<RuRuleDefinition> codecRules;

    /**
     * 依赖已有的规则
     */
    private List<RuRuleDependency> dependencyRules;

}
