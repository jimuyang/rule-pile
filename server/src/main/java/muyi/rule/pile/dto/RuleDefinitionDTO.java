package muyi.rule.pile.dto;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-07-15
 * @desc:
 */
@Data
public class RuleDefinitionDTO {

    /**
     * rule
     */
    private Long ruleId;

    private String ruleCode;

    private String ruleName;

    private String limitScenes;

    /**
     * rule store
     */
    private Long storeId;

    private String editor;

    /**
     * rule definition
     */
    private Long definitionId;

    private String options;

    private String logic;

    /**
     * 规则定义的存储
     *
     * @see RuleDefContentDTO
     */
    private String content;

    public String getRuleIdStr() {
        return String.valueOf(ruleId);
    }

    public String getStoreIdStr() {
        return String.valueOf(storeId);
    }

    public String getDefinitionIdStr() {
        return String.valueOf(definitionId);
    }


}

