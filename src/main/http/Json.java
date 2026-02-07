package main.http;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class Json {

    private Json() {
    }

    static Object parse(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Body cannot be null");
        }
        Parser parser = new Parser(text);
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isEnd()) {
            throw new IllegalArgumentException("Unexpected trailing content in JSON");
        }
        return value;
    }

    static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        appendValue(sb, value);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void appendValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append('"').append(escape((String) value)).append('"');
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(String.valueOf(value));
        } else if (value instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                sb.append('"').append(escape(entry.getKey())).append('"').append(':');
                appendValue(sb, entry.getValue());
            }
            sb.append('}');
        } else if (value instanceof List) {
            sb.append('[');
            boolean first = true;
            for (Object item : (List<Object>) value) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                appendValue(sb, item);
            }
            sb.append(']');
        } else {
            throw new IllegalArgumentException("Unsupported JSON value: " + value.getClass());
        }
    }

    private static String escape(String value) {
        StringBuilder sb = new StringBuilder();
        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (ch < 0x20) {
                        sb.append(String.format("\\u%04x", (int) ch));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    private static final class Parser {
        private final String text;
        private int index;

        Parser(String text) {
            this.text = text;
            this.index = 0;
        }

        Object parseValue() {
            skipWhitespace();
            if (isEnd()) {
                throw new IllegalArgumentException("Unexpected end of JSON input");
            }
            char ch = current();
            switch (ch) {
                case '"':
                    return parseString();
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case 't':
                case 'f':
                    return parseBoolean();
                case 'n':
                    return parseNull();
                default:
                    if (ch == '-' || Character.isDigit(ch)) {
                        return parseNumber();
                    }
                    throw new IllegalArgumentException("Unexpected character '" + ch + "' at position " + index);
            }
        }

        Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> result = new LinkedHashMap<>();
            skipWhitespace();
            if (peek('}')) {
                expect('}');
                return result;
            }
            do {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                result.put(key, value);
                skipWhitespace();
            } while (consume(','));
            expect('}');
            return result;
        }

        List<Object> parseArray() {
            expect('[');
            List<Object> result = new ArrayList<>();
            skipWhitespace();
            if (peek(']')) {
                expect(']');
                return result;
            }
            do {
                Object value = parseValue();
                result.add(value);
                skipWhitespace();
            } while (consume(','));
            expect(']');
            return result;
        }

        String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (!isEnd()) {
                char ch = current();
                index++;
                if (ch == '"') {
                    return sb.toString();
                }
                if (ch == '\\') {
                    if (isEnd()) {
                        throw new IllegalArgumentException("Unterminated escape sequence");
                    }
                    char esc = current();
                    index++;
                    switch (esc) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            sb.append(parseUnicode());
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported escape sequence \\" + esc);
                    }
                } else {
                    sb.append(ch);
                }
            }
            throw new IllegalArgumentException("Unterminated string literal");
        }

        Boolean parseBoolean() {
            if (match("true")) {
                return Boolean.TRUE;
            }
            if (match("false")) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Invalid boolean value at position " + index);
        }

        Object parseNull() {
            if (match("null")) {
                return null;
            }
            throw new IllegalArgumentException("Invalid null value at position " + index);
        }

        Number parseNumber() {
            int start = index;
            if (peek('-')) {
                index++;
            }
            if (peek('0')) {
                index++;
            } else if (Character.isDigit(current())) {
                while (!isEnd() && Character.isDigit(current())) {
                    index++;
                }
            } else {
                throw new IllegalArgumentException("Invalid number at position " + index);
            }
            if (!isEnd() && current() == '.') {
                index++;
                if (isEnd() || !Character.isDigit(current())) {
                    throw new IllegalArgumentException("Invalid number at position " + index);
                }
                while (!isEnd() && Character.isDigit(current())) {
                    index++;
                }
            }
            if (!isEnd() && (current() == 'e' || current() == 'E')) {
                index++;
                if (!isEnd() && (current() == '+' || current() == '-')) {
                    index++;
                }
                if (isEnd() || !Character.isDigit(current())) {
                    throw new IllegalArgumentException("Invalid exponent at position " + index);
                }
                while (!isEnd() && Character.isDigit(current())) {
                    index++;
                }
            }
            String number = text.substring(start, index);
            if (number.indexOf('.') >= 0 || number.indexOf('e') >= 0 || number.indexOf('E') >= 0) {
                return Double.parseDouble(number);
            }
            return Long.parseLong(number);
        }

        char parseUnicode() {
            if (index + 4 > text.length()) {
                throw new IllegalArgumentException("Invalid unicode escape sequence");
            }
            String hex = text.substring(index, index + 4);
            index += 4;
            try {
                return (char) Integer.parseInt(hex, 16);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid unicode escape sequence \\u" + hex);
            }
        }

        void skipWhitespace() {
            while (!isEnd() && Character.isWhitespace(current())) {
                index++;
            }
        }

        boolean consume(char expected) {
            skipWhitespace();
            if (!isEnd() && current() == expected) {
                index++;
                return true;
            }
            return false;
        }

        void expect(char expected) {
            skipWhitespace();
            if (isEnd() || current() != expected) {
                throw new IllegalArgumentException("Expected '" + expected + "' at position " + index);
            }
            index++;
        }

        boolean match(String keyword) {
            if (text.regionMatches(index, keyword, 0, keyword.length())) {
                index += keyword.length();
                return true;
            }
            return false;
        }

        boolean peek(char ch) {
            skipWhitespace();
            return !isEnd() && current() == ch;
        }

        char current() {
            return text.charAt(index);
        }

        boolean isEnd() {
            return index >= text.length();
        }
    }
}
