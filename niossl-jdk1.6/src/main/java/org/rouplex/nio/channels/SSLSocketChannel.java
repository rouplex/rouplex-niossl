package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * A secure {@link SocketChannel} for SSL/TLS communication with remote endpoints over TCP. An instance of this class
 * uses, in its turn, an inner SocketChannel for transmitting all the encrypted data to/from the remote endpoint.
 *
 * All the SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, the same way it is done when an {@link SSLSocket} class is
 * used.
 *
 * This class does not introduce any extra methods relative to the base class, and adheres to its exact semantics for a
 * maximum compatibility. The only difference is during instantiation, where other parameters such as an SSLContext
 * must be provided for it in order to function properly.
 *
 * @see SocketChannel
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLSocketChannel extends SocketChannel {
    protected SSLSocketChannel(SSLSelectorProvider provider) {
        super(provider);
    }

    /**
     * Used to get access to the inner channel in order to implement the setting of socket options directly here. This
     * way, the SPI is jdk agnostic (or more precisely it only needs to support the lowest jdk provided -- Jdk1.6).
     *
     * @return
     *          The inner channel to be used for tcp communication with the remote endpoint
     */
    protected abstract SocketChannel innerChannel();

    /**
     * Create an {@link SSLSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()}.
     *
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    public static SSLSocketChannel open() throws IOException {
        return open(null, null, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()} and connect it to the remote address before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after channel's creation
     * @return The newly created and connected SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created or connected
     */
    public static SSLSocketChannel open(SocketAddress socketAddress) throws IOException {
        if (socketAddress == null) {
            throw new NullPointerException("SocketAddress cannot be null");
        }
        return open(socketAddress, null, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    public static SSLSocketChannel open(SSLContext sslContext) throws IOException {
        return open(null, sslContext, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext} and connect it before
     * returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
     * @return The newly created and connected {@link SSLSocketChannel}
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created or connected
     */
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext) throws IOException {
        if (socketAddress == null) {
            throw new NullPointerException("SocketAddress cannot be null");
        }
        return open(socketAddress, sslContext, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, a clientMode defining
     * whether this channel must start handshaking in "client" mode, an optional executorService for background tasks,
     * and an optional {@link SocketChannel}. The returned instance is not connected (even if the inner channel is),
     * so a call to {@link #connect(SocketAddress)} is necessary afterwards.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and authentication of the
     *         remote party. If this parameter is null, then the JRE's default sslContext instance obtainable via
     *         {@link SSLContext#getDefault()} will be used.
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
     *         {@link #close()} or a condition leading to its closure)
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    public static SSLSocketChannel open(SSLContext sslContext, boolean clientMode,
        ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        return open(null, sslContext, null, 0, clientMode, executorService, innerChannel);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, an optional peerHost
     * and peerPort for {@link SSLSession} caching strategies (or when Kerberos is used), a clientMode defining whether
     * this channel must start handshaking in "client" mode, an optional executorService for background tasks and an
     * optional {@link SocketChannel}. The returned instance is not connected (even if the inner channel is), so a call
     * to {@link #connect(SocketAddress)} is necessary afterwards.
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
     *         {@link #close()} or a condition leading to its closure)
     * @return The newly created SSLSocketChannel
     * @throws IOException
     *         The reason the SSLSocketChannel could not be created
     */
    public static SSLSocketChannel open(SSLContext sslContext, String peerHost, int peerPort,
            boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        return open(null, sslContext, peerHost, peerPort, clientMode, executorService, innerChannel);
    }

    private static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext, String peerHost,
        int peerPort, boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        SSLSocketChannel sslSocketChannel = SSLSelectorProvider.provider()
            .openSocketChannel(sslContext, peerHost, peerPort, clientMode, executorService, innerChannel);

        if (socketAddress != null) {
            sslSocketChannel.connect(socketAddress);
        }

        return sslSocketChannel;
    }
}
