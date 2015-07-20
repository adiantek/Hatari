package ovh.adiantek.hatari;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ovh.adiantek.hatari.windows.WindowHub;

public class Configurator {
	private static final Logger l = LogManager.getLogger();
	private final String className;
	private static boolean isLoaded = false;
	public Configurator(Class<?> parent) {
		className=parent.getName()+"\t";
		if(!isLoaded)
			l.error("Warning: Configurator not loaded!");
	}
	private static TreeMap<String,Serializable> obj = new TreeMap<String,Serializable>() {
		@Override
		public Serializable put(String key, Serializable value) {
			super.put(key, value);
			return value;
		}
	};
	static void loadConfig(final File config) {
		try {
			FileInputStream fis = new FileInputStream(config);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			if(o!=null) {
				obj = (TreeMap<String, Serializable>) o;
				System.out.println(obj);
			} else {
				l.error("[H] Can't read config: ois.readObject() is null!");
			}
			ois.close();
			fis.close();
			l.info("[H] Readed settings!");
		} catch (Throwable e) {
			l.error("[H] Can't read config",e);
		}
		isLoaded=true;
		Runtime.getRuntime().addShutdownHook(new Thread("SavingSettings"){
			public void run() {
				try {
					WindowHub.save();
					Modification.saveAll();
					FileOutputStream fos = new FileOutputStream(config);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(obj);
					oos.close();
					fos.close();
					l.info("Saved settings!");
					
				} catch(Throwable t){
					l.error("Cannot save config", t);
				}
			}
		});
		
	}

	public final String getString(String key, String def){
		if(obj.get(className+key)==null)return setString(className+key,def);
		else return (String)obj.get(className+key);
	}
	public final String setString(String key, String value) {
		return (String) obj.put(className+key, value);
	}
	public final long getLong(String key, long def) {
		if(obj.get(className+key)==null)return setLong(className+key,def);
		else return (Long)obj.get(className+key);
	}
	public final long setLong(String key, long value) {
		return (Long) obj.put(className+key, value);
	}
	public final int getInteger(String key, int def) {
		if(obj.get(className+key)==null)return setInteger(className+key,def);
		else return (Integer)obj.get(className+key);
	}
	public final int setInteger(String key, int value) {
		return (Integer) obj.put(className+key, value);
	}
	public final short getShort(String key, short def) {
		if(obj.get(className+key)==null)return setShort(className+key,def);
		else return (Short)obj.get(className+key);
	}
	public final short setShort(String key, short value) {
		return (Short) obj.put(className+key, value);
	}
	public final boolean getBoolean(String key, boolean def) {
		if(obj.get(className+key)==null)return setBoolean(className+key,def);
		else {
			System.out.println(key+" - "+obj.get(className+key));
			return (Boolean)obj.get(className+key);
		}
	}
	public final boolean setBoolean(String key, boolean value) {
		return (Boolean) obj.put(className+key, value);
	}
	public final float getFloat(String key, float def) {
		if(obj.get(className+key)==null)return setFloat(className+key,def);
		else return (Float)obj.get(className+key);
	}
	public final float setFloat(String key, float value) {
		return (Float) obj.put(className+key, value);
	}
	public final double getDouble(String key, double def) {
		if(obj.get(className+key)==null)return setDouble(className+key,def);
		else return (Double)obj.get(className+key);
	}
	public final double setDouble(String key, double value) {
		return (Double) obj.put(className+key, value);
	}
	public final char getCharacter(String key, char def) {
		if(obj.get(className+key)==null)return setCharacter(className+key,def);
		else return (Character)obj.get(className+key);
	}
	public final char setCharacter(String key, char value) {
		return (Character) obj.put(className+key, value);
	}
	public final <T extends Serializable> T getObject(String key, T def) {
		if(obj.get(className+key)==null)return setObject(className+key,def);
		else return (T)obj.get(className+key);
	}
	public final <T extends Serializable> T setObject(String key, T value) {
		return (T) obj.put(className+key, value);
	}
}
