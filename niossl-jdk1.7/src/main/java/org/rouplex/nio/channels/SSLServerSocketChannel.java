package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/**
 * A subclass of a {@link SSLServerSocketChannel} that provides same functionality as a {@link ServerSocketChannel} but
 * over a secured line with the remote endpoints.
 * The various SSL configuration aspects, such as enabling particular secure protocols and ciphers, key and certificate
 * management, are handled via the {@link SSLContext} class, similar to the way it is done when {@link SSLSocket} class
 * is used.
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
     *         The sslContext to be used or null to if JVM's default security settings are preferred
     * @return The newly created {@link SSLServerSocketChannel}
     * @throws IOException
     *         If anything goes wrong during the creation
     */
    public static SSLServerSocketChannel open(SSLContext sslContext) throws IOException {
        return SSLSelectorProvider.provider().openServerSocketChannel(sslContext, null, null);
    }
}
