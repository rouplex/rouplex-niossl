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
 * Further, an SSLSelector can be used to register and select on a mixture of {@link SSLSocketChannel}s,
 * {@link SocketChannel}s, {@link SSLServerSocketChannel}s or {@link ServerSocketChannel}s if needed to.
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
     * Opens an sslSelector using the default {@link SSLSelectorProvider}.
     *
     * @return
     *          An sslSelector
     *
     * @throws  IOException
     *          If an I/O error occurs
     */
    public static SSLSelector open() throws IOException {
        return SSLSelectorProvider.provider().openSelector();
    }
}
