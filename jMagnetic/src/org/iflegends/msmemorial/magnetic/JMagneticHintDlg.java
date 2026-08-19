/*-----------------------------------------------------------------------------
*   JMagnetic 2 
*   An interpreter for Magnetic Scrolls games
*	
*   based on Magnetic 2.3 written by Niclas Karlsson, David Kinder,
*   Stefan Meier and Paul David Doherty 
*   
*   written by Stefan Meier
*
*   This program is free software; you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.

*   You should have received a copy of the GNU General Public License
*   along with this program; if not, write to the Free Software
*   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*------------------------------------------------------------------------------*/
package org.iflegends.msmemorial.magnetic;
import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Vector;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;

public class JMagneticHintDlg extends javax.swing.JDialog {
	private JTree hintTree;
	private JPanel jPanel1;
	private JScrollPane jScrollPane1;
	private Vector hints;
	private int hintidx=0;
	private DefaultMutableTreeNode root;
	private Integer baseElement = new Integer(0);
	private String gameTag;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				JMagneticHintDlg inst = new JMagneticHintDlg(frame,null,"");
				inst.setVisible(true);
			}
		});
	}
	
	public JMagneticHintDlg(JFrame frame, Vector hints, String gameTag) {
		super(frame);
		this.hints = hints;
		this.gameTag = gameTag;
		// Test
		for (int i=0;i<hints.size();i++)
		{
			JMagneticHint h = (JMagneticHint)hints.elementAt(i);
//			System.out.println(">>> "+i+":");
//			System.out.println("Nodetype: "+h.nodetype);
//			System.out.println("Elements: "+h.elcount);
//			System.out.println("Parent:   "+h.parent);
//			System.out.println("Content:  "+h.content);
//			System.out.println("Links:    "+h.links);
		}
		//
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				jPanel1 = new JPanel();
				BorderLayout jPanel1Layout = new BorderLayout();
				jPanel1.setLayout(jPanel1Layout);
				getContentPane().add(jPanel1, BorderLayout.CENTER);
				jPanel1.setBorder(BorderFactory.createTitledBorder(""));
				{
					jScrollPane1 = new JScrollPane();
					jPanel1.add(jScrollPane1, BorderLayout.CENTER);
					{
						this.setUndecorated(true);
						this.getRootPane().setWindowDecorationStyle(
								JRootPane.FRAME);
						this.getRootPane().setFont(
								UIManager.getFont("SystemFont"));
						this.getRootPane().putClientProperty(
								"Quaqua.RootPane.isVertical", Boolean.FALSE);
						this.getRootPane().putClientProperty(
								"Quaqua.RootPane.isPalette", Boolean.TRUE);
						//FloatingPaletteHandler14.getInstance().addPalette(d);
						this.setSize(300, 400);
						Window w = SwingUtilities.getWindowAncestor(this);
						this.setLocation(w.getX() + w.getWidth(), w.getY());
						this.setTitle("Hints");
						if ((gameTag.equals("mwcorruption")) || (gameTag.indexOf("corrupt") > -1))
							baseElement = new Integer(0);
						else if ((gameTag.equals("mwfish")) || (gameTag.indexOf("fish") > -1))
							baseElement = new Integer(1);
						else if ((gameTag.equals("mwguild")) || (gameTag.indexOf("guild") > -1))
							baseElement = new Integer(2);
						buildTree(hints,baseElement,null);
						hintTree = new JTree(root);
						jScrollPane1.setViewportView(hintTree);
						//hintTree.putClientProperty(
						//		   "Quaqua.Tree.style", "striped"
						//		);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildTree (Vector hints2, Integer idx, DefaultMutableTreeNode owner)
	{
		int i;
		if (owner == null)
		{
		    root = new DefaultMutableTreeNode(((JMagneticHint)hints2.elementAt(0)).content.elementAt(idx.intValue()));
		    buildTree(hints2,(Integer)(((JMagneticHint)hints2.elementAt(0)).links.elementAt(idx.intValue())),root);
		}
		else
		{
		   for (i=0; i < ((JMagneticHint)hints2.elementAt(idx.intValue())).elcount; i++)
		   {
			  if (((JMagneticHint)hints2.elementAt(idx.intValue())).nodetype == 1)
			  {
				  DefaultMutableTreeNode node = new DefaultMutableTreeNode(((JMagneticHint)hints2.elementAt(idx.intValue())).content.elementAt(i)); 
			      owner.add( node );
			      buildTree (hints2,(Integer)(((JMagneticHint)hints2.elementAt(idx.intValue())).links.elementAt(i)),node);
			  }
			  else
			  {
				  DefaultMutableTreeNode node = new DefaultMutableTreeNode("HINT "+i); 
				  DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(((JMagneticHint)hints2.elementAt(idx.intValue())).content.elementAt(i));
				  node.add(node2);
			      owner.add( node );
			  }
		   }
		}
	}

}
