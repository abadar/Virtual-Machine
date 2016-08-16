package com.virtual_machine;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Priority Queue
 *
 * @author arsalan
 */
public class PQueue {

    private final Memory memory;
    private final Register register;

    public PQueue(Memory memory, Register register) throws FileNotFoundException, IOException, NumberFormatException {
        this.memory = memory;
        this.register = register;
    }

    void showRQueue() {
        System.out.println("Queue2: ");
        for (int i = 192; i < 254; i++) {
            System.out.print(memory.Get_Mem_Val(i) + " ");
        }
        System.out.print("\n");
    }

    void showRunningQueue() {
        System.out.println("Ready Queue: ");
        System.out.println(memory.Get_Mem_Val(254) + " " + memory.Get_Mem_Val(255));
    }

    void showPQueue() {
        System.out.println("Queue1: ");
        //  display.Set_Listp(null, 0);
        for (int i = 128; i < 192; i++) {
            System.out.print(memory.Get_Mem_Val(i) + " ");
            //   StringBuilder sb = new StringBuilder();
            //   sb.append("");
            //   sb.append(memory.Get_Mem_Val(i));
            //   String strI = sb.toString();
            //  display.Set_Listp(strI, 1);
        }
        System.out.print("\n");
    }

    void enPQueue1(byte p, byte d) {
        int i, j;
        byte t1, t2;
        boolean add = false;
        ///  enRunningQueue((byte)0,(byte)0);

        i = 128;

        while ((i < 192) && !add) {
            if (memory.Get_Mem_Val(i) == 32) {
                memory.Set_Mem(i, (byte) p);
                memory.Set_Mem(i + 1, (byte) d);
                add = true;
                //System.out.print ("Added\n");
            } else {
                i += 2;
            }
        }

        if (add) {
            for (i = 128; i < 192; i += 2) {
                for (j = 128; j + 3 < 192; j += 2) {
                    if (memory.Get_Mem_Val(j) > memory.Get_Mem_Val(j + 2)) {
                        t1 = memory.Get_Mem_Val(j);
                        t2 = memory.Get_Mem_Val(j + 1);
                        memory.Set_Mem(j, memory.Get_Mem_Val(j + 2));
                        memory.Set_Mem(j + 1, memory.Get_Mem_Val(j + 3));
                        memory.Set_Mem(j + 2, t1);
                        memory.Set_Mem(j + 3, t2);
                        //System.out.print ("Sorting\n");
                    }
                }
            }
        }
        register.incrementQ1Counter();
    }

    byte dePQueue1() {
        byte temp = memory.Get_Mem_Val(129), t1, t2;
        // enRunningQueue(memory.Get_Mem_Val(128),memory.Get_Mem_Val(129));

        boolean sorted = false;
        int i = 128, j = 130;

        memory.Set_Mem(128, (byte) 32);
        memory.Set_Mem(129, (byte) 0);

        while ((j + 1 < 192) && !sorted) {
            if (memory.Get_Mem_Val(j) != 32) {
                t1 = memory.Get_Mem_Val(i);
                t2 = memory.Get_Mem_Val(i + 1);
                memory.Set_Mem(i, memory.Get_Mem_Val(j));
                memory.Set_Mem(i + 1, memory.Get_Mem_Val(j + 1));
                memory.Set_Mem(j, t1);
                memory.Set_Mem(j + 1, t2);

                i += 2;
                j += 2;
            } else {
                sorted = true;
            }
        }
        register.decrementQ1Counter();
        return temp;
    }

