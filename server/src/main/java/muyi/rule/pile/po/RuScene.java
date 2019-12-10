package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuScene {

    /**
     * 主键
     */
    private Long id;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 软删标识
     */
    private Integer deleted;
}
