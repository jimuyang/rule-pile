package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuRuleBean {

    private Long id;

    /**
     * 0: groovy
     */
    private Integer type;

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

    private Integer deleted;
}
