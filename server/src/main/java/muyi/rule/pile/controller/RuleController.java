package muyi.rule.pile.controller;

import muyi.rule.pile.dto.RuleDTO;
import muyi.rule.pile.exception.RuleException;
import muyi.rule.pile.service.RuleService;
import org.apache.tomcat.util.digester.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Yang Fan
 * @date: 2019/12/10
 * @desc:
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;


    @GetMapping("/test")
    public RuleDTO test() throws RuleException {
        return this.ruleService.get(1L);
//        throw new RuleException("test exception");
    }

}
