package muyi.rule.pile.dto.editor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class Node {

    /**
     * nodeId
     */
    private String id;

    /**
     * 类别 start end common logic
     */
    private String category;

    /**
     * label文案
     */
    private String label;

    /**
     * 判断流
     */
    private String stream;

    /**
     * input节点的输入
     */
    private String input;

    /**
     * output节点 是否命中
     */
    private Integer hit;

    /**
     * output节点 打分系数
     */
    private String factor;

    /**
     * start节点的options
     */
    private List<RuleOption> options;

    /**
     * 可选 logic节点用
     */
    private List<SingleLogic> logic;

    /**
     * sources
     */
    private transient List<ConnectNode> sources = new ArrayList<>();

    /**
     * targets
     */
    private transient List<ConnectNode> targets = new ArrayList<>();

}
