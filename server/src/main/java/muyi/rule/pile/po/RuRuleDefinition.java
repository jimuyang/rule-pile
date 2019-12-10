package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuRuleDefinition {

    /**
     * definition id
     */
    private Long id;

    /**
     * rule store id
     */
    private Long storeId;

    /**
     * ruleId为0时 为codecRule
     */
    private Long ruleId;

    /**
     * 冗余 也放codecFuncName
     */
    private String ruleCode;

    /**
     * demo输入
     */
    private String input;

    /**
     * 额外选项
     */
    private String options;

    /**
     * 自身逻辑
     */
    private String logic;

    /**
     * 用于非自身逻辑的存储
     */
    private String content;

    private Integer deleted;

}
