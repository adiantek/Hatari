package ovh.adiantek.hatari.mods;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.CommandManager.CommandValidator;
import ovh.adiantek.hatari.CommandManager.IntegerValidator;
import ovh.adiantek.hatari.CommandManager.StringValidator;
import ovh.adiantek.hatari.Modification;

// TODO end this
public class Binds extends Modification {
	private static final String[] KEYS;

	public Binds() {
		super(Binds.class, "Binds");
		addToggleCommand("bind list", "List all keyboard binds");
		CommandManager
				.createNewCommand()
				.setCommand("bind add")
				.setDescription("Add new keyboard bind")
				.setExecutor(this)
				.setRequestArguments(
						new CommandValidator[] { new StringValidator(),
								new StringValidator() },
						new String[] { "key", "command" }, true);
		CommandManager
				.createNewCommand()
				.setCommand("bind del")
				.setDescription("Delete keyboard bind")
				.setExecutor(this)
				.setRequestArguments(
						new CommandValidator[] { new IntegerValidator() },
						new String[] { "id" }, true);
		CommandManager
		.createNewCommand()
		.setCommand("bind del key")
		.setDescription("Delete all binds from specifed key")
		.setExecutor(this)
		.setRequestArguments(
				new CommandValidator[] { 
						new StringValidator() },
				new String[] {"key" }, true);
		CommandManager
				.createNewCommand()
				.setCommand("bind list")
				.setDescription("List all binds from key")
				.setExecutor(this)
				.setRequestArguments(
						new CommandValidator[] { new StringValidator() },
						new String[] { "key" }, false);
	}

	private void getDefaultBinds() {
		ArrayList<Bind> binds = new ArrayList<Bind>();
	}

	private class Bind implements Serializable {
		int keyNumber;
		String command;
		Bind(int key, String command) {
			this.keyNumber=key;
			this.command=command;
		}
	}

	static {
		try {
			Field f = Keyboard.class.getDeclaredField("keyName");
			f.setAccessible(true);
			KEYS = (String[]) f.get(null);
		} catch (NoSuchFieldException e) {
			throw new Error(e);
		} catch (SecurityException e) {
			throw new Error(e);
		} catch (IllegalArgumentException e) {
			throw new Error(e);
		} catch (IllegalAccessException e) {
			throw new Error(e);
		}

	}
}
