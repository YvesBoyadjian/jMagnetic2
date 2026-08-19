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

import java.io.IOException;
import java.util.Vector;

public interface JMagneticUI
{
	/****************************************************************************\
	* Function: ms_load_file
	*
	* Purpose: Load a save game file and restore game
	*
	* Parameters:   char	name            string of filename typed by player
	*               int		ptr             offset in game memory where data start
	*               int		size            number of bytes to load
	*
	* Result: 0 is successful
	*
	* Note: You probably want to put in a file requester!
	\****************************************************************************/
   public byte ms_load_file( char[] name, int offset, int size) throws IOException;

   /****************************************************************************\
   * Function: ms_save_file
   *
   * Purpose: Save the current game to file
   *
   * Parameters:   char	name            string of filename typed by player
   *               int		ptr             offset in game memory where data start
   *               int		size            number of bytes to save
   *
   * Result: 0 is successful
   *
   * Note: You probably want to put in a file requester!
   \****************************************************************************/
   public byte ms_save_file( char[] name, int offset, int size) throws IOException;
   
   public void ms_statuschar(char c);
   public void ms_putchar(char c);
   public void ms_flush();
   public char ms_getchar(int trans) throws IOException;
   public void ms_showpic(int c,byte mode);

   /* NEW */
   public void ms_fatal(String txt);

   public int  ms_extract(int pic);
   public void ms_waitForKey();

   /* NEW */
   public boolean ms_animate(Vector positions);
   public JMagneticAFProps ms_get_anim_frame(int number);
   public boolean ms_anim_is_repeating();
   public int ms_showhints(Vector hints);
   public char ms_is_running();
   public boolean ms_is_magwin();
   public void ms_stop();
   public void ms_status();
   public long ms_count();
   
   /* Really new */
   public void ms_playmusic(String name);
}
