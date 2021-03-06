package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * A secure {@link ServerSocketChannel} for SSL/TLS communication with remote endpoints over TCP. An instance of this
 * class uses, in its turn, an inner ServerSocketChannel for listening for and accepting TCP connections from remote
 * endpoints.
 *
 * All the SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, the same way it is done when an {@link SSLSocket} class is
 * used.
 *
 * This class does not introduce any extra methods relative to the base class, and adheres to its exact semantics for a
 * maximum compatibility. The only difference is during instantiation, where other parameters such as an SSLContext
 * must be provided for it in order to function properly.
 *
 * @see ServerSocketChannel
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLServerSocketChannel extends ServerSocketChannel {

    protected SSLServerSocketChannel(SSLSelectorProvider provider) {
        super(provider);
    }

    /**
     * Create an {@link SSLServerSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()}.
     *
     * @return The newly created SSLServerSocketChannel
     * @throws IOException
     *         The reason the SSLServerSocketChannel could not be created
     */
    public static SSLServerSocketChannel open() throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel();
    }

    /**
     * Create an {@link SSLServerSocketChannel} using security settings defined in {@link SSLContext}
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
     * @return The newly created SSLServerSocketChannel
     * @throws IOException
     *         The reason the SSLServerSocketChannel could not be created
     */
    public static SSLServerSocketChannel open(SSLContext sslContext) throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel(sslContext, null);
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
    public static SSLServerSocketChannel open(SSLContext sslContext, ExecutorService executorService) throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel(sslContext, executorService);
    }
}
