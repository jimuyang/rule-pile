package muyi.rule.pile.dto.editor;

import lombok.Data;

import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-06-22
 * @desc: 调用rule所需 === def `output` = `ruleCode` (`input`, ...`options`);
 */
@Data
public class RuleInvoke {

    /**
     * 要调用的规则id
     */
    private Long ruleId;

    /**
     * 要调用的规则code
     */
    private String ruleCode;

    /**
     * 要调用的规则definitionId
     */
    private Long definitionId;

    /**
     * 额外选项
     */
    private List<RuleOption> options;

}
