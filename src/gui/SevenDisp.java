/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class SevenDisp extends JPanel {

    public final int MIN_WIDTH  = 16;
    public final int MIN_HEIGHT = 20;
    
    private final int polx[][] = {
      { 1, 2, 8, 9, 8, 2},    //Segment 0
      { 9,10,10, 9, 8, 8},    //Segment 1
      { 9,10,10, 9, 8, 8},    //Segment 2
      { 1, 2, 8, 9, 8, 2},    //Segment 3
      { 1, 2, 2, 1, 0, 0},    //Segment 4
      { 1, 2, 2, 1, 0, 0},    //Segment 5
      { 1, 2, 8, 9, 8, 2},    //Segment 6
   };
   private final int poly[][] = {
      { 1, 0, 0, 1, 2, 2},    //Segment 0
      { 1, 2, 8, 9, 8, 2},    //Segment 1
      { 9,10,16,17,16,10},    //Segment 2
      {17,16,16,17,18,18},    //Segment 3
      { 9,10,16,17,16,10},    //Segment 4
      { 1, 2, 8, 9, 8, 2},    //Segment 5
      { 9, 8, 8, 9,10,10},    //Segment 6
   };
    private final byte decode[] =  {0x3F, 0x06, 0x5B, 0x4F,
                                     0x66, 0x6D, 0x7D, 0x07,
                                     0x7F, 0x6F, 0x77, 0x7C,
                                     0x39, 0x5E, 0x79, 0x71};
    private byte seg = 0;
    private boolean dec = true;      // is decimal point visible?
    private boolean left = false;    // is decimal point at left?
    private boolean opaque = false;  // is opaque?
    private Color on  = Color.RED;
    private Color off = Color.DARK_GRAY;
    private byte map[];
    
    public SevenDisp() {
        super();
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setPreferredSize(new Dimension(MIN_WIDTH*5, MIN_HEIGHT*5));
        setSize(getPreferredSize());
        setBackground(Color.BLACK);
        map = new byte[8];
        for(byte i=0; i<8; i++)
            map[i] = i;
    }
    
    public void setOpacity(boolean opac) {
        opaque = opac;
        repaint();
    }
    
    public void setOnColor(Color c) {
        on = c;
        repaint();
    }
    
    public void setOffColor(Color c) {
        off = c;
        repaint();
    }
    
    public void setPointVisible(boolean visible) {
        dec = visible;
        repaint();
    }
    
    public void setPointLeft(boolean isleft) {
        left = isleft;
        repaint();
    }

    public void setSegmentMap(byte sgmp[]) {
        map = sgmp;
    } 
    
    public void setSegments(int segments) {
        int mask = 0x01;
        seg = 0;
        for(byte n=0; n<8; n++) {
            if ((segments & mask) != 0) {
                seg |= (1 << map[n]);
            }
            mask <<= 1;
        }
        repaint();
    }
    
    public void Clear() {
        seg = 0;
        repaint();
    }
    
    public void Disp(int x) {
        if (x>0xf) return;
        seg &= 0x80; 
        seg |= decode[x];
        repaint();
    }
    
    public void setDP(boolean state) {
        if (state)
            seg |= 0x80;
        else
            seg &= 0x7f;
        repaint();
    }
    
    @Override
    public void paint(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;

        if(!opaque) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        int dx = getWidth() / 16;
        int dy = getHeight() / 20;

	for(int i=0,m=1; i<7; i++,m<<=1) {
            Polygon p = new Polygon();
            g.setColor(((seg & m)!=0) ? on : off);
            for(int j=0; j<6; j++) {
                p.addPoint(dx*polx[i][j]+(3*dx), dy*poly[i][j]+dy);
            }
            g.fillPolygon(p);
        } 	 
	if (dec) {
            g.setColor(((seg & 0x80)!=0) ? on : off);  // DP
            if (left)
                g.fillOval(dx, dy*17+(dy/2), dx*3/2, dy*3/2);
            else
                g.fillOval(dx*14-(dx/2), dy*17+(dy/2), dx*3/2, dy*3/2);
        }
    }    
}
