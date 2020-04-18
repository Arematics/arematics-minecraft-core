package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.language.LanguageAPI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class PreFileExistHook implements Hook<Collection<File>>{

    private File mcfile;

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        System.out.println("Starting File Create Hooks");
        Set<Collection<File>> files = startPreProcessor(loader, plugin);
        this.mcfile = new File("plugins/" + plugin.getName() + "/");
        if(!this.mcfile.exists()){
            System.out.println("Creating Plugin File");
            this.mcfile.mkdir();
        }

        files.forEach(f -> processAction(f, plugin));
    }

    @Override
    public Set<Collection<File>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        return new HashSet<Collection<File>>(){{
            add(FileUtils.listFiles(new File(loader.getResource("copyable/").toExternalForm()),
                    null, true));
        }};
    }

    @Override
    public void processAction(Collection<File> files, JavaPlugin plugin) {
        for(File f : files){
            System.out.println(f.getName());
            if(!f.getName().contains("plugin.yml")){
                boolean mirrored = isMirrored(f, this.mcfile);
                if(!mirrored){
                    File nfile = new File(this.mcfile + "/" + f.getName());
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

    private boolean isMirrored(File file, File dir){
        if(dir.listFiles() != null){
            return Arrays.stream(Objects.requireNonNull(dir.listFiles())).anyMatch(f2 -> f2.getName().equals(file.getName()));
        }

        return false;
    }
}
