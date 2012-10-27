package org.adbcj.postgresql.mina;

import org.adbcj.CloseMode;
import org.adbcj.Connection;
import org.adbcj.DbException;
import org.adbcj.DbFuture;
import org.adbcj.postgresql.codec.AbstractConnectionManager;
import org.adbcj.postgresql.codec.DecoderInputStream;
import org.adbcj.postgresql.codec.backend.AbstractBackendMessage;
import org.adbcj.postgresql.codec.backend.BackendMessageDecoder;
import org.adbcj.postgresql.codec.frontend.AbstractFrontendMessage;
import org.adbcj.postgresql.codec.frontend.FrontendMessageEncoder;
import org.adbcj.support.DefaultDbFuture;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.filter.codec.*;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author Mike Heath
 */
public class MinaConnectionManager extends AbstractConnectionManager {
	public static final String CODEC_NAME = AbstractConnectionManager.class.getName() + ".codec";

	private final Logger logger = LoggerFactory.getLogger(AbstractConnectionManager.class);

	private final NioSocketConnector socketConnector;

	// Access must be synchronized on 'this'
	private DefaultDbFuture<Void> closeFuture = null;

	private volatile boolean pipeliningEnabled = true;

	private static final ProtocolCodecFactory CODEC_FACTORY = new ProtocolCodecFactory() {
		public ProtocolDecoder getDecoder(IoSession session) throws Exception {
			final MinaConnection connection = IoSessionUtil.getConnection(session);
			return new CumulativeProtocolDecoder() {

				private final BackendMessageDecoder decoder = new BackendMessageDecoder(connection.getConnectionState());

				@Override
				protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
					DecoderInputStream inputStream = new DecoderInputStream(in.asInputStream());
					while (true) {
						inputStream.setLimit(Integer.MAX_VALUE);
						AbstractBackendMessage message = decoder.decode(inputStream, false);
						if (message == null) {
							return false;
						}
						out.write(message);
					}
				}
				
			};
		}
		public ProtocolEncoder getEncoder(IoSession session) throws Exception {
			final MinaConnection connection = IoSessionUtil.getConnection(session);
			return new ProtocolEncoder() {

				private final FrontendMessageEncoder encoder = new FrontendMessageEncoder(connection.getConnectionState());

				@Override
				public void dispose(IoSession ioSession) throws Exception {
					// Do nothing.
				}

				@Override
				public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
					IoBuffer buffer = IoBuffer.allocate(4096);
					OutputStream out = buffer.asOutputStream();
					if (o instanceof AbstractFrontendMessage) {
						encoder.encode(out, (AbstractFrontendMessage)o);
					} else if (o instanceof AbstractFrontendMessage[]) {
						encoder.encode(out, (AbstractFrontendMessage[])o);
					} else {
						throw new IllegalStateException("Unkown message type for: " + o);
					}
					out.close();
					buffer.flip();
					protocolEncoderOutput.write(buffer);
					protocolEncoderOutput.flush();
				}
			};
		}
	};

	public MinaConnectionManager(String host, int port, String username, String password, String database,
			Map<String,String> properties) {
		super(username, password, database);
		logger.debug("Creating new Postgresql ConnectionManager");

		socketConnector = new NioSocketConnector();

		socketConnector.getSessionConfig().setTcpNoDelay(true);
		DefaultIoFilterChainBuilder filterChain = socketConnector.getFilterChain();

		filterChain.addLast(CODEC_NAME, new ProtocolCodecFilter(CODEC_FACTORY));

		socketConnector.setHandler(new IoHandler(this));

		InetSocketAddress address = new InetSocketAddress(host, port);
		socketConnector.setDefaultRemoteAddress(address);
	}

	public DbFuture<Connection> connect() {
		if (isClosed()) {
			throw new DbException("Connection manager closed");
		}
		logger.debug("Starting connection");
		PgConnectFuture future = new PgConnectFuture();
		socketConnector.connect(future);

		logger.debug("Started connection");

		return future;
	}

	class PgConnectFuture extends DefaultDbFuture<Connection> implements IoSessionInitializer<ConnectFuture> {

		private boolean cancelled = false;
		private boolean started = false;

		@Override
		public synchronized void initializeSession(IoSession session, ConnectFuture future) {
			if (cancelled) {
				session.close(true);
				return;
			}
			logger.debug("Creating AbstractConnection");

			MinaConnection connection = new MinaConnection(MinaConnectionManager.this, this, session);
			IoSessionUtil.setConnection(session, connection);
		}

		@Override
		protected synchronized boolean doCancel(boolean mayInterruptIfRunning) {
			if (started) {
				logger.debug("Can't cancel, connection already started");
				return false;
			}
			logger.debug("Cancelled connect");
			cancelled = true;
			return true;
		}

	}

	public synchronized DbFuture<Void> close() throws DbException {
		// TODO Put test in TCK to make sure all ConnectionManager connections get closed
		if (isClosed()) {
			return closeFuture;
		}
		closeFuture = new DefaultDbFuture<Void>();
        socketConnector.dispose();
        closeFuture.setResult(null);
		return closeFuture;
	}

    @Override
    public DbFuture<Void> close(CloseMode mode) throws DbException {
        throw new Error("Not implemented yet: TODO");  //TODO: Implement
    }

    public synchronized boolean isClosed() {
		return closeFuture != null;
	}

	// ================================================================================================================
	//
	// Non-API methods
	//
	// ================================================================================================================

	@Override
	public String toString() {
		return String.format("Postgresql (MINA) Connection Manager (Db: '%s', User: '%s')", getDatabase(), getUsername());
	}


}
