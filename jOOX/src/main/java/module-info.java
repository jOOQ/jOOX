/* [java-9] */
module org.jooq.joox {

    // To support automatic data type conversions for common JDBC types, like Timestamp
    requires java.sql;

    // XML API dependencies
    requires java.xml;
    requires jakarta.xml.bind;

    exports org.joox;

    // Required for JAXB tests
    // See also https://github.com/javaee/jaxb-v2/issues/1184
    opens org.joox to jakarta.xml.bind;
}
/* [/java-9] */
