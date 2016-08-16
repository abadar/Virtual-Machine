package com.virtual_machine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 
 * @author arsalan
 */
public class InstructionDecoder {

    //class variables
    private final Memory memory;
    private final Register register;
    private final Instruction instruction;
    private final PQueue queue;
    private final Stack stack;
    private DisplayCommandScreen display;

    /**
     *
     * @param register
     * @param memory
     * @param queue
     * @param stack
     * @param instruction
     * @throws FileNotFoundException
     * @throws IOException
     */
    InstructionDecoder(Register register, Memory memory, PQueue queue, Stack stack, Instruction instruction) throws FileNotFoundException, IOException {
        this.register = register;
        this.stack = stack;
        this.memory = memory;
        this.instruction = instruction;
        this.queue = queue;
    }

    //Function to fetchInstructions Instructions from Memory
    private void fetchInstructions() throws FileNotFoundException, IOException {
        byte Value;
        boolean check = true;
        if (register.getCodeLimit() != 0) {
            //2 Show_Code_Memory();                      // Show all contents of Memory(Code Part)
        } else {
            check = false;
        }
        //System.out.println();

        while ((register.getProgramCounter() < register.getCodeLimit()) && check) {        //while loop which fetchs instructions till limit or any error occour in code
            queue.enRunningQueue((byte) (register.getPCB() / 128));
            IncWaitTime();
            int add = ((register.getProgramCounter() / 128) * 2);
            Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
            register.incrementProgramCounter();
            check = executeInstruction(Value);                // decode the value fetched from the memory and set the check flag accordingly
            short ET = (short) (memory.ExeTime(register.getPCB()) + 1);
            memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
            memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));
            if (!check) {
                memory.Deallocate();
            }
        }
        queue.deRunningQueue();
    }

    // calling respective functions
    private boolean executeInstruction(byte x) throws FileNotFoundException, IOException {
        boolean check = true;

        if (x >= 22 && x <= 28) {
            check = executeRRInstruction(check, x);
        } else if (x >= 48 && x <= 54) {
            check = executeRIInstructions(check, x);
        } else if (x == 81) {
            check = executeMoveLInstruction(check);
        } else if (x == 82) {
            check = executeMoveSInstruction(check);
        } else if (x >= 113 && x <= 120) {
            check = executeSingleOperandInstructions(x, check);
        } else if (x >= 55 && x <= 61) {
            check = executeRegisterImmInstructions(x, check);
        } else if (x == -15) {
            check = returnInstruction();
        } else if (x == -14) {
            noOperation();
        } else if (x == -13) {
            check = terminate();
        } else {
            check = invalidInstruction();
        }

        return check;
    }

    private boolean returnInstruction() {
        boolean check;
        //RETURN
        System.out.println("RETURN INSTRUCTION:");
        DashboardScreen.Main.append("RETURN INSTRUCTION:\n");
        if (register.getStackCounter() + register.getStackBase() > register.getStackLimit()) {  // start of if-condition
            System.out.println("ERROR!!! Out Of Bounds.... Terminating Program..");
            DashboardScreen.Main.append("ERROR!!! Out Of Bounds.... Terminating Program..\n");
            check = false;
        }// end of if-condition
        else if (register.getStackCounter() == 0) {    // start of else-condition
            System.out.println("Stack is Empty... Terminating Program..");
            DashboardScreen.Main.append("Stack is Empty... Terminating Program..\n");
            check = false;
        } // end of else-condition
        else {  // start of if-condition
            //short temp = ;
            register.setProgramCounter(stack.POP());
            register.setStackCounter((short) (register.getStackCounter() - 2));
            check = true;
            stack.Show_Stack();
            stack.updateStack();
        } // end of if-condition
        return check;
    }

    private void noOperation() {
        //NO OPERATION
        System.out.println("NOOP Instruction: NO OPERATION.........");
        DashboardScreen.Main.append("NOOP Instruction: NO OPERATION.........\n");
        //Reg.incrementProgramCounter();
    }

    private boolean invalidInstruction() {
        boolean check;
        // start of else-condition
        System.out.println("Wrong Instruction!!!! terminating program");
        DashboardScreen.Main.append("Wrong Instruction!!!! terminating program\n");
        check = false;
        return check;
    }

    private boolean terminate() {
        boolean check;
        //END
        System.out.println("TERMINATING PROCESS SUCCESSFULLY.........");
        DashboardScreen.Main.append("TERMINATING PROCESS SUCCESSFULLY.........\n");
        check = false;
        register.incrementProgramCounter();
        return check;
    }

    private boolean executeRegisterImmInstructions(byte x, boolean check) throws IOException {
        short Value;
        //reg - imm instructionz
        register.incrementProgramCounter();
        Value = (short) Get_Imm_Val();
        switch (x) {
            case 55:   //BZ
                System.out.println("BZ Instruction: ");
                DashboardScreen.Main.append("BZ Instruction: \n");
                check = instruction.BZ(Value);
                break;

            case 56:   //BNZ
                System.out.println("BNZ Instruction: ");
                DashboardScreen.Main.append("BNZ Instruction: \n");
                check = instruction.BNZ(Value);
                break;

            case 57:  //BC
                System.out.println("BC Instruction: ");
                DashboardScreen.Main.append("BC Instruction: \n");
                check = instruction.BC(Value);
                break;

            case 58:  //BS
                System.out.println("BS Instruction: ");
                DashboardScreen.Main.append("BS Instruction: \n");
                check = instruction.BS(Value);
                break;

            case 59:   //JMP
                System.out.println("JMP Instruction: ");
                DashboardScreen.Main.append("JMP Instruction: \n");
                //  System.out.println(Value);
                check = instruction.JMP(Value);
                break;

            case 60:   //CALL
                System.out.println("CALL Instruction: ");
                DashboardScreen.Main.append("CALL Instruction: \n");
                System.out.println(Value);
                check = instruction.CALL(Value);
                break;

            case 61:   //ACT
                System.out.println("ACT Instruction: ");
                DashboardScreen.Main.append("ACT Instruction: \n");
                check = ACT(Value);
                break;
        }
        return check;
    }

    private boolean executeSingleOperandInstructions(byte x, boolean check) {
        byte R1;
        //single operand instructions
        int add = ((register.getProgramCounter() / 128) * 2);
        //Reg.setTmpReg1((register.getProgramCounter() / 128) * 2);
        R1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        if ((R1 >= 0 && R1 <= 15)) {   // start of if-condition
            switch (x) {
                case 113://SHL
                    System.out.println("SHL Instruction: ");
                    DashboardScreen.Main.append("SHL Instruction: \n");
                    instruction.SHL(R1);
                    break;

                case 114:  //SHR
                    System.out.println("SHR Instruction: ");
                    DashboardScreen.Main.append("SHR Instruction: \n");
                    instruction.SHR(R1);
                    break;

                case 115:  //RTL
                    System.out.println("RTL Instruction: ");
                    DashboardScreen.Main.append("RTL Instruction: \n");
                    instruction.RTL(R1);
                    break;

                case 116:   //RTR
                    System.out.println("RTR Instruction: ");
                    DashboardScreen.Main.append("RTR Instruction: \n");
                    instruction.RTR(R1);
                    break;

                case 117:   //INC
                    System.out.println("INC Instruction: ");
                    DashboardScreen.Main.append("INC Instruction: \n");
                    instruction.INC(R1);
                    break;

                case 118:  //DEC
                    System.out.println("DEC Instruction: ");
                    DashboardScreen.Main.append("DEC Instruction: \n");
                    instruction.DEC(R1);
                    break;

                case 119:  //PUSH
                    System.out.println("PUSH Instruction: ");
                    DashboardScreen.Main.append("PUSH Instruction: \n");
                    //  int C = register.getStackCounter();
                    // short val1;
                    if (stack.PUSH(register.getGeneralPurposeRegister(R1))) {
                        // val1 = register.getGeneralPurposeRegister(R1);
                        check = true;
                        stack.Show_Stack();
                        stack.updateStack();
                    } // end of if-condition
                    else {  // start of else-condition
                        System.out.println("Stack is Full... Terminating Program..");
                        DashboardScreen.Main.append("Stack is Full... Terminating Program..\n");
                        check = false;
                    }// end of else-condition
                    break;

                case 120: //POP
                    System.out.println("POP Instruction: ");
                    DashboardScreen.Main.append("POP Instruction: \n");
                    short temp;

                    if (register.getStackCounter() + register.getStackBase() > register.getStackLimit()) {  // start of if-condition
                        System.out.println("ERROR!!! Out Of Bounds.... Terminating Program..");
                        DashboardScreen.Main.append("ERROR!!! Out Of Bounds.... Terminating Program..\n");
                        check = false;
                    } // end of if-condition
                    else if (register.getStackCounter() == 0) {  // start of else-condition
                        System.out.println("Stack is Empty... Terminating Program..");
                        DashboardScreen.Main.append("Stack is Empty... Terminating Program..\n");
                        check = false;
                    }// end of else-condition
                    else { // start of if-condition
                        temp = stack.POP();
                        System.out.println("POPed the Value from Stack.. " + temp);
                        DashboardScreen.Main.append("POPed the Value from Stack.. " + temp + "\n");
                        register.setGeneralPurposeRegister(temp, R1);
                        register.setStackCounter((short) (register.getStackCounter() - 2));
                        memory.Set_Mem(register.getStackBase() + register.getStackCounter(), (byte) 0);
                        memory.Set_Mem(register.getStackBase() + register.getStackCounter() + 1, (byte) 0);
                        check = true;
                        stack.Show_Stack();
                        stack.updateStack();
                    }// end of if-condition
                    break;
            }
        }// end of if-condition
        else {    // start of else-condition
            System.out.println("ERROR!!!!No Such Register Exist...... terminating program");
            DashboardScreen.Main.append("ERROR!!!!No Such Register Exist...... terminating program\n");
            check = false;
        }// end of else-condition
        return check;
    }

    private boolean executeMoveSInstruction(boolean check) {
        byte R1;
        short Value;
        //MOVS
        System.out.println("MOVS Instruction: ");
        DashboardScreen.Main.append("MOVS Instruction: \n");
        int add = ((register.getProgramCounter() / 128) * 2);
        //Reg.setTmpReg1((register.getProgramCounter() / 128) * 2);
        R1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        if ((R1 >= 0 && R1 <= 15)) { // start of if-condition
            register.incrementProgramCounter();
            Value = (short) Get_Imm_Val();
            if (register.getDataLimit() == 0) {
                System.out.print("ERROR!!!! No Data Segment is avalible for this process...... Terminating Process");
                DashboardScreen.Main.append("ERROR!!!! No Data Segment is avalible for this process...... Terminating Process\n");
                check = false;
            } else {
                if ((Value <= (register.getDataLimit() - 1))) {// start of if-condition
                    instruction.MOVS(R1, Value);
                }// end of if-condition
                else {  // start of else-condition
                    System.out.println("ERROR!! Wrong Offset");
                    DashboardScreen.Main.append("ERROR!! Wrong Offset\n");
                    check = false;
                }// end of else-condition
            }
        } // end of if-condition
        else {    // start of else-condition
            System.out.println("ERROR!!!!No Such Register Exist...... terminating program");
            DashboardScreen.Main.append("ERROR!!!!No Such Register Exist...... terminating program\n");
            check = false;
        }// end of else-condition
        return check;
    }

    private boolean executeMoveLInstruction(boolean check) {
        byte R1;
        short Value;
        //mem instruction
        System.out.println("MOVL Instruction: ");
        DashboardScreen.Main.append("MOVL Instruction: \n");
        int add = ((register.getProgramCounter() / 128) * 2);
        R1 = (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        if ((R1 >= 0 && R1 <= 15)) {  // start of if-condition
            Value = (short) Get_Imm_Val();
            if (register.getDataLimit() == 0) {
                System.out.print("ERROR!!!! No Data Segment is avalible for this process...... Terminating Process");
                DashboardScreen.Main.append("ERROR!!!! No Data Segment is avalible for this process...... Terminating Process\n");
                check = false;
            } else {
                if ((Value <= (register.getDataLimit() - 1))) { // start of if-condition
                    instruction.MOVL(R1, Value);
                }// end of if-condition
                else { // start of else-condition
                    System.out.println("ERROR!! Wrong Offset");
                    DashboardScreen.Main.append("ERROR!! Wrong Offset\n");
                    check = false;
                }// end of else-condition
            }
        }// end of if-condition
        else {   // start of else-condition
            System.out.println("ERROR!!!!No Such Register Exist...... terminating program");
            DashboardScreen.Main.append("ERROR!!!!No Such Register Exist...... terminating program\n");
            check = false;
        }   // end of else-condition
        return check;
    }

    private boolean executeRIInstructions(boolean check, byte x) {
        byte R1;
        short Value;
        //reg - imm instructionz
        int add = ((register.getProgramCounter() / 128) * 2);
        R1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        if ((R1 >= 0 && R1 <= 15)) {    // start of if-condition

            Value = (short) Get_Imm_Val();

            if ((Value < -32768) && (Value > 32767)) {// start of if-condition
                System.out.println("ERROR!!! WRONG INSTRUCTION...Value Error!!!! terminating program");
                DashboardScreen.Main.append("ERROR!!! WRONG INSTRUCTION...Value Error!!!! terminating program\n");
                check = false;
            }// end of if-condition
            else { // start of else-condition
                switch (x) {
                    case 48:  //MOVI
                        System.out.println("MOVI Instruction: ");
                        DashboardScreen.Main.append("MOVI Instruction: \n");
                        instruction.MOVI((int) R1, (short) Value);
                        break;

                    case 49:  //ADDI
                        System.out.println("ADDI Instruction: ");
                        DashboardScreen.Main.append("ADDI Instruction: \n");
                        instruction.ADDI((int) R1, (short) Value);
                        break;

                    case 50:  //SUBI
                        System.out.println("SUBI Instruction: ");
                        DashboardScreen.Main.append("SUBI Instruction: \n");
                        instruction.SUBI((int) R1, (short) Value);
                        break;

                    case 51: //MULI
                        System.out.println("MULI Instruction: ");
                        DashboardScreen.Main.append("MULI Instruction: \n");
                        instruction.MULI((int) R1, (short) Value);
                        break;

                    case 52:  //DIVI
                        System.out.println("DIVI Instruction: ");
                        DashboardScreen.Main.append("DIVI Instruction: \n");
                        check = instruction.DIVI((int) R1, (short) Value);
                        break;

                    case 53:  //ANDI
                        System.out.println("ANDI Instruction: ");
                        DashboardScreen.Main.append("ANDI Instruction: \n");
                        instruction.ANDI((int) R1, (short) Value);
                        break;

                    case 54:  //ORI
                        System.out.println("ORI Instruction: ");
                        DashboardScreen.Main.append("ORI Instruction: \n");
                        instruction.ORI((int) R1, (short) Value);
                        break;
                }
            }// end of else-condition
        } // end of if-condition
        else { // start of else-condition
            System.out.println("ERROR!!!!No Such Register Exist...... terminating program");
            DashboardScreen.Main.append("ERROR!!!!No Such Register Exist...... terminating program\n");
            check = false;
        }// end of else-condition
        return check;
    }

    private boolean executeRRInstruction(boolean check, byte x) {
        byte R1;
        byte R2;
        //reg - reg instructions
        int add = ((register.getProgramCounter() / 128) * 2);
        R1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        if ((R1 >= 0 && R1 <= 15)) {     // start of if-condition
            add = ((register.getProgramCounter() / 128) * 2);
            R2 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));

            register.incrementProgramCounter();
            check = executeArithmeticOperations(R2, x, R1, check);
        } else {
            System.out.println("ERROR!!!!!! WRONG INSTRUCTION...No such Register Exists!!!! terminating program");
            DashboardScreen.Main.append("ERROR!!!!!! WRONG INSTRUCTION...No such Register Exists!!!! terminating program\n");
            check = false;
        }
        return check;
    }

    private boolean executeArithmeticOperations(byte R2, byte x, byte R1, boolean check) {
        if ((R2 >= 0 && R2 <= 15)) {                          // start of if-condition
            switch (x) {
                case 22:  //MOV
                    System.out.println("MOV Instruction: ");
                    DashboardScreen.Main.append("MOV Instruction: \n");
                    instruction.MOV((int) R1, (int) R2);
                    break;

                case 23:    //ADD
                    System.out.println("ADD Instruction: ");
                    DashboardScreen.Main.append("ADD Instruction: \n");
                    instruction.ADD((int) R1, (int) R2);
                    break;

                case 24:  //SUB
                    System.out.println("SUB Instruction: ");
                    DashboardScreen.Main.append("SUB Instruction: \n");
                    instruction.SUB((int) R1, (int) R2);
                    break;

                case 25:      //MUL
                    System.out.println("MUL Instruction: ");
                    DashboardScreen.Main.append("MUL Instruction: \n");
                    instruction.MUL((int) R1, (int) R2);
                    break;

                case 26:      //DIV
                    System.out.println("DIV Instruction: ");
                    DashboardScreen.Main.append("DIV Instruction: \n");
                    check = instruction.DIV((int) R1, (int) R2);
                    break;

                case 27:      //AND
                    System.out.println("AND Instruction: ");
                    DashboardScreen.Main.append("AND Instruction: \n");
                    instruction.AND((int) R1, (int) R2);
                    break;

                case 28:      //OR
                    System.out.println("OR Instruction: ");
                    DashboardScreen.Main.append("OR Instruction: \n");
                    instruction.OR((int) R1, (int) R2);
                    break;
            } // end of condition
        } else {   // start of else-condition
            System.out.println("ERROR!!!!!! WRONG INSTRUCTION...No such Register Exists!!!! terminating program");
            DashboardScreen.Main.append("ERROR!!!!!! WRONG INSTRUCTION...No such Register Exists!!!! terminating program\n");
            check = false;
        }// end of else-condition
        return check;
    }

    // function to combine 2 bytes and return the immediate vaalue from function
    int Get_Imm_Val() {

        int add = ((register.getProgramCounter() / 128) * 2);

        byte b1 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        byte b2 = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
        register.incrementProgramCounter();
        return memory.bytesToInt(b1, b2);
    }

    //function to perform task defined by number    
    boolean ACT(short offset) throws FileNotFoundException, IOException {
        return executeInstruction((byte) offset);
    }//end of function

    // function for priority sceduling
    void Priotity() throws FileNotFoundException, IOException {
        register.setTmpReg1(1);
        if (register.getQ1Counter() != 0) {

            System.out.println("\n\nPriority scheduling algorithm \n");
        }
        do {
            if (register.getQ1Counter() != 0) {
                register.setTmpReg1(queue.dePQueue1() * 128);                                //Acts as running Queue
                queue.enRunningQueue((byte) (register.getTmpReg1() / 128));
            }
            memory.Set_PCB_To_Reg(register.getTmpReg1() + 7);
            System.out.println("---------------------------------------Process: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Starts");
            fetchInstructions();
            System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Is Completed");
            queue.deRunningQueue();
        } while (register.getQ1Counter() != 0);

    }

    // function for round robin
    void Round_Robin() throws FileNotFoundException, IOException {
        register.setTmpReg1(0);
        boolean c = true;

        if (register.getQ2Counter() != 0) {
            System.out.println("\n\nRound Robin scheduling algorithm ");
            register.setTmpReg1(queue.deRQueue() * 128);

        }
        while (register.getTmpReg1() != 0) {
            queue.enRunningQueue((byte) (register.getTmpReg1() / 128));
            c = true;
            System.out.println("Registers UPDATED!!!!!");
            memory.Set_PCB_To_Reg(register.getTmpReg1() + 7);                                  // setting PCB to register
            System.out.println("---------------------------------------Process: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Starts");
            DashboardScreen.Main.append("---------------------------------------Process: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Starts\n");
            byte Value = 0;
            for (int i = 0; i < 4 && c; i++) {                                      // loop to run 4 instructions
                int add = ((register.getProgramCounter() / 128) * 2);
                Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
                //  System.out.println(Value);
                register.incrementProgramCounter();
                c = executeInstruction(Value);
                short ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));
                if (c) {
                    //    register.showGeneralPurposeRegisters();
                    //  register.showSpecialPurposeRegisters();
                    // memory.Show_PCB();
                }

            }
            queue.deRunningQueue();
            register.setTmpReg1(queue.deRQueue() * 128);
            if (c && register.getProgramCounter() != register.getCodeLimit()) {
                System.out.println("PCB UPDATED!!!!!");
                DashboardScreen.Main.append("PCB UPDATED!!!!!\n");
                memory.Set_Reg_To_PCB(register.getPCB() + 7);
                queue.enRQueue(memory.Get_Mem_Val(register.getPCB() + 5), (byte) (register.getPCB() / 128));

            } else if (c == false || Value == -13) {
                memory.Deallocate();
                System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Is Completed");
                DashboardScreen.Main.append("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Is Completed\n");
            }

        }
    }

    void FeedBack() throws FileNotFoundException, IOException {
        int l = 1;
        if (register.getQ1Counter() != 0) {
            System.out.println("\n\nMultiLevel Feedback scheduling algorithm \n");

            while (register.getQ1Counter() != 0) {                                                            // loop running till queue 1 is not empty
                System.out.println("Queue 1");
                queue.showPQueue();                 //showing queue 1
                System.out.println("Queue 2");
                queue.showRQueue();                 //showing queue 1
                //System.out.println();
                if (register.getQ1Counter() != 0) {
                    l = (queue.dePQueue1() * 128);
                    queue.enRunningQueue((byte) (l / 128));
                    memory.Set_PCB_To_Reg(l + 7);

                    boolean c = true, maketrue = false;
                    byte Value = 0;
                    short ET = 0;
                    if (memory.ExeTime(register.getPCB()) == 0) {
                        maketrue = true;
                        ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                        memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                        memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));
                    }

                    for (int i = 0; i < 2 && c && memory.ExeTime(register.getPCB()) > 0 && memory.ExeTime(register.getPCB()) % 2 != 0; i++) {                                      // loop torun 2 instructions
                        if (i == 0) {
                            System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Starts");
                            DashboardScreen.Main.append("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Starts\n");
                        }
                        IncWaitTime();
                        if (i != 0) {
                            ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                            memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                            memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));
                        }
                        int add = ((register.getProgramCounter() / 128) * 2);

                        Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
                        System.out.println(Value);
                        register.incrementProgramCounter();
                        c = executeInstruction(Value);

                        if (c) {
//                            register.showGeneralPurposeRegisters();
                            //                          register.showSpecialPurposeRegisters();
                            memory.Show_PCB();
                            //System.out.println();
                        }
                        if (c == false || Value == -13) {
                            memory.Show_PCB();
                            queue.deRunningQueue();
                            memory.Deallocate();
                            System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Is Completed\n");
                            DashboardScreen.Main.append("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Is Completed\n");

                        }
                    }
                    /*if (c == true && Value != -13) {
                     memory.Set_Mem(l + 5, (byte) (memory.Get_Mem_Val(l + 5) + 16));
                     memory.Set_Reg_To_PCB(l + 7);
                     }*/

                    if ((c == true && Value != -13) && memory.ExeTime(l) > 0 && memory.ExeTime(l) % 2 == 0) {
                        memory.Set_Mem(l + 5, (byte) (memory.Get_Mem_Val(l + 5) + 16));
                        memory.Set_Reg_To_PCB(l + 7);
                        queue.enPQueue2(memory.Get_Mem_Val(l + 5), (byte) (l / 128));
                        queue.deRunningQueue();
                    }

                }

            }
        }
        System.out.println("Queue 1");
        queue.showPQueue();
        System.out.println("Queue 2");
        queue.showRQueue();
        //System.out.println();
        System.out.println("Queue 1 Ends---------------------------------------------------------------\n");

        if (register.getQ1Counter() == 0 && register.getQ2Counter() != 0) {
            //      do {
            if (register.getQ2Counter() != 0) {
                l = (queue.deRQueue() * 128);
                queue.enRunningQueue((byte) (l / 128));

                System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Starts");
                DashboardScreen.Main.append("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Starts\n");
                memory.Set_PCB_To_Reg(l + 7);
                //    fetchInstructions();
                boolean check = true;
                for (int i = 1; (register.getProgramCounter() < register.getCodeLimit()) && check; i++) {        //while loop which fetchs instructions till limit or any error occour in code
                    IncWaitTime();
                    int add = ((register.getProgramCounter() / 128) * 2);
                    byte Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
                    System.out.println(Value);
                    register.incrementProgramCounter();
                    check = executeInstruction(Value);                // decode the value fetched from the memory and set the check flag accordingly
                    //   memory.Set_Mem(register.getPCB() + 2, (byte) (memory.Get_Mem_Val(register.getPCB() + 2) + 2));
                    short ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                    memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                    memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));

                    if (check) {
                        //   register.showGeneralPurposeRegisters();                              // Function to show all General Purpose Register
                        //     register.showSpecialPurposeRegisters();                              // Function to show all Special Purpose Register
                        //  memory.Show_PCB();
                        //System.out.println();
                    } else if (check == false) {
                        memory.Show_PCB();
                        //queue.deRunningQueue();
                        memory.Deallocate();
                    }

                }
                queue.deRunningQueue();
                System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Is Completed");
                DashboardScreen.Main.append("---------------------------------------Process ID: " + memory.Get_Mem_Val(l + 6) + " Is Completed\n");

            } //while (register.getQ2Counter() != 0);
        }
        if (register.getQ1Counter() != 0 && (register.getQ2Counter() != 0 || register.getQ2Counter() == 0)) {
            FeedBack();
        }
    }

    private void IncWaitTime() {
        int tmp, loop;
        short WT;

        loop = register.getQ1Counter();

        for (int j = 0; j < loop; j++) {                                // loop to run till queue 2 is not empty
            tmp = queue.dePQueue1() * 128;
            WT = (short) (memory.WaitTime(tmp) + 2);
            memory.Set_Mem(tmp + 1, (byte) ((WT >> 8) & 0xFF));
            memory.Set_Mem(tmp + 2, (byte) (WT & 0xFF));
            queue.enPQueue(memory.Get_Mem_Val(tmp + 5), (byte) (tmp / 128));
        }

        loop = register.getQ2Counter();

        for (int j = 0; j < loop; j++) {                                // loop to run till queue 2 is not empty
            tmp = queue.deRQueue() * 128;
            WT = (short) (memory.WaitTime(tmp) + 2);
            memory.Set_Mem(tmp + 1, (byte) ((WT >> 8) & 0xFF));
            memory.Set_Mem(tmp + 2, (byte) (WT & 0xFF));

            if (memory.WaitTime(tmp) % 12 != 0) {
                queue.enRQueue(memory.Get_Mem_Val(tmp + 5), (byte) (tmp / 128));
                System.out.println("Enqueue in queue 2");
            } else {
                memory.Set_Mem(tmp + 5, (byte) (memory.Get_Mem_Val(tmp + 5) - 16));
                queue.enPQueue1(memory.Get_Mem_Val(tmp + 5), (byte) (tmp / 128));
                System.out.println("Enqueue in queue 1");
            }
        }
        loop = memory.Get_R_Counter();

        for (int j = 0; j < loop; j++) {                                // loop to run till queue 2 is not empty
            tmp = queue.deBQueue() * 128;
            WT = (short) (memory.WaitTime(tmp) + 2);
            memory.Set_Mem(tmp + 1, (byte) ((WT >> 8) & 0xFF));
            memory.Set_Mem(tmp + 2, (byte) (WT & 0xFF));
            queue.enBQueue((byte) (tmp / 128));
        }
        //System.out.println();
    }

    void Run_p(byte id) throws FileNotFoundException, IOException {
        boolean chk = false;
        int tmp = 0, tmp2 = 0, tmp3 = 0;
        if (register.getQ1Counter() > 0) {
            tmp = register.getQ1Counter();
            while (tmp != 0) {
                chk = false;
                tmp2 = queue.dePQueue1() * 128;
                //
                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    queue.enRunningQueue((byte) (tmp2 / 128));
                    memory.Set_PCB_To_Reg(tmp2 + 7);
                    fetchInstructions();
                    queue.deRunningQueue();
                    chk = true;
                } //else //    System.out.println("Not Found!!!");
                if (!chk) {
                    //{
                    queue.enPQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                    //}
                }
                tmp--;

            }
        }
        if (!chk && register.getQ2Counter() > 0) {
            tmp = register.getQ2Counter();
            while (tmp != 0) {
                tmp2 = queue.deRQueue() * 128;
                //            queue.enRunningQueue((byte) (tmp2 / 128));
                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    memory.Set_PCB_To_Reg(tmp2 + 7);
                    queue.enRunningQueue((byte) (tmp2 / 128));
                    fetchInstructions();
                    queue.deRunningQueue();
                    chk = true;
                } //if (!chk) 
                else {
                    queue.enRQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                }
                tmp--;

            }
        } else {
            System.out.println("Not Found!!!");
        }
        if (chk == false) {
            System.out.println("Sorry This Process Is Blocked!!!!!");
            DashboardScreen.Main.append("Sorry This Process Is Blocked!!!!!\n");
        }
        // return chk;
    }

    void Debug_p(byte id) throws FileNotFoundException, IOException {
        boolean chk = false;
        int tmp = 0, tmp2 = 0, tmp3 = 0;

        if (register.getQ1Counter() > 0) {
            tmp = register.getQ1Counter();
            while (tmp != 0) {
                tmp2 = queue.dePQueue1() * 128;
                queue.enRunningQueue((byte) (tmp2 / 128));
                if (id == memory.Get_Mem_Val(tmp2 + 6) && !chk) {
                    chk = true;
                    memory.Set_PCB_To_Reg(tmp2 + 7);

                    System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Starts");
                    boolean c = true;
                    byte Value = 0;                                      // loop torun 2 instructions
                    IncWaitTime();

                    short ET;
                    ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                    memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                    memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));

                    int add = ((register.getProgramCounter() / 128) * 2);

                    Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
                    System.out.println(Value);
                    register.incrementProgramCounter();
                    c = executeInstruction(Value);

                    if (c) {
                        //Reg.showGeneralPurposeRegisters();
                        //Reg.showSpecialPurposeRegisters();
                        //mem.Show_PCB();
                        System.out.println();
                    }
                    if (c == false || Value == -13) {
                        //mem.Show_PCB();
                        queue.deRunningQueue();
                        memory.Deallocate();
                        System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Is Completed\n");
                    } else if (c == true && Value != -13 && memory.ExeTime(register.getPCB()) > 0 && memory.ExeTime(register.getPCB()) % 2 == 0) {
                        memory.Set_Mem(register.getPCB() + 5, (byte) (memory.Get_Mem_Val(register.getPCB() + 5) + 16));
                        memory.Set_Reg_To_PCB(register.getPCB() + 7);
                        queue.enPQueue2(memory.Get_Mem_Val(register.getPCB() + 5), (byte) (register.getPCB() / 128));
                        queue.deRunningQueue();

                    } else if (c == true && Value != -13) {
                        memory.Set_Reg_To_PCB(register.getPCB() + 7);
                        queue.enPQueue(memory.Get_Mem_Val(register.getPCB() + 5), (byte) (register.getPCB() / 128));
                        queue.deRunningQueue();
                    }

                    System.out.println("Debugged Process " + id);
                    DashboardScreen.Main.append("Debugged Process " + id + "\n");
                } else {
                    queue.enPQueue(memory.Get_Mem_Val(tmp2 + 3), (byte) (tmp2 / 128));
                    queue.deRunningQueue();

                }
                tmp--;

            }
        }
        if (!chk && register.getQ2Counter() > 0) {
            tmp = register.getQ2Counter();
            while (tmp != 0) {
                tmp2 = queue.deRQueue() * 128;
                queue.enRunningQueue((byte) (tmp2 / 128));
                if (id == memory.Get_Mem_Val(tmp2 + 6) && !chk) {
                    chk = true;
                    memory.Set_PCB_To_Reg(tmp2 + 7);
                    System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Starts");
                    boolean c = true;
                    byte Value = 0;                                      // loop torun 2 instructions
                    IncWaitTime();

                    short ET;

                    int add = ((register.getProgramCounter() / 128) * 2);

                    Value = (byte) (memory.Get_Mem_Val(memory.bytesToInt(memory.Get_Mem_Val(add + register.getCodeBase()), memory.Get_Mem_Val(add + register.getCodeBase() + 1)) + (register.getProgramCounter() % 128)));
                    System.out.println(Value);
                    register.incrementProgramCounter();
                    c = executeInstruction(Value);
                    ET = (short) (memory.ExeTime(register.getPCB()) + 1);
                    memory.Set_Mem(register.getPCB() + 3, (byte) ((ET >> 8) & 0xFF));
                    memory.Set_Mem(register.getPCB() + 4, (byte) (ET & 0xFF));

                    if (c) {
                        //Reg.showGeneralPurposeRegisters();
                        //Reg.showSpecialPurposeRegisters();
                        //mem.Show_PCB();
                        System.out.println();
                    }
                    if (c == false || Value == -13) {
                        queue.deRunningQueue();
                        //mem.Show_PCB();
                        memory.Deallocate();
                        System.out.println("---------------------------------------Process ID: " + memory.Get_Mem_Val(register.getPCB() + 6) + " Is Completed\n");
                    }
                    if (c == true && Value != -13) {
                        // memory.Set_Mem(l + 5, (byte) (memory.Get_Mem_Val(l + 5) - 16));
                        memory.Set_Reg_To_PCB(register.getPCB() + 7);
                        queue.enRQueue(memory.Get_Mem_Val(register.getPCB() + 5), (byte) (register.getPCB() / 128));
                        queue.deRunningQueue();

                    }

                    System.out.println("Debugged Process " + id);
                    DashboardScreen.Main.append("Debugged Process " + id + "\n");
                } else {
                    queue.enRQueue(memory.Get_Mem_Val(tmp2 + 3), (byte) (tmp2 / 128));
                    queue.deRunningQueue();

                }
                tmp--;

            }
        }
        if (chk == false) {
            System.out.println("Sorry this process is in Blocked Queue!!!!!");
            DashboardScreen.Main.append("Sorry this process is in Blocked Queue!!!!!\n");
        }

    }

    void Run_a() throws FileNotFoundException, IOException {
        // Priotity();
        // Round_Robin();
        if (register.getQ1Counter() > 0 || register.getQ2Counter() > 0) {
            FeedBack();

        } else if (memory.Get_R_Counter() > 0) {
            System.out.println("Sorry this process is in Blocked Queue!!!!!");
            DashboardScreen.Main.append("Sorry this process is in Blocked Queue!!!!!\n");
        } else {
        }
    }

    void Kill_p(byte id) {
        boolean chk = false;
        int tmp = 0, tmp2 = 0, tmp3 = 0;

        if (register.getQ1Counter() > 0) {
            tmp = register.getQ1Counter();
            for (int i = 0; i < tmp; i++) {
                tmp2 = queue.dePQueue1() * 128;

                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                    memory.Set_PCB_To_Reg(tmp2 + 7);
                    memory.Deallocate();

                    System.out.println("Killed Process " + id);
                    DashboardScreen.Main.append("Killed Process " + id + "\n");
                } else {
                    queue.enPQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                }
            }
        }
        if (!chk && register.getQ2Counter() > 0) {
            tmp = register.getQ2Counter();
            while (tmp != 0) {
                tmp2 = queue.deRQueue() * 128;
                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                    memory.Set_PCB_To_Reg(tmp2 + 7);
                    memory.Deallocate();

                    System.out.println("Killed Process " + id);
                    DashboardScreen.Main.append("Killed Process " + id + "\n");
                } else {
                    queue.enRQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));

                }
                tmp--;

            }
        }
        /* if (!chk) {
         tmp = memory.Get_R_Counter();
         for (int i = 0; i < tmp; i++) {
         tmp2 = queue.deBQueue() * 128;
         if (id == memory.Get_Mem_Val(tmp2 + 6)) {
         chk = true;
         memory.Set_PCB_To_Reg(tmp2 + 7);
         memory.Deallocate();


         System.out.println("Killed Process " + id);
         DashboardScreen.Main.append("Killed Process " + id + "\n");
         } else {
         queue.enBQueue((byte) (tmp2 / 128));

         }

         }
         }*/
        if (chk == false) {
            System.out.println("Sorry this process is in Blocked Queue!!!!!");
            DashboardScreen.Main.append("Sorry this process is in Blocked Queue!!!!!");
        }
    }

    void Debug_a() throws FileNotFoundException, IOException {

        byte b = 0;
        int c, add = 0;

        for (int i = 0; i < DashboardScreen.PIDs.getItemCount(); i++) {
            b = (byte) Integer.parseInt((String) DashboardScreen.PIDs.getItemAt(i));
            Debug_p(b);
        }
    }

    void Clone(byte id) throws UnsupportedEncodingException {
        Random ran = new Random();
        int n, tmp;
        n = (ran.nextInt(126) + 1);
        while (queue.test((byte) n)) {
            n = (ran.nextInt(126) + 1);
        }
        boolean chk = false;

        int add_p = Get_Add(id);
        tmp = add_p;

        if ((memory.Get_Mem_Val(add_p)) + register.getKmCounter() > 127) {
            System.out.println("Cannot Proceed!! No free Frames avalible for this process............");
            DashboardScreen.Main.append("Cannot Proceed!! No free Frames avalible for this process............\n");
        } else {
            if (add_p != 0) {
                DashboardScreen.Main.append("\nCreating Clone with id: " + n + " ............\n");
                int index = (memory.Get_Free_KM_Frame((int) (memory.Get_Mem_Val(add_p)))) * 128;
                if ((memory.Get_Mem_Val(add_p + 65) + memory.Get_Mem_Val(add_p + 66) + (memory.Get_Mem_Val(add_p + 65) * 2)) * 2 + register.getMmCounter() < 384) {
                    byte p = memory.Get_Mem_Val(add_p + 5);
                    if (p >= 0 && p <= 15 && register.getQ1Counter() + 1 < 32) {
                        queue.enPQueue1(p, (byte) (index / 128));
                    } else {
                        queue.enPQueue2(p, (byte) (index / 128));
                    }
                    memory.Set_Mem(index, memory.Get_Mem_Val(add_p));
                    for (int i = 1; i < 6; i++) {
                        memory.Set_Mem(index + i, (byte) 0);
                    }

                    memory.Set_Mem(index + 6, (byte) n);

                    memory.Set_PCB_To_Reg(add_p + 7);
                    register.setPCB(index);

                    int DT;
                    memory.Set_Mem(index + 65, memory.Get_Mem_Val(add_p + 65));

                    index += 66;
                    add_p += 66;
                    register.setStackBase(memory.Get_Free_MM_Frame() * 128);
                    register.setDataBase(index);

                    for (int i = 0; i < (Math.ceil(register.getDataLimit() / 128.0)); i++) {
                        int address;
                        address = memory.bytesToInt(memory.Get_Mem_Val(add_p + i), memory.Get_Mem_Val(add_p + i + 1));

                        DT = (memory.Get_Free_MM_Frame() * 128);

                        memory.Set_Mem(index, (byte) ((DT >> 8) & 0xFF));
                        index++;
                        add_p++;
                        memory.Set_Mem(index, (byte) (DT & 0xFF));
                        index++;
                        add_p++;
                        for (int s = 0; s < 128 && (s < register.getDataLimit()); s++) {
                            memory.Set_Mem(DT + s, memory.Get_Mem_Val(address + s));
                        }

                    }

                    //code
                    memory.Set_Mem(index, memory.Get_Mem_Val(add_p));
                    index++;
                    add_p++;
                    register.setCodeBase(index);

                    for (int i = 0; i < (Math.ceil(register.getCodeLimit() / 128.0)); i++) {
                        int address;
                        address = memory.bytesToInt(memory.Get_Mem_Val(add_p + i), memory.Get_Mem_Val(add_p + i + 1));
                        DT = (memory.Get_Free_MM_Frame() * 128);

                        memory.Set_Mem(index, (byte) ((DT >> 8) & 0xFF));
                        index++;
                        add_p++;
                        memory.Set_Mem(index, (byte) (DT & 0xFF));
                        index++;
                        add_p++;

                        for (int s = 0; s < 128 && (s < register.getCodeLimit()); s++) {
                            memory.Set_Mem(DT + s, memory.Get_Mem_Val(address + s));
                        }

                    }
                    //total size
                    memory.Set_Mem(index, memory.Get_Mem_Val(add_p));
                    index++;
                    add_p++;
                    memory.Set_Mem(index, memory.Get_Mem_Val(add_p));
                    index++;
                    add_p++;

                    String name = Get_File_Name(tmp);
                    name = name + "_" + n;
                    memory.Set_Mem(index, (byte) name.length());
                    index++;
                    add_p++;
                    byte[] ByteArray;
                    ByteArray = name.getBytes("UTF-8");
                    for (int s = 0; s < name.length(); s++) {
                        memory.Set_Mem(index, ByteArray[s]);
                        index++;
                    }
                    register.setProgramCounter(0);
                    register.setStackCounter(0);
                    register.clearGeneralPurposeRegisters();
                    memory.Set_Reg_To_PCB(register.getPCB() + 7);
                    queue.updatePID();
                    Print_Info();
                } else {
                    DashboardScreen.Main.append("\nCannot make Clone as this process is blockd\n");
                    System.out.println("Cannot make Clone as this process is blockd");
                }
            } else {
                DashboardScreen.Main.append("\nCannot make Clone\n");
                System.out.println("Cannot make Clone");
            }
        }

    }

    void Block(byte id) {
        boolean chk = false;
        int tmp = 0, tmp2 = 0, tmp3 = 0;

        if (register.getQ1Counter() > 0) {
            tmp = register.getQ1Counter();
            while (tmp != 0) {
                tmp2 = queue.dePQueue1() * 128;

                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                    queue.enBQueue((byte) (tmp2 / 128));
                    System.out.println("Blocked Process " + id);
                    DashboardScreen.Main.append("Blocked Process " + id + "\n");
                } else {
                    queue.enPQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                }
                tmp--;
            }
        }
        if (!chk && register.getQ2Counter() > 0) {
            tmp = register.getQ2Counter();
            while (tmp != 0) {
                tmp2 = queue.deRQueue() * 128;
                if (id == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                    queue.enBQueue((byte) (tmp2 / 128));
                    System.out.println("Blocked Process " + id);
                    DashboardScreen.Main.append("Blocked Process " + id + "\n");
                } else {
                    queue.enRQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                }
                tmp--;
            }
        }
        if (chk == false) {
            System.out.println("No Such Process Id Exist!!!!!");
            DashboardScreen.Main.append("No Such Process Id Exist!!!!!\n");
        }

        Print_Info();
    }

    void UnBlock(byte id) {
        boolean chk = false;
        int i = 1;
        do {
            i = queue.deBQueue() * 128;
            if (id == memory.Get_Mem_Val(i + 6) && i != 0) {
                chk = true;
                if (memory.Get_Mem_Val(i + 5) >= 0 && memory.Get_Mem_Val(i + 5) <= 15 && register.getQ1Counter() + 1 < 32) {
                    queue.enPQueue1(memory.Get_Mem_Val(i + 5), (byte) (i / 128));
                    //enRQueue(PP, (byte) (register.getIndex()/128));
                } else if (memory.Get_Mem_Val(i + 5) >= 16 && memory.Get_Mem_Val(i + 5) <= 31 && register.getQ2Counter() + 1 < 32) {
                    queue.enPQueue2(memory.Get_Mem_Val(i + 5), (byte) (i / 128));
                }
                System.out.println("UnBlocked Process " + id);
                DashboardScreen.Main.append("UnBlocked Process " + id + "\n");
            } else {
            }
        } while (i != 0 && !chk);
        Print_Info();
    }

    void ListP(int i, DisplayCommandScreen d) {
        int l, add;
        display = d;
        switch (i) {
            case 0:
                // queue.showPQueue();
                l = register.getQ1Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.dePQueue1() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    //  display.Set_Listp(null,0);
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enPQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Queue 1");
                    //dis.Set_Listp(null,0);
                    display.Set_Listp("No Process is in Queue 1\n", 1);
                }
                //  queue.showRQueue();
                l = register.getQ2Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.deRQueue() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enRQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Queue 2");
                    display.Set_Listp("No Process is in Queue 2\n", 1);
                }
                //  queue.showRunningQueue();//Queue();
                add = queue.deRunningQueue() * 128;
                if (add != 0) {
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                } else {
                    System.out.println("No Process is in Running Queue");
                    display.Set_Listp("No Process is in Running Queue\n", 1);
                }
                queue.enRunningQueue((byte) (add / 128));
                //queue.showBQueue();//showQueue();
                l = memory.Get_R_Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.deBQueue() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enBQueue((byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Blocked");
                    display.Set_Listp("No Process is in Blocked\n", 1);
                }
                break;
            case 1:
                //   queue.showBQueue();//showQueue();
                l = memory.Get_R_Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.deBQueue() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enBQueue((byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Blocked Queue");
                    display.Set_Listp("No Process is in Blocked Queue\n", 1);
                }
                break;
            case 2:
                //   queue.showPQueue();
                l = register.getQ1Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.dePQueue1() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enPQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Queue 1");
                    display.Set_Listp("No Process is in Queue 1\n", 1);
                }
                //  queue.showRQueue();
                l = register.getQ2Counter();
                for (int k = 0; k < l; k++) {
                    add = queue.deRQueue() * 128;
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                    queue.enRQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
                }
                if (l == 0) {
                    System.out.println("No Process is in Queue 2");
                    display.Set_Listp("No Process is in Queue 2\n", 1);
                }
                break;
            case 3:
                //  queue.showRunningQueue();//Queue();
                add = queue.deRunningQueue() * 128;
                if (add > 0) {
                    System.out.println(memory.Get_Mem_Val(add + 6));
                    display.Set_Listp("PID: " + Integer.toString(memory.Get_Mem_Val(add + 6)) + "\n", 1);
                } else {
                    System.out.println("No Process is in Running Queue");
                    display.Set_Listp("No Process is in Running Queue\n", 1);
                }
                queue.enRunningQueue((byte) (add / 128));
                break;
        }
    }

    void Display_PCB(byte id, DisplayCommandScreen dis) throws UnsupportedEncodingException {
        int add = Get_Add(id), k = 0;
        if (add != 0) {
            memory.Set_PCB_To_Reg(add + 7);

            dis.Set_DisPcb("\nNo Of Kernal Frames = " + memory.Get_Mem_Val(register.getPCB()) + "\n", 1);
            dis.Set_DisPcb("Waiting Time = " + (short) memory.WaitTime(register.getPCB()) + "\n", 1);
            dis.Set_DisPcb("Execution Time = " + (short) memory.ExeTime(register.getPCB()) + "\n", 1);
            dis.Set_DisPcb("Process Priority = " + memory.Get_Mem_Val(register.getPCB() + 5) + "\n", 1);
            dis.Set_DisPcb("Process ID = " + memory.Get_Mem_Val(register.getPCB() + 6) + "\n", 1);
            int l = 0;
            l = register.getPCB() + 65 + (l * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("\nPage Table of Data: " + memory.Get_Mem_Val(l));
                dis.Set_DisPcb("\n\nPage Table of Data: " + memory.Get_Mem_Val(l) + "\n", 1);
                k = 1;
                for (int i = 0; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_DisPcb("\n" + (memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1))) + "\n", 1);
                }
            }

            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Page Table of Code: ");
                dis.Set_DisPcb("\nPage Table of Code: " + memory.Get_Mem_Val(l) + "\n", 1);
                k = 1;
                for (int i = 0; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_DisPcb("\n" + (memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1))) + "\n", 1);
                }
            }
            System.out.println(memory.Get_Mem_Val(l) * 2);
            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2) + k;
            ////System.out.println();
            dis.Set_DisPcb("\nProcess Size: " + (memory.bytesToInt(memory.Get_Mem_Val(l), memory.Get_Mem_Val(l + 1))) + "\n", 1);
            l += 2;
            //dis.Set_DisPcb("\n"+memory.Get_Mem_Val(l),1);
            byte arr[] = new byte[memory.Get_Mem_Val(l)];
            for (int i = 0; i < memory.Get_Mem_Val(l); i++) {
                arr[i] = memory.Get_Mem_Val(l + i + 1);
            }
            String name = new String(arr, "UTF-8");
            dis.Set_DisPcb("File Name: " + name, 1);
            register.showGeneralPurposeRegisters();
            register.showSpecialPurposeRegisters();
            dis.Set_DisPcb("\n**************** GENERAL PURPOSE REGISTERS ********************************\n", 1);
            dis.Set_DisPcb("---------------------------------------------------------------------------\n", 1);
            for (int i = 0; i < 16; i++) {
                dis.Set_DisPcb("Reg[ " + i + " ] = " + Integer.toHexString(((short) (register.getGeneralPurposeRegister(i)))) + "\n ", 1);
            } //end of loop
            dis.Set_DisPcb("---------------------------------------------------------------------------\n", 1);
            dis.Set_DisPcb("\n**************** SPECIAL PURPOSE REGISTERS ********************************", 1);
            dis.Set_DisPcb("\n\nRegister[0]: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(0))), 1);
            dis.Set_DisPcb("\nCode Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(1))), 1);
            dis.Set_DisPcb("\nCode Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(2))), 1);
            dis.Set_DisPcb("\nCode Counter(PC): " + Integer.toHexString((short) (register.getSpecialPurposeRegister(3))), 1);
            dis.Set_DisPcb("\nStack Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(4))), 1);
            dis.Set_DisPcb("\nStack Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(5))), 1);
            dis.Set_DisPcb("\nStack Counter: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(6))), 1);
            dis.Set_DisPcb("\nData Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(7))), 1);
            dis.Set_DisPcb("\nData Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(8))), 1);
            boolean flag = register.getCarryFlag();                      //get value of carry flag

            if (flag) {
                System.out.println("CF: 1");
                dis.Set_DisPcb("\nCF: 1", 1);

            } else {
                System.out.println("CF: 0");
                dis.Set_DisPcb("\nCF: 0", 1);
            }
            flag = register.getZeroFlag();                              //get value of zero flag

            if (flag) {
                System.out.println("ZF: 1");
                dis.Set_DisPcb("\nZF: 1", 1);

            } else {
                System.out.println("ZF: 0");
                dis.Set_DisPcb("\nZF: 0", 1);
            }

            flag = register.getSignFlag();                              //get value of sign flag

            if (flag) {
                System.out.println("SF: 1");
                dis.Set_DisPcb("\nSF: 1", 1);
            } else {
                System.out.println("SF: 0");
                dis.Set_DisPcb("\nSF: 0", 1);
            }

            flag = register.getOverflowFlag();                              //get value of overflow flag

            if (flag) {
                System.out.println("OF: 1");
                dis.Set_DisPcb("\nOF: 1", 1);
            } else {
                System.out.println("OF: 0");
                dis.Set_DisPcb("\nOF: 0", 1);
            }
        } else {
            dis.Set_DisPcb("\nSorry This Process Is blocked", 1);
        }
    }

    void Display_Reg(DisplayCommandScreen dis) {
        register.showGeneralPurposeRegisters();
        register.showSpecialPurposeRegisters();
        dis.Set_DisReg("**************** GENERAL PURPOSE REGISTERS ********************************\n", 1);
        dis.Set_DisReg("---------------------------------------------------------------------------\n", 1);
        for (int i = 0; i < 16; i++) {
            dis.Set_DisReg("Reg[ " + i + " ] = " + Integer.toHexString(((short) (register.getGeneralPurposeRegister(i)))) + "\n ", 1);
        } //end of loop
        dis.Set_DisReg("---------------------------------------------------------------------------\n", 1);
        dis.Set_DisReg("\n**************** SPECIAL PURPOSE REGISTERS ********************************", 1);
        dis.Set_DisReg("\n\nRegister[0]: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(0))), 1);
        dis.Set_DisReg("\nCode Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(1))), 1);
        dis.Set_DisReg("\nCode Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(2))), 1);
        dis.Set_DisReg("\nCode Counter(PC): " + Integer.toHexString((short) (register.getSpecialPurposeRegister(3))), 1);
        dis.Set_DisReg("\nStack Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(4))), 1);
        dis.Set_DisReg("\nStack Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(5))), 1);
        dis.Set_DisReg("\nStack Counter: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(6))), 1);
        dis.Set_DisReg("\nData Base: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(7))), 1);
        dis.Set_DisReg("\nData Limit: " + Integer.toHexString((short) (register.getSpecialPurposeRegister(8))), 1);
        boolean flag = register.getCarryFlag();                      //get value of carry flag

        if (flag) {
            System.out.println("CF: 1");
            dis.Set_DisReg("\nCF: 1", 1);
        } else {
            System.out.println("CF: 0");
            dis.Set_DisReg("\nCF:0", 1);
        }
        flag = register.getZeroFlag();                              //get value of zero flag

        if (flag) {
            System.out.println("ZF: 1");
            dis.Set_DisReg("\nZF: 1", 1);
        } else {
            System.out.println("ZF: 0");
            dis.Set_DisReg("\nZF: 0", 1);
        }

        flag = register.getSignFlag();                              //get value of sign flag

        if (flag) {
            System.out.println("SF: 1");
            dis.Set_DisReg("\nSF: 1", 1);
        } else {
            System.out.println("SF: 0");
            dis.Set_DisReg("\nSF: 0", 1);
        }

        flag = register.getOverflowFlag();                              //get value of overflow flag

        if (flag) {
            System.out.println("OF: 1");
            dis.Set_DisReg("\nOF: 1", 1);
        } else {
            System.out.println("OF: 0");
            dis.Set_DisReg("\nOF: 0", 1);
        }
    }

    void Display_PT(byte id, DisplayCommandScreen dis) {
        int add = Get_Add(id);
        if (add != 0) {
            memory.Set_PCB_To_Reg(add + 7);
//        memory.Show_PT(display);
            int l = 0;
            l = register.getPCB() + 65 + (l * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Page Table of Data: " + memory.Get_Mem_Val(l));
                dis.Set_DisPt("Page Table of Data: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_DisPt("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) + "\n", 1);
                }
            }

            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Page Table of Code: ");
                dis.Set_DisPt("\n\nPage Table of Code: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_DisPt("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) + "\n", 1);
                }
            }
        } else {
            dis.Set_DisPt("\nSorry This process is Blocked", 1);
        }
    }

    String Get_File_Name(int add) throws UnsupportedEncodingException {
        int tmp = memory.Get_Mem_Val(add + 65) + memory.Get_Mem_Val(add + 66 + (memory.Get_Mem_Val(add + 65) * 2));
        int loop = memory.Get_Mem_Val(add + 67 + (tmp * 2) + 2);
        byte arr[] = new byte[loop];
        for (int i = 0; i < loop; i++) {
            arr[i] = memory.Get_Mem_Val(add + 68 + (tmp * 2) + 2 + i);
        }
        String name;
        name = new String(arr, "UTF-8");
        System.out.println(name);
        return name;
    }

    void Dump_Mem(byte id, DisplayCommandScreen dis) throws FileNotFoundException, UnsupportedEncodingException {

        int add = Get_Add(id);
        if (add != 0) {
            memory.Set_PCB_To_Reg(add + 7);
            System.out.println(add + " " + id);
            String name = Get_File_Name(add) + ".dump";
            File f = new File(name);
            PrintWriter op = new PrintWriter(name);
            if (f.length() != 0) {
                op.write("");
            }

            op.write("Kernal: " + memory.Get_Mem_Val(add));
            op.write("Code: " + Math.ceil(register.getCodeLimit() / 128.0));
            op.write("Data: " + Math.ceil(register.getDataLimit() / 128.0));
            op.write("Stack: 1");

            dis.Set_MemDump("\nKernal: " + memory.Get_Mem_Val(add) + "\n", 1);
            dis.Set_MemDump("Code: " + Math.ceil(register.getCodeLimit() / 128.0) + "\n", 1);
            dis.Set_MemDump("Data: " + Math.ceil(register.getDataLimit() / 128.0) + "\n", 1);
            dis.Set_MemDump("Stack: 1", 1);

            int total = (int) (memory.Get_Mem_Val(add) + Math.ceil(register.getDataLimit() / 128.0) + Math.ceil(register.getCodeLimit() / 128.0) + 1);
            op.write("Total Allocated Frames: " + total);
            dis.Set_MemDump("\nTotal Allocated Frames: " + total + "\n", 1);

            int l = 0;
            l = register.getPCB() + 65 + (l * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                dis.Set_MemDump("Data Segment: \n", 1);
                op.write("Data Segment: ");
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    int address;
                    address = memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1));
                    for (int s = 0; s < 128 && (s < register.getDataLimit()); s++) {
                        dis.Set_MemDump("" + memory.Get_Mem_Val(address + s) + " ", 1);
                        op.write("" + memory.Get_Mem_Val(address + s) + " ");
                    }
                    dis.Set_MemDump("\n", 1);
                    op.write("\n");
                }
            }

            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                dis.Set_MemDump("\n\nCode Segment: \n", 1);
                op.write("Code Segment: ");
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    int address;
                    address = memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1));
                    for (int s = 0; s < 128 && (s < register.getCodeLimit()); s++) {
                        dis.Set_MemDump("" + memory.Get_Mem_Val(address + s) + " ", 1);
                        op.write("" + memory.Get_Mem_Val(address + s) + " ");
                    }
                    dis.Set_MemDump("\n", 1);
                    op.write("\n");

                }
            }
            dis.Set_MemDump("\n\nStack Segment: \n", 1);
            op.write("Stack Segment: ");
            for (int s = 0; s < 50; s++) {
                dis.Set_MemDump("" + memory.Get_Mem_Val(register.getStackBase() + s) + " ", 1);
                op.write("" + memory.Get_Mem_Val(register.getStackBase() + s) + " ");
            }
            dis.Set_MemDump("\n", 1);
            op.write("\n");

            op.close();
        } else {
            dis.Set_MemDump("\n Sorry This Process is Blocked ", 1);
        }
        // memory.Show_PT();
    }

    int Get_Add(byte id) {
        int l, add = 0;
        boolean chk = false;

        l = register.getQ1Counter();
        for (int k = 0; k < l && !chk; k++) {
            add = queue.dePQueue1() * 128;
            if (id == memory.Get_Mem_Val(add + 6)) {
                //    System.out.println("Found in "+ add +"With ID: "+id);
                chk = true;
            }
            queue.enPQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
        }
        if (!chk) {
            l = register.getQ2Counter();
            for (int k = 0; k < l && !chk; k++) {
                add = queue.deRQueue() * 128;
                if (id == memory.Get_Mem_Val(add + 6)) {
                    chk = true;
                }
                queue.enRQueue(memory.Get_Mem_Val(add + 5), (byte) (add / 128));
            }
        }
        if (!chk) {
            add = queue.deRunningQueue() * 128;
            if (id == memory.Get_Mem_Val(add + 6)) {
                chk = true;
            }
            queue.enRunningQueue((byte) (add / 128));
        }

        /*   if (!chk) {
         l = memory.Get_R_Counter();
         for (int k = 0; k < l && !chk; k++) {
         add = queue.deBQueue() * 128;
         if (id == memory.Get_Mem_Val(add + 6)) {
         chk = true;
         }
         queue.enBQueue((byte) (add / 128));
         }
         }*/
        return add;
    }

    void Print_Info() {
        DashboardScreen.Info.setText(null);
        DashboardScreen.Info.append("\t Memory Information\n\n");
        //  System.out.println("Total Memory: 64 K ("+(512*128)+" Bytes ) ");
        DashboardScreen.Info.append("Total Memory: 64 K (" + (512 * 128) + " Bytes ) \n");

        // System.out.println("  -> Free Kernal Memory: "+(126-register.getKmCounter())+" Frames\n");
        DashboardScreen.Info.append("  -> Free Kernal Memory: " + (126 - register.getKmCounter()) + " Frames\n");

        //  System.out.println("  -> Free User Memory:   "+(384-register.getMmCounter())+" Frames");
        DashboardScreen.Info.append("  -> Free User Memory:   " + (384 - register.getMmCounter()) + " Frames\n");

        DashboardScreen.Info.append("\n\n\t Process Information\n\n");
        DashboardScreen.Info.append("Maximum Processes: 63 \n");
        DashboardScreen.Info.append("  -> Current Loaded Processes: " + (register.getQ1Counter() + register.getQ2Counter() + memory.Get_R_Counter()) + "\n");
        // System.out.println("  -> Current Loaded Processes: "+(register.getQ1Counter()+register.getQ2Counter()+memory.Get_R_Counter())+"");

        DashboardScreen.Info.append("  -> Processes in Ready State: " + (register.getQ1Counter() + register.getQ2Counter()) + "\n");
        //System.out.println("  -> Processes in Ready State: "+(register.getQ1Counter()+register.getQ2Counter())+"");

        DashboardScreen.Info.append("  -> Processes in Blocked State: " + (memory.Get_R_Counter()) + "\n");
        // System.out.println("  -> Processes in Blocked State: "+(memory.Get_R_Counter())+"");     
    }

    void Mem_Details(DisplayCommandScreen dis) {
        byte id;
        int c = DashboardScreen.PIDs.getItemCount();
        for (int i1 = 0; i1 < c; i1++) {
            id = (byte) Integer.parseInt((String) DashboardScreen.PIDs.getItemAt(i1));
            System.out.println("PID: " + id);
            dis.Set_MemDetails("PID: " + id + "\n", 1);

            int add = Get_Add(id);
            memory.Set_PCB_To_Reg(add + 7);

            System.out.println("Kernal: " + register.getPCB());
            dis.Set_MemDetails("\nKernal: " + register.getPCB() + "\n", 1);
//        memory.Show_PT(display);
            int l = 0;
            l = register.getPCB() + 65 + (l * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Data Segment: " + memory.Get_Mem_Val(l));
                dis.Set_MemDetails("Data Segment: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_MemDetails("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) + "\n", 1);
                }
            }

            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Code Segment: ");
                dis.Set_MemDetails("\nCode Segment: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)));
                    dis.Set_MemDetails("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) + "\n", 1);
                }
            }
            System.out.println("Stack Segment: ");
            dis.Set_MemDetails("\nStack Segment: 1\n", 1);
            System.out.println("" + register.getStackBase());
            dis.Set_MemDetails("" + register.getStackBase() + "\n\n", 1);

            int tmp = memory.Get_Mem_Val(register.getPCB() + 65) + memory.Get_Mem_Val(register.getPCB() + 66 + (memory.Get_Mem_Val(register.getPCB() + 65) * 2));
            int p = register.getPCB() + 67 + (tmp * 2);

            System.out.println("Process Size: " + memory.bytesToInt(memory.Get_Mem_Val(p), memory.Get_Mem_Val(p + 1)));
            dis.Set_MemDetails("Process Size: " + memory.bytesToInt(memory.Get_Mem_Val(p), memory.Get_Mem_Val(p + 1)) + "\n\n", 1);
        }
    }

    void Allocated_Frames(DisplayCommandScreen dis) {
        byte id;
        int c = DashboardScreen.PIDs.getItemCount();
        for (int i1 = 0; i1 < c; i1++) {
            id = (byte) Integer.parseInt((String) DashboardScreen.PIDs.getItemAt(i1));
            System.out.println("PID: " + id);
            dis.Set_AllotFrames("PID: " + id + "\n", 1);

            int add = Get_Add(id);
            memory.Set_PCB_To_Reg(add + 7);

            System.out.println("Kernal: " + register.getPCB());
            dis.Set_AllotFrames("\nKernal: " + register.getPCB() / 128 + "\n", 1);
//        memory.Show_PT(display);
            int l = 0;
            l = register.getPCB() + 65 + (l * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Data Segment: " + memory.Get_Mem_Val(l));
                dis.Set_AllotFrames("Data Segment: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) / 128);
                    dis.Set_AllotFrames("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) / 128 + "\n", 1);
                }
            }

            l = register.getPCB() + 66 + (memory.Get_Mem_Val(l) * 2);
            if (memory.Get_Mem_Val(l) != 0) {
                System.out.println("Code Segment: ");
                dis.Set_AllotFrames("\nCode Segment: " + memory.Get_Mem_Val(l) + "\n", 1);
                for (int i = 0, k = 1; i < memory.Get_Mem_Val(l); i++, k += 2) {
                    System.out.println(memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) / 128);
                    dis.Set_AllotFrames("" + memory.bytesToInt(memory.Get_Mem_Val(l + k), memory.Get_Mem_Val(l + k + 1)) / 128 + "\n", 1);
                }
            }
            System.out.println("Stack Segment: ");
            dis.Set_AllotFrames("\nStack Segment: 1\n", 1);
            System.out.println("" + register.getStackBase() / 128);
            dis.Set_AllotFrames("" + register.getStackBase() / 128 + "\n\n", 1);
        }
    }

    void Shut_Down() {
        /* byte id;
         int c = DashboardScreen.PIDs.getItemCount();
         for (int i = 0; i < c; i++) {
         id = (byte) Integer.parseInt((String) DashboardScreen.PIDs.getItemAt(i));
         int add = Get_Add(id);
         memory.Set_PCB_To_Reg(add + 7);
         memory.Deallocate();
         queue.updatePID();
         Print_Info();
         }*/
        while (register.getQ1Counter() != 0) {
            memory.Set_PCB_To_Reg((queue.dePQueue1() * 128) + 7);
            memory.Deallocate();
        }
        while (register.getQ2Counter() != 0) {
            memory.Set_PCB_To_Reg((queue.deRQueue() * 128) + 7);
            memory.Deallocate();
        }
        while (memory.Get_R_Counter() != 0) {
            memory.Set_PCB_To_Reg((queue.deBQueue() * 128) + 7);
            memory.Deallocate();
        }

    }
}
