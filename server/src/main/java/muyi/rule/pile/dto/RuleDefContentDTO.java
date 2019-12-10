package muyi.rule.pile.dto;

import lombok.Data;
import muyi.rule.pile.dto.editor.Node;

import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-09-21
 * @desc: 规则定义内的content
 */
@Data
public class RuleDefContentDTO {

    private Node startNode;

    private List<Node> outputNodes;

    /**
     * codec规则的import块处理
     */
    private String importBlock;

    /**
     * 内部有codec
     */
    private Boolean hasCodec;
}
