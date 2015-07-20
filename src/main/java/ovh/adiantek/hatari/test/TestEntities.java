package ovh.adiantek.hatari.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TestEntities {
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
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		JFrame f = new JFrame();
		JTree tree = new JTree(ref.get(EntityLivingBase.class));
		f.add(new JScrollPane(tree));
		f.setSize(858, 480);
		f.setLocationByPlatform(true);
		f.setVisible(true);
	}
}
