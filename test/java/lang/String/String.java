import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * String类表示字符串.
 * Java程序中的所有字符串文字,比如"abc", 实现为该类的实例
 * <p>
 * Strings是个一个常量; 他们的值在创建后不能被修改。
 * String buffers 支持可变的字符串。
 * 因为字符串对象是不可变的，所以它可以被共享。
 * 比如：
 * String str = "abc";
 * 等价于：
 * char data[] = {'a', 'b', 'c'};
 * String str = new String(data);
 * 下面是一些如何使用字符串的例子：
 * System.out.println("abc");
 * String cde = "cde";
 * System.out.println("abc" + cde);
 * String c = "abc".substring(2,3);
 * String d = cde.substring(1, 2);
 * <p>
 * String类包含方法对每个字符序列进行检查，为了比较字符串，为了搜索字符串，为了提取子字符串，
 * The class {@code String} includes methods for examining
 * individual characters of the sequence, for comparing strings, for
 * searching strings, for extracting substrings, and for creating a
 * copy of a string with all characters translated to uppercase or to
 * lowercase. Case mapping is based on the Unicode Standard version
 * specified by the {@link java.lang.Character Character} class.
 * <p>
 * The Java language provides special support for the string
 * concatenation operator (&nbsp;+&nbsp;), and for conversion of
 * other objects to strings. String concatenation is implemented
 * through the {@code StringBuilder}(or {@code StringBuffer})
 * class and its {@code append} method.
 * String conversions are implemented through the method
 * {@code toString}, defined by {@code Object} and
 * inherited by all classes in Java. For additional information on
 * string concatenation and conversion, see Gosling, Joy, and Steele,
 * <i>The Java Language Specification</i>.
 * <p>
 * <p> Unless otherwise noted, passing a <tt>null</tt> argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 * <p>
 * <p>A {@code String} represents a string in the UTF-16 format
 * in which <em>supplementary characters</em> are represented by <em>surrogate
 * pairs</em> (see the section <a href="Character.html#unicode">Unicode
 * Character Representations</a> in the {@code Character} class for
 * more information).
 * Index values refer to {@code char} code units, so a supplementary
 * character uses two positions in a {@code String}.
 * <p>The {@code String} class provides methods for dealing with
 * Unicode code points (i.e., characters), in addition to those for
 * dealing with Unicode code units (i.e., {@code char} values).
 *
 * @author Lee Boynton
 * @author Arthur van Hoff
 * @author Martin Buchholz
 * @author Ulf Zibis
 * @see java.lang.Object#toString()
 * @see java.lang.StringBuffer
 * @see java.lang.StringBuilder
 * @see java.nio.charset.Charset
 * @since JDK1.0
 */
public final class String implements java.io.Serializable, Comparable<String>, CharSequence {

	/**
	 * A Comparator that orders {@code String} objects as by
	 * {@code compareToIgnoreCase}. This comparator is serializable.
	 * <p>
	 * Note that this Comparator does <em>not</em> take locale into account,
	 * and will result in an unsatisfactory ordering for certain locales.
	 * The java.text package provides <em>Collators</em> to allow
	 * locale-sensitive ordering.
	 *
	 * @see java.text.Collator#compare(String, String)
	 * @since 1.2
	 */
	public static final Comparator<String> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator();
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = -6849794470754667710L;
	/**
	 * 本质就是字符数组。String类型持有私有访问权限的字符数组。
	 * Java程序将字符串"abc"编译成String类型的实例对象
	 */
	private final char value[];
	/**
	 * 定义int类型的hash作为hash值
	 */
	private int hash; // Default to 0

	/**
	 * 无参构造
	 */
	public String() {
		this.value = "".value;
	}

	/**
	 * "abc123"字符串作为入参的构造器
	 */
	public String(String original) {
		this.value = original.value;
		this.hash = original.hash;
	}

	/**
	 * 用字符数组作为入参的构造器
	 * 复制数组
	 * 拷贝值和大小给定义的value
	 */
	public String(char value[]) {
		this.value = Arrays.copyOf(value, value.length);
	}

	/**
	 * 用字符数组、开始位置、总数  作为入参的构造器
	 * 使用Arrays.copyOfRange生成一个新数组
	 */
	public String(char value[], int offset, int count) {
		if (offset < 0) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (count <= 0) {
			if (count < 0) {
				throw new StringIndexOutOfBoundsException(count);
			}
			if (offset <= value.length) {
				this.value = "".value;
				return;
			}
		}
		// Note: offset or count might be near -1>>>1.
		if (offset > value.length - count) {
			throw new StringIndexOutOfBoundsException(offset + count);
		}
		this.value = Arrays.copyOfRange(value, offset, offset + count);
	}

	/**
	 * 整数、开始位置、总数作为入参的构造器
	 *
	 * @param codePoints
	 * @param offset
	 * @param count
	 */
	public String(int[] codePoints, int offset, int count) {
		if (offset < 0) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (count <= 0) {
			if (count < 0) {
				throw new StringIndexOutOfBoundsException(count);
			}
			if (offset <= codePoints.length) {
				this.value = "".value;
				return;
			}
		}
		// Note: offset or count might be near -1>>>1.
		if (offset > codePoints.length - count) {
			throw new StringIndexOutOfBoundsException(offset + count);
		}

		final int end = offset + count;
		//BmpCodePoint代码点是65535是2的16次方，刚好是两个字节（即一个字）的大小。在超出两个字节后只能算是有效的代码点，并非是BmpCodePoint代码点。
		int n = count;
		for (int i = offset; i < end; i++) {
			int c = codePoints[i];
			if (Character.isBmpCodePoint(c)) {
				continue;
			} else if (Character.isValidCodePoint(c)) {
				n++;
			} else {
				throw new IllegalArgumentException(Integer.toString(c));
			}
		}
		//char类型刚好是2个字符，满足BmpCodePoint代码点的int类型整数强制转换成char类型
		final char[] v = new char[n];

		for (int i = offset, j = 0; i < end; i++, j++) {
			int c = codePoints[i];
			if (Character.isBmpCodePoint(c)) {
				v[j] = (char) c;
			} else {
				Character.toSurrogates(c, v, j++);
			}
		}

		this.value = v;
	}

	/**
	 * ASC码作为入参的构造器
	 *
	 * @param ascii
	 * @param hibyte
	 * @param offset
	 * @param count
	 */

	@Deprecated
	public String(byte ascii[], int hibyte, int offset, int count) {
		checkBounds(ascii, offset, count);
		char value[] = new char[count];

		if (hibyte == 0) {
			for (int i = count; i-- > 0; ) {
				value[i] = (char) (ascii[i + offset] & 0xff);
			}
		} else {
			hibyte <<= 8;
			for (int i = count; i-- > 0; ) {
				value[i] = (char) (hibyte | (ascii[i + offset] & 0xff));
			}
		}
		this.value = value;
	}

	/**
	 * ASC编码作为入参的构造器
	 *
	 * @param ascii
	 * @param hibyte
	 */
	@Deprecated
	public String(byte ascii[], int hibyte) {
		this(ascii, hibyte, 0, ascii.length);
	}

	/**
	 * 字节数组作为入参的构造器
	 *
	 * @param bytes
	 * @param offset
	 * @param length
	 * @param charsetName
	 */
	public String(byte bytes[], int offset, int length, String charsetName) throws UnsupportedEncodingException {
		if (charsetName == null) {
			throw new NullPointerException("charsetName");
		}
		checkBounds(bytes, offset, length);
		this.value = StringCoding.decode(charsetName, bytes, offset, length);
	}

	/**
	 *
	 */
	public String(byte bytes[], int offset, int length, Charset charset) {
		if (charset == null) {
			throw new NullPointerException("charset");
		}
		checkBounds(bytes, offset, length);
		this.value = StringCoding.decode(charset, bytes, offset, length);
	}

	/**
	 */
	public String(byte bytes[], String charsetName) throws UnsupportedEncodingException {
		this(bytes, 0, bytes.length, charsetName);
	}

	/**
	 */
	public String(byte bytes[], Charset charset) {
		this(bytes, 0, bytes.length, charset);
	}

	/**
	 */
	public String(byte bytes[], int offset, int length) {
		checkBounds(bytes, offset, length);
		this.value = StringCoding.decode(bytes, offset, length);
	}

	/**
	 */
	public String(byte bytes[]) {
		this(bytes, 0, bytes.length);
	}

	/**
	 * 使用Stringbuffer作为入参的构造器
	 */
	public String(StringBuffer buffer) {
		synchronized (buffer) {
			this.value = Arrays.copyOf(buffer.getValue(), buffer.length());
		}
	}

	/**
	 */
	public String(StringBuilder builder) {
		this.value = Arrays.copyOf(builder.getValue(), builder.length());
	}

	/**
	 * 为了共享数组的速度，构造私有函数
	 */
	String(char[] value, boolean share) {
		// assert share : "unshared not supported";
		this.value = value;
	}

	/* 用于限制字节数组的公告私有实用方法
	 */
	private static void checkBounds(byte[] bytes, int offset, int length) {
		if (length < 0) {
			throw new StringIndexOutOfBoundsException(length);
		}
		if (offset < 0) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (offset > bytes.length - length) {
			throw new StringIndexOutOfBoundsException(offset + length);
		}
	}

	/**
	 * 搜索char数组的位置
	 * 由字符串和StringBuilder共享的字符串来搜索，
	 * 来源是被搜索的字符串，
	 * 目标也是被搜索的字符串。
	 *
	 * @param source 被搜索的字符串
	 * @param sourceOffset 抵消源字符串
	 * @param sourceCount 来源的总数
	 * @param target 搜索目标
	 * @param fromIndex 搜索指针
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount, String target, int fromIndex) {
		return indexOf(source, sourceOffset, sourceCount, target.value, 0, target.value.length, fromIndex);
	}

	/**
	 * 搜索char数组的位置
	 *
	 * @param source 被搜索的字符串
	 * @param sourceOffset 抵消源字符串
	 * @param sourceCount 来源的总数
	 * @param target 搜索目标
	 * @param targetOffset 抵消目标字符串
	 * @param targetCount 目标总数
	 * @param fromIndex 搜索指针
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/**
			 * 寻找第一个char
			 */
			if (source[i] != first) {
				while (++i <= max && source[i] != first) {
					;
				}
			}

