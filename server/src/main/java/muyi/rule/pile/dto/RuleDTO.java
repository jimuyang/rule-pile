package muyi.rule.pile.dto;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-07-01
 * @desc:
 */
@Data
public class RuleDTO {

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
     * 限制使用的场景
     */
    private String limitScenes;

    /**
     * 场景id -1时为任何场景
     */
    private Long sceneId;


}
