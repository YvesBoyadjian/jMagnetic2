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

import java.util.Vector;

public class JMagneticPicDescriptor {
  JMagneticFrameDescriptor mainFrame;
  boolean isAnim;
  int pos_table_size;
  Vector animFrames;
  Vector pos_table_count;
  Vector pos_table; //Vector containing Vectors of AniPos
  int command_count;
  int command_table;
  Vector anim_table; // of type Lookup
  int command_index;
  boolean anim_repeat;
  int pos_table_index;
  int pos_table_max;
  
  public JMagneticPicDescriptor()
  {
     this.mainFrame = new JMagneticFrameDescriptor();
     this.isAnim = false;
  }
  
  public void InitAnimContainer()
  {
     this.animFrames = new Vector();
     this.pos_table_count = new Vector();
     this.pos_table = new Vector();
     this.anim_table = new Vector();
  }
}  