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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Vector;

import org.apache.log4j.Category;
import org.iflegends.msmemorial.util.BitVector;

/**
 * This is a special {@link org.iflegends.msmemorial.swing.JImagePanel
 * JImagePanel} with support for the graphics format of the Magnetic
 * interpreter, including an option to toggle HAM scaling on/off.
 *
 * @since 1.0.0
 * @author Stefan Meier
 * @version 1.0.0
 */
public class JMagneticImgPanel extends org.iflegends.msmemorial.swing.JImagePanel {

  private int picIndex = -1;
  private boolean HAM = true;
  private double gamma = -1;

  private Category cat = Category.getInstance("JMagneticImgPanel");
  
  private AnimationThread aniThread = null;
  
  private class AnimationThread extends Thread
  {
     private JMagneticSwing ownerInstance;
     private JMagneticImgPanel ownerPanel;
     private boolean contAni = true;
     public AnimationThread(JMagneticImgPanel superPanel, JMagneticSwing magInstance)
     {
        ownerPanel = superPanel;
        ownerInstance = magInstance;
     }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try {
      Vector positions = new Vector();
      Image currImg = createImage(picWidth,picHeight);
      currImg.getGraphics().drawImage(getImage(),0,0,ownerPanel);

      while ((ownerInstance.ms_animate(positions)) && (contAni))
      {
         // Step 0 : Save current canvas
         //Graphics imgGraph = getImage().getGraphics();
         
         Image newImg = createImage(picWidth,picHeight);
         newImg.getGraphics().drawImage(currImg/*getImage()*/,0,0,ownerPanel);
         
         // Step 1 : Draw frames
         int breaktime = (int)((double)100 / (double)positions.size());
         for (int i=0;i < positions.size();i++)
         {
            try
            {
               sleep(breaktime);
                  //Thread.yield();
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         
            JMagneticAniPos ap = (JMagneticAniPos)positions.elementAt(i);
            
            
            if (ap.number >= 0)
            {
               //boolean specMask = false;
               JMagneticAFProps af = ownerInstance.ms_get_anim_frame(ap.number);
               
               int iByteWidth = -1;
            
               // Save source image
               if (af.mask > 0)
               {
                  // calc mask size
                  int iModulo = (int)((double)af.width % (double)16);
                  int iWidth = af.width + ((iModulo > 0) ? (16-iModulo) : 0);
                  iByteWidth = (int)((double)iWidth / (double)8);
                  
                  // Mask size is frame height * width in bits extended to match next 16 bit block
                  // e.g. width 26, height = 18, extended width=32, blocks: 4, mask size: 4*18 =72
                  int maskSize = af.height * iByteWidth;
                  
                  // try to detect a full frame mask
                  //for (int t=0; t < maskSize; t++)
                  //{
                  //   if (ownerInstance.gfx2_buf[af.mask+t] != 0x00)
                  //      specMask = true;
                  //}

                  //System.out.println("Mask index:"+af.mask);
               }
            
               int x,y;
               Image cacheImg;
               cacheImg = createImage( (int)(af.width), (int)(af.height) );
               // calc bounds
               
               String os = System.getProperty("os.name");
               if (os.startsWith("Mac OS"))
               {
                   // newImag so far has a copy of the background image
                   int sx,sy,dx,dy;
                   if (ap.x < 0)
                   {
                	   sx = ap.x;
                	   dx = -ap.x;
                   }
                   else
                   {
                	   sx = 0;
                	   dx = -ap.x;
                   }
                   if (ap.y < 0)
                   {
                	   sy = ap.y;
                	   dy = -ap.y;
                   }
                   else
                   {
                	   sy = 0;
                	   dy = -ap.y;
                   }
                   
                   // Test: Kopie ohne Skalierung
                   
                   
                   newImg.getGraphics().translate(sx, sy);
                   
                   cacheImg.getGraphics().drawImage(newImg,dx,dy,ownerPanel);
               }
               else
               {
                //cacheImg.getGraphics().drawImage(newImg,dx,dy,(int)(af.width),(int)(af.height),
                //           sx, sy,ap.x+(int)(af.width),ap.y+(int)(af.height), ownerPanel);       

                // ORGINAL
                cacheImg.getGraphics().drawImage(newImg,0,0,(int)(af.width),(int)(af.height),
                        ap.x, ap.y,ap.x+(int)(af.width),ap.y+(int)(af.height), ownerPanel);       
               }   
                   
                   Graphics gc = cacheImg.getGraphics();
               
               // DEBUG DRAW MASK
               /*
               if (af.mask > 0)
               {
                  for (y=0;y < af.height;y++)
                  {
                     for (x=0;x < af.width;x++) {
                        int wBlock = (int)((double)x / (double)8);
                        int pOff = (int)((double)x % (double)8);
                        char block = ownerInstance.gfx2_buf[af.mask+(y*iByteWidth)+wBlock];
                        BitVector bitBlock = new BitVector(block);
                        if (!bitBlock.get(7-pOff))
                        {
                           Color c = new Color(0x00,0x00,0x00 );
                           gc.setColor( c );
                           c = null;
                           gc.fillRect((int)(x),(int)(y),(int)1,(int)1);
                        }
                     }
                  }   
               }
               */
               
               // ENDE DRAW MASK
               
               for (y=0;y < af.height;y++)
               {
                  for (x=0;x < af.width;x++) {
                      boolean paintPixel = false;
                      
                      if (af.mask > 0)
                      {
                         int wBlock = (int)((double)x / (double)8);
                         int pOff = (int)((double)x % (double)8);
                         char block = ownerInstance.gfx2_buf[af.mask+(y*iByteWidth)+wBlock];
                         BitVector bitBlock = new BitVector(block);
                         //System.out.print("Line: "+y+",off: "+pOff+",Bitblock: "+bitBlock.toBinaryString());
                         if (block == 0x00)
                         {
                            //System.out.println(" *1*");
                            paintPixel = true;
                         }
                         else
                         {
                            //System.out.println(" *"+bitBlock.get(pOff)+"*");
                            paintPixel = !bitBlock.get(7-pOff);
                         }
                         
                      }
                      else
                      {
                         paintPixel = true;
                      }
                      //paintPixel = false;
                      if (paintPixel)
                      {
                         int red = (int)((ownerInstance.CurrPalette[ownerInstance.gfx_buf[(y*af.width)+x]]&0x0F00)>>>3);
                         int green = (int)((ownerInstance.CurrPalette[ownerInstance.gfx_buf[(y*af.width)+x]]&0x00F0)<<1);
                         int blue = (int)((ownerInstance.CurrPalette[ownerInstance.gfx_buf[(y*af.width)+x]]&0x000F)<<5);
    
                         if (gamma > 0.0)
                         {
                            red = (int)Math.sqrt((double)((double)red*(double)red*gamma)); if (red > 255) red = 255;
                            green = (int)Math.sqrt((double)((double)green*(double)green*gamma)); if (green > 255) green = 255;
                            blue = (int)Math.sqrt((double)((double)blue*(double)blue*gamma)); if (blue > 255) blue = 255;
                         }               
                   
                         Color c = new Color(red,green, blue);
                         gc.setColor( c );
                         c = null;
                         gc.fillRect((int)(x),(int)(y),(int)1,(int)1);
                      }
                   }
                }
                
                newImg.getGraphics().drawImage(cacheImg,ap.x,ap.y,ownerPanel);
                cacheImg = null;
            
            }
            
         }

         setImage(newImg);

         // Step 2 : Remove frames
         
      }
      System.gc();
      contAni = false;
		}
		catch (Exception e)
		{
			// thread most probably died while playing script... ignore
			// and hope the best
		}
	}
   
