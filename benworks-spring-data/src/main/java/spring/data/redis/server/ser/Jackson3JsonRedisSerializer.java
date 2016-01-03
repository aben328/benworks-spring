package spring.data.redis.server.ser;
import java.nio.charset.Charset;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class Jackson3JsonRedisSerializer<T> implements RedisSerializer<T> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private final JavaType javaType;

	public static ObjectMapper objectMapper = new ObjectMapper();

	static final byte[] EMPTY_ARRAY = new byte[0];

	static boolean isEmpty(byte[] data) {
		return (data == null || data.length == 0);
	}

	/**
	 * Creates a new {@link Jackson2JsonRedisSerializer} for the given target {@link Class}.
	 * @param type
	 */
	public Jackson3JsonRedisSerializer(Class<T> type) {
		this.javaType = getJavaType(type);
	}

	/**
	 * Creates a new {@link Jackson2JsonRedisSerializer} for the given target {@link JavaType}.
	 * @param javaType
	 */
	public Jackson3JsonRedisSerializer(JavaType javaType) {
		this.javaType = javaType;
	}

	@SuppressWarnings("unchecked")
	public T deserialize(byte[] bytes) throws SerializationException {

		if (isEmpty(bytes)) {
			return null;
		}
		try {
			return (T) objectMapper.readValue(bytes, 0, bytes.length, javaType);
		} catch (Exception ex) {
			throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	public byte[] serialize(Object o) throws SerializationException {
		return object2Byte(o);
	}

	public static String object2Json(Object o) {
		if (o == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(o);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}

	public static byte[] object2Byte(Object o) {
		if (o == null) {
			return EMPTY_ARRAY;
		}

		try {
			return objectMapper.writeValueAsBytes(o);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON byte: " + ex.getMessage(), ex);
		}
	}

	public static <T> T json2Object(String json, Class<T> type) {
		if (json == null) {
			return null;
		}
		try {
			return (T) objectMapper.readValue(json, type);
		} catch (Exception ex) {
			throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	public static <T> T json2Object(byte[] json, Class<T> type) {
		if (json == null) {
			return null;
		}
		try {
			return (T) objectMapper.readValue(json, type);
		} catch (Exception ex) {
			throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	protected JavaType getJavaType(Class<?> clazz) {
		return TypeFactory.defaultInstance().constructType(clazz);
	}
}
