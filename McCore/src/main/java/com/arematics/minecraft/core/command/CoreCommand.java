package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.annotations.Default;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.PermissionAnnotationProcessor;
import com.arematics.minecraft.core.command.processor.SubCommandAnnotationProcessor;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.CommonData;
import com.arematics.minecraft.core.processor.methods.MethodProcessorEnvironment;
import com.arematics.minecraft.core.utils.ClassUtils;
import com.arematics.minecraft.core.utils.MethodUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class CoreCommand implements CommandExecutor, TabExecutor {

    private static final String CMD_NO_PERMS = "cmd_noperms";
    private static final String CMD_FAILURE = "cmd_failure";
    private static final String CMD_SAME_SUB_METHOD = "cmd_same_sub_method_exist";

    private final String name;
    private final String[] commandNames;
    private final Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors = new LinkedHashMap<>();
    private final String classPermission;

    /**
     * Sorted Methods. Sorting is ordered by Default first and SubCommand Annotation Length
     */
    private final List<Method> sortedMethods;
    private final List<String> subCommands;

    public CoreCommand(String name) {
        this.name = name;
        this.commandNames = ClassUtils
                .fetchAnnotationValueSave(this, PluginCommand.class, PluginCommand::aliases)
                .orElse(new String[]{});
        this.classPermission = ClassUtils
                .fetchAnnotationValueSave(this, Perm.class, Perm::permission)
                .orElse("");
        this.sortedMethods = Arrays.stream(this.getClass().getDeclaredMethods())
                .sorted(this::sorted)
                .collect(Collectors.toList());
        this.subCommands = MethodUtils
                .fetchAllAnnotationValueSave(this, SubCommand.class, SubCommand::value);
        this.registerStandards();
    }

    private void registerStandards(){
        this.processors.put(Perm.class, new PermissionAnnotationProcessor());
        try {
            for(Annotation annotation : this.getClass().getAnnotations()){
                if(annotation.annotationType() == PluginCommand.class) {
                    Class<? extends AnnotationProcessor<?>>[] processors = ((PluginCommand)annotation).processors();
                    for (Class<? extends AnnotationProcessor<?>> processor : processors) {
                        AnnotationProcessor<?> instance = processor.newInstance();
                        this.processors.put(instance.get(), instance);
                    }
                }
            }
        } catch (Exception ignore) {}
        this.processors.put(SubCommand.class, new SubCommandAnnotationProcessor());
    }

    public void register(){
        ReflectCommand command = new ReflectCommand(this.name);
        command.setExecutor(this);
        if (this.commandNames != null) command.setAliases(Arrays.asList(this.commandNames));
        Bukkit.getServer().getCommandMap().register(this.name, command);
    }



    private int sorted(Method p1, Method p2){
        if(p1.isAnnotationPresent(Default.class)) return -1;
        else if (p2.isAnnotationPresent(Default.class)) return 1;

        if(p1.isAnnotationPresent(SubCommand.class)){
            if(p2.isAnnotationPresent(SubCommand.class)){
                String[] v1 = getSerializedValue(p1).split(" ");
                String[] v2 = getSerializedValue(p2).split(" ");
                return moreSubArguments(v1, v2);
            }
            return -1;
        }
        return 0;
    }

    private int moreSubArguments(String[] v1, String[] v2){
        if(v2.length > v1.length) return -1;
        if(v1.length > v2.length) return 1;
        for(int i = 0; i < v1.length; i++){
            String sub = v1[i];
            String sub2 = v2[i];
            if(!isParameter(sub) && (isParameter(sub2))) return -1;
            else if(!isParameter(sub2)) return 1;
        }

        return 0;
    }

    @Override
    public final boolean onCommand(CommandSender commandSender, Command command, String labels, String[] arguments) {
        return process(commandSender, arguments);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String labels, String[] arguments) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(StringUtils.join(arguments, " "), this.subCommands, completions);
        Collections.sort(completions);
        return completions;
    }

    private boolean process(CommandSender sender, String[] arguments){
        boolean isDefault = arguments.length == 0;
        List<String> annotations = new ArrayList<>();
        Map<String, Object> dataPack = new HashMap<>();
        dataPack.put(CommonData.COMMAND_SENDER.toString(), sender);
        dataPack.put("classLevelPermission", this.classPermission);
        MethodProcessorEnvironment environment = MethodProcessorEnvironment
                .newEnvironment(this, dataPack, this.processors);
        try{
            for (final Method method : this.sortedMethods){
                if(isDefault){
                    if(!StringUtils.isBlank(this.classPermission)) {
                        if (Permissions.isNotAllowed(sender, this.classPermission)) {
                            Messages.create("cmd_noperms")
                                    .WARNING()
                                    .to(sender)
                                    .handle();
                            return true;
                        }
                    }

                    if(ClassUtils.execute(Default.class, method, (tempMethod)
                            -> (Boolean) method.invoke(this, sender))) return true;
                }
                String value = getSerializedValue(method);
                if(annotations.contains(value)){
                    Messages.create(CMD_SAME_SUB_METHOD).FAILURE().to(sender).handle();
                    return false;
                }
                annotations.add(value);
                String[] annotationValues = value.split(" ");
                arguments = getSetupMessageArray(annotationValues, arguments);
                if(annotationValues.length == arguments.length && isMatch(annotationValues, arguments)) {
                    dataPack.put(CommonData.COMMAND_ARGUEMNTS.toString(), arguments);
                    if (environment.supply(method)) return true;
                }
            }
        }catch (Exception exception){
            exception.printStackTrace();
            Messages.create(CMD_FAILURE)
                    .FAILURE()
                    .to(sender)
                    .handle();
        }
        return true;
    }



    private boolean isMatch(String[] annotation, String[] src){
        boolean match = false;
        for(int i = 0; i < annotation.length; i++){
            String annotationString = annotation[i];
            if(!isParameter(annotationString)){
                if(!annotationString.equals(src[i])) return false;
                else match = true;
            }else{
                match = true;
            }
        }

        return match;
    }

    private boolean isParameter(String s){
        return s.startsWith("{") && s.endsWith("}");
    }

    private String[] getSetupMessageArray(String[] subArgs, String[] input){
        if(input.length > subArgs.length && subArgs[subArgs.length - 1].equals("{message}")){
            String message = StringUtils.join(input, " ", subArgs.length - 1, input.length);
            input = Arrays.copyOf(input, subArgs.length);
            input[input.length - 1] = message;
        }

        return input;
    }

    private String getSerializedValue(Method method) {
        if(method.isAnnotationPresent(SubCommand.class)) return method.getAnnotation(SubCommand.class).value();
        return "";
    }

}
