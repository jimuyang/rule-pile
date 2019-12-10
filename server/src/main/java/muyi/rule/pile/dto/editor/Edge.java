package muyi.rule.pile.dto.editor;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class Edge {

    /**
     * edgeId
     */
    private String id;

    /**
     * 文案
     */
    private String label;

    /**
     * 判断阀
     */
    private String valve;

    /**
     * 出发节点
     */
    private String source;

    private Integer sourceAnchor;

    /**
     * 到达节点
     */
    private String target;

    private Integer targetAnchor;

}
