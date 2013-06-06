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
    
    private final int T = 2500000 / 50;
    
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
    
    private int keypress;

    
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
        keypress = 0;

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

    public void ResetPressed() {
        Reset(false);
    }

    public void saveRam(String name) {
        try {
            HexFile hx = new HexFile(this);
            hx.hexOpen(name);
            hx.hexWrite(0x0600, 0x09ff, 0);
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
    @Override
    public int fetchOpcode(int address) {
        clk.addTstates(4);
        int opcode = mem.readByte(address) & 0xff;
//        System.out.println(String.format("PC: %04X (%02X)", address,opcode));
        return opcode;
    }

    @Override
    public int peek8(int address) {
        clk.addTstates(3);
        int value = mem.readByte(address) & 0xff;
//        if (address==0xffff) {
//        System.out.println(String.format("Peek: %04X,%02X (%04X)", address,value,cpu.getRegPC()));            
//        }
        return value;
    }

    @Override
    public void poke8(int address, int value) {
//        System.out.println(String.format("Poke: %04X,%02X (%04X)", address,value,cpu.getRegPC()));
        clk.addTstates(3);
        mem.writeByte(address, (byte) value);
    }

    @Override
    public int peek16(int address) {
        clk.addTstates(6);
        int lsb = mem.readByte(address) & 0xff;
        address = (address+1) & 0xffff;
        return ((mem.readByte(address) << 8) & 0xff00 | lsb);
    }

    @Override
    public void poke16(int address, int word) {
        clk.addTstates(6);
        mem.writeByte(address, (byte) word);
        address = (address+1) & 0xffff;
        mem.writeByte(address, (byte) (word >>> 8));
    }

    @Override
    public int inPort(int port) {
        clk.addTstates(4);
        port &= 0xff;
//        System.out.println(String.format("In: %02X (%04X)", port,cpu.getRegPC()));
        return 0xff;
    }

    @Override
    public void outPort(int port, int value) {
        clk.addTstates(4);
        port &= 0xff;
        value &= 0xff;
//        System.out.println(String.format("Out: %02X,%02X (%04X)", port,value,cpu.getRegPC()));
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
}