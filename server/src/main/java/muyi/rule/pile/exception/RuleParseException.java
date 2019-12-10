package muyi.rule.pile.exception;

/**
 * @author: Yang Fan
 * @date: 2019-06-22
 * @desc:
 */
public class RuleParseException extends RuleException {

    public RuleParseException(String message) {
        super(message);
    }

    public RuleParseException(String code, String message) {
        super(code, message);
    }

    public RuleParseException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public RuleParseException(String code, Throwable cause) {
        super(code, cause);
    }
}
