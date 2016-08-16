package com.virtual_machine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class Memory {
    public byte memory[];
    private Register Reg;
    PQueue queue;
    InstructionDecoder fetch;
    Stack stack;

    Memory() throws FileNotFoundException, IOException {
        this.memory = new byte[65536];                             //64K memory
        this.Reg = new Register();

        for (int i = 128; i < 192; i += 2) // Priority Queue1 (0-15)                                      
        {
            this.memory[i] = 32;                                            //for each priority 8 bits are used  
        }
        for (int i = 192; i < 254; i += 2) // Priority Queue2 (16-31)
        {
            this.memory[i] = 32;                                            //for each priority 8 bits are used
        }
        set_frame(0);
        set_frame(1);
    }
    void Initialize(Memory mem1, Register R, PQueue queue, InstructionDecoder f, Stack st)
    {
        this.Reg = R;
        this.queue = queue;
        this.fetch = f;
        this.stack = st;
        
    }
    //function to store in memory from file and return total no of elements
    void Store_Mem(Memory mem1, Register R, PQueue queue, String FileName, String link, InstructionDecoder f, Stack st) throws FileNotFoundException, IOException, NumberFormatException {
        //System.setOut(new PrintStream(new File("output.txt")));
        try (InputStream inp = new FileInputStream(link)) {                    //input from file surrounded with try-catch
            boolean check = true, q1 = false, q2 = false;
            byte PP = (byte) inp.read(), PID;
            PID = (byte) inp.read();

            if (PP >= 0 && PP <= 31) {
                if (((((((Math.ceil(inp.available() - 6) / 128.0)) * 2) + 69 + FileName.length()) / 128) + 1) + Reg.getKmCounter() > 127) {
                    System.out.println("Cannot Proceed!! No free Frames avalible for this process............");
                    DashboardScreen.Main.append("Cannot Proceed!! No free Frames avalible for this process............\n");
                } else {
                    Reg.setIndex(Get_Free_KM_Frame((int) (((((Math.ceil((inp.available() - 6) / 128.0)) * 2) + 68 + FileName.length()) / 128) + 1)) * 128);
                    if (queue.test(PID)) {
                        System.out.println("Process Id is not Unique. Cannot load the process");
                        DashboardScreen.Main.append("Process Id is not Unique. Cannot load the process\n");
                    } else {
                        System.out.println("Unique ID ");
                        DashboardScreen.Main.append("Unique ID\n");
                        if (PP >= 0 && PP <= 15 && Reg.getQ1Counter() + 1 < 32) {
                            queue.enPQueue1(PP, (byte) (Reg.getIndex() / 128));
                            q1 = true;
                            //enRQueue(PP, (byte) (register.getIndex()/128));
                        } else if (PP >= 0 && PP <= 15 && Reg.getQ1Counter() + 1 >= 32) {
                            System.out.println("No sufficient Space in Queue......s.");
                            DashboardScreen.Main.append("No sufficient Space in Queue......s.\n");
                        } else if (PP >= 16 && PP <= 31 && Reg.getQ2Counter() + 1 < 32) {
                            q2 = true;
                            queue.enPQueue2(PP, (byte) (Reg.getIndex() / 128));
                        } else if (PP >= 16 && PP <= 31 && Reg.getQ2Counter() + 1 >= 32) {
                            System.out.println("No sufficient Space in Queue.......");
                            DashboardScreen.Main.append("No sufficient Space in Queue.......\n");
                        }
                        queue.showPQueue();
                        queue.showRQueue();
                        if (q1 || q2) {
                            Reg.setPCB(Reg.getIndex());
                            Set_Mem(Reg.getIndex(), (byte) (((((Math.ceil((inp.available() - 7) / 128)) * 2) + 67 + FileName.length()) / 128) + 1));
                            Reg.incrementIndex();
                            Reg.incrementIndex();
                            Reg.incrementIndex();
                            Reg.incrementIndex();
                            Reg.incrementIndex();
                            Set_Mem(Reg.getIndex(), PP);
                            Reg.incrementIndex();
                            Set_Mem(Reg.getIndex(), PID);
                            Reg.incrementIndex();
                            Reg.setIndex(Reg.getPCB() + 65);

                            Reg.setStackBase((Get_Free_MM_Frame() * 128));

                            Reg.setStackCounter(0);
                            Reg.setStackLimit((short) ((50)));

                            boolean code = false;

                            for (int k = 0; k < 2 && !code && check; k++) {
                                Reg.setTmpReg1((byte) inp.read());
                                System.out.println("Segment = " + Reg.getTmpReg1());
                                if (Reg.getTmpReg1() == 1 || Reg.getTmpReg1() == 2) {
                                    Reg.setTmpReg2(bytesToInt((byte) inp.read(), (byte) inp.read()));
                                    //  register.setTmpReg2((((byte) inp.read() << 8) | (byte) inp.read()));

                                    if ((k == 0) && (Reg.getTmpReg1() == 2 || (Reg.getTmpReg1() == 1 && Reg.getTmpReg2() == 0))) {
                                        Reg.setDataLimit(0);
                                        Set_Mem(Reg.getIndex(), (byte) 0);
                                        Reg.incrementIndex();
                                        //  register.setDataBase(register.getIndex());
                                    }
                                    if ((Reg.getTmpReg1() == 1 && Reg.getTmpReg2() > 0) && k == 0) {
                                        Reg.setDataLimit(Reg.getTmpReg2());

                                        Set_Mem(Reg.getIndex(), (byte) (Math.ceil(Reg.getDataLimit() / 128.0)));
                                        Reg.incrementIndex();
                                        //----------------------------------------
                                        if (Reg.getMmCounter() + (Math.ceil(Reg.getDataLimit() / 128.0)) > 384) {
                                            System.out.println("No suffecient frames avalible now try again later");
                                            DashboardScreen.Main.append("No suffecient frames avalible now try again later\n");

                                            check = false;
                                        } else {
                                            short temp = (short) Reg.getTmpReg1();
                                            int c = 0;
                                            for (int l = 0; l < (Math.ceil(Reg.getDataLimit() / 128.0)); l++) {

                                                Reg.setTmpReg1((Get_Free_MM_Frame() * 128));
                                                if (l == 0) {
                                                    Reg.setDataBase(Reg.getIndex());
                                                }
                                                Set_Mem(Reg.getIndex(), (byte) (((Reg.getTmpReg1()) >> 8) & 0xFF));
                                                Reg.incrementIndex();
                                                Set_Mem(Reg.getIndex(), (byte) ((Reg.getTmpReg1()) & 0xFF));
                                                Reg.incrementIndex();
                                                Reg.setTmpReg2(Reg.getIndex());
                                                Reg.setIndex(Reg.getTmpReg1());
                                                System.out.println("Data: ");
                                                for (; Reg.getIndex() < Reg.getTmpReg1() + 128 && (c < Reg.getDataLimit()); Reg.incrementIndex()) {
                                                    if (Reg.getIndex() <= 65535) {
                                                        Set_Mem(Reg.getIndex(), (byte) inp.read());
                                                        System.out.print((byte) Get_Mem_Val(Reg.getIndex()) + " ");
                                                        c++;
                                                    }
                                                }
                                                Reg.setIndex(Reg.getTmpReg2());

                                                System.out.println("\nData size = " + Reg.getDataLimit());
                                                System.out.println("Storing data in memory at page " + Reg.getTmpReg1() / 128);
                                                DashboardScreen.Main.append("\nData size = " + Reg.getDataLimit() + "\n");
                                                DashboardScreen.Main.append("Storing data in memory at page " + Reg.getTmpReg1() / 128 + "\n");
                                            }
                                            Reg.setTmpReg1(temp);
                                        }
                                    }
                                    if (k == 0) {
                                        if (Reg.getTmpReg1() != 2) {
                                            Reg.setTmpReg1((byte) inp.read());
                                            System.out.println("Segment = " + Reg.getTmpReg1());
                                            Reg.setTmpReg2(bytesToInt((byte) inp.read(), (byte) inp.read()));
                                            // register.setTmpReg2((((byte) inp.read() << 8) | (byte) inp.read())); 
                                        }
                                    }
                                    if ((Reg.getTmpReg1() == 2 && Reg.getTmpReg2() > 0 && check)) {

                                        Reg.setCodeLimit(Reg.getTmpReg2());
                                        Set_Mem(Reg.getIndex(), (byte) (Math.ceil(Reg.getCodeLimit() / 128.0)));
                                        Reg.incrementIndex();
                                        //----------------------------------------
                                        if (Reg.getMmCounter() + (Math.ceil(Reg.getCodeLimit() / 128.0)) > 384) {
                                            System.out.println("No suffecient frames avalible now try again later");
                                            DashboardScreen.Main.append("No suffecient frames avalible now try again later\n");
                                            check = false;
                                        } else {
                                            Reg.setCodeBase(Reg.getIndex());
                                            Reg.setProgramCounter(0);
                                            code = true;
                                            short temp = (short) Reg.getTmpReg1();
                                            int c = 0;

                                            for (int l = 0; l < (Math.ceil(Reg.getCodeLimit() / 128.0)); l++) {
                                                Reg.setTmpReg1((short) (Get_Free_MM_Frame() * 128));

                                                Set_Mem(Reg.getIndex(), (byte) ((Reg.getTmpReg1() >> 8) & 0xFF));
                                                Reg.incrementIndex();
                                                Set_Mem(Reg.getIndex(), (byte) ((Reg.getTmpReg1()) & 0xFF));
                                                Reg.incrementIndex();
                                                Reg.setTmpReg2(Reg.getIndex());
                                                Reg.setIndex(Reg.getTmpReg1());
                                                for (; Reg.getIndex() < Reg.getTmpReg1() + 128 && (c < Reg.getCodeLimit()); Reg.incrementIndex()) {
                                                    if (Reg.getIndex() <= 65535) {
                                                        Set_Mem(Reg.getIndex(), (byte) inp.read());
                                                        System.out.print(Get_Mem_Val(Reg.getIndex()) + " ");
                                                        c++;
                                                    }
                                                }
                                                Reg.setIndex(Reg.getTmpReg2());
                                                System.out.println("\nCode size = " + Reg.getCodeLimit());
                                                System.out.println("Storing code in memory at page " + Reg.getTmpReg1() / 128);
                                                DashboardScreen.Main.append("\nCode size = " + Reg.getCodeLimit() + "\n");
                                                DashboardScreen.Main.append("Storing code in memory at page " + Reg.getTmpReg1() / 128 + "\n");
                                            }

                                            Reg.setTmpReg1(temp);
                                            Set_Mem(Reg.getIndex(), (byte) (((Reg.getDataLimit() + Reg.getCodeLimit() + 50) >> 8) & 0xFF));
                                            Reg.incrementIndex();
                                            Set_Mem(Reg.getIndex(), (byte) ((Reg.getDataLimit() + Reg.getCodeLimit() + 50) & 0xFF));
                                            Reg.incrementIndex();
                                            Set_Mem(Reg.getIndex(), (byte) FileName.length());
                                            Reg.incrementIndex();
                                            byte[] ByteArray;
                                            ByteArray = FileName.getBytes("UTF-8");

                                            for (int s = 0; s < FileName.length(); s++) {
                                                Set_Mem(Reg.getIndex(), ByteArray[s]);
                                                Reg.incrementIndex();
                                            }
                                            Reg.setIndex(Reg.getPCB() + 7);

                                        }
                                    } else {
                                        System.out.println("Error!!!!!! No Code part Avalibe...");
                                        DashboardScreen.Main.append("Error!!!!!! No Code part Avalibe...\n");
                                        Deallocate_K();
                                        Deallocate_D();
                                        Deallocate_S();
                                        check = false;
                                    }
                                } else {
                                    System.out.println("Error!!!!!! Wrong Segment Number");
                                    DashboardScreen.Main.append("Error!!!!!! Wrong Segment Number\n");
                                    Deallocate_K();
                                    Deallocate_S();
                                    check = false;
                                }
                            }
                            if (inp.available() == 0 && check) {

                                Set_Reg_To_PCB(Reg.getIndex());
                                Show_PCB();
                            } else {

                                System.out.println("File Format is not correct.. cannot load the segments.");
                                DashboardScreen.Main.append("File Format is not correct.. cannot load the segments.\n");
                                Deallocate();
                                if (q1) {
                                    queue.dePQueue1();
                                } else if (q2) {
                                    queue.deRQueue();
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("Wrong Priority.. Cannot Proceed With this process");
                DashboardScreen.Main.append("Wrong Priority.. Cannot Proceed With this process\n");
            }
        } catch (FileNotFoundException s) {                                             //catch exception of file not found
            System.out.println("ERROR!!!!!! No Such File Exist.............");
            DashboardScreen.Main.append("ERROR!!!!!! No Such File Exist.............\n");
        }
        queue.showPQueue();
        queue.showRQueue();
        queue.updatePID();
        fetch.Print_Info();
        Reg.clearRegisters();

    } //end of function Store_Mem()

    // function t set register to PCB
    void Set_Reg_To_PCB(int index) {
        Set_Mem(index, (byte) 0);
        index++;
        Set_Mem(index, (byte) 0);
        index++;

        for (int s = 1; s < 13; s++) {
            Set_Mem(index, (byte) ((Reg.getSpecialPurposeRegister(s) >> 8) & 0xFF));
            index++;
            Set_Mem(index, (byte) (Reg.getSpecialPurposeRegister(s) & 0xFF));
            index++;
        }

        for (int s = 0; s < 16; s++) {
            Set_Mem(index, (byte) ((Reg.getGeneralPurposeRegister(s) >> 8) & 0xFF));
            index++;
            Set_Mem(index, (byte) (Reg.getGeneralPurposeRegister(s) & 0xFF));
            index++;
        }
    }

    // function to show PCB
    void Show_PCB() {
        for (int i = Reg.getPCB(); i < Reg.getPCB() + 128; i++) {
            System.out.print(Get_Mem_Val(i) + " ");
        }
        System.out.println();
    }

    // function to set PCB to register
    void Set_PCB_To_Reg(int index) {
        for (int s = 0; s < 13; s++) {
            Reg.setSpecialPurposeRegister((short) (bytesToInt((Get_Mem_Val(index)), Get_Mem_Val(index + 1))), s);
            index += 2;
        }
        for (int s = 0; s < 16; s++) {
            Reg.setGeneralPurposeRegister((short) (bytesToInt((Get_Mem_Val(index)), Get_Mem_Val(index + 1))), s);
            index += 2;
        }
    }

    //function to get data from respective getProgramCounter() of memory
    byte Get_Mem_Val(int index) {
        if (index >= 0 && index < 65535) {
            return memory[index];
        }

        return 0;
    } //end of function

    //function to set data in memory at specific location
    void Set_Mem(int index, byte val) {
        memory[index] = val;
    } //end of function

    // function to get free kernel frame
    int Get_Free_KM_Frame(int num) {
        Random ran = new Random();
        int n = 0, count = 0;
        boolean found = false;

        while (!found) {
            n = ran.nextInt(126) + 2;
            if (((Get_Mem_Val(n / 8) >>> (n % 8)) & 1) == 0) {
                while (count != num - 1 && ((Get_Mem_Val(n / 8) >>> (n % 8) + count) & 1) == 0) {
                    count++;
                }
            }

            if (count == num - 1) {
                found = true;
            } else {
                count = 0;
            }
        }

        for (count = 0; count < num; count++) {
            Set_Mem(n / 8, (byte) (Get_Mem_Val(n / 8) | (1 << (n % 8) + count)));
            // memory[n / 8] = (byte) (memory[n / 8] | (1 << (n % 8) + count));
            Reg.incrementKmCounter();
        }
        return n;
    }

    //function to get free frame in memory
    int Get_Free_MM_Frame() {
        Random ran = new Random();
        int n = 0;
        boolean found = false;

        while (!found) {
            n = (ran.nextInt(384) + 128);

            if (((Get_Mem_Val(n / 8) >>> (n % 8)) & 1) == 0) {
                Set_Mem(n / 8, (byte) (Get_Mem_Val(n / 8) | (1 << n % 8)));
                // memory[n / 8] = (byte) (memory[n / 8] | (1 << n % 8));
                found = true;
            }
        }
        Reg.incrementMmCounter();
        return n;
    }

    //function to combine 2 bytes to 1 int
    int bytesToInt(byte b1, byte b2) {
        byte[] conversionArray = {0, 0, b1, b2};
        ByteBuffer ok = ByteBuffer.allocate(4);
        ok.order(ByteOrder.BIG_ENDIAN);
        ok.clear();
        ok.put(conversionArray);
        return ok.getInt(0);
    }

    // function to deallocate kernel
    void Deallocate_K() {
        if (((Get_Mem_Val((Reg.getPCB() / 128) / 8) >>> (Reg.getPCB() / 128) % 8) & 1) == 1) {
            for (int i = 0; i < Get_Mem_Val(Reg.getPCB()); i++) {
                Reg.decrementKmCounter();
                reset_frame((Reg.getPCB() + (i * 128)) / 128);
                System.out.println("\nDeallocated Kernal: " + ((Reg.getPCB() + (i * 128)) / 128));
                DashboardScreen.Main.append("\nDeallocated Kernal: " + ((Reg.getPCB() + (i * 128)) / 128) + "\n");
            }
        }
    }
    // function to deallocate code part

    void Deallocate_C() {
        //  if (((Get_Mem_Val((register.getCodeBase() / 128) / 8) >>> (register.getCodeBase() / 128) % 8) & 1) == 1) 
        int c = Reg.getCodeBase();
        for (int i = 0; i < Math.ceil(Reg.getCodeLimit() / 128.0); i++, c += 2) {
            Reg.decrementMmCounter();
            reset_frame(bytesToInt(Get_Mem_Val(Reg.getCodeBase() + i), Get_Mem_Val(Reg.getCodeBase() + i + 1)) / 128);
            System.out.println("Deallocated C: " + bytesToInt(Get_Mem_Val(c), Get_Mem_Val(c + 1)) / 128);
            DashboardScreen.Main.append("Deallocated C: " + bytesToInt(Get_Mem_Val(c), Get_Mem_Val(c + 1)) / 128 + " \n");       // }
        }
    }

    // function to deallocate data part
    void Deallocate_D() {
        //  if (((Get_Mem_Val((register.getDataBase() / 128) / 8) >>> (register.getDataBase() / 128) % 8) & 1) == 1) {
        int c = Reg.getDataBase();
        for (int i = 0; i < Math.ceil(Reg.getDataLimit() / 128.0); i++, c += 2) {
            reset_frame(bytesToInt(memory[Reg.getDataBase() + i], memory[Reg.getDataBase() + i + 1]) / 128);
            if (Reg.getDataLimit() != 0) {
                Reg.decrementMmCounter();
                System.out.println("Deallocated D: " + bytesToInt(Get_Mem_Val(c), Get_Mem_Val(c + 1)) / 128);
                DashboardScreen.Main.append("Deallocated D: " + bytesToInt(Get_Mem_Val(c), Get_Mem_Val(c + 1)) / 128 + " \n");
            }
        }
        // }
    }

    // function to deallocate stack part
    void Deallocate_S() {
        //    if (((Get_Mem_Val((register.getStackBase() / 128) / 8) >>> (register.getStackBase() / 128) % 8) & 1) == 1) {  
        Reg.decrementMmCounter();
        reset_frame(Reg.getStackBase() / 128);
        System.out.println("Deallocated S: " + Reg.getStackBase() / 128);
        DashboardScreen.Main.append("Deallocated S: " + Reg.getStackBase() / 128 + " \n");

        //  }
    }

    void Deallocate() {
        System.out.println("Deallocating PID: " + Get_Mem_Val(Reg.getPCB() + 6));
        DashboardScreen.Main.append("Deallocating PID: " + Get_Mem_Val(Reg.getPCB() + 6) + " \n");

        Deallocate_K();
        Deallocate_D();
        Deallocate_C();
        Deallocate_S();
        queue.updatePID();
        fetch.Print_Info();
        DashboardScreen.Stack.setText(null);
        DashboardScreen.Stack.append("\t     STACK \n");
    }

    //function to set frame
    void set_frame(int num) {
        if (((Get_Mem_Val(num / 8) >>> num % 8) & 1) == 0) {
            Set_Mem(num / 8, (byte) (Get_Mem_Val(num / 8) | (1 << num % 8)));
            //memory[num / 8] = (byte) (memory[num / 8] | (1 << num % 8));
        }
    }

    //function to reset frame
    void reset_frame(int num) {
        if (((Get_Mem_Val(num / 8) >>> num % 8) & 1) == 1) {
            Set_Mem(num / 8, (byte) (Get_Mem_Val(num / 8) & ~(1 << num % 8)));
            // memory[num / 8] = (byte) (memory[num / 8] & ~(1 << num % 8));
        }
    }

    short WaitTime(int index) {
        return (short) bytesToInt(Get_Mem_Val(index + 1), Get_Mem_Val(index + 2));
    }

    short ExeTime(int index) {
        return (short) bytesToInt(Get_Mem_Val(index + 3), Get_Mem_Val(index + 4));
    }

    void Inc_R_Counter() {
        memory[255] += 1;
    }

    void Dec_R_Counter() {
        memory[255] -= 1;
    }

    byte Get_R_Counter() {
        return memory[255];
    }

    void Show_PT(DisplayCommandScreen dis) {
        int l = 0;
        l = Reg.getPCB() + 65 + (l * 2);
        if (Get_Mem_Val(l) != 0) {
            System.out.println("Page Table of Data: " + Get_Mem_Val(l));
            dis.Set_DisPt("Page Table of Data: " + Get_Mem_Val(l) + "\n", 1);
            for (int i = 0, k = 1; i < Get_Mem_Val(l); i++, k += 2) {
                System.out.println(bytesToInt(Get_Mem_Val(l + k), Get_Mem_Val(l + k + 1)));
                dis.Set_DisPt(Integer.toString(bytesToInt(Get_Mem_Val(l + k), Get_Mem_Val(l + k + 1))) + "\n", 1);
            }
        }

        l = Reg.getPCB() + 66 + (Get_Mem_Val(l) * 2);
        if (Get_Mem_Val(l) != 0) {
            System.out.println("Page Table of Code: ");
            dis.Set_DisPt("\n\nPage Table of Code: " + Get_Mem_Val(l) + "\n", 1);
            for (int i = 0, k = 1; i < Get_Mem_Val(l); i++, k += 2) {
                System.out.println(bytesToInt(Get_Mem_Val(l + k), Get_Mem_Val(l + k + 1)));
                dis.Set_DisPt(Integer.toString(bytesToInt(Get_Mem_Val(l + k), Get_Mem_Val(l + k + 1))) + "\n", 1);
            }
        }
        //+65
    }

    void Show_Free_Frames(DisplayCommandScreen dis) {
        dis.Set_FreeFrames(null, 0);
        dis.Set_FreeFrames("Kernal Frames: \n", 1);
        System.out.println("Kernal Frames: " + Reg.getPCB());
        for (int n = 0; n < 128; n++) {
            if (((Get_Mem_Val(n / 8) >>> (n % 8)) & 1) == 0) {
                System.out.println(n);
                dis.Set_FreeFrames("Frame #: " + n + " At Location: " + n * 128 + "\n", 1);
            }
        }
        dis.Set_FreeFrames("\nUser Frames: \n", 1);
        System.out.println("User Frames: ");
        for (int n = 128; n < 512; n++) {
            if (((Get_Mem_Val(n / 8) >>> (n % 8)) & 1) == 0) {
                System.out.println(n);
                dis.Set_FreeFrames("Frame #: " + n + " At Location: " + n * 128 + "\n", 1);
            }
        }
    }

    void Show_Used_Frames(DisplayCommandScreen dis) {
        //dis.Set_AllotFrames(null, i);
        dis.Set_AllotFrames(null, 0);
        dis.Set_AllotFrames("Kernal Frames: \n", 1);
    }
} //end of class memory