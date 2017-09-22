package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.SocketChannel;
import java.util.Set;
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
     * Create an {@link SSLServerSocketChannel} using the default security settings obtainable via
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
     * Create an {@link SSLServerSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()} and connect it to the remote address before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SocketAddress socketAddress) throws IOException {
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
     * @return The newly created {@link SSLSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext) throws IOException {
        return open(socketAddress, sslContext, null, 0, true, null, null);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, an existing (and
     * possibly connected) {@link SocketChannel} and connect it before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @param clientMode
     *         True if the channel will be used on the client side, false if on the server
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. If null, a default
     *         executor service, shared with other SSLSocketChannel instances, will be used.
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
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext,
                                        boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        return open(socketAddress, sslContext, null, 0, clientMode, executorService, innerChannel);
    }

    /**
     * Create an {@link SSLSocketChannel} using security settings defined in {@link SSLContext}, an existing (and
     * possibly connected) {@link SocketChannel} and connect it before returning.
     *
     * @param socketAddress
     *         The socketAddress to connect right after the channels creation
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
     *         True if the channel will be used on the client side, false if on the server
     * @param executorService
     *         The executor service to be used for the long standing {@link SSLEngine} tasks. If null, a default
     *         executor service, shared with other SSLSocketChannel instances, will be used.
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
    public static SSLSocketChannel open(SocketAddress socketAddress, SSLContext sslContext, String peerHost, int peerPort,
                                        boolean clientMode, ExecutorService executorService, SocketChannel innerChannel) throws IOException {

        SSLSocketChannel sslSocketChannel = SSLSelectorProvider.provider()
            .openSocketChannel(sslContext, peerHost, peerPort, clientMode, executorService, innerChannel);

        if (socketAddress != null) {
            sslSocketChannel.connect(socketAddress);
        }

        return sslSocketChannel;
    }

    @Override
    public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
        return innerChannel().setOption(name, value);
    }

    @Override
    public <T> T getOption(SocketOption<T> name) throws IOException {
        return innerChannel().getOption(name);
    }

    @Override
    public Set<SocketOption<?>> supportedOptions() {
        return innerChannel().supportedOptions();
    }

    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return socket().getLocalSocketAddress();
    }
}
