# README #
Rouplex-Niossl is a java SPI (service provider interface) library for secure (SSL/TLS) and selectable socket channels. 
The classes defined in this library inherit from classes in the java.nio.channels and in principle provide the static 
methods for instantiating secured counterparts of the java.nio.channels package. A few examples:

* The SSLSelector class inherits from Selector and defines

    `public static SSLSelector SSLSelector.open() throws IOException { ... }`

* The SSLSocketChannel class inherits from SocketChannel and defines

    `public static SSLSocketChannel open() throws IOException { ... }`

* The SSLServerSocketChannel class inherits from ServerSocketChannel and defines

    `public static SSLServerSocketChannel open() throws IOException { ... }`

These classes, by design:
* do not add any new instance methods to the base ones
* adhere to the same exact semantics and behaviour to the base ones, including lock acquiring order, as documented in 
java.nio.channels package (*) here https://docs.oracle.com/javase/8/docs/api/java/nio/channels/package-summary.html
* are declared as abstract in this package, and can be implemented in various ways by various service providers

The last class in this library is SSLSelectorProvider, which provides the means for locating and dynamically loading 
an instance of itself. If a provider is found in runtime, it will be returned. Otherwise, a noop instance will be 
returned, calls to which will fail with "not implemented" exception. A SSLSelectorProvider implementation is available
 from Rouplex-Niossl-Spi, available as a separate library (only necessary in runtime, and not in compile time).

One or more instances of SSLSocketChannel can be registered with one (or more, but normally one) instance of SSLSelector
to be selected upon, with the same exact semantics a SocketChannel would expect from registering with a Selector.
Further, a mixture of SocketChannels and SSLSocketChannels can be registered simultaneously with an SSLSelector. This
way, the existing products can be easily updated to provide secure communication and new products can achieve security
of data in transit by using the already proven and excellent patterns for communication such as nio.

This library is implemented in pure java, it is available for jdk 1.6 and later, and has no external dependencies. 
The version of the artifact will imply the version of the JDK to be used with. At this time, the latest version 
available for jdk:1.6 is rouplex-niossl:1.6.4, the latest for jdk:1.7 is rouplex-niossl:1.7.4 and so on.

(*) The only difference in behaviour between the SSLSocketChannel and SocketChannel is when shutdownOutput is called.
In the case of a SocketChannel, the input can still be readable, whereas in the case of the SSLSocketChannel, the input
might be closed from remote peer in response to our own shutdown (in ssl layer). This is a constrain coming from SSL
specification itself, and no implementation can get around it (hence documented in this jar, representing the interface)
## Description ##
Most of the distributed services use SocketChannels and Selectors as the faster and scalable alternative to the classic
socket communication, especially on the server side. If such a service would need to be upgraded to use SSL
communication, this library would come very handy: just replace the SocketChannel/Selector with the secure counterparts
SSLSocketChannel/SSLSelector, provide the SSLContext for configuration of the various aspects of the security, and you
are done.

Using this library you cut down considerably in complexity and development time since we have done the heavy lifting
and the due diligence to make it completely compatible with the existing one for the plain channels. As an example, an
existing application which is using ServerSocketChannel with a Selector for their communications, would be turned into
a secure one, by simply replacing:

    Selector selector = Selector.open();
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.register(selector, SelectionKey.OP_READ);
    
    while(true) {
        selector.select();
        for (SocketChannel sc : selector.selectedKeys() {
            socketChannel.read(...)
        }
    }

with:

    Selector selector = SSLSelector.open(); // notice the SSL
    SocketChannel socketChannel = SSLSocketChannel.open();  // notice the SSL
    socketChannel.register(selector, SelectionKey.OP_READ);
    
    while(true) {
        selector.select();
        for (SocketChannel sc : selector.selectedKeys() {
            socketChannel.read(...)
        }
    }

Or, the same application would be able to handle plain and secure communications, side by side by using this:

    Selector selector = SSLSelector.open(); // notice the SSL
    SocketChannel sslSocketChannel = SSLSocketChannel.open();   // notice the SSL
    sslSocketChannel.register(selector, SelectionKey.OP_READ);
    
    SocketChannel socketChannel = SocketChannel.open(); // notice no SSL
    socketChannel.register(selector, SelectionKey.OP_READ);
    
    while(true) {
        selector.select();
        for (SocketChannel sc : selector.selectedKeys() {
            socketChannel.read(...)
        }
    }

and recompile their source code. Of course they must also include this library in the list of their dependencies and
provide runtime configuration for key_stores and trust_stores but that's it!

## Versioning ##
The version follows the jdk version for which this library is built. So, 1.6.[x] are all versions targeting JDK1.6, the
number x representing the update number for rouplex-niossl.

## Build ##
You would only need to build this project if you want to change it for your own needs or planning to contribute. Otherwise
the related artifact (jar file) is available in maven central repository.

1. Maven is required to build the project. Please download and install it. Make sure the installation is successful by
typing `mvn -version` in a shell window; the command output should be showing the installation folder.

1. This library extends classes defined in JDK1.6+, so the particular JDK for which you need artifacts for, needs to be
installed. Make sure the installation is successful by typing `java -version`; the command output should show the
version (1.6, 1.7, 1.8 ...).

1. This is a multi jdk project and uses the maven toolchain to provide the installation folders for the used jdks. Edit
or create the ~/.m2/toolchain.xml file to contain the following (replace the jdk installation folders with your own):

```
    <toolchains>
        <toolchain>
            <type>jdk</type>
            <provides>
                <version>1.6</version>
                <vendor>sun</vendor>
            </provides>
            <configuration>
                <jdkHome>/Library/Java/JavaVirtualMachines/1.6.0_65-b14-462.jdk/Contents/Home</jdkHome>
            </configuration>
        </toolchain>
        ...
        <toolchain>
            <type>jdk</type>
            <provides>
                <version>1.8</version>
                <vendor>sun</vendor>
            </provides>
            <configuration>
                <jdkHome>/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home</jdkHome>
            </configuration>
        </toolchain>
        ...
    </toolchains>
```

1. On a shell window, and from the folder containing this README file, type `mvn clean install` and if successful, the
built artifacts will be in the target folders inside each module. If you are interested in building for only one of the
JDKs, then cd to the appropriate folder and perform a 'mvn clean install' from there.

## Test ##
`mvn test`

## Usage ##
Assuming you are using maven (other build systems can be inferred easily), in your pom.xml:
Include this dependency:
```
  <dependency>
    <groupId>org.rouplex</groupId>
    <artifactId>rouplex-niossl</artifactId>
    <version>1.6.4</version>
  </dependency>
```
if you are using jdk 1.6.

Include this dependency:
```
  <dependency>
    <groupId>org.rouplex</groupId>
    <artifactId>rouplex-niossl</artifactId>
    <version>1.7.4</version>
  </dependency>
```
for any jdk 1.7 or higher.

If you really are into squeezing an extra 0.01% of CPU then align the jdk version with this library version.

Include this dependency:
```
  <dependency>
    <groupId>org.rouplex</groupId>
    <artifactId>rouplex-niossl</artifactId>
    <version>1.<x>.4</version>
  </dependency>
```
for any jdk 1.x or higher.

## Sole Owner ##
andimullaraj@gmail.com