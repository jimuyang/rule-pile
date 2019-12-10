package muyi.rule.pile.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: Yang Fan
 * @date: 2019-07-17
 * @desc:
 */
@Data
public class RuleResultDTO {

    private String output;

    private String outputNode;

    private Integer hit;

    private BigDecimal factor;

    private Map<String, String> extra;

}
