package Server;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public abstract class Utils {

	public static String[] arr(String... elements) {
		return elements;
	}

	public static String[] arr(Object... elements) {
		return Arrays.stream(elements).map(String::valueOf).toArray(String[]::new);
	}

	public static String quote(String element) {
		return "'" + element + "'";
	}

	public static String quote(Object value) {
		if (value instanceof String) {
			return quote((String) value);
		} else {
			return String.valueOf(value);
		}
	}

	public static String[] quoteArr(Object... elements) {
		return Arrays.stream(elements).map(Utils::quote).toArray(String[]::new);
	}

	public static StringBuilder iterateArray(StringBuilder sb, boolean brackets, Object... array) {
		return iterateArray(sb, brackets, arr(array));
	}

	public static StringBuilder iterateArray(StringBuilder sb, boolean brackets, String... array) {
		if (array != null) {
			if (brackets) {
				sb.append("(");
			}
			ListIterator<String> arrayItr = List.of(array).listIterator();
			while (arrayItr.hasNext()) {
				sb.append(arrayItr.next());
				if (arrayItr.hasNext()) {
					sb.append(", ");
				}
			}
			if (brackets) {
				sb.append(")");
			}
		}
		return sb;
	}

	public static final class Unary {
	}

	public static interface BiFunctionThrows<P1, P2, R> {
		R apply(P1 p1, P2 p2) throws Exception;
	}

	public static class UnexpectedExceptionException extends RuntimeException {

		private static final long serialVersionUID = -8826418715810623243L;

		public UnexpectedExceptionException(Exception e) {
			super(e);
		}
	}
}
