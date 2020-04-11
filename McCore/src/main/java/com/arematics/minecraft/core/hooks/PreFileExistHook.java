package com.arematics.minecraft.core.hooks;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class PreFileExistHook {

    public static void checkPluginFiles(ClassLoader loader, JavaPlugin plugin){
        System.out.println("Starting File Create Hooks");
        Collection<File> files = FileUtils.listFiles(new File(loader.getResource("copyable/").getFile()),
                null, false);
        File file = new File("plugins/" + plugin.getName() + "/");
        if(!file.exists()){
            System.out.println("Creating Plugin File");
            file.mkdir();
        }

        checkFolder(files, file);
    }

    private static void checkFolder(Collection<File> files, File jplugin){
        for(File f : files){
            System.out.println(f.getName());
            if(!f.getName().contains("plugin.yml")){
                boolean mirrored = isMirrored(f, jplugin);
                if(!mirrored){
                    File nfile = new File(jplugin + "/" + f.getName());
                    System.out.println(nfile.getName());
                    if(f.isDirectory()){
                        nfile.mkdir();
                    }
                    else {
                        try{
                            nfile.createNewFile();
                            InputStream in = new FileInputStream(f);
                            FileOutputStream out = new FileOutputStream(nfile);
                            final byte[] buffer = new byte[1024];
                            int n;
                            while ((n = in.read(buffer)) != -1)
                                out.write(buffer, 0, n);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static boolean isMirrored(File file, File dir){
        if(dir.listFiles() != null){
            return Arrays.stream(Objects.requireNonNull(dir.listFiles())).anyMatch(f2 -> f2.getName().equals(file.getName()));
        }

        return false;
    }
}
