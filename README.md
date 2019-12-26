# rule-pile

基于 Groovy 的决策和规则引擎

# 原理
基于 Java 运行时调用 Groovy 脚本的能力，可以让 Java 具备像脚本一样的灵活性。
核心代码：Java 执行 Groovy 脚本传参并获取结果
```java
/**
 * 生成新的groovyClass
 */
private Class buildGroovyClass(String groovyScript) throws RuleException {
    // 每个class都new一个loader 便于垃圾回收
    GroovyClassLoader loader = new GroovyClassLoader();
    try {
        return loader.parseClass(groovyScript);
    } catch (Exception e) {
        throw new RuleException("groovy脚本解析class出错:" + e.getMessage());
    }
}
···
// 获取groovyObject
GroovyObject go = (GroovyObject) clazz.newInstance();
// 传参并调用脚本的main方法 
Object result = groovyObject.invokeMethod("main", input);
```

# 实现
有了基本原理后，其实问题被转变成了``如何生成Groovy脚本？``，理所当然的回答是：开发写呗。这样的做法是可行的，但有几个问题：
1. 开发累死
2. 容易出错
3. 无法感受现有的逻辑
这里的解决方案是: 图编辑器->生成逻辑流程图->解析为Groovy脚本->执行。可见即可得
![](/assets/flow2.jpg)

## 图编辑器
图编辑器选择`g6`，准确来说选择开箱即用的`ant-design-pro`+`GGEditor`，前端要改的内容其实不多，为应对生成逻辑的需要，需要添加一些表单项即可
![](/assets/flow3.jpg)

## 解析图和逻辑依赖
编辑器的输出是`node`数组和`edge`数组，就是图上的节点和线们。后台解析这段json后，生成逻辑树。之后对逻辑树进行遍历后，生成最后的Groovy脚本。
逻辑树的遍历：
```java
void recursiveTravelNodeMap(Node node) throws RuleParseException {
    switch (node.getCategory()) {
        case "start":
           ···
        case "end":
           ···
        case "logic":
           ···
        case "common":
           ···
        default:
    }
    // node的后续节点
    for (ConnectNode target : node.getTargets()) {
        ···
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
```
为了复用现有逻辑，提供了一套逻辑依赖的解析：
```java
// 使用模版生成函数main方法 脚本入口
StringBuilder resultScript = new StringBuilder(String.format(template,
        mainRule.getRuleCode(), this.buildFuncOptions(mainRule.getOptions(), true)));
// 将依赖的规则函数体放入
for (RuRuleDefinition dependency : dependencies) {
    this.expandResultScript(resultScript, dependency);
}
return resultScript.toString();
```

## 具体设计
![](/assets/er.jpg)
