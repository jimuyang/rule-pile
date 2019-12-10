package muyi.rule.pile.exception;


import lombok.Getter;

/**
 * @author: Yang Fan
 * @date: 2019-06-25
 * @desc:
 */
@Getter
public class RuleException extends Exception {

    private String code;

    public RuleException(String message) {
        super(message);
    }

    public RuleException(String code, String message) {
        super(message);
        this.code = code;
    }

    public RuleException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RuleException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
