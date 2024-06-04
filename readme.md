# Create a Criticality Plugin for CAP Java Applications

## Prerequisites

**In case you are using SAP Business Application Studio or SAP Build Code you can skip this section.**

You need to have the following tools installed:

* Java >=17 (internally, we use sapmachine but any other vendor should work, too)
* Maven (some recent 3.x release)
* Your Java IDE of choice. Ideally with CDS Tooling installed. This would be VS Code or IntelliJ Idea Ultimate.

## Requirement

Write a CAP Java plugin that provides a handler detecting CDS enum values annotated with `@criticality.*` and 
sets the integer value according to the [criticality OData vocabulary](https://sap.github.io/odata-vocabularies/vocabularies/UI.html#CriticalityType)
to an `criticality` element of the same entity.

Bonus points: this works for expanded entities, too.

## With Shortcut: Use the Prebuilt Plugin

In case you want to take the shortcut you can change to the `criticality-prebuilt` folder and install it to the plugin to the local Maven repository with `mvn source:jar install`. From here you can continue [extending the base application](#add-the-plugin-to-the-base-application).

## Without Shortcut: Create a New Plain Java Project with Maven

Use the Maven quickstart archetype to generate a plain, empty Java project:

```
mvn archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DartifactId=criticality -DgroupId=com.sap.capire -Dversion=1.0-SNAPSHOT -DinteractiveMode=false
```


## Add Needed Dependencies

Replace the generated pom.xml with the following content. It contains the needed dependencies and the correct Java version:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.sap.capire</groupId>
  <artifactId>criticality</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>criticality</name>
  <url>http://maven.apache.org</url>
  <properties>
      <maven.compiler.target>17</maven.compiler.target>
      <maven.compiler.source>17</maven.compiler.source>
	  <maven.compiler.release>17</maven.compiler.release>
	  <cds.services.version>2.10.0</cds.services.version>
  </properties>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>com.sap.cds</groupId>
        <artifactId>cds-services-bom</artifactId>
        <version>${cds.services.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-autoconfigure</artifactId>
      <version>3.2.6</version>
    </dependency>
    <dependency>
      <groupId>com.sap.cds</groupId>
      <artifactId>cds-services-api</artifactId>
      <version>2.10.0</version>
    </dependency>
  </dependencies>
</project>
```

Perform the initial build with `mvn compile`.

## Spring Boot Auto Configuration for the handler class

Use [Spring Boot auto configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html) to register the handler automatically as soon the dependency is added to the pom.xml of the base application.

## Implement the handler

Write the handler. In contrast to the node.js implementation there is no dedicated registration per relevant entity at startup of the application. The handler needs to run for all services and all entities and detects at runtime if an entity is annotated and needs to be processed accordingly.

Use the following resources as a reference:

* https://cap.cloud.sap/docs/java/event-handlers/ At the core this task is about writing a generic event handler for a CAP Java application. Thus the event handler documentation is the foundation for this. 
* https://cap.cloud.sap/docs/java/reflection-api You use the reflection API to introspect the application's CDS model in order to find relevant entities and elements.
* https://cap.cloud.sap/docs/java/cds-data In the handler, you need to traverse the data in the result and act according to the information you discovered in the CDS model.

## Install the Handler to the Local Maven Repository

Once the plugin is ready it can be consumed in the base application. In order to consume the new plugin from e.g. the Incidents App you need to install it to the local Maven repo. The `source:jar` goal adds the source code to the jar as well. You might need it for debugging. 😈

```
mvn source:jar install
```

## Add the Plugin to the Base Application 

The plugin is now ready to use. The base application's model can be extended and the plugin needs to be added to the list of dependencies. Again, there is a shortcut: just checkout the branch `with-criticality` of this repo, install the `criticality-prebuilt` module and start the base application in `incidents-app-java` with `mvn spring-boot:run`.

### Adjust the Model of the Base Application

Change to the incidents-app-java folder:

Create a `criticality.cds file` in the `db` module and paste the following content: 

```cds
using {sap.capire.incidents as my} from './schema';

annotate my.Status with {
    code           @criticality {
        new        @criticality.Neutral;
        assigned   @criticality.Critical;
        in_process @criticality.Critical;
        on_hold    @criticality.Negative;
        resolved   @criticality.Positive;
        closed     @criticality.Positive;
    };
};

extend my.Urgency {
    criticality : Integer;
};

annotate my.Urgency with {
    code       @criticality {
        high   @criticality.Negative;
        medium @criticality.Critical;
    };
};
```

In order to have Criticality properly displayed in the Fiori Elements UI you also need to add the following CDS line to the `app/incidents/annotations.cds` below line 35:

```
            Criticality : urgency.criticality,
```

Add the dependency of the just created plugin to your `srv/pom.xml`:

```xml
<dependency>
    <groupId>com.sap.capire</groupId>
    <artifactId>criticality</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Start the base application with `mvn spring-boot:run`.

Finally, execute some HTTP requests.

Directly get the Urgency entities:
```http
GET http://localhost:8080/odata/v4/ProcessorService/Urgency
Authorization: basic YWxpY2U6
```

Get the Incidents entities with expanded `urgency` association.
```http
GET http://localhost:8080/odata/v4/ProcessorService/Incidents?$expand=urgency
Authorization: basic YWxpY2U6
```

The results should now include an integer value for some Urgency elements (according to the annotations you made). You can also see the Incidents coloured and annotated with little symbols on the UI now: http://localhost:8080/incidents/webapp/index.html

[How captastic was your experience?](https://forms.office.com/Pages/ResponsePage.aspx?id=bGf3QlX0PEKC9twtmXka914n6hNKFVlPml6fyiE6QrxUMUJKS1hLWUxENElFR0dCVUhFVzlEMTFPRC4u)
