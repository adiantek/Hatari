package ovh.adiantek.hatari.mods;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import ovh.adiantek.hatari.CommandManager;
import ovh.adiantek.hatari.Modification;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Friends extends Modification implements ActionListener {
	private TreeSet<String> friends = getObject("friends",
			new TreeSet<String>());
	private TreeSet<String> players = getObject("players",
			new TreeSet<String>());
	private SortableListModel playersList;
	private SortableListModel friendsList;
	private List<EntityPlayer> toParse = Collections
			.synchronizedList(new ArrayList<EntityPlayer>());
	private JList playersListComponent;
	private JPanel panel;
	private JList friendsListComponent;
	private JTextField addCustomField;
	private JButton addButton;
	private JButton removeButton;
	public static Friends instance;

	public Friends() {
		super(Friends.class, "Friends");
		instance = this;
		MinecraftForge.EVENT_BUS.register(this);
		CommandManager
				.createNewCommand()
				.setCommand("friends add")
				.setDescription("Add player(s) to friends list")
				.setExecutor(this)
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.EntityPlayerValidator() },
						new String[] { "username" }, true);
		CommandManager
				.createNewCommand()
				.setCommand("friends del")
				.setDescription("Delete player(s) to friends list")
				.setExecutor(this)
				.setRequestArguments(
						new CommandManager.CommandValidator[] { new CommandManager.EntityPlayerValidator() },
						new String[] { "username" }, true);
		FMLCommonHandler.instance().bus().register(this);
		addToggleCommand("friends list", "List your friends.");
	}

	public boolean isFriend(String nickname) {
		return friends.contains(nickname);
	}

	@SubscribeEvent
	public void event(TickEvent.ClientTickEvent e) {
		while (toParse.size() > 0) {
			if(toParse.get(0).getGameProfile()!=null)
				insertPlayer(toParse.remove(0).getCommandSenderName());
		}
	}

	@SubscribeEvent
	public void event(EntityEvent.EntityConstructing e) {
		if (e.entity instanceof EntityPlayer) {
			toParse.add((EntityPlayer) e.entity);
		}

	}
	public static String getName(Class<?> entity) {
		Object codePre = EntityList.classToStringMapping.get(entity);
		if(codePre==null)
			return entity.getSimpleName();
		String code = "entity."+codePre+".name";
		String translated = I18n.format(code, new Object[0]);
		if(code.equals(translated)) {
			return codePre.toString();
		}
		return translated;
	}
	private JScrollPane createEntitiesTree() {

		ArrayList<Class<?>> customs = new ArrayList<Class<?>>();
		Map<Class<?>, String> mapa = EntityList.classToStringMapping;
		HashMap<Class<?>, DefaultMutableTreeNode> ref = new HashMap<Class<?>, DefaultMutableTreeNode>();
		for(Class<?> cl : mapa.keySet()) {
			Class<?> curr = cl;
			while(curr!=null) {
				ref.put(curr, new DefaultMutableTreeNode(getName(curr)));
				curr=curr.getSuperclass();
			}
		}
		{
			Class<?> curr = EntityPlayer.class;
			while(curr!=null) {
				ref.put(curr, new DefaultMutableTreeNode(getName(curr)));
				curr=curr.getSuperclass();
			}
		}
		for(Class<?> cl : mapa.keySet()) {
			Class<?> currNode = cl;
			Class<?> currParent = currNode.getSuperclass();
			while(currParent!=null) {
				ref.get(currParent).add(ref.get(currNode));
				currNode=currParent;
				currParent=currNode.getSuperclass();
				
			}
		}
		{
			Class<?> currNode = EntityPlayer.class;
			Class<?> currParent = currNode.getSuperclass();
			while(currParent!=null) {
				ref.get(currParent).add(ref.get(currNode));
				currNode=currParent;
				currParent=currNode.getSuperclass();
				
			}
		}
		return new JScrollPane(new JTree(ref.get(EntityLivingBase.class)));
	}
	private JComponent createPanelFriends() {
		if (panel != null) {
			if (panel.getParent() != null)
				panel.getParent().remove(panel);
			return panel;
		}
		if (friendsList == null)
			friendsList = new SortableListModel(friends);
		if (playersList == null)
			playersList = new SortableListModel(players);
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5D;
		c.gridheight = 2;
		c.gridx = 0;
		c.weighty = 1;
		friendsListComponent = new JList(this.friendsList);
		friendsListComponent.setVisibleRowCount(1);
		friendsListComponent.setLayoutOrientation(JList.VERTICAL);
		friendsListComponent
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		addButton = new JButton("<< Add");
		removeButton = new JButton("Delete >>");
		addButton.setName("add");
		removeButton.setName("del");
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		playersListComponent = new JList(this.playersList);
		playersListComponent.setVisibleRowCount(1);
		playersListComponent.setLayoutOrientation(JList.VERTICAL);
		playersListComponent
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane playersScroll = new JScrollPane(playersListComponent);
		JScrollPane friendsScroll = new JScrollPane(friendsListComponent);
		JPanel playersPanel = new JPanel(new BorderLayout());
		addCustomField = new JTextField();
		addCustomField.setName("addCustom");
		JButton addCustomButton = new JButton("Add");
		addCustomButton.addActionListener(this);
		addCustomField.addActionListener(this);
		addCustomButton.setName("addCustom");
		JPanel addCustomPanel = new JPanel(new BorderLayout());
		addCustomPanel.add(addCustomField, BorderLayout.CENTER);
		addCustomPanel.add(addCustomButton, BorderLayout.EAST);
		playersPanel.add(addCustomPanel, BorderLayout.SOUTH);
		playersPanel.setBorder(BorderFactory.createTitledBorder("Players:"));
		friendsScroll.setBorder(BorderFactory.createTitledBorder("Friends:"));
		playersPanel.add(playersScroll, BorderLayout.CENTER);
		panel.add(friendsScroll, c);
		c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.weighty = .25;
		c.weightx = 0D;
		c.gridx = 1;
		panel.add(addButton, c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5D;
		c.gridx = 2;
		c.gridheight = 2;
		c.weighty = 1;
		panel.add(playersPanel, c);
		c.gridy = 1;
		c.gridheight = 1;
		c.weighty = .25;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.weightx = 0D;
		panel.add(removeButton, c);
		return panel;
	}

	@Override
	public JComponent openConfig() {
		JTabbedPane jtp = new JTabbedPane(JTabbedPane.BOTTOM);
		jtp.addTab("Players", createPanelFriends());
		jtp.addTab("Entities", createEntitiesTree());
		return jtp;
	}

	@Override
	public void resetConfig() {
		friends = new TreeSet<String>();
	}

	@Override
	protected void save() {
		setObject("friends", friends);
	}

	private void insertPlayer(String player) {
		if (player != null && !players.contains(player)) {
			players.add(player);
			if (playersList != null)
				playersList.change();
		}
	}

	@Executor
	public void event(String command, EntityPlayer[] player) {
		if (player.length == 0) {
			viewMessage("Player not found or not specifed!");
			return;
		}
		int index = 0;
		for (EntityPlayer p : player) {
			if (p == null) {
				viewMessage("Player at argument " + index + " not found");
			} else if (command.equals("friends add")) {
				if (friends.contains(p.getCommandSenderName())) {
					viewMessage(p.getCommandSenderName()
							+ " is already in friends list!");
					return;
				}
				friends.add(p.getCommandSenderName());
				insertPlayer(p.getCommandSenderName());
				if (friendsList != null)
					friendsList.change();
				viewMessage("Added " + p.getCommandSenderName()
						+ " to friends list.");
			} else if (command.equals("friends del")) {
				if (!friends.contains(p.getCommandSenderName())) {
					viewMessage(p.getCommandSenderName()
							+ " is not in friends list!");
					return;
				}
				friends.remove(p.getCommandSenderName());
				insertPlayer(p.getCommandSenderName());
				if (friendsList != null)
					friendsList.change();
				viewMessage("Deleted " + p.getCommandSenderName()
						+ " from friends list.");
			}
			index++;
		}
	}

	@Executor
	public void event(String command) {
		if (friends.size() == 0) {
			viewMessage("You don't have any friends.");
			return;
		}
		viewMessage("Total friends: " + friends.size());
		StringBuilder sb = new StringBuilder();
		for (String friend : friends) {
			sb.append(", ").append(friend);
		}
		viewMessage(sb.substring(2));
	}

	private class SortableListModel extends AbstractListModel {
		private TreeSet<String> list;

		private SortableListModel(TreeSet<String> list) {
			this.list = list;
		}

		private void change() {
			fireContentsChanged(this, 0, getSize());
		}

		@Override
		public int getSize() {
			return list.size();
		}

		@Override
		public Object getElementAt(int index) {
			Iterator<String> it = list.iterator();
			while (index != 0) {
				it.next();
				index--;
			}
			return it.next();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComponent comp = (JComponent) e.getSource();
		if (comp.getName().equals("addCustom")) {
			if (friends.contains(addCustomField.getText())) {
				JOptionPane.showMessageDialog(null,
						"Player " + addCustomField.getText()
								+ " is already in friends!", "Friends",
						JOptionPane.ERROR_MESSAGE);
			}
			if (addCustomField.getText().trim().length() == 0)
				return;
			insertPlayer(addCustomField.getText().trim());
			friends.add(addCustomField.getText().trim());
			if (friendsList != null)
				friendsList.change();
			addCustomField.setText("");
		} else if (comp.getName().equals("add")) {
			try {
				Object player = playersListComponent.getSelectedValue();
				if (player != null) {
					if(friends.contains(player.toString())){
						JOptionPane.showMessageDialog(null,
									"Player " + addCustomField.getText()
											+ " is already in friends!", "Friends",
									JOptionPane.ERROR_MESSAGE);
						return;
					}
					friends.add(player.toString());
					friendsList.change();
				}
			} catch (NoSuchElementException e2) {

			}
		} else if (comp.getName().equals("del")) {
			try {
				Object player = friendsListComponent.getSelectedValue();
				if (player != null) {
					insertPlayer(player.toString());
					friends.remove(player.toString());
					friendsList.change();
				}
			} catch (NoSuchElementException e2) {

			}

		}
	}
}
