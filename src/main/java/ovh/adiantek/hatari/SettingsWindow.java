package ovh.adiantek.hatari;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.DefaultEditorKit;

public class SettingsWindow extends JFrame implements ItemListener {
	private static SettingsWindow instance;
	private final JButton doneButton = new JButton("Done");
	private final JButton resetallButton = new JButton("Reset all");
	private JTabbedPane jtp;
	static {
		init();
	}

	public static class SettingsMod extends Modification {
		public SettingsMod() {
			super(SettingsWindow.class, "Settings");
			CommandManager
					.createNewCommand()
					.setCommand("settings")
					.setDescription("Open settings")
					.setExecutor(this)
					.setRequestArguments(
							new CommandManager.CommandValidator[] {},
							new String[] {}, false).register();
		}
		@Executor
		public void event(String command) {
			open(null);
		}

	}

	private static void init() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					return;
				}
			}
		} catch (Throwable e) {
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			return;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
			return;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		instance = new SettingsWindow();
	}

	public static JPopupMenu getPopupMenu(JTextField jtf) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem cut = new JMenuItem("Cut");
		JMenuItem paste = new JMenuItem("Paste");
		JMenuItem select = new JMenuItem("Select all");
		Action copyAction = jtf.getActionMap().get(DefaultEditorKit.copyAction);
		Action cutAction = jtf.getActionMap().get(DefaultEditorKit.cutAction);
		Action pasteAction = jtf.getActionMap().get(
				DefaultEditorKit.pasteAction);
		Action selectAllAction = jtf.getActionMap().get(
				DefaultEditorKit.selectAllAction);
		copy.addActionListener(copyAction);
		cut.addActionListener(cutAction);
		paste.addActionListener(pasteAction);
		select.addActionListener(selectAllAction);
		popup.add(cut);
		popup.add(copy);
		popup.add(paste);
		popup.addSeparator();
		popup.add(select);
		return popup;
	}

	public static void open(Modification tab) {
		if(instance!=null) {
			instance.setVisible(false);
			instance.dispose();
		}
		instance = new SettingsWindow();
		instance.setVisible(true);
		instance.config(tab);
	}

	private void config(final Modification tab) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				jtp.removeAll();
				for (Modification m : Modification.modifications) {
					JComponent comp = m.openConfig();
					if (comp != null)
						jtp.addTab(m.name, comp);
					if (tab == m)
						jtp.setSelectedIndex(jtp.getTabCount() - 1);
				}
			}});
	}

	private SettingsWindow() {
		setSize(640, 480);
		setTitle("Settings");
		setLocationRelativeTo(null);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 5));
		JPanel buttonPannel = new JPanel();
		JLabel lookAndFeel = new JLabel("L&F:");
		JComboBox jcb = new JComboBox();
		for (LookAndFeelInfo lf : UIManager.getInstalledLookAndFeels()) {
			JLabel lab = new JLabel(lf.getName()) {
				@Override
				public String toString() {
					return getText();
				}

			};
			lab.setName(lf.getClassName());
			jcb.addItem(lab);
			if (UIManager.getLookAndFeel().getName().equals(lf.getName())) {
				jcb.setSelectedItem(lab);
			}
		}
		jcb.addItemListener(this);
		buttonPannel.setLayout(new BoxLayout(buttonPannel, 0));
		buttonPannel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPannel.add(lookAndFeel);
		buttonPannel.add(Box.createHorizontalStrut(5));
		buttonPannel.add(jcb);
		buttonPannel.add(Box.createGlue());
		buttonPannel.add(this.resetallButton);
		buttonPannel.add(Box.createHorizontalStrut(5));
		buttonPannel.add(this.doneButton);
		panel.add(buttonPannel, BorderLayout.SOUTH);
		panel.add(jtp = new JTabbedPane(), BorderLayout.CENTER);
		add(panel);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		JLabel lab = (JLabel) e.getItem();
		try {
			UIManager.setLookAndFeel(lab.getName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}

	}
}
