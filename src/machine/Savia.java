/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import gui.SevenDisp;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import utils.HexFile;
import utils.HexMem;
import z80core.MemIoOps;
import z80core.NotifyOps;
import z80core.Z80;

/**
 *
 * @author Administrator
 */
public class Savia extends Thread implements MemIoOps, NotifyOps, HexMem {
    
    private final int T = 2000000 / 50;
    
    private final byte bs0 = 0x01;
    private final byte bs1 = 0x02;
    private final byte bs2 = 0x04;
    private final byte br0 = ~bs0;
    private final byte br1 = ~bs1;
    private final byte br2 = ~bs2;
    
    private Config cfg;
    public  Memory mem;
    private Timer tim;
    private MTimer task;
    public  Clock clk;
    private Z80 cpu;
    
    private boolean paused;
    
    private SevenDisp disp1,disp2,disp3,disp4,disp5,disp6,disp7,disp8;
    private SevenDisp dispA1,dispA2,dispA3,dispA4,dispD1,dispD2;
    private JLabel led1,led2,led3,led4,led5,led6,led7,led8;
    
    private int pa,pb,pc;
    private boolean dispst;
    private byte[] keyb = new byte[9];
    private enum ks {N, P1, P2, P3}
    private ks keyst = ks.N;

    private enum dm {NON, INS, CYC};
    private enum dc {M1, MR, MW, IR, IW};
    private dm dMode = dm.NON;
    private boolean step = false;
    
    public Savia() {
        cfg = new Config();
        mem = new Memory(this, cfg);
        tim = new Timer("Timer");
        clk = new Clock();
        cpu = new Z80(clk, this, this);
        
        paused = true;
        
        Reset(true);
    }
    
    public void setConfig(Config c) {
        if (!cfg.equals(c)) {
            cfg = c;
            Reset(false);
        }
    }
    
    public Config getConfig() {
        return cfg;
    } 
    
    public final void Reset(boolean dirty) {
        mem.Reset(dirty);
        clk.reset();
        cpu.reset();

        keyst = ks.N;   
        keyb[0] = 0x0f;
        keyb[1] = 0x0f;
        keyb[2] = 0x0f;
        keyb[3] = 0x0f;
        keyb[4] = 0x0f;
        keyb[5] = 0x0f;
        keyb[6] = 0x0f;
        keyb[7] = 0x0f;
        keyb[8] = 0x0f;
    }
    
    public void startEmulation() {
        if (!paused)
            return;
        
        paused = false;
        task = new MTimer(this);
        tim.scheduleAtFixedRate(task, 250, 20);
       }
    
