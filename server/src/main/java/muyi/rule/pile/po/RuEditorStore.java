package muyi.rule.pile.po;

import lombok.Data;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc:
 */
@Data
public class RuEditorStore {

    /**
     * 主键
     */
    private Long id;

    /**
     * 存储内容
     */
    private String content;

    private Integer deleted;

}
