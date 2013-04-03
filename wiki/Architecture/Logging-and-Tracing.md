The 'logging' - Project is created to easy adapt logging and tracing feature to other projects just with a maven dependency.

The Project includes two Aspects, the TracingAspect to trace entering and leaving methods and the ExceptionAspect to log RuntimeException which wasn't catched.

To adapt another project with this to aspects, just put in the projects' `pom.xml` file the following logging dependency and aspectj-maven-plugin.

```xml
<dependency>
	<groupId>de.consistec.doubleganger</groupId>
    <artifactId>logging</artifactId>
	<version>${version}</version>
</dependency>
```

```xml
<plugin>
	<groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <configuration>
    	<showWeaveInfo>true</showWeaveInfo>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <complianceLevel>${java.version}</complianceLevel>
        <aspectLibraries>
        	<aspectLibrary>
            	<groupId>de.consistec.doubleganger</groupId>
                <artifactId>logging</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
        	<goals>
            	<goal>compile</goal>
            </goals>
    	</execution>
    </executions>
</plugin>
```
