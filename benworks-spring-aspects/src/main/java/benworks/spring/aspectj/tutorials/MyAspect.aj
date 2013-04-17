package benworks.spring.aspectj.tutorials;

/**
 * 一个切面可以简单的理解为解决跨越多个模块的交叉关注点问题(大多数 是一些系统级的或者核心关注点外围的问题)的模块。
 * 
 * @author jy
 * 
 */
public aspect MyAspect {
	void around():call(void Component.business*(..))
	{
		// validateUser();
		// beginTransaction();
		proceed();
		// endTransaction();
		// writeLogInfo();
	}

}
