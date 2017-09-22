package org.rouplex.nio.channels.spi;

import org.rouplex.nio.channels.SSLSelector;
import org.rouplex.nio.channels.SSLServerSocketChannel;
import org.rouplex.nio.channels.SSLSocketChannel;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;

/**
 * The base SSLSelectorProvider, containing code for loading an eventual implementation of the same.
 * If no implementations are found, then an instance of this class is returned, calls to which will fail with "not
 * implemented" {@link IOException}s.
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public class SSLSelectorProvider extends SelectorProvider {
    private static SSLSelectorProvider sslSelectorProvider = null;

    /**
     * If the provider has been loaded prior to this call, then its reference will be returned.
     *
     * Otherwise, the system property "org.rouplex.nio.channels.spi.SSLSelectorProvider" will be checked for a fully
     * qualified class name representing an implementation of {@link SSLSelectorProvider}. If the class pointed to, is
     * loaded successfully then an instance of it will be created, cached for future use, and returned.
     *
     * If for any reason, an implementation is still not found, then the {@link ServiceLoader} class will be used to
     * look up for a service instance implementing a {@link SSLSelectorProvider}. If one is found, it will be cached
     * for future use, and returned.
     *
     * Lastly, if an implementation is still not found, then an instance of this same class will be cached for future
     * use and returned. Calls to this implementation will fail with "not implemented" {@link IOException}s though.
     *
     * @return the loaded provider, never null
     */
    public synchronized static SSLSelectorProvider provider() {
        if (sslSelectorProvider != null) {
            return sslSelectorProvider;
        }

        return AccessController.doPrivileged(new PrivilegedAction<SSLSelectorProvider>() {
            public SSLSelectorProvider run() {
                SSLSelectorProvider provider = loadProvider(SSLSelectorProvider.class.getClassLoader());

                synchronized (SSLSelectorProvider.class) {
                    return sslSelectorProvider = provider != null ? provider : new SSLSelectorProvider();
                }
            }
        });
    }

    /**
     * Try loading the provider by first trying a ClassLoader then ServiceLoader.
     *
     * @return A reference to the loaded provider if it was loaded, false otherwise
     */
    private static SSLSelectorProvider loadProvider(ClassLoader classLoader) {
        String fqcn = System.getProperty("org.rouplex.nio.channels.spi.SSLSelectorProvider");
        if (fqcn != null) {
            try {
                return (SSLSelectorProvider) Class.forName(fqcn, true, classLoader).newInstance();
            } catch (ClassNotFoundException x) {
                throw new ServiceConfigurationError(null, x);
            } catch (IllegalAccessException x) {
                throw new ServiceConfigurationError(null, x);
            } catch (InstantiationException x) {
                throw new ServiceConfigurationError(null, x);
            } catch (SecurityException x) {
                throw new ServiceConfigurationError(null, x);
            }
        }

        ServiceLoader<SSLSelectorProvider> serviceLoader = ServiceLoader.load(SSLSelectorProvider.class, classLoader);
        Iterator<SSLSelectorProvider> sslSelectorProviders = serviceLoader.iterator();

        while (sslSelectorProviders.hasNext()) {
            try {
                return sslSelectorProviders.next();
            } catch (ServiceConfigurationError sce) {
                if (sce.getCause() instanceof SecurityException) {
                    // Ignore the security exception, try the next provider
                    continue;
                }

                throw sce;
            }
        }

        return null;
    }

    /**
     * This method is not implemented/available and it is not expected to be implemented by any provider.
     *
     * @return The created DatagramChannel instance
     * @throws IOException
     *         It will always throw "Not implemented/available"
     */
    @Override
    public DatagramChannel openDatagramChannel() throws IOException {
        throw new IOException("Not implemented/available");
    }

    /**
     * This method is not implemented/available and it is not expected to be implemented by any provider.
     *
     * @return The created Pipe instance
     * @throws IOException
     *          It will always throw "Not implemented/available"
     */
    @Override
    public Pipe openPipe() throws IOException {
        throw new IOException("Not implemented/available");
    }

    /**
     * Create an {@link SSLSelector} which can be used to select on {@link SSLSocketChannel},
     * {@link SSLServerSocketChannel}, {@link SocketChannel}, {@link ServerSocketChannel}
     *
     * @return The created SSLSelector
     * @throws IOException
     *         If the provider is the default no-op skeleton provider, or any other problem trying to instantiate the
     *         SSLSelector.
     */
    @Override
    public SSLSelector openSelector() throws IOException {
        throw new IOException("This provider does not implement openSelector. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    /**
     * Create an {@link SSLServerSocketChannel} instance using {@link SSLContext#getDefault()} and the default
     * {@link ExecutorService} of the {@link SSLSelectorProvider}
     *
     * @return The newly created instance of {@link SSLServerSocketChannel}
     * @throws IOException
     *         If the provider is the default no-op skeleton provider, or any other problem trying to instantiate the
     *         SSLServerSocketChannel.
     */
    @Override
    public SSLServerSocketChannel openServerSocketChannel() throws IOException {
        return openServerSocketChannel(null, null);
    }

    /**
     * Create an {@link SSLServerSocketChannel} instance by using an optional {@link SSLContext} for custom security
     * needs, an optional {@link ExecutorService} for managing new connections, and an optional {@link ExecutorService}
     * to be used by the {@link SSLSocketChannel}s accepted/created.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If null, then the default {@link SSLContext}, containing JRE's defaults, and obtainable internally via
     *         {@link SSLContext#getDefault()} will be used.
     * @param executorService
     *         Used to execute long blocking operations of sslEngine as well as occasional flush outs. If left null,
     *         which is recommended, the default executorService internal to {@link SSLSelectorProvider} will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         Since the tasksExecutorService is not owned, it will not be shutdown when the channel is closed.
     * @return The newly created instance of {@link SSLServerSocketChannel}
     * @throws IOException
     *         If the provider is the default no-op skeleton provider, or any other problem trying to instantiate the
     *         SSLServerSocketChannel.
     */
    public SSLServerSocketChannel openServerSocketChannel(
        SSLContext sslContext, ExecutorService executorService) throws IOException {

        throw new IOException("This provider does not implement openServerSocketChannel. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    /**
     * Create an {@link SSLSocketChannel} instance using {@link SSLContext#getDefault()} and the default
     *          {@link ExecutorService} of the {@link SSLSelectorProvider}
     *
     * @return The newly created instance of {@link SSLSocketChannel}
     * @throws IOException
     *         If the provider is the default no-op skeleton provider, or any other problem trying to instantiate the
     *         SSLSocketChannel.
     */
    @Override
    public SSLSocketChannel openSocketChannel() throws IOException {
        return openSocketChannel(null, null, 0, false, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} instance by using an optional {@link SSLContext} for custom security
     * needs, an optional {@link ExecutorService} for managing ssl handshakes, and an optional {@link SocketChannel}
     * to be used as underlying communication medium for the secure communication.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If null, then the default {@link SSLContext}, containing JRE's defaults, and obtainable internally via
     *         {@link SSLContext#getDefault()} will be used.
     * @param peerHost
     *         The name of the remote host this channel will be connecting to, if the cipher suite requires it,
     *         otherwise it will be ignored (and can be null). This parameter is used when creating the internal
     *         {@link SSLEngine} handling the encryption/decryption and not  authenticated by the SSLEngine
     *         (per documentation at https://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLEngine.html#SSLEngine).
     * @param peerPort
     *         The remote port this channel will be connecting to if the cipher suite requires it, otherwise it will be
     *         ignored (and can be 0).
     * @param clientMode
     *         True if the channel will be used on the client side, false if on the server
     * @param executorService
     *         Used to execute long blocking operations of sslEngine as well as occasional flush outs. If left null,
     *         which is recommended, the default executorService internal to {@link SSLSelectorProvider} will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         Since the tasksExecutorService is not owned, it will not be shutdown when the channel is closed.
     * @param innerChannel
     *         The inner channels to be used by the secure channels being created, if it exists. The innerServerChannel
     *         would exist in cases where the TCP connection has already been created (and possibly used) with the
     *         remote party.
     *         If null, a new channel will be created. If not null and not connected, the innerServerChannel will first
     *         be connected and then used by the secure one for the remainder of the session.
     * @return The newly created instance of {@link SSLSocketChannel}
     * @throws IOException
     *         If the provider is the default no-op skeleton provider, or any other problem trying to instantiate the
     *         SSLSocketChannel.
     */
    public SSLSocketChannel openSocketChannel(SSLContext sslContext, String peerHost, int peerPort,
            boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        throw new IOException("This provider does not implement openSocketChannel. " +
            "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    /**
     * This method is not implemented/available and it is not expected to be implemented by any provider.
     *
     * @return The newly created DatagramChannel
     * @throws IOException
     *         It will always throw "Not implemented/available"
     */
    @Override
    public DatagramChannel openDatagramChannel(ProtocolFamily family) throws IOException {
        throw new IOException("Not implemented/available");
    }
}
