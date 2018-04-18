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
	 * 全类名_方法名
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