    void enPQueue2(byte p, byte d) {
        int i, j;
        byte t1, t2;
        boolean add = false;

        // enRunningQueue((byte)0,(byte)0);
        i = 192;

        while ((i < 254) && !add) {
            if (memory.Get_Mem_Val(i) == 32) {
                memory.Set_Mem(i, p);
                memory.Set_Mem(i + 1, d);
                add = true;
            } else {
                i += 2;
            }
        }
        if (add) {
            for (i = 192; i < 254; i += 2) {
                for (j = 192; j + 3 < 254; j += 2) {
                    if (memory.Get_Mem_Val(j) > memory.Get_Mem_Val(j + 2)) {
                        t1 = memory.Get_Mem_Val(j);
                        t2 = memory.Get_Mem_Val(j + 1);
                        memory.Set_Mem(j, memory.Get_Mem_Val(j + 2));
                        memory.Set_Mem(j + 1, memory.Get_Mem_Val(j + 3));
                        memory.Set_Mem(j + 2, t1);
                        memory.Set_Mem(j + 3, t2);
                        //System.out.print ("Sorting\n");
                    }
                }
            }
        }
        register.incrementQ2Counter();
    }

    void enRQueue(byte p, byte a1) {
        int i;
        boolean added = false;
        //   enRunningQueue((byte)0,(byte)0);
        for (i = 192; i < 254 && !added; i += 2) {
            if (memory.Get_Mem_Val(i) == 32) {
                memory.Set_Mem(i, p);
                memory.Set_Mem(i + 1, a1);
                added = true;
            }
        }
        register.incrementQ2Counter();
    }

    byte deRQueue() {
        byte t1, t2, t3;
        boolean sorted = false;
        int i = 192, j = 194;
        byte tmp;

        tmp = memory.Get_Mem_Val(193);
        //enReadyQueue(memory.Get_Mem_Val(192),memory.Get_Mem_Val(193));

        memory.Set_Mem(192, (byte) 32);
        memory.Set_Mem(193, (byte) 0);

        while ((j + 2 < 254) && !sorted) {
            if (memory.Get_Mem_Val(j) != 32) {
                t1 = memory.Get_Mem_Val(i);
                t2 = memory.Get_Mem_Val(i + 1);

                memory.Set_Mem(i, memory.Get_Mem_Val(j));
                memory.Set_Mem(i + 1, memory.Get_Mem_Val(j + 1));

                memory.Set_Mem(j, t1);
                memory.Set_Mem(j + 1, t2);

                i += 2;
                j += 2;
            } else {
                sorted = true;
            }
        }
        register.decrementQ2Counter();
        return tmp;
    }

    boolean test(byte val) {
        boolean chk = false;
        int tmp = 0, tmp2 = 0, tmp3 = 0;

        if (register.getQ1Counter() > 0) {
            tmp = register.getQ1Counter();
            while (tmp != 0) {
                tmp2 = dePQueue1() * 128;

                if (val == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                }
                enPQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                tmp--;
            }
        }
        if (!chk && register.getQ2Counter() > 0) {
            tmp = register.getQ2Counter();
            while (tmp != 0) {
                tmp2 = deRQueue() * 128;
                if (val == memory.Get_Mem_Val(tmp2 + 6)) {
                    chk = true;
                }

                enRQueue(memory.Get_Mem_Val(tmp2 + 5), (byte) (tmp2 / 128));
                tmp--;
            }
        }
        if (!chk) {
            int count = memory.Get_R_Counter();
            int i1;
            for (int i = 0; i < count; i++) {
                i1 = deBQueue() * 128;
                if (val == memory.Get_Mem_Val(i1 + 6)) {
                    chk = true;
                }
                enBQueue((byte) (i1 / 128));
            }
        }// }while(i!=0 && !chk);
        if (!chk) {
            int i1 = deRunningQueue() * 128;
            if (val == memory.Get_Mem_Val(i1 + 6)) {
                chk = true;
            }
            enRunningQueue((byte) (i1 / 128));
        }

        return chk;
    }

    void enPQueue(byte p, byte a1) {
        int i;
        boolean added = false;
        //enReadyQueue((byte)0,(byte)0);
        for (i = 128; i < 192 && !added; i += 2) {
            if (memory.Get_Mem_Val(i) == 32) {
                memory.Set_Mem(i, p);
                memory.Set_Mem(i + 1, a1);
                added = true;
            }
        }
        register.incrementQ1Counter();
    }

    void enRunningQueue(byte d) {
        memory.Set_Mem(254, d);
        // memory.Set_Mem(255, d);
    }

