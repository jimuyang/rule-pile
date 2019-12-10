package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuRuleDependency {

    private Long id;

    /**
     * 主规则definitionId
     */
    private Long definitionId;

    /**
     * 依赖的definitionId
     */
    private Long depDefinitionId;

    /**
     * 冗余
     */
    private Long depRuleId;

    /**
     * 冗余
     */
    private Integer depRuleVersion;

    private Integer deleted;

}
