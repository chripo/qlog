package com.quui.qlog.core;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class PropertiesReader {
	private String _path;
	private Document _document;

	public PropertiesReader(String path) {
		_path = path;
	}

	public void initialize() {
		try {
			readFile();
		} catch (Exception e) {
			System.err.println("Error: fail to parse config file: " + _path);
		}
	}

	private void readFile() throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = factory.newDocumentBuilder();
		_document = builder.parse(new File(_path));
	}

	public String getIp() {
		try {
			return _document.getElementsByTagName("ip").item(0).getTextContent();
		} catch (Exception e) {}
		return "127.0.0.1";
	}

	public int getPort() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("port").item(0).getTextContent());
		} catch (Exception e) {}
		return 6666;
	}

	public int getX() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("x").item(0).getTextContent());
		} catch (Exception e) {}
		return 0;
	}

	public int getY() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("y").item(0).getTextContent());
		} catch (Exception e) {}
		return 0;
	}

	public int getWidth() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("width").item(0).getTextContent());
		} catch (Exception e) {}
		return 800;
	}

	public int getHeight() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("height").item(0).getTextContent());
		} catch (Exception e) {}
		return 600;
	}

	public boolean getAlwaysOnTop() {
		try {
			return _document.getElementsByTagName("alwaysontop").item(0).getTextContent().equals("true");
		} catch (Exception e) {}
		return false;
	}

	public boolean getClearOnConnect() {
		try {
			return _document.getElementsByTagName("clearonconnect").item(0).getTextContent().equals("true");
		} catch (Exception e) {}
		return true;
	}

	public int getFontSize() {
		try {
			return Integer.parseInt(_document.getElementsByTagName("fontsize").item(0).getTextContent());
		} catch (Exception e) {}
		return 16;
	}
}
