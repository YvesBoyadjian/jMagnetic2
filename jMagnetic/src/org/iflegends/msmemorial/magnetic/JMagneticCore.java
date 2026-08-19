/*-----------------------------------------------------------------------------
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

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.iflegends.msmemorial.util.*;

/**
 * The JMagneticCore is the heart of the JMagnetic interpreter. It does the
 * actual interpreting of the the Magnetic Scrolls game data.
 * 
 * JMagneticCore is an abstract class, which does not provide I/O, so to get a
 * working JMagnetic you must derive from this class and implement the abstract
 * methods noted below (starting with ms_).
 * 
 * @since 1.0.0
 * @author Stefan Meier
 * @version 1.0.0
 */
abstract class JMagneticCore extends Thread implements JMagneticUI {

	private boolean LOGEMU = false;
	private boolean LOGGFX = false;
	private boolean LOGGFX_EXT = false;
	private boolean LOGHNT = false;
	private boolean LOGSND = false;

	private Logger emuLogger;
	private Logger gfxLogger;
	private Logger hntLogger;
	private Logger sndLogger;

	private Register32[] dreg = new Register32[8]; /* 8 32bit Register */
	private Register32[] areg = new Register32[8];

	int i_count, mov_count = 0;
	int pc, arg1i;
	int rseed = 0;
	int mem_size, string_size;
	int properties, fl_sub, fl_tab, fl_size, fp_tab, fp_size;
	char zflag, nflag, cflag, vflag, byte1, byte2, regnr, admode, opsize;
	int arg1, arg2;
	char[] acc1;
	char[] acc2;
	char is_reversible, running = 0, lastchar = 0, version = 0, sdd = 0;
	char tmparg[] = new char[4];
	char[] restart = null, code = null, tstring = null, tstring2 = null,
			dict = null;
	int decode_offset = 0;
	char[] decode_src;
	char quick_flag = 0;
	char[] gfx_buf = null;
	char[] gfx2_buf = null;
	char[] gfx_data = null;
	char[] gfx2_hdr = null;
	String gfx2_name;
	RandomAccessFile gfx_fp = null, log = null, log2 = null;
	int log_on = 0;
	int dlimit, slimit;
	boolean stopped = false;
	int pic_ind = 0;
	char gfx_ver = 0;
	int gfx2_hsize = 0;

	int CurrPHeight;
	int CurrPWidth;
	int[] CurrPalette;
	JMagneticPicDescriptor main_pic;
	protected boolean waitForAnims = false;

	static char[] undo_ok = "\n[Previous turn undone.]".toCharArray();
	static char[] undo_fail = "\n[You can't \"undo\" what hasn't been done!]"
			.toCharArray();
	static char big = 0, period = 0, pipe = 0;
	static int offset_bak;
	static byte mask_bak;
	int[][] undo_regs = new int[2][18];
	int undo_pc, undo_size;
	char[][] undo = new char[2][];
	char[] undo_stat = new char[2];
	StringBuffer logstr = new StringBuffer();

	int v4_id = 0, next_table = 1;
	boolean sndenabled = true;

	/*
	 * Abstract methods - push to interface abstract byte ms_load_file( char[]
	 * name, int offset, int size) throws IOException; abstract byte
	 * ms_save_file( char[] name, int offset, int size) throws IOException;
	 * abstract void ms_statuschar(char c); abstract void ms_putchar(char c);
	 * abstract void ms_flush(); abstract char ms_getchar(int trans) throws
	 * IOException; abstract void ms_showpic(int c,byte mode); abstract void
	 * ms_waitForKey(); abstract void ms_fatal(String txt); abstract int
	 * ms_showhints(Vector hints);
	 */
	class Lookup {
		int flag;
		int count;

		public Lookup() {
			flag = -1;
			count = -1;
		}
	}

	/*
	 * Vector anim_frame_table = new Vector(); // Type Picture int
	 * pos_table_size = 0; Vector pos_table_count = new Vector(); Vector
	 * pos_table = new Vector(); // Type Vector of AniPos int command_table = 0;
	 * int command_index = -1; Vector anim_table = new Vector(); //Type Lookup
	 * int pos_table_index = -1; int pos_table_max = -1; Vector pos_array = new
	 * Vector(); // Type AniPos byte anim_repeat = 0;
	 */

	/* Hint support */
	Vector hints = new Vector(); // Type Hint
	String hint_contents;
	static char[] no_hints = "[Hints are not available.]\n".toCharArray();
	static char[] not_supported = "[This function is not supported.]\n"
			.toCharArray();

	/* Sound support */
	HashMap sounds = new HashMap();

	public JMagneticCore(String string) {
		super(string);
	}

	// LOGGFX LOGHNT
	void log(String cat, String s) {
		if (cat.equals("logemu")) {
			if (emuLogger == null)
				emuLogger = Logger.getLogger("logemu");
			emuLogger.info(s);
		} else if (cat.equals("loghnt")) {
			if (hntLogger == null)
				hntLogger = Logger.getLogger("loghnt");
			hntLogger.info(s);
		} else if (cat.equals("logsnd")) {
			if (sndLogger == null)
				sndLogger = Logger.getLogger("logsnd");
			sndLogger.info(s);
		} else if (cat.startsWith("loggfx")) {
			if (gfxLogger == null)
				gfxLogger = Logger.getLogger("loggfx");
			gfxLogger.info(s);
		} else {
			Logger.getRootLogger().warn(s);
		}
		// System.out.println(s);
	}

	public void enableEmuLog(boolean onoff) {
		this.LOGEMU = onoff;
	}

	public void enableGfxLog(boolean onoff) {
		this.LOGGFX = onoff;
	}

	public void enableGfxExtLog(boolean onoff) {
		this.LOGGFX_EXT = onoff;
	}

	public void enableHntLog(boolean onoff) {
		this.LOGHNT = onoff;
	}

	public void enableSndLog(boolean onoff) {
		this.LOGSND = onoff;
	}

	/*
	 * Get code offset from virtual pointer Rev. calc offset in code array
	 */

	int effective(int ptr) {
		if ((version < 4) && (mem_size == 0x10000))
			return (ptr & 0xffff);
		if (ptr >= 131072)
			ptr = ptr - 65536;
		// if (ptr>=mem_size)
		// ms_fatal("Outside memory experience - effective");
		// return 0;
		return ptr;
	}

	/* Auxilliary methods for reading, writing long, word */

	int read_l(char[] data, int off) {

		int tmp1 = data[off] << 24;
		int tmp2 = data[off + 1] << 16;
		int tmp3 = data[off + 2] << 8;
		int tmp4 = data[off + 3];
		int res = (int) ((int) tmp1 | (int) tmp2 | (int) tmp3 | (int) tmp4);
		return res;
	}

	long read_ll(char[] data, int off) {

		long tmp1 = data[off] << 24;
		long tmp2 = data[off + 1] << 16;
		long tmp3 = data[off + 2] << 8;
		long tmp4 = data[off + 3];
		long res = (long) ((long) tmp1 | (long) tmp2 | (long) tmp3 | (long) tmp4);
		return res;
	}

	int read_l2(char[] data, int off) {

		int tmp1 = data[off + 1] << 24;
		int tmp2 = data[off] << 16;
		int tmp3 = data[off + 3] << 8;
		int tmp4 = data[off + 2];
		int res = (int) ((int) tmp1 | (int) tmp2 | (int) tmp3 | (int) tmp4);
		return res;
	}

	int read_w(char[] data, int off) {
		int res;
		res = (char) data[off] << 8 | (char) data[off + 1];
		return res;
	}

	int read_w2(char[] data, int off) {
		int res;
		res = (char) data[off + 1] << 8 | (char) data[off];
		return res;
	}

