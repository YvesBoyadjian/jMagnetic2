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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.rtf.RTFEditorKit;

import org.iflegends.msmemorial.util.MP3Player;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class JMagnetic2GameSelect extends javax.swing.JDialog {
	
	private static final Configuration configuration = ConfigurationManager.getConfiguration("jmagnetic2");
	
	private JTabbedPane gameTabs;
	private JPanel pawnTab;
	private JCheckBox jinxterMusic;
	private JButton jinxterPlay;
	private JTextPane jinxterText;
	private JScrollPane jinxterScroller;
	private JLabel jinxterCover;
	private JPanel jinxterTab;
	private JComboBox guildVersion;
	private JButton guildPlay;
	private JTextPane pawnText;
	private JButton wonderlandPlay;
	private JTextPane wonderlandText;
	private JScrollPane wonderlandScroller;
	private JLabel wonderlandCover;
	private JPanel wonderlandTab;
	private JButton mythPlay;
	private JTextPane mythText;
	private JScrollPane mythScroller;
	private JLabel mythCover;
	private JPanel mythTab;
	private JCheckBox fishMusic;
	private JComboBox fishVersion;
	private JButton fishPlay;
	private JTextPane fishText;
	private JScrollPane fishScroller;
	private JLabel fishCover;
	private JPanel fishTab;
	private JPanel basePanel;
	private JCheckBox corruptionMusic;
	private JComboBox corruptionVersion;
	private JButton corruptionPlay;
	private JTextPane corruptionText;
	private JScrollPane corruptionScroller;
	private JLabel corruptionCover;
	private JPanel corruptionTab;
	private JTextPane guildText;
	private JScrollPane guildScroller;
	private JLabel guildCover;
	private JCheckBox pawnMusic;
	private JButton playPawn;
	private JScrollPane pawnTextScroller;
	private JLabel pawnCover;
	private JPanel guildTab;
	private JCheckBox wonderMusic;
	
	private boolean guildMW = false;
	private boolean corrMW = false;
	private boolean fishMW = false;
	
	private Thread player = null;
	
	private String gameTag = "";

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JMagnetic2GameSelect inst = new JMagnetic2GameSelect(frame);
		
		inst.setVisible(true);
	}
	
	public JMagnetic2GameSelect(JFrame frame) {
		super(frame);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
				    public void run() {
						initGUI();
				    }
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
}
	
	private void initGUI() {
		String pawnStory = configuration.getProperty("pawn.story");
		String pawnMP3 = configuration.getProperty("pawn.music");
		String guildStory = configuration.getProperty("guild.story");
		String jinxterStory = configuration.getProperty("jinxter.story");
		String jinxterMP3 = configuration.getProperty("jinxter.music");
		String corruptStory = configuration.getProperty("corruption.story");
		String corruptMP3 = configuration.getProperty("corruption.music");
		String fishStory = configuration.getProperty("fish.story");
		String fishMP3 = configuration.getProperty("fish.music");
		String mythStory = configuration.getProperty("myth.story");
		String wonderStory = configuration.getProperty("wonderland.story");
		String wonderMP3 = configuration.getProperty("wonderland.music");
		String cguildStory = configuration.getProperty("mwguild.story");
		String ccorruptStory = configuration.getProperty("mwcorruption.story");
		String cfishStory = configuration.getProperty("mwfish.story");
        boolean automusic = configuration.getBooleanProperty("automusic", true);
		// Debug
        //automusic=true;
        try {
			{
				this.setTitle("Select a game");
				this.setSize(654, 480);
				this.setModal(true);
				//$protect>>$ 
				// Relocate
				Toolkit toolkit = Toolkit.getDefaultToolkit();

				// Get the current screen size
				Dimension scrnsize = toolkit.getScreenSize();
				int locX = (((int)scrnsize.getWidth())-654)/2;
				int locY = (((int)scrnsize.getHeight())-480)/2;
				this.setLocation(locX,locY);
				this.setResizable(false);
				this.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						thisWindowClosing(evt);
					}
				});
				//$protect<<$
			}
			{
				basePanel = new JPanel();
				BorderLayout jPanel1Layout = new BorderLayout();
				basePanel.setLayout(jPanel1Layout);
				getContentPane().add(basePanel, BorderLayout.NORTH);
				basePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				{
					gameTabs = new JTabbedPane();
					basePanel.add(gameTabs, BorderLayout.CENTER);
					gameTabs.setBorder(BorderFactory.createTitledBorder(""));
					gameTabs.putClientProperty("Quaqua.Component.visualMargin",
							new Insets(3, -5, -4, -5));
					gameTabs.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent evt) {
							gameTabsStateChanged(evt);
						}
					});

					if (checkFile(pawnStory)) 
					{
						pawnTab = new JPanel();
						GridBagLayout pawnTabLayout = new GridBagLayout();
						pawnTabLayout.rowWeights = new double[] { 0.1, 0.1 };
						pawnTabLayout.rowHeights = new int[] { 7, 7 };
						pawnTabLayout.columnWeights = new double[] { 0.1, 0.1 };
						pawnTabLayout.columnWidths = new int[] { 7, 7 };
						pawnTab.setLayout(pawnTabLayout);
						gameTabs.addTab("The Pawn", null, pawnTab, null);
						{
							pawnCover = new JLabel();
							pawnTab.add(pawnCover, new GridBagConstraints(0, 0,
									1, 1, 0.0, 0.0,
									GridBagConstraints.PAGE_START,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							pawnCover.setIcon(new ImageIcon(getClass()
									.getClassLoader().getResource(
											"Ressources/cover_pawn.png")));
							pawnCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							pawnCover.setSize(300, 370);
							pawnCover.setPreferredSize(new java.awt.Dimension(300, 370));
							pawnCover.setMaximumSize(new java.awt.Dimension(300, 370));
							pawnCover.setMinimumSize(new java.awt.Dimension(300, 370));
							pawnCover.setVerticalAlignment(SwingConstants.TOP);
							pawnCover.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							pawnTextScroller = new JScrollPane();
							pawnTab.add(pawnTextScroller,
									new GridBagConstraints(1, 0, 1, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(15, 10, 0, 10), 0, 10));
							pawnTextScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							pawnTextScroller.setSize(300, 335);
							pawnTextScroller
									.setMinimumSize(new java.awt.Dimension(300,
											335));
							pawnTextScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							pawnTextScroller.getVerticalScrollBar()
									.setAutoscrolls(true);
							pawnTextScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							{
								pawnText = new JTextPane();
								pawnTextScroller.setViewportView(pawnText);
								pawnText.setFont(new java.awt.Font("Serif", 0,
										14));
								pawnText.setEditable(false);
								pawnText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								pawnText.setAutoscrolls(false);
								//$protect>>$

								pawnText.setCaretPosition(0);
								pawnText.setContentType("text/html");
								try {
									pawnText.setPage(getClass()
											.getClassLoader().getResource(
													"Ressources/pawn.html"));
								} catch (Exception e) {
									//System.out.println("An internal file is missing: pawn.rtf");
								}
								//$protect<<$

							}
						}
						{
							playPawn = new JButton();
							pawnTab.add(playPawn, new GridBagConstraints(1, 1,
									1, 1, 0.0, 0.0, GridBagConstraints.EAST,
									GridBagConstraints.NONE, new Insets(0, 0,
											5, 15), 25, 0));
							playPawn.setText("Play");
							playPawn.setFont(new java.awt.Font("Arial", 0, 12));
							playPawn.setSelected(true);
							playPawn.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									playPawnActionPerformed(evt);
								}
							});
						}
						if (checkFile(pawnMP3)) {
							pawnMusic = new JCheckBox();
							pawnTab.add(pawnMusic, new GridBagConstraints(0, 1,
									1, 1, 0.0, 0.0, GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(0, 15,
											5, 0), 0, 0));
							pawnMusic.setText("Play music");
							if (automusic)
							   pawnMusic.setSelected(true);
							else
								   pawnMusic.setSelected(false);
								
							pawnMusic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									pawnMusicActionPerformed(evt);
								}
							});
						}
					}
					if (checkFile(guildStory) || checkFile(cguildStory)) {
						guildTab = new JPanel();
						GridBagLayout guildTabLayout = new GridBagLayout();
						guildTabLayout.rowWeights = new double[] { 0.1, 0.1 };
						guildTabLayout.rowHeights = new int[] { 7, 7 };
						guildTabLayout.columnWeights = new double[] { 0.1, 0.1 };
						guildTabLayout.columnWidths = new int[] { 7, 7 };
						guildTab.setLayout(guildTabLayout);
						gameTabs.addTab("Guild of Thieves", null, guildTab,
								null);
						{
							guildCover = new JLabel();
							guildTab.add(guildCover, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							if (checkFile(guildStory)) {
								guildCover.setIcon(new ImageIcon(getClass()
										.getClassLoader().getResource(
												"Ressources/cover_guild.png")));
							} else if (checkFile(cguildStory)) {
								guildCover
										.setIcon(new ImageIcon(
												getClass()
														.getClassLoader()
														.getResource(
																"Ressources/cover_collection.png")));
								guildMW = true;
							}
							guildCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							guildCover.setSize(300, 370);
							guildCover.setPreferredSize(new java.awt.Dimension(300, 370));
							guildCover.setMaximumSize(new java.awt.Dimension(300, 370));
							guildCover.setMinimumSize(new java.awt.Dimension(300, 370));
							guildCover.setHorizontalAlignment(SwingConstants.CENTER);
							guildCover.setVerticalAlignment(SwingConstants.TOP);
						}
						{
							guildScroller = new JScrollPane();
							guildTab.add(guildScroller, new GridBagConstraints(
									1, 0, 2, 1, 0.0, 0.0,
									GridBagConstraints.PAGE_START,
									GridBagConstraints.NONE, new Insets(15, 10,
											0, 10), 0, 10));
							guildScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							guildScroller
									.setMinimumSize(new java.awt.Dimension(300,
											335));
							guildScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							guildScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							guildScroller.setSize(300, 335);
							{
								guildText = new JTextPane();
								guildScroller.setViewportView(guildText);
								guildText.setFont(new java.awt.Font("Serif", 0,
										14));
								guildText.setEditable(false);
								guildText.setContentType("text/html");
								//$protect>>$

								guildText.setCaretPosition(0);
								try {
									guildText.setPage(getClass()
											.getClassLoader().getResource(
													"Ressources/guild.html"));
									guildText.setSize(279, 348);
									guildText
											.setPreferredSize(new java.awt.Dimension(
													279, 348));
									guildText.setAutoscrolls(false);
									guildText
											.setMinimumSize(new java.awt.Dimension(
													6, 20));
								} catch (Exception e) {
									//System.out.println("An internal file is missing: pawn.rtf");
								}
								//$protect<<$
							}
						}
						{
							guildPlay = new JButton();
							guildTab.add(guildPlay, new GridBagConstraints(2,
									1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
									GridBagConstraints.NONE, new Insets(0, 0,
											5, 15), 25, 0));
							guildPlay.setText("Play");
							guildPlay
									.setFont(new java.awt.Font("Arial", 0, 12));
							guildPlay.setSelected(true);
							guildPlay.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									guildPlayActionPerformed(evt);
								}
							});
						}
						if (checkFile(guildStory) && checkFile(cguildStory)) {
							ComboBoxModel guildVersionModel = new DefaultComboBoxModel(
									new String[] { "Standard version",
											"Magnetic Windows version" });
							guildVersion = new JComboBox();
							guildTab.add(guildVersion, new GridBagConstraints(
									1, 1, 1, 1, 0.0, 0.0,
									GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(0, 10,
											5, 0), 5, 0));
							guildVersion.setModel(guildVersionModel);
							guildVersion.setFont(new java.awt.Font("Arial", 0,
									12));
							guildVersion.setSelectedIndex(0);
							guildVersion
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											guildVersionActionPerformed(evt);
										}
									});
						}
					}
					if (checkFile(jinxterStory)) 
					{
						jinxterTab = new JPanel();
						gameTabs.addTab("Jinxter", null, jinxterTab, null);
						GridBagLayout jPanel1Layout8 = new GridBagLayout();
						jPanel1Layout8.columnWidths = new int[] { 7, 7 };
						jPanel1Layout8.rowHeights = new int[] { 7, 7 };
						jPanel1Layout8.columnWeights = new double[] { 0.1, 0.1 };
						jPanel1Layout8.rowWeights = new double[] { 0.1, 0.1 };
						jinxterTab.setLayout(jPanel1Layout8);
						{
							jinxterCover = new JLabel();
							jinxterTab.add(jinxterCover,
									new GridBagConstraints(0, 0, 1, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0), 0, 0));
							jinxterCover.setIcon(new ImageIcon(getClass()
									.getClassLoader().getResource(
											"Ressources/cover_jinxter.png")));
							jinxterCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							jinxterCover.setSize(300, 370);
							jinxterCover.setPreferredSize(new java.awt.Dimension(300, 370));
							jinxterCover.setMaximumSize(new java.awt.Dimension(300, 370));
							jinxterCover.setMinimumSize(new java.awt.Dimension(300, 370));
							jinxterCover.setVerticalAlignment(SwingConstants.TOP);
							jinxterCover.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							jinxterScroller = new JScrollPane();
							jinxterTab.add(jinxterScroller,
									new GridBagConstraints(1, 0, 2, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(15, 10, 0, 10), 0, 10));
							jinxterScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							jinxterScroller
									.setMinimumSize(new java.awt.Dimension(300,
											335));
							jinxterScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							jinxterScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							jinxterScroller.setSize(300, 335);
							{
								jinxterText = new JTextPane();
								jinxterScroller.setViewportView(jinxterText);
								jinxterText.setContentType("text/html");
								jinxterText
										.setPage(getClass()
												.getClassLoader()
												.getResource(
														"Ressources/jinxter.html"));
								jinxterText.setEditable(false);
								jinxterText.setCaretPosition(0);
								jinxterText.setFont(new java.awt.Font("Serif",
										0, 14));
								jinxterText
										.setMinimumSize(new java.awt.Dimension(
												6, 20));
								jinxterText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								jinxterText.setAutoscrolls(false);
								jinxterText.setSize(290, 341);
							}
						}
						{
							jinxterPlay = new JButton();
							jinxterTab.add(jinxterPlay, new GridBagConstraints(
									2, 1, 1, 1, 0.0, 0.0,
									GridBagConstraints.EAST,
									GridBagConstraints.NONE, new Insets(0, 0,
											5, 15), 25, 0));
							jinxterPlay.setText("Play");
							jinxterPlay.setFont(new java.awt.Font("Arial", 0,
									12));
							jinxterPlay.setSelected(true);
							jinxterPlay.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									jinxterPlayActionPerformed(evt);
								}
							});
						}
						if (checkFile(jinxterMP3)) {
							jinxterMusic = new JCheckBox();
							jinxterTab.add(jinxterMusic,
									new GridBagConstraints(0, 1, 1, 1, 0.0,
											0.0, GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(0, 15, 5, 0), 0, 0));
							jinxterMusic.setText("Play music");
							if (automusic)	
							   jinxterMusic.setSelected(true);
							else
							   jinxterMusic.setSelected(false);
								
							jinxterMusic
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											jinxterMusicActionPerformed(evt);
										}
									});
						}
					}
					if (checkFile(corruptStory) || checkFile(ccorruptStory)) {
						corruptionTab = new JPanel();
						gameTabs
								.addTab("Corruption", null, corruptionTab, null);
						GridBagLayout jPanel1Layout1 = new GridBagLayout();
						jPanel1Layout1.columnWidths = new int[] { 7, 7 };
						jPanel1Layout1.rowHeights = new int[] { 7, 7 };
						jPanel1Layout1.columnWeights = new double[] { 0.1, 0.1 };
						jPanel1Layout1.rowWeights = new double[] { 0.1, 0.1 };
						corruptionTab.setLayout(jPanel1Layout1);
						{
							corruptionCover = new JLabel();
							corruptionTab.add(corruptionCover,
									new GridBagConstraints(0, 0, 1, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0), 0, 0));
							if (checkFile(corruptStory)) {
								corruptionCover
										.setIcon(new ImageIcon(
												getClass()
														.getClassLoader()
														.getResource(
																"Ressources/cover_corrupt.png")));
							} else if (checkFile(ccorruptStory)) {
								corruptionCover
										.setIcon(new ImageIcon(
												getClass()
														.getClassLoader()
														.getResource(
																"Ressources/cover_collection.png")));
								corrMW = true;
							}
							corruptionCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							corruptionCover.setSize(300, 370);
							corruptionCover.setPreferredSize(new java.awt.Dimension(300, 370));
							corruptionCover.setMaximumSize(new java.awt.Dimension(300, 370));
							corruptionCover.setMinimumSize(new java.awt.Dimension(300, 370));
							corruptionCover.setVerticalAlignment(SwingConstants.TOP);
							corruptionCover.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							corruptionScroller = new JScrollPane();
							corruptionTab.add(corruptionScroller,
									new GridBagConstraints(1, 0, 2, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(15, 10, 0, 10), 0, 10));
							corruptionScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							corruptionScroller
									.setMinimumSize(new java.awt.Dimension(300,
											335));
							corruptionScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							corruptionScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							corruptionScroller.setSize(300, 335);
							{
								corruptionText = new JTextPane();
								corruptionScroller
										.setViewportView(corruptionText);
								corruptionText.setContentType("text/html");
								corruptionText.setPage(getClass()
										.getClassLoader().getResource(
												"Ressources/corruption.html"));
								corruptionText.setEditable(false);
								corruptionText.setCaretPosition(0);
								corruptionText.setFont(new java.awt.Font(
										"Serif", 0, 14));
								corruptionText
										.setMinimumSize(new java.awt.Dimension(
												6, 20));
								corruptionText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								corruptionText.setAutoscrolls(false);
								corruptionText.setSize(279, 348);
							}
						}
						{
							corruptionPlay = new JButton();
							corruptionTab.add(corruptionPlay, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 15), 25, 0));
							corruptionPlay.setText("Play");
							corruptionPlay.setFont(new java.awt.Font("Arial",
									0, 12));
							corruptionPlay.setSelected(true);
							corruptionPlay
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											corruptionPlayActionPerformed(evt);
										}
									});
						}
						if (checkFile(corruptStory) && checkFile(ccorruptStory)) {
							ComboBoxModel corruptionVersionModel = new DefaultComboBoxModel(
									new String[] { "Standard version",
											"Magnetic Windows version" });
							corruptionVersion = new JComboBox();
							corruptionTab.add(corruptionVersion, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 5, 0));
							corruptionVersion.setFont(new java.awt.Font(
									"Arial", 0, 12));
							corruptionVersion.setModel(corruptionVersionModel);
							corruptionVersion.setSelectedIndex(0);
							corruptionVersion
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											corruptionVersionActionPerformed(evt);
										}
									});
						}
						if (checkFile(corruptMP3)) {
							corruptionMusic = new JCheckBox();
							corruptionTab.add(corruptionMusic,
									new GridBagConstraints(0, 1, 1, 1, 0.0,
											0.0, GridBagConstraints.WEST,
											GridBagConstraints.NONE,
											new Insets(0, 15, 0, 0), 0, 0));
							corruptionMusic.setText("Play music");
							if (automusic)
							   corruptionMusic.setSelected(true);
							else
								corruptionMusic.setSelected(false);
								
							corruptionMusic
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											corruptionMusicActionPerformed(evt);
										}
									});
						}
					}
					if (checkFile(fishStory) || checkFile(cfishStory)) {
						fishTab = new JPanel();
						gameTabs.addTab("Fish", null, fishTab, null);
						GridBagLayout jPanel1Layout2 = new GridBagLayout();
						jPanel1Layout2.columnWidths = new int[] { 7, 7 };
						jPanel1Layout2.rowHeights = new int[] { 7, 7 };
						jPanel1Layout2.columnWeights = new double[] { 0.1, 0.1 };
						jPanel1Layout2.rowWeights = new double[] { 0.1, 0.1 };
						fishTab.setLayout(jPanel1Layout2);
						{
							fishCover = new JLabel();
							fishTab.add(fishCover, new GridBagConstraints(0, 0,
									1, 1, 0.0, 0.0,
									GridBagConstraints.PAGE_START,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							if (checkFile(fishStory)) {
								fishCover.setIcon(new ImageIcon(getClass()
										.getClassLoader().getResource(
												"Ressources/cover_fish.png")));
							} else if (checkFile(cfishStory)) {
								fishCover
										.setIcon(new ImageIcon(
												getClass()
														.getClassLoader()
														.getResource(
																"Ressources/cover_collection.png")));
								fishMW = true;
							}
							fishCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							fishCover.setSize(300, 370);
							fishCover.setPreferredSize(new java.awt.Dimension(300, 370));
							fishCover.setMaximumSize(new java.awt.Dimension(300, 370));
							fishCover.setMinimumSize(new java.awt.Dimension(300, 370));
							fishCover.setVerticalAlignment(SwingConstants.TOP);
						}
						{
							fishScroller = new JScrollPane();
							fishTab.add(fishScroller, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(15, 10, 0, 10), 0, 10));
							fishScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							fishScroller.setMinimumSize(new java.awt.Dimension(
									300, 335));
							fishScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							fishScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							fishScroller.setSize(300, 335);
							{
								fishText = new JTextPane();
								fishScroller.setViewportView(fishText);
								fishText.setContentType("text/html");
								fishText.setPage(getClass().getClassLoader()
										.getResource("Ressources/fish.html"));
								fishText.setEditable(false);
								fishText.setCaretPosition(0);
								fishText.setFont(new java.awt.Font("Serif", 0,
										14));
								fishText.setMinimumSize(new java.awt.Dimension(
										6, 20));
								fishText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								fishText.setAutoscrolls(false);
								fishText.setSize(279, 348);
							}
						}
						{
							fishPlay = new JButton();
							fishTab.add(fishPlay, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 15), 25, 0));
							fishPlay.setText("Play");
							fishPlay.setFont(new java.awt.Font("Arial", 0, 12));
							fishPlay.setSelected(true);
							fishPlay.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									fishPlayActionPerformed(evt);
								}
							});
						}
						if (checkFile(fishStory) && checkFile(cfishStory)) {
							ComboBoxModel fishVersionModelModel = new DefaultComboBoxModel(
									new String[] { "Standard version",
											"Magnetic Windows version" });
							fishVersion = new JComboBox();
							fishTab.add(fishVersion, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 5, 0));
							fishVersion.setModel(fishVersionModelModel);
							fishVersion.setSelectedIndex(0);
							fishVersion.setFont(new java.awt.Font("Arial", 0,
									12));
							fishVersion.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									fishVersionActionPerformed(evt);
								}
							});
						}
						if (checkFile(fishMP3)) {
							fishMusic = new JCheckBox();
							fishTab.add(fishMusic, new GridBagConstraints(0, 1,
									1, 1, 0.0, 0.0, GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(0, 15,
											0, 0), 0, 0));
							fishMusic.setText("Play music");
							fishMusic.setBorder(BorderFactory
									.createCompoundBorder(BorderFactory
											.createEmptyBorder(0, 0, 0, 0),
											null));
							if (automusic)
							   fishMusic.setSelected(true);
							else
								fishMusic.setSelected(false);
							fishMusic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									fishMusicActionPerformed(evt);
								}
							});
						}
					}
					if (checkFile(mythStory)) 
					{
						mythTab = new JPanel();
						gameTabs.addTab("Myth", null, mythTab, null);
						GridBagLayout jPanel1Layout3 = new GridBagLayout();
						jPanel1Layout3.columnWidths = new int[] { 7, 7 };
						jPanel1Layout3.rowHeights = new int[] { 7, 7 };
						jPanel1Layout3.columnWeights = new double[] { 0.1, 0.1 };
						jPanel1Layout3.rowWeights = new double[] { 0.1, 0.1 };
						mythTab.setLayout(jPanel1Layout3);
						{
							mythCover = new JLabel();
							mythTab.add(mythCover, new GridBagConstraints(0, 0,
									1, 1, 0.0, 0.0,
									GridBagConstraints.PAGE_START,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							mythCover.setIcon(new ImageIcon(getClass()
									.getClassLoader().getResource(
											"Ressources/cover_myth.png")));
							mythCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							mythCover.setAlignmentY(0.0f);
							mythCover.setSize(300, 370);
							mythCover.setPreferredSize(new java.awt.Dimension(300, 370));
							mythCover.setMaximumSize(new java.awt.Dimension(300, 370));
							mythCover.setMinimumSize(new java.awt.Dimension(300, 370));
							mythCover.setVerticalAlignment(SwingConstants.TOP);
							mythCover.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							mythScroller = new JScrollPane();
							mythTab.add(mythScroller, new GridBagConstraints(1,
									0, 1, 1, 0.0, 0.0,
									GridBagConstraints.PAGE_START,
									GridBagConstraints.NONE, new Insets(15, 10,
											0, 10), 0, 10));
							mythScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							mythScroller.setMinimumSize(new java.awt.Dimension(
									300, 335));
							mythScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							mythScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							mythScroller.setSize(300, 335);
							{
								mythText = new JTextPane();
								mythScroller.setViewportView(mythText);
								mythText.setContentType("text/html");
								mythText.setPage(getClass().getClassLoader()
										.getResource("Ressources/myth.html"));
								mythText.setEditable(false);
								mythText.setCaretPosition(0);
								mythText.setFont(new java.awt.Font("Serif", 0,
										14));
								mythText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								mythText.setAutoscrolls(false);
							}
						}
						{
							mythPlay = new JButton();
							mythTab.add(mythPlay, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 15), 25, 0));
							mythPlay.setText("Play");
							mythPlay.setFont(new java.awt.Font("Arial", 0, 12));
							mythPlay.setSelected(true);
							mythPlay.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									mythPlayActionPerformed(evt);
								}
							});
						}
					}
					if (checkFile(wonderStory)) 
					{
						wonderlandTab = new JPanel();
						gameTabs
								.addTab("Wonderland", null, wonderlandTab, null);
						GridBagLayout jPanel1Layout4 = new GridBagLayout();
						jPanel1Layout4.columnWidths = new int[] { 7, 7 };
						jPanel1Layout4.rowHeights = new int[] { 7, 7 };
						jPanel1Layout4.columnWeights = new double[] { 0.1, 0.1 };
						jPanel1Layout4.rowWeights = new double[] { 0.1, 0.1 };
						wonderlandTab.setLayout(jPanel1Layout4);
						{
							wonderlandCover = new JLabel();
							wonderlandTab.add(wonderlandCover,
									new GridBagConstraints(0, 0, 1, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(0, 0, 0, 0), 0, 0));
							wonderlandCover.setIcon(new ImageIcon(getClass()
									.getClassLoader().getResource(
											"Ressources/cover_wonder.png")));
							wonderlandCover.setBorder(BorderFactory
									.createEmptyBorder(0, 0, 0, 0));
							wonderlandCover.setHorizontalTextPosition(SwingConstants.LEFT);
							wonderlandCover.setIconTextGap(0);
							wonderlandCover.setVerticalAlignment(SwingConstants.TOP);
							wonderlandCover.setSize(300, 370);
							wonderlandCover.setPreferredSize(new java.awt.Dimension(300, 370));
							wonderlandCover.setMaximumSize(new java.awt.Dimension(300, 370));
							wonderlandCover.setMinimumSize(new java.awt.Dimension(300, 370));
							wonderlandCover.setHorizontalAlignment(SwingConstants.CENTER);
						}
						{
							wonderlandScroller = new JScrollPane();
							wonderlandTab.add(wonderlandScroller,
									new GridBagConstraints(1, 0, 1, 1, 0.0,
											0.0, GridBagConstraints.PAGE_START,
											GridBagConstraints.NONE,
											new Insets(15, 10, 0, 10), 0, 10));
							wonderlandScroller
									.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							wonderlandScroller
									.setMinimumSize(new java.awt.Dimension(300,
											335));
							wonderlandScroller
									.setPreferredSize(new java.awt.Dimension(
											300, 335));
							wonderlandScroller.setBorder(BorderFactory
									.createBevelBorder(BevelBorder.RAISED,
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192),
											new java.awt.Color(192, 192, 192)));
							wonderlandScroller.setSize(300, 335);
							{
								wonderlandText = new JTextPane();
								wonderlandScroller
										.setViewportView(wonderlandText);
								wonderlandText.setContentType("text/html");
								wonderlandText.setPage(getClass()
										.getClassLoader().getResource(
												"Ressources/wonderland.html"));
								wonderlandText.setEditable(false);
								wonderlandText.setCaretPosition(0);
								wonderlandText.setFont(new java.awt.Font(
										"Serif", 0, 14));
								wonderlandText
										.setPreferredSize(new java.awt.Dimension(
												279, 348));
								wonderlandText.setAutoscrolls(false);
							}
						}
						{
							wonderlandPlay = new JButton();
							wonderlandTab.add(wonderlandPlay, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 5, 15), 25, 0));
							wonderlandPlay.setText("Play");
							wonderlandPlay.setFont(new java.awt.Font("Arial",
									0, 12));
							wonderlandPlay.setSelected(true);
							wonderlandPlay
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											wonderlandPlayActionPerformed(evt);
										}
									});
						}
						if (checkFile(wonderMP3) || true) { // TODO: Wegnehmen!
							wonderMusic = new JCheckBox();
							wonderlandTab.add(wonderMusic, new GridBagConstraints(0, 1,
									1, 1, 0.0, 0.0, GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(0, 15,
											5, 0), 0, 0));
							wonderMusic.setText("Play music");
							if (automusic)
							   wonderMusic.setSelected(true);
							else
								   wonderMusic.setSelected(false);
								
							wonderMusic.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									wonderMusicActionPerformed(evt);
								}
							});
						}
					}
				}
			}
			//this.setSize(600, 480);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void pawnMusicActionPerformed(ActionEvent evt) {
		if (pawnMusic.isSelected())
		{
			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String pawnMP3 = configuration.getProperty("pawn.music");
            playMP3(gameDir+File.separator+pawnMP3);
		}
		else
		{
			stopMP3();
		}
	}

	private void wonderMusicActionPerformed(ActionEvent evt) {
		if (wonderMusic.isSelected())
		{
			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String wonderMP3 = configuration.getProperty("wonderland.music");
            playMP3(gameDir+File.separator+wonderMP3);
		}
		else
		{
			stopMP3();
		}
	}

	private void jinxterMusicActionPerformed(ActionEvent evt) {
		if (jinxterMusic.isSelected())
		{
			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String jinxterMP3 = configuration.getProperty("jinxter.music");
            playMP3(gameDir+File.separator+jinxterMP3);
		}
		else
		{
			stopMP3();
		}
	}

	private void guildVersionActionPerformed(ActionEvent evt) {
		//System.out.println("guildVersion.actionPerformed, event=" + evt);
		if (guildVersion.getSelectedIndex()==0)
		{
		   guildCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_guild.png")));
		   guildMW = false;
		}
		else
		{
			guildCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_collection.png")));
			guildMW = true;
		}
	}

	private void corruptionMusicActionPerformed(ActionEvent evt) {
		if (corruptionMusic.isSelected())
		{
			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String corruptMP3 = configuration.getProperty("corruption.music");
            playMP3(gameDir+File.separator+corruptMP3);
		}
		else
		{
			stopMP3();
		}
	}

	private void corruptionVersionActionPerformed(ActionEvent evt) {
		//System.out.println("corruptionVersion.actionPerformed, event=" + evt);
		if (corruptionVersion.getSelectedIndex()==0)
		{
			corruptionCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_corrupt.png")));
			corrMW = false;
		}
		else
		{
			corruptionCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_collection.png")));
			corrMW = true;
		}
	}

	private void fishVersionActionPerformed(ActionEvent evt) {
		//System.out.println("fishVersion.actionPerformed, event=" + evt);
		if (fishVersion.getSelectedIndex()==0)
		{
			fishCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_fish.png")));
			fishMW = false;
		}
		else
		{
			fishCover.setIcon(new ImageIcon(getClass().getClassLoader().getResource("Ressources/cover_collection.png")));
			fishMW = true;
		}
	}

	private void fishMusicActionPerformed(ActionEvent evt) {
		if (fishMusic.isSelected())
		{
			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String fishMP3 = configuration.getProperty("fish.music");
            playMP3(gameDir+File.separator+fishMP3);
		}
		else
		{
			stopMP3();
		}
	}

	public boolean checkFile(String fileName)
	{
		String gameDir = configuration.getProperty("gamedir");
	    // Configure Pawn Tab
		File f = new File(gameDir);
		if (!f.isAbsolute())
		{
			gameDir = getBasePath()+gameDir;
		}
		if ((new File(gameDir+File.separator+fileName)).exists())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	private void gameTabsStateChanged(ChangeEvent evt) {
		String mp3File = null;
		String gameDir = configuration.getProperty("gamedir");
		String tab = gameTabs.getTitleAt(gameTabs.getSelectedIndex());
		boolean musicOn = configuration.getBooleanProperty("automusic", true);
		//System.out.println(tab);
		if (tab.equalsIgnoreCase("The Pawn"))
		{
			mp3File = configuration.getProperty("pawn.music");
			if (pawnMusic != null)
				musicOn = pawnMusic.isSelected();
		}
		else if (tab.equalsIgnoreCase("Jinxter"))
		{
			mp3File = configuration.getProperty("jinxter.music");
			if (jinxterMusic != null)
				musicOn = jinxterMusic.isSelected();
		}
		else if (tab.equalsIgnoreCase("Corruption"))
		{
			mp3File = configuration.getProperty("corruption.music");
			if (corruptionMusic != null)
				musicOn = corruptionMusic.isSelected();
		}
		else if (tab.equalsIgnoreCase("Fish"))
		{
			mp3File = configuration.getProperty("fish.music");
			if (fishMusic != null)
				musicOn = fishMusic.isSelected();
		}
		else if (tab.equalsIgnoreCase("Wonderland"))
		{
			mp3File = configuration.getProperty("wonderland.music");
			if (wonderMusic != null)
				musicOn = wonderMusic.isSelected();
		}
		else
		{
			stopMP3();
			musicOn = false;
		}
		if (musicOn)
		{
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			mp3File = gameDir+File.separator+mp3File;
			playMP3(mp3File);
		}
	}

	void playMP3(String mp3File)
	{
		if ((mp3File != null) && (new File(mp3File)).exists())
		{
			if (this.player != null)
			{
				player.stop();
			}
			player = (new Thread((new MP3Player(mp3File))));
			player.start();
		}
	}
	
	void stopMP3()
	{
		if (this.player != null)
		{
			player.stop();
		}
	}

	private void fishPlayActionPerformed(ActionEvent evt) {
		//System.out.println("fishPlay.actionPerformed, event=" + evt);
		if (this.fishMW)
			   gameTag = "mwfish";
			else
				gameTag ="fish";
			closeAndHide();
	}

	private void guildPlayActionPerformed(ActionEvent evt) {
		//System.out.println("guildPlay.actionPerformed, event=" + evt);
		if (this.guildMW)
			   gameTag = "mwguild";
			else
				gameTag ="guild";
			closeAndHide();
	}

	private void jinxterPlayActionPerformed(ActionEvent evt) {
		//System.out.println("jinxterPlay.actionPerformed, event=" + evt);
		gameTag = "jinxter";
		closeAndHide();
	}

	private void corruptionPlayActionPerformed(ActionEvent evt) {
		if (this.corrMW)
		   gameTag = "mwcorruption";
		else
			gameTag ="corruption";
		closeAndHide();
	}

	private void playPawnActionPerformed(ActionEvent evt) {
		//System.out.println("playPawn.actionPerformed, event=" + evt);
		gameTag = "pawn";
		closeAndHide();
	}

	private void mythPlayActionPerformed(ActionEvent evt) {
		//System.out.println("mythPlay.actionPerformed, event=" + evt);
		gameTag = "myth";
		closeAndHide();
	}

	private void wonderlandPlayActionPerformed(ActionEvent evt) {
		//System.out.println("wonderlandPlay.actionPerformed, event=" + evt);
		gameTag = "wonderland";
		closeAndHide();
	}
	
	private void closeAndHide()
	{
		stopMP3();
		setVisible(false);
	}
	
	public String getSelectedGame()
	{
		return gameTag;
	}

	public void setSelectedGame(String game)
	{
		gameTag=game;
	}

	private void thisWindowClosing(WindowEvent evt) {
		//System.out.println("this.windowClosing, event=" + evt);
		stopMP3();
		gameTag = "";
	}
	private String getBasePath()
	{
		ClassLoader cl = ConfigurationManager.class.getClassLoader();
		URL res = cl.getResource("org/iflegends/msmemorial/magnetic/JMagnetic2.class");
		String path;
		try {
			path = URLDecoder.decode(res.getPath(),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			path = URLDecoder.decode(res.getPath());
		}
        //System.out.println("RES:"+path);
		String install_path = path;  
        //System.out.println("FILE:"+install_path);
	    if (install_path.indexOf("file:") >= 0) 
	      install_path = install_path.substring(install_path.indexOf("file:")+("file:").length());
	    if (install_path.lastIndexOf("/classes") >= 0) 
		      install_path = install_path.substring(0, install_path.lastIndexOf("/classes")+1);
	    if (install_path.lastIndexOf("JMagnetic2.class") >= 0)
	      install_path = install_path.substring(0, install_path.lastIndexOf("JMagnetic2.class"));
	    if (install_path.lastIndexOf("JMagnetic2.jar") >= 0) 
	      install_path = install_path.substring(0, install_path.lastIndexOf("JMagnetic2.jar"));
	    if (!install_path.endsWith("/"))
	    	install_path = install_path+"/";
        //System.out.println("FINAL:"+install_path);
	    return install_path;
	}

}
