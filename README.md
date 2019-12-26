# rule-pile

基于 Groovy 的决策和规则引擎

#

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
![](/assets/flow1.jpg)