			/**
			 * 找到第一个char后，着眼于V2
			 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++) {
					;
				}

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	/**
	 * 搜索char数组的最后位置
	 *
	 * @param source 被搜索的字符串
	 * @param sourceOffset 抵消源字符串
	 * @param sourceCount 来源的总数
	 * @param target 搜索目标
	 * @param fromIndex 搜索指针
	 *
	 * @return 字符串位置
	 */
	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, String target, int fromIndex) {
		return lastIndexOf(source, sourceOffset, sourceCount, target.value, 0, target.value.length, fromIndex);
	}

	/**
	 * 搜索char数组的最后位置
	 *
	 * @param source 被搜索的字符串
	 * @param sourceOffset 抵消源字符串
	 * @param sourceCount 来源的总数
	 * @param target 搜索目标
	 * @param targetOffset 抵消目标字符串
	 * @param targetCount 目标总数
	 * @param fromIndex 搜索指针
	 *
	 * @return
	 */
	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex) {
		//检查参数，返回可能性，对于一致性来说不检查空字符串
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		//空字符串总是匹配
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar:
		while (true) {
			while (i >= min && source[i] != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source[j--] != target[k--]) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	/**
	 * 返回一个新的副本是指定的不同副本拼接而成
	 * 内部实例化一个StringJoiner对象，用于循环拼接
	 *
	 * @param delimiter 分界符
	 * @param elements 多个拼接元素
	 *
	 * @return
	 */
	public static String join(CharSequence delimiter, CharSequence... elements) {
		//参数为null就报空指针
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		//		新建一个StringJoiner类
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}

	/**
	 * 返回一个新的副本是指定的不同副本拼接而成
	 *
	 * @param delimiter 分界符
	 * @param elements List类型的元素
	 *
	 * @return
	 */
	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}

	/**
	 * 使用指定格式的字符和参数，返回格式化后的字符串
	 * 一直使用的区域是返回的
	 *
	 * @param format 格式化的字符串
	 * @param args 格式字符串中使用格式说明符引用的参数，
	 * 如果参数多余格式说明符号，多余参数就被忽略。
	 * 变量可能为0。
	 * 参数的最大数量被定义为Java数组的最大限度根据JAVA&trade;虚拟机规范。
	 * null的行为会被转换。
	 *
	 * @throws java.util.IllegalFormatException 如果字符串包含非法字符,与给定的参数不相容的格式说明符,
	 * 格式字符串或其他非法条件的参数不足.  对于所有可能的格式错误的规范,查看...formatter类规范的一部分。
	 * <p>
	 * 原理：实例Formatter对象，使用format方法
	 */
	public static String format(String format, Object... args) {
		return new Formatter().format(format, args).toString();
	}

	/**
	 * 使用指定的区域设置、格式字符串和参数返回格式化的字符串。
	 */
	public static String format(Locale l, String format, Object... args) {
		return new Formatter(l).format(format, args).toString();
	}

	/**
	 * 返回Object参数的字符串表示
	 * 如果是null就返回null，否则就返回字符串表示
	 */
	public static String valueOf(Object obj) {
		return (obj == null) ? "null" : obj.toString();
	}

	/**
	 * 返回char数组参数的字符串表示。复制字符数组的内容;随后对字符数组的修改不会影响返回的字符串
	 */
	public static String valueOf(char data[]) {
		return new String(data);
	}

	/**
	 * 返回char数组参数的特定子数组的字符串表示。
	 * {@code offset}参数是子数组的第一个字符的索引。
	 * {@code count} 参数指定字符串长度。
	 * 复制字符串数组;
	 * 随后对字符数组的修改不会影响返回的字符串。
	 *
	 * @param data 字符数组。
	 * @param offset 子数组的初始偏移量。
	 * @param count 子数组的长度
	 *
	 * @return 一个字符串包含指定字数组的字符。
	 *
	 * @throws IndexOutOfBoundsException 如果offset或者count为负的，或者count大于data长度
	 */
	public static String valueOf(char data[], int offset, int count) {
		return new String(data, offset, count);
	}

	/**
	 * 等同于valueOf(char[], int, int)
	 *
	 * @param data 字符数组。
	 * @param offset 子数组的初始偏移量。
	 * @param count length of the subarray.
	 *
	 * @return 一个{@String}，它包含字符数组的指定子数组的字符。
	 *
	 * @throws IndexOutOfBoundsException 如果offset或者count为负的，或者count大于data长度
	 */
	public static String copyValueOf(char data[], int offset, int count) {
		return new String(data, offset, count);
	}

	/**
	 * 等同于valueOf(char[])
	 */
	public static String copyValueOf(char data[]) {
		return new String(data);
	}

	/**
	 * 返回boolean值代表的内容
	 */
	public static String valueOf(boolean b) {
		return b ? "true" : "false";
	}

	/**
	 * 返回char类型字段的内容
	 *
	 * @return 返回字符串长度为1
	 */
	public static String valueOf(char c) {
		char data[] = { c };
		return new String(data, true);
	}

	/**
	 * 返回int类型参数代表的内容的字符串
	 */
	public static String valueOf(int i) {
		return Integer.toString(i);
	}

	/**
	 * 返回Long型参数的字符串表示
	 * <p>
	 * Long.toString的另一种表示方式
	 *
	 * @param l 一个Long型入参
	 *
	 * @return Long型入参的String表达方式
	 *
	 * @see java.lang.Long#toString(long)
	 */
	public static String valueOf(long l) {
		return Long.toString(l);
	}

	/**
	 * 返回float参数的字符串表示
	 * <p>
	 * Float.String的另一种表达方式
	 *
	 * @param f 单精度、32位、符合IEEE 754标准的浮点数
	 *
	 * @return float型入参的字符串表达方式
	 *
	 * @see java.lang.Float#toString(float)
	 */
	public static String valueOf(float f) {
		return Float.toString(f);
	}

	/**
	 * Returns the string representation of the {@code double} argument.
	 * <p>
	 * The representation is exactly the one returned by the
	 * {@code Double.toString} method of one argument.
	 *
	 * @param d a {@code double}.
	 *
	 * @return a  string representation of the {@code double} argument.
	 *
	 * @see java.lang.Double#toString(double)
	 */
	public static String valueOf(double d) {
		return Double.toString(d);
	}

	/**
	 * Code shared by String and AbstractStringBuilder to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param source the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount count of the source string.
	 * @param target the characters being searched for.
	 * @param fromIndex the index to begin searching from.
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount, String target, int fromIndex) {
		return indexOf(source, sourceOffset, sourceCount, target.value, 0, target.value.length, fromIndex);
	}

	/**
	 * Code shared by String and StringBuffer to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param source the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount count of the source string.
	 * @param target the characters being searched for.
	 * @param targetOffset offset of the target string.
	 * @param targetCount count of the target string.
	 * @param fromIndex the index to begin searching from.
	 */
	static int indexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex) {
		if (fromIndex >= sourceCount) {
			return (targetCount == 0 ? sourceCount : -1);
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (targetCount == 0) {
			return fromIndex;
		}

		char first = target[targetOffset];
		int max = sourceOffset + (sourceCount - targetCount);

		for (int i = sourceOffset + fromIndex; i <= max; i++) {
			/* Look for first character. */
			if (source[i] != first) {
				while (++i <= max && source[i] != first) {
					;
				}
			}

            /* Found first character, now look at the rest of v2 */
			if (i <= max) {
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = targetOffset + 1; j < end && source[j] == target[k]; j++, k++) {
					;
				}

				if (j == end) {
					/* Found whole string. */
					return i - sourceOffset;
				}
			}
		}
		return -1;
	}

	/**
	 * Code shared by String and AbstractStringBuilder to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param source the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount count of the source string.
	 * @param target the characters being searched for.
	 * @param fromIndex the index to begin searching from.
	 */
	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, String target, int fromIndex) {
		return lastIndexOf(source, sourceOffset, sourceCount, target.value, 0, target.value.length, fromIndex);
	}

	/**
	 * Code shared by String and StringBuffer to do searches. The
	 * source is the character array being searched, and the target
	 * is the string being searched for.
	 *
	 * @param source the characters being searched.
	 * @param sourceOffset offset of the source string.
	 * @param sourceCount count of the source string.
	 * @param target the characters being searched for.
	 * @param targetOffset offset of the target string.
	 * @param targetCount count of the target string.
	 * @param fromIndex the index to begin searching from.
	 */
	static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount, int fromIndex) {
		/*
		 * Check arguments; return immediately where possible. For
         * consistency, don't check for null str.
         */
		int rightIndex = sourceCount - targetCount;
		if (fromIndex < 0) {
			return -1;
		}
		if (fromIndex > rightIndex) {
			fromIndex = rightIndex;
		}
		/* Empty string always matches. */
		if (targetCount == 0) {
			return fromIndex;
		}

		int strLastIndex = targetOffset + targetCount - 1;
		char strLastChar = target[strLastIndex];
		int min = sourceOffset + targetCount - 1;
		int i = min + fromIndex;

		startSearchForLastChar:
		while (true) {
			while (i >= min && source[i] != strLastChar) {
				i--;
			}
			if (i < min) {
				return -1;
			}
			int j = i - 1;
			int start = j - (targetCount - 1);
			int k = strLastIndex - 1;

			while (j > start) {
				if (source[j--] != target[k--]) {
					i--;
					continue startSearchForLastChar;
				}
			}
			return start - sourceOffset + 1;
		}
	}

	/**
	 * Returns a new String composed of copies of the
	 * {@code CharSequence elements} joined together with a copy of
	 * the specified {@code delimiter}.
	 * <p>
	 * <blockquote>For example,
	 * <pre>{@code
	 *     String message = String.join("-", "Java", "is", "cool");
	 *     // message returned is: "Java-is-cool"
	 * }</pre></blockquote>
	 *
	 * Note that if an element is null, then {@code "null"} is added.
	 *
	 * @param delimiter the delimiter that separates each element
	 * @param elements the elements to join together.
	 *
	 * @return a new {@code String} that is composed of the {@code elements}
	 * separated by the {@code delimiter}
	 *
	 * @throws NullPointerException If {@code delimiter} or {@code elements}
	 * is {@code null}
	 * @see java.util.StringJoiner
	 * @since 1.8
	 */
	public static String join(CharSequence delimiter, CharSequence... elements) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		// Number of elements not likely worth Arrays.stream overhead.
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}

	/**
	 * Returns a new {@code String} composed of copies of the
	 * {@code CharSequence elements} joined together with a copy of the
	 * specified {@code delimiter}.
	 * <p>
	 * <blockquote>For example,
	 * <pre>{@code
	 *     List<String> strings = new LinkedList<>();
	 *     strings.add("Java");strings.add("is");
	 *     strings.add("cool");
	 *     String message = String.join(" ", strings);
	 *     //message returned is: "Java is cool"
	 *
	 *     Set<String> strings = new LinkedHashSet<>();
	 *     strings.add("Java"); strings.add("is");
	 *     strings.add("very"); strings.add("cool");
	 *     String message = String.join("-", strings);
	 *     //message returned is: "Java-is-very-cool"
	 * }</pre></blockquote>
	 *
	 * Note that if an individual element is {@code null}, then {@code "null"} is added.
	 *
	 * @param delimiter a sequence of characters that is used to separate each
	 * of the {@code elements} in the resulting {@code String}
	 * @param elements an {@code Iterable} that will have its {@code elements}
	 * joined together.
	 *
	 * @return a new {@code String} that is composed from the {@code elements}
	 * argument
	 *
	 * @throws NullPointerException If {@code delimiter} or {@code elements}
	 * is {@code null}
	 * @see #join(CharSequence, CharSequence...)
	 * @see java.util.StringJoiner
	 * @since 1.8
	 */
	public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
		Objects.requireNonNull(delimiter);
		Objects.requireNonNull(elements);
		StringJoiner joiner = new StringJoiner(delimiter);
		for (CharSequence cs : elements) {
			joiner.add(cs);
		}
		return joiner.toString();
	}

	/**
	 * Returns a formatted string using the specified format string and
	 * arguments.
	 * <p>
	 * <p> The locale always used is the one returned by {@link
	 * java.util.Locale#getDefault() Locale.getDefault()}.
	 *
	 * @param format A <a href="../util/Formatter.html#syntax">format string</a>
	 * @param args Arguments referenced by the format specifiers in the format
	 * string.  If there are more arguments than format specifiers, the
	 * extra arguments are ignored.  The number of arguments is
	 * variable and may be zero.  The maximum number of arguments is
	 * limited by the maximum dimension of a Java array as defined by
	 * <cite>The Java&trade; Virtual Machine Specification</cite>.
	 * The behaviour on a
	 * {@code null} argument depends on the <a
	 * href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @return A formatted string
	 *
	 * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
	 * specifier that is incompatible with the given arguments,
	 * insufficient arguments given the format string, or other
	 * illegal conditions.  For specification of all possible
	 * formatting errors, see the <a
	 * href="../util/Formatter.html#detail">Details</a> section of the
	 * formatter class specification.
	 * @see java.util.Formatter
	 * @since 1.5
	 */
	public static String format(String format, Object... args) {
		return new Formatter().format(format, args).toString();
	}

	/**
	 * Returns a formatted string using the specified locale, format string,
	 * and arguments.
	 *
	 * @param l The {@linkplain java.util.Locale locale} to apply during
	 * formatting.  If {@code l} is {@code null} then no localization
	 * is applied.
	 * @param format A <a href="../util/Formatter.html#syntax">format string</a>
	 * @param args Arguments referenced by the format specifiers in the format
	 * string.  If there are more arguments than format specifiers, the
	 * extra arguments are ignored.  The number of arguments is
	 * variable and may be zero.  The maximum number of arguments is
	 * limited by the maximum dimension of a Java array as defined by
	 * <cite>The Java&trade; Virtual Machine Specification</cite>.
	 * The behaviour on a
	 * {@code null} argument depends on the
	 * <a href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @return A formatted string
	 *
	 * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
	 * specifier that is incompatible with the given arguments,
	 * insufficient arguments given the format string, or other
	 * illegal conditions.  For specification of all possible
	 * formatting errors, see the <a
	 * href="../util/Formatter.html#detail">Details</a> section of the
	 * formatter class specification
	 * @see java.util.Formatter
	 * @since 1.5
	 */
	public static String format(Locale l, String format, Object... args) {
		return new Formatter(l).format(format, args).toString();
	}

	/**
	 * Returns the string representation of the {@code Object} argument.
	 *
	 * @param obj an {@code Object}.
	 *
	 * @return if the argument is {@code null}, then a string equal to
	 * {@code "null"}; otherwise, the value of
	 * {@code obj.toString()} is returned.
	 *
	 * @see java.lang.Object#toString()
	 */
	public static String valueOf(Object obj) {
		return (obj == null) ? "null" : obj.toString();
	}

	/**
	 * Returns the string representation of the {@code char} array
	 * argument. The contents of the character array are copied; subsequent
	 * modification of the character array does not affect the returned
	 * string.
	 *
	 * @param data the character array.
	 *
	 * @return a {@code String} that contains the characters of the
	 * character array.
	 */
	public static String valueOf(char data[]) {
		return new String(data);
	}

	/**
	 * Returns the string representation of a specific subarray of the
	 * {@code char} array argument.
	 * <p>
	 * The {@code offset} argument is the index of the first
	 * character of the subarray. The {@code count} argument
	 * specifies the length of the subarray. The contents of the subarray
	 * are copied; subsequent modification of the character array does not
	 * affect the returned string.
	 *
	 * @param data the character array.
	 * @param offset initial offset of the subarray.
	 * @param count length of the subarray.
	 *
	 * @return a {@code String} that contains the characters of the
	 * specified subarray of the character array.
	 *
	 * @throws IndexOutOfBoundsException if {@code offset} is
	 * negative, or {@code count} is negative, or
	 * {@code offset+count} is larger than
	 * {@code data.length}.
	 */
	public static String valueOf(char data[], int offset, int count) {
		return new String(data, offset, count);
	}

	/**
	 * Equivalent to {@link #valueOf(char[], int, int)}.
	 *
	 * @param data the character array.
	 * @param offset initial offset of the subarray.
	 * @param count length of the subarray.
	 *
	 * @return a {@code String} that contains the characters of the
	 * specified subarray of the character array.
	 *
	 * @throws IndexOutOfBoundsException if {@code offset} is
	 * negative, or {@code count} is negative, or
	 * {@code offset+count} is larger than
	 * {@code data.length}.
	 */
	public static String copyValueOf(char data[], int offset, int count) {
		return new String(data, offset, count);
	}

	/**
	 * Equivalent to {@link #valueOf(char[])}.
	 *
	 * @param data the character array.
	 *
	 * @return a {@code String} that contains the characters of the
	 * character array.
	 */
	public static String copyValueOf(char data[]) {
		return new String(data);
	}

	/**
	 * Returns the string representation of the {@code boolean} argument.
	 *
	 * @param b a {@code boolean}.
	 *
	 * @return if the argument is {@code true}, a string equal to
	 * {@code "true"} is returned; otherwise, a string equal to
	 * {@code "false"} is returned.
	 */
	public static String valueOf(boolean b) {
		return b ? "true" : "false";
	}

	/**
	 * Returns the string representation of the {@code char}
	 * argument.
	 *
	 * @param c a {@code char}.
	 *
	 * @return a string of length {@code 1} containing
	 * as its single character the argument {@code c}.
	 */
	public static String valueOf(char c) {
		char data[] = { c };
		return new String(data, true);
	}

	/**
	 * Returns the string representation of the {@code int} argument.
	 * <p>
	 * The representation is exactly the one returned by the
	 * {@code Integer.toString} method of one argument.
	 *
	 * @param i an {@code int}.
	 *
	 * @return a string representation of the {@code int} argument.
	 *
	 * @see java.lang.Integer#toString(int, int)
	 */
	public static String valueOf(int i) {
		return Integer.toString(i);
	}

	/**
	 * Returns the string representation of the {@code long} argument.
	 * <p>
	 * The representation is exactly the one returned by the
	 * {@code Long.toString} method of one argument.
	 *
	 * @param l a {@code long}.
	 *
	 * @return a string representation of the {@code long} argument.
	 *
	 * @see java.lang.Long#toString(long)
	 */
	public static String valueOf(long l) {
		return Long.toString(l);
	}

	/**
	 * Returns the string representation of the {@code float} argument.
	 * <p>
	 * The representation is exactly the one returned by the
	 * {@code Float.toString} method of one argument.
	 *
	 * @param f a {@code float}.
	 *
	 * @return a string representation of the {@code float} argument.
	 *
	 * @see java.lang.Float#toString(float)
	 */
	public static String valueOf(float f) {
		return Float.toString(f);
	}

	/**
	 * Returns the string representation of the {@code double} argument.
	 * <p>
	 * The representation is exactly the one returned by the
	 * {@code Double.toString} method of one argument.
	 *
	 * @param d a {@code double}.
	 *
	 * @return a  string representation of the {@code double} argument.
	 *
	 * @see java.lang.Double#toString(double)
	 */
	public static String valueOf(double d) {
		return Double.toString(d);
	}

	/**
	 * 返回字符串的长度
	 * The length is equal to the number of <a href="Character.html#unicode">Unicode
	 * code units</a> in the string.
	 *
	 * @return the length of the sequence of characters represented by this
	 * object.
	 */
	public int length() {
		return value.length;
	}

	/**
	 * 在字符串长度为0的时候返回true
	 */
	public boolean isEmpty() {
		return value.length == 0;
	}

	/**
	 * 返回索引处的char值
	 * 索引范围是0到长度-1
	 * 第一个char的索引为0, 下一个以此类推, 对于数组的索引。
	 * 如果索引是代理，代理值就被返回
	 *
	 * @param index the index of the {@code char} value.
	 *
	 * @return the {@code char} value at the specified index of this string.
	 * The first {@code char} value is at index {@code 0}.
	 *
	 * @throws IndexOutOfBoundsException if the {@code index}
	 * argument is negative or not less than the length of this
	 * string.
	 */
	public char charAt(int index) {
		if ((index < 0) || (index >= value.length)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return value[index];
	}

	/**
	 * 返回指定索引处的字符（Unicode代码点）.
	 * char的index索引范围是0到字符串长度-1
	 * <p> If the {@code char} value specified at the given index
	 * is in the high-surrogate range, the following index is less
	 * than the length of this {@code String}, and the
	 * {@code char} value at the following index is in the
	 * low-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise,
	 * the {@code char} value at the given index is returned.
	 *
	 * @param index 索引到char字符的值
	 *
	 * @return 在index中字符的代码点
	 *
	 * @throws IndexOutOfBoundsException 如果index参数为负，或者不小于该字符串的长度
	 * @since 1.5
	 */
	public int codePointAt(int index) {
		if ((index < 0) || (index >= value.length)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return Character.codePointAtImpl(value, index, value.length);
	}

	/**
	 * Returns the character (Unicode code point) before the specified
	 * index. The index refers to {@code char} values
	 * (Unicode code units) and ranges from {@code 1} to {@link
	 * CharSequence#length() length}.
	 * <p>
	 * <p> If the {@code char} value at {@code (index - 1)}
	 * is in the low-surrogate range, {@code (index - 2)} is not
	 * negative, and the {@code char} value at {@code (index -
	 * 2)} is in the high-surrogate range, then the
	 * supplementary code point value of the surrogate pair is
	 * returned. If the {@code char} value at {@code index -
	 * 1} is an unpaired low-surrogate or a high-surrogate, the
	 * surrogate value is returned.
	 *
	 * @param index the index following the code point that should be returned
	 *
	 * @return the Unicode code point value before the given index.
	 *
	 * @throws IndexOutOfBoundsException if the {@code index}
	 * argument is less than 1 or greater than the length
	 * of this string.
	 * @since 1.5
	 */
	public int codePointBefore(int index) {
		int i = index - 1;
		if ((i < 0) || (i >= value.length)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return Character.codePointBeforeImpl(value, index, 0);
	}

	/**
	 * Returns the number of Unicode code points in the specified text
	 * range of this {@code String}. The text range begins at the
	 * specified {@code beginIndex} and extends to the
	 * {@code char} at index {@code endIndex - 1}. Thus the
	 * length (in {@code char}s) of the text range is
	 * {@code endIndex-beginIndex}. Unpaired surrogates within
	 * the text range count as one code point each.
	 *
	 * @param beginIndex the index to the first {@code char} of
	 * the text range.
	 * @param endIndex the index after the last {@code char} of
	 * the text range.
	 *
	 * @return the number of Unicode code points in the specified text
	 * range
	 *
	 * @throws IndexOutOfBoundsException if the
	 * {@code beginIndex} is negative, or {@code endIndex}
	 * is larger than the length of this {@code String}, or
	 * {@code beginIndex} is larger than {@code endIndex}.
	 * @since 1.5
	 */
	public int codePointCount(int beginIndex, int endIndex) {
		if (beginIndex < 0 || endIndex > value.length || beginIndex > endIndex) {
			throw new IndexOutOfBoundsException();
		}
		return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
	}

	/**
	 * 返回索引，被偏移量偏移
	 * Returns the index within this {@code String} that is
	 * offset from the given {@code index} by
	 * {@code codePointOffset} code points. Unpaired surrogates
	 * within the text range given by {@code index} and
	 * {@code codePointOffset} count as one code point each.
	 *
	 * @param index the index to be offset
	 * @param codePointOffset the offset in code points
	 *
	 * @return the index within this {@code String}
	 *
	 * @throws IndexOutOfBoundsException if {@code index}
	 * is negative or larger then the length of this
	 * {@code String}, or if {@code codePointOffset} is positive
	 * and the substring starting with {@code index} has fewer
	 * than {@code codePointOffset} code points,
	 * or if {@code codePointOffset} is negative and the substring
	 * before {@code index} has fewer than the absolute value
	 * of {@code codePointOffset} code points.
	 * @since 1.5
	 */
	public int offsetByCodePoints(int index, int codePointOffset) {
		if (index < 0 || index > value.length) {
			throw new IndexOutOfBoundsException();
		}
		return Character.offsetByCodePointsImpl(value, 0, value.length, index, codePointOffset);
	}

	/**
	 * 复制char字符，从dstBegin开始复制
	 * 这个方法不执行人任何范围检查
	 */
	void getChars(char dst[], int dstBegin) {
		System.arraycopy(value, 0, dst, dstBegin, value.length);
	}

	/**
	 * 将此字符串中的字符复制到目标字符数组中
	 * <p>
	 * 复制的第一个字符的索引srtBegin
	 * 最后一个复制的字符的所以srcEnd-1
	 * 因此，要复制的字符总数是end-begin
	 * The characters are copied into the
	 * subarray of {@code dst} starting at index {@code dstBegin}
	 * and ending at index:  dstBegin + (srcEnd-srcBegin) - 1
	 *
	 * @param srcBegin 索引字符串中的第一个字符去拷贝
	 * @param srcEnd 索引后面的字符串中的最后一个字符去拷贝
	 * @param dst 目标数组
	 * @param dstBegin 在目标数组中开始偏移。
	 *
	 * @throws IndexOutOfBoundsException 如果下面情况为真的报异常：
	 * begin小于0
	 * end比字符传总数大
	 * begin大于end
	 */
	public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
		if (srcBegin < 0) {
			throw new StringIndexOutOfBoundsException(srcBegin);
		}
		if (srcEnd > value.length) {
			throw new StringIndexOutOfBoundsException(srcEnd);
		}
		if (srcBegin > srcEnd) {
			throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
		}
		System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}

	/**
	 * 将此字符串中的字符复制到目标字节数组中。
	 * 每个字节接收对应字符的8个低阶位。
	 * 每一个字符的8个高阶位都没有复制，也不以任何方式参与到传输中。
	 * <p>
	 * 复制的第一个字符的索引srtBegin
	 * 最后一个复制的字符的所以srcEnd-1
	 * 因此，要复制的字符总数是end-begin
	 * <p>
	 * 转换成字节的字符被复制到dst的子数组中，从索引dstBegin和结束于索引:dstBegin + (srcEnd-srcBegin) - 1
	 *
	 * @deprecated 此方法不能正确地将字符转换成字节。 As of JDK&nbsp;1.1, the preferred way to do this is via the
	 * {@link #getBytes()} method, which uses the platform's default charset.
	 */
	@Deprecated
	public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
		if (srcBegin < 0) {
			throw new StringIndexOutOfBoundsException(srcBegin);
		}
		if (srcEnd > value.length) {
			throw new StringIndexOutOfBoundsException(srcEnd);
		}
		if (srcBegin > srcEnd) {
			throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
		}
		Objects.requireNonNull(dst);

		int j = dstBegin;
		int n = srcEnd;
		int i = srcBegin;
		char[] val = value;   /* avoid getfield opcode */

		while (i < n) {
			dst[j++] = (byte) val[i++];
		}
	}

	/**
	 * String命名charset的字节序列，将结果存储到一个新的字节数组中。
	 * <p>
	 * 当这个字符串不能被编码到给定的字符集时，该方法的行为是未指定的。
	 * <p>
	 * 当需要对编码过程进行更多的控制时，应该使用Charset Encoder类。
	 *
	 * @throws UnsupportedEncodingException 如果字符集是不支持的就报异常
	 * @since JDK1.1
	 */
	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
		if (charsetName == null) {
			throw new NullPointerException();
		}
		return StringCoding.encode(charsetName, value, 0, value.length);
	}

	/**
	 * 将String编码放入Byte字符集，使用给定的Charset，将结果储存为新的字节数组
	 * <p>
	 * 这个方法总是替换畸形输入或无法具象化字符的序列的，使用Charset的默认替换数组。
	 * 这个CharsetEncoder类应该在 需要对编码过程进行更多控制时候使用。
	 */
	public byte[] getBytes(Charset charset) {
		if (charset == null) {
			throw new NullPointerException();
		}
		return StringCoding.encode(charset, value, 0, value.length);
	}

	/**
	 * 将String编码放入Byte字符集，使用给定的Charset，将结果储存为新的字节数组
	 * <p>
	 * 在默认字符集中无法对该字符串进行编码时，该方法的行为是未指定的。
	 * 这个CharsetEncoder类应该在 需要对编码过程进行更多控制时候使用。
	 */
	public byte[] getBytes() {
		return StringCoding.encode(value, 0, value.length);
	}

	/**
	 * 将对象和指定对象进行比较。
	 * 返回是true，只有在这个论点不是空，并且这个String对象表现的字符序列和anObject表示的相同。
	 */
	public boolean equals(Object anObject) {
		//		比较对象和指定对象是否值相等，如果相等输出true
		if (this == anObject) {
			return true;
		}
		//		判断指定对象是否为String类或String类的父类
		if (anObject instanceof String) {
			//			强转成Strign
			String anotherString = (String) anObject;
			int n = value.length;
			if (n == anotherString.value.length) {
				//				对象和指定对象都转成char数组
				char v1[] = value;
				char v2[] = anotherString.value;
				int i = 0;
				//				从未到头遍历，减省时间，针对出现数组长度不一致或者开头部分相同几率大的情况
				while (n-- != 0) {
					//					只要值不想等就返回false
					if (v1[i] != v2[i]) {
						return false;
					}
					i++;
				}
				//				对象char数组和指定对象的char数组全部相等
				return true;
			}
		}
		return false;
	}

	/**
	 * 将此字符串与指定的Stringbuffer进行比较。
	 * 这个返回是true，只有在这个字符串表示的序列相同于指定StringBuffer。
	 * 这个方法只在StringBuffer上同步。
	 */
	public boolean contentEquals(StringBuffer sb) {
		//		强转CharSequence，就是字符序列
		return contentEquals((CharSequence) sb);
	}

	private boolean nonSyncContentEquals(AbstractStringBuilder sb) {
		char v1[] = value;
		char v2[] = sb.getValue();
		int n = v1.length;
		if (n != sb.length()) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (v1[i] != v2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * String和指定的CharSequence字符序列进行比较。
	 * 只有在字符序列的的char值序列和指定字符序列相同时返回true。
	 * 注意，如果CharSequence是一个StringBuffer那么方法会同步它。
	 */
	public boolean contentEquals(CharSequence cs) {
		// 		参数是一个字符串缓冲区StringBuffer，字符串构建器StringBuilder

		if (cs instanceof AbstractStringBuilder) {
			if (cs instanceof StringBuffer) {
				synchronized (cs) {
					return nonSyncContentEquals((AbstractStringBuilder) cs);
				}
			} else {
				return nonSyncContentEquals((AbstractStringBuilder) cs);
			}
		}
		// Argument is a String
		if (cs instanceof String) {
			return equals(cs);
		}
		// Argument is a generic CharSequence
		char v1[] = value;
		int n = v1.length;
		if (n != cs.length()) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (v1[i] != cs.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares this {@code String} to another {@code String}, ignoring case
	 * considerations.  Two strings are considered equal ignoring case if they
	 * are of the same length and corresponding characters in the two strings
	 * are equal ignoring case.
	 * <p>
	 * <p> Two characters {@code c1} and {@code c2} are considered the same
	 * ignoring case if at least one of the following is true:
	 * <ul>
	 * <li> The two characters are the same (as compared by the
	 * {@code ==} operator)
	 * <li> Applying the method {@link
	 * java.lang.Character#toUpperCase(char)} to each character
	 * produces the same result
	 * <li> Applying the method {@link
	 * java.lang.Character#toLowerCase(char)} to each character
	 * produces the same result
	 * </ul>
	 *
	 * @param anotherString The {@code String} to compare this {@code String} against
	 *
	 * @return {@code true} if the argument is not {@code null} and it
	 * represents an equivalent {@code String} ignoring case; {@code
	 * false} otherwise
	 *
	 * @see #equals(Object)
	 */
	public boolean equalsIgnoreCase(String anotherString) {
		return (this == anotherString) ?
				true :
				(anotherString != null) && (anotherString.value.length == value.length) && regionMatches(true, 0, anotherString, 0, value.length);
	}

	/**
	 * Compares two strings lexicographically.
	 * The comparison is based on the Unicode value of each character in
	 * the strings. The character sequence represented by this
	 * {@code String} object is compared lexicographically to the
	 * character sequence represented by the argument string. The result is
	 * a negative integer if this {@code String} object
	 * lexicographically precedes the argument string. The result is a
	 * positive integer if this {@code String} object lexicographically
	 * follows the argument string. The result is zero if the strings
	 * are equal; {@code compareTo} returns {@code 0} exactly when
	 * the {@link #equals(Object)} method would return {@code true}.
	 * <p>
	 * This is the definition of lexicographic ordering. If two strings are
	 * different, then either they have different characters at some index
	 * that is a valid index for both strings, or their lengths are different,
	 * or both. If they have different characters at one or more index
	 * positions, let <i>k</i> be the smallest such index; then the string
	 * whose character at position <i>k</i> has the smaller value, as
	 * determined by using the &lt; operator, lexicographically precedes the
	 * other string. In this case, {@code compareTo} returns the
	 * difference of the two character values at position {@code k} in
	 * the two string -- that is, the value:
	 * <blockquote><pre>
	 * this.charAt(k)-anotherString.charAt(k)
	 * </pre></blockquote>
	 * If there is no index position at which they differ, then the shorter
	 * string lexicographically precedes the longer string. In this case,
	 * {@code compareTo} returns the difference of the lengths of the
	 * strings -- that is, the value:
	 * <blockquote><pre>
	 * this.length()-anotherString.length()
	 * </pre></blockquote>
	 *
	 * @param anotherString the {@code String} to be compared.
	 *
	 * @return the value {@code 0} if the argument string is equal to
	 * this string; a value less than {@code 0} if this string
	 * is lexicographically less than the string argument; and a
	 * value greater than {@code 0} if this string is
	 * lexicographically greater than the string argument.
	 */
	public int compareTo(String anotherString) {
		int len1 = value.length;
		int len2 = anotherString.value.length;
		int lim = Math.min(len1, len2);
		char v1[] = value;
		char v2[] = anotherString.value;

		int k = 0;
		while (k < lim) {
			char c1 = v1[k];
			char c2 = v2[k];
			if (c1 != c2) {
				return c1 - c2;
			}
			k++;
		}
		return len1 - len2;
	}

	/**
	 * Compares two strings lexicographically, ignoring case
	 * differences. This method returns an integer whose sign is that of
	 * calling {@code compareTo} with normalized versions of the strings
	 * where case differences have been eliminated by calling
	 * {@code Character.toLowerCase(Character.toUpperCase(character))} on
	 * each character.
	 * <p>
	 * Note that this method does <em>not</em> take locale into account,
	 * and will result in an unsatisfactory ordering for certain locales.
	 * The java.text package provides <em>collators</em> to allow
	 * locale-sensitive ordering.
	 *
	 * @param str the {@code String} to be compared.
	 *
	 * @return a negative integer, zero, or a positive integer as the
	 * specified String is greater than, equal to, or less
	 * than this String, ignoring case considerations.
	 *
	 * @see java.text.Collator#compare(String, String)
	 * @since 1.2
	 */
	public int compareToIgnoreCase(String str) {
		return CASE_INSENSITIVE_ORDER.compare(this, str);
	}

	/**
	 * Tests if two string regions are equal.
	 * <p>
	 * A substring of this {@code String} object is compared to a substring
	 * of the argument other. The result is true if these substrings
	 * represent identical character sequences. The substring of this
	 * {@code String} object to be compared begins at index {@code toffset}
	 * and has length {@code len}. The substring of other to be compared
	 * begins at index {@code ooffset} and has length {@code len}. The
	 * result is {@code false} if and only if at least one of the following
	 * is true:
	 * <ul><li>{@code toffset} is negative.
	 * <li>{@code ooffset} is negative.
	 * <li>{@code toffset+len} is greater than the length of this
	 * {@code String} object.
	 * <li>{@code ooffset+len} is greater than the length of the other
	 * argument.
	 * <li>There is some nonnegative integer <i>k</i> less than {@code len}
	 * such that:
	 * {@code this.charAt(toffset + }<i>k</i>{@code ) != other.charAt(ooffset + }
	 * <i>k</i>{@code )}
	 * </ul>
	 *
	 * @param toffset the starting offset of the subregion in this string.
	 * @param other the string argument.
	 * @param ooffset the starting offset of the subregion in the string
	 * argument.
	 * @param len the number of characters to compare.
	 *
	 * @return {@code true} if the specified subregion of this string
	 * exactly matches the specified subregion of the string argument;
	 * {@code false} otherwise.
	 */
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		char ta[] = value;
		int to = toffset;
		char pa[] = other.value;
		int po = ooffset;
		// Note: toffset, ooffset, or len might be near -1>>>1.
		if ((ooffset < 0) || (toffset < 0) || (toffset > (long) value.length - len) || (ooffset > (long) other.value.length - len)) {
			return false;
		}
		while (len-- > 0) {
			if (ta[to++] != pa[po++]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if two string regions are equal.
	 * <p>
	 * A substring of this {@code String} object is compared to a substring
	 * of the argument {@code other}. The result is {@code true} if these
	 * substrings represent character sequences that are the same, ignoring
	 * case if and only if {@code ignoreCase} is true. The substring of
	 * this {@code String} object to be compared begins at index
	 * {@code toffset} and has length {@code len}. The substring of
	 * {@code other} to be compared begins at index {@code ooffset} and
	 * has length {@code len}. The result is {@code false} if and only if
	 * at least one of the following is true:
	 * <ul><li>{@code toffset} is negative.
	 * <li>{@code ooffset} is negative.
	 * <li>{@code toffset+len} is greater than the length of this
	 * {@code String} object.
	 * <li>{@code ooffset+len} is greater than the length of the other
	 * argument.
	 * <li>{@code ignoreCase} is {@code false} and there is some nonnegative
	 * integer <i>k</i> less than {@code len} such that:
	 * <blockquote><pre>
	 * this.charAt(toffset+k) != other.charAt(ooffset+k)
	 * </pre></blockquote>
	 * <li>{@code ignoreCase} is {@code true} and there is some nonnegative
	 * integer <i>k</i> less than {@code len} such that:
	 * <blockquote><pre>
	 * Character.toLowerCase(this.charAt(toffset+k)) !=
	 * Character.toLowerCase(other.charAt(ooffset+k))
	 * </pre></blockquote>
	 * and:
	 * <blockquote><pre>
	 * Character.toUpperCase(this.charAt(toffset+k)) !=
	 *         Character.toUpperCase(other.charAt(ooffset+k))
	 * </pre></blockquote>
	 * </ul>
	 *
	 * @param ignoreCase if {@code true}, ignore case when comparing
	 * characters.
	 * @param toffset the starting offset of the subregion in this
	 * string.
	 * @param other the string argument.
	 * @param ooffset the starting offset of the subregion in the string
	 * argument.
	 * @param len the number of characters to compare.
	 *
	 * @return {@code true} if the specified subregion of this string
	 * matches the specified subregion of the string argument;
	 * {@code false} otherwise. Whether the matching is exact
	 * or case insensitive depends on the {@code ignoreCase}
	 * argument.
	 */
	public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
		char ta[] = value;
		int to = toffset;
		char pa[] = other.value;
		int po = ooffset;
		// Note: toffset, ooffset, or len might be near -1>>>1.
		if ((ooffset < 0) || (toffset < 0) || (toffset > (long) value.length - len) || (ooffset > (long) other.value.length - len)) {
			return false;
		}
		while (len-- > 0) {
			char c1 = ta[to++];
			char c2 = pa[po++];
			if (c1 == c2) {
				continue;
			}
			if (ignoreCase) {
				// If characters don't match but case may be ignored,
				// try converting both characters to uppercase.
				// If the results match, then the comparison scan should
				// continue.
				char u1 = Character.toUpperCase(c1);
				char u2 = Character.toUpperCase(c2);
				if (u1 == u2) {
					continue;
				}
				// Unfortunately, conversion to uppercase does not work properly
				// for the Georgian alphabet, which has strange rules about case
				// conversion.  So we need to make one last check before
				// exiting.
				if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
					continue;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Tests if the substring of this string beginning at the
	 * specified index starts with the specified prefix.
	 *
	 * @param prefix the prefix.
	 * @param toffset where to begin looking in this string.
	 *
	 * @return {@code true} if the character sequence represented by the
	 * argument is a prefix of the substring of this object starting
	 * at index {@code toffset}; {@code false} otherwise.
	 * The result is {@code false} if {@code toffset} is
	 * negative or greater than the length of this
	 * {@code String} object; otherwise the result is the same
	 * as the result of the expression
	 * <pre>
	 *          this.substring(toffset).startsWith(prefix)
	 *          </pre>
	 */
	public boolean startsWith(String prefix, int toffset) {
		char ta[] = value;
		int to = toffset;
		char pa[] = prefix.value;
		int po = 0;
		int pc = prefix.value.length;
		// Note: toffset might be near -1>>>1.
		if ((toffset < 0) || (toffset > value.length - pc)) {
			return false;
		}
		while (--pc >= 0) {
			if (ta[to++] != pa[po++]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if this string starts with the specified prefix.
	 *
	 * @param prefix the prefix.
	 *
	 * @return {@code true} if the character sequence represented by the
	 * argument is a prefix of the character sequence represented by
	 * this string; {@code false} otherwise.
	 * Note also that {@code true} will be returned if the
	 * argument is an empty string or is equal to this
	 * {@code String} object as determined by the
	 * {@link #equals(Object)} method.
	 *
	 * @since 1. 0
	 */
	public boolean startsWith(String prefix) {
		return startsWith(prefix, 0);
	}

	/**
	 * Tests if this string ends with the specified suffix.
	 *
	 * @param suffix the suffix.
	 *
	 * @return {@code true} if the character sequence represented by the
	 * argument is a suffix of the character sequence represented by
	 * this object; {@code false} otherwise. Note that the
	 * result will be {@code true} if the argument is the
	 * empty string or is equal to this {@code String} object
	 * as determined by the {@link #equals(Object)} method.
	 */
	public boolean endsWith(String suffix) {
		return startsWith(suffix, value.length - suffix.value.length);
	}

	/**
	 * Returns a hash code for this string. The hash code for a
	 * {@code String} object is computed as
	 * <blockquote><pre>
	 * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
	 * </pre></blockquote>
	 * using {@code int} arithmetic, where {@code s[i]} is the
	 * <i>i</i>th character of the string, {@code n} is the length of
	 * the string, and {@code ^} indicates exponentiation.
	 * (The hash value of the empty string is zero.)
	 *
	 * @return a hash code value for this object.
	 */
	public int hashCode() {
		int h = hash;
		if (h == 0 && value.length > 0) {
			char val[] = value;

			for (int i = 0; i < value.length; i++) {
				h = 31 * h + val[i];
			}
			hash = h;
		}
		return h;
	}

	/**
	 * Returns the index within this string of the first occurrence of
	 * the specified character. If a character with value
	 * {@code ch} occurs in the character sequence represented by
	 * this {@code String} object, then the index (in Unicode
	 * code units) of the first such occurrence is returned. For
	 * values of {@code ch} in the range from 0 to 0xFFFF
	 * (inclusive), this is the smallest value <i>k</i> such that:
	 * <blockquote><pre>
	 * this.charAt(<i>k</i>) == ch
	 * </pre></blockquote>
	 * is true. For other values of {@code ch}, it is the
	 * smallest value <i>k</i> such that:
	 * <blockquote><pre>
	 * this.codePointAt(<i>k</i>) == ch
	 * </pre></blockquote>
	 * is true. In either case, if no such character occurs in this
	 * string, then {@code -1} is returned.
	 *
	 * @param ch a character (Unicode code point).
	 *
	 * @return the index of the first occurrence of the character in the
	 * character sequence represented by this object, or
	 * {@code -1} if the character does not occur.
	 */
	public int indexOf(int ch) {
		return indexOf(ch, 0);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified character, starting the search at the specified index.
	 * <p>
	 * If a character with value {@code ch} occurs in the
	 * character sequence represented by this {@code String}
	 * object at an index no smaller than {@code fromIndex}, then
	 * the index of the first such occurrence is returned. For values
	 * of {@code ch} in the range from 0 to 0xFFFF (inclusive),
	 * this is the smallest value <i>k</i> such that:
	 * <blockquote><pre>
	 * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
	 * </pre></blockquote>
	 * is true. For other values of {@code ch}, it is the
	 * smallest value <i>k</i> such that:
	 * <blockquote><pre>
	 * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &gt;= fromIndex)
	 * </pre></blockquote>
	 * is true. In either case, if no such character occurs in this
	 * string at or after position {@code fromIndex}, then
	 * {@code -1} is returned.
	 * <p>
	 * <p>
	 * There is no restriction on the value of {@code fromIndex}. If it
	 * is negative, it has the same effect as if it were zero: this entire
	 * string may be searched. If it is greater than the length of this
	 * string, it has the same effect as if it were equal to the length of
	 * this string: {@code -1} is returned.
	 * <p>
	 * <p>All indices are specified in {@code char} values
	 * (Unicode code units).
	 *
	 * @param ch a character (Unicode code point).
	 * @param fromIndex the index to start the search from.
	 *
	 * @return the index of the first occurrence of the character in the
	 * character sequence represented by this object that is greater
	 * than or equal to {@code fromIndex}, or {@code -1}
	 * if the character does not occur.
	 */
	public int indexOf(int ch, int fromIndex) {
		final int max = value.length;
		if (fromIndex < 0) {
			fromIndex = 0;
		} else if (fromIndex >= max) {
			// Note: fromIndex might be near -1>>>1.
			return -1;
		}

		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			// handle most cases here (ch is a BMP code point or a
			// negative value (invalid code point))
			final char[] value = this.value;
			for (int i = fromIndex; i < max; i++) {
				if (value[i] == ch) {
					return i;
				}
			}
			return -1;
		} else {
			return indexOfSupplementary(ch, fromIndex);
		}
	}

	/**
	 * Handles (rare) calls of indexOf with a supplementary character.
	 */
	private int indexOfSupplementary(int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
			final char[] value = this.value;
			final char hi = Character.highSurrogate(ch);
			final char lo = Character.lowSurrogate(ch);
			final int max = value.length - 1;
			for (int i = fromIndex; i < max; i++) {
				if (value[i] == hi && value[i + 1] == lo) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index within this string of the last occurrence of
	 * the specified character. For values of {@code ch} in the
	 * range from 0 to 0xFFFF (inclusive), the index (in Unicode code
	 * units) returned is the largest value <i>k</i> such that:
	 * <blockquote><pre>
	 * this.charAt(<i>k</i>) == ch
	 * </pre></blockquote>
	 * is true. For other values of {@code ch}, it is the
	 * largest value <i>k</i> such that:
	 * <blockquote><pre>
	 * this.codePointAt(<i>k</i>) == ch
	 * </pre></blockquote>
	 * is true.  In either case, if no such character occurs in this
	 * string, then {@code -1} is returned.  The
	 * {@code String} is searched backwards starting at the last
	 * character.
	 *
	 * @param ch a character (Unicode code point).
	 *
	 * @return the index of the last occurrence of the character in the
	 * character sequence represented by this object, or
	 * {@code -1} if the character does not occur.
	 */
	public int lastIndexOf(int ch) {
		return lastIndexOf(ch, value.length - 1);
	}

	/**
	 * Returns the index within this string of the last occurrence of
	 * the specified character, searching backward starting at the
	 * specified index. For values of {@code ch} in the range
	 * from 0 to 0xFFFF (inclusive), the index returned is the largest
	 * value <i>k</i> such that:
	 * <blockquote><pre>
	 * (this.charAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
	 * </pre></blockquote>
	 * is true. For other values of {@code ch}, it is the
	 * largest value <i>k</i> such that:
	 * <blockquote><pre>
	 * (this.codePointAt(<i>k</i>) == ch) {@code &&} (<i>k</i> &lt;= fromIndex)
	 * </pre></blockquote>
	 * is true. In either case, if no such character occurs in this
	 * string at or before position {@code fromIndex}, then
	 * {@code -1} is returned.
	 * <p>
	 * <p>All indices are specified in {@code char} values
	 * (Unicode code units).
	 *
	 * @param ch a character (Unicode code point).
	 * @param fromIndex the index to start the search from. There is no
	 * restriction on the value of {@code fromIndex}. If it is
	 * greater than or equal to the length of this string, it has
	 * the same effect as if it were equal to one less than the
	 * length of this string: this entire string may be searched.
	 * If it is negative, it has the same effect as if it were -1:
	 * -1 is returned.
	 *
	 * @return the index of the last occurrence of the character in the
	 * character sequence represented by this object that is less
	 * than or equal to {@code fromIndex}, or {@code -1}
	 * if the character does not occur before that point.
	 */
	public int lastIndexOf(int ch, int fromIndex) {
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			// handle most cases here (ch is a BMP code point or a
			// negative value (invalid code point))
			final char[] value = this.value;
			int i = Math.min(fromIndex, value.length - 1);
			for (; i >= 0; i--) {
				if (value[i] == ch) {
					return i;
				}
			}
			return -1;
		} else {
			return lastIndexOfSupplementary(ch, fromIndex);
		}
	}

	/**
	 * Handles (rare) calls of lastIndexOf with a supplementary character.
	 */
	private int lastIndexOfSupplementary(int ch, int fromIndex) {
		if (Character.isValidCodePoint(ch)) {
			final char[] value = this.value;
			char hi = Character.highSurrogate(ch);
			char lo = Character.lowSurrogate(ch);
			int i = Math.min(fromIndex, value.length - 2);
			for (; i >= 0; i--) {
				if (value[i] == hi && value[i + 1] == lo) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring.
	 * <p>
	 * <p>The returned index is the smallest value <i>k</i> for which:
	 * <blockquote><pre>
	 * this.startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then {@code -1} is returned.
	 *
	 * @param str the substring to search for.
	 *
	 * @return the index of the first occurrence of the specified substring,
	 * or {@code -1} if there is no such occurrence.
	 */
	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring, starting at the specified index.
	 * <p>
	 * <p>The returned index is the smallest value <i>k</i> for which:
	 * <blockquote><pre>
	 * <i>k</i> &gt;= fromIndex {@code &&} this.startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then {@code -1} is returned.
	 *
	 * @param str the substring to search for.
	 * @param fromIndex the index from which to start the search.
	 *
	 * @return the index of the first occurrence of the specified substring,
	 * starting at the specified index,
	 * or {@code -1} if there is no such occurrence.
	 */
	public int indexOf(String str, int fromIndex) {
		return indexOf(value, 0, value.length, str.value, 0, str.value.length, fromIndex);
	}

	/**
	 * Returns the index within this string of the last occurrence of the
	 * specified substring.  The last occurrence of the empty string ""
	 * is considered to occur at the index value {@code this.length()}.
	 * <p>
	 * <p>The returned index is the largest value <i>k</i> for which:
	 * <blockquote><pre>
	 * this.startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then {@code -1} is returned.
	 *
	 * @param str the substring to search for.
	 *
	 * @return the index of the last occurrence of the specified substring,
	 * or {@code -1} if there is no such occurrence.
	 */
	public int lastIndexOf(String str) {
		return lastIndexOf(str, value.length);
	}

	/**
	 * Returns the index within this string of the last occurrence of the
	 * specified substring, searching backward starting at the specified index.
	 * <p>
	 * <p>The returned index is the largest value <i>k</i> for which:
	 * <blockquote><pre>
	 * <i>k</i> {@code <=} fromIndex {@code &&} this.startsWith(str, <i>k</i>)
	 * </pre></blockquote>
	 * If no such value of <i>k</i> exists, then {@code -1} is returned.
	 *
	 * @param str the substring to search for.
	 * @param fromIndex the index to start the search from.
	 *
	 * @return the index of the last occurrence of the specified substring,
	 * searching backward from the specified index,
	 * or {@code -1} if there is no such occurrence.
	 */
	public int lastIndexOf(String str, int fromIndex) {
		return lastIndexOf(value, 0, value.length, str.value, 0, str.value.length, fromIndex);
	}

	/**
	 * Returns a string that is a substring of this string. The
	 * substring begins with the character at the specified index and
	 * extends to the end of this string. <p>
	 * Examples:
	 * <blockquote><pre>
	 * "unhappy".substring(2) returns "happy"
	 * "Harbison".substring(3) returns "bison"
	 * "emptiness".substring(9) returns "" (an empty string)
	 * </pre></blockquote>
	 *
	 * @param beginIndex the beginning index, inclusive.
	 *
	 * @return the specified substring.
	 *
	 * @throws IndexOutOfBoundsException if
	 * {@code beginIndex} is negative or larger than the
	 * length of this {@code String} object.
	 */
	public String substring(int beginIndex) {
		if (beginIndex < 0) {
			throw new StringIndexOutOfBoundsException(beginIndex);
		}
		int subLen = value.length - beginIndex;
		if (subLen < 0) {
			throw new StringIndexOutOfBoundsException(subLen);
		}
		return (beginIndex == 0) ? this : new String(value, beginIndex, subLen);
	}

	/**
	 * Returns a string that is a substring of this string. The
	 * substring begins at the specified {@code beginIndex} and
	 * extends to the character at index {@code endIndex - 1}.
	 * Thus the length of the substring is {@code endIndex-beginIndex}.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * "hamburger".substring(4, 8) returns "urge"
	 * "smiles".substring(1, 5) returns "mile"
	 * </pre></blockquote>
	 *
	 * @param beginIndex the beginning index, inclusive.
	 * @param endIndex the ending index, exclusive.
	 *
	 * @return the specified substring.
	 *
	 * @throws IndexOutOfBoundsException if the
	 * {@code beginIndex} is negative, or
	 * {@code endIndex} is larger than the length of
	 * this {@code String} object, or
	 * {@code beginIndex} is larger than
	 * {@code endIndex}.
	 */
	public String substring(int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new StringIndexOutOfBoundsException(beginIndex);
		}
		if (endIndex > value.length) {
			throw new StringIndexOutOfBoundsException(endIndex);
		}
		int subLen = endIndex - beginIndex;
		if (subLen < 0) {
			throw new StringIndexOutOfBoundsException(subLen);
		}
		return ((beginIndex == 0) && (endIndex == value.length)) ? this : new String(value, beginIndex, subLen);
	}

	/**
	 * Returns a character sequence that is a subsequence of this sequence.
	 * <p>
	 * <p> An invocation of this method of the form
	 * <p>
	 * <blockquote><pre>
	 * str.subSequence(begin,&nbsp;end)</pre></blockquote>
	 * <p>
	 * behaves in exactly the same way as the invocation
	 * <p>
	 * <blockquote><pre>
	 * str.substring(begin,&nbsp;end)</pre></blockquote>
	 *
	 * @param beginIndex the begin index, inclusive.
	 * @param endIndex the end index, exclusive.
	 *
	 * @return the specified subsequence.
	 *
	 * @throws IndexOutOfBoundsException if {@code beginIndex} or {@code endIndex} is negative,
	 * if {@code endIndex} is greater than {@code length()},
	 * or if {@code beginIndex} is greater than {@code endIndex}
	 * @apiNote This method is defined so that the {@code String} class can implement
	 * the {@link CharSequence} interface.
	 * @spec JSR-51
	 * @since 1.4
	 */
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return this.substring(beginIndex, endIndex);
	}

	/**
	 * Concatenates the specified string to the end of this string.
	 * <p>
	 * If the length of the argument string is {@code 0}, then this
	 * {@code String} object is returned. Otherwise, a
	 * {@code String} object is returned that represents a character
	 * sequence that is the concatenation of the character sequence
	 * represented by this {@code String} object and the character
	 * sequence represented by the argument string.<p>
	 * Examples:
	 * <blockquote><pre>
	 * "cares".concat("s") returns "caress"
	 * "to".concat("get").concat("her") returns "together"
	 * </pre></blockquote>
	 *
	 * @param str the {@code String} that is concatenated to the end
	 * of this {@code String}.
	 *
	 * @return a string that represents the concatenation of this object's
	 * characters followed by the string argument's characters.
	 */
	public String concat(String str) {
		int otherLen = str.length();
		if (otherLen == 0) {
			return this;
		}
		int len = value.length;
		char buf[] = Arrays.copyOf(value, len + otherLen);
		str.getChars(buf, len);
		return new String(buf, true);
	}

	/**
	 * Returns a string resulting from replacing all occurrences of
	 * {@code oldChar} in this string with {@code newChar}.
	 * <p>
	 * If the character {@code oldChar} does not occur in the
	 * character sequence represented by this {@code String} object,
	 * then a reference to this {@code String} object is returned.
	 * Otherwise, a {@code String} object is returned that
	 * represents a character sequence identical to the character sequence
	 * represented by this {@code String} object, except that every
	 * occurrence of {@code oldChar} is replaced by an occurrence
	 * of {@code newChar}.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * "mesquite in your cellar".replace('e', 'o')
	 *         returns "mosquito in your collar"
	 * "the war of baronets".replace('r', 'y')
	 *         returns "the way of bayonets"
	 * "sparring with a purple porpoise".replace('p', 't')
	 *         returns "starring with a turtle tortoise"
	 * "JonL".replace('q', 'x') returns "JonL" (no change)
	 * </pre></blockquote>
	 *
	 * @param oldChar the old character.
	 * @param newChar the new character.
	 *
	 * @return a string derived from this string by replacing every
	 * occurrence of {@code oldChar} with {@code newChar}.
	 */
	public String replace(char oldChar, char newChar) {
		if (oldChar != newChar) {
			int len = value.length;
			int i = -1;
			char[] val = value; /* avoid getfield opcode */

			while (++i < len) {
				if (val[i] == oldChar) {
					break;
				}
			}
			if (i < len) {
				char buf[] = new char[len];
				for (int j = 0; j < i; j++) {
					buf[j] = val[j];
				}
				while (i < len) {
					char c = val[i];
					buf[i] = (c == oldChar) ? newChar : c;
					i++;
				}
				return new String(buf, true);
			}
		}
		return this;
	}

	/**
	 * Tells whether or not this string matches the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a>.
	 * <p>
	 * <p> An invocation of this method of the form
	 * <i>str</i>{@code .matches(}<i>regex</i>{@code )} yields exactly the
	 * same result as the expression
	 * <p>
	 * <blockquote>
	 * {@link java.util.regex.Pattern}.{@link java.util.regex.Pattern#matches(String, CharSequence)
	 * matches(<i>regex</i>, <i>str</i>)}
	 * </blockquote>
	 *
	 * @param regex the regular expression to which this string is to be matched
	 *
	 * @return {@code true} if, and only if, this string matches the
	 * given regular expression
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 * @spec JSR-51
	 * @see java.util.regex.Pattern
	 * @since 1.4
	 */
	public boolean matches(String regex) {
		return Pattern.matches(regex, this);
	}

	/**
	 * Returns true if and only if this string contains the specified
	 * sequence of char values.
	 *
	 * @param s the sequence to search for
	 *
	 * @return true if this string contains {@code s}, false otherwise
	 *
	 * @since 1.5
	 */
	public boolean contains(CharSequence s) {
		return indexOf(s.toString()) > -1;
	}

	/**
	 * Replaces the first substring of this string that matches the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a> with the
	 * given replacement.
	 * <p>
	 * <p> An invocation of this method of the form
	 * <i>str</i>{@code .replaceFirst(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
	 * yields exactly the same result as the expression
	 * <p>
	 * <blockquote>
	 * <code>
	 * {@link java.util.regex.Pattern}.{@link
	 * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
	 * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
	 * java.util.regex.Matcher#replaceFirst replaceFirst}(<i>repl</i>)
	 * </code>
	 * </blockquote>
	 * <p>
	 * <p>
	 * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
	 * replacement string may cause the results to be different than if it were
	 * being treated as a literal replacement string; see
	 * {@link java.util.regex.Matcher#replaceFirst}.
	 * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
	 * meaning of these characters, if desired.
	 *
	 * @param regex the regular expression to which this string is to be matched
	 * @param replacement the string to be substituted for the first match
	 *
	 * @return The resulting {@code String}
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 * @spec JSR-51
	 * @see java.util.regex.Pattern
	 * @since 1.4
	 */
	public String replaceFirst(String regex, String replacement) {
		return Pattern.compile(regex).matcher(this).replaceFirst(replacement);
	}

	/**
	 * Replaces each substring of this string that matches the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a> with the
	 * given replacement.
	 * <p>
	 * <p> An invocation of this method of the form
	 * <i>str</i>{@code .replaceAll(}<i>regex</i>{@code ,} <i>repl</i>{@code )}
	 * yields exactly the same result as the expression
	 * <p>
	 * <blockquote>
	 * <code>
	 * {@link java.util.regex.Pattern}.{@link
	 * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
	 * java.util.regex.Pattern#matcher(java.lang.CharSequence) matcher}(<i>str</i>).{@link
	 * java.util.regex.Matcher#replaceAll replaceAll}(<i>repl</i>)
	 * </code>
	 * </blockquote>
	 * <p>
	 * <p>
	 * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
	 * replacement string may cause the results to be different than if it were
	 * being treated as a literal replacement string; see
	 * {@link java.util.regex.Matcher#replaceAll Matcher.replaceAll}.
	 * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
	 * meaning of these characters, if desired.
	 *
	 * @param regex the regular expression to which this string is to be matched
	 * @param replacement the string to be substituted for each match
	 *
	 * @return The resulting {@code String}
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 * @spec JSR-51
	 * @see java.util.regex.Pattern
	 * @since 1.4
	 */
	public String replaceAll(String regex, String replacement) {
		return Pattern.compile(regex).matcher(this).replaceAll(replacement);
	}

	/**
	 * Replaces each substring of this string that matches the literal target
	 * sequence with the specified literal replacement sequence. The
	 * replacement proceeds from the beginning of the string to the end, for
	 * example, replacing "aa" with "b" in the string "aaa" will result in
	 * "ba" rather than "ab".
	 *
	 * @param target The sequence of char values to be replaced
	 * @param replacement The replacement sequence of char values
	 *
	 * @return The resulting string
	 *
	 * @since 1.5
	 */
	public String replace(CharSequence target, CharSequence replacement) {
		return Pattern.compile(target.toString(), Pattern.LITERAL).matcher(this).replaceAll(Matcher.quoteReplacement(replacement.toString()));
	}

	/**
	 * Splits this string around matches of the given
	 * <a href="../util/regex/Pattern.html#sum">regular expression</a>.
	 * <p>
	 * <p> The array returned by this method contains each substring of this
	 * string that is terminated by another substring that matches the given
	 * expression or is terminated by the end of the string.  The substrings in
	 * the array are in the order in which they occur in this string.  If the
	 * expression does not match any part of the input then the resulting array
	 * has just one element, namely this string.
	 * <p>
	 * <p> When there is a positive-width match at the beginning of this
	 * string then an empty leading substring is included at the beginning
	 * of the resulting array. A zero-width match at the beginning however
	 * never produces such empty leading substring.
	 * <p>
	 * <p> The {@code limit} parameter controls the number of times the
	 * pattern is applied and therefore affects the length of the resulting
	 * array.  If the limit <i>n</i> is greater than zero then the pattern
	 * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
	 * length will be no greater than <i>n</i>, and the array's last entry
	 * will contain all input beyond the last matched delimiter.  If <i>n</i>
	 * is non-positive then the pattern will be applied as many times as
	 * possible and the array can have any length.  If <i>n</i> is zero then
	 * the pattern will be applied as many times as possible, the array can
	 * have any length, and trailing empty strings will be discarded.
	 * <p>
	 * <p> The string {@code "boo:and:foo"}, for example, yields the
	 * following results with these parameters:
	 * <p>
	 * <blockquote><table cellpadding=1 cellspacing=0 summary="Split example showing regex, limit, and result">
	 * <tr>
	 * <th>Regex</th>
	 * <th>Limit</th>
	 * <th>Result</th>
	 * </tr>
	 * <tr><td align=center>:</td>
	 * <td align=center>2</td>
	 * <td>{@code { "boo", "and:foo" }}</td></tr>
	 * <tr><td align=center>:</td>
	 * <td align=center>5</td>
	 * <td>{@code { "boo", "and", "foo" }}</td></tr>
	 * <tr><td align=center>:</td>
	 * <td align=center>-2</td>
	 * <td>{@code { "boo", "and", "foo" }}</td></tr>
	 * <tr><td align=center>o</td>
	 * <td align=center>5</td>
	 * <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
	 * <tr><td align=center>o</td>
	 * <td align=center>-2</td>
	 * <td>{@code { "b", "", ":and:f", "", "" }}</td></tr>
	 * <tr><td align=center>o</td>
	 * <td align=center>0</td>
	 * <td>{@code { "b", "", ":and:f" }}</td></tr>
	 * </table></blockquote>
	 * <p>
	 * <p> An invocation of this method of the form
	 * <i>str.</i>{@code split(}<i>regex</i>{@code ,}&nbsp;<i>n</i>{@code )}
	 * yields the same result as the expression
	 * <p>
	 * <blockquote>
	 * <code>
	 * {@link java.util.regex.Pattern}.{@link
	 * java.util.regex.Pattern#compile compile}(<i>regex</i>).{@link
	 * java.util.regex.Pattern#split(java.lang.CharSequence, int) split}(<i>str</i>,&nbsp;<i>n</i>)
	 * </code>
	 * </blockquote>
	 *
	 * @param regex the delimiting regular expression
	 * @param limit the result threshold, as described above
	 *
	 * @return the array of strings computed by splitting this string
	 * around matches of the given regular expression
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 * @spec JSR-51
	 * @see java.util.regex.Pattern
	 * @since 1.4
	 */
	public String[] split(String regex, int limit) {
		/* fastpath if the regex is a
		 (1)one-char String and this character is not one of the
            RegEx's meta characters ".$|()[{^?*+\\", or
         (2)two-char String and the first char is the backslash and
            the second is not the ascii digit or ascii letter.
         */
		char ch = 0;
		if (((regex.value.length == 1 && ".$|()[{^?*+\\".indexOf(ch = regex.charAt(0)) == -1) || (regex.length() == 2 && regex.charAt(0) == '\\'
				&& (((ch = regex.charAt(1)) - '0') | ('9' - ch)) < 0 && ((ch - 'a') | ('z' - ch)) < 0 && ((ch - 'A') | ('Z' - ch)) < 0)) && (
				ch < Character.MIN_HIGH_SURROGATE || ch > Character.MAX_LOW_SURROGATE)) {
			int off = 0;
			int next = 0;
			boolean limited = limit > 0;
			ArrayList<String> list = new ArrayList<>();
			while ((next = indexOf(ch, off)) != -1) {
				if (!limited || list.size() < limit - 1) {
					list.add(substring(off, next));
					off = next + 1;
				} else {    // last one
					//assert (list.size() == limit - 1);
					list.add(substring(off, value.length));
					off = value.length;
					break;
				}
			}
			// If no match was found, return this
			if (off == 0) {
				return new String[] { this };
			}

			// Add remaining segment
			if (!limited || list.size() < limit) {
				list.add(substring(off, value.length));
			}

			// Construct result
			int resultSize = list.size();
			if (limit == 0) {
				while (resultSize > 0 && list.get(resultSize - 1).length() == 0) {
					resultSize--;
				}
			}
			String[] result = new String[resultSize];
			return list.subList(0, resultSize).toArray(result);
		}
		return Pattern.compile(regex).split(this, limit);
	}

	/**
	 * Splits this string around matches of the given <a
	 * href="../util/regex/Pattern.html#sum">regular expression</a>.
	 * <p>
	 * <p> This method works as if by invoking the two-argument {@link
	 * #split(String, int) split} method with the given expression and a limit
	 * argument of zero.  Trailing empty strings are therefore not included in
	 * the resulting array.
	 * <p>
	 * <p> The string {@code "boo:and:foo"}, for example, yields the following
	 * results with these expressions:
	 * <p>
	 * <blockquote><table cellpadding=1 cellspacing=0 summary="Split examples showing regex and result">
	 * <tr>
	 * <th>Regex</th>
	 * <th>Result</th>
	 * </tr>
	 * <tr><td align=center>:</td>
	 * <td>{@code { "boo", "and", "foo" }}</td></tr>
	 * <tr><td align=center>o</td>
	 * <td>{@code { "b", "", ":and:f" }}</td></tr>
	 * </table></blockquote>
	 *
	 * @param regex the delimiting regular expression
	 *
	 * @return the array of strings computed by splitting this string
	 * around matches of the given regular expression
	 *
	 * @throws PatternSyntaxException if the regular expression's syntax is invalid
	 * @spec JSR-51
	 * @see java.util.regex.Pattern
	 * @since 1.4
	 */
	public String[] split(String regex) {
		return split(regex, 0);
	}

	/**
	 * Converts all of the characters in this {@code String} to lower
	 * case using the rules of the given {@code Locale}.  Case mapping is based
	 * on the Unicode Standard version specified by the {@link java.lang.Character Character}
	 * class. Since case mappings are not always 1:1 char mappings, the resulting
	 * {@code String} may be a different length than the original {@code String}.
	 * <p>
	 * Examples of lowercase  mappings are in the following table:
	 * <table border="1" summary="Lowercase mapping examples showing language code of locale, upper case, lower case, and description">
	 * <tr>
	 * <th>Language Code of Locale</th>
	 * <th>Upper Case</th>
	 * <th>Lower Case</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0130</td>
	 * <td>&#92;u0069</td>
	 * <td>capital letter I with dot above -&gt; small letter i</td>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0049</td>
	 * <td>&#92;u0131</td>
	 * <td>capital letter I -&gt; small letter dotless i </td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>French Fries</td>
	 * <td>french fries</td>
	 * <td>lowercased all chars in String</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td><img src="doc-files/capiota.gif" alt="capiota"><img src="doc-files/capchi.gif" alt="capchi">
	 * <img src="doc-files/captheta.gif" alt="captheta"><img src="doc-files/capupsil.gif" alt="capupsil">
	 * <img src="doc-files/capsigma.gif" alt="capsigma"></td>
	 * <td><img src="doc-files/iota.gif" alt="iota"><img src="doc-files/chi.gif" alt="chi">
	 * <img src="doc-files/theta.gif" alt="theta"><img src="doc-files/upsilon.gif" alt="upsilon">
	 * <img src="doc-files/sigma1.gif" alt="sigma"></td>
	 * <td>lowercased all chars in String</td>
	 * </tr>
	 * </table>
	 *
	 * @param locale use the case transformation rules for this locale
	 *
	 * @return the {@code String}, converted to lowercase.
	 *
	 * @see java.lang.String#toLowerCase()
	 * @see java.lang.String#toUpperCase()
	 * @see java.lang.String#toUpperCase(Locale)
	 * @since 1.1
	 */
	public String toLowerCase(Locale locale) {
		if (locale == null) {
			throw new NullPointerException();
		}

		int firstUpper;
		final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
		scan:
		{
			for (firstUpper = 0; firstUpper < len; ) {
				char c = value[firstUpper];
				if ((c >= Character.MIN_HIGH_SURROGATE) && (c <= Character.MAX_HIGH_SURROGATE)) {
					int supplChar = codePointAt(firstUpper);
					if (supplChar != Character.toLowerCase(supplChar)) {
						break scan;
					}
					firstUpper += Character.charCount(supplChar);
				} else {
					if (c != Character.toLowerCase(c)) {
						break scan;
					}
					firstUpper++;
				}
			}
			return this;
		}

		char[] result = new char[len];
		int resultOffset = 0;  /* result may grow, so i+resultOffset
								* is the write location in result */

        /* Just copy the first few lowerCase characters. */
		System.arraycopy(value, 0, result, 0, firstUpper);

		String lang = locale.getLanguage();
		boolean localeDependent = (lang == "tr" || lang == "az" || lang == "lt");
		char[] lowerCharArray;
		int lowerChar;
		int srcChar;
		int srcCount;
		for (int i = firstUpper; i < len; i += srcCount) {
			srcChar = (int) value[i];
			if ((char) srcChar >= Character.MIN_HIGH_SURROGATE && (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
				srcChar = codePointAt(i);
				srcCount = Character.charCount(srcChar);
			} else {
				srcCount = 1;
			}
			if (localeDependent || srcChar == '\u03A3' || // GREEK CAPITAL LETTER SIGMA
					srcChar == '\u0130') { // LATIN CAPITAL LETTER I WITH DOT ABOVE
				lowerChar = ConditionalSpecialCasing.toLowerCaseEx(this, i, locale);
			} else {
				lowerChar = Character.toLowerCase(srcChar);
			}
			if ((lowerChar == Character.ERROR) || (lowerChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
				if (lowerChar == Character.ERROR) {
					lowerCharArray = ConditionalSpecialCasing.toLowerCaseCharArray(this, i, locale);
				} else if (srcCount == 2) {
					resultOffset += Character.toChars(lowerChar, result, i + resultOffset) - srcCount;
					continue;
				} else {
					lowerCharArray = Character.toChars(lowerChar);
				}

                /* Grow result if needed */
				int mapLen = lowerCharArray.length;
				if (mapLen > srcCount) {
					char[] result2 = new char[result.length + mapLen - srcCount];
					System.arraycopy(result, 0, result2, 0, i + resultOffset);
					result = result2;
				}
				for (int x = 0; x < mapLen; ++x) {
					result[i + resultOffset + x] = lowerCharArray[x];
				}
				resultOffset += (mapLen - srcCount);
			} else {
				result[i + resultOffset] = (char) lowerChar;
			}
		}
		return new String(result, 0, len + resultOffset);
	}

	/**
	 * Converts all of the characters in this {@code String} to lower
	 * case using the rules of the default locale. This is equivalent to calling
	 * {@code toLowerCase(Locale.getDefault())}.
	 * <p>
	 * <b>Note:</b> This method is locale sensitive, and may produce unexpected
	 * results if used for strings that are intended to be interpreted locale
	 * independently.
	 * Examples are programming language identifiers, protocol keys, and HTML
	 * tags.
	 * For instance, {@code "TITLE".toLowerCase()} in a Turkish locale
	 * returns {@code "t\u005Cu0131tle"}, where '\u005Cu0131' is the
	 * LATIN SMALL LETTER DOTLESS I character.
	 * To obtain correct results for locale insensitive strings, use
	 * {@code toLowerCase(Locale.ROOT)}.
	 * <p>
	 *
	 * @return the {@code String}, converted to lowercase.
	 *
	 * @see java.lang.String#toLowerCase(Locale)
	 */
	public String toLowerCase() {
		return toLowerCase(Locale.getDefault());
	}

	/**
	 * Converts all of the characters in this {@code String} to upper
	 * case using the rules of the given {@code Locale}. Case mapping is based
	 * on the Unicode Standard version specified by the {@link java.lang.Character Character}
	 * class. Since case mappings are not always 1:1 char mappings, the resulting
	 * {@code String} may be a different length than the original {@code String}.
	 * <p>
	 * Examples of locale-sensitive and 1:M case mappings are in the following table.
	 * <p>
	 * <table border="1" summary="Examples of locale-sensitive and 1:M case mappings. Shows Language code of locale, lower case, upper case, and description.">
	 * <tr>
	 * <th>Language Code of Locale</th>
	 * <th>Lower Case</th>
	 * <th>Upper Case</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0069</td>
	 * <td>&#92;u0130</td>
	 * <td>small letter i -&gt; capital letter I with dot above</td>
	 * </tr>
	 * <tr>
	 * <td>tr (Turkish)</td>
	 * <td>&#92;u0131</td>
	 * <td>&#92;u0049</td>
	 * <td>small letter dotless i -&gt; capital letter I</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>&#92;u00df</td>
	 * <td>&#92;u0053 &#92;u0053</td>
	 * <td>small letter sharp s -&gt; two letters: SS</td>
	 * </tr>
	 * <tr>
	 * <td>(all)</td>
	 * <td>Fahrvergn&uuml;gen</td>
	 * <td>FAHRVERGN&Uuml;GEN</td>
	 * <td></td>
	 * </tr>
	 * </table>
	 *
	 * @param locale use the case transformation rules for this locale
	 *
	 * @return the {@code String}, converted to uppercase.
	 *
	 * @see java.lang.String#toUpperCase()
	 * @see java.lang.String#toLowerCase()
	 * @see java.lang.String#toLowerCase(Locale)
	 * @since 1.1
	 */
	public String toUpperCase(Locale locale) {
		if (locale == null) {
			throw new NullPointerException();
		}

		int firstLower;
		final int len = value.length;

        /* Now check if there are any characters that need to be changed. */
		scan:
		{
			for (firstLower = 0; firstLower < len; ) {
				int c = (int) value[firstLower];
				int srcCount;
				if ((c >= Character.MIN_HIGH_SURROGATE) && (c <= Character.MAX_HIGH_SURROGATE)) {
					c = codePointAt(firstLower);
					srcCount = Character.charCount(c);
				} else {
					srcCount = 1;
				}
				int upperCaseChar = Character.toUpperCaseEx(c);
				if ((upperCaseChar == Character.ERROR) || (c != upperCaseChar)) {
					break scan;
				}
				firstLower += srcCount;
			}
			return this;
		}

        /* result may grow, so i+resultOffset is the write location in result */
		int resultOffset = 0;
		char[] result = new char[len]; /* may grow */

        /* Just copy the first few upperCase characters. */
		System.arraycopy(value, 0, result, 0, firstLower);

		String lang = locale.getLanguage();
		boolean localeDependent = (lang == "tr" || lang == "az" || lang == "lt");
		char[] upperCharArray;
		int upperChar;
		int srcChar;
		int srcCount;
		for (int i = firstLower; i < len; i += srcCount) {
			srcChar = (int) value[i];
			if ((char) srcChar >= Character.MIN_HIGH_SURROGATE && (char) srcChar <= Character.MAX_HIGH_SURROGATE) {
				srcChar = codePointAt(i);
				srcCount = Character.charCount(srcChar);
			} else {
				srcCount = 1;
			}
			if (localeDependent) {
				upperChar = ConditionalSpecialCasing.toUpperCaseEx(this, i, locale);
			} else {
				upperChar = Character.toUpperCaseEx(srcChar);
			}
			if ((upperChar == Character.ERROR) || (upperChar >= Character.MIN_SUPPLEMENTARY_CODE_POINT)) {
				if (upperChar == Character.ERROR) {
					if (localeDependent) {
						upperCharArray = ConditionalSpecialCasing.toUpperCaseCharArray(this, i, locale);
					} else {
						upperCharArray = Character.toUpperCaseCharArray(srcChar);
					}
				} else if (srcCount == 2) {
					resultOffset += Character.toChars(upperChar, result, i + resultOffset) - srcCount;
					continue;
				} else {
					upperCharArray = Character.toChars(upperChar);
				}

                /* Grow result if needed */
				int mapLen = upperCharArray.length;
				if (mapLen > srcCount) {
					char[] result2 = new char[result.length + mapLen - srcCount];
					System.arraycopy(result, 0, result2, 0, i + resultOffset);
					result = result2;
				}
				for (int x = 0; x < mapLen; ++x) {
					result[i + resultOffset + x] = upperCharArray[x];
				}
				resultOffset += (mapLen - srcCount);
			} else {
				result[i + resultOffset] = (char) upperChar;
			}
		}
		return new String(result, 0, len + resultOffset);
	}

	/**
	 * Converts all of the characters in this {@code String} to upper
	 * case using the rules of the default locale. This method is equivalent to
	 * {@code toUpperCase(Locale.getDefault())}.
	 * <p>
	 * <b>Note:</b> This method is locale sensitive, and may produce unexpected
	 * results if used for strings that are intended to be interpreted locale
	 * independently.
	 * Examples are programming language identifiers, protocol keys, and HTML
	 * tags.
	 * For instance, {@code "title".toUpperCase()} in a Turkish locale
	 * returns {@code "T\u005Cu0130TLE"}, where '\u005Cu0130' is the
	 * LATIN CAPITAL LETTER I WITH DOT ABOVE character.
	 * To obtain correct results for locale insensitive strings, use
	 * {@code toUpperCase(Locale.ROOT)}.
	 * <p>
	 *
	 * @return the {@code String}, converted to uppercase.
	 *
	 * @see java.lang.String#toUpperCase(Locale)
	 */
	public String toUpperCase() {
		return toUpperCase(Locale.getDefault());
	}

	/**
	 * Returns a string whose value is this string, with any leading and trailing
	 * whitespace removed.
	 * <p>
	 * If this {@code String} object represents an empty character
	 * sequence, or the first and last characters of character sequence
	 * represented by this {@code String} object both have codes
	 * greater than {@code '\u005Cu0020'} (the space character), then a
	 * reference to this {@code String} object is returned.
	 * <p>
	 * Otherwise, if there is no character with a code greater than
	 * {@code '\u005Cu0020'} in the string, then a
	 * {@code String} object representing an empty string is
	 * returned.
	 * <p>
	 * Otherwise, let <i>k</i> be the index of the first character in the
	 * string whose code is greater than {@code '\u005Cu0020'}, and let
	 * <i>m</i> be the index of the last character in the string whose code
	 * is greater than {@code '\u005Cu0020'}. A {@code String}
	 * object is returned, representing the substring of this string that
	 * begins with the character at index <i>k</i> and ends with the
	 * character at index <i>m</i>-that is, the result of
	 * {@code this.substring(k, m + 1)}.
	 * <p>
	 * This method may be used to trim whitespace (as defined above) from
	 * the beginning and end of a string.
	 *
	 * @return A string whose value is this string, with any leading and trailing white
	 * space removed, or this string if it has no leading or
	 * trailing white space.
	 */
	public String trim() {
		int len = value.length;
		int st = 0;
		char[] val = value;    /* avoid getfield opcode */

		while ((st < len) && (val[st] <= ' ')) {
			st++;
		}
		while ((st < len) && (val[len - 1] <= ' ')) {
			len--;
		}
		return ((st > 0) || (len < value.length)) ? substring(st, len) : this;
	}

	/**
	 * This object (which is already a string!) is itself returned.
	 *
	 * @return the string itself.
	 */
	public String toString() {
		return this;
	}

	/**
	 * Converts this string to a new character array.
	 *
	 * @return a newly allocated character array whose length is the length
	 * of this string and whose contents are initialized to contain
	 * the character sequence represented by this string.
	 */
	public char[] toCharArray() {
		// Cannot use Arrays.copyOf because of class initialization order issues
		char result[] = new char[value.length];
		System.arraycopy(value, 0, result, 0, value.length);
		return result;
	}

	/**
	 * Returns a canonical representation for the string object.
	 * <p>
	 * A pool of strings, initially empty, is maintained privately by the
	 * class {@code String}.
	 * <p>
	 * When the intern method is invoked, if the pool already contains a
	 * string equal to this {@code String} object as determined by
	 * the {@link #equals(Object)} method, then the string from the pool is
	 * returned. Otherwise, this {@code String} object is added to the
	 * pool and a reference to this {@code String} object is returned.
	 * <p>
	 * It follows that for any two strings {@code s} and {@code t},
	 * {@code s.intern() == t.intern()} is {@code true}
	 * if and only if {@code s.equals(t)} is {@code true}.
	 * <p>
	 * All literal strings and string-valued constant expressions are
	 * interned. String literals are defined in section 3.10.5 of the
	 * <cite>The Java&trade; Language Specification</cite>.
	 *
	 * @return a string that has the same contents as this string, but is
	 * guaranteed to be from a pool of unique strings.
	 */
	public native String intern();

	private static class CaseInsensitiveComparator implements Comparator<String>, java.io.Serializable {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 8575799808933029326L;

		public int compare(String s1, String s2) {
			int n1 = s1.length();
			int n2 = s2.length();
			int min = Math.min(n1, n2);
			for (int i = 0; i < min; i++) {
				char c1 = s1.charAt(i);
				char c2 = s2.charAt(i);
				if (c1 != c2) {
					c1 = Character.toUpperCase(c1);
					c2 = Character.toUpperCase(c2);
					if (c1 != c2) {
						c1 = Character.toLowerCase(c1);
						c2 = Character.toLowerCase(c2);
						if (c1 != c2) {
							// No overflow because of numeric promotion
							return c1 - c2;
						}
					}
				}
			}
			return n1 - n2;
		}

		/**
		 * Replaces the de-serialized object.
		 */
		private Object readResolve() {
			return CASE_INSENSITIVE_ORDER;
		}
	}
}