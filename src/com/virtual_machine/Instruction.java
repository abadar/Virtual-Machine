package com.virtual_machine;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author arsalan
 */
public class Instruction {

    private short answer, value1, value2;
    private final Register register;
    private final Memory memory;
    private final Stack stack;

    Instruction(Register register, Memory memory, Stack stack) throws FileNotFoundException, IOException, NumberFormatException {
        this.register = register;
        this.memory = memory;
        this.stack = stack;
    }

    /**
     * Move Command R1 <- R2
     *
     * @param index1
     * @param index2
     */
    void MOV(int index1, int index2) {
        value2 = register.getGeneralPurposeRegister(index2);
        register.setGeneralPurposeRegister(value2, index1);
    }

    /**
     * Add Command R1 <- R1 + R2 @p
     *
     *
     * aram R1
     * @param index2
     */
    void ADD(int index1, int index2) {
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);
        answer = (short) (value1 + value2);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value2, answer);
        register.setGeneralPurposeRegister(answer, index1);
    }

    /**
     * Subtract Command R1 <- R1 - R2 @p
     *
     *
     * aram R1
     * @param index2
     */
    void SUB(int index1, int index2) {
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);
        answer = (short) (value1 - value2);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value2, answer);
        register.setGeneralPurposeRegister(answer, index1);
    }

    /**
     * Multiply Command R1 <- R1 * R2 @p
     *
     *
     * aram R1
     * @param index2
     */
    void MUL(int index1, int index2) {
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);
        answer = (short) (value1 * value2);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value2, answer);
        register.setGeneralPurposeRegister(answer, index1);
    }

    /**
     * Divide Command R1 <- R1 / R2 @p
     *
     *
     * aram R1
     * @param index2
     * @return
     */
    boolean DIV(int index1, int index2) {
        boolean check = true;
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);

        if (value2 != 0) {   // start of if-condition
            answer = (short) (value1 / value2);

            register.clearFlags();
            // set/clear flags
            register.updateZeroFlag(answer);
            register.updateSignFlag(answer);
            register.updateOverflowFlag(value1, value2, answer);
            register.setGeneralPurposeRegister(answer, index1);
        } // end of if-condition
        else {    // start of else-condition
            System.out.println("ERROR!!!!! 0 Causes Infinity, Terminating Program...");
            check = false;
        }   // end of else-condition
        return check;
    }

    /**
     * AND Command R1 <- R1 && R2 @pa
     *
     *
     * ram R1
     * @param index2
     */
    void AND(int index1, int index2) {
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);
        answer = (short) (value1 & value2);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value2, answer);
        register.setGeneralPurposeRegister(answer, index1);
    }

    /**
     * OR COmmand R1 <- R1 || R2 @pa
     *
     *
     * ram R1
     * @param index2
     */
    void OR(int index1, int index2) {
        value1 = register.getGeneralPurposeRegister(index1);
        value2 = register.getGeneralPurposeRegister(index2);
        answer = (short) (value1 | value2);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value2, answer);
        register.setGeneralPurposeRegister(answer, index1);
    }

    /**
     * Shift Left R1 <- R1 << 1
     *
     * @param index
     */
    void SHL(int index) {
        value1 = register.getGeneralPurposeRegister(index);

        answer = (short) (value1 << 1);

        register.clearFlags();
        register.updateCarryFlag(value1);
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * Shift Right R1 <- R1 >> 1
     *
     * @param index
     */
    void SHR(int index) {
        value1 = register.getGeneralPurposeRegister(index);

        answer = (short) (value1 >> 1);

        register.clearFlags();
        register.updateCarryFlag(value1);
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * Rotate Left
     *
     * @param index
     */
    void RTL(int index) {
        value1 = register.getGeneralPurposeRegister(index);

        answer = (short) Integer.rotateLeft(value1, 1);

        register.clearFlags();
        register.updateCarryFlag(value1);
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * Rotate Right
     *
     * @param index
     */
    void RTR(int index) {
        value1 = register.getGeneralPurposeRegister(index);

        answer = (short) Integer.rotateRight(value1, 1);

        register.clearFlags();
        register.updateCarryFlag(value1);
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * INC command
     *
     * @param index
     */
    void INC(int index) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 + 1);

        register.clearFlags();
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value1, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * DEC Command
     *
     * @param index
     */
    void DEC(int index) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 - 1);

        register.clearFlags();
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value1, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * MOVI R1 <- num
     *
     * @param index
     * @param value
     */
    void MOVI(int index, short value) {
        register.setGeneralPurposeRegister(value, index);
    }

    /**
     * ADDI index <- index + num @param
     *
     * index @param value
     */
    void ADDI(int index, short value) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 + value);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * SUBI R1 <- R1 - num @pa
     *
     *
     * ram R1
     * @param value
     */
    void SUBI(int index, short value) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 - value);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * MULI R1 <- R1 * num @pa
     *
     *
     * ram R1
     * @param value
     */
    void MULI(int index, short value) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 * value);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * DIVI R1 <- R1 / num @pa
     *
     *
     * ram index
     * @param value
     * @return
     */
    boolean DIVI(int index, short value) {
        boolean check = true;

        if (value != 0) {   // start of if-condition
            value1 = register.getGeneralPurposeRegister(index);
            answer = (short) (value1 / value);

            register.clearFlags();
            // set/clear flags
            register.updateZeroFlag(answer);
            register.updateSignFlag(answer);
            register.updateOverflowFlag(value1, value, answer);
            register.setGeneralPurposeRegister(answer, index);
        } // end of if-condition
        else {   // start of else-condition
            System.out.println("0 Causes Infinity, Terminating Program...");
            check = false;
        }  // end of else-condition

        return check;
    }

    /**
     * ANDI R1 <- R1 && num @par
     *
     *
     * am R1
     * @param value
     */
    void ANDI(int index, short value) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 & value);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * ORI R1 <- R1 || num @par
     *
     *
     * am R1
     * @param value
     */
    void ORI(int index, short value) {
        value1 = register.getGeneralPurposeRegister(index);
        answer = (short) (value1 | value);

        register.clearFlags();
        // set/clear flags
        register.updateZeroFlag(answer);
        register.updateSignFlag(answer);
        register.updateOverflowFlag(value1, value, answer);
        register.setGeneralPurposeRegister(answer, index);
    }

    /**
     * BZ check flag register, and jump to offset
     *
     * @param offset
     * @return
     */
    boolean BZ(short offset) {
        boolean chk = false;

        if (offset <= register.getCodeLimit()) { // start of if-condition
            if ((register.getZeroFlag())) {  // start of if-condition
                System.out.println("Branching to Offset: " + offset);
                register.setProgramCounter((char) offset);
            } // end of if-condition
            else {
                System.out.println("Cannot Branching ");
            }
            chk = true;
        } // end of if-condition
        else { // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
        }    // end of else-condition

        return chk;
    }

    /**
     * BNZ check flag register, and jump to offset
     *
     * @param offset
     * @return
     */
    boolean BNZ(short offset) {
        boolean chk = false;

        if (offset <= register.getCodeLimit()) {    // start of if-condition
            if (!(register.getZeroFlag())) {// start of if-condition
                System.out.println("Branching to Offset: " + offset);
                register.setProgramCounter((char) offset);
            } // end of if-condition
            else {
                System.out.println("Cannot Branching ");
            }
            chk = true;
        } else {   // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
        }  // end of else-condition        
        return chk;
    }   // end of function

    /**
     * BC check flag register, and jump to offset
     *
     * @param offset
     * @return
     */
    boolean BC(short offset) {
        boolean chk = false;

        if (offset <= register.getCodeLimit()) {  // start of if-condition
            if ((register.getCarryFlag())) { // start of if-condition
                System.out.println("Branching to Offset: " + offset);
                register.setProgramCounter((char) offset);
            } // end of if-condition 
            else {
                System.out.println("Cannot Branching ");
            }
            chk = true;
        } // end of if-condition
        else {   // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
        }  // end of else-condition

        return chk;
    }

    /**
     * BC check flag register, and jump to offset
     *
     * @param offset
     * @return
     */
    boolean BS(short offset) {
        boolean chk = false;

        if (offset <= register.getCodeLimit()) {    // start of if-condition
            if ((register.getSignFlag())) { // start of if-condition
                System.out.println("Branching to Offset: " + offset);
                register.setProgramCounter((char) offset);
            } // end of if-condition  
            else {
                System.out.println("Cannot Branching ");
            }
            chk = true;
        } else {    // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
        }  // end of else-condition

        return chk;
    }

    /**
     * JMP jump to offset
     *
     * @param offset
     * @return
     */
    boolean JMP(short offset) {
        boolean chk = false;
        if (offset <= register.getCodeLimit()) {   // start of if-condition
            System.out.println("Jumping to Offset: " + offset);
            register.setProgramCounter((char) offset);
            chk = true;
        }// end of if-condition
        else {   // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
        }  // end of else-condition

        return chk;
    }

    /**
     * CALL push PC on stack, Jump to offset
     *
     * @param offset
     * @return
     */
    boolean CALL(short offset) {
        boolean chk = false;

        if (offset <= register.getCodeLimit()) {   // start of if-condition
            if (register.getStackCounter() <= register.getStackLimit()) {  // start of if-condition
                //int value1 = register.getProgramCounter();
                //System.out.println(value1);
                System.out.println("PC is push on stack, now jumping to offset: " + offset);
                stack.PUSH((short) register.getProgramCounter());
                register.setProgramCounter((char) offset);
                stack.Show_Stack();
                chk = true;
            } // end of if-condition
            else {    // start of else-condition
                System.out.println("Stack Is Full... Terminating Program..");
                chk = false;
            }   // end of else-condition
        } // end of if-condition
        else {   // start of else-condition
            System.out.println("ERROR!!! Offset is out of Range....");
            chk = false;
        }  // end of else-condition

        return chk;
    }

    /**
     * MOVL R1  Mem [location]
     *
     * @param index
     * @param offset
     */
    void MOVL(int index, int offset) {

        // int add = ((offset / 128) * 2);
        byte b1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase()), memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase() + 1)) + (offset % 128)));
        offset++;
        byte b2 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase()), memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase() + 1)) + (offset % 128)));
        register.setGeneralPurposeRegister((short) memory.bytesToInt(b1, b2), index);

    }

    /**
     * MOVS Mem [location]  R1
     *
     * @param index
     * @param offset
     */
    void MOVS(int index, int offset) {
        memory.Set_Mem((memory.bytesToInt(memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase()), memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase() + 1)) + (offset % 128)), (byte) ((register.getGeneralPurposeRegister(index) >> 8) & 0xFF));
        offset++;
        memory.Set_Mem((memory.bytesToInt(memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase()), memory.Get_Mem_Val(((offset / 128) * 2) + register.getDataBase() + 1)) + (offset % 128)), (byte) ((register.getGeneralPurposeRegister(index) >> 8) & 0xFF));
    }
}
