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

import org.apache.log4j.Logger;

/**
 * Helper class for emulating a 32 bit register.
 * 
 * @since 1.0.0
 * @author Stefan Meier
 * @version 1.0.0
 */
public class Register32 {
    
  private Logger cat = Logger.getRootLogger();
  public char[]  bytes = new char[4];

  /**
   * Reset the register to 0L
   */
  public void clearReg() {
    cat.debug("Clearing register");  
    for (int i=0;i<4;i++) bytes[i]=0;
  }

  /**
   * Read long from register
   *  
   * @return the long value from the register
  */
  public int rread_l() {
    int lVal = (int)(bytes[0]<<24|bytes[1]<<16|bytes[2]<<8|bytes[3]);  
    cat.debug("Returning long value "+lVal+" from register");  
    return lVal;
  }

  /**
   * Read word from register

   * @param alignment at which offset of the register the read starts
   * @return the word from the register
   */
  public int rread_w( byte alignment) {
    int wVal = (int)(bytes[alignment]<<8 | bytes[alignment+1]);
    cat.debug("Returning word value "+wVal+" from "+alignment);  
    return wVal;
  }

  /**
   * Read byte from register

   * @param alignment at which offset of the register the read starts
   * @return the byte value from the register
   */
  public int rread_b( byte alignment) {
    int bVal = (int)(bytes[alignment]);     
    cat.debug("Returning byte value "+bVal+" from "+alignment);  
    return  bVal;
  }

  /**
   * Write long value to register
   *
   * @param val Long value to be written to the register
   */
  public void rwrite_l( int val) {
    cat.debug("Writing long value "+val+" to register");  
    bytes[3]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val); val>>>=8;
    bytes[2]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val); val>>>=8;
    bytes[1]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val); val>>>=8;
    bytes[0]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val);
  }

  /**
   * Write word value to register starting at given offset
   *
   * @param alignment at which offset of the register the write starts
   * @param val Word to be written to the register
   */
  public void rwrite_w( byte alignment, int val) {
    cat.debug("Writing word value "+val+" with alignment "+alignment+" to register");  
    bytes[alignment+1]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val);
    val>>>=8;                         
    bytes[alignment]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val);
  }

  /**
   * Write byte value to register starting at given offset
   *
   * @param alignment at which offset of the register the write starts
   * @param val Byte value to be written to the register
   */
  public void rwrite_b( byte alignment, int val ) {
    cat.debug("Writing byte value "+val+" with alignment "+alignment+" to register");  
    bytes[alignment]=((byte)val < 0)?(char)((byte)val+256):(char)((byte)val);
  }

}  