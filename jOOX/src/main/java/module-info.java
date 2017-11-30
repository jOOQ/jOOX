/* [java-9] */
module org.jooq.joox {

    // To support automatic data type conversions for common JDBC types, like Timestamp
    requires java.sql;

    // XML API dependencies
    requires java.xml;
    requires java.xml.bind;
}
/* [/java-9] */
