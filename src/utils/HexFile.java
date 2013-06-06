package utils;

import java.io.*;

/*
Intel Hex File Format. this description is taken from www.8052.com.
Pos Description
1   Record Marker: 
    The first character of the line is always a colon (ASCII 0x3A) 
    to identify the line as an Intel HEX file 

2-3 Record Length: 
    This field contains the number of data bytes in the register 
    represented as a 2-digit hexidecimal number. This is the 
    total number of data bytes, not including the checksum byte 
    nor the first 9 characters of the line.   

4-7 Address: 
    This field contains the address where the data should be 
    loaded into the chip. This is a value from 0 to 65,535 
    represented as a 4-digit hexidecimal value. 

8-9 Record Type: 
    This field indicates the type of record for this line. 
    The possible values are: 
    00=Register contains normal data. 
    01=End of File. 
    02=Extended address. 

10-? Data Bytes: 
    The following bytes are the actual data that 
    will be burned into the EPROM. The data is represented as 
    2-digit hexidecimal values. 

Last 2 Checksum: 
     The last two characters of the line are a checksum for the 
     line. The checksum value is calculated by taking the two's 
     complement of the sum of all the preceeding data bytes, 
     excluding the checksum byte itself and the colon at the
     beginning of the line.
*/


public class HexFile {
    public static final int RADIX=16;
    public static final int LINE_LENGTH=16; 
    private HexMem mem;
    private File f;
    private BufferedWriter bw = null;
    
    public HexFile(HexMem memory) {
        mem = memory;
    } 
    
    public void hexRead(String name, int offs) 
            throws FileNotFoundException, IOException {
  
        File f = new File(name);
        if (f == null) throw new NullPointerException("File is null");
        BufferedReader br = new BufferedReader(new FileReader(f));
   
   	String rec;
	int recNum = 0;
        int recLen;
        byte dataLen;
        byte recType;
        byte hexVal;
        int  address;
        byte temp;
        int i,a;
        int sum,k;
        byte checksum, compChecksum;
        
	while ((rec = br.readLine()) != null) {
	    recNum++;
	    recLen = rec.length();
	    dataLen = (byte) (Integer.parseInt(rec.substring(1,3), RADIX));
	    address = (int) (Integer.parseInt(rec.substring(3, 7), RADIX));
	    recType = (byte) (Integer.parseInt(rec.substring(7,9), RADIX));

	    //Extract the code bytes.

	    temp = (byte) (9 + (2 * dataLen));
	    for (i = 9, a = address+offs; i < temp; i = i + 2, a++) {
		a &= 0xffff;

                if (recType == 0) {
                    mem.WriteByte(a, (Integer.parseInt(rec.substring(i, (i + 2)), RADIX)));
                }
	    }

	    //Compute the sum of hexvalues
	    k = 0;
	    sum = 0;
	    for (i = 1; i < (recLen - 2); i = i + 2) {
		hexVal = (byte) (Integer.parseInt(rec.substring(i, (i + 2)), RADIX));
		sum += hexVal;
		k++;
	    }
	    
	    checksum = (byte) (Integer.parseInt(rec.substring((recLen - 2), 
								 recLen), RADIX));
            sum += checksum;
            compChecksum =  (byte) ((sum % 256) & 0xFF);

	    if (compChecksum != 0) 
		throw new IllegalArgumentException("invalid checksum in record " + recNum);
        }
    }

    public void hexOpen(String name) throws IOException {
        f = new File(name);
        if (f == null) throw new NullPointerException("File is null");
        bw = new BufferedWriter(new FileWriter(f));
    }
    
    public void hexWrite(int from, int to, int offs) 
            throws IOException {
        int a = (from + offs) & 0xffff;
        int sum = 0; 
        int num = 0;
        int len = 0;
        
        for(int n=from; n<=to; n++, a++) {
            if (num == 0) {
                sum = ((a>>>8) & 0xff) + (a & 0xff);
                len = (to-n)+1;
                if (len>LINE_LENGTH) { len = LINE_LENGTH; }
                sum += len;
                bw.write(String.format(":%02X%04X00", len, a));
            }
            bw.write(String.format("%02X", 0xff & mem.ReadByte(a)));
            sum += mem.ReadByte(a) & 0xff;
            num++;
            if ((num==LINE_LENGTH) || (n==to) ) {
                num = 0;
                sum = (sum % 256) & 0xff;
                bw.write(String.format("%02X", (0-sum) & 0xff));
                bw.newLine();
            }
        }
    }

    public void hexClose(int starta) throws IOException {
        int sum = ((starta>>>8) & 0xff) + (starta & 0xff) + 1; 
            sum = (sum % 256) & 0xff;
            bw.write(String.format(":00%04X01%02X", starta, (0-sum)  & 0xff));
            bw.newLine();
            bw.close();
            bw = null;
    }
}
