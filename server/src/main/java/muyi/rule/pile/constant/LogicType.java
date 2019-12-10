package muyi.rule.pile.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: Yang Fan
 * @date: 2019-06-23
 * @desc:
 */
@Getter
@AllArgsConstructor
public enum LogicType {

    /**
     * 手书逻辑
     */
    CODEC(1),

    /**
     * 规则调用
     */
    RULE_INVOKE(0),
    ;

    private final int type;
}
