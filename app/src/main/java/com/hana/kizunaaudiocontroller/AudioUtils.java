package com.hana.kizunaaudiocontroller;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class AudioUtils {

    static String readFromFile(Context context, String filename){
        String line = null;

        try {
            FileInputStream fileInputStream = new FileInputStream (new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),filename));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException fnfe) {
            Log.e("Exception", "File not found " + fnfe.toString());
        }
        catch(IOException ex) {
            Log.e("Exception", "File not found " + ex.toString());
        }

        return line;
    }

    void writeToFile(String data, String value) {
        try
        {
            CommandResult write = Shell.SU.run("echo" + " " + value + " " + ">" + " " + data);
        } catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    void DumpFile(String filename, String local_dir) {
        try
        {
            CommandResult dump = Shell.SU.run("cp" + " " + filename + " " + local_dir);
        } catch (Exception e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
    }

    void DropFile(String local_dir) {
        try
        {
            CommandResult dump = Shell.SU.run("rm" + " " + local_dir);
        } catch (Exception e) {
            Log.e("Exception", "File drop failed: " + e.toString());
        }
    }
}
