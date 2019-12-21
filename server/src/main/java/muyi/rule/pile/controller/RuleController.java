package muyi.rule.pile.controller;

import muyi.rule.pile.dao.RuRuleDao;
import muyi.rule.pile.dto.RuleDTO;
import muyi.rule.pile.exception.RuleException;
import muyi.rule.pile.po.RuRule;
import muyi.rule.pile.service.RuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    @Autowired
    private RuRuleDao ruleDao;

    @Autowired
    private RuleService ruleService;


    @GetMapping("/test")
    public RuleDTO test() throws RuleException {
        RuRule rule = ruleDao.selectRule(1L);
        if (rule == null) {
            rule = new RuRule();
            rule.setCode("FirstCode");
            rule.setName("第一个规则");
            rule.setLimitScenes("");
            ruleDao.insertRule(rule);
        }
        LOGGER.info("第一条规则: {}", rule);

        return this.ruleService.get(1L);
//        throw new RuleException("test exception");
    }

}
