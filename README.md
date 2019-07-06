# Function Metrics
This simple library provides a way to annotate your functions and get success, failure and call metrics for them.

# Requirements

* _JDK -_ Oracle/Sun JDK 1.8+/OpenJDK 8+
* _Dropwizard Metrics (Formerly called Yammer Metrics) -_ 3.2.2+
* _Guava -_ 21.0+

## Usage
To use this library the following need to be added to your Maven pom file.

### Dependency
Put this into the `dependencies` section of your pom file:

```
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>1.8.13</version>
    </dependency>
    <dependency>
        <groupId>io.appform.functionmetrics</groupId>
        <artifactId>function-metrics</artifactId>
        <version>1.0.2</version>
    </dependency>
```

If you are not using guava and metrics already, then add the following as well:
```
    <dependency>
        <groupId>io.dropwizard.metrics</groupId>
        <artifactId>metrics-core</artifactId>
        <version>3.2.2</version>
    </dependency>
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>21.0</version>
    </dependency>
```

### Build plugin
This library uses an aspect to introspect and instrument your code during compile time to inject metrics collection code.
Therefore, configuration needs to be put into your pom file in the `build/plugins` section to enabje aspectj weaving.

```
                <plugin>
                    <groupId>org.codehaus.mojo</groupId>
                    <artifactId>aspectj-maven-plugin</artifactId>
                    <version>1.11</version>
                    <dependencies>
                        <dependency>
                            <groupId>org.aspectj</groupId>
                            <artifactId>aspectjrt</artifactId>
                            <version>1.8.13</version>
                        </dependency>
                        <dependency>
                            <groupId>org.aspectj</groupId>
                            <artifactId>aspectjtools</artifactId>
                            <version>1.8.13</version>
                        </dependency>
                    </dependencies>

                    <configuration>
                        <complianceLevel>1.8</complianceLevel>
                        <source>1.8</source>
                        <target>1.8</target>
                        <showWeaveInfo>true</showWeaveInfo>
                        <forceAjcCompile>true</forceAjcCompile>
                        <sources/>
                        <weaveDirectories>
                            <weaveDirectory>${project.build.directory}/classes</weaveDirectory>
                        </weaveDirectories>
                        <verbose>true</verbose>
                        <Xlint>ignore</Xlint>
                        <aspectLibraries>
                            <aspectLibrary>
                                <groupId>io.appform.functionmetrics</groupId>
                                <artifactId>function-metrics</artifactId>
                            </aspectLibrary>
                        </aspectLibraries>
                    </configuration>
                    <executions>
                        <execution>
                            <phase>process-classes</phase>
                            <goals>
                                <goal>compile</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
```

_NOTE: This config uses in-place weaving of the compiled classes.
Weaving can also be done during compilation, however in this mode libraries like Lombok etc will stop working._

### Code

#### Initializing the metrics collection system
The following information must be provided for the system to initialize.
* _**Prefix of the metrics:**_ This will be prepended to the metrics being generated.
* _**Registry:**_ The metrics registry to which the metrics will be pushed.

```
        FunctionMetricsManager.initialize(<prefix>, <metricsRegistry>);
```

For example

```
    FunctionMetricsManager.initialize("test", SharedMetricRegistries.getOrCreate("test-metrics"));
```

#### Preparing a function for metric collection

This is simple. Just annotate the method with `@MonitoredFucntion`.

For example:
```
    @MonitoredFunction
    private void myFunction(int val) {
        //Demo function
    }
```

##### Overriding default class and method names
You can override the name of the class/method that will be populated in the metric.
```
    @MonitoredFunction(className="Blah", method="randomFunction")
    private void myFunction(int val) {
        //Demo function
    }
```
_**NOTE:** This is handy in case you want to differentiate between overloaded methods. _


## What metrics will get pushed

Let's assume the following:
* **Metric prefix:** _test_
* **Function being tested:** _MyClass.myFunction()_

### Metrics published

#### Overall call rates and timings
* \<prefix>.\<className>.\<methodName>.all
    * mean rate
    * 1-minute rate
    * 5-minute rate
    * 15-minute rate
    * min
    * max
    * mean
    * stddev
    * median
    * 75%
    * 95%
    * 98%
    * 99%
    * 99.9%
#### Failure rates and timings of failed calls

_**NOTE:** We consider it to be a failure if the method throws an exception_

* \<prefix>.\<className>.\<methodName>.failure
    * mean rate
    * 1-minute rate
    * 5-minute rate
    * 15-minute rate
    * min
    * max
    * mean
    * stddev
    * median
    * 75%
    * 95%
    * 98%
    * 99%
    * 99.9%

#### Successful call rates and timings
* \<prefix>.\<className>.\<methodName>.success
    *  mean rate
    *  1-minute rate
    *  5-minute rate
    *  15-minute rate
    *  min
    *  max
    *  mean
    *  stddev
    *  median
    *  75%
    *  95%
    *  98%
    *  99%
    *  99.9%

### Sample Metrics

```text
test.MyClass.myFunction.all
             count = 3
         mean rate = 1.00 calls/second
     1-minute rate = 0.00 calls/second
     5-minute rate = 0.00 calls/second
    15-minute rate = 0.00 calls/second
               min = 0.00 milliseconds
               max = 0.00 milliseconds
              mean = 0.00 milliseconds
            stddev = 0.00 milliseconds
            median = 0.00 milliseconds
              75% <= 0.00 milliseconds
              95% <= 0.00 milliseconds
              98% <= 0.00 milliseconds
              99% <= 0.00 milliseconds
            99.9% <= 0.00 milliseconds
test.MyClass.myFunction.failure
             count = 1
         mean rate = 0.33 calls/second
     1-minute rate = 0.00 calls/second
     5-minute rate = 0.00 calls/second
    15-minute rate = 0.00 calls/second
               min = 0.00 milliseconds
               max = 0.00 milliseconds
              mean = 0.00 milliseconds
            stddev = 0.00 milliseconds
            median = 0.00 milliseconds
              75% <= 0.00 milliseconds
              95% <= 0.00 milliseconds
              98% <= 0.00 milliseconds
              99% <= 0.00 milliseconds
            99.9% <= 0.00 milliseconds
test.MyClass.myFunction.success
             count = 2
         mean rate = 0.65 calls/second
     1-minute rate = 0.00 calls/second
     5-minute rate = 0.00 calls/second
    15-minute rate = 0.00 calls/second
               min = 0.00 milliseconds
               max = 0.00 milliseconds
              mean = 0.00 milliseconds
            stddev = 0.00 milliseconds
            median = 0.00 milliseconds
              75% <= 0.00 milliseconds
              95% <= 0.00 milliseconds
              98% <= 0.00 milliseconds
              99% <= 0.00 milliseconds
            99.9% <= 0.00 milliseconds
```
_**NOTE:** This is output from Dropwizard metrics console reporter._

## License
Apache 2

## Version
1.0.1
