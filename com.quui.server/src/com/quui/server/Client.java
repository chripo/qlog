package com.quui.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class Client extends Thread implements IClient {
	interface CONST {
		String UTF8 = "UTF-8";
		String DELIMITER = "\u0000";
	}

	final private String _id;
	final private long _connectionTime;
	private Scanner _input;
	private PrintWriter _output;
	protected IDataTransformer _handler;
	protected Server _server;
	private Socket _socket;

	public Client(final Server server, final Socket socket, final IDataTransformer handler) throws IOException {
		_id = socket.getPort() + "_ID";
		_connectionTime = new Date().getTime();
		_server = server;
		_handler = handler;
		_handler.setClient(this);
		_socket = socket;

		initialize(socket);
	}

	protected void initialize(final Socket socket) throws IOException {
		_input = new Scanner(new InputStreamReader(socket.getInputStream(), CONST.UTF8));
		_input.useDelimiter(CONST.DELIMITER);
		_output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), CONST.UTF8));
	}

	final public void run() {
		try {
			while (true) {
				final String data = _input.next();

				if (handleFlashSecurity(data))
					continue;

				_handler.onData(data);
			}
		} catch (Exception e) {
			System.err.println(getClientId() + " read error: " + e.getMessage());
		}
		destroy();
	}

	private boolean handleFlashSecurity(final String data) {
		try {
			if (data.startsWith("<policy-file-request")) {
				send("<?xml version='1.0'?><cross-domain-policy><allow-access-from domain='*' to-ports='*'/></cross-domain-policy>");
				return true;
			}
		} catch (Exception e) {}
		return false;
	}

	/**
	 * send a message to the connected client
	 *
	 * @param message
	 */
	final public void send(final String message) {
		try {
			_output.print(message + CONST.DELIMITER);
			_output.flush();
		} catch (Exception e) {
			System.err.println("Client [" + getClientId() + "] send exeption: " + e.getMessage());
		}
	}

	final public String getClientId() {
		return _id;
	}

	/**
	 * @return the time this client is connected to the server measured in seconds
	 */
	final public long getConnectionTime() {
		final long diff = new Date().getTime() - _connectionTime;
		return ((diff / 60) / 60);
	}

	/**
	 * Shuts down the thread and cleans up used resources
	 */
	public void destroy() {
		try {
			System.out.println("shutting down client: " + getClientId());
			try { _socket.close(); }  catch (Exception e) {}
			try { _input.close(); } catch (Exception e) {}
			try { _output.close(); } catch (Exception e) {}
			_input = null;
			_output = null;
			_socket = null;
			_server.removeClient(this);
			_server = null;
		} catch (Exception e) {}
	}
}
