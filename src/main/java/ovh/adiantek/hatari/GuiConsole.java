package ovh.adiantek.hatari;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

public class GuiConsole extends GuiScreen {

	static ArrayList<String> sent = new ArrayList<String>();
	private GuiTextField line;
	private long start = System.currentTimeMillis();
	/**
	 * keeps position of which chat message you will select when you press up,
	 * (does not increase for duplicated messages sent immediately after each
	 * other)
	 */
	private static int sentHistoryCursor = -1;
	private static String field_146410_g = "";
	private ArrayList<String> help;

	public void drawScreen(int par1, int par2, float par3) {
		if(help==null)
			help = CommandManager.getHelpForPrefix(line.getText());
		drawRect(2, 0, this.width-4, help.size()*10+14, 0x80000000);
		int y = 14;
		for(String str : help) {
			this.drawString(fontRendererObj, str, 8, y, 0xffffffff);
			y+=10;
		}
		line.drawTextBox();
	}
	public void getSentHistory(int p_146402_1_) {
		int j = this.sentHistoryCursor + p_146402_1_;
		int k = sent.size();
		if (j < 0) {
			j = 0;
		}

		if (j > k) {
			j = k;
		}

		if (j != this.sentHistoryCursor) {
			if (j == k) {
				this.sentHistoryCursor = k;
				line.setText(this.field_146410_g);
			} else {
				if (this.sentHistoryCursor == k) {
					this.field_146410_g = line.getText();
				}
				this.sentHistoryCursor=j;
				line.setText(sent.get(j));
			}
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */

	protected void keyTyped(char p_73869_1_, int p_73869_2_) {
		if(System.currentTimeMillis()-start<100)
			return;
		if (p_73869_2_ == 1) {
			this.mc.displayGuiScreen((GuiScreen) null);
		} else if (p_73869_2_ != 28 && p_73869_2_ != 156) {
			if (p_73869_2_ == 200) {
				this.getSentHistory(-1);
			} else if (p_73869_2_ == 208) {
				this.getSentHistory(1);
			} else {
				line.textboxKeyTyped(p_73869_1_, p_73869_2_);
				help = CommandManager.getHelpForPrefix(line.getText());
			}
		} else {
			String s = line.getText().trim();

			if (s.length() > 0) {
				if (!CommandManager.invoke(s)) {
					Modification.viewMessage("Command not found!");
				}
			}
			this.mc.displayGuiScreen((GuiScreen) null);
		}
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		sentHistoryCursor=sent.size();
		line = new GuiTextField(fontRendererObj, 2, 2, width - 4, 12);
		line.setMaxStringLength(256);
		line.setEnableBackgroundDrawing(false);
		line.setFocused(true);
		line.setText("");
		line.setCanLoseFocus(false);
	}

	public void updateScreen() {
		line.updateCursorCounter();
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

}
