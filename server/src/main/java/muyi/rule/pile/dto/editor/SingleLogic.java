package muyi.rule.pile.dto.editor;

import lombok.Data;
import muyi.rule.pile.constant.LogicType;

/**
 * @author: Yang Fan
 * @date: 2019-06-22
 * @desc: output = func(input, ...options);
 */
@Data
public class SingleLogic {

    /**
     * 类型 1 手书 0 调用
     *
     * @see LogicType
     */
    private Integer type;

    /**
     * 输入表达式
     */
    private String input;

    /**
     * 规则调用
     */
    private RuleInvoke invoke;

    /**
     * 手书code
     */
    private String code;

    /**
     * 输出赋值
     */
    private String output;

    /**
     * 输出命中赋值
     */
    private String hit;
}
