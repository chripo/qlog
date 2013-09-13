package com.quui.qlog.swing.util;

import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import com.quui.qlog.core.TableBuilder;
import com.quui.qlog.swing.gui.tab.ITab;
import com.quui.qlog.swing.gui.tab.Tab;
import com.quui.qlog.swing.gui.tab.TabController;
import com.quui.qlog.swing.gui.tab.TabFactory;
import com.quui.utils.file.FileSaver;
import com.quui.utils.util.IDestroyable;

public class Session {

	public interface ITabFilter {
		boolean filter(final ITab tab);
	}

	static String STYLE = "<style type='text/css'>/*<![CDATA[*/\n"
+ " body { background-color: #efefef; }\n"
+ " h1, h2 { color: #333; }\n"
+ " .qtab { border: 1px solid #999; padding: 1em; margin-bottom: 2.6em; background-color: #fff; }\n"
+ "/*]]>*/</style>\n";

	static public final void export(final Window wnd, final List<ITab> tabs) {
		export(wnd, tabs, new ITabFilter() {

			public boolean filter(final ITab tab) {
				return false;
			}
		});
	}

	static public final void export(final Window wnd, final List<ITab> tabs, final ITabFilter filter) {

		final StringBuilder c = new StringBuilder();
		for (final ITab t : tabs) {
			if (filter.filter(t))
				continue;
			c.append("<h2>" + t.getName() + "</h2>\n");
			c.append("<div class='qtab' id='" + t.getName().replace('\'', '"') +"'>\n");
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
			fc.setSelectedFile(new File(filename.replace(' ', '-').replace(':', '-').toLowerCase() + ".htm"));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fc.showOpenDialog(wnd) == JFileChooser.APPROVE_OPTION) {

				if (fc.getSelectedFile().exists()
						&& JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(wnd, ""
							+ fc.getSelectedFile().getName()
							+ " exists. Overwrite?", "Overwirte Existing File",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE))
					return;

				FileSaver.write(c, fc.getSelectedFile(), true);
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
			public boolean accept(final File f) {
				if (f.isDirectory())
					return true;
				try {
					final String ext = f.getName().substring(f.getName().length() - 3).toLowerCase();
					return "htm".equals(ext) || "tml".equals(ext) || ".gz".equals(ext);
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
		private Pattern tabStartPttrn;
		private Pattern msgPttrn;

		public SessionParser(final TabController tc, final File file) {
			this.tc = tc;
			this.file = file;

			tabStartPttrn = Pattern.compile("<div\\s+class='qtab'\\s+id='(.*?)'\\s*>");
			msgPttrn = Pattern.compile("<p\\s*style='background-color:(.{7})'\\s*>((?s).*?)</p>");
		}

		@Override
		protected Integer doInBackground() throws Exception {
			tc.removeAllTabs();

			BufferedReader r = null;
			try {
				if (file.getName().endsWith("gz"))
					r = new BufferedReader(new InputStreamReader(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), "UTF-8"));
				else
					r = new BufferedReader(new FileReader(file));
				String line;
				Matcher tm, mm;
				ITab tab = null;
				while ( (line = r.readLine()) !=  null ) {
					tm = tabStartPttrn.matcher(line);
					if (tm.matches()) {
						tab = TabFactory.createTab(new IDestroyable() {
							public void destroy() {}
							}, tm.group(1));
						tab.incomingCommand("clearonconnect");
						continue;
					}

					if (tab == null)
						continue;

					mm = msgPttrn.matcher(line);
					while (mm.find())
						tab.incomingMessage(mm.group(1), mm.group(2));
				}
			} finally {
				try { r.close(); } catch (Exception ex) {}
			}
			return 0;
		}
	}
}
