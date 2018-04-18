package org.aop;

import org.aop.demo.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: huangwenjun
 * @Description:
 * @Date: Created in 15:57  2018/4/18
 **/
public class Main {

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
}
