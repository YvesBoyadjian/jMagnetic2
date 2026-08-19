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
package org.iflegends.msmemorial.swing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;


public class JFontChooser extends JDialog implements ActionListener, ChangeListener {

  JColorChooser colorChooser;
  JComboBox fontName;
  JCheckBox fontBold, fontItalic;
  private JSpinner fontSize;
  JLabel previewLabel;
  SimpleAttributeSet attributes;
  Font newFont;
  Color newColor;

  public JFontChooser(JFrame parent, String pfontName, int pfontSize, int pfontStyle, int prgb) {
    super(parent, "Font Chooser", true);
	Toolkit toolkit = Toolkit.getDefaultToolkit();

	// Get the current screen size
	Dimension scrnsize = toolkit.getScreenSize();
	int locX = (((int)scrnsize.getWidth())-500)/2;
	int locY = (((int)scrnsize.getHeight())-450)/2;
	this.setLocation(locX,locY);
    setSize(500, 450);
    attributes = new SimpleAttributeSet();
	StyleConstants.setFontFamily(attributes, pfontName);
    StyleConstants.setFontSize(attributes, pfontSize);
    StyleConstants.setForeground(attributes, new Color(prgb));
    if (pfontStyle == Font.BOLD || pfontStyle == (Font.ITALIC+Font.BOLD))
       StyleConstants.setBold(attributes, true);
    else
       StyleConstants.setBold(attributes, false);
    if (pfontStyle == Font.ITALIC || pfontStyle == (Font.ITALIC+Font.BOLD))
       StyleConstants.setItalic(attributes, true);
    else
       StyleConstants.setItalic(attributes, false);

    // Make sure that any way the user cancels the window does the right thing
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        closeAndCancel();
      }
    });

    // Start the long process of setting up our interface
    Container c = getContentPane();
    
    JPanel fontPanel = new JPanel();
    String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment()
    .getAvailableFontFamilyNames();
     Arrays.sort(fontFamilies);
    fontName = new JComboBox(fontFamilies);
    fontName.setSelectedItem(pfontName);
    fontName.addActionListener(this);
	SpinnerNumberModel jSpinner1Model = new SpinnerNumberModel(pfontSize,5,30,1);
		fontSize = new JSpinner();
		fontSize.setModel(jSpinner1Model);
		fontSize.addChangeListener(this);
    fontBold = new JCheckBox("Bold");
	if (pfontStyle == Font.BOLD || pfontStyle == (Font.BOLD+Font.ITALIC))
	{
       fontBold.setSelected(true);
	}
	else
	{
	       fontBold.setSelected(false);		
	}
    fontBold.addActionListener(this);
    fontItalic = new JCheckBox("Italic");
	if (pfontStyle == Font.ITALIC || pfontStyle == (Font.BOLD+Font.ITALIC))
	{
		fontItalic.setSelected(true);
	}
	else
	{
		fontItalic.setSelected(false);		
	}
    fontItalic.addActionListener(this);

    fontPanel.add(fontName);
    fontPanel.add(new JLabel(" Size: "));
    fontPanel.add(fontSize);
    fontPanel.add(fontBold);
    fontPanel.add(fontItalic);

    c.add(fontPanel, BorderLayout.NORTH);
    
    // Set up the color chooser panel and attach a change listener so that color
    // updates get reflected in our preview label.
    colorChooser = new JColorChooser(new Color(prgb));
    colorChooser.getSelectionModel()
                .addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        updatePreviewColor();
      }
    });
    c.add(colorChooser, BorderLayout.CENTER);

    JPanel previewPanel = new JPanel(new BorderLayout());
    previewLabel = new JLabel("Here's a sample of this font.");
    previewLabel.setForeground(colorChooser.getColor());
    previewPanel.add(previewLabel, BorderLayout.CENTER);

    // Add in the Ok and Cancel buttons for our dialog box
    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndSave();
      }
    });
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        closeAndCancel();
      }
    });

    JPanel controlPanel = new JPanel();
    controlPanel.add(okButton);
    controlPanel.add(cancelButton);
    previewPanel.add(controlPanel, BorderLayout.SOUTH);

    // Give the preview label room to grow.
    previewPanel.setMinimumSize(new Dimension(100, 100));
    previewPanel.setPreferredSize(new Dimension(100, 100));

    c.add(previewPanel, BorderLayout.SOUTH);
    updatePreviewFont();
  }
  // Ok, something in the font changed, so figure that out and make a
  // new font for the preview label
  public void actionPerformed(ActionEvent ae) {
    // Check the name of the font
    if (!StyleConstants.getFontFamily(attributes)
                       .equals(fontName.getSelectedItem())) {
      StyleConstants.setFontFamily(attributes, 
                                   (String)fontName.getSelectedItem());
    }
    // Check the font size (no error checking yet)
    if (StyleConstants.getFontSize(attributes) != 
                                   Integer.parseInt((fontSize.getValue()).toString())) {
      StyleConstants.setFontSize(attributes, Integer.parseInt((fontSize.getValue()).toString()));
    }
    // Check to see if the font should be bold
    if (StyleConstants.isBold(attributes) != fontBold.isSelected()) {
      StyleConstants.setBold(attributes, fontBold.isSelected());
    }
    // Check to see if the font should be italic
    if (StyleConstants.isItalic(attributes) != fontItalic.isSelected()) {
      StyleConstants.setItalic(attributes, fontItalic.isSelected());
    }
    // and update our preview label
    updatePreviewFont();
  }

  // Get the appropriate font from our attributes object and update
  // the preview label
  protected void updatePreviewFont() {
    String name = StyleConstants.getFontFamily(attributes);
    boolean bold = StyleConstants.isBold(attributes);
    boolean ital = StyleConstants.isItalic(attributes);
    int size = StyleConstants.getFontSize(attributes);

    //Bold and italic don’t work properly in beta 4.
    Font f = new Font(name, (bold ? Font.BOLD : 0) +
                            (ital ? Font.ITALIC : 0), size);
    previewLabel.setFont(f);
  }

  // Get the appropriate color from our chooser and update previewLabel
  protected void updatePreviewColor() {
    previewLabel.setForeground(colorChooser.getColor());
    // Manually force the label to repaint
    previewLabel.repaint();
  }
  public Font getNewFont() { return newFont; }
  public Color getNewColor() { return newColor; }
  public AttributeSet getAttributes() { return attributes; }

  public void closeAndSave() {
    // Save font & color information
    newFont = previewLabel.getFont();
    newColor = previewLabel.getForeground();

    // Close the window
    setVisible(false);
  }

  public void closeAndCancel() {
    // Erase any font information and then close the window
    newFont = null;
    newColor = null;
    setVisible(false);
  }
public void stateChanged(ChangeEvent arg0) {
	actionPerformed(null);
	
}
}