package ovh.adiantek.hatari.mods;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.Session;
import net.minecraft.util.Session.Type;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.SettingsWindow;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ChangeUsername extends Modification implements DocumentListener, ItemListener, ActionListener {
	private List<GuiButton> buttonList;
	private GuiMultiplayer guiMultiplayer;
	private Session session = mc.getSession();
	private JTextField uuidField;
	private int buttonId = getNextButtonID();

	public ChangeUsername() {
		super(ChangeUsername.class, "Change username");
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void refresh() {
		if (buttonList == null)
			return;
		if (!(mc.currentScreen instanceof GuiMultiplayer))
			return;
		if (isShouldVisible()) {
			for (GuiButton gb : buttonList) {
				if (gb.id == buttonId) {
					return;
				}
			}
			buttonList.add(createButton());
		}
	}

	public void update(DocumentEvent e) {
		int act = (Integer) e.getDocument().getProperty("field");
		try {
			String text = e.getDocument().getText(0, e.getDocument().getLength());
			if(act==0)
				session.username=text;
			else if(act==1)
				session.playerID=text;
			else
				session.token=text;
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		
	}
	@Override
	public void insertUpdate(DocumentEvent e) {
		update(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		update(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		update(e);
	}

	@Override
	public JComponent openConfig() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.gridy = 0;
		String[] labels = new String[]{"Username:", "Player ID:", "Token:"};
		for(int i=0; i<3; i++) {
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 0.0D;
			panel.add(new JLabel(labels[i]), constraints);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0D;
			JTextField field = new JTextField((i==0?session.username:(i==1?session.playerID:session.token)));
			if(i==1)
				uuidField=field;
			field.getDocument().addDocumentListener(this);
			field.setComponentPopupMenu(SettingsWindow.getPopupMenu(field));
			field.getDocument().putProperty("field", i);
			panel.add(field, constraints);
			constraints.gridy++;
		}
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0D;
		panel.add(new JLabel("Session type:"), constraints);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0D;
		JComboBox jcb = new JComboBox();
		Type[] sessions = Type.values();
		for(Type t : sessions) {
			jcb.addItem(t);
		}
		jcb.addItemListener(this);
		jcb.setSelectedItem(session.field_152429_d);
		panel.add(jcb, constraints);
		constraints.gridy++;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.0D;
		constraints.gridwidth = 2;
		JButton button = new JButton("Generate new random Player ID");
		button.addActionListener(this);
		panel.add(button, constraints);
		return new JScrollPane(panel);
	}

	private GuiButton createButton() {
		return new GuiButton(buttonId, guiMultiplayer.width / 2 - 154,
				guiMultiplayer.height - 76, 308, 20, "Change username");
	}

	@Override
	public void hide() {
		refresh();
	}

	@Override
	public void show() {
		refresh();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED)
			session.field_152429_d=(Type) e.getSource();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UUID uuid = UUID.randomUUID();
		session.playerID=uuid.toString();
		if(uuidField!=null)
			uuidField.setText(session.playerID);
	}

	@SubscribeEvent
	public void event(GuiScreenEvent.InitGuiEvent.Post s) {
		if (s.gui instanceof GuiMultiplayer) {
			this.buttonList = s.buttonList;
			this.guiMultiplayer = (GuiMultiplayer) s.gui;
			refresh();
		}
	}

	@SubscribeEvent
	public void event(GuiScreenEvent.ActionPerformedEvent.Post bt) {
		if (bt.button.id == buttonId) {
			SettingsWindow.open(this);
		}
	}
}
