package com.virtual_machine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Register {

    //class varaibles
    private final short[] generalPurposeRegisters;
    private final char[] specialPurposeRegisters;

    public Register() throws FileNotFoundException, IOException {
        this.specialPurposeRegisters = new char[16];                   //16 special purpose registors of 16-bits
        this.generalPurposeRegisters = new short[16];                  //16 general purpose registers of 16-bits
        specialPurposeRegisters[0] = 0;                                //1st register of special purpose is kept constant to 0
    }

    //function to get value of specific general purpose register
    short getGeneralPurposeRegister(int index) {
        return generalPurposeRegisters[index];
    }

    //function to get value of specific special purpose register
    char getSpecialPurposeRegister(int index) {
        return specialPurposeRegisters[index];
    }

    //function to set value of specific general purpose register 
    void setGeneralPurposeRegister(short val, int index) {
        generalPurposeRegisters[index] = val;
    } //end of function

    //function to set value of specific special purpose register 
    void setSpecialPurposeRegister(short val, int index) {
        specialPurposeRegisters[index] = (char) val;
    } //end of function

    //function to show all general purpose registers value in HEX
    void showGeneralPurposeRegisters() {
        System.out.println("**************** GENERAL PURPOSE REGISTERS ********************************");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < 16; i++) {
            System.out.printf("Reg[" + i + "]= %04x" + ",", generalPurposeRegisters[i]);
        } //end of loop

        System.out.println();
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    } //end of function

    //function to show all special purpose registers value in HEX
    void showSpecialPurposeRegisters() {
        System.out.println("**************** SPECIAL PURPOSE REGISTERS ********************************");
        System.out.println("Register[0]: " + Integer.toHexString(specialPurposeRegisters[0]));
        System.out.println("Code Base: " + Integer.toHexString(specialPurposeRegisters[1]));
        System.out.println("Code Limit: " + Integer.toHexString(specialPurposeRegisters[2]));
        System.out.println("Code Counter(PC): " + Integer.toHexString(specialPurposeRegisters[3]));
        System.out.println("Stack Base: " + Integer.toHexString(specialPurposeRegisters[4]));
        System.out.println("Stack Limit: " + Integer.toHexString(specialPurposeRegisters[5]));
        System.out.println("Stack Counter: " + Integer.toHexString(specialPurposeRegisters[6]));
        System.out.println("Data Base: " + Integer.toHexString(specialPurposeRegisters[7]));
        System.out.println("Data Limit: " + Integer.toHexString(specialPurposeRegisters[8]));
        boolean flag = getCarryFlag();                      //get value of carry flag

        if (flag) {
            System.out.println("CF: 1");
        } else {
            System.out.println("CF: 0");
        }
        flag = getZeroFlag();                              //get value of zero flag

        if (flag) {
            System.out.println("ZF: 1");
        } else {
            System.out.println("ZF: 0");
        }

        flag = getSignFlag();                              //get value of sign flag

        if (flag) {
            System.out.println("SF: 1");
        } else {
            System.out.println("SF: 0");
        }

        flag = getOverflowFlag();                              //get value of overflow flag

        if (flag) {
            System.out.println("OF: 1");
        } else {
            System.out.println("OF: 0");
        }
    } //end of function

    //function to set code base
    void setCodeBase(int val) {
        specialPurposeRegisters[1] = (char) val;
    } //end of function

    //function to get code base
    int getCodeBase() {
        return (int) specialPurposeRegisters[1];
    } //end of function

    //function to set code limit
    void setCodeLimit(int val) {
        specialPurposeRegisters[2] = (char) val;
    } //end of function

    //function to get code limit 
    int getCodeLimit() {
        return (int) specialPurposeRegisters[2];
    }//end of function

    //function to set Program counter
    void setProgramCounter(int val) {
        specialPurposeRegisters[3] = (char) val;
    }//end of function

    //function to get value of program counter
    int getProgramCounter() {
        return (int) specialPurposeRegisters[3];
    }//end of function

    //function to increment PC by 1
    void incrementProgramCounter() {
        specialPurposeRegisters[3] = (char) (specialPurposeRegisters[3] + 1);
    }//end of function

    //function to set stack base
    void setStackBase(int val) {
        specialPurposeRegisters[4] = (char) val;
    }//end of function

    //function to get stack base
    int getStackBase() {
        return (int) specialPurposeRegisters[4];
    }//end of function

    //function to set stack limit
    void setStackLimit(int val) {
        specialPurposeRegisters[5] = (char) val;
    }//end of function

    //function to get stack limit
    int getStackLimit() {
        return (int) specialPurposeRegisters[5];
    }//end of function

    //function to set stack counter
    void setStackCounter(int val) {
        specialPurposeRegisters[6] = (char) val;
    }//end of function

    //function to get stack counter
    int getStackCounter() {
        return (int) specialPurposeRegisters[6];
    }//end of function

    //function to set data base
    void setDataBase(int val) {
        specialPurposeRegisters[7] = (char) val;
    }//end of function

    //funtion to get data base
    int getDataBase() {
        return (int) specialPurposeRegisters[7];
    }//end of function

    //function to set data limit
    void setDataLimit(int val) {
        specialPurposeRegisters[8] = (char) val;
    }//end of function

    //function to get data limit
    int getDataLimit() {
        return (int) specialPurposeRegisters[8];
    }//end of function

    //function to get value of carry flag
    boolean getCarryFlag() {
        String cf = Integer.toBinaryString(0x10000 | specialPurposeRegisters[9]).substring(1);                  //convert value to binary
        char cf1 = cf.charAt(15);                                                            //get value of 0th bit
        boolean chk = false;

        if (cf1 == '1') {
            chk = true;
        }

        return chk;
    }//end of function

    //function to get value of zero flag
    boolean getZeroFlag() {
        String zf = Integer.toBinaryString(0x10000 | specialPurposeRegisters[9]).substring(1);               //convert value to binary
        char zf1 = zf.charAt(14);                                                         //get value of 1st bit
        boolean chk = false;

        if (zf1 == '1') {
            chk = true;
        }

        return chk;
    }//end of function

    //function to get value of sign flag
    boolean getSignFlag() {
        String sf = Integer.toBinaryString(0x10000 | specialPurposeRegisters[9]).substring(1);                   //convert value to binary
        char sf1 = sf.charAt(13);                                                             //get value of 2nd bit
        boolean chk = false;

        if (sf1 == '1') {
            chk = true;
        }

        return chk;
    }//end of function

    //function to get value of overflow flag
    boolean getOverflowFlag() {
        String of = Integer.toBinaryString(0x10000 | specialPurposeRegisters[9]).substring(1);
        char of1 = of.charAt(12);
        boolean chk = false;

        if (of1 == '1') {
            chk = true;
        }

        return chk;
    }//end of function

    //function to set/clear carry flag
    void updateCarryFlag(short x) {
        String of = Integer.toBinaryString(0x10000 | x).substring(1);                       //convert value to binary
        char of1 = of.charAt(0);
        if (of1 == '1') {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] | (1)));//set carry flag
        } else {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] & ~(1))); //clear carry flag
        }
    }//end of function

    //function to set/clear zero flag
    void updateZeroFlag(short x) {
        if (x == 0) {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] | (1 << 1))); //set zero flag
        } else {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] & ~(1 << 1))); //clear zero flag
        }
    }//end of function

    //function to set/clear sign flag
    void updateSignFlag(short x) {
        if (x < 0) {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] | (1 << 2))); //set sign flag
        } else {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] & ~(1 << 2))); //clear sign flag
        }
    }//end of function

    //function to set/reset overflow flag
    void updateOverflowFlag(short R1, short R2, short ans) {
        if ((R1 < 0) && (R2 < 0) && (ans >= 0)) {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] | (1 << 3))); //set overflow flag
        } else if ((R1 >= 0) && (R2 >= 0) && (ans < 0)) {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] | (1 << 3))); //set overflow flag
        } else {
            specialPurposeRegisters[9] = (char) ((specialPurposeRegisters[9] & ~(1 << 3))); //clear overflow flag
        }
    }//end of function*/

    void setPCB(int P) {
        specialPurposeRegisters[10] = (char) P;
    }

    int getPCB() {
        return (int) specialPurposeRegisters[10];
    }

    void setTmpReg1(int val) {
        specialPurposeRegisters[11] = (char) val;
    }

    int getTmpReg1() {
        return (int) specialPurposeRegisters[11];
    }

    void setTmpReg2(int val) {
        specialPurposeRegisters[12] = (char) val;
    }

    int getTmpReg2() {
        return (int) specialPurposeRegisters[12];
    }

    // setting index
    void setIndex(int val) {
        specialPurposeRegisters[0] = (char) val;
    }

    // fetching index
    int getIndex() {
        return (int) specialPurposeRegisters[0];
    }

    // incrementing index
    void incrementIndex() {
        specialPurposeRegisters[0] = (char) (specialPurposeRegisters[0] + 1);
    }

    void incrementKmCounter() {
        specialPurposeRegisters[13] = (char) (specialPurposeRegisters[13] + 1);
    }

    void decrementKmCounter() {
        specialPurposeRegisters[13] = (char) (specialPurposeRegisters[13] - 1);
    }

    short getKmCounter() {
        return (short) specialPurposeRegisters[13];
    }

    void incrementMmCounter() {
        specialPurposeRegisters[14] = (char) (specialPurposeRegisters[14] + 1);
    }

    void decrementMmCounter() {
        specialPurposeRegisters[14] = (char) (specialPurposeRegisters[14] - 1);
    }

    short getMmCounter() {
        return (short) specialPurposeRegisters[14];
    }

    void incrementQ1Counter() {
        //  specialPurposeRegisters[15] = (char) (specialPurposeRegisters[15] + 1);
        specialPurposeRegisters[15] = (char) bytesToInt((byte) (((specialPurposeRegisters[15] >> 8) & 0xFF) + 1), (byte) (specialPurposeRegisters[15] & 0xFF));
    }

    void decrementQ1Counter() {
        //      specialPurposeRegisters[15] = (char) (specialPurposeRegisters[15] - 1);
        specialPurposeRegisters[15] = (char) bytesToInt((byte) (((specialPurposeRegisters[15] >> 8) & 0xFF) - 1), (byte) (specialPurposeRegisters[15] & 0xFF));
    }

    short getQ1Counter() {
        return (short) ((specialPurposeRegisters[15] >> 8) & 0xFF);
    }

    void incrementQ2Counter() {
        //  specialPurposeRegisters[15] = (char) (specialPurposeRegisters[15] + 1);
        specialPurposeRegisters[15] = (char) bytesToInt((byte) ((specialPurposeRegisters[15] >> 8) & 0xFF), (byte) ((specialPurposeRegisters[15] & 0xFF) + 1));
    }

    void decrementQ2Counter() {
        //      specialPurposeRegisters[15] = (char) (specialPurposeRegisters[15] - 1);
        specialPurposeRegisters[15] = (char) bytesToInt((byte) ((specialPurposeRegisters[15] >> 8) & 0xFF), (byte) ((specialPurposeRegisters[15] & 0xFF) - 1));
    }

    short getQ2Counter() {
        return (short) (specialPurposeRegisters[15] & 0xFF);
    }

    /**
     *
     */
    void clearRegisters() {
        for (int i = 0; i < 13; i++) {
            setSpecialPurposeRegister((short) 0, i);
        }
        clearGeneralPurposeRegisters();
    }

    /**
     *
     */
    void clearFlags() {
        specialPurposeRegisters[9] = 0;
    }

    /**
     *
     */
    void clearGeneralPurposeRegisters() {
        for (int i = 0; i < 16; i++) {
            setGeneralPurposeRegister((short) 0, i);
        }
    }

    /**
     *
     * @param byte1
     * @param byte2
     * @return
     */
    private int bytesToInt(byte byte1, byte byte2) {
        byte[] conversionArray = {0, 0, byte1, byte2};
        ByteBuffer ok = ByteBuffer.allocate(4);
        ok.order(ByteOrder.BIG_ENDIAN);
        ok.clear();
        ok.put(conversionArray);
        return ok.getInt(0);
    }
}
