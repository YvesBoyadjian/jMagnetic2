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

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.util.*;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PipedWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessControlException;
import java.util.Arrays;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;

import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.xml.DOMConfigurator;
import org.iflegends.msmemorial.swing.DefaultFileFilter;
import org.iflegends.msmemorial.swing.JTitleScreen;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;
import org.jconfig.handler.XMLFileHandler;

import com.birosoft.liquid.LiquidLookAndFeel;
import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;





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
public class JMagnetic2 extends javax.swing.JFrame {
	
	private static final ConfigurationManager cm = ConfigurationManager.getInstance();

	private JMenuItem helpMenuItem;
	private JMenu jMenu5;
	private JMagneticImgPanel graphicsPanel;
	public JTextField inputLine;
	private JPanel contentPane;
	private JMenuItem deleteMenuItem;
	private JSeparator jSeparator1;
	private JMenuItem pasteMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem cutMenuItem;
	private JMenu jMenu4;
	private JMenuItem exitMenuItem;
	private JSeparator jSeparator2;
	private JMenuItem closeFileMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveMenuItem;
	private JPanel statusLinePanel;
	private JMenuItem xtract64Menu;
	private JMenuItem jMenuItem1;
	private JMenuItem mythPassMenu;
	private JSeparator jSeparator8;
	private JMenuItem hintLinkMenu;
	private JSeparator jSeparator7;
	private JMenuItem gfxLink2Menu;
	private JMenuItem gfxLinkMenu;
	private JSeparator jSeparator6;
	private JMenuItem xtractMWMenu;
	private JMenuItem xtractPCMenu;
	private JMenu jMenu1;
	private JMenuItem imgSave;
	private JSeparator jSeparator5;
	private JTextPane interpreterOutput;
	private JScrollPane jScrollPane1;
	private JSeparator jSeparator4;
	private JMenuItem aboutMenuItem;
	private JSeparator jSeparator3;
	private JLabel statusLineRight;
	private JLabel statusLineLeft;
	private JSplitPane splitPane;
	private JMenuItem openFileMenuItem;
	private JMenu jMenu3;
	private JMenuBar jmagneticMenu;

	private static Configuration configuration;
	
	public JMagneticSwing magInstance;
	
	public JMagnetic2GameSelect gameSelect;
	private JSeparator jSeparator9;

	public PipedWriter kbOut;
	
	private SimpleAttributeSet userAttributes;
	private SimpleAttributeSet gameAttributes;


	public static void main(String[] args) {
		
		final StringBuffer gameTag = new StringBuffer();
		final StringBuffer storyFile = new StringBuffer("");

        loadConfiguration();
        
        initLoging();
        
        configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
        
        String laf = configuration.getProperty("laf");
        
        if (laf.startsWith("Quaqua"))
        {
        	if (laf.equals("Quaqua Tiger"))
        	{
        		System.setProperty("Quaqua.design","tiger");
        	}
        	else if (laf.equals("Quaqua Leopard"))
        	{
        		System.setProperty("Quaqua.design","leopard");
        	}
        		
        	String sys = System.getProperty("os.name");
        	if (!(sys.equals("Mac OS X") || sys.startsWith("Windows")|| sys.startsWith("Linux") ||
        			sys.startsWith("SunOS")))
        	{
        		boolean unixquaqua = configuration.getBooleanProperty("unixquaqua", false);
        		if (!unixquaqua)
                	loadSystemSettings(args);
        		else
             	   loadQuaquaSettings(args);
        	}
        	else
        	{
        	   loadQuaquaSettings(args);
        	}
        }
        else if (laf.equals("Liquid"))
        	loadLiquidSettings(args);
        else if (laf.equals("SystemLF"))
        	loadSystemSettings(args);
        else if (laf.equals("SkinLF"))
        	loadSkinLFSettings(args);
        else if (laf.equals("Nimbus"))
        	loadNimbusSettings(args);
        else
        	loadCommonSettings(args);
        
		JMagnetic2 inst = new JMagnetic2();
		if (!gameAvailable())
		{
	        JOptionPane pane = new JOptionPane(
	                "<html>"+
	                "<b>No story files found.</b><p>"+
	                "The default game directory does not contain any story files.<br>"+
	                "To use the game selection feature of JMagnetic, please put<br>"+
	                "your game files into the game directory or select a new directory."+
	                "To select your game files manually just continue.",
	                JOptionPane.WARNING_MESSAGE
	                );
	                Object[] options = { "Proceed without changes","Select directory"  };
	                pane.setOptions(options);
	                pane.setInitialValue(null);
	                JDialog dialog = pane.createDialog(null, "Use game selector?");
	                dialog.setVisible(true);
	                if ((pane.getValue() != null) && pane.getValue().equals("Select directory"))
	                {
	                	String gameDir = inst.selectGameDir();
	                	if (!gameDir.equals(""))
	                	{
	                		configuration.setProperty("gamedir", gameDir);
	                		try {
								cm.save("jmagnetic2");
							} catch (ConfigurationManagerException e) {
								e.printStackTrace();
							}
	                	}
	                	// Directory w�hlen und speichern
	                }
		}
		if (gameAvailable())
		{
	       inst.gameSelect = new JMagnetic2GameSelect(null);
           inst.gameSelect.setVisible(true);
           gameTag.append(inst.gameSelect.getSelectedGame());
		}
		//inst.pack();
		inst.setVisible(true);
		if (gameTag.toString().equals(""))
		{		 
			storyFile.append(inst.getGameFile());
			gameTag.append(inst.getGameTag(storyFile.toString()));
		}
         if (!gameTag.toString().equals(""))
         {
			inst.startInterpreter(gameTag.toString(), storyFile.toString());
		}
        
	}
	
