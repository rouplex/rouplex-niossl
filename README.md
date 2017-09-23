# README #
Rouplex-Niossl is a java SPI (service provider interface) for secure (SSL/TLS), selectable, socket channels.
Some of the classes in the java.nio.channels package have been extended by secure counterparts that can be used
side by side, or replace existing instances of the plain implementations. This package contains just the entry
point calls for instantiating such instances, as well as a non-functional, default implementation. For a
concrete implementation of these classes you can take a look at Rouplex-Niossl-Spi, which would be included as
a separate dependency to your applications.

More specifically this library defines SSLSocketChannel class to inherit from SocketChannel,
SSLServerSocketChannel to inherit from ServerSocketChannel and SSLSelector to inherit from SSLSelector. One or
more instances of SSLSocketChannel can be registered with an (or more) instance of SSLSelector to be selected
upon, with the same exact semantics a SocketChannel would expect from registering with a Selector. Further, a
mixture of SocketChannels and SSLSocketChannels can be registered simultaneously with an SSLSelector. The
secure counterparts abide to the same API and semantics defined for plain channels at
https://docs.oracle.com/javase/8/docs/api/java/nio/channels/package-summary.html. This way, the existing
products can be easily updated to provide secure communication and new products can achieve security of data in
transit by using the already proven and excellent patterns for communication such as nio.

This library is implemented in pure java, it is available for jdk 1.6 and later, and has no external dependencies. 
The version of the artifact will imply the version of the JDK to be used with. At this time, the latest version 
available for jdk:1.6 is rouplex-niossl:1.6.3, the latest for jdk:1.7 is rouplex-niossl:1.7.3 and so on.

## Description ##
Most of the modern services and online applications use java.nio.channels as the better and faster alternative for
communication, especially on the server side. Using this library would cut down considerably in complexity and
development time since we have done the heavy lifting and due diligence to make it completely compatible with the 
existing one for the plain channels. As an example, an existing application which is using ServerSocketChannel with 
a Selector for their communications, would need to simply replace two import statements

        import java.nio.channels.Selector;
        import java.nio.channels.ServerSocketChannel;
with

        import org.rouplex.nio.channels.SSLSelector;
        import org.rouplex.nio.channels.SSLServerSocketChannel;

and recompile their source code. Of course they must also include this library in the list of their dependencies and
provide runtime configuration for key_stores and trust_stores but that's it!

## Versioning ##
The version follows the jdk version for which this library is built. So, 1.6.[x] are all versions targeting JDK1.6, the
number x representing the update number.

## Build ##
1. Maven is required to build the project. Please download and install it. Make sure the installation is successful by
typing `mvn -version` in a shell window; the command output should be showing the installation folder.

1. This library extends classes defined in JDK1.6+, so the particular JDK for which you need artifacts for, needs to be
installed. Make sure the installation is successful by typing `java -version`; the command output should show the
version (1.6, 1.7, 1.8 or 1.9).

1. This is a multi jdk project and uses the maven toolchain to provide the installation folders for the used jdks. Edit
or create the ~/.m2/toolchain.xml file to contain the following (replace the jdk installation folders with your own):

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

1. On a shell window, and from the folder containing this README file, type `mvn clean install` and if successful, the
built artifacts will be in the target folders inside each module. If you are interested in building for only one of the
JDKs, then cd to the appropriate folder and perform a 'mvn clean install' from there.

## Test ##
`mvn test`

## Sole Owner ##
andimullaraj@gmail.com