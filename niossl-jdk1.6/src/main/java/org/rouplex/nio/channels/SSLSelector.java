package org.rouplex.nio.channels;

import org.rouplex.nio.channels.spi.SSLSelectorProvider;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;

/**
 * A subclass of a {@link Selector} that can be used to register and select on {@link SSLSocketChannel}s or
 * {@link SSLServerSocketChannel}s the same way as a Selector is used to register and select on {@link SocketChannel}s
 * or {@link ServerSocketChannel}s.
 *
 * Further, an SSLSelector instance can be used to register and select on {@link SocketChannel}s or
 * {@link ServerSocketChannel}s. The same SSLSelector instance can be used to register and select a mixture of the
 * plain and SSL channels if needed.
 *
 * @see Selector
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLSelector extends AbstractSelector {

    protected SSLSelector(SSLSelectorProvider selectorProvider) {
        super(selectorProvider);
    }

    /**
     * Opens an sslSelector.
     *
     * The new selector is created by invoking the {@link
     * org.rouplex.nio.channels.spi.SSLSelectorProvider#openSelector openSelector} method of the system-wide default
     * {@link org.rouplex.nio.channels.spi.SSLSelectorProvider} object.
     *
     * @return
     *          A new sslSelector
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public static SSLSelector open() throws IOException {
        return SSLSelectorProvider.provider().openSelector();
    }
}
