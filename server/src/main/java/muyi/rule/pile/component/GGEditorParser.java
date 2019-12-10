package muyi.rule.pile.component;

import com.alibaba.fastjson.JSON;
import muyi.rule.pile.constant.LogicType;
import muyi.rule.pile.dto.RuleDefContentDTO;
import muyi.rule.pile.dto.editor.*;
import muyi.rule.pile.exception.RuleParseException;
import muyi.rule.pile.po.RuRuleDefinition;
import muyi.rule.pile.po.RuRuleDependency;
import muyi.rule.pile.util.CollectionUtil;
import muyi.rule.pile.util.NumberUtil;
import muyi.rule.pile.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Yang Fan
 * @date: 2019-06-21
 * @desc: 主要生成自身主逻辑 和 依赖的codec逻辑
 */
public class GGEditorParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GGEditorParser.class);

    // 脚本函数名前缀
    private static final String CODEC_FUNC_NAME_PREFIX = "_codec_";

    private static final String BOOLEAN_TRUE = "true";
    private static final String BOOLEAN_FALSE = "false";

    // 整个main函数的开头 继承GLOBAL
    private static final String MAIN_START =
            "def t = new HashMap(); t.GLOBAL = _.GLOBAL; _ = t; def _result = new HashMap();";

    // main函数参数赋值给 _
    private static final String MAIN_OPTION_USE_TEMPLATE = "_.$%s = $%s;";

    // main函数return语句模版
    private static final String RETURN_TEMPLATE =
            "_result.output = %s; _result.hit = %s; _result.node = '%s'; return _result;";

    // 已有函数结果赋值
    private static final String RULE_INVOKE_AND_ASSIGN_TEMPLATE =
            "_.$%s_result = %s (%s);_.$%s = _.$%s_result.output;_.$%s = _.$%s_result.hit == 1;";

    // 脚本函数结果赋值
    private static final String CODEC_RULE_INVOKE_AND_ASSIGN_TEMPLATE =
            "_.$%s = %s (%s);";

    // codec规则的import块提取
    private static final Pattern CODEC_IMPORT_PATTERN = Pattern.compile("/\\*import\\{((\\n|.)*?)}\\*/");

    private Map<String, Node> nodeMap;

    private Node startNode;

    private List<Node> outputNodes;

    /**
     * main logic
     */
    private StringBuilder mainBuilder = new StringBuilder();

    private List<RuleOption> mainOptions = new ArrayList<>();

    private String mainInput = "";

    /**
     * codec anonymous rule
     */
    private List<RuRuleDefinition> codecRules = new ArrayList<>();

    private int codecRuleCount = 0;

    /**
     * dependency rule declare
     */
    private List<RuRuleDependency> dependencyRules = new ArrayList<>();

    /**
     * 解析处理前端GGEditor传入的json
     */
    public EditorParseResult parse(GGEditor editor) throws RuleParseException {
        EditorParseResult result = new EditorParseResult();

        // nodeMap startNode
        this.buildNodeMap(editor);
        if (startNode == null) {
            throw new RuleParseException("start node not found");
        }
        if (CollectionUtil.isNullOrEmpty(outputNodes)) {
            throw new RuleParseException("end node not found");
        }
        result.setStartNode(startNode);
        result.setOutputNodes(outputNodes);

        // 递归遍历nodeMap
        this.recursiveTravelNodeMap(startNode);

        result.setMainInput(mainInput);
        result.setMainLogic(mainBuilder.toString());
        result.setMainOptions(mainOptions);
        result.setCodecRules(codecRules);
        result.setDependencyRules(dependencyRules);
        return result;
    }


    private void buildNodeMap(GGEditor editor) throws RuleParseException {
        List<Node> nodes = editor.getNodes();
        List<Edge> edges = editor.getEdges();

        this.outputNodes = new ArrayList<>();
        // build nodeMap
        this.nodeMap = new HashMap<>(); // Map<nodeId, node>
        for (Node node : nodes) {
            nodeMap.put(node.getId(), node);
            if ("start".equalsIgnoreCase(node.getCategory())) {
                this.startNode = node;
            } else if ("end".equalsIgnoreCase(node.getCategory())) {
                this.outputNodes.add(node);
            }
        }

        // 设置node的targets和sources
        for (Edge edge : edges) {
            Node sourceNode = nodeMap.get(edge.getSource());
            Node targetNode = nodeMap.get(edge.getTarget());

            ConnectNode connectNode = new ConnectNode();
            connectNode.setNode(targetNode);
            connectNode.setTargetAnchor(edge.getTargetAnchor());
            connectNode.setSourceAnchor(edge.getSourceAnchor());
            connectNode.setValve(edge.getValve());
            sourceNode.getTargets().add(connectNode);

            connectNode = new ConnectNode();
            connectNode.setNode(sourceNode);
            connectNode.setTargetAnchor(edge.getTargetAnchor());
            connectNode.setSourceAnchor(edge.getSourceAnchor());
            connectNode.setValve(edge.getValve());
            sourceNode.getSources().add(connectNode);
        }
    }

    private void recursiveTravelNodeMap(Node node) throws RuleParseException {
        if (StringUtil.isEmpty(node.getCategory())) {
            throw new RuleParseException("node no category");
        }
        // 特别处理 end 和 logic
        switch (node.getCategory()) {
            case "start":
                this.mainBuilder.append(MAIN_START);

                List<RuleOption> options = node.getOptions();
                if (StringUtil.isNotEmpty(node.getInput())) {
                    this.mainInput = node.getInput();
                }
                if (CollectionUtil.isNotEmpty(options)) {
                    this.mainOptions = options;
                    for (RuleOption option : options) {
                        // 主规则选项定义成 _ 的变量
                        this.mainBuilder.append(String.format(MAIN_OPTION_USE_TEMPLATE, option.getKey(), option.getKey()));
                    }
                } else {
                    this.mainOptions = Collections.emptyList();
                }
                break;
            case "end":
                String output = node.getStream();
                // 不以$或"开头的话 认为是纯字符串
                if (!node.getStream().startsWith("$") && !node.getStream().startsWith("\"")) {
                    output = "\"" + output + "\"";
                }
                output = handle$(output);
                Integer hit = node.getHit();
                this.mainBuilder.append(String.format(RETURN_TEMPLATE, output, hit, node.getId()));
                break;
            case "logic":
                List<SingleLogic> logicList = node.getLogic();
                if (CollectionUtil.isNullOrEmpty(logicList)) {
                    break;
                }
                for (SingleLogic logic : logicList) {
                    this.handleLogic(logic);
                }
                break;
            case "common":
                break;
            default:
        }

        // 处理和生成 if 逻辑
        String stream = StringUtil.isEmpty(node.getStream()) ? BOOLEAN_TRUE : node.getStream();
        stream = handle$(stream);

        ConnectNode defaultTarget = null;

        for (ConnectNode target : node.getTargets()) {
            if (StringUtil.isEmpty(target.getValve())) {
                // 没有valve的认为是defaultTarget
                defaultTarget = target;
                continue;
            }
            String valve = handle$(target.getValve());
            // if (node.stream.equals(target.valve)) {}
            if (BOOLEAN_TRUE.equalsIgnoreCase(stream) && BOOLEAN_TRUE.equalsIgnoreCase(valve)) {
                this.recursiveTravelNodeMap(target.getNode());
            } else {
                this.mainBuilder.append("if ((").append(stream).append(").equals(").append(valve).append(")) {");
                this.recursiveTravelNodeMap(target.getNode());
                this.mainBuilder.append("}\n");
            }
        }
        if (defaultTarget != null) {
            this.recursiveTravelNodeMap(defaultTarget.getNode());
        }
    }

    private void handleLogic(SingleLogic logic) throws RuleParseException {
        if (logic.getType() == null) {
            throw new RuleParseException("logic no type");
        }
        String funcName;
        String funcParam;
        if (logic.getType() == LogicType.CODEC.getType()) {
            // handle /*import{}*/ 特殊的注释语法 用来声明import
            RuleDefContentDTO content = null;
            Matcher matcher = CODEC_IMPORT_PATTERN.matcher(logic.getCode());
            if (matcher.find()) {
                content = new RuleDefContentDTO();
                content.setImportBlock(matcher.group(1));
            }
            // handle $ in logic.code
            logic.setCode(handle$(logic.getCode()));

            // 增加codec rule
            funcName = CODEC_FUNC_NAME_PREFIX + (codecRuleCount++);
            funcParam = StringUtil.isEmpty(logic.getInput()) ? "input" : logic.getInput() + ", _";

            RuRuleDefinition codecRule = new RuRuleDefinition();
            codecRule.setRuleId(0L);
            codecRule.setStoreId(0L);
            codecRule.setRuleCode(funcName);
            codecRule.setLogic(logic.getCode());
            codecRule.setOptions("");
            codecRule.setInput("");
            codecRule.setContent(content == null ? "" : JSON.toJSONString(content));
            this.codecRules.add(codecRule);

            // _.$output = funcName (funcParam);
            String output = StringUtil.isNotEmpty(logic.getOutput()) ? logic.getOutput().trim() : "temp";
            this.mainBuilder.append(String.format(CODEC_RULE_INVOKE_AND_ASSIGN_TEMPLATE, output, funcName, funcParam));

        } else if (logic.getType() == LogicType.RULE_INVOKE.getType()) {
            RuleInvoke ruleInvoke = logic.getInvoke();
            if (NumberUtil.nullOrZero(ruleInvoke.getDefinitionId())) {
                throw new RuleParseException("rule invoke logic no rule definition");
            }
            funcName = ruleInvoke.getRuleCode();

            // rule invoke params (input, _, ...)
            List<RuleOption> options = ruleInvoke.getOptions();
            int paramNum = CollectionUtil.isNotEmpty(options) ? options.size() + 2 : 2;
            List<String> params = new ArrayList<>(paramNum);
            params.add(StringUtil.isEmpty(logic.getInput()) ? "input" : logic.getInput());
            params.add("_");
            if (CollectionUtil.isNotEmpty(options)) {
                options.forEach(option -> params.add(StringUtil.isEmpty(option.getValue())
                        ? "null" : option.getValue().startsWith("\"")
                        ? option.getValue() : option.getValue().startsWith("$")
                        ? handle$(option.getValue()) : stringWrap(option.getValue())));
            }
            funcParam = StringUtil.join(", ", params);

            RuRuleDependency dependencyRule = new RuRuleDependency();
            // 这里申明依赖的rule
            dependencyRule.setDepDefinitionId(ruleInvoke.getDefinitionId());
            dependencyRule.setDepRuleId(ruleInvoke.getRuleId());
            this.dependencyRules.add(dependencyRule);

            // _.$result = funcName (funcParam);
            // _.$output = _.$result.output;
            // _.$hit = _.$result.hit;
            String outputVar = StringUtil.isNotEmpty(logic.getOutput()) ? logic.getOutput().trim() : "temp";
            String hitVar = StringUtil.isNotEmpty(logic.getHit()) ? logic.getHit().trim() : outputVar + "_hit";
            this.mainBuilder.append(this.buildRuleInvokeScript(outputVar, hitVar, funcName, funcParam));
        } else {
            throw new RuleParseException("unsupported logic type: " + logic.getType());
        }
    }

    private String buildRuleInvokeScript(String output, String hit, String funcName, String funcParam) {
        return String.format(RULE_INVOKE_AND_ASSIGN_TEMPLATE,
                output, funcName, funcParam, output, output, hit, output);
    }

    /**
     * 所有的 $ 转为 _.$ 但 \$ 不变
     */
    public static String handle$(String str) {
        return str == null ? null : str.replaceAll("\\$", "_.\\$")
                .replaceAll("\\\\_.\\$", "\\\\\\$");
    }

    private static String stringWrap(String str) {
        return str == null ? null : "\"" + str + "\"";
    }

}













