package muyi.rule.pile.dao;

import muyi.rule.pile.po.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author: Yang Fan
 * @date: 2019-06-24
 * @desc:
 */
@Mapper
public interface RuRuleDao {
    /**
     * ru_rule
     */

    @Select("select * from `ru_rule` where id = #{ruleId} and deleted = 0")
    RuRule selectRule(Long ruleId);

    @Select("<script>" +
            "select * from `ru_rule` where id in " +
            "<foreach collection='list' open='(' item='id' separator=',' close=')'>#{id}</foreach> " +
            "and deleted = 0" +
            "</script>")
    List<RuRule> batchSelectRule(List<Long> ruleId);

    @Select("select * from `ru_rule` where (limit_scenes = '' or limit_scenes like '%${sceneId}%') and deleted = 0")
    List<RuRule> availableRulesBySceneId(@Param("sceneId") Long sceneId);

    @Select("select * from `ru_rule` where limit_scenes like '%${sceneId}%' and deleted = 0")
    List<RuRule> exclusiveRulesBySceneId(@Param("sceneId") Long sceneId);

    @Select("select * from `ru_rule` where limit_scenes = '' and deleted = 0")
    List<RuRule> commonRules();

    @Select("select * from `ru_rule` where code = #{code}")
    RuRule selectRuleByCode(String code);

    @Insert("insert into `ru_rule`(code, name, limit_scenes) values(#{code}, #{name}, #{limitScenes})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertRule(RuRule rule);

    @Update("update `ru_rule` set name = #{name} where id = #{id}")
    int updateRuleName(RuRule rule);

    @Update("update `ru_rule` set deleted = 1 where id = #{ruleId}")
    int deleteRule(Long ruleId);


    /**
     * ru_editor_store
     */

    @Select("select * from `ru_editor_store` where id = #{storeId} and deleted = 0")
    RuEditorStore selectStore(Long storeId);

    @Insert("insert into `ru_editor_store`(content) values(#{content}) ")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertStore(RuEditorStore store);

    /**
     * ru_rule_definition
     */

    @Select("select * from `ru_rule_definition` where id = #{definitionId} and deleted = 0")
    RuRuleDefinition selectDefinition(Long definitionId);

    @Select("select * from `ru_rule_definition` where id = #{definitionId} and deleted = 0 and 'bind_master' = 'bind_master'")
    RuRuleDefinition selectDefinitionBM(Long definitionId);

    @Select("select * from `ru_rule_definition` where rule_id = #{ruleId} and deleted = 0 order by id desc limit 1")
    RuRuleDefinition latestDefinitionByRuleId(Long ruleId);

    @Insert("insert into `ru_rule_definition`(store_id, rule_id, rule_code, options, logic, input, content) " +
            "values (#{storeId}, #{ruleId}, #{ruleCode}, #{options}, #{logic}, #{input}, #{content})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertDefinition(RuRuleDefinition definition);

    /**
     * ru_rule_bean
     */

    @Select("select * from `ru_rule_bean` where id = #{beanId} and deleted = 0")
    RuRuleBean selectBean(Long beanId);

    @Select("select * from `ru_rule_bean` where definition_id = #{definitionId} and deleted = 0 order by id desc limit 1")
    RuRuleBean latestBeanByDefinitionId(Long definitionId);

    @Insert("insert into `ru_rule_bean` (type, definition_id, remark, content) " +
            "values(#{type}, #{definitionId}, #{remark}, #{content})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertBean(RuRuleBean bean);

    @Update("update `ru_rule_bean` set deleted = 1 where id = #{beanId}")
    int deleteBean(Long beanId);

    /**
     * ru_rule_dependency
     */

    @Select("select * from `ru_rule_dependency` where id = #{dependencyId} and deleted = 0")
    RuRuleDependency selectDependency(Long dependencyId);

    @Select("select * from `ru_rule_dependency` where dep_definition_id = #{depDefinitionId} and deleted = 0")
    List<RuRuleDependency> dependOn(Long depDefinitionId);

    @Select("select * from `ru_rule_dependency` where definition_id = #{definitionId} and deleted = 0")
    List<RuRuleDependency> dependencies(Long definitionId);

    @Select("select * from `ru_rule_dependency` where definition_id = #{definitionId} and deleted = 0 and 'bind_master' = 'bind_master'")
    List<RuRuleDependency> dependenciesBM(Long definitionId);

    @Insert("insert into `ru_rule_dependency` (definition_id, dep_definition_id, dep_rule_id) " +
            "values (#{definitionId}, #{depDefinitionId}, #{depRuleId})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertDependency(RuRuleDependency dependency);

    /**
     * ru_scene
     */

    @Insert("insert into `ru_scene` (id, name) values (#{id}, #{name})")
    int insertSceneById(RuScene scene);

    @Insert("insert into `ru_scene` (name) values (#{name})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertScene(RuScene scene);

    @Select("select * from `ru_scene` where id = #{id} and deleted = 0")
    RuScene selectScene(Long sceneId);

}