	private static void loadCommonSettings(String[] args) {
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
		        	  // first tell SkinLF which theme to use
					try {
						UIManager.setLookAndFeel(
								UIManager.getCrossPlatformLookAndFeelClassName());					} catch (Exception e) {
						e.printStackTrace();
					}
			    }

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadSystemSettings(String[] args) {
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
		        	  // first tell SkinLF which theme to use
					try {
						UIManager.setLookAndFeel(
					            UIManager.getSystemLookAndFeelClassName());					
						} catch (Exception e) {
						e.printStackTrace();
					}
			    }

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadNimbusSettings(String[] args) {
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
		        	  // first tell SkinLF which theme to use
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGameTag(String storyFile) {
		if (storyFile.toLowerCase().endsWith("pawn.mag"))
			return "pawn";
		else if (storyFile.toLowerCase().endsWith("cguild.mag"))
			return "mwguild";
		else if (storyFile.toLowerCase().endsWith("guild.mag"))
			return "guild";
		else if (storyFile.toLowerCase().endsWith("jinxter.mag"))
			return "jinxter";
		else if (storyFile.toLowerCase().endsWith("ccorrupt.mag"))
			return "mwcorruption";
		else if (storyFile.toLowerCase().endsWith("corrupt.mag"))
			return "corruption";
		else if (storyFile.toLowerCase().endsWith("cfish.mag"))
			return "mwfish";
		else if (storyFile.toLowerCase().endsWith("fish.mag"))
			return "fish";
		else if (storyFile.toLowerCase().endsWith("myth.mag"))
			return "myth";
		else if (storyFile.toLowerCase().endsWith("wonder.mag"))
			return "wonderland";
		else
			{
			try {
				RandomAccessFile rf = new RandomAccessFile(storyFile, "r");
				int size = (int) rf.length();
				byte fbytes[] = new byte[size];
				rf.readFully(fbytes);
				rf.close();				
				String internalData = new String(fbytes);
				if (internalData.indexOf("Stone Bridg")>=0)
					return "pawn";
				else if (internalData.indexOf("Bank_of_Kerovni")>=0)
				{
					if (internalData.charAt(13)==0x04)
						return "mwguild";
					else
						return "guild";
				}
				else if (internalData.indexOf("In The Ove")>=0)
					return "jinxter";
				else if (internalData.indexOf("Margaret")>=0)
				{
					if (internalData.charAt(13)==0x04)
						return "mwcorruption";
					else
						return "corruption";
				}
				else if (internalData.indexOf("Paddlingto")>=0)
				{
					if (internalData.charAt(13)==0x04)
						return "mwfish";
					else
						return "fish";
				}
				else if (internalData.indexOf("outside Hades")>=0)
					return "myth";
				else if (internalData.indexOf("Outside A Kitche")>=0)
					return "wonderland";
				else
				{
					Object[] possibleValues = { "The Pawn", "Guild of Thieves", "Guild of Thieves (MW)",
							                    "Jinxter", "Corruption", "Corruption (MW)", "Fish",
							                    "Fish (MW)", "Myth", "Wonderland"};
					
					Object selectedValue = JOptionPane.showInputDialog(null, 
							"<html>"+
			                "<b>Unable to detect valid game signature.</b><BR><p>"+
			                "The story file you selected can not be identified.<br><BR>"+
			                "If you created the story file from original game disks, you might"+
			                "have an unknown game version and can try to run it with Magnetic."+
			                "Otherwise please verifiy that you selected a valid Magnetic "+
			                "game file and identify the selected game manually below.", "Identify game",
					JOptionPane.WARNING_MESSAGE, null,
					possibleValues, possibleValues[0]);

					if (selectedValue != null)
					{
                    if (((String)selectedValue).equals("The Pawn"))
                    	return "pawn";
                    else if (((String)selectedValue).equals("Guild of Thieves"))
                    	return "guild";
                    else if (((String)selectedValue).equals("Guild of Thieves (MW)"))
                        return "mwguild";
                    else if (((String)selectedValue).equals("Jinxter"))
                        return "jinxter";
                    else if (((String)selectedValue).equals("Corruption"))
                    	return "corruption";
                    else if (((String)selectedValue).equals("Corruption (MW)"))
                        return "mwcorruption";
                    else if (((String)selectedValue).equals("Fish"))
                    	return "fish";
                    else if (((String)selectedValue).equals("Fish (MW)"))
                        return "mwfish";
                    else if (((String)selectedValue).equals("Myth"))
                        return "myth";
                    else if (((String)selectedValue).equals("Wonderland"))
                        return "wonderland";
                    else
                       return storyFile;
					}
					else
						return storyFile;
				}
			} catch (FileNotFoundException e) {
                return storyFile;
			} catch (IOException e) {
                return storyFile;
			}
			}
	}

	public static boolean gameAvailable() {

			String gameDir = configuration.getProperty("gamedir");
			File f = new File(gameDir);
			if (!f.isAbsolute())
			{
				gameDir = getBasePath()+gameDir;
			}
			String pawnStory = configuration.getProperty("pawn.story");
			String guildStory = configuration.getProperty("guild.story");
			String jinxterStory = configuration.getProperty("jinxter.story");
			String corruptStory = configuration.getProperty("corruption.story");
			String fishStory = configuration.getProperty("fish.story");
			String mythStory = configuration.getProperty("myth.story");
			String wonderStory = configuration.getProperty("wonderland.story");
			String cguildStory = configuration.getProperty("mwguild.story");
			String ccorruptStory = configuration.getProperty("mwcorruption.story");
			String cfishStory = configuration.getProperty("mwfish.story");
		    // Configure Pawn Tab
			if ((new File(gameDir+File.separator+pawnStory)).exists() ||
				(new File(gameDir+File.separator+guildStory)).exists() ||	
				(new File(gameDir+File.separator+jinxterStory)).exists() ||
				(new File(gameDir+File.separator+corruptStory)).exists() ||
				(new File(gameDir+File.separator+fishStory)).exists() ||
				(new File(gameDir+File.separator+mythStory)).exists() ||
				(new File(gameDir+File.separator+wonderStory)).exists() ||				
				(new File(gameDir+File.separator+cguildStory)).exists() ||				
				(new File(gameDir+File.separator+ccorruptStory)).exists() ||
				(new File(gameDir+File.separator+cfishStory)).exists())
			{
				return true;
			}
			else
			{
				return false;
			}
	}

	private static void initLoging() 
	{
		String basePath = getBasePath();
		System.setProperty("app.home", basePath);
		//System.out.println(System.getProperty("app.home"));
	    DOMConfigurator.configure(getBasePath()+"conf"+File.separator+"log4j.xml");
	}

	protected StringBuffer getGameFile()
	{
		final StringBuffer gameFile = new StringBuffer();
		final JMagnetic2 owner = this;
		 JFileChooser fileChooser = new JFileChooser();
	   	  DefaultFileFilter dff = new DefaultFileFilter("mag","Magnetic story files");
	   	fileChooser.setFileFilter(dff);
		 int res = fileChooser.showOpenDialog(owner);
	         if (res == JFileChooser.APPROVE_OPTION) {
	             File file =

	                 fileChooser.getSelectedFile();
	             try {
					gameFile.append(file.getCanonicalPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
	         }
		 return gameFile;
	}
		
	protected String selectGameDir()
	{
		final StringBuffer gameDir = new StringBuffer();
		final JMagnetic2 owner = this;
		 JFileChooser fileChooser = new JFileChooser();
		 fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		 int res = fileChooser.showOpenDialog(owner);
             if (res == JFileChooser.APPROVE_OPTION) {
	             
            	 gameDir.append(fileChooser.getSelectedFile().toString());
	         }
		 return gameDir.toString();
	}
	
	protected void startInterpreter(final String gameTag, final String storySelection) {
	      final String msg = "\nLoading game data and graphics...please wait!";
	      final String msg2 = "\n...loading the selected story file failed!\n[EXITING]";
	      final JMagnetic2 owner = this;
	      Runnable startEngine = new Runnable() {
	         public void run() {
		            try {
						magInstance = new JMagneticSwing(owner,gameTag);
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					String gameDir =null;
					String storyFile=null;
					String gfxFile=null;
					String titleFile=null;
					String hintFile=null;
					String sndFile=null;
					String musicFile=null;
				    configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
					if (!storySelection.equals(""))
					{
					   storyFile = storySelection;
					   gfxFile = storySelection.replaceAll(".mag",".gfx");
					   titleFile = storySelection.replaceAll(".mag",".png");
					   hintFile = storySelection.replaceAll(".mag",".hnt");
					   musicFile = storySelection.replaceAll(".mag",".mp3");
					   sndFile = storySelection.replaceAll(".mag",".snd");
					   //System.out.println("Story:"+storyFile+",gfx:"+gfxFile);
					}
					else
					{
			       gameDir = configuration.getProperty("gamedir");
			       File f = new File(gameDir);
			       if (!f.isAbsolute())
			       {
			    	   gameDir = getBasePath()+gameDir;
			       }
	               storyFile = gameDir+File.separator+configuration.getProperty(gameTag+".story");
	               gfxFile = gameDir+File.separator+configuration.getProperty(gameTag+".graphics");
	               titleFile = gameDir+File.separator+configuration.getProperty(gameTag+".title");
	               hintFile = gameDir+File.separator+configuration.getProperty(gameTag+".hints");
	               sndFile = gameDir+File.separator+configuration.getProperty(gameTag+".mscores");
	               musicFile = gameDir+File.separator+configuration.getProperty(gameTag+".music");
					}
					
                   owner.applyConfiguration();					
	               //if (fileName == null)
	               //   storyFile = selectStoryFile();
	               if ((storyFile != null) && ( (new File(storyFile)).exists()))
	               {   
//	                  try {
//
	            	   final String title = titleFile;
	            	   final String music = musicFile;
	            	   final boolean musicon = configuration.getBooleanProperty("automusic", true);
					      
	            	   // TOO File exists!!!
	            	   if (title != null && !title.equals(""))
					      {
	           		try {
						SwingUtilities.invokeAndWait(new Runnable() {
						    public void run() {
						         JTitleScreen titleSplash = new JTitleScreen(owner,title,music,musicon,0,JTitleScreen.CENTER_APP);
						         //loadImage(titleFile);
						         //requestFocus();
						         //textWindow.requestFocus();
						         //magInstance.tsWait = true;
						      }
						});
					} catch (InterruptedException e2) {
						e2.printStackTrace();
					} catch (InvocationTargetException e2) {
						e2.printStackTrace();
					}
						    }

	                   // Instanciate Magnetic
	      
							appendEngineStatusText(msg);
	                      	//System.out.println("4 >>"+javax.swing.SwingUtilities.isEventDispatchThread());	
	                      try {
	                    	  magInstance.setGfxEnabled(magInstance.ms_init(storyFile,gfxFile, hintFile, sndFile));
						} catch (IOException e) {
							e.printStackTrace();
						}
	                   if (((JMagneticSwing)(magInstance)).getGfxEnabled() == 0) {
	                      try
	                      {
	                         appendEngineErrorText("[Loading game failed]\n");
		                      	//System.out.println("4 >>"+javax.swing.SwingUtilities.isEventDispatchThread());	
	                      }
	                      catch (Exception e1)
	                      {
	                      }
	                   }
	                   
	                   //System.out.println("G VERSION:"+(magInstance.gfx_ver));

	                   magInstance.setGfxEnabled((byte)(magInstance.getGfxEnabled()-(byte)1));
	                   //textWindow.setText("");
						clearInterpreterOutput();
	                   if (magInstance.gfx_ver == 2)
	                   {
	                      //statusLine.setPreferredSize(new java.awt.Dimension(63, 0));
	                      //statusLine.setMinimumSize(new java.awt.Dimension(4, 0));
	                      //SwingUtilities.updateComponentTreeUI(_owner);
	                   }
	                   magInstance.stopped = false;    
	                   magInstance.run();
	                   inputLine.requestFocus();

	                }
	                else
	                {
						appendEngineStatusText(msg2);
	                }
	             }
	         };
	      
	         try {
	            startEngine.run();
	         } catch (Exception ce) {
	            //System.out.println(ce);
	         }

	}

	public PipedWriter getKBOut()
	{
		return kbOut;
	}
	
	public void killKBOut()
	{
		kbOut=null;
	}
	
	public JMagneticImgPanel getGraphicsPanel()
	{
		return graphicsPanel;
	}

	public void appendEngineStatusText(final String text)
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				//System.out.println(""+javax.swing.SwingUtilities.isEventDispatchThread());
		        StyledDocument doc = interpreterOutput.getStyledDocument();		
				SimpleAttributeSet attrs = new SimpleAttributeSet();
		        StyleConstants.setForeground(attrs, Color.gray);
		        
		        try {
					doc.insertString(doc.getLength(), text, attrs);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
                inputLine.requestFocus();
		    }
		});

	}

	public void appendEngineErrorText(final String text)
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        StyledDocument doc = interpreterOutput.getStyledDocument();		
				SimpleAttributeSet attrs = new SimpleAttributeSet();
		        StyleConstants.setForeground(attrs, Color.red);
		        
		        try {
					doc.insertString(doc.getLength(), text, attrs);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
                inputLine.requestFocus();
		    }
		});

	}

	public void appendUserInput(final String text)
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        StyledDocument doc = interpreterOutput.getStyledDocument();		
		        
		        try {
					doc.insertString(doc.getLength(), text, userAttributes);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
                inputLine.requestFocus();
		    }
		});

		
	}

	public void appendGameOutput(final String textFull)
	{
		int length = textFull.length();
		for (int i=0;i < length; i++) {
			String text = textFull.substring(i, i+1);
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
					//System.out.println(""+javax.swing.SwingUtilities.isEventDispatchThread());
					StyledDocument doc = interpreterOutput.getStyledDocument();		
			        
			        try {
						doc.insertString(doc.getLength(), text, gameAttributes);
						String text = interpreterOutput.getText();
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (text.indexOf(">")>=0)
	                   inputLine.requestFocus();
			    }
			});

		}
	}
	
	public void clearInterpreterOutput()
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        StyledDocument doc = interpreterOutput.getStyledDocument();		
				try {
					doc.remove(0,doc.getLength()-1);
				} catch (BadLocationException e) {
				}
		    }
		});
	}
	
	public void clearStatusLine()
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				statusLineLeft.setText("");
				statusLineRight.setText("");
		    }
		});
	}
	
	public void statusLineAppend(final boolean isScore, final char c)
	{
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				if (isScore)
					statusLineRight.setText(statusLineRight.getText()+c);
				else
					statusLineLeft.setText(statusLineLeft.getText()+c);
		    }
		});
	}

	private static void loadSkinLFSettings(String[] args) {
        String pskinlfdir = configuration.getProperty("skinlfdir");
        File f = new File(pskinlfdir);
        if (!f.isAbsolute())
        {
        	pskinlfdir = getBasePath()+pskinlfdir;
        }
        final String skinlfdir = pskinlfdir;
        final String skinlftheme = configuration.getProperty("skinlftheme");
        // Launch the test program
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
		        	  // first tell SkinLF which theme to use
		            Skin theSkinToUse;
					try {
						theSkinToUse = SkinLookAndFeel.loadThemePack(skinlfdir+File.separator+skinlftheme+".zip");
			            SkinLookAndFeel.setSkin(theSkinToUse);

			            // finally set the Skin Look And Feel
			            UIManager.setLookAndFeel(new SkinLookAndFeel());
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void loadLiquidSettings(String[] args) {
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
		        	  // first tell SkinLF which theme to use
					try {
			            // finally set the Skin Look And Feel
			            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			            LiquidLookAndFeel.setLiquidDecorations(true, "mac");
					} catch (Exception e) {
						e.printStackTrace();
					}
			    }

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadQuaquaSettings(String[] args) {
        // Taken from QUAqua samples
        final java.util.List argList = Arrays.asList(args);

        // Explicitly turn on font antialiasing.
        try {
            System.setProperty("swing.aatext", "true");
        } catch (AccessControlException e) {
            // can't do anything about this
        }
        
        // Use screen menu bar, if not switched off explicitly
        try {
            if (System.getProperty("apple.laf.useScreenMenuBar") == null) {
            System.setProperty("apple.laf.useScreenMenuBar","true");
            }
            if (System.getProperty("com.apple.macos.useScreenMenuBar") == null) {
            System.setProperty("com.apple.macos.useScreenMenuBar","true");
            }
        } catch (AccessControlException e) {
            // can't do anything about this
        }
        
        // Turn on look and feel decoration when not running on Mac OS X.
        // This will still not look pretty, because we haven't got cast shadows
        // for the frame on other operating systems.
        boolean useDefaultLookAndFeelDecoration = 
                ! System.getProperty("os.name").toLowerCase().startsWith("mac");
                        int index = argList.indexOf("-decoration");
                        if (index != -1 && index < argList.size() - 1) {
                            useDefaultLookAndFeelDecoration = argList.get(index + 1).equals("true");
                        }

        if (useDefaultLookAndFeelDecoration) {
            try {
                Methods.invokeStatic(JFrame.class, "setDefaultLookAndFeelDecorated", Boolean.TYPE, Boolean.TRUE);
                Methods.invokeStatic(JDialog.class, "setDefaultLookAndFeelDecorated", Boolean.TYPE, Boolean.TRUE);
            } catch (NoSuchMethodException e) {
                // can't do anything about this
                //e.printStackTrace();
            }
        }
        
        // Launch the test program
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
			    public void run() {
			        int index = argList.indexOf("-laf");
			        String lafName;
			        if (index != -1 && index < argList.size() - 1) {
			            lafName = (String) argList.get(index + 1);
			        } else {
			            lafName = QuaquaManager.getLookAndFeelClassName();
			        
			        }
			        long lafCreate = 0;
			        try {
			            //UIManager.setLookAndFeel(lafName);
//				        QuaquaManager.setProperty("Quaqua.design", "tiger");
//			            LookAndFeel laf = (LookAndFeel) Class.forName(lafName).newInstance();
//			            UIManager.setLookAndFeel(laf);
			        	UIManager.setLookAndFeel(
			                    "ch.randelshofer.quaqua.QuaquaLookAndFeel"
			                );
			        	} catch (Exception e) {
			            //e.printStackTrace();
			            // can't do anything about this
			        }
			    }

			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public JMagnetic2() {
		super();
		kbOut = new PipedWriter();
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
	
	private void initUIManager(String[] args) {
	}

	private void initGUI() {
		try {
			{
				this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				this.setTitle("JMagnetic 2");
				BorderLayout thisLayout = new BorderLayout();
				getContentPane().setLayout(thisLayout);
				this.setSize(new java.awt.Dimension(600, 600));
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension scrnsize = toolkit.getScreenSize();
				int locX = (((int)scrnsize.getWidth())-600)/2;
				int locY = (((int)scrnsize.getHeight())-600)/2;
				this.setLocation(locX,locY);
				this.setSize(600, 600);
				this.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						thisWindowClosing(evt);
					}
				});
				{
					contentPane = new JPanel();
					BorderLayout contentPaneLayout = new BorderLayout();
					getContentPane().add(contentPane, BorderLayout.CENTER);
					contentPane.setLayout(contentPaneLayout);
					contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
					{
						inputLine = new JTextField();
						contentPane.add(inputLine, BorderLayout.SOUTH);
						inputLine.setSelectedTextColor(new java.awt.Color(255,
								255, 128));
						inputLine.setDoubleBuffered(true);
						inputLine.setMargin(new java.awt.Insets(3, 3, 3, 3));
						inputLine.addKeyListener(new KeyAdapter() {
							public void keyPressed(KeyEvent evt) {
								inputLineKeyPressed(evt);
							}
						});
					}
					{
						splitPane = new JSplitPane();
						contentPane.add(splitPane, BorderLayout.CENTER);
						splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
						splitPane.setBackground(new java.awt.Color(0, 0, 0));
						splitPane.setBorder(BorderFactory.createEmptyBorder(
								0, 0, 5, 0));
						splitPane.setOpaque(false);
						splitPane.setDividerLocation(0);
						splitPane.setDividerSize(5);
						{
							graphicsPanel = new JMagneticImgPanel();
							splitPane.add(graphicsPanel, JSplitPane.TOP);
							graphicsPanel.setMinimumSize(new java.awt.Dimension(10, 0));
						}
						{
							jScrollPane1 = new JScrollPane();
							splitPane.add(jScrollPane1, JSplitPane.BOTTOM);
							jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
							jScrollPane1.getVerticalScrollBar().setAutoscrolls(true);
							jScrollPane1
									.addComponentListener(new ComponentAdapter() {
										public void componentResized(
												ComponentEvent evt) {
											jScrollPane1ComponentResized(evt);
										}
									});
							{
								interpreterOutput = new JTextPane();
								jScrollPane1.setViewportView(interpreterOutput);
								interpreterOutput.setEditable(false);
								interpreterOutput
										.addComponentListener(new ComponentAdapter() {
											public void componentResized(
													ComponentEvent evt) {
												interpreterOutputComponentResized(evt);
											}
										});
							}
						}
					}
					{
						statusLinePanel = new JPanel();
						GridLayout jPanel1Layout = new GridLayout(1, 2);
						jPanel1Layout.setHgap(0);
						jPanel1Layout.setVgap(0);
						jPanel1Layout.setColumns(2);
						contentPane.add(statusLinePanel, BorderLayout.NORTH);
						statusLinePanel.setLayout(jPanel1Layout);
						{
							statusLineLeft = new JLabel();
							statusLinePanel.add(statusLineLeft);
							statusLineLeft.setText("");
							statusLineLeft.setHorizontalTextPosition(SwingConstants.LEADING);
						}
						{
							statusLineRight = new JLabel();
							statusLinePanel.add(statusLineRight);
							statusLineRight.setText("");
							statusLineRight
									.setHorizontalAlignment(SwingConstants.TRAILING);
						}
					}
				}
			}
			{
				jmagneticMenu = new JMenuBar();
				setJMenuBar(jmagneticMenu);
				jmagneticMenu.setOpaque(false);
				{
					jMenu3 = new JMenu();
					jmagneticMenu.add(jMenu3);
					jMenu3.setText("File");
					{
						openFileMenuItem = new JMenuItem();
						jMenu3.add(getJMenuItem1());
						jMenu3.add(getJSeparator9());
						jMenu3.add(openFileMenuItem);
						openFileMenuItem.setText("Open script");
						openFileMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_O);
						openFileMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										openFileMenuItemActionPerformed(evt);
									}
								});
					}
					{
						saveMenuItem = new JMenuItem();
						jMenu3.add(saveMenuItem);
						saveMenuItem.setText("Save script");
						saveMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_S);
						saveMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								saveMenuItemActionPerformed(evt);
							}
						});
					}
					{
						saveAsMenuItem = new JMenuItem();
						jMenu3.add(saveAsMenuItem);
						saveAsMenuItem.setText("Save transcript");
						saveAsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_T);
						saveAsMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								saveAsMenuItemActionPerformed(evt);
							}
						});
					}
					{
						jSeparator2 = new JSeparator();
						jMenu3.add(jSeparator2);
					}
					{
						closeFileMenuItem = new JMenuItem();
						jMenu3.add(closeFileMenuItem);
						closeFileMenuItem.setText("Preferences");
						closeFileMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_P);
						closeFileMenuItem
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										closeFileMenuItemActionPerformed(evt);
									}
								});
					}
					{
						jSeparator4 = new JSeparator();
						jMenu3.add(jSeparator4);
					}
					{
						exitMenuItem = new JMenuItem();
						jMenu3.add(exitMenuItem);
						exitMenuItem.setText("Exit");
						exitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
						exitMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								exitMenuItemActionPerformed(evt);
							}
						});
					}
				}
				{
					jMenu4 = new JMenu();
					jmagneticMenu.add(jMenu4);
					jmagneticMenu.add(getJMenu1());
					jMenu4.setText("Edit");
					{
						cutMenuItem = new JMenuItem();
						jMenu4.add(cutMenuItem);
						cutMenuItem.setText("Cut");
					}
					{
						copyMenuItem = new JMenuItem();
						jMenu4.add(copyMenuItem);
						copyMenuItem.setText("Copy");
					}
					{
						pasteMenuItem = new JMenuItem();
						jMenu4.add(pasteMenuItem);
						pasteMenuItem.setText("Paste");
					}
					{
						jSeparator1 = new JSeparator();
						jMenu4.add(jSeparator1);
					}
					{
						deleteMenuItem = new JMenuItem();
						jMenu4.add(deleteMenuItem);
						jMenu4.add(getJSeparator5());
						jMenu4.add(getImgSave());
						deleteMenuItem.setText("Delete");
					}
				}
				{
					jMenu5 = new JMenu();
					jmagneticMenu.add(jMenu5);
					jMenu5.setText("Help");
					{
						helpMenuItem = new JMenuItem();
						jMenu5.add(helpMenuItem);
						helpMenuItem.setText("Help");
						helpMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_H);
						helpMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								helpMenuItemActionPerformed(evt);
							}
						});
					}
					{
						jSeparator3 = new JSeparator();
						jMenu5.add(jSeparator3);
					}
					{
						aboutMenuItem = new JMenuItem();
						jMenu5.add(aboutMenuItem);
						aboutMenuItem.setText("About");
						aboutMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_A);
						aboutMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								aboutMenuItemActionPerformed(evt);
							}
						});
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private static void loadConfiguration() {
		String install_path = getBasePath();  

		File file = new File(install_path+"conf"+File.separator+"jmagnetic2.xml");
		XMLFileHandler handler = new XMLFileHandler();
		handler.setFile(file);
		try {
		//System.out.println("trying to load file");
		cm.load(handler,"jmagnetic2");
		//System.out.println("file successfully processed");
		}
		catch (Exception e) {
		e.printStackTrace();
		}				
	}

	private void inputLineKeyTyped(KeyEvent evt) {
	}

	private void inputLineKeyPressed(KeyEvent evt) {
		//System.out.println("inputLine.keyPressed, event=" + evt);
		if (evt.getKeyCode()==10)
		{
			try {
				appendUserInput(inputLine.getText()+"\n");
				kbOut.write(inputLine.getText()+"\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			inputLine.setText("");
			evt.consume();
		}
	}

	   /* Scripting INTERFACE */
	   /* INTERFACE */
	   public void runScript(File file)
	   { 
	      try
			{
	    	  magInstance.setLog(new RandomAccessFile( file, "r" ));
	            if ( magInstance.getLog() != null) magInstance.setLogOn((byte)1);
	            if (magInstance.getLog() == null) {
					this.appendEngineErrorText("\n[Failed to open \""+ file.getName()
					                  + "\" for reading.]\n");
	            }
	            else {
	                kbOut.write("\n");
	            }  
			}
			catch (FileNotFoundException e)
			{
			} catch (IOException e)
			{
			}
	   }
	
	   public void saveScript(File file)
	   { 
	      try
			{
	    	  magInstance.setLog(new RandomAccessFile( file, "rw" ));
	            if ( magInstance.getLog() != null) ((JMagneticSwing)(magInstance)).setLogOn((byte)2);
	            if (magInstance.getLog() == null) {
	               this.appendEngineErrorText("\n[Failed to open \""+ file.getName()
	                                        + "\" for writing.]\n");
	            }
			}
			catch (FileNotFoundException e)
			{
			}
	   }

	   /* INTERFACE */
	   public void startTransscript(File file)
	   { 
	      try
			{
	    	  magInstance.setLog2(new RandomAccessFile( file, "rw" ));
	            if (magInstance.getLog2() == null) {
	               this.appendEngineErrorText("\n[Failed to open \""+ file.getName() + "\" for writing.]\n");
	            }
			}
			catch (FileNotFoundException e)
			{
			}
	   }

	// Animation interface
	/**
	 * @param main_pic
	 */
	public void playAnimation()
	{
		graphicsPanel.playAnimation(magInstance);
		
	}

	/**
	 * 
	 */
	public void stopAnimation()
	{
		graphicsPanel.stopAnimation();
		
	}
   
   public boolean is_ani_running()
   {
      return graphicsPanel.is_ani_running();
   }

   public int getPicIndex() {
	      return graphicsPanel.getPicIndex();	
	   }
	    
	   public void setPicHeight(int NewPHeight) {
	      graphicsPanel.setPicHeight(NewPHeight);	
	   }
	    
	   public void setPicWidth(int NewPWidth) {
	      graphicsPanel.setPicWidth(NewPWidth);	
	   }
	   
	   public void setGraphic(int picId, char[] PicData, int dataOffset, int[]  PicPalette ) {
	  	int currentLocation = splitPane.getDividerLocation();
	  	int newLocation = graphicsPanel.getPicHeight()+20;
	  	splitPane.setDividerLocation( Math.max(currentLocation, newLocation) );
	  	graphicsPanel.setGraphic(picId, PicData, dataOffset, PicPalette );
	   }
	   public synchronized void rollUp() {
		      splitPane.setDividerLocation( 0 );
		   }

	private void jScrollPane1ComponentResized(ComponentEvent evt) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum());
		    }
		});
	}

	private void interpreterOutputComponentResized(ComponentEvent evt) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	jScrollPane1.getVerticalScrollBar().setValue(jScrollPane1.getVerticalScrollBar().getMaximum());
		    }
		});
	//System.out.println("jScrollPane1.componentResized, event=" + evt);
	}

	private void openFileMenuItemActionPerformed(ActionEvent evt) {
		//System.out.println("openFileMenuItem.actionPerformed, event=" + evt);
		 JFileChooser fileChooser = new JFileChooser();
		 int res = fileChooser.showOpenDialog(this);
         if (res == JFileChooser.APPROVE_OPTION) {
             File file =

                 fileChooser.getSelectedFile();
             runScript(file);
         }
	}

	private void saveAsMenuItemActionPerformed(ActionEvent evt) {
		//System.out.println("saveAsMenuItem.actionPerformed, event=" + evt);
		 JFileChooser fileChooser = new JFileChooser();
		 int res = fileChooser.showSaveDialog(this);
         if (res == JFileChooser.APPROVE_OPTION) {
             File file =

                 fileChooser.getSelectedFile();
             startTransscript(file);
         }
	}

	private void saveMenuItemActionPerformed(ActionEvent evt) {
		//System.out.println("saveMenuItem.actionPerformed, event=" + evt);
		 JFileChooser fileChooser = new JFileChooser();
		 int res = fileChooser.showSaveDialog(this);
         if (res == JFileChooser.APPROVE_OPTION) {
             File file =

                 fileChooser.getSelectedFile();
             saveScript(file);
         }
	}

	private void thisWindowClosing(WindowEvent evt) {
	       shutdownMagnetic(false);
	}

	private void exitMenuItemActionPerformed(ActionEvent evt) {
		//System.out.println("exitMenuItem.actionPerformed, event=" + evt);
       shutdownMagnetic(false);
	}

	public void shutdownMagnetic(boolean forceShutdown)
	{
		final StringBuffer shutdown = new StringBuffer("");
		final JMagnetic2 owner = this;
		if (magInstance != null && !forceShutdown)
		{
	        JOptionPane pane = new JOptionPane(
	                "<html>"+
	                "<head>"+
	                "<style type=\"text/css\">"+
	                "b { font: 13pt \"Lucida Grande\" }"+
	                "p { font: 11pt \"Lucida Grande\"; margin-top: 8px }"+
	                "</style>"+
	                "</head>"+
	                "<b>Do you want to save your game<br>"+
	                "before closing?</b><p>"+
	                "If you don't save, your game progress will be lost.",
	                JOptionPane.WARNING_MESSAGE
	                );
	                Object[] options = { "Save", "Cancel", "Don't Save" };
	                pane.setOptions(options);
	                pane.setInitialValue(options[0]);
	                pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
	                JSheet.showSheet(pane, this, new SheetListener() {
	                    public void optionSelected(SheetEvent evt) {
	                    	//System.out.println(evt.getOption());
	                    	if (evt.getOption() == 0)
	                    	{
	                    		magInstance.setSaverequest(true);
	            				appendUserInput("save\n");
	            				try {
									kbOut.write("save\n");
								} catch (IOException e) {
									e.printStackTrace();
								}
								owner.inputLine.requestFocus();
	                    	}
	                    	else if (evt.getOption() == 2)
	                        {
	                    		if (magInstance.sequencer != null)
	                    		{
	                    			magInstance.sequencer.stop();
	                    			magInstance.sequencer.close();
	                    		}
	                           magInstance.running = 0;
	                 		   magInstance.stop();
	                 		   magInstance = null;
	                 		  try {
	                 				kbOut.close();
	                 			} catch (IOException e) {
	                 				e.printStackTrace();
	                 			}
	                 			kbOut = null;
	                 			// Kill gameselector
	                 			if (gameSelect != null)
	                 			{
	                 				gameSelect.stopMP3();
	                 				gameSelect.dispose();
	                 				gameSelect = null;
	                 			}
	                 			
	                 		      //Kill main frame
	                 			owner.setVisible(false);
	                 			owner.dispose();
                                 System.exit(0);	                 			
	                 			}	                        
	                    }
	                });
		} else
		{
		// Kill pipe
		try {
			kbOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		kbOut = null;
		// Kill gameselector
		if (gameSelect != null)
		{
			gameSelect.stopMP3();
			gameSelect.dispose();
			gameSelect = null;
		}
		
	      //Kill main frame
		this.setVisible(false);
		this.dispose();
		System.exit(0);
		
		}
	}
	
	private void newGameMenuItemActionPerformed(ActionEvent evt) {
	}

	private void aboutMenuItemActionPerformed(ActionEvent evt) {
        JMagneticAbout aboutbox = new JMagneticAbout(null, new Dimension(500,450));
        aboutbox.showInfo();
	}

	private void helpMenuItemActionPerformed(ActionEvent evt) {
		JHelp helpViewer = null; 
		try { 
		      ClassLoader cl = JMagnetic2.class.getClassLoader(); 
		      URL url = HelpSet.findHelpSet(cl, "helpset.hs"); 
		      helpViewer = new JHelp(new HelpSet(cl, url)); 
		      helpViewer.setCurrentID("overview_html"); 
		} catch (Exception e) { 
		      System.err.println("API Help Set not found"); 
		} 
		         
		JFrame frame = new JFrame(); 
		frame.setTitle("JMagnetic 2 help"); 
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scrnsize = toolkit.getScreenSize();
		int locX = (((int)scrnsize.getWidth())-800)/2;
		int locY = (((int)scrnsize.getHeight())-600)/2;
		frame.setLocation(locX,locY);
		frame.setSize(800,600); 
		frame.getContentPane().add(helpViewer); 
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		frame.setVisible(true); 
	}

	private void closeFileMenuItemActionPerformed(ActionEvent evt) {
		//System.out.println("closeFileMenuItem.actionPerformed, event=" + evt);
		JMagneticSettings prefDlg = new JMagneticSettings(this);
		prefDlg.setVisible(true);
	}
	
	public void applyConfiguration()
	{
	       configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
			
			// Configure logging
		    magInstance.enableEmuLog(configuration.getBooleanProperty("logemu",false));
			magInstance.enableHntLog(configuration.getBooleanProperty("loghnt",false));
			magInstance.enableSndLog(configuration.getBooleanProperty("logsnd",false));
			magInstance.enableGfxLog(configuration.getBooleanProperty("loggfx",false));
			magInstance.enableGfxExtLog(configuration.getBooleanProperty("loggfxext",false));
			// Configure graphics
			magInstance.waitForAnims = configuration.getBooleanProperty("scriptswaitforanim",false);
			magInstance.setAutographics(configuration.getBooleanProperty("autographics",true));
			magInstance.setUseHintWindow(configuration.getBooleanProperty("showhints",true));
			magInstance.setLastSaveDir(configuration.getBooleanProperty("defaultsavedir",false));
            magInstance.setSndDevice(configuration.getProperty("snddevice","Java Sound Synthesizer"));
			graphicsPanel.setGamma(Double.parseDouble(configuration.getProperty("gamma")));
            // Configure fonts and background
			interpreterOutput.setBackground(new Color((int)(250/1.4),(int)(255/1.5),250/2));//Integer.parseInt(configuration.getProperty("output.background"))));// 250,255,250
            userAttributes = new SimpleAttributeSet();
			StyleConstants.setFontFamily(userAttributes, configuration.getProperty("user.fontname","Tahoma"));
	        StyleConstants.setFontSize(userAttributes, Integer.parseInt(configuration.getProperty("user.fontsize","12")));
	        StyleConstants.setForeground(userAttributes, new Color(Integer.parseInt(configuration.getProperty("user.fontcolor","-1"))));
	        int fs = Integer.parseInt(configuration.getProperty("user.fontstyle"));
	        if (fs == Font.BOLD || fs == (Font.ITALIC+Font.BOLD))
	           StyleConstants.setBold(userAttributes, true);
	        else
		       StyleConstants.setBold(userAttributes, false);
	        if (fs == Font.ITALIC || fs == (Font.ITALIC+Font.BOLD))
		       StyleConstants.setItalic(userAttributes, true);
	        else
	           StyleConstants.setItalic(userAttributes, false);

	        gameAttributes = new SimpleAttributeSet();
			StyleConstants.setFontFamily(gameAttributes, configuration.getProperty("output.fontname","Tahoma"));
	        StyleConstants.setFontSize(gameAttributes, Integer.parseInt(configuration.getProperty("output.fontsize","12")));
	        StyleConstants.setForeground(gameAttributes, new Color(Integer.parseInt(configuration.getProperty("output.fontcolor","-1"))));
	        StyleConstants.setBold(gameAttributes, true);
	        int fs2 = Integer.parseInt(configuration.getProperty("output.fontstyle"));
	        if (fs2 == Font.BOLD || fs2 == (Font.ITALIC+Font.BOLD))
	           StyleConstants.setBold(gameAttributes, true);
	        else
		       StyleConstants.setBold(gameAttributes, false);
	        if (fs2 == Font.ITALIC || fs2 == (Font.ITALIC+Font.BOLD))
		       StyleConstants.setItalic(gameAttributes, true);
	        else
	           StyleConstants.setItalic(gameAttributes, false);

			// Configure L&F
			// LAF requires restart!
	}
	
	void setUserAttributes( SimpleAttributeSet userAttributes)
	{
		this.userAttributes = userAttributes;
	}

	void setGameAttributes( SimpleAttributeSet gameAttributes)
	{
		this.gameAttributes = gameAttributes;
	}
	
	private static String getBasePath()
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
	
	private JSeparator getJSeparator5() {
		if(jSeparator5 == null) {
			jSeparator5 = new JSeparator();
		}
		return jSeparator5;
	}
	
	private JMenuItem getImgSave() {
		if(imgSave == null) {
			imgSave = new JMenuItem();
			imgSave.setText("Save image as PNG");
			imgSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					imgSaveActionPerformed(evt);
				}
			});
		}
		return imgSave;
	}
	
	private void imgSaveActionPerformed(ActionEvent evt) {
		String filename;
		Image img = this.graphicsPanel.getImage(); 
       if ( img != null)
       {
		   	  JFileChooser fc = new JFileChooser();
		   	  DefaultFileFilter dff = new DefaultFileFilter("png","PNG Graphics");
		   	  fc.setFileFilter(dff);
		      int returnVal = fc.showSaveDialog(this);
		      if (returnVal == JFileChooser.APPROVE_OPTION) {
		         File file = fc.getSelectedFile();
		    	   BufferedImage bufImage =
		    		     new BufferedImage(this.graphicsPanel.getPicWidth(),
		    		    		           this.graphicsPanel.getPicHeight(),
		    		    		           BufferedImage.TYPE_INT_ARGB);
		    	   Graphics2D bufImageGraphics = bufImage.createGraphics();
		    	   bufImageGraphics.drawImage(img, 0, 0, null);   
		    	   try {
					ImageIO.write( bufImage, "png", file );
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
    	}
	}
	
	private JMenu getJMenu1() {
		if(jMenu1 == null) {
			jMenu1 = new JMenu();
			jMenu1.setText("Tools");
			jMenu1.add(getXtract64Menu());
			jMenu1.add(getXtractPCMenu());
			jMenu1.add(getXtractMWMenu());
			jMenu1.add(getJSeparator6());
			jMenu1.add(getGfxLinkMenu());
			jMenu1.add(getGfxLink2Menu());
			jMenu1.add(getJSeparator7());
			jMenu1.add(getHintLinkMenu());
			jMenu1.add(getJSeparator8());
			jMenu1.add(getMythPassMenu());
		}
		return jMenu1;
	}
	
	private JMenuItem getXtract64Menu() {
		if(xtract64Menu == null) {
			xtract64Menu = new JMenuItem();
			xtract64Menu.setText("Xtract64");
			xtract64Menu.setEnabled(false);
			xtract64Menu.setVisible(false);
			xtract64Menu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					xtract64MenuActionPerformed(evt);
				}
			});
		}
		return xtract64Menu;
	}
	
	private JMenuItem getXtractPCMenu() {
		if(xtractPCMenu == null) {
			xtractPCMenu = new JMenuItem();
			xtractPCMenu.setText("XtractPC");
			xtractPCMenu.setEnabled(false);
			xtractPCMenu.setVisible(false);
		}
		return xtractPCMenu;
	}
	
	private JMenuItem getXtractMWMenu() {
		if(xtractMWMenu == null) {
			xtractMWMenu = new JMenuItem();
			xtractMWMenu.setText("XtractMW");
			xtractMWMenu.setEnabled(false);
			xtractMWMenu.setVisible(false);
		}
		return xtractMWMenu;
	}
	
	private JSeparator getJSeparator6() {
		if(jSeparator6 == null) {
			jSeparator6 = new JSeparator();
			jSeparator6.setVisible(false);
		}
		return jSeparator6;
	}
	
	private JMenuItem getGfxLinkMenu() {
		if(gfxLinkMenu == null) {
			gfxLinkMenu = new JMenuItem();
			gfxLinkMenu.setText("GfxLink");
			gfxLinkMenu.setEnabled(false);
			gfxLinkMenu.setVisible(false);
		}
		return gfxLinkMenu;
	}
	
	private JMenuItem getGfxLink2Menu() {
		if(gfxLink2Menu == null) {
			gfxLink2Menu = new JMenuItem();
			gfxLink2Menu.setText("GfxLink (MW)");
			gfxLink2Menu.setEnabled(false);
			gfxLink2Menu.setVisible(false);
		}
		return gfxLink2Menu;
	}
	
	private JSeparator getJSeparator7() {
		if(jSeparator7 == null) {
			jSeparator7 = new JSeparator();
			jSeparator7.setVisible(false);
		}
		return jSeparator7;
	}
	
	private JMenuItem getHintLinkMenu() {
		if(hintLinkMenu == null) {
			hintLinkMenu = new JMenuItem();
			hintLinkMenu.setText("HintLink");
			hintLinkMenu.setEnabled(false);
			hintLinkMenu.setVisible(false);
		}
		return hintLinkMenu;
	}
	
	private JSeparator getJSeparator8() {
		if(jSeparator8 == null) {
			jSeparator8 = new JSeparator();
			jSeparator8.setVisible(false);
		}
		return jSeparator8;
	}
	
	private JMenuItem getMythPassMenu() {
		if(mythPassMenu == null) {
			mythPassMenu = new JMenuItem();
			mythPassMenu.setText("Myth Password");
			mythPassMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mythPassMenuActionPerformed(evt);
				}
			});
		}
		return mythPassMenu;
	}

		private void mythPassMenuActionPerformed(ActionEvent evt) {
			String s = (String)JOptionPane.showInputDialog(
			                    this,
			                    "Enter your name:\n",
			                    "Myth Password Generator",
			                    JOptionPane.INFORMATION_MESSAGE,
			                    null,
			                    null,
			                    "");

			if ((s != null) && (s.length() > 0))
			{
			    MythPassGen mpg = new MythPassGen();
			    String pass = mpg.genPassword(s);
			    JOptionPane.showMessageDialog(this, "The password is: "+pass,
			    		"Myth password", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		private void xtract64MenuActionPerformed(ActionEvent evt) {
			XTract64Dialog xt64 = new XTract64Dialog(this);
			xt64.setVisible(true);
		}

		public void clearInputLine() {
			inputLine.setText("");
			
		}
		
		private JMenuItem getJMenuItem1() {
			if(jMenuItem1 == null) {
				jMenuItem1 = new JMenuItem();
				jMenuItem1.setText("Start new game");
				jMenuItem1.setMnemonic(java.awt.event.KeyEvent.VK_N);
				jMenuItem1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						jMenuItem1ActionPerformed(evt);
					}
				});
			}
			return jMenuItem1;
		}
		
		private void jMenuItem1ActionPerformed(ActionEvent evt) {
			final StringBuffer shutdown = new StringBuffer("");
			final JMagnetic2 owner = this;
			if (magInstance != null)
			{
		        JOptionPane pane = new JOptionPane(
		                "<html>"+
		                "<head>"+
		                "<style type=\"text/css\">"+
		                "b { font: 13pt \"Lucida Grande\" }"+
		                "p { font: 11pt \"Lucida Grande\"; margin-top: 8px }"+
		                "</style>"+
		                "</head>"+
		                "<b>Do you want to save your game<br>"+
		                "before closing?</b><p>"+
		                "If you don't save, your game progress will be lost.",
		                JOptionPane.WARNING_MESSAGE
		                );
		                Object[] options = { "Save", "Cancel", "Don't Save" };
		                pane.setOptions(options);
		                pane.setInitialValue(options[0]);
		                pane.putClientProperty("Quaqua.OptionPane.destructiveOption", new Integer(2));
		                JSheet.showSheet(pane, this, new SheetListener() {
		                    public void optionSelected(SheetEvent evt) {
		                    	//System.out.println(evt.getOption());
		                    	if (evt.getOption() == 0)
		                    	{
		                    		magInstance.setSaverequest2(true);
		            				appendUserInput("save\n");
		            				try {
										kbOut.write("save\n");
									} catch (IOException e) {
										e.printStackTrace();
									}
									owner.inputLine.requestFocus();
		                    	}
		                    	else if (evt.getOption() == 2)
		                        {
		                			JMagneticGameStarter gs = new JMagneticGameStarter(owner);
		                			gs.start();
		                        }
		                    }
		                });
			}
			else
			{
    			JMagneticGameStarter gs = new JMagneticGameStarter(owner);
    			gs.start();
				
			}
		}

		public void resetDividerLocation() {
			splitPane.setDividerLocation(0);
			
		}
		
		private JSeparator getJSeparator9() {
			if(jSeparator9 == null) {
				jSeparator9 = new JSeparator();
			}
			return jSeparator9;
		}
}


