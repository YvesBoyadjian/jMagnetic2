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
import java.awt.image.BufferedImage;
import java.io.*;

import org.apache.log4j.Category;

import com.sixlegs.png.PngImage;

public class JImagePanel extends javax.swing.JPanel {
    
  private Category cat = Category.getInstance("ImagePanel");
  private Image image = null;
    
  protected int picWidth = 0;
  protected int picHeight = 0;
  private int gfxMargin = 10;
  protected double scale = 1.0;
    
  /**
   * Creates a new JImagePanel.
   */
  public JImagePanel() {
    super();
  }

  /**
   * Creates a new JImagePanel. Optionally you can provide an filename of 
   * an image.
   *
   * @param fileName Image to be loaded
   */
  public JImagePanel( String fileName ) {
    super();
    cat.debug("Initial image is "+fileName);
    if (!fileName.equals(""))
      loadImage( fileName );
  }

  /**
   * Creates a new JImagePanel. Optionally you can provide an filename of 
   * an image.
   *
   * @param fileName Image to be loaded
   */
  public JImagePanel( Image img ) {
    super();
    this.image = img;
  }

  /**
   * Read the given file using the Jimi package.
   *
   * @param fileName Image to be loaded
   */
  protected Image decode(String fileName) throws IOException
  {
    cat.debug("Reading image file "+fileName);
    BufferedImage newImage = new PngImage().read(new java.io.File(fileName));


    //Image newImage = Jimi.getImage(fileName);
    return (newImage);
  }

  /**
   * Clears the internal image.
   */
  public void clearCache()
  {
    cat.debug("Clearing internal image");
    image = null;
  }	
    
  /**
   * Load a new image from the given file.
   *
   * @param fileName Image to be loaded
   */
  public void loadImage( String fileName )
  {
    Image imgtmp,img2tmp;
    try
    {
      cat.debug("Reading new image from "+fileName);
      imgtmp = decode( fileName );
    }
    catch(Exception e)
    {
      cat.error(e+",trying work around");
      imgtmp = Toolkit.getDefaultToolkit().getImage(fileName);
    }
    cat.debug("Scaling with factor "+scale);
    img2tmp = imgtmp.getScaledInstance( (int)(imgtmp.getWidth(this) * scale),
                                        (int)(imgtmp.getHeight(this)* scale),
                                        Image.SCALE_DEFAULT );  
    imgtmp=null;                                        
    setImage(img2tmp);    
  }
            
  /**
   * Replace/set the current image.
   *
   * @param image Image to be set
   */
  public void setImage(Image image)
  {
    this.image = image;
       
    if(isVisible())
    {
      cat.debug("Repainting after image set");
      repaint();
    }
  }

  /**
   * Customized paint routine of the component.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
	//g.setColor(Color.BLACK);
	 //g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
    if (isVisible())  {
    	 //System.out.println("Width="+this.getWidth()+" , Height="+this.getHeight());
      if (image != null) {
        float widthProp = (float)(this.getWidth()) / 
                          (float)(image.getWidth(this));
        float heightProp = (float)(this.getHeight()-gfxMargin) / 
                           (float)(image.getHeight(this));
        cat.debug("Image width proportion is "+widthProp);
        cat.debug("Image height proportion is "+heightProp);
    	float scaleFac;
        if (widthProp <= heightProp) { 
          scaleFac = widthProp;
        } else {
          scaleFac = heightProp;
        }
        cat.debug("Reference scale factor is "+scaleFac);
        g.drawImage(image, (this.getWidth()-
                     (int)((float)(image.getWidth(this))*scaleFac))/2, gfxMargin/2,
             	     (int)((float)(image.getWidth(this))*scaleFac),
             	     (int)((float)(image.getHeight(this))*scaleFac), this);
      }    	           
    }
  }
    
  /**
   * Returns the width of the current image.
   */
  public int getPicWidth() {
    return picWidth;
  }	

  /**
   * Set the width of the image.
   */
  public void setPicWidth(int newWidth) {
    this.picWidth = newWidth;
  }	

  /**
   * Returns the height of the current image.
   */
  public int getPicHeight() {
    return picHeight;
  }	
    
  /**
   * Sets the height of the image.
   */
  public void setPicHeight(int newHeight) {
    this.picHeight = newHeight;
  }	

  /** 
   * Returns the current value of gfxMargin.
   */
  public int getGfxMargin() {
    return gfxMargin;
  }
    
  /** 
   * Sets a new margin width 
   * @param gfxMargin New value of property gfxMargin.
   */
  public void setGfxMargin(int gfxMargin) {
    this.gfxMargin = gfxMargin;
  }
    
  /**
   * Returns the current scale factor
   */
  public double getScale() {
    return scale;
  }
    
  /**
   * Sets a new scale factor 
   * @param scale New value of property scale.
   */
  public void setScale(double scale) {
    this.scale = scale;
  }
    
  /**
   * Returns the current image.
   */
  public java.awt.Image getImage() {
    return image;
  }
    
}