package org.joox.selector;



/**
 * An implementation of {@link Specifier} for attributes.
 * <p/>
 * Note:
 * <br/>
 * This implementation will also be used for ID selectors and class selectors.
 *
 * @see <a href="http://www.w3.org/TR/css3-selectors/#attribute-selectors">Attribute selectors</a>
 *
 * @author Christer Sandberg
 */
class AttributeSpecifier implements Specifier {

    /** The type of match to perform for the attribute. */
    public static enum Match {
        EXACT, LIST, HYPHEN, PREFIX, SUFFIX, CONTAINS
    };

    /** The name of the attribute. */
    private final String name;

    /** The attribute value. */
    private final String value;

    /** The type of match to perform for the attribute. */
    private final Match match;

    /**
     * Create a new attribute specifier with the specified attribute name.
     * <p/>
     * This attribute specifier is used to check if the attribute with the
     * specified name exists whatever the value of the attribute.
     *
     * @param name The name of the attribute.
     */
    public AttributeSpecifier(String name) {
        Assert.notNull(name, "name is null!");
        this.name = name;
        this.value = null;
        this.match = null;
    }

    /**
     * Create a new attribute specifier with the specified name, value and match type.
     *
     * @param name The name of the attribute.
     * @param value The attribute value.
     * @param match The type of match to perform for the attribute.
     */
    public AttributeSpecifier(String name, String value, Match match) {
        Assert.notNull(name, "name is null!");
        Assert.notNull(value, "value is null!");
        Assert.notNull(match, "match is null!");
        this.name = name;
        this.value = value;
        this.match = match;
    }

    /**
     * Get the name of the attribute.
     *
     * @return The name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the attribute value.
     *
     * @return The attribute value or {@code null}.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the type of match to perform for the attribute.
     *
     * @return The type of match or {@code null}.
     */
    public Match getMatch() {
        return match;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.ATTRIBUTE;
    }

}
