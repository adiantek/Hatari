package ovh.adiantek.hatari.mods;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;

import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.SettingsWindow;

public class Console extends Modification implements ActionListener {
	private JPanel p;
	private static PlainDocument console = new PlainDocument();

	public Console() {
		super(Console.class, "Console");
		addToggleCommand("console", "Show Hatari Console");
	}

	@Executor
	public void event(String cmd) {
		SettingsWindow.open(this);
	}

	@Override
	public JComponent openConfig() {
		if (p != null)
			return p;
		p = new JPanel(new BorderLayout());
		JTextArea console = new JTextArea(Console.console);
		JScrollPane jsp = new JScrollPane(console);
		console.setEditable(false);
		JTextField cmd = new JTextField();
		p.add(cmd, BorderLayout.SOUTH);
		p.add(jsp, BorderLayout.CENTER);
		cmd.addActionListener(this);
		return p;
	}

	public static class MyAppender extends AbstractAppender {
		protected MyAppender() {
			super("Swing Console Appender", new Filter() {
				@Override
				public Result getOnMismatch() {
					return Filter.Result.ACCEPT;
				}

				@Override
				public Result getOnMatch() {
					return Filter.Result.ACCEPT;
				}

				@Override
				public Result filter(Logger logger, Level level, Marker marker,
						String msg, Object... params) {
					return Filter.Result.ACCEPT;
				}

				@Override
				public Result filter(Logger logger, Level level, Marker marker,
						Object msg, Throwable t) {
					return Filter.Result.ACCEPT;
				}

				@Override
				public Result filter(Logger logger, Level level, Marker marker,
						Message msg, Throwable t) {
					return Filter.Result.ACCEPT;
				}

				@Override
				public Result filter(LogEvent event) {
					return Filter.Result.ACCEPT;
				}
			}, PatternLayout.createLayout("[%d{ABSOLUTE}] [%t/%p] %m\n", null,
					null, "UTF-8", "true"), false);
			start();
		}

		public MyAppender(String name, Filter filter,
				Layout<? extends Serializable> layout, boolean ignoreExceptions) {
			super(name, filter, layout, ignoreExceptions);
		}

		@Override
		public void append(LogEvent event) {
			try {
				console.insertString(console.getLength(), getLayout()
						.toSerializable(event).toString(), null);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static {
		((Logger) LogManager.getLogger("H")).addAppender(new MyAppender());
	}

	public static void init() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField jtf = (JTextField) e.getSource();
		String line = jtf.getText();
		if(line.toLowerCase().equals("help") || line.toLowerCase().startsWith("help ")) {
			String add = line.substring(4);
			if(add.startsWith(" "))
				add=add.substring(1);
			ArrayList<String> lista = CommandManager.getHelpForPrefix(add);
			for(String ln : lista) {
				Modification.viewMessage(ln);
			}
		}else if (!CommandManager.invoke(line)) {
			Modification.viewMessage("Command not found: " + jtf.getText());
			Modification.viewMessage("Type \"help\" for help.");
		}
		jtf.setText("");
	}
}
