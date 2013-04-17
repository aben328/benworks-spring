/**
 * 
 */
package benworks.spring.aspectj;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author jy
 * 
 */
@Aspect
public class SystemArchitecture {

	@Pointcut("within(benworks.spring.aspectj..*)")
	public void inWebLayer() {
	}

}