    byte deRunningQueue() {
        byte t = memory.Get_Mem_Val(254);
        memory.Set_Mem(254, (byte) 0);
        //mem.Set_Mem(255, (byte)0);
        return t;
    }

    void enBQueue(byte n) {
        int i = 64;
        boolean add = false;
        memory.Inc_R_Counter();
        while ((i < 128) && !add) {
            if (memory.Get_Mem_Val(i) == 0) {
                memory.Set_Mem(i, n);//arr[i] = n;
                add = true;
            } else {
                i++;
            }
        }
    }

    byte deBQueue() {
        boolean sorted = false;
        int i = 64;
        byte temp, t;
        memory.Dec_R_Counter();
        temp = memory.Get_Mem_Val(64);
        memory.Set_Mem(64, (byte) 0);//arr[0] = 0;

        while ((i + 1 < 128) && !sorted) {
            if (memory.Get_Mem_Val(i + 1) != 0) {
                t = memory.Get_Mem_Val(i);
                memory.Set_Mem(i, memory.Get_Mem_Val(i + 1));//  arr[i] = arr[i + 1];
                memory.Set_Mem(i, t);
                //arr[i + 1] = t;
                i++;
            } else {
                sorted = true;
            }
        }

        return temp;
    }

    void showBQueue() {
        // int i = 64;
        for (int i = 64; i < 128; i++)//mem.Get_Mem_Val(i)!=0 && i < 128)
        {
            System.out.print(memory.Get_Mem_Val(i) + " ");
        }
        System.out.println();
    }

    void updatePID() {
        DashboardScreen.PIDs.removeAllItems();
        int l = register.getQ1Counter();

        for (int i = 0; i < l; i++) {
            int k = dePQueue1() * 128;
            //       sb.append("");
            //sb.append(memory.Get_Mem_Val(k+6));
            //  sb.append(memory.Get_Mem_Val(k+6));
            String strI = Integer.toString(memory.Get_Mem_Val(k + 6));
            DashboardScreen.PIDs.addItem(strI);
            enPQueue(memory.Get_Mem_Val(k + 5), (byte) (k / 128));
        }
        l = register.getQ2Counter();
        for (int i = 0; i < l; i++) {
            int k = deRQueue() * 128;
            // sb.append("");
            // sb.append(memory.Get_Mem_Val(k+6));
            // sb.append(memory.Get_Mem_Val(k+6));
            String strI = Integer.toString(memory.Get_Mem_Val(k + 6));
            DashboardScreen.PIDs.addItem(strI);
            enRQueue(memory.Get_Mem_Val(k + 5), (byte) (k / 128));
        }
        l = memory.Get_R_Counter();
        for (int i = 0; i < l; i++) {
            int k = deBQueue() * 128;
            //sb.append("");
            //   sb.append(memory.Get_Mem_Val(k+6));
            //sb.append(memory.Get_Mem_Val(k+6));
            String strI = Integer.toString(memory.Get_Mem_Val(k + 6));
            DashboardScreen.PIDs.addItem(strI);
            enBQueue((byte) (k / 128));
        }
        DashboardScreen.Runp.setEnabled(false);
        DashboardScreen.DebugP.setEnabled(false);
        DashboardScreen.KillP.setEnabled(false);
        DashboardScreen.UnBlock.setEnabled(false);
        DashboardScreen.Clone.setEnabled(false);
        DashboardScreen.Block.setEnabled(false);
        DashboardScreen.DisplayP.setEnabled(false);
        DashboardScreen.DisplayM.setEnabled(false);
        DashboardScreen.Dump.setEnabled(false);

        if (DashboardScreen.PIDs.getItemCount() == 0) {
            DashboardScreen.RunA.setEnabled(false);
            DashboardScreen.DebugA.setEnabled(false);
            DashboardScreen.MemDetails.setEnabled(false);
            DashboardScreen.FramesA.setEnabled(false);
            DashboardScreen.PrintProcess.setEnabled(false);
        }
    }
}