    public void interruptAni()
    {
       contAni = false;
    }
    
    public boolean is_ani_running()
    {
       return contAni;
    }
       
  }
  /**
   * Creates a new JMagneticImgPanel.
   */
  public JMagneticImgPanel() {
    super();
  }

  /**
   * Creates a new JMagneticPanel. Optionally you can provide an filename of 
   * an image.
   *
   * @param fileName Image to be loaded
   */
  public JMagneticImgPanel(String fileName) {
    super(fileName);
    cat.debug("Initial image is "+fileName);
  }

  /**
   * Creates a new image from the provided Magnetic image data. For in-depth
   * information on the graphics format, please refer to the Magnetic
   * documentation.
   *
   * @param picId Numeric id of the image, prevents redundant image processing
   * @param picData the actual image data
   * @param picPalette a 16 color palette for the image
   */
  public synchronized void setGraphic(int picId, char[] picData, int dataOffset, int[]  picPalette )
  {
    int i,j;
    Graphics gc;
    Image cacheImg = null;

    cat.debug("Picture index is "+picId);
    clearCache(); 	
    cacheImg = createImage( (int)(picWidth), (int)(picHeight) );      	
    gc = cacheImg.getGraphics();
    for (i=0;i < picHeight;i++) {
      for (j=0;j < picWidth;j++) {
        
         int red = (int)((picPalette[picData[dataOffset+(i*picWidth)+j]]&0x0F00)>>>3);
         int green = (int)((picPalette[picData[dataOffset+(i*picWidth)+j]]&0x00F0)<<1);
         int blue = (int)((picPalette[picData[dataOffset+(i*picWidth)+j]]&0x000F)<<5);
    
         if (gamma > 0.0)
         {
            red = (int)Math.sqrt((double)((double)red*(double)red*gamma)); if (red > 255) red = 255;
            green = (int)Math.sqrt((double)((double)green*(double)green*gamma)); if (green > 255) green = 255;
            blue = (int)Math.sqrt((double)((double)blue*(double)blue*gamma)); if (blue > 255) blue = 255;
         }               
                   
         Color c = new Color(red,green, blue);
        /* 
        Color c = new Color(
                (int)((picPalette[picData[(i*picWidth)+j]]&0x0F00)>>>3),
          	(int)((picPalette[picData[(i*picWidth)+j]]&0x00F0)<<1),
       	    	(int)((picPalette[picData[(i*picWidth)+j]]&0x000F)<<5));
        */       
        gc.setColor( c );
        
        c = null;
        gc.fillRect((int)(j),(int)(i),(int)1,(int)1);
      }
    }
    
    setImage(cacheImg);
    picIndex = picId;
  }

