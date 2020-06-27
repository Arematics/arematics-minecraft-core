package com.arematics.minecraft.core.hooks;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class PreFileExistHook implements Hook<String>{

    private File mcfile;
    private ClassLoader loader;

    @Override
    public void startHook(ClassLoader loader, JavaPlugin plugin) {
        this.loader = loader;
        Set<String> files = startPreProcessor(loader, plugin);
        this.mcfile = new File("plugins/" + plugin.getName() + "/");
        if(!this.mcfile.exists()){
            this.mcfile.mkdir();
        }

        files.forEach(f -> processAction(f, plugin));
    }

    @Override
    public Set<String> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections("copyable", new ResourcesScanner());
        return reflections.getResources(Pattern.compile(".*"));
    }
    @Override
    public void processAction(String file, JavaPlugin plugin) {
        file = file.substring(file.indexOf("/") + 1);
        String name = file.contains("/") ? file.substring(file.lastIndexOf("/") + 1) : file;

        boolean mirrored = isMirrored(name, this.mcfile);
        if(!mirrored){
            if(file.contains("/")){
                dirProcessor(file, 0);
            }
            File nfile = new File(this.mcfile + "/" + file);
            try(InputStream in = this.loader.getResourceAsStream("copyable/" + file);
                FileOutputStream out = new FileOutputStream(nfile)){
                nfile.createNewFile();
                final byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1)
                    out.write(buffer, 0, n);
                out.flush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void dirProcessor(String path, int start){
        if(path.contains("/")){
            if(path.substring(start).contains("/")) {
                int i = path.indexOf("/", start);
                String root = path.substring(start, i);
                if (!isMirrored(root, this.mcfile)) {
                    File nfile = new File(this.mcfile + "/" + root);
                    nfile.mkdir();
                }
                dirProcessor(path, i + 1);
            }
        }
    }

    private boolean isMirrored(String name, File dir){
        return FileUtils.listFiles(dir, null, true).stream().anyMatch(f2 -> f2.getName().equals(name));
    }
}
