package org.iflegends.msmemorial.magnetic;

import org.iflegends.msmemorial.util.UType8;

public class MythPassGen 
{

	private UType8 state = new UType8(0);

	private UType8 obfuscate(UType8 c)
	{
		int i;

		if (c.eq(0)) state.set(0);
 		else {
			state.xorset(c);
			for (i=0;i<13;i++) {
				//if ((state & 1) ^ ((state>>1) & 1)) state|=0x80;
				UType8 x = UType8.xor(state.and(1), UType8.and(state.rsh(1), 1));
				if ( !(x.eq(0)))
						state.orset(0x80);
				else state.andset(0x7f);
				state.rshset(1);
			}
		}
		return new UType8(state);
	}
	
	public String genPassword(String name)
	{
		UType8 tmp;
		StringBuffer result = new StringBuffer();
		String pad="MAGNETICSCR";
		int i,j;
		
		name = name.toUpperCase();

		if (name.endsWith("#") || name.endsWith("]"))
			name = name.substring(0, name.length()-1);

		if (name.length()<12)
		{
          name = name +  pad.substring(0, 12-name.length());
		}

		obfuscate(new UType8(0));
		i=j=0;
		while (i < name.length())
		{
			tmp=obfuscate(new UType8(name.charAt(i++)));
			if (i < name.length()) 
				tmp.addset(obfuscate(new UType8(name.charAt(i++))));
			tmp.andset(0x1f);
			if (tmp.lt(26)) tmp.addset(65);
			else tmp.addset(0x16);
			result.append(tmp.charValue());
		}
		
		return result.toString();
	}

	public static void main(String[] args)
	{

        String result;
		
		if (args.length!=1)
		{
			System.out.println("Usage: MythPassGen string\n");
			System.exit(1);
		}
		
		MythPassGen mpg = new MythPassGen();
		result = mpg.genPassword(args[0]);


		System.out.println("The password for "+args[0]+" is: "+result+"\n");
	}
 }
