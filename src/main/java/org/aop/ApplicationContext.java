package org.aop;

import org.aop.annotion.aspect.Aspect;
import org.aop.annotion.aspect.PointCut;
import org.aop.proxy.AbsMethodAdvance;
import org.aop.util.ClassUtil;
import org.aop.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: huangwenjun
 * @Description:
 * @Date: Created in 15:25  2018/4/18
 **/
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
