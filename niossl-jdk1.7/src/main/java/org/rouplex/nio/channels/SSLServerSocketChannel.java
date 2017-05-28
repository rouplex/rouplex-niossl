package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * A subclass of a {@link ServerSocketChannel} that provides the same functionality but over a secured line with the
 * remote endpoint.
 *
 * The various SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, the same way it is done when an {@link SSLSocket} class is
 * used.
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLServerSocketChannel extends ServerSocketChannel {

    protected SSLServerSocketChannel(SSLSelectorProvider provider) {
        super(provider);
    }

    /**
     * Used to get access to the inner channel in order to implement the setting of socket options directly here. This
     * way, the SPI is jdk agnostic (or more precisely it only needs to support the lowest jdk provided -- Jdk1.6).
     * @return
     */
    protected abstract ServerSocketChannel innerChannel();

    /**
     * Create an {@link SSLServerSocketChannel} using the default security settings obtainable via
     * {@link SSLContext#getDefault()}.
     *
     * @return The newly created {@link SSLServerSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLServerSocketChannel open() throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel();
    }

    /**
     * Create an {@link SSLServerSocketChannel} using security settings defined in {@link SSLContext}.
     *
     * @param sslContext
     *         An instance of {@link SSLContext} via which the caller defines the {@link KeyManager} and {@link
     *         TrustManager} providing the private keys and certificates for the encryption and
     *         authentication/authorization of the remote party.
     *         If this parameter is null, then the JRE's default sslContext instance, configured with JRE's defaults,
     *         and obtainable via {@link SSLContext#getDefault()} will be used.
     * @return The newly created {@link SSLServerSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLServerSocketChannel open(SSLContext sslContext) throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel(sslContext, null, null);
    }

    @Override
    public <T> ServerSocketChannel setOption(SocketOption<T> name, T value) throws IOException {
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
