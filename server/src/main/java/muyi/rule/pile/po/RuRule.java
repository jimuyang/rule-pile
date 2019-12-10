package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuRule {

    private Long id;

    /**
     * 唯一code
     */
    private String code;

    /**
     * 规则名
     */
    private String name;

    /**
     * 限制使用的场景 计划废弃
     */
    private String limitScenes;

    /**
     * 场景id -1时为任何场景
     */
    private Long sceneId;

    /**
     * 软删标识
     */
    private Integer deleted;

}
