public final class String implements java.io.Serializable, Comparable<String>, CharSequence {

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
	 * 等同于 {@link #valueOf(char[])}.
	 *
	 * @param data 字符数组
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
	 * Returns the length of this string.
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
	 * Returns {@code true} if, and only if, {@link #length()} is {@code 0}.
	 *
	 * @return {@code true} if {@link #length()} is {@code 0}, otherwise
	 * {@code false}
	 *
	 * @since 1.6
	 */
	public boolean isEmpty() {
		return value.length == 0;
	}

	/**
	 * Returns the {@code char} value at the
	 * specified index. An index ranges from {@code 0} to
	 * {@code length() - 1}. The first {@code char} value of the sequence
	 * is at index {@code 0}, the next at index {@code 1},
	 * and so on, as for array indexing.
	 * <p>
	 * <p>If the {@code char} value specified by the index is a
	 * <a href="Character.html#unicode">surrogate</a>, the surrogate
	 * value is returned.
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
	 * Returns the character (Unicode code point) at the specified
	 * index. The index refers to {@code char} values
	 * (Unicode code units) and ranges from {@code 0} to
	 * {@link #length()}{@code  - 1}.
	 * <p>
	 * <p> If the {@code char} value specified at the given index
	 * is in the high-surrogate range, the following index is less
	 * than the length of this {@code String}, and the
	 * {@code char} value at the following index is in the
	 * low-surrogate range, then the supplementary code point
	 * corresponding to this surrogate pair is returned. Otherwise,
	 * the {@code char} value at the given index is returned.
	 *
	 * @param index the index to the {@code char} values
	 *
	 * @return the code point value of the character at the
	 * {@code index}
	 *
	 * @throws IndexOutOfBoundsException if the {@code index}
	 * argument is negative or not less than the length of this
	 * string.
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
	 * Copy characters from this string into dst starting at dstBegin.
	 * This method doesn't perform any range checking.
	 */
	void getChars(char dst[], int dstBegin) {
		System.arraycopy(value, 0, dst, dstBegin, value.length);
	}

	/**
	 * Copies characters from this string into the destination character
	 * array.
	 * <p>
	 * The first character to be copied is at index {@code srcBegin};
	 * the last character to be copied is at index {@code srcEnd-1}
	 * (thus the total number of characters to be copied is
	 * {@code srcEnd-srcBegin}). The characters are copied into the
	 * subarray of {@code dst} starting at index {@code dstBegin}
	 * and ending at index:
	 * <blockquote><pre>
	 *     dstBegin + (srcEnd-srcBegin) - 1
	 * </pre></blockquote>
	 *
	 * @param srcBegin index of the first character in the string
	 * to copy.
	 * @param srcEnd index after the last character in the string
	 * to copy.
	 * @param dst the destination array.
	 * @param dstBegin the start offset in the destination array.
	 *
	 * @throws IndexOutOfBoundsException If any of the following
	 * is true:
	 * <ul><li>{@code srcBegin} is negative.
	 * <li>{@code srcBegin} is greater than {@code srcEnd}
	 * <li>{@code srcEnd} is greater than the length of this
	 * string
	 * <li>{@code dstBegin} is negative
	 * <li>{@code dstBegin+(srcEnd-srcBegin)} is larger than
	 * {@code dst.length}</ul>
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
	 * Copies characters from this string into the destination byte array. Each
	 * byte receives the 8 low-order bits of the corresponding character. The
	 * eight high-order bits of each character are not copied and do not
	 * participate in the transfer in any way.
	 * <p>
	 * <p> The first character to be copied is at index {@code srcBegin}; the
	 * last character to be copied is at index {@code srcEnd-1}.  The total
	 * number of characters to be copied is {@code srcEnd-srcBegin}. The
	 * characters, converted to bytes, are copied into the subarray of {@code
	 * dst} starting at index {@code dstBegin} and ending at index:
	 * <p>
	 * <blockquote><pre>
	 *     dstBegin + (srcEnd-srcBegin) - 1
	 * </pre></blockquote>
	 *
	 * @param srcBegin Index of the first character in the string to copy
	 * @param srcEnd Index after the last character in the string to copy
	 * @param dst The destination array
	 * @param dstBegin The start offset in the destination array
	 *
	 * @throws IndexOutOfBoundsException If any of the following is true:
	 * <ul>
	 * <li> {@code srcBegin} is negative
	 * <li> {@code srcBegin} is greater than {@code srcEnd}
	 * <li> {@code srcEnd} is greater than the length of this String
	 * <li> {@code dstBegin} is negative
	 * <li> {@code dstBegin+(srcEnd-srcBegin)} is larger than {@code
	 * dst.length}
	 * </ul>
	 * @deprecated This method does not properly convert characters into
	 * bytes.  As of JDK&nbsp;1.1, the preferred way to do this is via the
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

}