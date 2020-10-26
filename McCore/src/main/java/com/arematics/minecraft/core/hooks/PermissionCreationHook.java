package com.arematics.minecraft.core.hooks;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.data.model.Permission;
import com.arematics.minecraft.data.service.PermissionService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Set;

public class PermissionCreationHook extends PackageHook<Class<?>> {

    private String url;
    private ClassLoader classLoader;

    @Override
    void startPathHook(String url, ClassLoader loader, JavaPlugin plugin) {

        try{
            this.url = url;
            this.classLoader = loader;
            Set<Class<?>> classes = startPreProcessor(loader, plugin);
            if(classes.isEmpty())
                plugin.getLogger().info("Could not find any Permissions");
            classes.forEach(classprocess -> processAction(classprocess, plugin));
        }catch (Exception e){
            plugin.getLogger().info("Could not find any Permissions: " + e.getMessage());
        }
    }

    @Override
    public Set<Class<?>> startPreProcessor(ClassLoader loader, JavaPlugin plugin) {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder(this.url, this.classLoader));
        return reflections.getTypesAnnotatedWith(Perm.class);
    }

    @Override
    public void processAction(Class<?> theClass, JavaPlugin plugin) {
        PermissionService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PermissionService.class);
        String classPermission = readElement(service, theClass, "");
        if(!StringUtils.isBlank(classPermission))
            readMethods(service, theClass, classPermission);
    }

    private void readMethods(PermissionService service, Class<?> theClass, String classPermission){
        Arrays.stream(theClass.getMethods()).forEach(method -> readElement(service, method, classPermission + "."));
    }

    private String readElement(PermissionService service, AnnotatedElement element, String prefix){
        if(element.isAnnotationPresent(Perm.class)){
            Perm annotation = element.getAnnotation(Perm.class);
            try{
                service.findByName(prefix + annotation.permission());
                return annotation.permission();
            }catch (RuntimeException runtimeException){
                Permission permission = Permission.of(annotation, prefix);
                service.addPermission(permission);
                return annotation.permission();
            }

        }
        return "";
    }
}
