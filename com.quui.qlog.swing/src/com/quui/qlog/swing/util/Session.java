package com.quui.qlog.swing.util;

import java.awt.Window;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.quui.qlog.core.TableBuilder;
import com.quui.qlog.swing.gui.tab.ITab;
import com.quui.qlog.swing.gui.tab.Tab;
import com.quui.qlog.swing.gui.tab.TabController;
import com.quui.qlog.swing.gui.tab.TabFactory;
import com.quui.utils.file.FileSaver;
import com.quui.utils.util.IDestroyable;

public class Session {

	static String STYLE = "<style type='text/css'>/*<![CDATA[*/\n"
+ " body { background-color: #efefef; }\n"
+ " h1, h2 { color: #333; }\n"
+ " .qtab { border: 1px solid #999; padding: 1em; margin-bottom: 2.6em; background-color: #fff; }\n"
+ "/*]]>*/</style>\n";

	static public void export(final Window wnd, final List<ITab> tabs) {

		final StringBuilder c = new StringBuilder();
		for (final ITab t : tabs) {
			c.append("<h2>" + t.getName() + "</h2>\n");
			c.append("<div id='" + t.getName() +"' class='qtab'>\n");
			c.append( ((Tab)t).getTableBuilder().getContent() );
			c.append("\n</div>\n");
		}

		if (c.length() != 0) {
			final String filename = "QLog Session "
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date());

			c.insert(0, "<h1>" + filename + "</h1>\n");
			c.insert(0, "<body>\n");
			c.insert(0, TableBuilder.getCss(1.0f) + "\n");
			c.insert(0, STYLE);
			c.insert(0, "<title>" + filename + "</title>\n");
			c.insert(0, "<html>\n");
			c.insert(0, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			c.append("</body>\n</html>");

			final JFileChooser fc = new JFileChooser();

			fc.setDialogTitle("Save Session " + filename);
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
			fc.setSelectedFile(new File(filename.replace(' ', '-').toLowerCase() + ".htm"));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fc.showOpenDialog(wnd) == JFileChooser.APPROVE_OPTION) {

				if (fc.getSelectedFile().exists()
						&& JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(wnd, ""
							+ fc.getSelectedFile().getName()
							+ " exists. Overwrite?", "Overwirte Existing File",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE))
					return;

				new FileSaver(c.toString(), fc.getSelectedFile().getAbsolutePath());
			}
		}
	}

	static public void doimport(final Window wnd, final TabController tc) throws Exception {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Import QLog Session");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "QLog-Session";
			}

			@Override
			public boolean accept(File f) {
				try {
					final String ext = f.getName().substring(f.getName().length() - 3).toLowerCase();
					return "htm".equals(ext) || "tml".equals(ext);
				} catch (Exception ex) {}

				return false;
			}
		});
		if (fc.showOpenDialog(wnd) == JFileChooser.APPROVE_OPTION) {
			new SessionParser(tc, fc.getSelectedFile()).execute();
		}
	}

	static class SessionParser extends SwingWorker<Integer, Integer> {

		private TabController tc;
		private File file;

		public SessionParser(final TabController tc, final File file) {
			this.tc = tc;
			this.file = file;
		}

		@Override
		protected Integer doInBackground() throws Exception {
			tc.removeAllTabs();
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document doc = builder.parse(file);
			doc.getDocumentElement().normalize();
			final NodeList l = doc.getElementsByTagName("body").item(0).getChildNodes();
			for (int i=0; i < l.getLength(); ++i ) {
				try {
					final Element e = (Element)l.item(i);
					if (!"qtab".equals(e.getAttribute("class")))
						continue;
					final String id = e.getAttribute("id");
					final ITab tab = TabFactory.createTab(new IDestroyable() {
						public void destroy() {}
					}, id);

					tab.incomingCommand("clearonconnect");

					final NodeList msgs = e.getElementsByTagName("p");
					for (int j=0; j < msgs.getLength(); ++j) {
						final Element msg = (Element)msgs.item(j);
						tab.incomingMessage(msg.getAttribute("style").substring(17),  msg.getTextContent());
					}
				} catch (Exception ex) {
					continue;
				}
			}
			return 0;
		}
	}
}
