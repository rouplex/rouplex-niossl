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
 * or {@link ServerSocketChannel}s
 * As with the rest of the package, we have stayed faithful to the requirements of the API, laid out at
 * http://docs.oracle.com/javase/6/docs/api/java/nio/channels/Selector.html.
 *
 * @author Andi Mullaraj (andimullaraj at gmail.com)
 */
public abstract class SSLSelector extends AbstractSelector {

    protected SSLSelector(SSLSelectorProvider selectorProvider) {
        super(selectorProvider);
    }

    /**
     * {@inheritDoc}
     */
    public static SSLSelector open() throws IOException {
        return SSLSelectorProvider.provider().openSelector();
    }
}
