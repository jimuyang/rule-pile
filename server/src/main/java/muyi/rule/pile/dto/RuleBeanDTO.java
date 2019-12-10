package muyi.rule.pile.dto;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-07-16
 * @desc:
 */
@Data
public class RuleBeanDTO {

    /**
     * 实例id
     */
    private Long beanId;

    /**
     * 0: groovy
     */
    private Integer beanType;

    /**
     * 由哪个
     */
    private Long definitionId;

    /**
     * 实例说明
     */
    private String remark;

    /**
     * 内容
     */
    private String content;

    /**
     * rule
     */
    private Long ruleId;

    private String ruleName;

//    private String logic;

    /**
     * ruleDefinition
     */
    private String defOptions;

    private String defContent;

}
