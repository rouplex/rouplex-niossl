package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * A subclass of a {@link SocketChannel} that provides same functionality as a SocketChannel but over a secured line
 * with the remote endpoint.
 *
 * The various SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, the same way it is done when an {@link SSLSocket} class is
 * used.
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
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open() throws IOException {
        return open(null, null, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()} and connect it to the remote address before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @return The newly created and connected {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
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
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
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
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @return The newly created and connected {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
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
     * and an existing (and possibly connected) {@link SocketChannel}. The returned instance is not connected securely
     * yet (even if the inner channel is), so a call to {@link #connect(SocketAddress)} is necessary afterwards.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @param clientMode
     *         True if the channel must start handshaking in "client" mode, false otherwise
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. Except for more advanced
     *         use cases, our recommendation is to pass null, in which case the default executor service, shared with
     *         other SSLSocketChannel instances, will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         Since the executorService is not owned, it will not be shutdown when the channel is closed.
     * @param innerChannel
     *         The inner channel to be used by the secure channels being created, if it exists. The inner channel would
     *         exist in cases where the TCP connection has already been established (and possibly used) with the remote
     *         party.
     *         If null, a new channel will be created. If not null and not connected, the inner channel will first be
     *         connected and then used by the secure channel for the remainder of the session.
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SSLContext sslContext, boolean clientMode,
        ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        return open(null, sslContext, null, 0, clientMode, executorService, innerChannel);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, a peerHost and
     * peerPort required for certain cipher suites such as kerberos, a clientMode defining whether this channel must
     * start handshaking in "client" mode, an optional executorService for background tasks, and an existing (and
     * possibly connected) {@link SocketChannel}. The returned instance is not connected securely yet (even if the
     * inner channel is), so a call to {@link #connect(SocketAddress)} is necessary afterwards.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @param peerHost
     *         The name of the remote host this channel will be connecting to, if the cipher suite requires it,
     *         otherwise it will be ignored (and can be null). This parameter is used when creating the internal
     *         {@link SSLEngine} handling the encryption/decryption and not  authenticated by the SSLEngine
     *         (per documentation at https://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLEngine.html#SSLEngine).
     * @param peerPort
     *         The remote port this channel will be connecting to if the cipher suite requires it, otherwise it will be
     *         ignored (and can be 0).
     * @param clientMode
     *         True if the channel must start handshaking in "client" mode, false otherwise
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. Except for more advanced
     *         use cases, our recommendation is to pass null, in which case the default executor service, shared with
     *         other SSLSocketChannel instances, will be used.
     *         This executor service should allow for parallel execution among its tasks, since sslEngine can take
     *         advantage of it when performing long ops (a singleThreadExecutor, for example, would be a bad choice).
     *         Since the executorService is not owned, it will not be shutdown when the channel is closed.
     * @param innerChannel
     *         The inner channel to be used by the secure channels being created, if it exists. The inner channel would
     *         exist in cases where the TCP connection has already been established (and possibly used) with the remote
     *         party.
     *         If null, a new channel will be created. If not null and not connected, the inner channel will first be
     *         connected and then used by the secure channel for the remainder of the session.
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
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
