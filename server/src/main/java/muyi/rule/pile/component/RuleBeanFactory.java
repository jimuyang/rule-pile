package muyi.rule.pile.component;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import muyi.rule.pile.dao.RuRuleDao;
import muyi.rule.pile.exception.RuleException;
import muyi.rule.pile.po.RuRuleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Yang Fan
 * @date: 2019-07-15
 * @desc:
 */
@Component("ruleBeanFactory")
public class RuleBeanFactory {

    /**
     * Map <beanId, groovyClass>
     */
    private Map<Long, Class> groovyClassCache = new HashMap<>();

    @Autowired
    private RuRuleDao ruleDao;

    /**
     * 获取规则解析出的Groovy class
     */
    public Class getGroovyClass(Long beanId) throws RuleException {
        if (groovyClassCache.containsKey(beanId)) {
            return groovyClassCache.get(beanId);
        } else {
            RuRuleBean ruleBean = this.ruleDao.selectBean(beanId);
            if (ruleBean == null) {
                throw new RuleException("找不到规则实例");
            }
            Class groovyClass = this.buildGroovyClass(ruleBean.getContent());
            groovyClassCache.put(beanId, groovyClass);
            return groovyClass;
        }
    }

    /**
     * 获取规则生成的groovyObject
     */
    public GroovyObject getGroovyObject(Long beanId) throws RuleException {
        Class clazz = this.getGroovyClass(beanId);
        try {
            return (GroovyObject) clazz.newInstance();
        } catch (Exception e) {
            throw new RuleException("groovy class newInstance出错:" + e.getMessage());
        }
    }

    /**
     * 生成新的groovyClass
     */
    private Class buildGroovyClass(String groovyScript) throws RuleException {
        GroovyClassLoader loader = new GroovyClassLoader();
        try {
            return loader.parseClass(groovyScript);
        } catch (Exception e) {
            throw new RuleException("groovy脚本解析class出错:" + e.getMessage());
        }
    }


}
