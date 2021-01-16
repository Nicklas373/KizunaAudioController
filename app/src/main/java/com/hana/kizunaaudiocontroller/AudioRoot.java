package com.hana.kizunaaudiocontroller;

import java.io.DataOutputStream;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioRoot {
    private final AtomicBoolean execute = new AtomicBoolean();

    public boolean checkRooted()
    {
        try
        {
            Process p = Runtime.getRuntime().exec("su", null, new File("/"));
            DataOutputStream os = new DataOutputStream( p.getOutputStream());
            os.writeBytes("pwd\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void run(Runnable task) {
        if (execute.get()) return;
        if (execute.compareAndSet(false, true)) {
            task.run();
        }
    }
}
