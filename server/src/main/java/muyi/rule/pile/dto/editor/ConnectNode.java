package muyi.rule.pile.dto.editor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Yang Fan
 * @date: 2019-06-22
 * @desc:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectNode {

    private Node node;

    private int targetAnchor;

    private int sourceAnchor;

    private String valve;

}
