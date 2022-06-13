# Overview

*jOOX* stands for jOOX Object Oriented XML. It is a simple wrapper for the org.w3c.dom package, to allow for fluent XML document creation and manipulation where DOM is required but too verbose. jOOX only wraps the underlying document and can be used to enhance DOM, not as an alternative.

*jOOX*'s fluency is inspired by [jRTF](https://github.com/ullenboom/jrtf), a very nice fluent API for the creation of RTF documents in Java.

*jOOX*'s API itself is inspired by [jQuery](http://jquery.com/), an excellent !JavaScript library for efficient DOM manipulation of HTML and XML.

*jOOX*'s name is inspired by [jOOQ](http://www.jooq.org), a fluent API for SQL building and execution.

## Dependencies

- java.sql
- java.xml
- java.xml.bind

### Download

**For use with Java 9+**

```xml
<dependency>
  <groupId>org.jooq</groupId>
  <artifactId>joox</artifactId>
  <version>1.6.2</version>
</dependency>
```

**For use with Java 6+**

```xml
<dependency>
  <groupId>org.jooq</groupId>
  <artifactId>joox-java-6</artifactId>
  <version>1.6.2</version>
</dependency>
```

## Simple example

```java
// Find the order at index 4 and add an element "paid"
$(document).find("orders").children().eq(4).append("<paid>true</paid>");

// Find those orders that are paid and flag them as "settled"
$(document).find("orders").children().find("paid").after("<settled>true</settled>");

// Add a complex element
$(document).find("orders").append(
  $("order", $("date", "2011-08-14"),
             $("amount", "155"),
             $("paid", "false"),
             $("settled", "false")).attr("id", "13");
```

# Examples

For the following examples, we're going to operate on this XML document modelling a library with books and dvds:

## XML document

```xml
<document>
  <library name="Amazon">
    <books>
      <book id="1">
        <name>1984</name>
        <authors>
          <author>George Orwell</author>
        </authors>
      </book>
      <book id="2">
        <name>Animal Farm</name>
        <authors>
          <author>George Orwell</author>
        </authors>
      </book>
      <book id="3">
        <name>O Alquimista</name>
        <authors>
          <author>Paulo Coelho</author>
        </authors>
      </book>
      <book id="4">
        <name>Brida</name>
        <authors>
          <author>Paulo Coelho</author>
        </authors>
      </book>
    </books>

    <dvds>
      <dvd id="5">
        <name>Once Upon a Time in the West</name>
        <directors>
          <director>Sergio Leone</director>
        </directors>
        <actors>
          <actor>Charles Bronson</actor>
          <actor>Jason Robards</actor>
          <actor>Claudia Cardinale</actor>
        </actors>
      </dvd>
    </dvds>
  </library>
</document>
```

## Java code accessing that document

Like many fluent API's jOOX relies on static methods. Since Java 5 and static imports, using jOOX is very simple. Just import

```java
import static org.joox.JOOX.*;
```

Using the above static import wrapping DOM objects with jOOX is very simple:

## Navigation methods

All navigation methods will return a new wrapper containing references to resulting DOM elements:

```java
// Parse the document from a file
Document document = $(xmlFile).document();

// Wrap the document with the jOOX API
Match x1 = $(document);

// This will get all books (wrapped <book/> DOM Elements)
Match x2 = $(document).find("book");

// This will get all even or odd books
Match x3 = $(document).find("book").filter(even());
Match x4 = $(document).find("book").filter(odd());

// This will get all book ID's
List<String> ids = $(document).find("book").ids();

// This will get all books with ID = 1 or ID = 2
Match x5 = $(document).find("book").filter(ids("1", "2"));

// Or, use css-selector syntax:
Match x6 = $(document).find("book#1, book#2");

// This will use XPath to find books with ID = 1 or ID = 2
Match x7 = $(document).xpath("//book[@id = 1 or @id = 2]");
```

## Manipulation methods

All jOOX manipulations are executed on the underlying DOM document:

```java
// This will add a new book
$(document).find("books").append("<book id=\"5\"><name>Harry Potter</name></book>");

// But so does this
$(document).find("book").filter(ids(5)).after("<book id=\"6\"/>");

// This will remove book ID = 1
$(document).find("book").filter(ids(1)).remove();

// Or this
$(document).find("book").remove(ids(1));
```

## Similar tools

Inspiration might be taken from similar products, such as

 * The original: http://jquery.com

 * A Java-port of jQuery: http://jsoup.org/
 * Another Java-port: http://jodd.org/doc/jerry/index.html
 * A GWT-port of jQuery: http://code.google.com/p/gwtquery/

Unfortunately, all of the above projects focus on HTML, not on arbitrary XML. Besides, jsoup completely rebuilt a proprietary parser / DOM structure, which is incompatible with the org.w3c.dom package. 

jOOX uses css-selectors for parsing css selector expressions:

 * https://github.com/chrsan/css-selectors

Other platform ports:

 * A PHP-port of jQuery: http://fluentdom.github.com/
 * Another PHP-port of jQuery: http://code.google.com/p/phpquery/
