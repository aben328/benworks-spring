/**
 * 
 */
package benworks.spring.core.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

/**
 * @author Ben
 */
public class ResourceTest {

	@Test
	public void testByteArrayResouce() {
		Resource resource = new ByteArrayResource("Hello World!".getBytes());
		if (resource.exists()) {
			dumpStream(resource);
		}
	}

	private void dumpStream(Resource resource) {
		InputStream is = null;
		try {
			is = resource.getInputStream();
			byte[] descBytes = new byte[is.available()];
			is.read(descBytes);
			System.out.println(new String(descBytes));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e2) {

			}
		}
	}

	// InputStreamResource代表java.io.InputStream字节流，
	// 对于“getInputStream ”操作将直接返回该字节流，因此只能读取一次该字节流，即“isOpen”永远返回true。
	@Test
	public void testInputStreamResource() {
		ByteArrayInputStream bis = new ByteArrayInputStream("Hello World!".getBytes());
		Resource resource = new InputStreamResource(bis);
		if (resource.exists()) {
			dumpStream(resource);
		}
		Assert.assertEquals(true, resource.isOpen());
	}

	@Test
	public void testFileResource() {
		File file = new File("C:/Windows/System32/drivers/etc/hosts.txt");
		Resource resource = new FileSystemResource(file);
		if (resource.exists()) {
			dumpStream(resource);
		}
		// FileSystemResource代表java.io.File资源，对于“getInputStream ”操作将返回底层文件的字节流，
		// “isOpen”将永远返回false，从而表示可多次读取底层文件的字节流。
		Assert.assertEquals(false, resource.isOpen());//
	}

	@Test
	public void testClasspathResourceByDefaultClassLoader() throws IOException {
		// Resource resource = new ClassPathResource("benworks/spring/core/io/test1.properties");//wrong
		Resource resource = new ClassPathResource("test1.properties");// right
		if (resource.exists()) {
			dumpStream(resource);
		}
		System.out.println("path:" + resource.getFile().getAbsolutePath());
		Assert.assertEquals(false, resource.isOpen());
	}

	@Test
	public void testClasspathResourceByClassLoader() throws IOException {
		ClassLoader cl = this.getClass().getClassLoader();
		Resource resource = new ClassPathResource("test1.properties", cl);
		if (resource.exists()) {
			dumpStream(resource);
		}
		System.out.println("path:" + resource.getFile().getAbsolutePath());
		Assert.assertEquals(false, resource.isOpen());
	}

	@Test
	public void testClasspathResourceByClass() throws IOException {
		Class<? extends ResourceTest> clazz = this.getClass();
		Resource resource1 = new ClassPathResource("test1.properties", clazz);
		if (resource1.exists()) {
			dumpStream(resource1);
		}
		System.out.println("path:" + resource1.getFile().getAbsolutePath());
		Assert.assertEquals(false, resource1.isOpen());

		Resource resource2 = new ClassPathResource("test1.properties", this.getClass());
		if (resource2.exists()) {
			dumpStream(resource2);
		}
		System.out.println("path:" + resource2.getFile().getAbsolutePath());
		Assert.assertEquals(false, resource2.isOpen());
	}

	@Test
	public void classpathResourceTestFromJar() throws IOException {
		Resource resource = new ClassPathResource("overview.html");
		if (resource.exists()) {
			dumpStream(resource);
		}
		System.out.println("path:" + resource.getURL().getPath());
		Assert.assertEquals(false, resource.isOpen());
	}

	@Test
	public void testResourceLoad() {
		ResourceLoader loader = new DefaultResourceLoader();
		Resource resource = loader.getResource("classpath:cn/javass/spring/chapter4/test1.txt");
		// 验证返回的是ClassPathResource
		Assert.assertEquals(ClassPathResource.class, resource.getClass());
		Resource resource2 = loader.getResource("file:cn/javass/spring/chapter4/test1.txt");
		// 验证返回的是ClassPathResource
		Assert.assertEquals(UrlResource.class, resource2.getClass());
		Resource resource3 = loader.getResource("cn/javass/spring/chapter4/test1.txt");
		// 验证返默认可以加载ClasspathResource
		Assert.assertTrue(resource3 instanceof ClassPathResource);
	}

}
