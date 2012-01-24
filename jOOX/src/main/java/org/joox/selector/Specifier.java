package org.joox.selector;

/**
 * This interface represents a specifier. A specifier is either an
 * attribute specifier, a pseudo specifier or a negation specifier.
 *
 * @author Christer Sandberg
 */
interface Specifier {

    /** The specifier type. */
    public static enum Type {
        ATTRIBUTE, PSEUDO, NEGATION
    };

    /**
     * Get the specifier type.
     *
     * @return The specifier type.
     */
    public Type getType();

}
