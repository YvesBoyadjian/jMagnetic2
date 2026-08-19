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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;

import org.iflegends.msmemorial.swing.JFontChooser;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;
import org.jconfig.ConfigurationManagerException;


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
public class JMagneticSettings extends javax.swing.JDialog {
	private JPanel jPanel1;
	private JButton gameFontButton;
	private JCheckBox logsndCheck;
	private JCheckBox loggfxextCheck;
	private JCheckBox loggfxCheck;
	private JCheckBox loghntCheck;
	private JCheckBox logemuCheck;
	private JPanel jPanel3;
	private JLabel jLabel4;
	private JCheckBox animWaitCheckBox;
	private JButton bgSelectButton;
	private JPanel jPanel7;
	private JCheckBox saveDirCheckBox;
	private JButton saveSettingsBtn;
	private JButton cancelSettingsBtn;
	private JSpinner gammaValue;
	private JLabel jLabel10;
	private JButton userFontButton;
	private JLabel jLabel9;
	private JSpinner titleScale;
	private JLabel jLabel7;
	private JCheckBox autoGraphCheck;
	private JPanel jPanel5;
	private JCheckBox automusicCheckBox;
	private JComboBox lfComboBox;
	private JPanel jPanel4;
	private JLabel jLabel2;
	private JLabel jLabel1;
	private JPanel jPanel2;
	
	private JCheckBox hintsWndCheck;
	private Font gameFont;
	private JPanel jPanel6;
	private Color gameFontColor;
	private Font userFont;
	private Color userFontColor;
	private boolean newConf = false;
	private JLabel jLabel5;
	private JComboBox mdComboBox;
	
	private static final ConfigurationManager cm = ConfigurationManager.getInstance();
	
