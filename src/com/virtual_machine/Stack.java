package com.virtual_machine;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * @author arsalan
 */
public class Stack {

    private final Memory memory;
    private final Register register;

    Stack(Memory memory, Register register) throws FileNotFoundException, IOException {
        this.memory = memory;
        this.register = register;
    }
    //function to PUSH values in stack

    boolean PUSH(short val) {
        if (register.getStackCounter() < register.getStackLimit() - 1) {
            memory.Set_Mem(register.getStackBase() + register.getStackCounter(), (byte) ((val >> 8) & 0xFF));
            register.setStackCounter(register.getStackCounter() + 1);
            memory.Set_Mem(register.getStackBase() + register.getStackCounter(), (byte) (val & 0xFF));
            register.setStackCounter(register.getStackCounter() + 1);
            System.out.println("PUSHing the Value of Register  in Stack..");
            return true;
        } else {
            return false;
        }
    } //end of function

    //function to POP values from stack
    short POP() {
        return (short) memory.bytesToInt((memory.Get_Mem_Val(register.getStackBase() + register.getStackCounter())), (memory.Get_Mem_Val(register.getStackBase() + register.getStackCounter() - 1)));
    } //end of function

    //function to show values in stack
    void Show_Stack() {
        System.out.print("Stack: ");

        for (int i = register.getStackBase(); i < register.getStackLimit(); i++) {
            System.out.printf("%02x,", memory.Get_Mem_Val(i));
        }//end of loop

        System.out.println();
    } //end of function

    void updateStack() {
        DashboardScreen.Stack.setText(null);
        DashboardScreen.Stack.append("\t     STACK \n");

        DashboardScreen.Stack.append("\n\n\t");
        System.out.println(register.getStackCounter());
        for (int i = register.getStackBase(); i < register.getStackBase() + register.getStackCounter(); i++) {
            if (i % 10 == 0) {
                DashboardScreen.Stack.append("\n\t");
            }

            DashboardScreen.Stack.append("" + memory.Get_Mem_Val(i) + " ");
        }
    }
}