    public void stopEmulation() {
        if (paused)
            return;
        
        paused = true;
        task.cancel();
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void ms20() {        
        if (!paused) {

            cpu.execute(clk.getTstates()+T);
            
            switch(keyst) {
                case P1: {
                    keyst = ks.P2;
                }
                case P2: {
                    keyst = ks.P3;
                }
                case P3: {
                    keyst = ks.N;
                    keyb[0] = 0x0f;
                    keyb[1] = 0x0f;
                    keyb[2] = 0x0f;
                    keyb[3] = 0x0f;
                    keyb[4] = 0x0f;
                    keyb[5] = 0x0f;
                    keyb[6] = 0x0f;
                    keyb[7] = 0x0f;
                    keyb[8] = 0x0f;
                }                    
            } // switch
        }  
    }
            
    @Override
    public void run() {
        startEmulation();
        
        boolean forever = true;
        while(forever) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
                Logger.getLogger(Savia.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }    
    
    @Override
    public int ReadByte(int address) {
        int value = mem.readByte(address) & 0xff;
        return value;
    }

    @Override
    public void WriteByte(int address, int value) {
        mem.writeByte(address, (byte) value);
    }

    public void setDisp1(SevenDisp disp) {
        disp1 = disp;
    }

    public void setDisp2(SevenDisp disp) {
        disp2 = disp;
    }

    public void setDisp3(SevenDisp disp) {
        disp3 = disp;
    }

    public void setDisp4(SevenDisp disp) {
        disp4 = disp;
    }

    public void setDisp5(SevenDisp disp) {
        disp5 = disp;
    }

    public void setDisp6(SevenDisp disp) {
        disp6 = disp;
    }

    public void setDisp7(SevenDisp disp) {
        disp7 = disp;
    }

    public void setDisp8(SevenDisp disp) {
        disp8 = disp;
    }

    public void setDispA1(SevenDisp disp) {
        dispA1 = disp;
    }

    public void setDispA2(SevenDisp disp) {
        dispA2 = disp;
    }

    public void setDispA3(SevenDisp disp) {
        dispA3 = disp;
    }

    public void setDispA4(SevenDisp disp) {
        dispA4 = disp;
    }

    public void setDispD1(SevenDisp disp) {
        dispD1 = disp;
    }

    public void setDispD2(SevenDisp disp) {
        dispD2 = disp;
    }

    public void setLed1(JLabel led) {
        led1 = led;
    }

    public void setLed2(JLabel led) {
        led2 = led;
    }

    public void setLed3(JLabel led) {
        led3 = led;
    }

    public void setLed4(JLabel led) {
        led4 = led;
    }

    public void setLed5(JLabel led) {
        led5 = led;
    }

    public void setLed6(JLabel led) {
        led6 = led;
    }

    public void setLed7(JLabel led) {
        led7 = led;
    }

    public void setLed8(JLabel led) {
        led8 = led;
    }

    public void resetPressed() {
        Reset(false);
    }
    
    public void keyPressed(int keycode) {
        switch(keycode) {
            case 0x00: {
                keyb[0] &= br2; 
                break;
            }
            case 0x01: {
                keyb[8] &= br2;                 
                break;
            }
            case 0x02: {
               keyb[0] &= br1; 
                break;
            }
            case 0x03: {
                keyb[8] &= br1; 
                break;
            }
            case 0x04: {
                keyb[1] &= br2; 
                break;
            }
            case 0x05: {
                keyb[7] &= br2;
                break;
            }
            case 0x06: {
                keyb[1] &= br1;
                break;
            }
            case 0x07: {
                keyb[7] &= br1;
                break;
            }
            case 0x08: {
                keyb[2] &= br2;
                break;
            }
            case 0x09: {
                keyb[6] &= br2;
                break;
            }
            case 0x0a: {
                keyb[2] &= br1;
                break;
            }
            case 0x0b: {
                keyb[6] &= br1;
                break;
            }
            case 0x0c: {
                keyb[5] &= br2;
                break;
            }
            case 0x0d: {
                keyb[4] &= br2;
                break;
            }
            case 0x0e: {
                keyb[5] &= br1;
                break;
            }
            case 0x0f: {
                keyb[4] &= br1;
                break;
            }
            case 0x10: {
                keyb[4] &= br0; // BR
                break;
            }
            case 0x20: {
                keyb[1] &= br0; // S
                break;
            }
            case 0x30: {
                keyb[0] &= br0; // L
                break;
            }
            case 0x40: {
                keyb[3] &= br2; // EX
                break;
            }
            case 0x21: {
                keyb[3] &= br1; // R
                break;
            }
            case 0x31: {
                keyb[5] &= br0; // AD
                break;
            }
            case 0x41: {
                keyb[8] &= br0; // DA
                break;
            }
        } // switch
    } 

    public void saveRam(String name) {
        try {
            HexFile hx = new HexFile(this);
            hx.hexOpen(name);
            hx.hexWrite(0x1800, 0x1fff, 0);
            hx.hexClose(0);
        } catch (IOException ex) {
            Logger.getLogger(Savia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRam(String name) {
        try {
            HexFile hx = new HexFile(this);
            hx.hexRead(name, 0);
        } catch (IOException ex) {
            Logger.getLogger(Savia.class.getName()).log(Level.SEVERE, null, ex);
        }
        cpu.setRegPC(0);
    }
    
    public void setDebugMode(char mode) { 
        switch((byte) mode) {
            case 'O': {
                dMode = dm.NON;
                break;
            }
            case 'I': {
                dMode = dm.INS;
                break;
            }
            case 'C': {
                dMode = dm.CYC;
                break;
            }
        } // switch
    }

    public void stepPressed() {
        step = true;
    }

    @Override
    public int fetchOpcode(int address) {
        clk.addTstates(4);
        int opcode = mem.readByte(address) & 0xff;
        if (dMode != dm.NON) { debug(address, opcode, dc.M1); }
//        System.out.println(String.format("PC: %04X (%02X)", address,opcode));
        return opcode;
    }

    @Override
    public int peek8(int address) {
        clk.addTstates(3);
        int value = mem.readByte(address) & 0xff;
//        System.out.println(String.format("Peek: %04X,%02X (%04X)", address,value,cpu.getRegPC()));            
        if (dMode == dm.CYC) { debug(address, value, dc.MR); }
        return value;
    }

    @Override
    public void poke8(int address, int value) {
//        System.out.println(String.format("Poke: %04X,%02X (%04X)", address,value,cpu.getRegPC()));
        clk.addTstates(3);
        mem.writeByte(address, (byte) value);
        if (dMode == dm.CYC) { debug(address, value, dc.MW); }
    }

    @Override
    public int peek16(int address) {
        clk.addTstates(6);
        int lsb = mem.readByte(address) & 0xff;
        if (dMode == dm.CYC) { debug(address, lsb, dc.MR); }
        address = (address+1) & 0xffff;
        int msb = mem.readByte(address) & 0xff;
        if (dMode == dm.CYC) { debug(address, msb, dc.MR); }
        return ((msb << 8) & 0xff00 | lsb);
    }

    @Override
    public void poke16(int address, int word) {
        clk.addTstates(6);
        mem.writeByte(address, (byte) word);
        if (dMode == dm.CYC) { debug(address, (word & 0xff), dc.MW); }
        address = (address+1) & 0xffff;
        mem.writeByte(address, (byte) (word >>> 8));
        if (dMode == dm.CYC) { debug(address, (word >>> 8), dc.MR); }
    }

    @Override
    public int inPort(int port) {
        clk.addTstates(4);
        port &= 0xff;
        int value = 0xff;
        if ((port & 0x04) == 0) {   // A2 = 0
            if ( ((port & 0x03) == 2) && (pc<9) ) {   // PC
                value = (keyb[pc & 0x0f] << 4);
                keyb[pc & 0x0f] = 0x0f;
            } // PC 
        } // A2 = 0;
//        System.out.println(String.format("In: %02X (%04X)", port,cpu.getRegPC()));
        if (dMode == dm.CYC) { debug(port, value, dc.IR); }
        return value;
    }

    @Override
    public void outPort(int port, int value) {
        clk.addTstates(4);
        port &= 0xff;
        value &= 0xff;
//        System.out.println(String.format("Out: %02X,%02X (%04X)", port,value,cpu.getRegPC()));
        if (dMode == dm.CYC) { debug(port, value, dc.IW); }
        if ((port & 0x04) == 0) {   // A2 = 0
            switch(port & 0x03) {   // 8255 ports
                case 0: {   // PA
                    pa = value;
                    if (dispst) { disp(); }
                    dispst = false;
                    break;
                }
                case 1: {   // PB
                    pb = value;
                    led1.setEnabled((value & 0x80) != 0);
                    led2.setEnabled((value & 0x40) != 0);
                    led3.setEnabled((value & 0x20) != 0);
                    led4.setEnabled((value & 0x10) != 0);
                    led5.setEnabled((value & 0x08) != 0);
                    led6.setEnabled((value & 0x04) != 0);
                    led7.setEnabled((value & 0x02) != 0);
                    led8.setEnabled((value & 0x01) != 0);
                    break;
                }
                case 2: {   // PC
                    pc = value;
                    dispst = true;
                    break;
                }
            } // switch
        } // A2 = 0
    }

    @Override
    public void contendedStates(int address, int tstates) {
        clk.addTstates(tstates);
    }

    @Override
    public int atAddress(int address, int opcode) {
//        System.out.println(String.format("bp: %04X,%02X", address,opcode));
//        System.out.println(String.format("HL: %04X DE: %04X", cpu.getRegHL(),cpu.getRegDE()));
        return opcode;
    }

    @Override
    public void execDone() {
    
    }
    
    private void disp() {
        switch(pc & 0x0f) {
            case 0: {
                disp1.setSegments(0x7f & ~pa);
                break;
            }
            case 2: {
                disp2.setSegments(0x7f & ~pa);
                break;
            }
            case 3: {
                disp3.setSegments(0x7f & ~pa);
                break;
            }
            case 4: {
                disp4.setSegments(0x7f & ~pa);
                break;
            }
            case 5: {
                disp5.setSegments(0x7f & ~pa);
                break;
            }
            case 6: {
                disp6.setSegments(0x7f & ~pa);
                break;
            }
            case 7: {
                disp7.setSegments(0x7f & ~pa);
                break;
            }
            case 8: {
                disp8.setSegments(0x7f & ~pa);
                break;
            }
        } //swicth
    }
    
    private void debug(int a, int d, dc c) {
        dispA1.Disp((a >>> 12) & 0x0f);
        dispA2.Disp((a >>>  8) & 0x0f);
        dispA3.Disp((a >>>  4) & 0x0f);
        dispA4.Disp( a         & 0x0f);
        dispD1.Disp((a >>>  4) & 0x0f);
        dispD2.Disp( a         & 0x0f);
        
        if (dMode == dm.CYC) {
            switch(c) {
                case M1: {
                    dispA1.setDP(true);
                    dispA2.setDP(false);
                    dispA3.setDP(false);
                    dispD1.setDP(true);
                    dispD2.setDP(false);
                    break;
                }
                case MR: {
                    dispA1.setDP(false);
                    dispA2.setDP(true);
                    dispA3.setDP(false);
                    dispD1.setDP(true);
                    dispD2.setDP(false);                
                    break;
                }
                case MW: {
                    dispA1.setDP(false);
                    dispA2.setDP(true);
                    dispA3.setDP(false);
                    dispD1.setDP(false);
                    dispD2.setDP(true);
                    break;
                }
                case IR: {
                    dispA1.setDP(false);
                    dispA2.setDP(false);
                    dispA3.setDP(true);
                    dispD1.setDP(true);
                    dispD2.setDP(false);
                    break;
                }
                case IW: {
                    dispA1.setDP(false);
                    dispA2.setDP(false);
                    dispA3.setDP(true);
                    dispD1.setDP(false);
                    dispD2.setDP(true);
                    break;
                }
            } // switch
        }
        
        step = false;
        while(!step) {
            
        }
    }
}