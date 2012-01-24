package org.joox.selector;



/**
 * An implementation of {@link Specifier} for the negation pseudo-class.
 *
 * @see <a href="http://www.w3.org/TR/css3-selectors/#negation">The negation pseudo-class</a>
 *
 * @author Christer Sandberg
 */
class NegationSpecifier implements Specifier {

    /** The negation {@linkplain Selector selector}. */
    private final Selector selector;

    /**
     * Create a new negation specifier with the specified negation selector.
     *
     * @param selector The negation {@linkplain Selector selector}.
     */
    public NegationSpecifier(Selector selector) {
        Assert.notNull(selector, "selector is null!");
        this.selector = selector;
    }

    /**
     * Get the negation selector.
     *
     * @return The negation selector.
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return Type.NEGATION;
    }

}