  /**
   * Load a new image from the given file and applying scale factor and HAM
   * resizing.
   *
   * @param fileName Image to be loaded
   */
  public synchronized void loadImage( String fileName )
  {
    Image imgtmp,img2tmp;
    double hamScale = 0.5;
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
    if (HAM) hamScale = 0.5;
    else hamScale = 1.0;     
    cat.debug("Scaling with factor "+scale+" and HAM mode set to "+HAM);
    img2tmp = imgtmp.getScaledInstance(
                (int)(imgtmp.getWidth(this) * scale),
                (int)(imgtmp.getHeight(this)*hamScale * scale),
                Image.SCALE_DEFAULT );  
    imgtmp=null;                                        
    setImage(img2tmp);    
  }
            
  /**
   * returns the current picture index.
   */
  public int getPicIndex() {
    return picIndex;
  }
    
  /**
   * returns true if HAM mode is enabled, otherwise false
   */
  public boolean isHAM() {
    return HAM;
  }
    
  /** Enable or disable HAM mode
   * @param HAM New value of property HAM.
   */
  public void setHAM(boolean HAM) {
    this.HAM = HAM;
  }

  /**
   * @param main_pic
   */
  public void playAnimation(JMagneticSwing magInstance)
  {
     if (aniThread != null)
        aniThread.interruptAni();
     aniThread = null;   
     aniThread = new AnimationThread(this, magInstance);
     new Thread(aniThread).start();
  }

  public void stopAnimation()
  {
     if (aniThread != null)
        aniThread.interruptAni();
  }
  
  public boolean is_ani_running()
  {
     if (aniThread != null)
        return aniThread.is_ani_running();
     else
        return false;   
  }
/**
 * @return
 */
public double getGamma()
{
	return gamma;
}

/**
 * @param d
 */
public void setGamma(double d)
{
	gamma = d;
}

}