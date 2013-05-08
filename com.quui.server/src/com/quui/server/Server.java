package com.quui.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements Runnable {
	private Thread _listener;
	private ServerSocket _server;
	private Vector<Client> _clients = new Vector<Client>();
	private IDataTransformerFactory _factory;

	public Server(final String host, final int port, final IDataTransformerFactory factory) throws BindException {
		_factory = factory;
		startServer(host, port);
	}

	protected void startServer(final String host, final int port) throws BindException {
		try {
			_server = new ServerSocket(port, 30, InetAddress.getByName(host));
			_listener = new Thread(this);
			_listener.setDaemon(true);
			_listener.start();
		} catch (Exception e) {
			throw new BindException(e.getMessage());
		}
	}

	public void run() {
		try {
			while (!_server.isClosed()) {
				final Socket clientSocket = _server.accept();
				try {
					final Client c = new Client(this, clientSocket, _factory.create());
					c.start();
					_clients.addElement(c);
					System.out.println("client connected: " + clientSocket + " count: " + _clients.size());
				} catch (Exception ce) {
					System.err.println("fail to setup client: " + ce.getMessage());
					try { clientSocket.close(); } catch (Exception e) {}
				}
			}
		} catch (Exception e) {
			System.err.println("While running, caught exception: " + e.getMessage());
		}

		destroy();
	}

	protected void removeClient(final Client client) {
		try {
			_clients.remove(client);
		} catch (Exception e) {}
	}

	/**
	 * Closes the socket
	 */
	public void destroy() {
		try {
			_server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		_listener = null;
		_server = null;
		_factory = null;
		_clients = null;
	}
}
