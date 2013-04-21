package benworks.spring.core.expression;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpELTest {
	@Test
	public void helloWorld() {
		// 1）创建解析器：SpEL使用ExpressionParser接口表示解析器，提供SpelExpressionParser默认实现；
		ExpressionParser parser = new SpelExpressionParser();

		// 2）解析表达式：使用ExpressionParser的parseExpression来解析相应的表达式为Expression对象。
		Expression expression = parser.parseExpression("('Hello' + ' World').concat(#end)");

		// 3）构造上下文：准备比如变量定义等等表达式需要的上下文数据。
		EvaluationContext context = new StandardEvaluationContext();
		context.setVariable("end", "!");

		// 4）求值：通过Expression接口的getValue方法根据上下文获得表达式值。
		Assert.assertEquals("Hello World!", expression.getValue(context));
	}

	@Test
	public void testParserContext() {
		ExpressionParser parser = new SpelExpressionParser();
		ParserContext parserContext = new ParserContext() {
			@Override
			public boolean isTemplate() {
				return true;
			}

			@Override
			public String getExpressionPrefix() {
				return "#{";
			}

			@Override
			public String getExpressionSuffix() {
				return "}";
			}
		};
		String template = "#{'Hello '}#{'World!'}";
		Expression expression = parser.parseExpression(template, parserContext);
		Assert.assertEquals("Hello World!", expression.getValue());
	}

	@Test
	public void testVariableExpression() {
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext();
		context.setVariable("variable", "haha");
		context.setVariable("variable", "haha");
		String result1 = parser.parseExpression("#variable").getValue(context, String.class);
		Assert.assertEquals("haha", result1);

		context = new StandardEvaluationContext("haha");
		String result2 = parser.parseExpression("#root").getValue(context, String.class);
		Assert.assertEquals("haha", result2);
		String result3 = parser.parseExpression("#this").getValue(context, String.class);
		Assert.assertEquals("haha", result3);
	}

	/**
	 * 目前只支持类静态方法注册为自定义函数；SpEL使用StandardEvaluationContext的registerFunction方法进行注册自定义函数， 其实完全可以使用setVariable代替，两者其实本质是一样的；
	 * 此处可以看出“registerFunction”和“setVariable”都可以注册自定义函数，但是两个方法的含义不一样， 推荐使用“registerFunction”方法注册自定义函数。
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@Test
	public void testFunctionExpression() throws SecurityException, NoSuchMethodException {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		Method parseInt = Integer.class.getDeclaredMethod("parseInt", String.class);
		context.registerFunction("parseInt", parseInt);
		context.setVariable("parseInt2", parseInt);
		String expression1 = "#parseInt('3') == #parseInt2('3')";
		boolean result1 = parser.parseExpression(expression1).getValue(context, boolean.class);
		Assert.assertEquals(true, result1);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testClassTypeExpression() {
		ExpressionParser parser = new SpelExpressionParser();
		// java.lang包类访问
		
 		Class<String> result1 = parser.parseExpression("T(String)").getValue(Class.class);
		Assert.assertEquals(String.class, result1);
		// 其他包类访问
		String expression2 = "T(benworks.spring.core.expression.SpELTest)";
		Class<String> result2 = parser.parseExpression(expression2).getValue(Class.class);
		Assert.assertEquals(SpELTest.class, result2);
		// 类静态字段访问
		int result3 = parser.parseExpression("T(Integer).MAX_VALUE").getValue(int.class);
		Assert.assertEquals(Integer.MAX_VALUE, result3);
		// 类静态方法调用
		int result4 = parser.parseExpression("T(Integer).parseInt('1')").getValue(int.class);
		Assert.assertEquals(1, result4);
	}

}
