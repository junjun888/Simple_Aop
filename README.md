# cglib 手写 简单 aop 框架，实现方法级别的拦截


[TOC]


----

## 0缘起

一直对 java 的动态代理 还有 spring 的 aop 理解不深刻， 所以打算实现一个简单的 aop 目的是用于学习 cglib 和 aop 思想。


## 1思路


```
1 扫描 aop 包， 获取 aspect 的类

2 根据 切点 获取该切点的 类 和 方法

3 根据配置的 类 和 方法 为该类生成一个代理对象 

4 将改代理对象放入 bean Map 中

5 调用的时候 将代理对象 转换成需要的对象

```

## 2 使用


### Step 1: 定义被代理的实体类 
```
public class Test {

	public void doSomeThing() {
		System.out.println("do some thing...");
	}

	public void doWtihNotProxy() {
		System.out.println("do some thing with not proxy");
	}
}
```

### Step 2: 定义切点和切面， 并且继承 AbsMethodAdvance


```
package org.aop.demo;

import org.aop.annotion.aspect.Aspect;
import org.aop.annotion.aspect.PointCut;
import org.aop.proxy.AbsMethodAdvance;

/**
 * @Author: huangwenjun
 * @Description:
 * @Date: Created in 15:53  2018/4/18
 **/
@Aspect
public class TestAspect extends AbsMethodAdvance {

    /** 
	 * 全类名_方法名 （被拦截的类_被拦截的方法）
	 */
	@PointCut("org.aop.demo.Test_doSomeThing")
	public void testAspect() {
	}

	@Override
	public void doBefore() {
		System.out.println("do before");
	}

	@Override
	public void doAfter() {
		System.out.println("do after");
	}
}

```

### Step 3: 测试


```
    public static void main(String[] args) {
		// 模拟容器初始化
		ApplicationContext applicationContext = new ApplicationContext();
		ConcurrentHashMap<String, Object> proxyBeanMap = ApplicationContext.proxyBeanMap;

		// 生成的代理对象 默认为该类名的小写
		Test test = (Test) proxyBeanMap.get("test");
		test.doSomeThing();

		System.out.println("-------------");

		test.doWtihNotProxy();
	}
```

输出：


```
do before
do some thing...
do after
-------------
do some thing with not proxy
```

## 3 核心代码


### 3.1 ApplicationContext

```
public class ApplicationContext {

	/**
	 * 存放代理类的集合
	 */
	public static ConcurrentHashMap<String, Object> proxyBeanMap = new ConcurrentHashMap<String, Object>();

	static {
		initAopBeanMap("org.aop.demo");
	}

	/**
	 * 初始化 aop 容器
	 */
	public static void initAopBeanMap(String basePath) {
		try {
			Set<Class<?>> classSet = ClassUtil.getClassSet(basePath);

			for (Class clazz : classSet) {
				if (clazz.isAnnotationPresent(Aspect.class)) {
					//找到切面
					Method[] methods = clazz.getMethods();

					for(Method method : methods) {

						if (method.isAnnotationPresent(PointCut.class)) {
							// 找到切点
							PointCut pointCut = (PointCut) method.getAnnotations()[0];
							String pointCutStr = pointCut.value();
							String[] pointCutArr = pointCutStr.split("_");

							// 被代理的类名
							String className = pointCutArr[0];
							// 被代理的方法名
							String methodName = pointCutArr[1];

							// 根据切点 创建被代理对象
							Object targetObj = ReflectionUtil.newInstance(className);
							// 根据切面类创建代理者
							AbsMethodAdvance proxyer = (AbsMethodAdvance) ReflectionUtil.newInstance(clazz);
							// 设置代理的方法
							proxyer.setProxyMethodName(methodName);

							Object object = proxyer.createProxyObject(targetObj);

							if (object != null) {
								proxyBeanMap.put(targetObj.getClass().getSimpleName().toLowerCase(), object);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```


### 3.2 AbsMethodAdvance


```
public abstract class AbsMethodAdvance implements MethodInterceptor {

	/**
	 * 要被代理的目标对象
	 */
	private Object targetObject;

	/**
	 * 被代理的方法名
	 */
	private String proxyMethodName;

	/**
	 * 根据被代理对象 创建代理对象
	 * @param target
	 * @return
	 */
	public Object createProxyObject(Object target) {
		this.targetObject = target;
		// 该类用于生成代理对象
		Enhancer enhancer = new Enhancer();
		// 设置目标类为代理对象的父类
		enhancer.setSuperclass(this.targetObject.getClass());
		// 设置回调用对象为本身
		enhancer.setCallback(this);

		return enhancer.create();
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		Object result;

		String proxyMethod = getProxyMethodName();

		if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
			doBefore();
		}

		// 执行拦截的方法
		result = methodProxy.invokeSuper(proxy, args);

		if (StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())) {
			doAfter();
		}

		return result;
	}

	public abstract void doBefore();

	public abstract void doAfter();

	public String getProxyMethodName() {
		return proxyMethodName;
	}

	public void setProxyMethodName(String proxyMethodName) {
		this.proxyMethodName = proxyMethodName;
	}
}

```


## 4 完整代码


GitHub

> https://github.com/junjun888/Simple_Aop

----
