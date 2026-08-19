package org.iflegends.msmemorial.util;
/*
 * Copyright (C) 2000-2001 Technical University of Ilmenau, Germany
 *                         Department of Microelectronic Circuits and Systems
 *
 * You may only use this file according to the terms of the license agreement included
 * within the file LICENSE which is distributed together with the software package.
 *
 */

import java.util.Random;

/** Replacement for Sun's unflexible BitSet.
 * This class provides an internal boundchecking. If an invalid index is given the appropriate method does
 * simply nothing. For convinience reasons no exception is thrown in such a case.
 * 
 * @since 0.1
 * @author Andreas Rummler
 * @version 0.1 (11-30-2000)
 */
public class BitVector implements Cloneable {

	/** All bits are kept in this byte array.
	*/
	protected byte[] set;

	/** Size of byte array.
	* This value must not be dividable by 8.
	*/
	protected int setSize;

	/** Byte array, for masking single bits with zero.
	* This array has the size of 8 and contains the masks for bits 0, 1 etc.
	*/
	protected final static byte[] zeroMask = new byte[] { 1, 2, 4, 8, 16, 32, 64, -128 };

	/** Byte array, for masking single bits with one.
	* This array has the size of 8 and contains the masks for bits 0, 1 etc.
	*/
	protected final static byte[] oneMask = new byte[] { -2, -3, -5, -9, -17, -33, -65, 127 };


	/** Constructor with initial size of 8 bits.
	*/
	public BitVector() {
		this( 8 );
	}

	/** Constructor with size of BitVector.
	* 
	* @param size size of bit vector
	*/
	public BitVector( int size ) {
		setSize = size;
		if ( setSize % 8 == 0 ) {
			set = new byte[setSize / 8];
		} else {
			set = new byte[setSize / 8 + 1];
		}
		for ( int i = 0; i < set.length; i++ ) {
			set[i] = (byte) 0;
		}
	}

	/** Constructs a new bitvector from a byte.
	* 
	* @param ini byte to construct bitvector from
	*/
	public BitVector( byte ini ) {
		setSize = 8;
		set = new byte[1];
		set[0] = ini;
	}

	/** Constructs a new bitvector from a short number.
	* 
	* @param ini short number to construct bitvector from
	*/
	public BitVector( short ini ) {
		setSize = 16;
		set = new byte[2];
		for ( int i = 0; i < 2; i++ ) {
			set[i] = (byte) ini;
			ini = (short) (ini >> 8);
		}
	}

   /** Constructs a new bitvector from a char.
   * 
   * @param ini char to construct bitvector from
   */
   public BitVector( char ini ) {
      setSize = 16;
      set = new byte[2];
      for ( int i = 0; i < 2; i++ ) {
         set[i] = (byte) ini;
         ini = (char) (ini >> 8);
      }
   }

	/** Constructs a new bitvector from an int number.
	* 
	* @param ini int number to construct bitvector from
	* @param intFlag this is used to differentiate this constructor from 
	*      {@link #BitVector(int), BitVector(int)}
	*/
	public BitVector( int ini, boolean intFlag ) {
		setSize = 32;
		set = new byte[4];
		for ( int i = 0; i < 4; i++ ) {
			set[i] = (byte) ini;
			ini = ini >> 8;
		}
	}

	/** Constructs a new bitvector from a long number.
	* 
	* @param ini long number to construct bitvector from
	*/
	public BitVector( long ini ) {
		setSize = 64;
		set = new byte[8];
		for ( int i = 0; i < 8; i++ ) {
			set[i] = (byte) ini;
			ini = ini >> 8;
		}
	}

	/** Constructor with byte array.
	* This creates a new bitvector from the given byte array.
	* 
	* @param ba initial byte array
	*/
	public BitVector( byte[] ba ) {
		this( ba, false );
	}

