/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

/**
 *
 * @author Administrator
 */
public interface HexMem {
    int ReadByte(int address);
    void WriteByte(int address, int value);
}
