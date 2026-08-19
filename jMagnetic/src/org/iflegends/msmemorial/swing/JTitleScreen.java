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

import java.awt.*;
import javax.swing.*;

import org.iflegends.msmemorial.util.MP3Player;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import com.sixlegs.png.PngImage;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JTitleScreen extends JDialog {
	
    public static final int CENTER_APP = 1;
    public static final int CENTER_SCREEN = 2;

	private Thread player = null;
/**
	 * Constructs and displays a splash screen with the supplied 
	 * image, title and parent. Once the parent is visible, the
	 * splash screen disposes.
	 *
	 * @param owner Parent frame
	 * @param text Splash screen title text
	 * @param image The image to display
	 */
	public JTitleScreen(JFrame owner, String imgfilename, String musfilename,boolean musicon,int waitTime, int centerMode) {

		super(owner);
		this.setModal(true);
		this.setTitle("Click image to start game...");
		PngImage png = new PngImage();
		Image image=null;
		Configuration configuration =  ConfigurationManager.getConfiguration("jmagnetic2");
		double titleScale = Double.parseDouble(configuration.getProperty("titlescale"));
		  // decode a PNG file into a BufferedImage using the read method
		try {
			if (imgfilename.indexOf("wonder")>=0)
				image = png.read(new java.io.File(imgfilename)).getScaledInstance((int)(png.getWidth()*titleScale), (int)(png.getHeight()*titleScale), BufferedImage.SCALE_SMOOTH);
			else
				image = png.read(new java.io.File(imgfilename)).getScaledInstance((int)(png.getWidth()*titleScale), (int)(png.getHeight()*0.5*titleScale), BufferedImage.SCALE_SMOOTH);

		} catch (IOException e1) {
		}
		if (image != null)
		{
		JLabel l = new JLabel(new ImageIcon(image));
		getContentPane().add(l, BorderLayout.CENTER);
        pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
		setLocation(screenSize.width/2 - (labelSize.width / 2),
                screenSize.height/2 - (labelSize.height / 2));
        addMouseListener(new MouseAdapter()
        {
           public void mousePressed(MouseEvent e)
           {
       		if (player != null)
    		{
    			player.stop();
    		}
              setVisible(false);
              dispose();
           }
        });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
        {
           public void run()
           {
              setVisible(false);
              dispose();            
           }
        };
        Runnable waitRunner = new Runnable()
        {
           public void run()
           {
              try
              {
                 Thread.sleep(pause);
                 SwingUtilities.invokeAndWait(closerRunner);
              }
              catch (Exception e)
              {
                 e.printStackTrace();
              }
           }
        };
		if ((musfilename != null) && (musicon) && (new File(musfilename)).exists())
		{
			if (this.player != null)
			{
				player.stop();
			}
			player = (new Thread((new MP3Player(musfilename))));
			player.start();
		}
        setVisible(true);
        if (waitTime > 0)
        {
		   Thread splashThread = new Thread(waitRunner,"SplashThread");
           splashThread.start();
        }
		}
	}
}