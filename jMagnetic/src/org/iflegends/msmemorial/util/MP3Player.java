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

package org.iflegends.msmemorial.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

public class MP3Player implements Runnable
{
   private String fileName;
   private Player player = null;
   /**
    * Starts this applet. An input stream and audio device 
    * are created and passed to the play() method.
    */
   public MP3Player (final String mp3File)
   {
      this.fileName = mp3File;
   }  
   
     /**
       * Retrieves the <code>AudioDevice</code> instance that will
       * be used to sound the audio data. 
       * 
       * @return  an audio device instance that will be used to 
       *       sound the audio stream.
       */
      protected AudioDevice getAudioDevice() throws JavaLayerException
      {
         return FactoryRegistry.systemRegistry().createAudioDevice();
      }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
      InputStream in;
      try
      {
         in = new FileInputStream(fileName);
                 AudioDevice dev = getAudioDevice();
                 player = new Player(in, dev);
                 player.play();
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      } catch (JavaLayerException e)
      {
         e.printStackTrace();
      }
	}
	
	public void stop()
	{
		if (player != null)
		{
			player.close();
			player = null;
		}
	}
	
}
