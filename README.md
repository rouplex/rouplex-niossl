# README #
Rouplex-Niossl is a java library providing SSL counterparts for java.nio.channels classes such as SocketChannel,
ServerSocketChannel, Selector and SelectorProvider. This library is implemented in pure java, it is available for
Jdk1.6 and later, and has no external dependencies. The SSL classes are completely compatible with the base classes
(they simply extend and override their base functionality) and adhere fully to the clear channels documentation at
https://docs.oracle.com/javase/8/docs/api/java/nio/channels/package-summary.html. That way one can use them with the
full confidence, since they don't have to rethink any of their communication stack, just update their import statement
and recompile.

## Description ##
Most of the successful services and online applications use java.nio.channels as the fastest alternative for
communications, especially on the server side. Using this library would cut down complexity and development time for
them, which we know it takes their teams from weeks to months at a time. As an example, an existing application which
is using ServerSocketChannel with a Selector for their communications, would need to simply replace two import
statements

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

1. The path to JDK home will be needed. Please edit (or create if missing) the ~/.m2/settings.xml file and setup the
JAVA_1_6_HOME (or respective) property. Here is an example (edit the element JAVA_1_6_HOME (or respective) to contain
your own JDK1.6 (or respective) installation path):

        <settings>
            <profiles>
                <profile>
                    <id>compiler</id>
                    <properties>
                        <JAVA_1_6_HOME>/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home</JAVA_1_6_HOME>
                        <JAVA_1_8_HOME>/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home</JAVA_1_8_HOME>
                    </properties>
                </profile>
            </profiles>

            <activeProfiles>
                <activeProfile>compiler</activeProfile>
            </activeProfiles>
        </settings>

1. On a shell window, and from the folder containing this README.txt file, type `mvn clean install` and if
successful, the built artifacts will be in the target folders inside each module. If you are
interested in building for only one of the JDKs, then cd to the appropriate folder and perform a
'mvn clean install' from there.

## Test ##
`mvn test`

## Sole Owner ##
andimullaraj@gmail.com