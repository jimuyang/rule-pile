package muyi.rule.pile.dto.editor;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-22
 * @desc:
 */
@Data
public class RuleOption {

    /**
     * 关键标识
     */
    private String key;

    /**
     * 描述
     */
    private String desc;

    /**
     * 调用时实际传参
     */
    private String value;

    /**
     * 类型 Array Object
     */
    private String type;

}
