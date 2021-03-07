package org.joox.selector;

import java.util.List;


/**
 * Represents a selector.
 * <p/>
 * A selector has a tag name, a combinator and a list of {@linkplain Specifier specifiers}.
 *
 * @see <a href="http://www.w3.org/TR/css3-selectors/#selector-syntax">Selector syntax description</a>
 *
 * @author Christer Sandberg
 */
class Selector {

    /** The universal tag name (i.e. {@code *}). */
    public static final String UNIVERSAL_TAG = "*";

    /**
     * Combinators
     *
     * @see <a href="http://www.w3.org/TR/css3-selectors/#combinators">Combinators description</a>
     */
    public static enum Combinator {
        DESCENDANT, CHILD, ADJACENT_SIBLING, GENERAL_SIBLING
    };

    /** Tag name. */
    private final String tagName;

    /** Combinator */
    private final Combinator combinator;

    /** A list of {@linkplain Specifier specifiers}. */
    private final List<Specifier> specifiers;

    /**
     * Create a new instance with the tag name set to the value of {@link #UNIVERSAL_TAG},
     * and with the combinator set to {@link Combinator#DESCENDANT}. The list of
     * {@linkplain #specifiers specifiers} will be set to {@code null}.
     */
    public Selector() {
        this.tagName = UNIVERSAL_TAG;
        this.combinator = Combinator.DESCENDANT;
        this.specifiers = null;
    }

    /**
     * Create a new instance with the specified tag name and combinator.
     * <p/>
     * The list of {@linkplain #specifiers specifiers} will be set to {@code null}.
     *
     * @param tagName The tag name to set.
     * @param combinator The combinator to set.
     */
    public Selector(String tagName, Combinator combinator) {
        Assert.notNull(tagName, "tagName is null!");
        Assert.notNull(combinator, "combinator is null!");
        this.tagName = tagName;
        this.combinator = combinator;
        this.specifiers = null;
    }

    /**
     * Create a new instance with the specified tag name and list of specifiers.
     * <p/>
     * The combinator will be set to {@link Combinator#DESCENDANT}.
     *
     * @param tagName The tag name to set.
     * @param specifiers The list of specifiers to set.
     */
    public Selector(String tagName, List<Specifier> specifiers) {
        Assert.notNull(tagName, "tagName is null!");
        this.tagName = tagName;
        this.combinator = Combinator.DESCENDANT;
        this.specifiers = specifiers;
    }

    /**
     * Create a new instance with the specified tag name, combinator and
     * list of specifiers.
     *
     * @param tagName The tag name to set.
     * @param combinator The combinator to set.
     * @param specifiers The list of specifiers to set.
     */
    public Selector(String tagName, Combinator combinator, List<Specifier> specifiers) {
        Assert.notNull(tagName, "tagName is null!");
        Assert.notNull(combinator, "combinator is null!");
        this.tagName = tagName;
        this.combinator = combinator;
        this.specifiers = specifiers;
    }

    /**
     * Get the tag name.
     *
     * @return The tag name.
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Get the combinator.
     *
     * @return The combinator.
     */
    public Combinator getCombinator() {
        return combinator;
    }

    /**
     * Get the list of specifiers.
     *
     * @return The list of specifiers or {@code null}.
     */
    public List<Specifier> getSpecifiers() {
        return specifiers;
    }

    /**
     * Returns whether this selector has any specifiers or not.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean hasSpecifiers() {
        return specifiers != null && !specifiers.isEmpty();
    }

}