	/** Constructor with byte array.
	* This creates a new bitvector from the given byte array.
	* The byte vector can be optionally reversed
	* 
	* @param ba initial byte array
	* @param reverse if true, reverses the byte vector
	*/
	public BitVector( byte[] ba, boolean reverse ) {
		if ( reverse == false ) {
			set = (byte[]) ba.clone();
			setSize = 8 * set.length;
		} else {
			set = new byte[ba.length];
			setSize = 8 * set.length;
			for ( int i = 0, j = ba.length - 1; i < ba.length; i++, j-- ) {
				set[i] = ba[j];
			}
		}
	}
	
	/** Constructor with string.
	* This creates a new bitvector from the given string. The string is interpreted in that way that the MSB is the leftmost
	* character. Any characters that are not "0" or "1" are treated as zeros.
	* 
	* @param bitstring string that this bit vector is constructed from
	*/
	public BitVector( String bitstring ) {
		this( bitstring.length() );
		for ( int i = bitstring.length() - 1, j = 0; i >= 0; i--, j++ ) {
			if ( bitstring.charAt( i ) == '1' ) {
				internalSet( j );
			} else {
				internalUnset( j );
			}
		}
	}
	
	/** Gets a single bit.
	* 
	* @param index position of bit in bit vector
	* @return value of the bit
	*/
	public boolean get( int index ) {
		if ( index < setSize && index >= 0  ) {
			int bytePos = index / 8;
			int bitPos = index % 8;
			byte bitMask = zeroMask[bitPos];
			byte r = (byte) ( set[bytePos] & bitMask );
			if ( r == 0 ) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/** Gets a single bit and returns the value as an integer.
	* 
	* @param index position of the bit in bit vector
	* @return value of the bit
	*/
	public int getInteger( int index ) {
		if ( index < setSize && index >= 0  ) {
			int bytePos = index / 8;
			int bitPos = index % 8;
			byte bitMask = zeroMask[bitPos];
			byte r = (byte) ( set[bytePos] & bitMask );
			if ( r == 0 ) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	/** Gets a byte from the bitvector.
	* 
	* @param byteIndex position of byte in byte array
	* @return byte from byte array
	*/
	public byte getByte( int byteIndex ) {
		if ( byteIndex < set.length && byteIndex >= 0 ) {
			return set[byteIndex];
		} else {
			return 0;
		}
	}

	/** Gets a byte from the bitvector starting at a particular position.
	* 
	* @param bitIndex position of the starting bit in the bitvector
	* @param length number of bits to get (values > 8 will be ignored)
	* @return new byte created from selected bits
	*/
	public byte getByte( int bitIndex, int length ) {
		if ( length < 1 ) {
			return (byte) 0;
		}
		if ( length > 8 ) {
			length = 8;
		}
		byte b = 0;
		byte seed = 1;
		for ( int i = bitIndex; i < bitIndex + length && i < setSize; i++ ) {
			if ( internalGet( i ) ) {
				b += seed;
			}
			seed *= 2;
		}
		return b;
	}

	/** Gets a new bitvector with bits starting at a particular position.
	* 
	* @param index position of the starting bit in the current bitvector
	* @param length number of bits to get
	* @return new bitvector created from selected bits
	*/
	public BitVector getBitVector( int index, int length ) {
		BitVector nb;
		if ( length < 1 ) {
			return new BitVector( 1 );
		} else {
			nb = new BitVector( length );
		}
		for ( int i = index, j = 0; i < index + length && i < setSize; i++, j++ ) {
			if ( internalGet( i ) ) {
				nb.internalSet( j );
			}
		}
		return nb;
	}

	/** Gets a single bit.
	* This method is for internal use, it provides the same functionality as
	* {@link #get(int) get(int)}, but works without boundchecking.
	* 
	* @param index position of the bit in bit vector
	* @return value of the bit
	*/
	protected boolean internalGet( int index ) {
		int bytePos = index / 8;
		int bitPos = index % 8;
		byte bitMask = zeroMask[bitPos];
		byte r = (byte) ( set[bytePos] & bitMask );
		if ( r == 0 ) {
			return false;
		} else {
			return true;
		}
	}

	/** Sets a single bit.
	* This method is for internal use, it provides the same functionality as
	* {@link #set(int) set(int)}, but works without boundchecking.
	* 
	* @param index position of the bit in bit vector
	*/
	protected void internalSet( int index ) {
		int bytePos = index / 8;
		int bitPos = index % 8;
		byte bitMask = zeroMask[bitPos];
		set[bytePos] = (byte) ( set[bytePos] | bitMask );
	}

	/** Sets a single bit.
	* This method is for internal use, it provides the same functionality as
	* {@link #set(int, boolean) set(int, boolean)}, but works without boundchecking.
	* 
	* @param index position of the bit in bit vector
	* @param value value of the bit
	*/
	protected void internalSet( int index, boolean value ) {
		if ( value == true ) {
			internalSet( index );
		} else {
			internalUnset( index );
		}
	}

	/** Clears a single bit.
	* This method is for internal use, it provides the same functionality as
	* {@link #unset(int) unset(int)}, but works without boundchecking.
	* 
	* @param index position of the bit in bit vector
	*/
	protected void internalUnset( int index ) {
		int bytePos = index / 8;
		int bitPos = index % 8;
		byte bitMask = oneMask[bitPos];
		set[bytePos] = (byte) ( set[bytePos] & bitMask );
	}

	/** Sets a single bit.
	* 
	* @param index position of the bit in bit vector
	*/
	public void set( int index ) {
		if ( index < setSize && index >= 0  ) {
			int bytePos = index / 8;
			int bitPos = index % 8;
			byte bitMask = zeroMask[bitPos];
			set[bytePos] = (byte) ( set[bytePos] | bitMask );
		}
	}

	/** Sets a single bit.
	* 
	* @param index position of the bit in bit vector
	* @param value value of the bit
	*/
	public void set( int index, boolean value ) {
		if ( index < setSize && index >= 0 ) {
			if ( value == true ) {
				set( index );
			} else {
				unset( index );
			}
		}
	}

	/** Sets a single bit.
	* 
	* @param index position of the bit in bit vector
	* @param value value of the bit
	*/
	public void set( int index, int value ) {
		if ( index < setSize && index >= 0 ) {
			if ( value == 0 ) {
				unset( index );
			} else {
				set( index );
			}
		}
	}

	/** Sets a single bit and resizes the bit vector, if necessary.
	* Warning: Resizing is done in 8 bit steps. This may be changed in the future.
	* 
	* @param index position of the bit in bit vector
	*/
	public void rset( int index ) {
		if ( index >= 0 ) {
			if ( index >= setSize ) { // resize the internal byte array
				if ( index < set.length * 8 ) {
					setSize = set.length * 8;
				} else {
					int newSize = index / 8 + 1;
					byte[] newSet = new byte[newSize];
					System.arraycopy( set, 0, newSet, 0, set.length );
					set = newSet;
					setSize = newSize * 8;
				}
			}
			internalSet( index );
		}
	}

	/** Clears a single bits.
	* 
	* @param index position of the bit in bit vector
	*/
	public void unset( int index ) {
		if ( index < setSize && index >= 0 ) {
			int bytePos = index / 8;
			int bitPos = index % 8;
			byte bitMask = oneMask[bitPos];
			set[bytePos] = (byte) ( set[bytePos] & bitMask );
		}
	}

	/** Clears a single bits.
	* 
	* @param index position of the bit in bit vector
	*/
	public void clear( int index ) {
		unset( index );
	}

	/** Clears a single bit and resizes the bit vector, if necessary.
	* Warning: resizing is done in 8 bit steps. This may be changed in the future.
	* 
	* @param index position of the bit in bit vector
	*/
	public void runset( int index ) {
		if ( index >= 0 ) {
			if ( index >= setSize ) { // resize the internal byte array
				if ( index < set.length * 8 ) {
					setSize = set.length * 8;
				} else {
					int newSize = index / 8 + 1;
					byte[] newSet = new byte[newSize];
					System.arraycopy( set, 0, newSet, 0, set.length );
					set = newSet;
					setSize = newSize * 8;
				}
			}
			internalUnset( index );
		}
	}

	/** Clears a single bit and resizes the bit vector, if necessary.
	* Warning : resizing is done in 8 bit steps. This may be changed in the future.
	* 
	* @param index index of the bit to be cleared
	*/
	public void rclear( int index ) {
		runset( index );
	}

	// section for bit operations

	/** Performs an AND operation.
	* 
	* @param bv second bitvector to use for for AND operation
	* @return new bitvector containing result
	*/
	public BitVector and( BitVector bv ) {
		byte[] result;
		int length;
		if ( set.length >= bv.byteSize() ) {
			length = set.length;
		} else {
			length = bv.byteSize();
		}
		result = new byte[length];
		for ( int i = 0; i < length; i++ ) {
			result[i] = (byte) ( getByte( i ) & bv.getByte( i ) );
		}
		return new BitVector( result );
	}

	/** Inverts the bitvector.
	*/
	public void not() {
		for ( int i = 0; i < set.length; i++ ) {
			set[i] = (byte) ~set[i];
		}
		for ( int i = setSize; i < set.length * 8; i++ ) {
			internalUnset( i );
		}
	}

	/** Performs an OR operation.
	* 
	* @param bv second bitvector to use for for OR operation
	* @return new bitvector containing result
	*/
	public BitVector or( BitVector bv ) {
		byte[] result;
		int length;
		if ( set.length >= bv.byteSize() ) {
			length = set.length;
		} else {
			length = bv.byteSize();
		}
		result = new byte[length];
		for ( int i = 0; i < length; i++ ) {
			result[i] = (byte) ( getByte( i ) | bv.getByte( i ) );
		}
		return new BitVector( result );
	}

	/** Performs an XOR operation.
	* 
	* @param bv second bitvector to use for for XOR operation
	* @return new bitvector containing result
	*/
	public BitVector xor( BitVector bv ) {
		byte[] result;
		int length;
		if ( set.length >= bv.byteSize() ) {
			length = set.length;
		} else {
			length = bv.byteSize();
		}
		result = new byte[length];
		for ( int i = 0; i < length; i++ ) {
			result[i] = (byte) ( getByte( i ) ^ bv.getByte( i ) );
		}
		return new BitVector( result );
	}

	/** Shifts the bitvector to the left.
	*/
	public void shiftLeft() {
		boolean lowerCarry = false, upperCarry = false;
		for ( int i = 0; i <= set.length - 1; i++ ) {
			upperCarry = internalGet( i * 8 + 7 );
			set[i] = (byte) ( set[i] << 1 );
			internalSet( i * 8, lowerCarry );
			lowerCarry = upperCarry;
		}
	}

	/** Shifts the bitvector n times to the left.
	* 
	* @param times number of shift operations
	*/
	public void shiftLeft( int times ) {
		for( int i = 0; i < times; i++ ) {
			shiftLeft();
		}
	}

	/** Shifts the bitvector to the right.
	*/
	public void shiftRight() {
		boolean lowerCarry = false, upperCarry = false;
		for ( int i = set.length - 1; i >= 0; i-- ) {
			lowerCarry = internalGet( i*8 );
			set[i] = (byte) ( set[i] >> 1 );
			internalSet( i*8+7, upperCarry );
			upperCarry = lowerCarry;
		}
	}

	/** Shifts the bitvector n times to the right.
	* 
	* @param times number of shift operations
	*/
	public void shiftRight( int times ) {
		for( int i = 0; i < times; i++ ) {
			shiftRight();
		}
	}

	/** Gets the size of the bitvector.
	* 
	* @return size of the bitvector
	*/
	public int size() {
		return setSize;
	}

	/** This returns the number of the the bytes used for holding the bit vector.
	* 
	* @return number of bytes
	*/
	public int byteSize() {
		return set.length;
	}

	/** Returns a string representation of the bitvector.
	* The output is done in 8 bit blocks. After each block a whitespace is inserted.
	* The most significant block is printed first. In each block the bits are printed from MSB .. LSB.
	* 
	* @return string representation
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for ( int i = setSize - 1; i >= 0; i-- ) {
			sb.append( getInteger( i ) );
			if ( ( i % 8 == 0 ) && ( i != 0 ) ) {
				sb.append( " " );
			}
		}
		return sb.toString();
	}

	/** Returns a string representation of the bitvector.
	* This method works like {@link #toString() toString()}, but whitespaces ar missing.
	* 
	* @return string representation
	*/
	public String toBinaryString() {
		StringBuffer sb = new StringBuffer();
		for ( int i = setSize - 1; i >= 0; i-- ) {
			sb.append( getInteger( i ) );
		}
		return sb.toString();
	}

	/** Returns a hexadecimal string representation of the bitvector.
	* 
	* @return hexadecimal string representation
	*/
	public String toHexString() {
		return toHexString( true );
	}

	/** Returns a hexadecimal string representation of the bitvector.
	* 
	* @param withPrefix if true, the prefix "0x" is used
	* @return hexadecimal string representation
	*/
	public String toHexString( boolean withPrefix ) {
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < setSize; i += 4 ) {
			sb.insert( 0, Integer.toHexString( getByte( i, 4 ) ) );
		}
		if ( withPrefix == true ) {
			sb.insert( 0, "0x" );
		}
		return sb.toString();
	}

	/** Returns a string representation, which equals the one Sun's BitSet returns.
	* The method is done for convinience reasons.
	* 
	* @return bit set string representation
	*/
	public String toBitSetString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "{" );
		for ( int i = 0; i < setSize; i++ ) {
			if ( internalGet( i ) ) {
				sb.append( i + ", " );
			}
		}
		if ( sb.length() > 3 ) {
			sb.delete( sb.length() - 2, sb.length() );
		}
		sb.append( "}" );
		return sb.toString();
	}

	/** Returns the lowest 4 bytes from the bit vector as an integer number.
	* 
	* @return integer number
	*/
	public int intValue() {
		int r = 0;
		if ( set.length > 4 ) {
			for ( int i = 3; i >= 0; i-- ) {
				r = r << 8;
				r = r | (set[i] & 255);
			}
		} else {
			for ( int i = (set.length - 1); i >= 0; i-- ) {
				r = r << 8;
				r = r | (set[i] & 255);
			}
		}
		return r;
	}

	/** Returns the lowest 8 bytes of the bit vector as a long number.
	* 
	* @return long number
	*/
	public long longValue() {
		long r = 0;
		if ( set.length > 8 ) {
			for ( int i = 7; i >= 0; i-- ) {
				r = r << 8;
				r = r | (long) (set[i] & 255);
			}
		} else {
			for ( int i = (set.length - 1); i >= 0; i-- ) {
				r = r << 8;
				r = r | (long) (set[i] & 255);
			}
		}
		return r;
	}

	/** Inverts a bit.
	* 
	* @param index position of the bit in bit vector
	*/
	public void toggle( int index ) {
		if ( index < setSize ) {
			if ( get( index ) == true ) {
				unset( index );
			} else {
				set( index );
			}
		}
	}

	/** Inverts a boolean value.
	* This is a static helper method.
	* 
	* @param bit value to be inverted
	* @return inverted value
	*/
	public static boolean toggleBit( boolean bit ) {
		if ( bit == true ) {
			return false;
		} else {
			return true;
		}
	}

	/** Fills the bit vector with random bits.
	*/
	public void randomize() {
		Random r = new Random();
		for ( int i = 0; i < setSize; i++ ) {
			set( i, r.nextInt( 2 ) );
		}
	}

	/** Clones the bit vector.
	* 
	* @return copy of the bit vector
	*/
	public Object clone() {
		BitVector clone = new BitVector( setSize );
		for( int i = 0; i < set.length; i++ ) {
			clone.set[i] = set[i];
		}
		return clone;
	}

}
