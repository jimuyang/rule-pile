package muyi.rule.pile.dao;

import muyi.rule.pile.po.RuScene;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: Yang Fan
 * @date: 2019/12/9
 * @desc:
 */
@SpringBootTest
class RuRuleDaoTest {

    @Autowired
    private RuRuleDao ruleDao;

    @Test
    public void test() {
        RuScene scene = new RuScene();
        scene.setName("testScene");
        ruleDao.insertScene(scene);

        RuScene queryScene = ruleDao.selectScene(scene.getId());
        System.out.println(queryScene.toString());
    }

}