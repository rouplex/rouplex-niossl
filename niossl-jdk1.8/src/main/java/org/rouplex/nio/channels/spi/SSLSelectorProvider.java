package org.rouplex.nio.channels.spi;

import org.rouplex.nio.channels.SSLSelector;
import org.rouplex.nio.channels.SSLServerSocketChannel;
import org.rouplex.nio.channels.SSLSocketChannel;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;

/**
 * The base SSLSelectorProvider, containing boilerplate code for loading an eventual implementation of the same.
 * If no implementations are found, then an instance of this class is returned, calls to which will fail with "not
 * implemented" {@link IOException}s.
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public class SSLSelectorProvider extends SelectorProvider {
    private static SSLSelectorProvider sslSelectorProvider = null;

    /**
     * Load the provider if not already loaded, then return its reference.
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
     * @return a reference to the loaded provider if it was loaded, false otherwise
     */
    private static SSLSelectorProvider loadProvider(ClassLoader classLoader) {
        String fqcn = System.getProperty("java.nio.channels.spi.SSLSelectorProvider");
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

    @Override
    public DatagramChannel openDatagramChannel() throws IOException {
        throw new IOException("Not implemented/available");
    }

    @Override
    public Pipe openPipe() throws IOException {
        throw new IOException("Not implemented/available");
    }

    @Override
    public SSLSelector openSelector() throws IOException {
        throw new IOException("This provider does not implement openSelector. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    @Override
    public SSLServerSocketChannel openServerSocketChannel() throws IOException {
        return openServerSocketChannel(null);
    }

    public SSLServerSocketChannel openServerSocketChannel(SSLContext sslContext) throws IOException {
        return openServerSocketChannel(sslContext, null, null);
    }

    public SSLServerSocketChannel openServerSocketChannel(SSLContext sslContext,
            ExecutorService acceptExecutorService, ExecutorService tasksExecutorService) throws IOException {

        throw new IOException("This provider does not implement openServerSocketChannel. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    @Override
    public SSLSocketChannel openSocketChannel() throws IOException {
        return openSocketChannel(null, false, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} instance by using an optional {@link SSLContext}, {@link ExecutorService}, or
     * {@link SocketChannel}.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         will be used for the {@link SSLSocketChannel} instance being created.
     * @param clientMode
     *         True if the channel will be used on the client side, false if on the server
     * @param executorService
     *         Used to execute long blocking operations of sslEngine as well as occasional flush outs. If left null,
     *         which is recommended, the default tasksExecutorService obtainable internally from
     *         {@link SSLSelectorProvider} will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         Since the tasksExecutorService is not owned, it will not be shutdown when the channel is closed.
     * @param innerChannel
     *         The inner channels to be used by the secure channels being created, if it exists. The innerServerChannel
     *         would exist in cases where the TCP connection has already been created (and possibly used) with the
     *         remote party.
     *         If null, a new channel will be created. If not null and not connected, the innerServerChannel will first
     *         be connected and then used by the secure one for the remainder of the session.
     * @return the newly created instance of {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public SSLSocketChannel openSocketChannel(SSLContext sslContext, boolean clientMode,
            ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        throw new IOException("This provider does not implement openSocketChannel. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

//////////////////////// JDK 7+ ///////////

    @Override
    public DatagramChannel openDatagramChannel(ProtocolFamily family) throws IOException {
        throw new IOException("Not implemented/available");
    }
}
