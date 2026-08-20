/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iflegends.msmemorial.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * @author YvesFabienne
 */
public class ImageDoubler {

    public static BufferedImage doubleImage(BufferedImage src) {

        int nx = src.getWidth();
        int ny = src.getHeight();

        int[] src_array = ((DataBufferInt)src.getRaster().getDataBuffer()).getData();

        int new_nx = nx*2-1;
        int new_ny = ny*2-1;

        BufferedImage dest = new BufferedImage(new_nx, new_ny, BufferedImage.TYPE_INT_ARGB);

        int[] dest_array = ((DataBufferInt)dest.getRaster().getDataBuffer()).getData();

        //Recopie des points d'origine
            for ( int y=0; y < ny; y++) {
                for ( int x=0; x< nx; x++) {
                //dest.setRGB(2*x, 2*y, src.getRGB(x, y));
                dest_array[2*x + 2*y*new_nx] = src_array[x+ y*nx];
            }
        }

        //Interpolation en x
            for ( int y=0; y < ny; y++) {
        for ( int x=0; x< nx-1; x++) {
                int col0 = src_array[Math.max(0,x-2)+ y*nx];
                int col1 = src_array[Math.max(0,x-1)+ y*nx];
                int col2 = src_array[x+ y*nx];
                int col3 = src_array[Math.min(nx-1,x+1)+ y*nx];
                int col4 = src_array[Math.min(nx-1,x+2)+ y*nx];
                int col5 = src_array[Math.min(nx-1,x+3)+ y*nx];
               int red = toScreen(
                       0.65*( toLinear(getRed(col2))+toLinear(getRed(col3)))
                       -0.21 *( toLinear(getRed(col1))+toLinear(getRed(col4)))
                       + 0.06*( toLinear(getRed(col0))+toLinear(getRed(col5))));
               int green = toScreen(
                       0.65*( toLinear(getGreen(col2))+toLinear(getGreen(col3)))
                       -0.21*( toLinear(getGreen(col1))+toLinear(getGreen(col4)))
                       +0.06*( toLinear(getGreen(col0))+toLinear(getGreen(col5))));
               int blue = toScreen(
                       0.65*( toLinear(getBlue(col2))+toLinear(getBlue(col3)))
                       -0.21*( toLinear(getBlue(col1))+toLinear(getBlue(col4)))
                       +0.06*( toLinear(getBlue(col0))+toLinear(getBlue(col5))));
               red = Math.min(red,255); red = Math.max(0,red);
               green = Math.min(green,255); green = Math.max(green,0);
               blue = Math.min(blue,255); blue = Math.max(0,blue);
                Color res = new Color(red,green,blue);
                dest_array[2*x+1+ new_nx*2*y] = res.getRGB();
            }
        }

            for ( int y=0; y < ny-1; y++) {
        for ( int x=0; x< 2*nx-1; x++) {
                int col0 = dest_array[x + new_nx * Math.max(0,2*(y-2))];
                int col1 = dest_array[x + new_nx * Math.max(0,2*(y-1))];
                int col2 = dest_array[x + new_nx * 2*y];
                int col3 = dest_array[x + new_nx * Math.min(2*(ny-1),2*(y+1))];
                int col4 = dest_array[x + new_nx * Math.min(2*(ny-1),2*(y+2))];
                int col5 = dest_array[x + new_nx * Math.min(2*(ny-1),2*(y+3))];
               int red = toScreen(
                       0.65*( toLinear(getRed(col2))+toLinear(getRed(col3)))
                       -0.21 *( toLinear(getRed(col1))+toLinear(getRed(col4)))
                       + 0.06*( toLinear(getRed(col0))+toLinear(getRed(col5))));
               int green = toScreen(
                       0.65*( toLinear(getGreen(col2))+toLinear(getGreen(col3)))
                       -0.21*( toLinear(getGreen(col1))+toLinear(getGreen(col4)))
                       +0.06*( toLinear(getGreen(col0))+toLinear(getGreen(col5))));
               int blue = toScreen(
                       0.65*( toLinear(getBlue(col2))+toLinear(getBlue(col3)))
                       -0.21*( toLinear(getBlue(col1))+toLinear(getBlue(col4)))
                       +0.06*( toLinear(getBlue(col0))+toLinear(getBlue(col5))));
               red = Math.min(red,255); red = Math.max(0,red);
               green = Math.min(green,255); green = Math.max(green,0);
               blue = Math.min(blue,255); blue = Math.max(0,blue);
                Color res = new Color(red,green,blue);
                dest_array[x+new_nx*( 2*y+1)]= res.getRGB();
            }
        }

        return dest;
    }
    
    private static double toLinear(int lum) {
    	return Math.pow(lum/255d, 2.0);
    }
    
    private static int toScreen(double lum) {
    	return (int)Math.round(Math.pow(lum, 0.5) * 255);
    }

    /**
     * Returns the red component in the range 0-255 in the default sRGB
     * space.
     * @return the red component.
     * @see #getRGB
     */
    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    /**
     * Returns the green component in the range 0-255 in the default sRGB
     * space.
     * @return the green component.
     * @see #getRGB
     */
    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    /**
     * Returns the blue component in the range 0-255 in the default sRGB
     * space.
     * @return the blue component.
     * @see #getRGB
     */
    public static int getBlue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }
}
