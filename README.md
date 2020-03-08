# [Jooq](https://www.jooq.org/) GenerationTool as Java Annotation Processor 

It's a simple Java Annotation Processor wrapper over GenerationTool
[![Download](https://api.bintray.com/packages/xnike/maven/me.xnike.jooq-codegen/images/download.svg)](https://bintray.com/xnike/maven/me.xnike.jooq-codegen/_latestVersion)
___

To use in gradle do following steps:

* Create DDL, for example `sample.ddl`:
```postgres-sql
create table if not exists someschema.pnl_run(
    id bigint not null,
    note varchar(3) not null,

    primary key(id)
);
```

* Create GenerationTool configuration, for example `jooq.xml`:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<configuration xmlns="http://www.jooq.org/xsd/jooq-codegen-3.13.0.xsd">
    <generator>
        <database>
            <name>org.jooq.meta.extensions.ddl.DDLDatabase</name>
            <includes>.*</includes>
            <includeSystemIndexes>true</includeSystemIndexes>
            <properties>
                <property>
                    <key>defaultNameCase</key>
                    <value>lower</value>
                </property>
                <property>
                    <key>scripts</key>
                    <value>*.ddl</value>
                </property>
            </properties>
        </database>
        <generate>
            <daos>true</daos>
            <generatedAnnotation>true</generatedAnnotation>
            <fluentSetters>true</fluentSetters>
            <pojos>true</pojos>
        </generate>
        <target>
            <packageName>some.package</packageName>
            <directory>.</directory>
        </target>
    </generator>
</configuration>
```

* Add gradle task into `build.gradle` and pass 2 options to configure processor:
```groovy
task jooqCodegen(type: JavaCompile, group: 'build') {
    classpath = configurations.compileClasspath
    destinationDir = project.file('src/generated')
    options.compilerArgs = [
            "-proc:only",
            "-AgenerationToolRunner.baseDir=" + projectDir.path + "/src/main/META-INF",
            "-AgenerationToolRunner.configurationFile=" + projectDir.path + "/src/main/META-INF/jooq.xml",
            "-processor", "com.rbm.jooq.GenerationToolRunnerProcessor",
            "-Xmaxerrs", "1",
            "-Xmaxwarns", "1"
    ]
}
```

* Somehow add dependency on created task:
```groovy
compileJava {
    dependsOn {
        [
            jooqCodegen
        ]
    }
}
```