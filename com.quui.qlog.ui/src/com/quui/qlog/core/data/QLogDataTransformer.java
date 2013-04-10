package com.quui.qlog.core.data;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.quui.server.IClient;
import com.quui.server.IDataTransformer;


public class QLogDataTransformer implements IDataTransformer {
	private DocumentBuilder _builder;
	private boolean _isLoggedIn = false;
	private IClient _client;
	private IGuiMediator _mediator;
	final private Pattern _logPttrn;

	public QLogDataTransformer(IGuiMediator mediator) {
		_mediator = mediator;
		_logPttrn = Pattern.compile("<log>\\s*(?:<color>\\s*(.*?)\\s*</color>\\s*)?<(msg|command|tree)>\\s*(?:<!\\[CDATA\\[)?\\s*(.*?)(?:\\]\\]>)?</\\2>\\s*</log>");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			_builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClient(IClient client) {
		_client = client;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Expected data: <log><color>#ff00ff</color><msg>Some log msg</msg></log>
	 */
	public void onData(String data) {
		if (_isLoggedIn) {
			handleData(data);
		} else {
			handleLogin(data);
		}
	}

	private void handleData(final String data) {
		final Matcher m = _logPttrn.matcher(data);
		String type;
		while (m.find()) {
			type = m.group(2);
			if ("msg".equals(type)) {
				_mediator.onMessage(m.group(3), m.group(1));

			} else if ("command".equals(type)) {
				_mediator.onCommand(m.group(3));

			} else if ("tree".equals(type)) {
				final Document doc;
				try {
					doc = _builder.parse(new InputSource(new StringReader(m.group(3))));
				} catch (Exception e) {
					continue;
				}
				_mediator.onTree(doc);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Expected data: <login><name>Some name</name></login>
	 */
	private void handleLogin(String data) {
		String name = "default";
		try {
			Document doc = _builder.parse(new InputSource(new StringReader(data)));
			name = doc.getElementsByTagName("name").item(0).getTextContent();
		} catch (Exception e) {
			System.out.println(e.getMessage() + "\r" + e.getStackTrace());
		}

		_mediator.setDataTransformer(this);
		_mediator.onLogin(name);
		_isLoggedIn = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() {
		_client.destroy();
		_client = null;
		_builder = null;
	}

}
