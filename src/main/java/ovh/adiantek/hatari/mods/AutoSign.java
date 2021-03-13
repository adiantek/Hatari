package ovh.adiantek.hatari.mods;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import ovh.adiantek.hatari.Modification;
import ovh.adiantek.hatari.SettingsWindow;
import ovh.adiantek.hatari.windows.Categories;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AutoSign extends Modification implements DocumentListener {
	private int buttonId = getNextButtonID();
	private List<GuiButton> buttonList;
	private GuiEditSign gui;
	private String[] lines = getObject("lines", new String[]{"","","",""});

	public void update(DocumentEvent e) {
		int line = (Integer) e.getDocument().getProperty("line");
		try {
			int length = Math.min(15, e.getDocument().getLength());
			lines[line] = e.getDocument().getText(0, length);
			setObject("lines", lines);
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

	private static class LimitDocument extends PlainDocument {

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if ((getLength() + str.length()) >= 15) {
				if(getLength()==15)
					str="";
				else
					str=str.substring(0, 15-getLength());
			}
			if(str.length()>0)
				super.insertString(offs, str, a);
		}
	}
	@Override
	public JComponent openConfig() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		String[] labels = new String[] { "1st line:", "2nd line:", "3rd line:",
				"4th line:" };
		for (int i = 0; i < 4; i++) {
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 0.0D;
			panel.add(new JLabel(labels[i]), constraints);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0D;
			JTextField field = new JTextField();
			field.setComponentPopupMenu(SettingsWindow.getPopupMenu(field));
			field.setDocument(new LimitDocument());
			field.getDocument().putProperty("line", i);
			field.getDocument().addDocumentListener(this);
			field.setText(lines[i]);
			panel.add(field, constraints);
			constraints.gridy++;
		}

		return new JScrollPane(panel);
	}

	@Override
	public void resetConfig() {
		setEnabledState(false);
		lines = setObject("lines", new String[]{"","","",""});
	}

	private void refresh() {
		if (buttonList == null)
			return;
		if (!(mc.currentScreen instanceof GuiEditSign))
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
	private GuiButton createButton() {
		return new GuiButton(buttonId, gui.width / 2 - 100,
				gui.height / 4 + 120 + 24, "Set Auto-Sign");
	}

	public AutoSign() {
		super(AutoSign.class, Categories.WORLD, "Auto-Sgin");
		MinecraftForge.EVENT_BUS.register(this);
		this.addToggleCommand("autosign", "Fills in a sign for you");
	}

	@Override
	protected boolean onEnable() {
		return true;
	}

	@Override
	protected boolean onDisable() {
		return true;
	}

	@Override
	public void hide() {
		refresh();
	}

	@Override
	public void show() {
		refresh();
	}

	@Executor
	public void event(String command){
		toggle();
	}

	@SubscribeEvent
	public void event(GuiScreenEvent.ActionPerformedEvent.Post bt) {
		if (bt.button.id == buttonId) {
			lines = setObject("lines", gui.tileSign.signText.clone());
			viewMessage("Sign content set");
		}
	}

	@SubscribeEvent
	public void event(GuiScreenEvent.InitGuiEvent.Post s) {
		if (s.gui instanceof GuiEditSign) {
			if (isEnabled()) {
				GuiEditSign ges = (GuiEditSign) s.gui;
				ges.tileSign.signText = lines.clone();
			}
			gui = (GuiEditSign) s.gui;
			buttonList = s.buttonList;
			refresh();
		}
	}
}