	public JMagneticSettings(JFrame frame) {
		super(frame);
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				this.setTitle("Preferences");
				this.setModal(true);
				Toolkit toolkit = Toolkit.getDefaultToolkit();

				// Get the current screen size
				Dimension scrnsize = toolkit.getScreenSize();
				int locX = (((int)scrnsize.getWidth())-523)/2;
				int locY = (((int)scrnsize.getHeight())-420)/2;
				this.setLocation(locX,locY);
				this.setResizable(false);
				Configuration configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
				{
					jPanel1 = new JPanel();
					GridBagLayout jPanel1Layout = new GridBagLayout();
					jPanel1Layout.columnWidths = new int[] {7, 7};
					jPanel1Layout.rowHeights = new int[] {7, 7, 7, 7};
					jPanel1Layout.columnWeights = new double[] {0.1, 0.1};
					jPanel1Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
					getContentPane().add(jPanel1, BorderLayout.CENTER);
					jPanel1.setLayout(jPanel1Layout);
					jPanel1.setBorder(BorderFactory.createTitledBorder(""));
					jPanel1.setPreferredSize(new java.awt.Dimension(434, 410));
					{
						jPanel2 = new JPanel();
						GridBagLayout jPanel2Layout = new GridBagLayout();
						jPanel2Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
						jPanel2Layout.rowHeights = new int[] {7, 7, 7, 7};
						jPanel2Layout.columnWeights = new double[] {0.1, 0.1};
						jPanel2Layout.columnWidths = new int[] {7, 7};
						jPanel2.setLayout(jPanel2Layout);
						jPanel1.add(jPanel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 3, 3));
						jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Game window", TitledBorder.LEADING, TitledBorder.TOP));
						jPanel2.setPreferredSize(new java.awt.Dimension(240, 130));
						jPanel2.setSize(240, 130);
						jPanel2.setMinimumSize(new java.awt.Dimension(240, 130));
						{
							jLabel1 = new JLabel();
							jPanel2.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel1.setText("Game text");
						}
						{
							gameFontButton = new JButton();
							jPanel2.add(gameFontButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							gameFontButton.setText("Select font");
							gameFontButton.setPreferredSize(new java.awt.Dimension(101, 26));
							gameFontButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									gameFontButtonActionPerformed(evt);
								}
							});
						}
						{
							jLabel2 = new JLabel();
							jPanel2.add(jLabel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel2.setText("Background");
						}
						{
							jLabel9 = new JLabel();
							jPanel2.add(jLabel9, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel9.setText("User text");
						}
						{
							userFontButton = new JButton();
							jPanel2.add(userFontButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							userFontButton.setText("Select font");
							userFontButton.setPreferredSize(new java.awt.Dimension(103, 27));
							userFontButton
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											userFontButtonActionPerformed(evt);
										}
									});
						}
						{
							bgSelectButton = new JButton();
							
							Color color;
							try
							{
							   color = new Color(Integer.parseInt(configuration.getProperty("output.background")));
							}
							catch (Exception e)
							{
								color = Color.gray;
							}
							   
							   //System.out.println(color.getRGB());
							bgSelectButton.putClientProperty("Quaqua.Button.style","colorWell");
							bgSelectButton.setBackground(color);
							jPanel2.add(bgSelectButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							bgSelectButton.setPreferredSize(new java.awt.Dimension(48, 26));
							bgSelectButton
									.addActionListener(new ActionListener() {
										public void actionPerformed(
												ActionEvent evt) {
											bgSelectButtonActionPerformed(evt);
										}
									});
						}
					}
					{
						jPanel3 = new JPanel();
						GridBagLayout jPanel3Layout = new GridBagLayout();
						jPanel3Layout.columnWidths = new int[] {7, 7};
						jPanel3Layout.rowHeights = new int[] {7, 7, 7, 7};
						jPanel3Layout.columnWeights = new double[] {0.1, 0.1};
						jPanel3Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
						jPanel1.add(jPanel3, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
						jPanel3.setLayout(jPanel3Layout);
						jPanel3.setBorder(BorderFactory.createTitledBorder("Logging"));
						jPanel3.setPreferredSize(new java.awt.Dimension(240, 130));
						jPanel3.setMinimumSize(new java.awt.Dimension(240, 130));
						{
							logemuCheck = new JCheckBox();
							jPanel3.add(logemuCheck, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							logemuCheck.setText("Log core messages");
							boolean isChecked = configuration.getBooleanProperty("logemu",false);
							if (isChecked)
								logemuCheck.setSelected(true);
							else
								logemuCheck.setSelected(false);
						}
						{
							loghntCheck = new JCheckBox();
							jPanel3.add(loghntCheck, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							loghntCheck.setText("Log hint messages");
							boolean isChecked = configuration.getBooleanProperty("loghnt",false);
							if (isChecked)
								loghntCheck.setSelected(true);
							else
								loghntCheck.setSelected(false);
						}
						{
							loggfxCheck = new JCheckBox();
							jPanel3.add(loggfxCheck, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							loggfxCheck.setText("Log graphics messages");
							boolean isChecked = configuration.getBooleanProperty("loggfx",false);
							if (isChecked)
								loggfxCheck.setSelected(true);
							else
								loggfxCheck.setSelected(false);
						}
						{
							loggfxextCheck = new JCheckBox();
							jPanel3.add(loggfxextCheck, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							loggfxextCheck.setText("Log more graphics messages");
							boolean isChecked = configuration.getBooleanProperty("loggfxext",false);
							if (isChecked)
								loggfxextCheck.setSelected(true);
							else
								loggfxextCheck.setSelected(false);
						}
						{
							logsndCheck = new JCheckBox();
							jPanel3.add(logsndCheck, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							logsndCheck.setText("Log sound messages");
							boolean isChecked = configuration.getBooleanProperty("logsnd",false);
							if (isChecked)
								logsndCheck.setSelected(true);
							else
								logsndCheck.setSelected(false);
						}
					}
					{
						jPanel4 = new JPanel();
						GridBagLayout jPanel4Layout = new GridBagLayout();
						jPanel4Layout.columnWidths = new int[] {7, 7, 7};
						jPanel4Layout.rowHeights = new int[] {7, 7, 7, 7};
						jPanel4Layout.columnWeights = new double[] {0.1, 0.1, 0.1};
						jPanel4Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
						jPanel1.add(jPanel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
						jPanel4.setLayout(jPanel4Layout);
						jPanel4.setBorder(BorderFactory.createTitledBorder("Application"));
						jPanel4.setPreferredSize(new java.awt.Dimension(240, 170));
						jPanel4.setMinimumSize(new java.awt.Dimension(240, 170));
						jPanel4.setSize(240, 170);
						{
							jLabel4 = new JLabel();
							jPanel4.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel4.setText("Look&Feel");
						}
						{
							ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(
									new String[] { "Quaqua default", "Quaqua Tiger", "Quaqua Leopard", "Liquid", "SkinLF", "SystemLF", "MetalLF", "Nimbus" });
							lfComboBox = new JComboBox();
							jPanel4.add(lfComboBox, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							lfComboBox.setModel(jComboBox1Model);
							lfComboBox.setPreferredSize(new java.awt.Dimension(121, 25));
							lfComboBox.setSelectedItem(configuration.getProperty("laf"));
						}
						{
							automusicCheckBox = new JCheckBox();
							jPanel4.add(automusicCheckBox, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							automusicCheckBox.setText("Autoplay music");
							boolean isChecked = configuration.getBooleanProperty("automusic",false);
							if (isChecked)
								automusicCheckBox.setSelected(true);
							else
								automusicCheckBox.setSelected(false);
						}
						{
							saveDirCheckBox = new JCheckBox();
							jPanel4.add(saveDirCheckBox, new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							saveDirCheckBox.setText("Remember last save dir");
							boolean isChecked = configuration.getBooleanProperty("defaultsavedir",false);
							if (isChecked)
								saveDirCheckBox.setSelected(true);
							else
								saveDirCheckBox.setSelected(false);
						}
						{
							hintsWndCheck = new JCheckBox();
							jPanel4.add(hintsWndCheck, new GridBagConstraints(
									0, 3, 3, 1, 0.0, 0.0,
									GridBagConstraints.WEST,
									GridBagConstraints.NONE, new Insets(0, 0,
											0, 0), 0, 0));
							hintsWndCheck
									.setText("Show hints in separate window");
							boolean isChecked = configuration.getBooleanProperty("showhints",true);
							if (isChecked)
								hintsWndCheck.setSelected(true);
							else
								hintsWndCheck.setSelected(false);
						}
					}
					{
						jPanel5 = new JPanel();
						GridBagLayout jPanel5Layout = new GridBagLayout();
						jPanel5Layout.columnWidths = new int[] {7, 7, 7};
						jPanel5Layout.rowHeights = new int[] {7, 7, 7, 7, 7};
						jPanel5Layout.columnWeights = new double[] {0.1, 0.1, 0.1};
						jPanel5Layout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1, 0.1};
						jPanel1.add(jPanel5, new GridBagConstraints(1, 0, 1, 1,
								0.0, 0.0, GridBagConstraints.CENTER,
								GridBagConstraints.NONE,
								new Insets(0, 0, 0, 0), 0, 0));
						jPanel5.setLayout(jPanel5Layout);
						jPanel5.setBorder(BorderFactory.createTitledBorder("Graphics"));
						jPanel5.setPreferredSize(new java.awt.Dimension(240, 170));
						jPanel5.setMinimumSize(new java.awt.Dimension(240, 170));
						{
							autoGraphCheck = new JCheckBox();
							jPanel5.add(autoGraphCheck, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							autoGraphCheck.setText("Auto-\"graphics\" for MW games");
							boolean isChecked = configuration.getBooleanProperty("autographics",true);
							if (isChecked)
								autoGraphCheck.setSelected(true);
							else
								autoGraphCheck.setSelected(false);
						}
						{
							animWaitCheckBox = new JCheckBox();
							jPanel5.add(animWaitCheckBox, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							animWaitCheckBox.setText("Scripts wait for animations");
							boolean isChecked = configuration.getBooleanProperty("scriptswaitforanim",false);
							if (isChecked)
								animWaitCheckBox.setSelected(true);
							else
								animWaitCheckBox.setSelected(false);
						}
						{
							jLabel7 = new JLabel();
							jPanel5.add(jLabel7, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel7.setText("Scale title pictures:");
						}
						{
							SpinnerNumberModel jSpinner1Model = new SpinnerNumberModel(1.5,0.5,2.5,0.1);
							titleScale = new JSpinner();
							jPanel5.add(titleScale, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							titleScale.setModel(jSpinner1Model);
							titleScale.getEditor().setPreferredSize(new java.awt.Dimension(23, 17));
							titleScale.setPreferredSize(new java.awt.Dimension(52, 31));
							titleScale.getEditor().setMinimumSize(new java.awt.Dimension(52, 31));
							titleScale.setMinimumSize(new java.awt.Dimension(52, 31));
							double titleS;
							try
							{
							   titleS = Double.parseDouble(configuration.getProperty("titlescale"));
							}
							catch (Exception e)
							{
							   titleS = 1.5;
							}
							
							titleScale.setValue(new Double(titleS));
						}
						{
							jLabel10 = new JLabel();
							jPanel5.add(jLabel10, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel10.setText("Gamma correction");
						}
						{
							SpinnerNumberModel jSpinner3Model = new SpinnerNumberModel(0.0,0.0,10.0,0.1);
							gammaValue = new JSpinner();
							jPanel5.add(gammaValue, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							gammaValue.setModel(jSpinner3Model);
							gammaValue.setPreferredSize(new java.awt.Dimension(52, 31));
							gammaValue.setMinimumSize(new java.awt.Dimension(52, 31));
							double gamma;
							try
							{
							   gamma = Double.parseDouble(configuration.getProperty("gamma"));
							}
							catch (Exception e)
							{
							   gamma = 0.0;
							}
							
							gammaValue.setValue(new Double(gamma));
						}
					}
					{
						jPanel6 = new JPanel();
						GridBagLayout jPanel6Layout = new GridBagLayout();
						jPanel1.add(jPanel6, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
						jPanel6.setBorder(BorderFactory.createTitledBorder("Music"));
						jPanel6Layout.rowWeights = new double[] {0.1};
						jPanel6Layout.rowHeights = new int[] {7};
						jPanel6Layout.columnWeights = new double[] {0.0, 0.0};
						jPanel6Layout.columnWidths = new int[] {-1, -1};
						jPanel6.setLayout(jPanel6Layout);
						jPanel6.setPreferredSize(new java.awt.Dimension(500, 80));
						jPanel6.setMinimumSize(new java.awt.Dimension(500, 80));
						jPanel6.setSize(500, 80);
						{
							jLabel5 = new JLabel();
							jPanel6.add(jLabel5, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							jLabel5.setText("Music device (Wonderland):  ");
						}
						{
							String mdevices[];
							int mcount = 0;
							MidiDevice.Info[]	aInfos = MidiSystem.getMidiDeviceInfo();
							mdevices = new String[aInfos.length];
							for (int i = 0; i < aInfos.length; i++) {
								try {
									MidiDevice	device = MidiSystem.getMidiDevice(aInfos[i]);
									boolean		bAllowsOutput = (device.getMaxReceivers() != 0);
									if (bAllowsOutput) {
										mdevices[mcount]=aInfos[i].getName();
										mcount++;
									}
								}
								catch (MidiUnavailableException e) {
									// device is obviously not available...
								}
							}
							ComboBoxModel jComboBox2Model = new DefaultComboBoxModel(mdevices);
							mdComboBox = new JComboBox();
							jPanel6.add(mdComboBox, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
							mdComboBox.setModel(jComboBox2Model);
							mdComboBox.setPreferredSize(new java.awt.Dimension(250, 25));
							mdComboBox.setSize(250, 25);
							mdComboBox.setMinimumSize(new java.awt.Dimension(250, 25));
							mdComboBox.setSelectedItem(configuration.getProperty("snddevice"));
						}
					}
				}
				{
					jPanel7 = new JPanel();
					getContentPane().add(jPanel7, BorderLayout.SOUTH);
					{
						saveSettingsBtn = new JButton();
						jPanel7.add(saveSettingsBtn);
						saveSettingsBtn.setText("Ok");
						saveSettingsBtn.setSelected(true);
						saveSettingsBtn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								saveSettingsBtnActionPerformed(evt);
							}
						});
					}
					{
						cancelSettingsBtn = new JButton();
						jPanel7.add(cancelSettingsBtn);
						cancelSettingsBtn.setText("Cancel");
						cancelSettingsBtn.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								cancelSettingsBtnActionPerformed(evt);
							}
						});
					}
				}
			}
			this.setSize(525, 440);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancelSettingsBtnActionPerformed(ActionEvent evt) {
		//System.out.println("cancelSettingsBtn.actionPerformed, event=" + evt);
		setVisible(false);
		dispose();
	}

	private void saveSettingsBtnActionPerformed(ActionEvent evt) {
		//System.out.println("saveSettingsBtn.actionPerformed, event=" + evt);
		setVisible(false);
		saveConfig();
		((JMagnetic2)this.getParent()).applyConfiguration();
		dispose();

	}

	private void saveConfig() {
		Configuration configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
        configuration.setProperty("snddevice", (String)mdComboBox.getSelectedItem());
        configuration.setProperty("laf", (String)lfComboBox.getSelectedItem());
        configuration.setProperty("automusic",Boolean.toString(automusicCheckBox.isSelected()));
        configuration.setProperty("defaultsavedir", Boolean.toString(saveDirCheckBox.isSelected()));
        configuration.setProperty("showhints", Boolean.toString(hintsWndCheck.isSelected()));
		configuration.setProperty("output.background", Integer.toString(bgSelectButton.getBackground().getRGB()));
        if (gameFont != null)
        {
    		configuration.setProperty("output.fontname", gameFont.getFamily());
    		configuration.setProperty("output.fontsize", Integer.toString(gameFont.getSize()));
    		configuration.setProperty("output.fontstyle", Integer.toString(gameFont.getStyle()));
        }
        if (gameFontColor != null)
        {
    		configuration.setProperty("output.fontcolor", Integer.toString(gameFontColor.getRGB()));
        	
        }
        if (userFont != null)
        {
    		configuration.setProperty("user.fontname", userFont.getFamily());
    		configuration.setProperty("user.fontsize", Integer.toString(userFont.getSize()));
    		configuration.setProperty("user.fontstyle", Integer.toString(userFont.getStyle()));
        }
        if (userFontColor != null)
        {
    		configuration.setProperty("user.fontcolor", Integer.toString(userFontColor.getRGB()));
        	
        }
        configuration.setProperty("autographics", Boolean.toString(autoGraphCheck.isSelected()));
        configuration.setProperty("scriptswaitforanim", Boolean.toString(animWaitCheckBox.isSelected()));
        // Scales
        configuration.setDoubleProperty("titlescale", ((Double)(titleScale.getValue())).doubleValue());
        configuration.setDoubleProperty("gamma", ((Double)(gammaValue.getValue())).doubleValue());
        configuration.setProperty("logemu", Boolean.toString(logemuCheck.isSelected()));
        configuration.setProperty("loghnt", Boolean.toString(loghntCheck.isSelected()));
        configuration.setProperty("loggfx", Boolean.toString(loggfxCheck.isSelected()));
        configuration.setProperty("loggfxext", Boolean.toString(loggfxextCheck.isSelected()));
        configuration.setProperty("logsnd", Boolean.toString(logsndCheck.isSelected()));
        try {
			cm.save("jmagnetic2");
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
	}

	private void gameFontButtonActionPerformed(ActionEvent evt) {
		//System.out.println("gameFontButton.actionPerformed, event=" + evt);
		Configuration configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
		String fontName = configuration.getProperty("output.fontname");
		int fontSize = Integer.parseInt(configuration.getProperty("output.fontsize"));
		int fontStyle = Integer.parseInt(configuration.getProperty("output.fontstyle"));
		int fontColor = Integer.parseInt(configuration.getProperty("output.fontcolor"));
		JFontChooser fc = new JFontChooser(null,fontName,fontSize,fontStyle,fontColor);
		fc.setVisible(true);
		if (fc.getNewFont() != null)
			gameFont = fc.getNewFont();
		if (fc.getNewColor() != null)
			gameFontColor = fc.getNewColor();
	}

	private void bgSelectButtonActionPerformed(ActionEvent evt) {
        JColorChooser chooser = new JColorChooser();
        Color color = chooser.showDialog(this, "Background color Chooser", bgSelectButton.getBackground());
        bgSelectButton.setBackground(color);
	}

	private void userFontButtonActionPerformed(ActionEvent evt) {
		//System.out.println("userFontButton.actionPerformed, event=" + evt);
		Configuration configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
		String fontName = configuration.getProperty("user.fontname");
		int fontSize = Integer.parseInt(configuration.getProperty("user.fontsize"));
		int fontStyle = Integer.parseInt(configuration.getProperty("user.fontstyle"));
		int fontColor = Integer.parseInt(configuration.getProperty("user.fontcolor"));
		JFontChooser fc = new JFontChooser(null,fontName,fontSize,fontStyle,fontColor);
		fc.setVisible(true);
		if (fc.getNewFont() != null)
			userFont = fc.getNewFont();
		if (fc.getNewColor() != null)
			userFontColor = fc.getNewColor();
	}

}