	void write_l(char[] data, int off, int val) {
		data[off + 3] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off + 2] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off + 1] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
	}

	void write_ll(char[] data, int off, long val) {
		data[off + 3] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off + 2] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off + 1] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
	}

	void write_w(char[] data, int off, int val) {
		data[off + 1] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
		val >>>= 8;
		data[off] = ((byte) val < 0) ? (char) ((byte) val + 256)
				: (char) ((byte) val);
	}

	/* Standard rand - for equal cross-platform behaviour */

	void ms_seed(int seed) {
		rseed = seed;
	}

	int rand_emu() {
		rseed = 1103515245 * rseed + 12345;
		return (rseed & 0x7fffffff);
	}

	/*
	 * Function removed because of garbage collection void ms_freemem(void)
	 */
	public char ms_is_running() {
		return running;
	}

	public boolean ms_is_magwin() {
		if (version == 4)
			return true;
		else
			return false;
	}

	public void ms_stop() {
		// System.out.println("STOP reached");
		if (LOGEMU)
			log("logemu", "STOP reached");

		running = 0;
	}

	byte init_gfx1(char[] header2) throws IOException {
		gfx_buf = new char[51200];
		if (gfx_buf == null) {
			gfx_fp.close();
			gfx_fp = null;
			return 1;
		}
		gfx_data = new char[read_l(header2, 4) - 8];
		if (gfx_data == null) {
			gfx_fp.close();
			gfx_buf = null;
			gfx_fp = null;
			return 1;
		}
		if (ReadUbyteFromFile(gfx_fp, gfx_data) != gfx_data.length) {
			gfx_fp.close();
			gfx_data = gfx_buf = null;
			gfx_fp = null;
			return 1;
		}

		gfx_fp.close();
		gfx_fp = null;

		gfx_ver = 1;
		return 2;
	}

	byte init_gfx2(char[] header) throws IOException {
		gfx_buf = new char[51200];
		if (gfx_buf == null) {
			gfx_fp.close();
			gfx_fp = null;
			return 1;
		}

		gfx2_hsize = read_w(header, 4);
		gfx2_hdr = new char[gfx2_hsize];
		if (gfx2_hdr == null) {
			gfx_buf = null;
			gfx_fp.close();
			gfx_fp = null;
			return 1;
		}

		gfx_fp.seek(6);

		if (ReadUbyteFromFile(gfx_fp, gfx2_hdr, gfx2_hsize) != gfx2_hsize) {
			gfx_fp.close();
			gfx_buf = null;
			gfx2_hdr = null;
			gfx_fp = null;
			return 1;
		}

		gfx_ver = 2;
		return 2;
	}

	/* zero all registers and flags and load the game */

	public byte ms_init(String name, String gfxname, String hntname,
			String sndname) throws IOException {
		RandomAccessFile fp;
		char[] header = new char[42];
		char[] header2 = new char[8];
		char[] header3 = new char[4];
		int i, dict_size, string2_size, code_size, dec;

		running = 0;

		ms_stop();
		if (name == null) {
			if (restart == null) {
				// System.out.println("OOPS. Aborting");
				if (LOGEMU)
					log("logemu", "OOPS. Aborting");
				return 0;
			} else {
				System.arraycopy(restart, 0, code, 0, undo_size);
				undo_stat[0] = 0;
				undo_stat[1] = 0;
				ms_showpic((byte) 0, (byte) 0);
			}
		} else {
			ms_seed((int) (System.currentTimeMillis()));
			// ms_seed( 12345 );
			fp = new RandomAccessFile(name, "r");
			if ((ReadUbyteFromFile(fp, header) != 42)
					|| (read_l(header, 0) != 0x4d615363)) {
				fp.close();
				return 0;
			}
			if (read_l(header, 8) != 42) { /* Bad style, but it works for now */
				fp.close();
				return 0;
			}

			version = header[13]; /* all Offsets dec 1 */
			code_size = (int) ((read_l(header, 14)));
			string_size = read_l(header, 18);
			string2_size = read_l(header, 22);
			dict_size = read_l(header, 26);
			undo_size = read_l(header, 34);
			undo_pc = read_l(header, 38);

			if ((version < 4) && (code_size < 65536))
				mem_size = 65536;
			else
				mem_size = code_size;

			sdd = (char) ((dict_size != (long) 0) ? 1 : 0); /*
															 * if (sd) =>
															 * separate dict
															 */

			code = new char[mem_size];
			tstring = new char[string_size];
			tstring2 = new char[string2_size];
			restart = new char[undo_size];

			if (version > 1)
				dict = new char[dict_size];
			if ((code == null) || (tstring == null) || (tstring2 == null)
					|| (restart == null) || ((sdd != 0) && (dict == null))) {
				fp.close();
				return 0;
			}

			undo[0] = new char[undo_size + 1];
			undo[1] = new char[undo_size + 1];
			if ((undo[0] == null) || (undo[1] == null)) {
				fp.close();
				return 0;
			}
			if (ReadUbyteFromFile(fp, code, code_size) != code_size) {
				fp.close();
				return 0;
			}

			if (code_size == 65535)
				fp.read();

			System.arraycopy(code, 0, restart, 0, undo_size); /* fast restarts */
			if (ReadUbyteFromFile(fp, tstring) != string_size) {
				fp.close();
				return 0;
			}
			if (ReadUbyteFromFile(fp, tstring2) != string2_size) {
				fp.close();
				return 0;
			}
			if ((sdd != 0) && ReadUbyteFromFile(fp, dict) != dict_size) {
				fp.close();
				return 0;
			}

			/* no pointer arithmetic, remember offset */
			dec = read_l(header, 30);
			if (dec >= string_size) {
				decode_src = tstring2;
				decode_offset = (int) (dec - string_size);
			} else {
				decode_src = tstring;
				decode_offset = dec;
			}
			fp.close();
		}

		// System.out.println("Initializing registers.\n");
		if (LOGEMU)
			log("logemu", "Initializing registers.\n");
		for (i = 0; i < 8; i++) {
			if (dreg[i] == null)
				dreg[i] = new Register32();
			if (areg[i] == null)
				areg[i] = new Register32();
			dreg[i].clearReg();
			areg[i].clearReg();
		}
		write_reg(8 + 7, 2, 0xfffe); /*
										 * Stack-pointer, -2 due to MS-DOS
										 * segments
										 */
		pc = 0;
		zflag = nflag = cflag = vflag = 0;
		i_count = 0;
		running = 1;

		if (name == null)
			return (byte) ((gfx_buf != null) ? 2 : 1); /* Restarted */

		// -> Hint file and sound file
		/* Try loading a hint file and a sound file */
		if (version == 4) {
			RandomAccessFile hnt_fp;

			// TODO File exists!
			if (hntname != null) {
				try {
					hnt_fp = new RandomAccessFile(hntname, "r");
					if (hnt_fp == null) {
						// System.out.println("Unable to open hint file
						// "+hntname);
						if (LOGEMU)
							log("logemu", "Unable to open hint file " + hntname);
					} else {
						// if ((ReadUbyteFromFile(fp,header) != 42) ||
						// (read_l(header,0) != 0x4d615363)) {
						if ((ReadUbyteFromFile(hnt_fp, header3) == 4)
								&& (read_l(header3, 0) == 0x4D614874)) {
							char[] buf = new char[8];
							int blkcnt, elcnt, ntype, elsize, conidx;

							/* Read number of blocks */
							ReadUbyteFromFile(hnt_fp, buf, 2);
							blkcnt = read_w2(buf, 0);
							if (LOGHNT) {
								log("loghnt", "Blocks: " + blkcnt);
							}
							conidx = 0;
							for (i = 0; i < blkcnt; i++) {
								JMagneticHint newHint = new JMagneticHint();
								if (LOGHNT) {
									log("loghnt", "Block No. " + i);
								}
								/* Read number of elements */
								ReadUbyteFromFile(hnt_fp, buf, 2);
								elcnt = read_w2(buf, 0);
								if (LOGHNT) {
									log("loghnt", "Elements: " + elcnt);
								}
								newHint.elcount = elcnt;

								/* Read node type */
								ReadUbyteFromFile(hnt_fp, buf, 2);
								ntype = read_w2(buf, 0);
								if (LOGHNT) {
									if (ntype == 1)
										log("loghnt", "Type: Node");
									else
										log("loghnt", "Type: Leaf");
								}
								newHint.nodetype = ntype;
								if (LOGHNT) {
									log("loghnt", "Elements:\n");
								}
								for (int j = 0; j < elcnt; j++) {
									ReadUbyteFromFile(hnt_fp, buf, 2);
									elsize = read_w2(buf, 0);
									char[] newEl = new char[elsize];
									ReadUbyteFromFile(hnt_fp, newEl, elsize);
									newHint.content.add(new String(newEl));
									if (LOGHNT) {
										log("loghnt", new String(newEl));
									}
								}
								/* Do we need a jump table? */
								if (ntype == 1) {
									if (LOGHNT) {
										log("loghnt", "Jump to block:\n");
									}
									for (int j = 0; j < elcnt; j++) {
										ReadUbyteFromFile(hnt_fp, buf, 2);
										int link = read_w2(buf, 0);
										newHint.links.add(new Integer(link));
										if (LOGHNT) {
											log("loghnt", "" + link);
										}
									}
								}

								/* Read the parent block */
								ReadUbyteFromFile(hnt_fp, buf, 2);
								newHint.parent = read_w2(buf, 0);
								if (LOGHNT) {
									log("loghnt", "Parent: " + newHint.parent);
								}
								this.hints.add(newHint);
							}
						}
						hnt_fp.close();
					}
				} catch (IOException e) {
					// File reading failed
				}
			}

			/* --> Sound file */
			RandomAccessFile snd_fp;
			int s_offset, s_length;

			if (sndname != null) {
				try {
					snd_fp = new RandomAccessFile(sndname, "r");
					if (snd_fp == null) {
						// System.out.println("Unable to open sound file
						// "+sndname);
						if (LOGEMU)
							log("logemu", "Unable to open sound file "
									+ sndname);
					} else {
						// if ((ReadUbyteFromFile(fp,header) != 42) ||
						// (read_l(header,0) != 0x4d615363)) {
						if ((ReadUbyteFromFile(snd_fp, header3) == 4)
								&& (read_l(header3, 0) == 0x4D615364)) {
							char[] buf = new char[8];
							int h_size, numSounds;
							long snd_fp_seek;

							/* Read header size and calculate num of sounds */
							ReadUbyteFromFile(snd_fp, buf, 2);
							numSounds = read_w(buf, 0) / 18; // 18= size of
																// header
																// element
							if (LOGSND) {
								log("logsnd", "Music scores: " + numSounds);
							}
							for (i = 0; i < numSounds; i++) {
								JMagneticSound newSound = new JMagneticSound();
								if (LOGSND) {
									log("logsnd", "Sound no. " + i);
								}
								/* Read name of element */
								ReadUbyteFromFile(snd_fp, buf, 8);
								newSound.name = (String.valueOf(buf)).trim();
								if (LOGSND) {
									log("logsnd", "Music name: "
											+ newSound.name);
								}

								/* Read tempo */
								ReadUbyteFromFile(snd_fp, buf, 2);
								newSound.tempo = read_w(buf, 0);
								if (LOGSND) {
									log("logsnd", "Recommended tempo: "
											+ newSound.tempo);
								}

								/* Read file offset */
								ReadUbyteFromFile(snd_fp, buf, 4);
								s_offset = read_l(buf, 0);
								if (LOGSND) {
									log("logsnd", "Sound offset: " + s_offset);
								}

								/* Read file length */
								ReadUbyteFromFile(snd_fp, buf, 4);
								s_length = read_l(buf, 0);
								if (LOGSND) {
									log("logsnd", "Sound length: " + s_length);
								}

								snd_fp_seek = snd_fp.getFilePointer();
								snd_fp.seek(s_offset);
								newSound.data = new char[s_length];
								ReadUbyteFromFile(snd_fp, newSound.data,
										s_length);
								snd_fp.seek(snd_fp_seek);

								this.sounds.put(newSound.name, newSound);
							}
						}
						snd_fp.close();
					}
				} catch (IOException e) {
					// File reading failed
				}
			}
		}

		// <- Hint file
		if (gfxname == null) {
			// System.out.println("INIT done - no gfx.");
			if (LOGEMU)
				log("logemu", "INIT done - no gfx.");
			return 1;
		} else {
			try {
				gfx_fp = new RandomAccessFile(gfxname, "r");
			} catch (IOException ioe) {
				;
			}
			if (gfx_fp == null)
				return 1;
		}

		if (ReadUbyteFromFile(gfx_fp, header2) != 8) {
			gfx_fp.close();
			gfx_fp = null;
			return 1;
		}

		if (version < 4 && read_l(header2, 0) == 0x4D615069) /* MaPi */
			return init_gfx1(header2);
		else if (version == 4 && read_l(header2, 0) == 0x4D615032) /* MaP2 */
			return init_gfx2(header2);
		gfx_fp.close();
		gfx_fp = null;
		// System.out.println("INIT done - with gfx.");
		if (LOGEMU)
			log("logemu", "INIT done - with gfx.");
		return 1;

	}

	boolean is_blank(int line, int width) {
		int i;

		for (i = line * width; i < ((line + 1) * width); i++)
			if (gfx_buf[i] != 0)
				return false;
		return true;
	}

	int ms_extract1(int pic) {
		int c, bit;
		char val;
		int tablesize, count;
		int i, j;
		int datasize, upsize, offset, buffer, decode_table, data;
		int w, h;
		int[] pal = new int[16];

		// if (gfx_buf == null) return 0;

		offset = read_l(gfx_data, 4 * pic);
		buffer = offset - 8;

		for (i = 0; i < 16; i++)
			pal[i] = read_w(gfx_data, buffer + 0x1c + 2 * i);
		w = read_w(gfx_data, buffer + 4) - read_w(gfx_data, buffer + 2);
		h = read_w(gfx_data, buffer + 6);

		tablesize = read_w(gfx_data, buffer + 0x3c);
		datasize = read_l(gfx_data, buffer + 0x3e);
		decode_table = buffer + 0x42;
		data = decode_table + tablesize * 2 + 2;
		upsize = h * w;

		for (i = 0, j = 0, count = 0, val = 0, bit = 7; i < upsize; i++, count--) {
			if (count == 0) {
				count = tablesize;
				while (count < 0x80) {
					if ((gfx_data[data + j] & (1 << bit)) != 0)
						count = gfx_data[decode_table + 2 * count];
					else
						count = gfx_data[decode_table + 2 * count + 1];
					if (bit == 0)
						j++;
					bit = (bit != 0) ? bit - 1 : 7;
				}
				count &= 0x7f;
				if (count >= 0x10)
					count -= 0x10;
				else {
					val = (char) count;
					count = 1;
				}
			}
			gfx_buf[i] = val;
		}
		for (j = w; j < upsize; j++)
			gfx_buf[j] ^= gfx_buf[j - w];

		for (; h > 0 && (is_blank(h - 1, w)); h--)
			;
		for (i = 0; h > 0 && (is_blank(i, w)); h--, i++)
			;
		//if (i != 0)
		//	System.out.println("Bild gefunden");
		CurrPHeight = h;
		CurrPWidth = w;
		CurrPalette = pal;
		return i * w;
	}

	int find_name_in_header(String name, boolean upper) {
		int header_pos = 0;

		if (upper) {
			name = name.toUpperCase();
		}

		if (name.length() > 6)
			name = name.substring(0, 6);

		while (header_pos < gfx2_hsize) {
			String hname = new String(gfx2_hdr, header_pos, name.length());
			if (hname.compareTo(name) == 0)
				return header_pos;
			header_pos += 16;
		}
		return -1;
	}

	void extract_frame(JMagneticFrameDescriptor frame) {
		int i, x, y, bit_x, mask, ywb, yw, value;
		int[] values = new int[4];

		if (frame.width * frame.height > 51200) {
			ms_fatal("picture too large");
		} else {

			for (y = 0; y < frame.height; y++) {
				ywb = y * frame.wbytes;
				yw = y * frame.width;

				for (x = 0; x < frame.width; x++) {
					if ((x % 8) == 0) {
						for (i = 0; i < 4; i++)
							values[i] = frame.data[frame.offset + ywb + (x / 8)
									+ (frame.plane_step * i)];
					}

					bit_x = 7 - (x & 7);
					mask = 1 << bit_x;
					value = ((values[0] & mask) >> bit_x) << 0
							| ((values[1] & mask) >> bit_x) << 1
							| ((values[2] & mask) >> bit_x) << 2
							| ((values[3] & mask) >> bit_x) << 3;
					value &= 15;

					gfx_buf[yw + x] = (char) value;
				}
			}
		}
	}

	public int ms_extract(int pic) {
		try {
			switch (gfx_ver) {
			case 1:
				return ms_extract1(pic);
			case 2:
				return ms_extract2(pic); // call code[pic], because here pic
											// is offset
			}
		} catch (IOException ioe) {
		}
		return 0;
	}

	//
	// Animation stuff
	//
	byte ms_extract2(int codeOffset) throws IOException {
		main_pic = new JMagneticPicDescriptor();
		int offset = 0, length = 0, i;
		int header_pos = -1;
		String name;
		int w, h;
		int[] pal = new int[16];

		int anim_data;
		long j;

		// System.out.println(codeOffset);
		// log("logemu","INIT done - no gfx.");

		int str_len = findnull(code, codeOffset);
		if (str_len > 0) {
			char[] str = new char[str_len];
			System.arraycopy(code, codeOffset, str, 0, str_len);
			name = new String(str);

			gfx2_name = name;

			// System.out.println(name);

			main_pic.isAnim = false;

			main_pic.pos_table_size = 0;
			if (gfx2_name.equals("outkit")) {
				int k = 98;
			}

			/* Find the uppercase (no animation) version of the picture first. */
			header_pos = find_name_in_header(name, true);

			// if (header_pos < 0)
			int ani_header_pos = find_name_in_header(name, false);
			if (ani_header_pos >= 0)
				header_pos = ani_header_pos;
			if (header_pos < 0)
				return 0;

			// System.out.println( "headerpos: "+header_pos+"\n");
			offset = read_l(gfx2_hdr, header_pos + 8);
			length = read_l(gfx2_hdr, header_pos + 12);

			if (offset != 0) {
				if (gfx2_buf != null) {
					gfx2_buf = null;
				}

				gfx2_buf = new char[length];
				if (gfx2_buf == null)
					return 0;

				try {
					gfx_fp.seek(offset);
				} catch (IOException seekerror) {
					gfx2_buf = null;
					return 0;
				}

				if (ReadUbyteFromFile(gfx_fp, gfx2_buf, length) != length) {
					gfx2_buf = null;
					return 0;
				}

				for (i = 0; i < 16; i++)
					pal[i] = read_w2(gfx2_buf, 4 + (2 * i));

				main_pic.mainFrame.data = gfx2_buf;
				main_pic.mainFrame.offset = 48;
				main_pic.mainFrame.data_size = read_l2(gfx2_buf, 38);
				main_pic.mainFrame.width = read_w2(gfx2_buf, 42);
				main_pic.mainFrame.height = read_w2(gfx2_buf, 44);
				main_pic.mainFrame.wbytes = (int) (main_pic.mainFrame.data_size / main_pic.mainFrame.height);
				main_pic.mainFrame.plane_step = (int) (main_pic.mainFrame.wbytes / 4);
				main_pic.mainFrame.mask = -1;
				extract_frame(main_pic.mainFrame);

				CurrPHeight = main_pic.mainFrame.height;
				CurrPWidth = main_pic.mainFrame.width;
				CurrPalette = pal;

				anim_data = 48 + main_pic.mainFrame.data_size; // relative to
																// gfx2_buf
				if ((gfx2_buf[anim_data] != 0xD0)
						|| (gfx2_buf[anim_data + 1] != 0x5E)) {
					int current;
					int frame_count, command_count;
					int value1, value2;

					main_pic.isAnim = true;
					main_pic.InitAnimContainer();

					current = anim_data + 6; // Offset to gfx2_buf
					frame_count = read_w2(gfx2_buf, anim_data + 2);

					for (i = 0; i < frame_count; i++) {
						JMagneticFrameDescriptor newFrame = new JMagneticFrameDescriptor();
						// Must be tested!!!
						newFrame.data = gfx2_buf;
						newFrame.offset = current + 10;
						newFrame.data_size = read_l2(gfx2_buf, current);
						newFrame.width = read_w2(gfx2_buf, current + 4);
						newFrame.height = read_w2(gfx2_buf, current + 6);
						newFrame.wbytes = (int) (newFrame.data_size / newFrame.height);
						newFrame.plane_step = (int) (newFrame.wbytes / 4);
						newFrame.mask = -1;

						current += newFrame.data_size + 12;
						value1 = read_w2(gfx2_buf, current - 2);
						value2 = read_w2(gfx2_buf, current);

						if ((value1 == newFrame.width)
								&& (value2 == newFrame.height)) {
							int skip;

							newFrame.mask = (current + 4);
							skip = read_w2(gfx2_buf, current + 2);
							current += (skip + 6);
						}

						main_pic.animFrames.add(newFrame);
					}

					main_pic.pos_table_size = read_w2(gfx2_buf, current - 2);
					for (int x = 0; x < main_pic.pos_table_size; x++)
						main_pic.anim_table.add(new Lookup());

					if (LOGGFX_EXT) {
						log("loggfx", "POSITION TABLE DUMP\n");
					}

					for (i = 0; i < main_pic.pos_table_size; i++) {
						int newPos = read_w2(gfx2_buf, current + 2);
						main_pic.pos_table_count.add(new Integer(newPos));
						current += 4;

						// new Position table for current frame
						Vector currTable = new Vector();

						int elCount = ((Integer) main_pic.pos_table_count
								.elementAt(i)).intValue();

						for (j = 0; j < elCount; j++) {
							JMagneticAniPos newAni = new JMagneticAniPos();
							newAni.x = (short) read_w2(gfx2_buf, current);
							newAni.y = (short) read_w2(gfx2_buf, current + 2);
							newAni.number = read_w2(gfx2_buf, current + 4) - 1;
							current += 8;
							if (LOGGFX_EXT) {
								// System.out.println("Position entry: Table:
								// "+i+" Entry: "+j+" X: "+newAni.x+" Y:
								// "+newAni.y+" Frame: "+newAni.number+"\n");
								log("loggfx", "Position entry: Table: " + i
										+ "  Entry: " + j + "  X: " + newAni.x
										+ " Y: " + newAni.y + " Frame: "
										+ newAni.number + "\n");
							}
							currTable.add(newAni);
						}

						main_pic.pos_table.add(currTable);
					}

					main_pic.command_count = read_w2(gfx2_buf, current);
					main_pic.command_table = current + 2;

					main_pic.command_index = 0;
					main_pic.anim_repeat = false;
					main_pic.pos_table_index = -1;
					main_pic.pos_table_max = -1;
					// main_pic.anim_table.clear();
				}
				return 99;
			}
			return 0;
		}
		return 0;
	}

	public boolean ms_animate(Vector positions /* of type JMagenticAniPos */) {
		boolean got_anim = false;
		int i, j, ttable;
		int count = 0;

		if ((main_pic == null) || (gfx_ver != 2))
			return false;
		if ((main_pic.pos_table_size == 0) || (main_pic.command_index < 0))
			return false;

		positions.clear();

		while (!got_anim) {
			if (main_pic.pos_table_max >= 0) {
				if (main_pic.pos_table_index < main_pic.pos_table_max) {
					for (i = 0; i < main_pic.pos_table_size; i++) {
						Lookup lu = (Lookup) main_pic.anim_table.elementAt(i);
						if (lu.flag > -1) {
							Vector currPosArray = (Vector) main_pic.pos_table
									.elementAt(i);
							JMagneticAniPos newPos = (JMagneticAniPos) currPosArray
									.elementAt(lu.flag);
							// ### Ist das richtig? pos_array[count] =
							// pos_table[i][anim_table[i].flag];
							if (LOGGFX_EXT) {
								log("loggfx", "Adding frame " + newPos.number
										+ " to buffer");
							}
							count++;

							int currPostTableCount = ((Integer) main_pic.pos_table_count
									.elementAt(i)).intValue();

							if (lu.flag < (currPostTableCount - 1))
								lu.flag++;
							if (lu.count > 0)
								lu.count--;
							else
								lu.flag = -1;

							// write back
							main_pic.anim_table.set(i, lu);

							positions.add(newPos);
						}
					}
					if (count > 0) {
						// *positions = pos_array;
						got_anim = true;
					}
					main_pic.pos_table_index++;
				}
			}

			if (!got_anim) {
				char command = gfx2_buf[main_pic.command_table
						+ main_pic.command_index];
				main_pic.command_index++;

				main_pic.pos_table_max = -1;
				main_pic.pos_table_index = -1;
				// main_pic.anim_table.clear();

				switch (command) {
				case 0x00:
					main_pic.command_index = -1;
					return false;
				case 0x01:
					if (LOGGFX_EXT) {
						log("loggfx", "Load Frame Table: "
								+ (int) command
								+ " Start at: "
								+ (int) gfx2_buf[main_pic.command_table
										+ main_pic.command_index + 1]
								+ " Count: "
								+ (int) gfx2_buf[main_pic.command_table
										+ main_pic.command_index + 2]);
					}

					ttable = gfx2_buf[main_pic.command_table
							+ main_pic.command_index];
					main_pic.command_index++;

					Lookup lu = (Lookup) main_pic.anim_table
							.elementAt(ttable - 1);

					// int offset1 =
					// main_pic.command_table+main_pic.command_index;
					// char fchar = gfx2_buf[main_pic.command_table];
					// char fchar2 = gfx2_buf[offset1];
					lu.flag = (gfx2_buf[main_pic.command_table
							+ main_pic.command_index]) - 1;
					main_pic.command_index++;
					// int offset2 =
					// main_pic.command_table+main_pic.command_index;
					// char fchar3 = gfx2_buf[offset2];

					lu.count = (gfx2_buf[main_pic.command_table
							+ main_pic.command_index]) - 1;
					main_pic.command_index++;

					/* Workaround for Wonderland "catter" animation */
					if (v4_id == 0) {
						if (gfx2_name.equalsIgnoreCase("catter")) {
							if (main_pic.command_index == 96)
								lu.count = 9;
							if (main_pic.command_index == 108)
								lu.flag = -1;
							if (main_pic.command_index == 156)
								lu.flag = -1;
						}
					}

					// write back
					main_pic.anim_table.set(ttable - 1, lu);

					break;
				case 0x02:
					if (LOGGFX_EXT) {
						log("loggfx", "Animate: "
								+ (int) gfx2_buf[main_pic.command_table
										+ main_pic.command_index]);
					}
					main_pic.pos_table_max = gfx2_buf[main_pic.command_table
							+ main_pic.command_index];
					main_pic.pos_table_index = 0;
					main_pic.command_index++;
					break;
				case 0x03:
					if (LOGGFX_EXT) {
						log("loggfx", "Stop/Repeat Param: "
								+ (int) gfx2_buf[main_pic.command_table
										+ main_pic.command_index]);
						main_pic.command_index = -1;
						return false;
					} else {
						if (v4_id == 0) {
							main_pic.command_index = -1;
							return false;
						} else {
							if (!this.waitForAnims) {
								main_pic.command_index = 0;
								main_pic.anim_repeat = true;
								main_pic.pos_table_index = -1;
								main_pic.pos_table_max = -1;
							} else {
								main_pic.command_index = -1;
								return false;
							}

							// main_pic.anim_table.clear();
							/*
							 * for (j = 0; j < MAX_POSITIONS; j++) {
							 * anim_table[j].flag = -1; anim_table[j].count =
							 * -1; }
							 */
						}
						break;
					}
				case 0x04:
					if (LOGGFX_EXT) {
						log("loggfx", "Unknown Command: "
								+ command
								+ " Prop1: "
								+ gfx2_buf[main_pic.command_table
										+ main_pic.command_index]
								+ "  Prop2: "
								+ gfx2_buf[main_pic.command_table
										+ main_pic.command_index + 1]
								+ " WARNING:not parsed");
					}
					main_pic.command_index += 3;
					return false;
				case 0x05:
					ttable = next_table;
					main_pic.command_index++;

					Lookup lu05 = (Lookup) main_pic.anim_table
							.elementAt(ttable - 1);

					lu05.flag = 0;
					lu05.count = (int) gfx2_buf[main_pic.command_table
							+ main_pic.command_index];

					// write back
					main_pic.anim_table.set(ttable - 1, lu05);

					main_pic.pos_table_max = (int) gfx2_buf[main_pic.command_table
							+ main_pic.command_index];
					main_pic.pos_table_index = 0;
					main_pic.command_index++;
					main_pic.command_index++;
					next_table++;
					break;
				default:
					ms_fatal("unknown animation command");
					main_pic.command_index = -1;
					return false;
				}
			}
		}
		if (LOGGFX_EXT) {
			log("loggfx", "ms_animate() returning " + count + " frames");
		}
		return got_anim;
	}

	public JMagneticAFProps ms_get_anim_frame(int number) {
		JMagneticAFProps props = new JMagneticAFProps();
		if (number >= 0) {
			try {
				JMagneticFrameDescriptor frame = (JMagneticFrameDescriptor) main_pic.animFrames
						.elementAt(number);
				extract_frame(frame);
				props.width = frame.width;
				props.height = frame.height;
				props.mask = frame.mask;
			} catch (Exception e) {
			}
			return props;
		}
		return null;
	}

	public boolean ms_anim_is_repeating() {
		return main_pic.anim_repeat;
	}

	void save_undo() {

		char[] tmp;
		byte i;
		int tmp32;

		tmp = new char[undo_size + 1];

		System.arraycopy(undo[0], 0, tmp, 0, undo_size);
		System.arraycopy(undo[1], 0, undo[0], 0, undo_size);
		System.arraycopy(tmp, 0, undo[0], 0, undo_size);
		tmp = undo[0];
		undo[0] = undo[1];
		undo[1] = tmp;

		for (i = 0; i < 18; i++) {
			tmp32 = undo_regs[0][i];
			undo_regs[0][i] = undo_regs[1][i];
			undo_regs[1][i] = tmp32;
		}

		System.arraycopy(code, 0, undo[1], 0, undo_size);
		for (i = 0; i < 8; i++) {
			undo_regs[1][i] = dreg[i].rread_l();
			undo_regs[1][8 + i] = areg[i].rread_l();
		}
		undo_regs[1][16] = i_count;
		undo_regs[1][17] = pc;

		undo_stat[0] = undo_stat[1];
		undo_stat[1] = 1;
	}

	char ms_undo() {
		byte i;

		ms_flush();
		if (undo_stat[0] == 0)
			return 0;

		undo_stat[0] = undo_stat[1] = 0;
		System.arraycopy(undo[0], 0, code, 0, undo_size);
		for (i = 0; i < 8; i++) {
			dreg[i].rwrite_l(undo_regs[0][i]);
			areg[i].rwrite_l(undo_regs[0][8 + i]);
		}
		i_count = undo_regs[0][16];
		pc = undo_regs[0][17]; /* status flags intentionally omitted */
		return (char) 1;
	}

	public void log_status() {
		short j;
		StringBuffer s = new StringBuffer();

		s.append("D0: ");
		for (j = 0; j < 8; j++)
			s.append(" " + Integer.toHexString(read_reg(j, 3)));
		// System.err.println("");
		log("logemu", s.toString());
		s = new StringBuffer();
		s.append("A0:");
		for (j = 0; j < 8; j++)
			s.append(" " + Integer.toHexString(read_reg((8 + j), 3)));
		log("logemu", s.toString());
		log("logemu", "PC=" + Integer.toHexString(pc) + " ZCNV=" + (zflag & 1)
				+ (cflag & 1) + (nflag & 1) + (vflag & 1) + " - " + i_count
				+ " instructions");
		System.err.println("");
	}

	public void ms_status() {
		short j;

		System.out.print("D0: ");
		for (j = 0; j < 8; j++)
			System.out.print(" " + Integer.toHexString(read_reg(j, 3)));
		System.out.println("");
		System.out.print("A0:");
		for (j = 0; j < 8; j++)
			System.out.print(" " + Integer.toHexString(read_reg((8 + j), 3)));
		System.out.println("");
		System.out.print("PC=" + Integer.toHexString(pc) + " ZCNV="
				+ (zflag & 1) + (cflag & 1) + (nflag & 1) + (vflag & 1) + " - "
				+ i_count + " instructions");
		System.out.println("");
	}

	public long ms_count() {
		return i_count;
	}

	/* align register pointer for word/byte accesses */

	byte reg_align(char size) {
		byte bRet;

		bRet = 0;
		if (size == 1)
			bRet = 2;
		if (size == 0)
			bRet = 3;

		return bRet;
	}

	int read_reg(int i, int s) {
		Register32 ptr;

		if (i > 15) {
			ms_fatal("invalid register - read_reg");
			return 0;
		}
		if (i < 8)
			ptr = dreg[i];
		else
			ptr = areg[i - 8];

		switch (s) {
		case 0:
			return (int) (ptr.rread_b(reg_align((char) 0)));
		case 1:
			return (int) (ptr.rread_w(reg_align((char) 1)));
		default:
			return ptr.rread_l();
		}
	}

	/* Register access */
	void write_reg(int i, int s, int val) {
		Register32 ptr;

		if (i > 15) {
			ms_fatal("invalid register - write_reg");
			return;
		}
		if (i < 8)
			ptr = dreg[i];
		else
			ptr = areg[i - 8];

		switch (s) {
		case 0:
			ptr.rwrite_b(reg_align((char) 0), val);
			break;
		case 1:
			ptr.rwrite_w(reg_align((char) 1), val);
			break;
		default:
			ptr.rwrite_l(val);
			break;
		}

	}

	/* [35c4] */

	void char_out(char c) {

		if (c == 0xff) {
			big = 1;
			return;
		}
		c &= 0x7f;
		// c&=0x7f;
		if (read_reg(3, 0) != 0) {
			if (c == 0x5f)
				c = 0x20;
			if ((c >= 'a') && (c <= 'z'))
				c &= 0xdf;
			if (version < 4)
				ms_statuschar(c);
			return;
		}
		if (c == 0x5e)
			c = 0x0a;
		if (c == 0x40) {
			if (read_reg(2, 0) != 0)
				return;
			else
				c = 0x73;
		}
		if (version < 3 && c == 0x7e) {
			lastchar = 0x7e;
			c = 0x0a;
		}
		if (((c > 0x40) && (c < 0x5b)) || ((c > 0x60) && (c < 0x7b))) {
			if (big != 0) {
				c &= 0xdf;
				big = 0;
			}
			if (period != 0)
				char_out((char) 0x20);
		}
		period = 0;
		// if ((c==0x2e) || (c==0x3f) || (c==0x21) || (c==0x0a)) big=1;
		if (version < 4) {
			if ((c == 0x2e) || (c == 0x3f) || (c == 0x21) || (c == 0x0a))
				big = 1;
			else if (c == 0x22)
				big = 0;
		} else {
			if ((c == 0x20) && (lastchar == 0x0a))
				return;
			if ((c == 0x2e) || (c == 0x3f) || (c == 0x21) || (c == 0x0a))
				big = 1;
			else if (c == 0x22)
				big = 0;
		}
		//
		if (((c == 0x20) || (c == 0x0a)) && (c == lastchar))
			return;
		if (version < 3) {
			if (pipe != 0) {
				pipe = 0;
				return;
			}
			if (c == 0x7c) {
				pipe = 1;
				return;
			}
		} else {
			if (c == 0x7e) {
				c = 0x0a;
				if (lastchar != 0x0a)
					char_out((char) 0x0a);
			}
		}
		lastchar = c;
		if (c == 0x5f)
			c = 0x20;
		if ((c == 0x2e) || (c == 0x2c) || (c == 0x3b) || (c == 0x3a)
				|| (c == 0x21) || (c == 0x3f)) {
			period = 1;
		}
		ms_putchar(c);
	}

	/* extract addressing mode information [1c6f] */

	void set_info(char b) {
		regnr = (char) (b & 0x07);
		admode = (char) ((b >>> 3) & 0x07);
		opsize = (char) (b >>> 6);
	}

	/* read a word and increase pc */

	void read_word() {
		int epc, a, b;

		epc = effective(pc);
		byte1 = (char) code[epc];
		a = code[epc];

		byte2 = (char) code[epc + 1];
		b = code[epc + 1];
		pc += 2;
	}

	/* get addressing mode and set arg1 [1c84] */

	void set_arg1() {
		char[] tmp = new char[2];
		char l1c;

		is_reversible = 1;
		switch (admode) {
		case 0:
			acc1 = dreg[regnr].bytes; /* Dx */
			arg1 = reg_align(opsize);
			is_reversible = 0;
			if (LOGEMU) {
				log("logemu", " d" + regnr);
			}
			break;
		case 1:
			acc1 = areg[regnr].bytes;
			arg1 = reg_align(opsize);
			is_reversible = 0;
			if (LOGEMU) {
				log("logemu", " a" + regnr);
			}
			break;
		case 2:
			arg1i = read_reg((8 + regnr), 2); /* (Ax) */
			if (LOGEMU) {
				log("logemu", " (a" + regnr + ")");
			}
			break;
		case 3:
			arg1i = read_reg((8 + regnr), 2); /* (Ax)+ */
			write_reg(8 + regnr, 2, read_reg((8 + regnr), 2) + (1 << opsize));
			if (LOGEMU) {
				log("logemu", " (a" + regnr + ")+");
			}
			break;
		case 4:
			write_reg(8 + regnr, 2, read_reg((8 + regnr), 2) - (1 << opsize));
			arg1i = read_reg((8 + regnr), 2); /* -(Ax) */
			if (LOGEMU) {
				log("logemu", " -(a" + regnr + ")");
			}
			break;
		case 5:
			arg1i = read_reg((8 + regnr), 2)
					+ (short) read_w(code, effective(pc));
			pc += 2; /* offset.w(Ax) */
			if (LOGEMU) {
				log("logemu", " " + (short) read_w(code, effective(pc)) + "(a"
						+ regnr + ")");
			}
			break;
		case 6:
			tmp[0] = byte1;
			tmp[1] = byte2;
			read_word(); /* offset.b(Ax, Dx/Ax) [1d1c] */
			StringBuffer s = new StringBuffer();
			if (LOGEMU)
				s.append(" " + (int) byte2 + "(a" + regnr + ",");

			arg1i = read_reg((regnr + 8), 2) + (byte) byte2;
			if (LOGEMU) {
				if ((byte1 >>> 4) > 8)
					s.append("a" + ((byte1 >>> 4) - 8));
				else
					s.append("d" + (byte1 >>> 4));
			}
			if ((byte1 & 0x08) != 0) {
				if (LOGEMU) {
					log("logemu", s.append(".l)").toString());
				}
				int ix1 = (int) read_reg((byte1 >>> 4), 2);
				arg1i += ix1;
			} else {
				if (LOGEMU) {
					log("logemu", s.append(".w)").toString());
				}
				short sx1 = (short) read_reg((byte1 >>> 4), 1);
				arg1i += sx1;
			}
			byte1 = tmp[0];
			byte2 = tmp[1];
			break;
		case 7: /* specials */
			switch (regnr) {

			case 0:
				arg1i = read_w(code, effective(pc)); /* $xxxx.W */
				pc += 2;
				if (LOGEMU)
					log("logemu", arg1i + ".w");
				break;
			case 1:
				arg1i = read_l(code, effective(pc)); /* $xxxx */
				pc += 4;
				if (LOGEMU)
					log("logemu", "" + arg1i);
				break;
			case 2:
				arg1i = (short) read_w(code, effective(pc)) + pc; /* $xxxx(PC) */
				pc += 2;
				if (LOGEMU)
					log("logemu", " " + arg1i + "(pc)");
				break;
			case 3:
				l1c = code[effective(pc)]; /* $xx(PC,A/Dx) */
				if (LOGEMU)
					log("logemu", " ???2");
				if ((l1c & 0x08) != 0)
					arg1i = pc + (int) read_reg((l1c >>> 4), 2);
				else
					arg1i = pc + (short) read_reg((l1c >>> 4), 1);
				l1c = code[effective(pc) + 1];
				pc += 2;
				arg1i += (byte) l1c;
				break;
			case 4:
				arg1i = pc; /* #$xxxx */
				if (opsize == 0)
					arg1i += 1;
				pc += 2;
				if (opsize == 2)
					pc += 2;
				if (LOGEMU)
					log("logemu", " #" + arg1i);
				break;
			}
			break;
		}
		if (is_reversible != 0) {
			acc1 = code;
			arg1 = effective(arg1i);
		}
	}

	/* get addressing mode and set arg2 [1bc5] */

	void set_arg2_nosize(int use_dx, char b) { /* c=0 corresponds to use_dx */

		if (use_dx != 0) {
			acc2 = dreg[((b & 0x0e) << 1) / 4].bytes; /*
														 * 4 is logical byte
														 * count of reg.
														 */
			if ((((b & 0x0e) << 1) % 4) != 0)
				ms_fatal("reg align is not null - set_arg2");
		} else {
			acc2 = areg[((b & 0x0e) << 1) / 4].bytes;
			if ((((b & 0x0e) << 1) % 4) != 0)
				ms_fatal("reg align is not null - set_arg2");
		}
		// VORSICHT: UNTERSCHIED ZU EMU.C
		// arg2= reg_align(opsize);
		arg2 = 0;

	}

	void set_arg2(int use_dx, char b) { /* c=0 corresponds to use_dx */

		set_arg2_nosize(use_dx, b);
		arg2 = reg_align(opsize);
	}

	/* [1b9e] */

	void swap_args() {
		char[] tmp;
		int tmp2;
		int a, b, c, d, e, f, g, h;

		/*
		 * if (i_count >= 55311) { a = acc1[arg1]; b = acc1[arg1+1]; c =
		 * acc1[arg1+2]; d = acc1[arg1+3]; e = acc2[arg2]; f = acc2[arg2+1]; g =
		 * acc2[arg2+2]; h = acc2[arg2+3]; }
		 */

		tmp = acc1;
		tmp2 = arg1;
		acc1 = acc2;
		arg1 = arg2;
		acc2 = tmp;
		arg2 = tmp2;
	}

	/* [1cdc] */

	void push(int c) {

		write_reg(15, 2, read_reg(15, 2) - 4);
		write_l(code, effective(read_reg(15, 2)), c);
	}

	/* [1cd1] */

	int pop() {
		int c;
		int tmp, tmp2;

		tmp = read_reg(15, 2);
		tmp2 = effective(tmp);
		c = read_l(code, tmp2);
		// Fixes for Magnetic Windows games
		if (c >= 131072)
			c = c - 65536;
		if (c >= mem_size)
			c = c - 65536;

		write_reg(15, 2, read_reg(15, 2) + 4);
		return c;
	}

	/* check addressing mode and get argument [2e85] */
	void get_arg() {
		if (LOGEMU) {
			log("logemu", " " + pc);
		}
		set_info(byte2);
		arg2 = effective(pc);
		acc2 = code;
		pc += 2;
		if (opsize == 2)
			pc += 2;
		if (opsize == 0)
			arg2 += 1;
		set_arg1();
	}

	void set_flags() {
		int i;
		int j, x;
		long y;

		zflag = nflag = 0;
		switch (opsize) {

		case 0:
			if (getarg(acc1, arg1) > 127)
				nflag = 0xff;
			if (getarg(acc1, arg1) == 0)
				zflag = 0xff;
			break;
		case 1:
			i = read_w(acc1, arg1); // hier geht's schief
			if (i == 0)
				zflag = 0xff;
			if ((i >>> 15) > 0)
				nflag = 0xff;
			break;
		case 2:
			j = read_l(acc1, arg1);
			if (j == 0)
				zflag = 0xff;
			x = (j >>> 31);
			y = (j >>> 31);
			if ((j >>> 31) > 0)
				nflag = 0xff;
			break;
		}
	}

	/* [263a] */

	char condition(char b) {
		char bRes;

		bRes = (char) 0x00;
		switch (b & 0x0f) {
		case 0:
			return (char) 0xff;
		case 1:
			return (char) 0x00;
		case 2:
			return (char) ((zflag | cflag) ^ 0xff);
		case 3:
			return (char) ((zflag | cflag));
		case 4:
			return (char) (cflag ^ 0xff);
		case 5:
			return (char) (cflag);
		case 6:
			return (char) (zflag ^ 0xff);
		case 7:
			return (char) (zflag);
		case 8:
			return (char) (vflag ^ 0xff);
		case 9:
			return (char) (vflag);
		case 10:
		case 12:
			return (char) (nflag ^ 0xff);
		case 11:
		case 13:
			return (char) (nflag);
		case 14:
			return (char) ((zflag | nflag) ^ 0xff);
		case 15:
			return (char) ((zflag | nflag));
		}
		return (bRes);
	}

	/* [26dc] */

	void branch(char b) {
		if (b == 0)
			pc += (short) read_w(code, effective(pc));
		else
			pc += (byte) b; /* changed from short to byte for sign */
		if (LOGEMU) {
			log("logemu", " " + pc);
		}
	}

	/* [2869] */

	void do_add(boolean adda) {
		if (adda) {
			int a, b, c;
			if (opsize == 0) {
				a = read_l(acc1, arg1);
				b = getarg(acc2, arg2);
				c = a + b;
				write_l(acc1, arg1, c);
			}
			if (opsize == 1) {
				a = read_l(acc1, arg1);
				b = read_w(acc2, arg2);
				if ((b > a) && (b >= 32768)) {
					b = b - 65536;
				}
				c = a + b;
				// if ((c > 65535) && (a <= 65535))
				// c = c - 65536;
				/*
				 * System.out.println("i_count="+i_count);
				 * System.out.println("a="+a); System.out.println("b="+b);
				 * System.out.println("c="+c);
				 */
				write_l(acc1, arg1, c);
			}
			if (opsize == 2)
				write_l(acc1, arg1, read_l(acc1, arg1) + read_l(acc2, arg2));
		} else {
			cflag = 0;
			if (opsize == 0) {
				setargval(acc1, arg1, (char) ((getarg(acc1, arg1) + getarg(
						acc2, arg2)) % 256));
				if (getarg(acc2, arg2) > getarg(acc1, arg1))
					cflag = 0xff;
			}
			if (opsize == 1) {
				write_w(acc1, arg1, (read_w(acc1, arg1) + read_w(acc2, arg2)));
				if (read_w(acc2, arg2) > read_w(acc1, arg1))
					cflag = 0xff;
			}
			if (opsize == 2) {
				write_l(acc1, arg1, read_l(acc1, arg1) + read_l(acc2, arg2));
				if (read_l(acc2, arg2) > read_l(acc1, arg1))
					cflag = 0xff;
			}
			if ((version < 3) || (quick_flag == 0)) { /* Corruption onwards */
				vflag = 0;
				set_flags();
			}
		}
	}

	/* [2923] */

	void do_sub(boolean suba) {
		if (suba) {
			if (opsize == 0)
				write_l(acc1, arg1, read_l(acc1, arg1)
						- (byte) getarg(acc2, arg2));
			if (opsize == 1)
				write_l(acc1, arg1, read_l(acc1, arg1)
						- (short) read_w(acc2, arg2));
			if (opsize == 2)
				write_l(acc1, arg1, read_l(acc1, arg1)
						- (int) read_l(acc2, arg2));
		} else {
			cflag = 0;
			if (opsize == 0) {
				if (getarg(acc2, arg2) > getarg(acc1, arg1)) {
					cflag = 0xff;
					setargval(acc1, arg1, (char) (getarg(acc1, arg1)
							- getarg(acc2, arg2) + 256));
				} else {
					setargval(acc1, arg1, (char) (getarg(acc1, arg1) - getarg(
							acc2, arg2)));
				}
			}
			if (opsize == 1) {
				int a = read_w(acc2, arg2);
				int b = read_w(acc1, arg1);
				if (a > b)
					cflag = 0xff;
				a = read_w(acc1, arg1);
				b = read_w(acc2, arg2);
				write_w(acc1, arg1, (a - b));
			}
			if (opsize == 2) {
				int l3, l4, lw;
				int l1 = read_l(acc2, arg2);
				int l2 = read_l(acc1, arg1);
				if (l1 < 0)
					l3 = Integer.MAX_VALUE;
				else
					l3 = l1;
				if (l2 < 0)
					l4 = Integer.MAX_VALUE;
				else
					l4 = l2;
				if (l3 > l4)
					cflag = 0xff;
				lw = l2 - l1;
				write_l(acc1, arg1, lw);
			}
			if ((version < 3) || (quick_flag == 0)) { /* Corruption onwards */
				vflag = 0;
				set_flags();
			}
		}
	}

	/* [283b] */

	void do_eor() {
		if (opsize == 0)
			setargval(acc1, arg1, (char) (getarg(acc1, arg1) ^ getarg(acc2,
					arg2)));
		if (opsize == 1)
			write_w(acc1, arg1, (read_w(acc1, arg1) ^ read_w(acc2, arg2)));
		if (opsize == 2)
			write_l(acc1, arg1, read_l(acc1, arg1) ^ read_l(acc2, arg2));
		cflag = vflag = 0;
		set_flags();
	}

	/* [280d] */

	void do_and() {
		if (opsize == 0)
			setargval(acc1, arg1, (char) (getarg(acc1, arg1) & getarg(acc2,
					arg2)));
		if (opsize == 1)
			write_w(acc1, arg1, (read_w(acc1, arg1) & read_w(acc2, arg2)));
		if (opsize == 2)
			write_l(acc1, arg1, read_l(acc1, arg1) & read_l(acc2, arg2));
		cflag = vflag = 0;
		set_flags();
	}

	/* [27df] */

	void do_or() {
		if (opsize == 0)
			setargval(acc1, arg1, (char) (getarg(acc1, arg1) | getarg(acc2,
					arg2)));
		if (opsize == 1)
			write_w(acc1, arg1, (read_w(acc1, arg1) | read_w(acc2, arg2)));
		if (opsize == 2)
			write_l(acc1, arg1, read_l(acc1, arg1) | read_l(acc2, arg2));
		cflag = vflag = 0;
		set_flags(); /* [1c2b] */
	}

	/* [289f] */

	void do_cmp() {
		char[] tmp;
		int tmp2, a, b, c, d;

		tmp = acc1;
		tmp2 = arg1;
		if (acc1 != code) {
			a = tmparg[0] = getarg(acc1, arg1);
			b = tmparg[1] = (arg1 < 3) ? (char) getarg(acc1, arg1 + 1) : 0x00;
			c = tmparg[2] = (arg1 < 2) ? (char) getarg(acc1, arg1 + 2) : 0x00;
			d = tmparg[3] = (arg1 < 1) ? (char) getarg(acc1, arg1 + 3) : 0x00;
		} else {
			tmparg[0] = getarg(acc1, arg1);
			tmparg[1] = getarg(acc1, arg1 + 1);
			tmparg[2] = getarg(acc1, arg1 + 2);
			tmparg[3] = getarg(acc1, arg1 + 3);
		}
		acc1 = tmparg;
		arg1 = 0;
		quick_flag = 0;
		do_sub(false);
		acc1 = tmp;
		arg1 = tmp2;
	}

	/* [2973] */

	void do_move() {

		if (opsize == 0)
			setargval(acc1, arg1, (char) (getarg(acc2, arg2)));
		if (opsize == 1)
			write_w(acc1, arg1, read_w(acc2, arg2));
		if (opsize == 2)
			write_l(acc1, arg1, read_l(acc2, arg2));
		if (version < 2 || admode != 1) { /*
											 * Jinxter: no flags if destination
											 * Ax
											 */
			cflag = vflag = 0;
			set_flags();
		}
	}

	char do_btst(char a) {

		a &= (admode != 0) ? 0x7 : 0x1f;
		while (admode == 0 && a >= 8) {
			a -= 8;
			if (arg1 == 0)
				ms_fatal("Violation of reg bounds - do_btst");
			arg1 -= 1;
			/*
			 * PROBLEM: Orginal: arg1-=1; Does this work? What happens if arg1 =
			 * 0-offset --> get reg below?
			 */
		}
		zflag = 0;
		if ((getarg(acc1, arg1) & (1 << a)) == 0)
			zflag = 0xff;
		return a;
	}

	/* bit operation entry point [307c] */
	void do_bop(char b, char a) {

		if (LOGEMU) {
			log("logemu", "bop (" + (int) b + "," + (int) a + ") ");
		}
		b = (char) (b & 0xc0);
		a = do_btst(a);
		if (LOGEMU) {
			if (b == 0x00)
				log("logemu", "no bop???");
		}
		if (b == 0x40) {
			setargval(acc1, arg1, (char) (getarg(acc1, arg1) ^ (1 << a)));
			if (LOGEMU) {
				log("logemu", "bchg");
			}
		} /* bchg */
		if (b == 0x80) {
			setargval(acc1, arg1,
					(char) (getarg(acc1, arg1) & ((1 << a) ^ 0xff)));
			if (LOGEMU) {
				log("logemu", "bclr");
			}
		} /* bclr */
		if (b == 0xc0) {
			setargval(acc1, arg1, (char) (getarg(acc1, arg1) | (1 << a)));
			if (LOGEMU) {
				log("logemu", "bset");
			}
		} /* bset */
	}

	void check_btst() {

		if (LOGEMU) {
			log("logemu", "btst");
		}
		set_info((char) (byte2 & 0x3f));
		set_arg1();
		set_arg2(1, byte1);
		do_bop(byte2, getarg(acc2, arg2));
	}

	void check_lea() {

		if (LOGEMU) {
			log("logemu", "lea");
		}
		if ((byte2 & 0xc0) == 0xc0) {
			set_info((char) byte2);
			opsize = 2;
			set_arg1();
			set_arg2(0, byte1);
			write_w(acc2, arg2, 5);
			if (is_reversible != 0)
				write_l(acc2, arg2, arg1i);
			else
				ms_fatal("illegal addressing mode for LEA - check_lea");
		} else {
			ms_fatal("unimplemented instruction CHK");
		}
	}

	/* [33cc] */

	void check_movem() {
		char l1c;

		if (LOGEMU) {
			log("logemu", "movem");
		}
		set_info((char) (byte2 - 0x40));
		read_word();
		for (l1c = 0; l1c < 8; l1c++) {
			if ((byte2 & 1 << l1c) != 0) {
				set_arg1();
				if (opsize == 2)
					write_l(acc1, arg1, read_reg((15 - l1c), 2));
				if (opsize == 1)
					write_w(acc1, arg1, read_reg((15 - l1c), 1));
			}
		}
		for (l1c = 0; l1c < 8; l1c++) {
			if ((byte1 & 1 << l1c) != 0) {
				set_arg1();
				if (opsize == 2)
					write_l(acc1, arg1, read_reg((7 - l1c), 2));
				if (opsize == 1)
					write_w(acc1, arg1, read_reg((7 - l1c), 1));
			}
		}
	}

	/* [3357] */

	void check_movem2() {
		char l1c;

		if (LOGEMU) {
			log("logemu", "movem (2)");
		}
		set_info((char) (byte2 - 0x40));
		read_word();
		for (l1c = 0; l1c < 8; l1c++) {
			if ((byte2 & 1 << l1c) != 0) {
				set_arg1();
				if (opsize == 2)
					write_reg(l1c, 2, read_l(acc1, arg1));
				if (opsize == 1)
					write_reg(l1c, 1, read_w(acc1, arg1));
			}
		}
		for (l1c = 0; l1c < 8; l1c++) {
			if ((byte1 & 1 << l1c) != 0) {
				set_arg1();
				if (opsize == 2)
					write_reg(8 + l1c, 2, read_l(acc1, arg1));
				if (opsize == 1)
					write_reg(8 + l1c, 1, read_w(acc1, arg1));
			}
		}
	}

	/* [30e4] in Jinxter, ~540 lines of 6510 spaghetti-code */
	/* The mother of all bugs, but hey - no gotos used :-) */

	void dict_lookup() {

		int dtab, doff, output, output_bak, output2;
		int obj_adj, adjlist, adjlist_bak;
		char c, c2, c3, flag, matchlen, longest, flag2;
		char restart = 0, accept = 0, bank;
		int i, tmp16, word;

		dtab = read_reg((8 + 5), 1); /* used by version>0 */
		output = read_reg((8 + 2), 1);
		write_reg(8 + 5, 1, read_reg((8 + 6), 1));
		doff = read_reg((8 + 3), 1);
		adjlist = read_reg((8 + 0), 1);

		bank = (char) read_reg(6, 0); /* l2d */
		flag = 0; /* l2c */
		word = 0; /* l26 */
		matchlen = 0; /* l2e */
		longest = 0; /* 30e2 */
		write_reg(0, 1, 0); /* apostroph */

		goon: while ((c = (sdd != 0) ? dict[doff] : code[effective(doff)]) != 0x81) {
			if (c >= 0x80) {
				if (c == 0x82) {
					flag = matchlen = 0;
					word = 0;
					write_reg(8 + 6, 1, read_reg((8 + 5), 1));
					bank++;
					doff++;
					continue goon;
				}
				c3 = c;
				c &= 0x5f;
				c2 = (char) (code[effective(read_reg((8 + 6), 1))] & 0x5f);
				if (c2 == c) {
					write_reg(8 + 6, 1, read_reg((8 + 6), 1) + 1);
					c = code[effective(read_reg((8 + 6), 1))];
					if ((c == 0) || (c == 0x20) || (c == 0x27)
							|| (version == 0 && (matchlen > 6))) {
						if (c == 0x27) {
							write_reg(8 + 6, 1, read_reg((8 + 6), 1) + 1);
							write_reg(0, 1, 0x200 + code[effective(read_reg(
									(8 + 6), 1))]);
						}
						if ((version < 4) || (c3 != 0xa0))
							accept = 1;
					} else
						restart = 1;
				} else if (version == 0 && matchlen > 6 && c2 == 0)
					accept = 1;
				else
					restart = 1;
			} else {
				c &= 0x5f;
				c2 = (char) (code[effective(read_reg((8 + 6), 1))] & 0x5f);
				if ((c2 == c && c != 0)
						|| (version != 0 && c2 == 0 && (c == 0x5f))) {
					if (version != 0 && c2 == 0 && (c == 0x5f))
						flag = 0x80;
					matchlen++;
					write_reg(8 + 6, 1, read_reg((8 + 6), 1) + 1);
					doff++;
				} else if (version == 0 && matchlen > 6 && c2 == 0)
					accept = 1;
				else
					restart = 1;
			}
			if (accept != 0) {
				code[effective(read_reg((8 + 2), 1))] = (version != 0) ? flag
						: 0;
				code[effective(read_reg((8 + 2), 1)) + 1] = bank;
				write_w(code, effective(read_reg((8 + 2), 1) + 2), word);
				write_reg(8 + 2, 1, read_reg((8 + 2), 1) + 4);
				if (matchlen >= longest)
					longest = matchlen;
				restart = 1;
				accept = 0;
			}
			if (restart != 0) {
				write_reg(8 + 6, 1, read_reg((8 + 5), 1));
				flag = matchlen = 0;
				word++;
				if (sdd != 0)
					while (dict[doff++] < 0x80)
						;
				else
					while (code[effective(doff++)] < 0x80)
						;
				restart = 0;
			}
		}
		write_w(code, effective(read_reg((8 + 2), 1)), 0xffff);

		if (version != 0) { /* version > 0 */
			output_bak = output; /* check synonyms */
			c = code[effective(output) + 1];
			while (c != 0xff) {
				if (c == 0x0b) {
					if (sdd != 0)
						tmp16 = read_w(dict, dtab
								+ read_w(code, effective(output + 2)) * 2);
					else
						tmp16 = read_w(code, effective(dtab
								+ read_w(code, effective(output + 2)) * 2));
					code[effective(output) + 1] = (char) (tmp16 & 0x1f);
					write_w(code, effective(output + 2), (tmp16 >>> 5));
				}
				output += 4;
				c = code[effective(output) + 1];
			}
			output = output_bak;
		}

		/* l22 = output2, l1e = adjlist, l20 = obj_adj, l26 = word, l2f = c2 */
		/* l1c = adjlist_bak, 333C = i, l2d = bank, l2c = flag, l30e3 = flag2 */

		write_reg(1, 1, 0); /* D1.W=0 [32B5] */
		flag2 = 0;
		output_bak = output;
		output2 = output;
		bank = (char) code[effective(output2) + 1];
		while (bank != 0xff) {
			obj_adj = read_reg((8 + 1), 1); /*
											 * A1.W - obj_adj, ie. adjs for this
											 * word
											 */
			write_reg(1, 0, 0); /* D1.B=0 */
			flag = code[effective(output2)]; /* flag */
			word = read_w(code, effective(output2 + 2)); /* wordnumber */
			output2 += 4; /* next match */
			if ((read_w(code, effective(obj_adj)) != 0) && (bank == 6)) { /*
																			 * Any
																			 * adjectives?
																			 */
				i = word;
				if (i != 0) { /* Find list of valid adjs */
					do {
						while (code[effective(adjlist++)] != 0)
							;
					} while (--i > 0);
				}
				adjlist_bak = adjlist;
				do {
					adjlist = adjlist_bak;
					c2 = code[effective(obj_adj) + 1]; /* given adjective */
					tmp16 = read_w(code, effective(obj_adj));
					if (tmp16 != 0) {
						obj_adj += 2;
						c = code[effective(adjlist++)];
						while ((c != 0) && (c - 3 != c2)) {
							c = code[effective(adjlist++)];
						}
						;
						if (c - 3 != c2)
							write_reg(1, 0, 1); /* invalid adjective */
					}
				} while (tmp16 != 0 && read_reg(1, 0) == 0);
				adjlist = read_reg((8 + 0), 1);
			}
			if (read_reg(1, 0) == 0) { /* invalid_flag */
				flag2 |= flag;
				code[effective(output)] = flag2;
				code[effective(output) + 1] = bank;
				write_w(code, effective(output + 2), word);
				output += 4;
			}
			bank = code[effective(output2) + 1];
		}
		write_reg(8 + 2, 1, output);
		output = output_bak;

		if ((flag2 & 0x80) != 0) {
			tmp16 = output;
			output -= 4;
			do {
				output += 4;
				c = code[effective(output)];
			} while ((c & 0x80) == 0);
			write_l(code, effective(tmp16),
					read_l(code, effective(output)) & 0x7fffffff);
			write_reg(8 + 2, 2, tmp16 + 4);
			if (longest > 1) {
				write_reg(8 + 5, 1, read_reg((8 + 5), 1) + longest - 2);
			}
		}
		write_reg(8 + 6, 1, read_reg((8 + 5), 1) + 1);
	}

	char getarg(char[] srcarr, int off) {

		return (srcarr[off]);

	}

	/* A0=findproperties(D0) [2b86], properties_ptr=[2b78] A0FE */

	void do_findprop() {
		int tmp;

		if ((version > 2) && ((read_reg(0, 1) & 0x3fff) > fp_size)) {
			int tmp1 = (read_reg(0, 1) & 0x3fff);
			int tmp2 = ((fp_size - tmp1) < 0) ? (fp_size - tmp1 + 65536)
					: (fp_size - tmp1);
			int tmp3 = tmp2 ^ 0xffff;
			tmp = tmp3 << 1;
			// tmp=(((fp_size-(read_reg(0,1) & 0x3fff)) ^ 0xffff) << 1);
			tmp += fp_tab;
			//System.out.println( "Adress: "+tmp);
			tmp = read_w(code, effective(tmp));
		} else {
			if (version < 2)
				write_reg(0, 2, read_reg(0, 2) & 0x7fff);
			else
				write_reg(0, 1, read_reg(0, 1) & 0x7fff);
			tmp = read_reg(0, 1);
		}
		tmp &= 0x3fff;
		//System.out.println( "Adress: "+tmp);
		write_reg(8 + 0, 2, tmp * 14 + properties);
	}

	void write_string() {
		char c, b;
		byte mask;
		int ptr;
		int offset;

		if (cflag == 0) { /* new string */
			ptr = read_reg(0, 1);
			if (ptr == 0)
				offset = 0;
			else {
				offset = read_w(decode_src, decode_offset + 0x100 + 2 * ptr);
				if (read_w(decode_src, 0x100) != 0) {
					if (ptr >= read_w(decode_src, decode_offset + 0x100))
						offset += string_size;
				}
			}
			mask = 1;
		} else {
			offset = offset_bak;
			mask = mask_bak;
		}
		do {
			c = 0;
			while (c < 0x80) {
				if (offset >= string_size)
					b = tstring2[offset - string_size];
				else
					b = tstring[offset];
				if ((b & mask) != 0)
					c = decode_src[decode_offset + 0x80 + c];
				else
					c = decode_src[decode_offset + c];
				mask <<= 1;
				if (mask == 0) {
					mask = 1;
					offset++;
				}
			}
			c &= 0x7f;
			if ((c != 0 && ((c != 0x40) || (lastchar != 0x20))))
				char_out(c);
		} while ((c != 0 && ((c != 0x40) || (lastchar != 0x20))));
		cflag = (c != 0) ? 0xff : (char) 0;
		if (c != 0) {
			offset_bak = offset;
			mask_bak = mask;
		}
	}

	//
	// HINT stuff ->
	void output_number(int number) {
		int tens = number / 10;

		if (tens > 0)
			ms_putchar((char) ('0' + tens));
		number -= (tens * 10);
		ms_putchar((char) ('0' + number));
	}

	int output_text(char[] text) {
		int i;

		for (i = 0; i < text.length; i++)
			ms_putchar(text[i]);
		return i;
	}

	int hint_input() {
		char c1 = '\0', c2 = '\0', c3 = '\0';

		output_text(">>".toCharArray());
		ms_flush();

		do {
			try {
				c1 = ms_getchar(0);
			} catch (IOException e) {
			}
		} while (c1 == '\n');
		if (c1 == 1)
			return -1; /* New game loaded, abort hints */

		try {
			c2 = ms_getchar(0);
		} catch (IOException e) {
		}
		if (c2 == 1)
			return -1;

		/* Get and discard any remaining characters */
		c3 = c2;
		while (c3 != '\n') {
			try {
				c3 = ms_getchar(0);
			} catch (IOException e1) {
			}
			if (c3 == 1)
				return -1;
		}
		ms_putchar('\n');

		if ((c1 >= '0') && (c1 <= '9')) {
			int number = c1 - '0';
			if ((c2 >= '0') && (c2 <= '9')) {
				number *= 10;
				number += c2 - '0';
			}
			return number;
		}

		if ((c1 >= 'A') && (c1 <= 'Z'))
			c1 = (char) ('a' + (c1 - 'A'));
		if ((c1 >= 'a') && (c1 <= 'z')) {
			switch (c1) {
			case 'e':
				return -2; /* End hints */
			case 'n':
				return -3; /* Next hint */
			case 'p':
				return -4; /* Show parent hint list */
			}
		}
		return 0;
	}

	int show_hints_text(Vector hints, int index) {
		int i = 0, j = 0;
		int input;
		JMagneticHint hint = (JMagneticHint) hints.elementAt(index);

		while (true) {
			switch (hint.nodetype) {

			case 1: /* folders */
				output_text("Hint categories:\n".toCharArray());
				for (i = 0; i < hint.elcount; i++) {
					output_number(i + 1);
					output_text(". ".toCharArray());
					output_text(((String) (hint.content.elementAt(i)))
							.toCharArray());
					// ms_putchar('\n');
				}
				output_text("Enter hint category number, ".toCharArray());
				if (hint.parent >= 0) // Differs from emu.c
					output_text("P for the parent hint menu, ".toCharArray());
				output_text("or E to end hints.\n".toCharArray());

				input = hint_input();
				switch (input) {
				case -1: /* A new game is being loaded */
					return 1;
				case -2: /* End hints */
					return 1;
				case -4: /* Show parent hint list */
					if (hint.parent >= 0) // Differs from emu.c
						return 0;
				default:
					if ((input > 0) && (input <= hint.elcount)) {
						if (show_hints_text(hints, ((Integer) hint.links
								.elementAt(input - 1)).intValue()) == 1)
							return 1;
					}
					break;
				}
				break;

			case 2: /* hints */
				if (i < hint.elcount) {
					output_number(i + 1);
					output_text(". ".toCharArray());
					output_text(((String) hint.content.elementAt(j))
							.toCharArray());

					if (i == hint.elcount - 1) {
						output_text("\nNo more hints.\n".toCharArray());
						return 0; /* Last hint */
					} else {
						output_text("\nEnter N for the next hint, "
								.toCharArray());
						output_text("P for the parent hint menu, "
								.toCharArray());
						output_text("or E to end hints.\n".toCharArray());
					}

					input = hint_input();
					switch (input) {
					case -1: /* A new game is being loaded */
						return 1;
					case -2: /* End hints */
						return 1;
					case -3:
						i++;
						break;
					case -4: /* Show parent hint list */
						return 0;
					}
				} else
					return 0;
				break;
			}
		}
		// return 0;
	}

	// <- HINT stuff

	void do_line_a() throws IOException {
		char l1c;
		char[] str = new char[256];
		int offset;
		int ptr, ptr2;
		int tmp16, tmp32;
		int str_offset, str_len;

		if ((byte2 < 0xdd) || (version < 4 && byte2 < 0xe4)
				|| (version < 2 && byte2 < 0xed)) {
			// System.err.println("\nDO_LINE_A - special io ");
			ms_flush(); /* flush output-buffer */
			rand_emu(); /* Increase game randomness */
			l1c = ms_getchar(1); /* 0 means UNDO */
			if (l1c == 1)
				return;
			if (l1c != 0)
				write_reg(1, 2, l1c); /* d1=getkey() */
			else {
				if ((l1c = ms_undo()) != 0)
					output_text(undo_ok);
				else
					output_text(undo_fail);
				if (l1c == 0x00)
					write_reg(1, 2, '\n');
			}
		} else {

			// System.err.println("\nDO_LINE_A - Opcode: "+ (byte2-0xdd));
			if (LOGEMU)
				log("logemu", "DO_LINE_A - Opcode: "+ (byte2-0xdd));
			switch (byte2 - 0xdd) {

			case 0: /* A0DD - Won't probably be needed at all */
				break;
			case 1: /* A0DE */
				write_reg(1, 0, 1); /* Should remove the manual check */
				break;
			case 2: /* A0DF */

				int a1reg = read_reg(9, 2);

				int dtype = code[a1reg+2];

				//ms_status(); // 0x3A4C
				if (LOGEMU)
				{
					StringBuffer df_dump = new StringBuffer();
					for (int u=0;u<=15;u++)
						df_dump.append(((code[a1reg+u]<=15)?"0":"")+(Integer.toHexString(code[a1reg+u])).toUpperCase()+" ");
					for (int u=3;u<=15;u++)
						df_dump.append((code[a1reg+u]>30 && code[a1reg+u]<128)?""+code[a1reg+u]:"_");
					log("logemu", "A0DF: "	+ df_dump);   
				}
//					for (int u=0;u<=15;u++)
//						System.out.print(((code[a1reg+u]<=15)?"0":"")+(Integer.toHexString(code[a1reg+u])).toUpperCase()+" ");
//					for (int u=3;u<=15;u++)
//						System.out.print((code[a1reg+u]>30 && code[a1reg+u]<128)?""+code[a1reg+u]:"_");
//					System.out.println();  

				switch (dtype)
				{
					case 0x07: // Picture
							if (LOGGFX) {
								String sPicName = "";
								int nameLen = code[a1reg] -2;
								char[] picName = new char[nameLen];
								if (nameLen > 0) {
									System.arraycopy(code, a1reg + 3, picName, 0,
										nameLen);
									sPicName = String.valueOf(picName);
								}
								log("loggfx", "PICTURE IS "	+ new String(picName));
							}
							/* gfx mode = normal, df is not called if graphics are off */
							ms_showpic(a1reg + 3, (byte)2);
							break;
					case 0x0A:// Open window commands
							  switch (code[a1reg+3])
					          {
					            case 0: /* Carried items */
										output_text(not_supported);
										break;
								case 1: /* Room items */
										output_text(not_supported);
										break;
								case 2: /* Map */
										output_text(not_supported);
										break;
								case 3: /* Compass */
										output_text(not_supported);
										break;
								case 4: /* Help/Hints */
									if (hints.size() > 0)
									{
										if (ms_showhints(hints) == 0)
											show_hints_text(hints,0);
									}
									else
										output_text(no_hints);
									break;
								}
								break;
					case 0x0D: // Music
								switch (code[a1reg+3])
								{
					            case 0: /* stop music */
					            	ms_playmusic( null );
										break;
								default: /* play music */
										String sSndName = "";
										int nameLen = code[a1reg] -2;
										char[] sndName = new char[nameLen];
										if (nameLen > 0) {
											System.arraycopy(code, a1reg + 3, sndName, 0,
															nameLen);
											sSndName = String.valueOf(sndName);
										}
										if (LOGSND) {
											log("logsnd", "MUSIC IS "	+ new String(sndName));
										}
										if (sndenabled)
											ms_playmusic( sSndName );
										break;
								}
								break;
				}
				break;
			case 3: /* A0E0 */
				/* printf("A0E0 stubbed\n"); */
				break;
			case 4: /* A0E1 Read from keyboard to (A1), status in D1 (0 for ok) */
				write_reg(1, 1, (int) 0);
				ms_flush();
				rand_emu();
				tmp32 = read_reg((8 + 1), 2);
				offset = effective(tmp32);
				tmp16 = 0;
				do {
					l1c = ms_getchar(1);
					if (l1c == 0) {
						if ((l1c = ms_undo()) != 0)
							output_text(undo_ok);
						else
							output_text(undo_fail);
						if (l1c == 0x00) {
							tmp16 = 0;
							str[tmp16++] = '\n';
							l1c = '\n';
							output_text("\n>".toCharArray());
						} else {
							ms_putchar('\n');
							return;
						}
					} else {
						if (l1c == 1)
							return;
						code[offset + (tmp16++)] = (char) l1c;
						if (LOGGFX) {
							log("loggfx", "" + (char) l1c);
						}
					}
				} while ((l1c != '\n') && (tmp16 < 256));
				write_reg(8 + 1, 2, tmp32 + (tmp16 - 1));
				if ((tmp16 != 256) && (tmp16 != 1)) {
					write_reg(1, 1, (int) 0);
				} else
					write_reg(1, 1, (int) 1);
				// System.out.println(i_count);
				// ms_status();
				break;
			case 5: /* A0E2 */
				System.out.println("Reached A0E2.");
				/* printf("A0E2 stubbed\n"); */
				break;
			case 6: /* A0E3 */
				/* printf("\nMoves: %u\n",read_reg(0,1)); */
				if (read_reg(1, 2) == 0) {
					// System.out.println( "Graphics off");
					if ((version < 4) || (read_reg(6, 2) == 0))
						ms_showpic((byte) 0, (byte) 0);
				}
				break;

			case 7: /* A0E4 sp+=4, RTS */
				write_reg(8 + 7, 1, read_reg((8 + 7), 1) + 4);
				pc = pop();
				break;

			case 8: /* A0E5 set z, RTS */
			case 9: /* A0E6 clear z, RTS */
				pc = pop();
				zflag = (byte2 == (char) 0xe5) ? 0xff : (char) 0;
				break;

			case 10: /* A0E7 set z */
				zflag = 0xff;
				break;

			case 11: /* A0E8 clear z */
				zflag = 0;
				break;

			case 12: /* A0E9 [3083 - j] */
				ptr = read_reg((8 + 0), 1);
				ptr2 = read_reg((8 + 1), 1);
				do {
					l1c = dict[ptr2++];
					code[effective(ptr++)] = l1c;
					// l1c=code[effective(ptr++)];
					// dict[ptr2]=l1c;
				} while ((l1c & 0x80) == 0);
				write_reg(8 + 0, 1, ptr);
				write_reg(8 + 1, 1, ptr2);
				break;

			case 13: /* A0EA A1=write_dictword(A1,D1=output_mode) */
				ptr = read_reg((8 + 1), 1);
				tmp32 = read_reg(3, 0);
				write_reg(3, 0, read_reg(1, 0));
				do {
					l1c = dict[ptr++];
					char_out(l1c);
				} while (l1c < 0x80);
				write_reg(8 + 1, 1, ptr);
				write_reg(3, 0, tmp32);
				break;

			case 14: /* A0EB [3037 - j] */
				dict[read_reg((8 + 1), 1)] = (char) read_reg(1, 0);
				break;

			case 15: /* A0EC */
				write_reg(1, 0, dict[read_reg((8 + 1), 1)]);
				break;

			case 16:
				ms_stop(); /* infinite loop A0ED */
				break;
			case 17:
				if (ms_init(null, null, null, null) == 0)
					ms_stop(); /* restart game ie. pc, sp etc. A0EE */
				break;
			case 18: // ms_fatal("undefined op 11 in do_line_a"); /*
						// undefined A0EF */
				break;
			case 19:
				// System.err.println("Showpic: "+ (byte)read_reg(0,0) );

				ms_showpic((byte) read_reg(0, 0), (byte) read_reg(1, 0)); /*
																			 * Do_picture(D0)
																			 * A0F0
																			 */
				break;
			case 20:
				ptr = read_reg((8 + 1), 1); /* A1=nth_string(A1,D0) A0F1 */
				tmp32 = read_reg(0, 1);
				while (tmp32-- > 0) {
					while (code[effective(ptr++)] != 0)
						;
				}
				write_reg(8 + 1, 1, ptr);
				break;

			case 21: /* [2a43] A0F2 */
				cflag = 0;
				write_reg(0, 1, read_reg(2, 1));
				do_findprop();
				ptr = read_reg((8 + 0), 1);
				while (read_reg(2, 1) > 0) {
					if ((read_w(code, effective(ptr + 12)) & 0x3fff) != 0) {
						cflag = 0xff;
						break;
					}
					if (read_reg(2, 1) == (read_reg(4, 1) & 0x7fff)) {
						cflag = 0xff;
						break;
					}
					ptr -= 0x0e;
					write_reg(2, 1, read_reg(2, 1) - 1);
				}
				break;

			case 22:
				char_out((char) read_reg(1, 0)); /* A0F3 */
				break;

			case 23: /* D7=Save_(filename A0) D1 bytes starting from A1 A0F4 */
				if (version < 4) {
					str_offset = (version < 4) ? effective(read_reg((8 + 0), 1))
							: 0;
					str_len = findnull(code, str_offset);
					if (str_len > 0) {
						str = new char[str_len];
						System.arraycopy(code, str_offset, str, 0, str_len);
					} else {
						str = null;
					}
				} else {
					str = null;
				}
				write_reg(7, 0, ms_save_file(str,
						effective(read_reg((8 + 1), 1)), read_reg(1, 1)));
				break;

			case 24: /* D7=Load_(filename A0) D1 bytes starting from A1 A0F5 */
				if (version < 4) {
					str_offset = (version < 4) ? effective(read_reg((8 + 0), 1))
							: 0;
					str_len = findnull(code, str_offset);
					if (str_len > 0) {
						str = new char[str_len];
						System.arraycopy(code, str_offset, str, 0, str_len);
					} else {
						str = null;
					}
				} else {
					str = null;
				}
				write_reg(7, 0, ms_load_file(str,
						effective(read_reg((8 + 1), 1)), read_reg(1, 1)));
				break;

			case 25: /* D1=Random(0..D1-1) [3748] A0F6 */
				l1c = (char) read_reg(1, 0);
				write_reg(1, 1, rand_emu() % ((l1c != 0) ? l1c : 1));
				break;

			case 26: /* D0=Random(0..255) [3742] A0F7 */
				tmp16 = rand_emu();
				write_reg(0, 0, tmp16 + (tmp16 >>> 8));
				break;

			case 27: /* write string [D0] [2999] A0F8 */
				write_string();
				break;

			case 28: /* Z,D0=Get_inventory_item(D0) [2a9e] A0F9 */
				zflag = 0;
				ptr = read_reg(0, 1);
				do {
					write_reg(0, 1, ptr);
					do {
						do_findprop();
						ptr2 = read_reg((8 + 0), 1); /* object properties */
						if (((code[effective(ptr2) + 5]) & 1) != 0)
							break; /* is_described or so */
						l1c = code[effective(ptr2) + 6]; /* some_flags */
						tmp16 = read_w(code, effective(ptr2 + 8)); /* parent_object */
						if (l1c == 0) { /* ordinary object? */
							if (tmp16 == 0)
								zflag = 0xff; /* return if parent()=player */
							break; /* otherwise try next */
						}
						if ((l1c & 0xcc) != 0) {
							break;
						} /* skip worn, bodypart, room, hidden */
						if (tmp16 == 0) { /* return if parent()=player? */
							zflag = 0xff;
							break;
						}
						write_reg(0, 1, tmp16); /* else look at parent() */
					} while (1 == 1);
					ptr--;
				} while ((zflag == 0) && (ptr != 0));
				write_reg(0, 1, ptr + 1);
				break;

			case 29: /* [2b18] A0FA */
				ptr = read_reg(8, 1);
				do {
					if (read_reg(5, 0) != 0) {
						if ((read_w(code, effective(ptr)) & 0x3fff) == read_reg(
								2, 1))
							l1c = 1;
						else
							l1c = 0;
					} else {
						if (code[effective(ptr)] == read_reg(2, 0))
							l1c = 1;
						else
							l1c = 0;
					}
					if (read_reg(3, 1) == read_reg(4, 1)) {
						cflag = 0;
						write_reg(8, 1, ptr);
					} else {
						write_reg(3, 1, read_reg(3, 1) + 1);
						ptr += 0x0e;
						if (l1c != 0) {
							cflag = 0xff;
							write_reg(8, 1, ptr);
						}
					}
				} while ((l1c == 0) && (read_reg(3, 1) != read_reg(4, 1)));
				break;

			case 30: /* [2bd1] A0FB */
				ptr = read_reg((8 + 1), 1);
				do {
					if (dict != null)
						while (dict[ptr++] < 0x80)
							;
					else
						while (code[effective(ptr++)] < 0x80)
							;
					write_reg(2, 1, read_reg(2, 1) - 1);
				} while (read_reg(2, 1) != 0);
				write_reg(8 + 1, 1, ptr);
				break;

			case 31: /* [2c3b] A0FC */
				ptr = read_reg((8 + 0), 1);
				ptr2 = read_reg((8 + 1), 1);
				do {
					if (dict != null)
						while (dict[ptr++] < 0x80)
							;
					else
						while (code[effective(ptr++)] < 0x80)
							;
					while (code[effective(ptr2++)] != 0)
						;
					write_reg(0, 1, read_reg(0, 1) - 1);
				} while (read_reg(0, 1) != 0);
				write_reg(8 + 0, 1, ptr);
				write_reg(8 + 1, 1, ptr2);
				break;

			case 32: /* Set properties pointer from A0 [2b7b] A0FD */
				properties = read_reg((8 + 0), 1);
				if (version > 0)
					fl_sub = read_reg((8 + 3), 1);
				if (version > 1) {
					fl_tab = read_reg((8 + 5), 1);
					fl_size = (read_reg(7, 1) + 1);
					/* A3 [routine], A5 [table] and D7 [table-size] */
				}
				if (version > 2) {
					fp_tab = read_reg((8 + 6), 1);
					fp_size = read_reg(6, 1);
				}
				break;

			case 33:
				do_findprop(); /* A0FE */
				break;

			case 34: /* Dictionary_lookup A0FF */
				dict_lookup();
				break;
			}
		}
	}

	/* emulate an instruction [1b7e] */

	public char ms_rungame() throws IOException {
		char l1c;
		int ptr;
		int tmp32;
		int testvar;

		if (running == 0)
			return running;
		if (pc == undo_pc)
			save_undo();

		if (LOGEMU) {
			if (pc != 0x0000) {
				//log_status();
			}

			//log("logemu", "" + pc);
		}

		i_count++;

		// if ((i_count >= 250000) && (i_count <= 280000))
		// {
		// //System.out.println(i_count);
		// //ms_status();
		// }
		//	
		// if (i_count == 55311)
		// {
		// //System.out.println("REACHED");
		// //ms_status();
		// }
		read_word();
		switch (byte1 >>> 1) {

		/* 00-0F */
		case 0x00:
			if (byte1 == 0x00) {
				if (byte2 == 0x3c || byte2 == 0x7c) {
					/* OR immediate to CCR (30D9) */
					read_word();
					if (LOGEMU) {
						log("logemu", "or_ccr #" + byte2);
					}
					if ((byte2 & 0x01) != 0)
						cflag = 0xff;
					if ((byte2 & 0x02) != 0)
						vflag = 0xff;
					if ((byte2 & 0x04) != 0)
						zflag = 0xff;
					if ((byte2 & 0x08) != 0)
						nflag = 0xff;
				} else { /* OR [27df] */
					if (LOGEMU) {
						log("logemu", "or");
					}
					get_arg();
					do_or();
				}
			} else
				check_btst();
			break;

		case 0x01:
			if (byte1 == 0x02) {
				if (byte2 == 0x3c || byte2 == 0x7c) {
					/* AND immediate to CCR */
					read_word();
					if (LOGEMU) {
						log("logemu", "and_ccr #" + byte2);
					}
					if ((byte2 & 0x01) == 0)
						cflag = 0;
					if ((byte2 & 0x02) == 0)
						vflag = 0;
					if ((byte2 & 0x04) == 0)
						zflag = 0;
					if ((byte2 & 0x08) == 0)
						nflag = 0;
				} else { /* AND */
					if (LOGEMU) {
						log("logemu", "and");
					}
					get_arg();
					do_and();
				}
			} else
				check_btst();
			break;

		case 0x02:
			if (byte1 == 0x04) { /* SUB */
				if (LOGEMU) {
					log("logemu", "sub");
				}
				get_arg();
				do_sub(false);
			} else
				check_btst();
			break;

		case 0x03:
			if (byte1 == 0x06) { /* ADD */
				if (LOGEMU) {
					log("logemu", "addi");
				}
				get_arg();
				do_add(false);
			} else
				check_btst();
			break;

		case 0x04:
			if (byte1 == 0x08) { /* bit operations (immediate) */
				set_info((char) (byte2 & 0x3f));
				l1c = code[effective(pc) + 1];
				pc += 2;
				set_arg1();
				do_bop(byte2, l1c);
			} else
				check_btst();
			break;

		case 0x05:
			if (byte1 == 0x0a) {
				if (byte2 == 0x3c || byte2 == 0x7c) {
					/* EOR immediate to CCR */
					read_word();
					if (LOGEMU) {
						log("logemu", "eor_ccr #" + byte2);
					}
					if ((byte2 & 0x01) != 0)
						cflag ^= 0xff;
					if ((byte2 & 0x02) != 0)
						vflag ^= 0xff;
					if ((byte2 & 0x04) != 0)
						zflag ^= 0xff;
					if ((byte2 & 0x08) != 0)
						nflag ^= 0xff;
				} else { /* EOR */
					if (LOGEMU) {
						log("logemu", "eor");
					}
					get_arg();
					do_eor();
				}
			} else
				check_btst();
			break;

		case 0x06:
			if (byte1 == 0x0c) { /* CMP */
				if (LOGEMU) {
					log("logemu", "cmp");
				}
				get_arg();
				do_cmp();
			} else
				check_btst();
			break;

		case 0x07:
			check_btst();
			break;

		/* 10-1F [3327] MOVE.B */
		case 0x08:
		case 0x09:
		case 0x0a:
		case 0x0b:
		case 0x0c:
		case 0x0d:
		case 0x0e:
		case 0x0f:

			if (LOGEMU) {
				log("logemu", "move.b");
			}
			set_info((char) (byte2 & 0x3f));
			set_arg1();
			swap_args();
			l1c = (char) (((byte1 >>> 1 & 0x07) | (byte2 >>> 3 & 0x18) | (byte1 << 5 & 0x20)));
			set_info(l1c);
			set_arg1();
			do_move();
			break;

		/* 20-2F [32d1] MOVE.L */
		case 0x10:
		case 0x11:
		case 0x12:
		case 0x13:
		case 0x14:
		case 0x15:
		case 0x16:
		case 0x17:
			if (LOGEMU) {
				log("logemu", "move.l");
			}

			set_info((char) (byte2 & 0x3f | 0x80));
			set_arg1();
			swap_args();
			l1c = (char) (((byte1 >>> 1 & 0x07) | (byte2 >>> 3 & 0x18) | (byte1 << 5 & 0x20)));
			set_info((char) (l1c | 0x80));
			set_arg1();
			do_move();
			break;

		/* 30-3F [3327] MOVE.W */
		case 0x18:
		case 0x19:
		case 0x1a:
		case 0x1b:
		case 0x1c:
		case 0x1d:
		case 0x1e:
		case 0x1f:

			if (LOGEMU) {
				log("logemu", "move.w");
			}
			set_info((char) (byte2 & 0x3f | 0x40));
			set_arg1();
			swap_args();
			l1c = (char) ((byte1 >>> 1 & 0x07) | (byte2 >>> 3 & 0x18) | (byte1 << 5 & 0x20));
			set_info((char) (l1c | 0x40));
			set_arg1();
			do_move();
			break;

		/* 40-4F various commands */

		case 0x20:
			if (byte1 == 0x40) { /* [31d5] */
				ms_fatal("unimplemented instructions NEGX and MOVE SR,xx");
			} else
				check_lea();
			break;

		case 0x21:
			if (byte1 == 0x42) { /* [3188] */
				if ((byte2 & 0xc0) == 0xc0) {
					ms_fatal("unimplemented instruction MOVE CCR,xx");
				} else { /* CLR */
					if (LOGEMU) {
						log("logemu", "clr");
					}
					set_info(byte2);
					set_arg1();
					if (opsize == 0)
						setargval(acc1, arg1, (char) 0);
					if (opsize == 1)
						write_w(acc1, arg1, 0);
					if (opsize == 2)
						write_l(acc1, arg1, 0);
					nflag = cflag = 0;
					zflag = 0xff;
				}
			} else
				check_lea();
			break;

		case 0x22:
			if (byte1 == 0x44) { /* [31a0] */
				if ((byte2 & 0xc0) == 0xc0) { /* MOVE to CCR */
					if (LOGEMU) {
						log("logemu", "move_ccr");
					}
					zflag = nflag = cflag = vflag = 0;
					set_info((char) (byte2 & 0x7f));
					set_arg1();
					byte2 = getarg(acc1, arg1 + 1);
					if ((byte2 & 0x01) != 0)
						cflag = 0xff;
					if ((byte2 & 0x02) != 0)
						vflag = 0xff;
					if ((byte2 & 0x04) != 0)
						zflag = 0xff;
					if ((byte2 & 0x08) != 0)
						nflag = 0xff;
				} else {
					if (LOGEMU) {
						log("logemu", "neg");
					}
					set_info(byte2); /* NEG */
					set_arg1();
					cflag = 0xff;
					if (opsize == 0) {
						setargval(acc1, arg1,
								(char) (-1 * getarg(acc1, arg1) + 256));
						cflag = ((getarg(acc1, arg1)) != 0) ? 0xff : (char) 0;
					}
					if (opsize == 1) {
						write_w(acc1, arg1, (short) (-1 * read_w(acc1, arg1)));
						cflag = ((read_w(acc1, arg1)) != 0) ? 0xff : (char) 0;
					}
					if (opsize == 2) {
						write_l(acc1, arg1, -read_l(acc1, arg1));
						cflag = (read_l(acc1, arg1) != 0) ? 0xff : (char) 0;
					}
					vflag = 0;
					set_flags();
				}
			} else
				check_lea();
			break;

		case 0x23:
			if (byte1 == 0x46) {
				if ((byte2 & 0xc0) == 0xc0) {
					ms_fatal("unimplemented instruction MOVE xx,SR");
				} else {
					if (LOGEMU) {
						log("logemu", "not");
					}
					set_info(byte2); /* NOT */
					set_arg1();
					tmparg[0] = tmparg[1] = tmparg[2] = tmparg[3] = 0xff;
					acc2 = tmparg;
					arg2 = 0;
					do_eor();
				}
			} else
				check_lea();
			break;

		case 0x24:
			if (byte1 == 0x48) {
				if ((byte2 & 0xf8) == 0x40) {
					if (LOGEMU) {
						log("logemu", "swap");
					}
					opsize = 2; /* SWAP */
					admode = 0;
					regnr = (char) (byte2 & 0x07);
					set_arg1();
					tmp32 = read_w(acc1, arg1);
					write_w(acc1, arg1, read_w(acc1, arg1 + 2));
					write_w(acc1, arg1 + 2, tmp32); /*
													 * PROBLEMATIC with long
													 * bounds??
													 */
					set_flags();
				} else if ((byte2 & 0xf8) == 0x80) {
					if (LOGEMU) {
						log("logemu", "ext.w");
					}
					opsize = 1; /* EXT.W */
					admode = 0;
					regnr = (char) (byte2 & 0x07);
					set_arg1();
					if (getarg(acc1, arg1 + 1) > 0x7f)
						setargval(acc1, arg1, (char) 0xff);
					else
						setargval(acc1, arg1, (char) 0);
					set_flags();
				} else if ((byte2 & 0xf8) == 0xc0) {
					if (LOGEMU) {
						log("logemu", "ext.l");
					}
					opsize = 2; /* EXT.L */
					admode = 0;
					regnr = (char) (byte2 & 0x07);
					set_arg1();
					if (read_w(acc1, arg1 + 2) > 0x7fff)
						write_w(acc1, arg1, 0xffff);
					else
						write_w(acc1, arg1, 0);
					set_flags();
				} else if ((byte2 & 0xc0) == 0x40) {
					if (LOGEMU) {
						log("logemu", "pea");
					}
					set_info((char) (byte2 & 0x3f | 0x80)); /* PEA */
					set_arg1();
					if (is_reversible != 0)
						push(arg1i);
					else
						ms_fatal("illegal addressing mode for PEA");
				} else {
					check_movem(); /* MOVEM */
				}
			} else
				check_lea();
			break;

		case 0x25:
			if (byte1 == 0x4a) { /* [3219] TST */
				if ((byte2 & 0xc0) == 0xc0) {
					ms_fatal("unimplemented instruction TAS");
				} else {
					if (LOGEMU) {
						log("logemu", "tst");
					}
					set_info(byte2);
					set_arg1();
					cflag = vflag = 0;
					set_flags();
				}
			} else
				check_lea();
			break;

		case 0x26:
			if (byte1 == 0x4c)
				check_movem2(); /* [3350] MOVEM.L (Ax)+,A/Dx */
			else
				check_lea(); /* LEA */
			break;

		case 0x27:
			if (byte1 == 0x4e) { /* [3290] */
				if (byte2 == 0x75) { /* RTS */
					if (LOGEMU) {
						log("logemu", "rts\n");
					}
					pc = pop();
				} else if (byte2 == 0x71) { /* NOP */
					if (LOGEMU) {
						log("logemu", "nop");
					}
				} else if ((byte2 & 0xc0) == 0xc0) { /* indir JMP */
					if (LOGEMU) {
						log("logemu", "jmp");
					}
					set_info((char) (byte2 | 0xc0));
					set_arg1();
					if (is_reversible != 0)
						pc = arg1i;
					else
						ms_fatal("illegal addressing mode for JMP");
				} else if ((byte2 & 0xc0) == 0x80) {
					if (LOGEMU) {
						log("logemu", "jsr");
					}
					set_info((char) (byte2 | 0xc0)); /* indir JSR */
					set_arg1();
					push(pc);
					if (is_reversible != 0)
						pc = arg1i;
					else
						ms_fatal("illegal addressing mode for JSR");
				} else {
					ms_fatal("unimplemented instructions 0x4EXX");
				}
			} else
				check_lea(); /* LEA */
			break;

		/* 50-5F [2ed5] ADDQ/SUBQ/Scc/DBcc */
		case 0x28:
		case 0x29:
		case 0x2a:
		case 0x2b:
		case 0x2c:
		case 0x2d:
		case 0x2e:
		case 0x2f:

			if ((byte2 & 0xc0) == 0xc0) {
				set_info((char) (byte2 & 0x3f));
				set_arg1();
				if (admode == 1) { /* DBcc */
					if (LOGEMU) {
						log("logemu", "dbcc");
					}
					if (condition(byte1) == 0) {
						// arg1=(arg1-(type8 *)areg)+(type8 *)dreg-1; /* nasty
						// */
						// really nasty, hope this works...
						if (arg1 < 1)
							ms_fatal(" DBcc bounds violation - ms_rungame 0x28");
						setargsrc(1, dreg[regnr].bytes, arg1 - 1);
						write_w(acc1, arg1, (read_w(acc1, arg1) - 1));
						if (read_w(acc1, arg1) != 0xffff) {
							branch((char) 0);
						} else {
							pc += 2;
						}
					} else {
						pc += 2;
					}
				} else { /* Scc */
					if (LOGEMU) {
						log("logemu", "scc");
					}
					setargval(acc1, arg1, ((condition(byte1) != 0) ? 0xff
							: (char) 0));
				}
			} else {
				set_info(byte2);
				set_arg1();
				quick_flag = (admode == 1) ? 0xff : (char) 0;
				l1c = (char) (byte1 >>> 1 & 0x07);
				tmparg[0] = tmparg[1] = tmparg[2] = 0;
				tmparg[3] = (l1c != 0) ? l1c : 8;
				setargsrc(2, tmparg, reg_align(opsize));
				long outnum = 0;
				if (LOGEMU) {
					switch (opsize) {
					case 0:
						outnum = (byte) acc1[arg2];
						break;
					case 1:
						outnum = (short) read_w(acc1, arg2);
						break;
					case 2:
						outnum = (int) read_l(acc1, arg2);
						break;
					}
				}
				if ((byte1 & 0x01) == 1) {
					if (LOGEMU) {
						log("logemu", "subq #" + outnum);
					}
					do_sub(false);
				} /* SUBQ */
				else {
					if (LOGEMU) {
						log("logemu", "addq #" + outnum);
					}
					do_add(false);
				} /* ADDQ */
			}
			break;

		/* 60-6F [26ba] Bcc */

		case 0x30:
			if (byte1 == 0x61) { /* BRA, BSR */
				if (LOGEMU) {
					log("logemu", "bsr");
				}
				if (byte2 == 0)
					push(pc + 2);
				else
					push(pc);
			} else {
				if (LOGEMU) {
					log("logemu", "bra");
				}
			}
			if ((byte1 == 0x60) && (byte2 == 0xfe)) {
				ms_flush(); /* flush stdout */
				ms_stop(); /* infinite loop - just exit */
			}
			branch(byte2);
			break;

		case 0x31:
		case 0x32:
		case 0x33:
		case 0x34:
		case 0x35:
		case 0x36:
		case 0x37:

			if (condition(byte1) == 0) {
				if (LOGEMU) {
					log("logemu", "beq.s");
				}
				if (byte2 == 0)
					pc += 2;
			} else {
				if (LOGEMU) {
					log("logemu", "bra");
				}
				branch(byte2);
			}
			break;

		/* 70-7F [260a] MOVEQ */
		case 0x38:
		case 0x39:
		case 0x3a:
		case 0x3b:
		case 0x3c:
		case 0x3d:
		case 0x3e:
		case 0x3f:

			if (LOGEMU) {
				log("logemu", "move.q");
			}
			setargsrc(1, dreg[byte1 >>> 1 & 0x07].bytes, 0);
			if (byte2 > 127) {
				setargval(acc1, arg1, (char) 0xff);
				setargval(acc1, arg1 + 1, (char) 0xff);
				setargval(acc1, arg1 + 2, (char) 0xff);
				nflag = 0xff;

			} else {
				nflag = 0;
				setargval(acc1, arg1, (char) 0);
				setargval(acc1, arg1 + 1, (char) 0);
				setargval(acc1, arg1 + 2, (char) 0);
			}
			setargval(acc1, arg1 + 3, byte2);
			zflag = (byte2 != 0) ? (char) 0 : 0xff;
			break;

		/* 80-8F [2f36] */
		case 0x40:
		case 0x41:
		case 0x42:
		case 0x43:
		case 0x44:
		case 0x45:
		case 0x46:
		case 0x47:

			if ((byte2 & 0xc0) == 0xc0) {
				ms_fatal("unimplemented instructions DIVS and DIVU");
			} else if (((byte2 & 0xf0) == 0) && ((byte1 & 0x01) != 0)) {
				ms_fatal("unimplemented instruction SBCD");
			} else {
				if (LOGEMU) {
					log("logemu", "or?");
				}
				set_info(byte2);
				set_arg1();
				set_arg2(1, byte1);
				if ((byte1 & 0x01) == 0)
					swap_args();
				do_or();
			}
			break;

		/* 90-9F [3005] SUB */
		case 0x48:
		case 0x49:
		case 0x4a:
		case 0x4b:
		case 0x4c:
		case 0x4d:
		case 0x4e:
		case 0x4f:

			if (LOGEMU) {
				log("logemu", "sub");
			}
			quick_flag = 0;
			if ((byte2 & 0xc0) == 0xc0) {
				if ((byte1 & 0x01) == 1)
					set_info((char) (byte2 & 0xbf));
				else
					set_info((char) (byte2 & 0x7f));
				set_arg1();
				set_arg2_nosize(0, byte1);
				// set_arg2(0,byte1);
				swap_args();
				do_sub(true);
			} else {
				set_info(byte2);
				set_arg1();
				set_arg2(1, byte1);
				if ((byte1 & 0x01) == 0)
					swap_args();
				do_sub(false);

			}
			break;

		/* A0-AF various special commands [LINE_A] */

		case 0x50:
		case 0x56: /* [2521] */
		case 0x57:
			do_line_a();
			if (LOGEMU) {
				log("logemu", "LINE_A A0" + byte2);
			}
			break;

		case 0x51:
			if (LOGEMU) {
				log("logemu", "rts\n");
			}
			pc = pop(); /* RTS */
			break;

		case 0x52:
			if (LOGEMU) {
				log("logemu", "bsr");
			}

			if (byte2 == 0)
				push(pc + 2); /* BSR */
			else
				push(pc);
			branch(byte2);
			break;

		case 0x53:
			if ((byte2 & 0xc0) == 0xc0) { /* TST [321d] */
				ms_fatal("unimplemented instructions LINE_A #$6C0-#$6FF");
			} else {
				if (LOGEMU) {
					log("logemu", "tst");
				}
				set_info(byte2);
				set_arg1();
				cflag = vflag = 0;
				set_flags();
			}
			break;

		case 0x54:
			check_movem();
			break;

		case 0x55:
			check_movem2();
			break;

		/* B0-BF [2fe4] */
		case 0x58:
		case 0x59:
		case 0x5a:
		case 0x5b:
		case 0x5c:
		case 0x5d:
		case 0x5e:
		case 0x5f:

			if ((byte2 & 0xc0) == 0xc0) {
				if (LOGEMU) {
					log("logemu", "cmp");
				}
				if ((byte1 & 0x01) == 1)
					set_info((char) (byte2 & 0xbf));
				else
					set_info((char) (byte2 & 0x7f));
				set_arg1();
				set_arg2(0, byte1);
				swap_args();
				do_cmp(); /* CMP */
			} else {
				if ((byte1 & 0x01) == 0) {
					if (LOGEMU) {
						log("logemu", "cmp");
					}
					set_info(byte2);
					set_arg1();
					set_arg2(1, byte1);
					swap_args();
					do_cmp(); /* CMP */
				} else {
					if (LOGEMU) {
						log("logemu", "eor");
					}
					set_info(byte2);
					set_arg1();
					set_arg2(1, byte1);
					do_eor(); /* EOR */
				}
			}
			break;

		/* C0-CF [2f52] */
		case 0x60:
		case 0x61:
		case 0x62:
		case 0x63:
		case 0x64:
		case 0x65:
		case 0x66:
		case 0x67:

			if ((byte1 & 0x01) == 0) {
				if ((byte2 & 0xc0) == 0xc0) {
					ms_fatal("unimplemented instruction MULU");
				} else { /* AND */
					if (LOGEMU) {
						log("logemu", "and");
					}
					set_info(byte2);
					set_arg1();
					set_arg2(1, byte1);
					if ((byte1 & 0x01) == 0)
						swap_args();
					do_and();
				}
			} else {
				if ((byte2 & 0xf8) == 0x40) {
					if (LOGEMU) {
						log("logemu", "exg (dx)");
					}
					opsize = 2; /* EXG Dx,Dx */
					set_arg2(1, (char) (byte2 << 1));
					swap_args();
					set_arg2(1, byte1);
					tmp32 = read_l(acc1, arg1);
					write_l(acc1, arg1, read_l(acc2, arg2));
					write_l(acc2, arg2, tmp32);
				} else if ((byte2 & 0xf8) == 0x48) {
					if (LOGEMU) {
						log("logemu", "exg (ax)");
					}
					opsize = 2; /* EXG Ax,Ax */
					set_arg2(0, (char) (byte2 << 1));
					swap_args();
					set_arg2(0, byte1);
					tmp32 = read_l(acc1, arg1);
					write_l(acc1, arg1, read_l(acc2, arg2));
					write_l(acc2, arg2, tmp32);
				} else if ((byte2 & 0xf8) == 0x88) {
					if (LOGEMU) {
						log("logemu", "exg (dx,ax)");
					}
					opsize = 2; /* EXG Dx,Ax */
					set_arg2(0, (char) (byte2 << 1));
					swap_args();
					set_arg2(1, byte1);
					tmp32 = read_l(acc1, arg1);
					write_l(acc1, arg1, read_l(acc2, arg2));
					write_l(acc2, arg2, tmp32);
				} else {
					if ((byte2 & 0xc0) == 0xc0) {
						ms_fatal("unimplemented instruction MULS");
					} else {
						set_info(byte2);
						set_arg1();
						set_arg2(1, byte1);
						if ((byte1 & 0x01) == 0)
							swap_args();
						do_and();
					}
				}
			}
			break;

		/* D0-DF [2fc8] ADD */
		case 0x68:
		case 0x69:
		case 0x6a:
		case 0x6b:
		case 0x6c:
		case 0x6d:
		case 0x6e:
		case 0x6f:

			if (LOGEMU) {
				log("logemu", "add");
			}
			quick_flag = 0;
			if ((byte2 & 0xc0) == 0xc0) {
				if ((byte1 & 0x01) == 1)
					set_info((char) (byte2 & 0xbf));
				else
					set_info((char) (byte2 & 0x7f));
				set_arg1();
				set_arg2_nosize(0, byte1);
				// set_arg2(0,byte1);
				swap_args();
				do_add(true);
			} else {
				set_info(byte2);
				set_arg1();
				set_arg2(1, byte1);
				if ((byte1 & 0x01) == 0)
					swap_args();
				do_add(false);
			}
			break;

		/* E0-EF [3479] LSR ASL ROR ROL */
		case 0x70:
		case 0x71:
		case 0x72:
		case 0x73:
		case 0x74:
		case 0x75:
		case 0x76:
		case 0x77:

			if (LOGEMU) {
				log("logemu", "lsr,asl,ror,rol");
			}
			if ((byte2 & 0xc0) == 0xc0) {
				set_info((char) (byte2 & 0xbf)); /* OP Dx */
				set_arg1();
				l1c = 1; /* steps=1 */
				byte2 = (char) ((byte1 >>> 1) & 0x03);
			} else {
				set_info((char) (byte2 & 0xc7));
				set_arg1();
				if ((byte2 & 0x20) == 0) { /* immediate */
					l1c = (char) ((byte1 >>> 1) & 0x07);
					if (l1c == 0)
						l1c = 8;
				} else {
					l1c = (char) read_reg((byte1 >>> 1 & 0x07), 0);
				}
				byte2 = (char) ((byte2 >> 3) & 0x03);
			}
			if ((byte1 & 0x01) == 0) { /* right */
				while (l1c-- > 0) {
					if (opsize == 0) {
						cflag = ((getarg(acc1, arg1) & 0x01) != 0) ? 0xff
								: (char) 0;
						setargval(acc1, arg1, (char) (getarg(acc1, arg1) >>> 1));
						if ((cflag != 0) && (byte2 == 3))
							setargval(acc1, arg1,
									(char) (getarg(acc1, arg1) | 0x80));
					}
					if (opsize == 1) {
						cflag = ((read_w(acc1, arg1) & 0x01) != 0) ? 0xff
								: (char) 0;
						write_w(acc1, arg1, (read_w(acc1, arg1) >>> 1));
						if ((cflag != 0) && (byte2 == 3))
							write_w(acc1, arg1,
									(read_w(acc1, arg1) | (1 << 15)));
					}
					if (opsize == 2) {
						cflag = ((read_l(acc1, arg1) & 0x01) != 0) ? 0xff
								: (char) 0;
						write_l(acc1, arg1, read_l(acc1, arg1) >>> 1);
						if ((cflag != 0) && (byte2 == 3))
							write_l(acc1, arg1, read_l(acc1, arg1)
									| ((int) 1 << 31));
					}
				}
			} else { /* left */
				while (l1c-- > 0) {
					if (opsize == 0) {
						cflag = ((getarg(acc1, arg1) & 0x80) != 0) ? 0xff
								: (char) 0; /* [3527] */
						setargval(acc1, arg1, (char) (getarg(acc1, arg1) << 1));
						if ((cflag != 0) && (byte2 == 3))
							setargval(acc1, arg1,
									(char) (getarg(acc1, arg1) | 0x01));
					}
					if (opsize == 1) {
						cflag = ((read_w(acc1, arg1) & (1 << 15)) != 0) ? 0xff
								: (char) 0;
						write_w(acc1, arg1, (read_w(acc1, arg1) << 1));
						if ((cflag != 0) && (byte2 == 3))
							write_w(acc1, arg1, (read_w(acc1, arg1) | 0x01));
					}
					if (opsize == 2) {
						cflag = ((read_l(acc1, arg1) & ((int) 1 << 31)) != 0) ? 0xff
								: (char) 0;
						write_l(acc1, arg1, read_l(acc1, arg1) << 1);
						if ((cflag != 0) && (byte2 == 3))
							write_l(acc1, arg1, read_l(acc1, arg1) | 0x01);
					}
				}
			}
			set_flags();
			break;

		/* F0-FF [24f3] LINE_F */
		case 0x78:
		case 0x79:
		case 0x7a:
		case 0x7b:
		case 0x7c:
		case 0x7d:
		case 0x7e:
		case 0x7f:

			if (version == 0) { /* hardcoded jump */
				char_out(l1c = (char) read_reg(1, 0));
			} else if (version == 1) { /* single programmable shortcut */
				push(pc);
				pc = fl_sub;
			} else { /* programmable shortcuts from table */
				if (LOGEMU) {
					log("logemu", "LINK: " + byte1 + "," + byte2);
				}
				ptr = ((byte1 & 7) << 8 | byte2);
				if (ptr >= fl_size) {
					if ((byte1 & 8) == 0)
						push(pc);
					ptr = (byte1 << 8 | byte2 | 0x0800);
					ptr = (fl_tab + 2 * (ptr ^ 0xffff));
					pc = ptr + (short) read_w(code, effective(ptr));
				} else {
					push(pc);
					pc = fl_sub;
				}
			}
			break;

		default:
			ms_fatal("Constants aren't and variables don't");
			break;
		}
		return running;
	}

	void setargval(char[] desarr, int off, char val) {
		desarr[off] = val;
	}

	void setargsrc(int argid, char[] newdes, int newcnt) {
		if (argid == 1) {
			acc1 = newdes;
			arg1 = newcnt;
		} else {
			acc2 = newdes;
			arg2 = newcnt;
		}
	}

	/* aux function to read unsigned bytes from file into char array */
	int ReadUbyteFromFile(RandomAccessFile df, char[] des) throws IOException {
		int iCnt, tmp;

		for (iCnt = 0; iCnt < des.length; iCnt++) {
			tmp = df.readUnsignedByte();
			des[iCnt] = (char) tmp;
		}
		return iCnt;
	}

	/* aux function to read count unsigned bytes from file into char array */
	int ReadUbyteFromFile(RandomAccessFile df, char[] des, int Count)
			throws IOException {
		int iCnt, tmp;

		for (iCnt = 0; iCnt < Count; iCnt++) {
			tmp = df.readUnsignedByte();
			des[iCnt] = (char) tmp;
		}
		return iCnt;
	}

	/* Find a terminating \0 in a char array */
	public int findnull(char[] data, int offset) {
		int iLen = 0;
		while (data[offset + iLen] != 0)
			iLen++;
		return iLen;
	}

	// Thread runner

	public void run() {

		final SwingWorker worker = new SwingWorker() {
			int running = 1;

			public Object construct() {
				// ms_waitForKey();
				while (running != 0) {
					try {
						running = ms_rungame();
					} catch (IOException e) {
					}
				}
				try {
					if (log_on != 0)
						log.close();
					if (log2 != null)
						log2.close();
				} catch (IOException e) {
				}
				;
				log2 = null;
				ms_fatal("Exiting.");
				return new Integer(0);
			}
		};

		// worker.start();
		int running = 1;
		// ms_waitForKey();
		while (running != 0) {
			try {
				running = ms_rungame();
			} catch (IOException e) {
			}
		}
		try {
			if (log_on != 0)
				log.close();
			if (log2 != null)
				log2.close();
		} catch (IOException e) {
		}
		;
		log2 = null;
		ms_fatal("Exiting.");
	}

	/*
	 * Thread runner public void run() {
	 * 
	 * ms_waitForKey(); int running=1; while ((running != 0)) { try {
	 * running=ms_rungame(); } catch ( IOException e)
	 * {System.out.println(e.toString());} }
	 * 
	 * 
	 * if (i_count==slimit) { ms_fatal("\n\nSafety limit "+slimit+"
	 * reached.\n"); ms_status(); }
	 * 
	 * try { if (log_on != 0) log.close(); if (log2 != null) log2.close(); }
	 * catch (IOException e ) {}; log2 = null; ms_fatal("Exiting."); }
	 */
}