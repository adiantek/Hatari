package ovh.adiantek.hatari;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class CommandManager {
	public static interface CommandValidator<T> {
		public boolean isValid(String arg);

		public boolean isValidForHelp(String arg, boolean isLastArgument);

		public T parse(String arg);

		public String getDisplay();

		public Class<T> getClassValidator();
	}

	public static class Command {
		private static long id = 0;
		private long commandId;
		private CommandValidator[] types;
		private String[] titles;
		private String command;
		private Modification executor;
		private boolean infinityArgs;
		private String description;
		private String builded = "";
		private ArrayList<Method> methods = new ArrayList<Method>();
		private int[] offsets=new int[10];

		private final void rebuild() {
			if (command == null)
				builded = "";
			builded = command;
			int d = 0;
			
			if (types != null && titles != null) {
				for (int i = 0; i < types.length - 1; i++) {
					builded += " \u00A77\u00A7o["
							+ (types[i].getDisplay() == null ? "" : types[i]
									.getDisplay() + " ") + titles[i] + "]";
					offsets[d++]=builded.length();
				}
				if(types.length>0) {
					builded += " \u00A77\u00A7o["
							+ (types[types.length - 1].getDisplay() == null ? ""
									: (types[types.length - 1].getDisplay()))
							+ (infinityArgs ? "... " : " ")
							+ titles[types.length - 1] + "]";
					offsets[d++]=builded.length();
				}
			}
			if (description != null) {
				builded += "\u00A7r\u00A7f \u2013 " + description;
			}
		}
		private String insert(String str, int offset, String insert) {
			return str.replace("\u00A77", "").substring(0, offset)+insert+str.substring(offset);
			
		}
		final String build(String cmd) {
			if(cmd.length()<=command.length()){
				return "\u00A7r\u00A7b"+insert(builded, cmd.length(), "\u00A7r");
			}
			cmd=cmd.substring(command.length()+1);
			int args = cmd.split(" ").length;
			return "\u00A7r\u00A7b"+insert(builded, offsets[Math.min(offsets.length, args)-1], "\u00A7r");
		}
		public final Command register() {
			registerCommand(this);
			return this;
		}
		public final Command setCommand(String command) {
			this.command = command.toLowerCase();
			rebuild();
			return this;
		}

		public final Command setExecutor(Modification executor) {
			this.executor = executor;
			methods.clear();
			Method[] md = executor.getClass().getMethods();
			for(Method m : md) {
				if(m.getAnnotation(Modification.Executor.class)!=null) {
					methods.add(m);
				}
			}
			return this;
		}
		/**
		 * 
		 * @param types
		 * @param titles 
		 * @param infinityArgs make last argument array
		 * @return
		 */
		public final Command setRequestArguments(CommandValidator[] types,
				String[] titles, boolean infinityArgs) {
			this.titles = titles;
			this.types = types;
			this.infinityArgs = infinityArgs;
			offsets=new int[types.length];
			rebuild();
			return this;
		}

		public final Command setDescription(String description) {
			this.description = description;
			rebuild();
			return this;
		}

		public final String getLine() {
			return builded;
		}
		final private Object[] parse1(String command, String[] args) {
			if(!infinityArgs)
				return null;
			if(args.length<types.length)
				return null;
			Object[] parsed = new Object[types.length+1];
			parsed[0]=command;
			for(int i=0; i<parsed.length-2; i++) {
				if(!types[i].isValid(args[i])) {
					return null;
				}
				parsed[i+1]=types[i].parse(args[i]);
			}
			Object array = Array.newInstance(types[types.length-1].getClassValidator(), args.length-types.length+1);
			for(int i=parsed.length-2, j=0; i<args.length; i++, j++) {
				if(!types[types.length-1].isValid(args[i])) {
					return null;
				}
				Array.set(array, j, types[types.length-1].parse(args[i]));
			}
			parsed[parsed.length-1]=array;
			return parsed;
		}
		final private Object[] parse2(String command, String[] args) {
			if(infinityArgs)
				return null;
			if(args.length!=types.length)
				return null;
			Object[] parsed = new Object[types.length+1];
			parsed[0]=command;
			for(int i=0; i<args.length; i++) {
				if(!types[i].isValid(args[i])) {
					return null;
				}
				parsed[i+1]=types[i].parse(args[i]);
			}
			return parsed;
		}
		
		final boolean _invoke(String cmd) {
			if (!(cmd+" ").toLowerCase().startsWith(command.toLowerCase()+" "))
				return false;
			String[] args;
			if(cmd.length()>command.length()) {
				args=cmd.substring(1+command.length()).split(" ");
			} else {
				args=new String[]{};
			}
			Object[] parsed = infinityArgs?parse1(cmd, args):parse2(cmd, args);
			if(parsed==null)
				return false;
			for(Method m : methods) {
				try {
					m.invoke(executor, parsed);
					return true;
				} catch (IllegalAccessException e) {
					Modification.viewMessage("Warning: "+executor+" contains inaccessible method with @Executor! Did you forget make it public?");
					Modification.viewMessage(m.toGenericString());
					return true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Modification.viewMessage("Error: command threw exception: "+e.getCause());
					Modification.viewMessage("See more in log file.");
					Modification.LOG.error("Command threw exception.", e);
					return true;
				}
			}
			Modification.viewMessage("Error: executor method can't be found.");
			if(methods.size()==0) {
				Modification.viewMessage("Executor doesn't contains any executor methods. Did you forget declare it with @Executor?");
			} else {
				Modification.viewMessage("Parameters: "+CommandManager.toString(parsed));
			}
			return true;
		}

		final boolean _isValid(String cmd) {
			if (!command.toLowerCase().startsWith(cmd.toLowerCase()) && !cmd.toLowerCase().startsWith(command.toLowerCase()))
				return false;
			if(cmd.length()<=command.length())
				return true;
			String[] args = cmd.substring(command.length()+1).split(" ");
			if (!infinityArgs && args.length > types.length)
				return false;
			if (infinityArgs) {
				for (int i = 0; i < args.length; i++)
					if (!types[Math.min(types.length-1, i)].isValidForHelp(args[i], i+1==args.length))
						return false;
			} else {
				for (int i = 0; i < args.length; i++)
					if (!types[i].isValidForHelp(args[i], i+1==args.length))
						return false;
			}
			return true;
		}

		private Command() {
			commandId = id++;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Command))
				return false;
			return ((Command) obj).commandId == commandId;
		}
	}

	static List<Command> commands = Collections
			.synchronizedList(new ArrayList<Command>());

	public static Command createNewCommand() {
		return new Command();
	}

	public static void registerCommand(Command command) {
		if (commands.contains(command)) {
			System.out.println(command.builded+" - duplicated");
			return;
		}
		commands.add(command);
	}

	public static class IntegerValidator implements CommandValidator<Integer> {
		@Override
		public boolean isValid(String arg) {
			arg = arg.toLowerCase();
			try {
				if (arg.startsWith("0x")) {
					Integer.parseInt(arg.substring(2), 16);
				} else if (arg.startsWith("0b")) {
					Integer.parseInt(arg.substring(2), 2);
				} else if (arg.startsWith("0")) {
					Integer.parseInt(arg.substring(1), 8);
				} else {
					Integer.parseInt(arg);
				}
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Integer parse(String arg) {
			arg = arg.toLowerCase();
			if (arg.startsWith("0x")) {
				return Integer.parseInt(arg.substring(2), 16);
			} else if (arg.startsWith("0b")) {
				return Integer.parseInt(arg.substring(2), 2);
			} else if (arg.startsWith("0")) {
				return Integer.parseInt(arg.substring(1), 8);
			} else {
				return Integer.parseInt(arg);
			}
		}

		@Override
		public Class<Integer> getClassValidator() {
			return Integer.class;
		}

		@Override
		public String getDisplay() {
			return "int";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg);
		}
	}

	public static class LongValidator implements CommandValidator<Long> {
		@Override
		public boolean isValid(String arg) {
			arg = arg.toLowerCase();
			try {
				if (arg.startsWith("0x")) {
					Long.parseLong(arg.substring(2), 16);
				} else if (arg.startsWith("0b")) {
					Long.parseLong(arg.substring(2), 2);
				} else if (arg.startsWith("0")) {
					Long.parseLong(arg.substring(1), 8);
				} else {
					Long.parseLong(arg);
				}
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Long parse(String arg) {
			arg = arg.toLowerCase();
			if (arg.startsWith("0x")) {
				return Long.parseLong(arg.substring(2), 16);
			} else if (arg.startsWith("0b")) {
				return Long.parseLong(arg.substring(2), 2);
			} else if (arg.startsWith("0")) {
				return Long.parseLong(arg.substring(1), 8);
			} else {
				return Long.parseLong(arg);
			}
		}

		@Override
		public Class<Long> getClassValidator() {
			return Long.class;
		}

		@Override
		public String getDisplay() {
			return "long";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg);
		}
	}

	public static class ShortValidator implements CommandValidator<Short> {

		@Override
		public boolean isValid(String arg) {
			arg = arg.toLowerCase();
			try {
				if (arg.startsWith("0x")) {
					Short.parseShort(arg.substring(2), 16);
				} else if (arg.startsWith("0b")) {
					Short.parseShort(arg.substring(2), 2);
				} else if (arg.startsWith("0")) {
					Short.parseShort(arg.substring(1), 8);
				} else {
					Short.parseShort(arg);
				}
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Short parse(String arg) {
			arg = arg.toLowerCase();
			if (arg.startsWith("0x")) {
				return Short.parseShort(arg.substring(2), 16);
			} else if (arg.startsWith("0b")) {
				return Short.parseShort(arg.substring(2), 2);
			} else if (arg.startsWith("0")) {
				return Short.parseShort(arg.substring(1), 8);
			} else {
				return Short.parseShort(arg);
			}
		}

		@Override
		public Class<Short> getClassValidator() {
			return Short.class;
		}

		@Override
		public String getDisplay() {
			return "short";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg);
		}
	}

	public static class DoubleValidator implements CommandValidator<Double> {

		@Override
		public boolean isValid(String arg) {
			try {
				arg = arg.replace(',', '.').toLowerCase();
				Double.parseDouble(arg);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Double parse(String arg) {
			return Double.parseDouble(arg);
		}

		@Override
		public Class<Double> getClassValidator() {
			return Double.class;
		}

		@Override
		public String getDisplay() {
			return "double";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg);
		}
	}

	public static class StringValidator implements CommandValidator<String> {

		@Override
		public boolean isValid(String arg) {
			return true;
		}

		@Override
		public String parse(String arg) {
			return arg;
		}

		@Override
		public Class<String> getClassValidator() {
			return String.class;
		}

		@Override
		public String getDisplay() {
			return "String";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return true;
		}
	}

	public static class PercentValidator implements CommandValidator<Double> {
		private double scale;
		public PercentValidator(double scale) {
			this.scale=scale;
		}
		public PercentValidator() {
			this(1);
		}
		@Override
		public boolean isValid(String arg) {
			try {
				if(!arg.endsWith("%"))
					return false;
				arg = arg.substring(0, arg.length()-1).replace(',', '.');
				Double.parseDouble(arg);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Double parse(String arg) {
			arg = arg.substring(0, arg.length()-1).replace(',', '.');
			return Double.parseDouble(arg)*scale/100d;
		}

		@Override
		public Class<Double> getClassValidator() {
			return Double.class;
		}

		@Override
		public String getDisplay() {
			return "%";
		}
		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg) || (isLastArgument&&isValid(arg+"%"));
		}
	}

	public static class FloatValidator implements CommandValidator<Float> {

		@Override
		public boolean isValid(String arg) {
			try {
				arg = arg.replace(',', '.');
				Float.parseFloat(arg);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		}

		@Override
		public Float parse(String arg) {
			arg = arg.replace(',', '.');
			return Float.parseFloat(arg);
		}

		@Override
		public Class<Float> getClassValidator() {
			return Float.class;
		}

		@Override
		public String getDisplay() {
			return "float";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return isValid(arg);
		}
	}

	public static class EntityPlayerValidator implements
			CommandValidator<EntityPlayer> {

		@Override
		public boolean isValid(String arg) {
			return true;
		}

		@Override
		public EntityPlayer parse(String arg) {
			return Modification.findPlayer(arg);
		}

		@Override
		public Class<EntityPlayer> getClassValidator() {
			return EntityPlayer.class;
		}

		@Override
		public String getDisplay() {
			return "player";
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			return true;
		}
	}

	public static class StricteArgument implements CommandValidator<String> {
		private String text;
		private boolean ignoreCase = true;

		public StricteArgument(String text) {
			this.text = text;
		}

		public StricteArgument(String text, boolean ignoreCase) {
			this.text = text;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public boolean isValid(String arg) {
			return ignoreCase ? text.equalsIgnoreCase(arg) : text.equals(arg);

		}

		@Override
		public String parse(String arg) {
			return (ignoreCase ? text.equalsIgnoreCase(arg) : text.equals(arg)) ? arg
					: null;
		}

		@Override
		public String getDisplay() {
			return null;
		}

		@Override
		public Class<String> getClassValidator() {
			return String.class;
		}

		@Override
		public boolean isValidForHelp(String arg, boolean isLastArgument) {
			if(isLastArgument) {

				return ignoreCase ? text.toLowerCase().startsWith(arg.toLowerCase()) : text.startsWith(arg);
			} else
				return isValid(arg);
		}

	}
	private static String toString(Object a) {
		if(a==null)
			return "null";
		if(a.getClass().isArray())
			return toStringArray(a);
		else
			return a.toString();
	}
	private static String toStringArray(Object a) {
		int iMax = Array.getLength(a) - 1;
			if (iMax == -1)
				return "[]";
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; ; i++) {
			b.append(toString(Array.get(a, i)));
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}
	public static ArrayList<String> getHelpForPrefix(String cmd) {
		cmd=cmd.trim();
		ArrayList<String> lista = new ArrayList<String>();
		for(Command c : commands) {
			if(c._isValid(cmd))
				lista.add(c.build(cmd));
		}
		return lista;
	}
	public static boolean invoke(String s) {
		s=s.trim();
		GuiConsole.sent.add(s);
		Modification.LOG.info(">"+s);
		for(Command c : commands) {
			if(c._invoke(s))
				return true;
		}
		return false;
	}
}
