package org.joox.selector;

/**
 * Assertion utility methods.
 *
 * @author Christer Sandberg
 */
final class Assert {

    /**
     * Private CTOR.
     */
    private Assert() {
    }

    /**
     * Check if the specified {@code expression} is {@code true}. If not throw an
     * {@link IllegalArgumentException} with the specified {@code message}.
     *
     * @param expression The expression to check.
     * @param message The exception message if the {@code expression} is {@code false}.
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check if the specified {@code object} is {@code null}, and throw an
     * {@link IllegalArgumentException} if it is.
     *
     * @param object The object to check.
     * @param message The exception message if the {@code object} is {@code null}.
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
