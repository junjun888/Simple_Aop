package org.aop.util;

/**
 * @Author: huangwenjun
 * @Description:
 * @Date: Created in 17:21  2018/4/4
 **/
public class StringUtils {

	public static boolean isNotBlank(String str) {
		if (str != null && str.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlank(String str) {
		return !isNotBlank(str);
	}
}
