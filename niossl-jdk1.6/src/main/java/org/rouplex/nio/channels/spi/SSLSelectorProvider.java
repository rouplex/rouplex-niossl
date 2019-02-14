package org.rouplex.nio.channels.spi;

import org.rouplex.nio.channels.SSLSelector;
import org.rouplex.nio.channels.SSLServerSocketChannel;
import org.rouplex.nio.channels.SSLSocketChannel;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketAddress;
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
 * An extension of {@link SelectorProvider}, this class defines the methods for creating instances of
 * {@link SSLSelector}, {@link SSLSocketChannel} and {@link SSLServerSocketChannel}.
 *
 * Non static methods on this class are stubs which fail with {@link IOException} if called. An implementation provider
 * overrides them with their own implementation.
 *
 * This class also provides the static calls for dynamically locating and loading an instance this class. If no
 * implementations are found, either via system properties, or service provider libraries, then an instance of this
 * same class is returned, calls to which will fail with "not implemented" {@link IOException}s.
 *
 * @see SelectorProvider
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
     * If an implementation is still not found, then the {@link ServiceLoader} class will be used to look up for a
     * service instance implementing a {@link SSLSelectorProvider}. If one is found, it will be cached for future use,
     * then returned.
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
     * {@link SSLServerSocketChannel}, {@link SocketChannel}, {@link ServerSocketChannel} instances
     *
     * @return The created SSLSelector
     * @throws IOException
     *         If the provider is the default no-op provider, or any other problem trying to create the SSLSelector.
     */
    @Override
    public SSLSelector openSelector() throws IOException {
        throw new IOException("This provider does not implement openSelector. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    /**
     * Create an {@link SSLServerSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()}.
     *
     * @return The newly created SSLServerSocketChannel
     * @throws IOException
     *         The reason the SSLServerSocketChannel could not be created
     */
    @Override
    public SSLServerSocketChannel openServerSocketChannel() throws IOException {
        return openServerSocketChannel(null, null);
    }

    /**
     * Create an {@link SSLServerSocketChannel} using security settings defined in {@link SSLContext} and an optional
     * executorService for background tasks.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. Except for more advanced
     *         use cases, our recommendation is to pass null, in which case the {@link SSLSelectorProvider}'s default
     *         executor service, shared with other SSLSocketChannel instances, will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         The executorService is not considered to be owned by the returned SSLSocketChannel instance, so it will
     *         not be shutdown when the channel is closed.
     * @return The newly created SSLServerSocketChannel
     * @throws IOException
     *         The reason the SSLServerSocketChannel could not be created
     */
    public SSLServerSocketChannel openServerSocketChannel(
        SSLContext sslContext, ExecutorService executorService) throws IOException {

        throw new IOException("This provider does not implement openServerSocketChannel. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }

    /**
     * Create an {@link SSLSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()}.
     *
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    @Override
    public SSLSocketChannel openSocketChannel() throws IOException {
        return openSocketChannel(null, null, 0, false, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, an optional peerHost
     * and peerPort for {@link SSLSession} caching strategies (or when Kerberos is used), a clientMode defining whether
     * this channel must start handshaking in "client" mode, an optional executorService for background tasks and an
     * optional {@link SocketChannel}. The returned instance is not connected (even if the inner channel is), so a call
     * to {@link SSLSocketChannel#connect(SocketAddress)} is necessary afterwards.
     *
     * The reason for peerHost/peerPort is twofold, for SSLSession cashing strategies, as well when using Kerberos
     * cipher suites. For more visit
     * https://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLContext.html#createSSLEngine(java.lang.String, int)

     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
     * @param peerHost
     *         The name of the remote host this channel will be connecting to. It must be present if SSLSession reuse
     *         is preferred or if Kerberos cipher suites are used. Otherwise it can be left to null.
     * @param peerPort
     *         The remote port this channel will be connecting to. It must be a positive number if SSLSession reuse
     *         is preferred or if Kerberos cipher suites are used. Otherwise it can be left to 0.
     * @param clientMode
     *         True if the channel must start handshaking in "client" mode, false otherwise
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. Except for more advanced
     *         use cases, our recommendation is to pass null, in which case the {@link SSLSelectorProvider}'s default
     *         executor service, shared with other SSLSocketChannel instances, will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         The executorService is not considered to be owned by the returned SSLSocketChannel instance, so it will
     *         not be shutdown when the channel is closed.
     * @param innerChannel
     *         The inner channel to be used by the secure channel being created, if it exists. Passing an inner channel
     *         is useful in cases where the TCP connection has already been established (and possibly used) with the
     *         remote peer, and now they have agreed to secure their line via SSL/TLS. If null is passed, a new channel
     *         will be created; if a not null and not connected channel is passed, the inner channel will first be
     *         connected and then used by the secure channel for the remainder of the session. The innerChannel should
     *         not be used after this moment, and it will close when this channel closes (either via a call to
     *         {@link SSLSocketChannel#close()} or a condition leading to its closure)
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    public SSLSocketChannel openSocketChannel(SSLContext sslContext, String peerHost, int peerPort,
            boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        throw new IOException("This provider does not implement openSocketChannel. " +
                "Include rouplex-niossl-spi provider for a concrete implementation");
    }
}
