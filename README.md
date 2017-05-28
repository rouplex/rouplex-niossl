# README #
Rouplex-Niossl is a java library providing SSL counterparts for classic java.nio.channels classes such as SocketChannel,
ServerSocketChannel, Selector and SelectorProvider. This library is implemented in pure java, it is available for
Jdk1.6 and later, and has no external dependencies. The SSL classes are completely compatible with the base classes
(they simply extend and override their base functionality) and adhere fully to the channels documentation available at
https://docs.oracle.com/javase/8/docs/api/java/nio/channels/package-summary.html. This way, the existing products can
be easily updated to provide secure communication and new products can achieve security of data in transit by using the
already proven patterns for communication.

## Description ##
Most of the modern services and online applications use java.nio.channels as the better and faster alternative for
communication, especially on the server side. Using this library would cut down considerably in complexity and
development time since we have done the due diligence to make it completely compatible with the existing one for the
plain channels. As an example, an existing application which is using ServerSocketChannel with a Selector for their
communications, would need to simply replace two import statements

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