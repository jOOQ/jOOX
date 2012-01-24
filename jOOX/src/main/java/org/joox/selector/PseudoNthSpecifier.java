package org.joox.selector;



/**
 * An implementation of {@link Specifier} for {@code nth-*} pseudo-classes.
 *
 * @see <a href="http://www.w3.org/TR/css3-selectors/#pseudo-classes">Pseudo-classes</a>
 *
 * @author Christer Sandberg
 */
class PseudoNthSpecifier implements Specifier {

    /** The {@code nth-*} pseudo-class value (i.e. {@code nth-child} etc). */
    private final String value;

    /** The {@code nth-*} pseudo-class argument (i.e. {@code 2n+1} etc). */
    private final String argument;

    /** The parsed <em>a</em> value. */
    private int a = 0;

    /** The parsed <em>b</em> value. */
    private int b = 0;

    /**
     * Create a new {@code nth-*} pseudo-class instance with the
     * specified value and argument.
     *
     * @param value The {@code nth-*} pseudo-class value (i.e. {@code nth-child} etc).
     * @param argument The {@code nth-*} pseudo-class argument (i.e. {@code odd} etc).
     */
    public PseudoNthSpecifier(String value, String argument) {
        Assert.notNull(value, "value is null!");
        Assert.notNull(argument, "argument is null!");
        this.value = value;
        this.argument = argument;
        parseNth();
    }

    /**
     * Get the {@code nth-*} pseudo-class argument.
     *
     * @return The argument.
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Get the {@code nth-*} pseudo-class value.
     *
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.PSEUDO;
    }

    /**
     * Check if the <em>node</em> count matches this specifier.
     *
     * @param count The <em>node</em> count.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isMatch(int count) {
        if (a == 0) {
            return count == b;
        } else if (a > 0) {
            if (count < b) {
                return false;
            }

            return (count - b) % a == 0;
        } else {
            if (count > b) {
                return false;
            }

            return (b - count) % (-a) == 0;
        }
    }

    /**
     * Parse the {@code nth-*} pseudo-class argument.
     */
    private void parseNth() {
        // Remove all '+' and spaces.
        String str = argument.replaceAll("\\+|\\s+", "");
        if ("odd".equals(str)) {
            a = 2;
            b = 1;
        } else if ("even".equals(str)) {
            a = 2;
        } else {
            int n = str.indexOf('n');
            if (n != -1) {
                if (n == 0) {
                    a = 1;
                } else if (n == 1 && str.charAt(0) == '-') {
                    a = -1;
                } else {
                    a = Integer.parseInt(str.substring(0, n));
                }

                if ((n + 1) != str.length()) {
                    b = Integer.parseInt(str.substring(n + 1, str.length()));
                }
            } else {
                b = Integer.parseInt(str);
            }
        }
    }

}
