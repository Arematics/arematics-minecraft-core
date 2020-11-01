package com.arematics.minecraft.core.command;

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
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.ClassUtils;
import com.arematics.minecraft.core.utils.Methods;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final List<String> longArgumentParameters = new ArrayList<>();

    public CoreCommand(String name, String... aliases) {
        this.name = name;
        registerLongArgument("message");
        registerLongArgument("name");
        this.commandNames = aliases;
        this.classPermission = ClassUtils
                .fetchAnnotationValueSave(this, Perm.class, Perm::permission)
                .orElse("");
        this.sortedMethods = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SubCommand.class))
                .sorted((m1, m2) -> this.moreSubArguments(fetchSubCommand(m1), fetchSubCommand(m2)))
                .collect(Collectors.toList());
        this.subCommands = Methods
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
        if(this.subCommands.stream().distinct().count() != this.subCommands.size()){
            throw new RuntimeException("The command: " + this.getName() + " has identical sub command methods");
        }else{
            ReflectCommand command = new ReflectCommand(this.name);
            command.setExecutor(this);
            if (this.commandNames != null) command.setAliases(Arrays.asList(this.commandNames));
            Bukkit.getServer().getCommandMap().register(this.name, command);
        }
    }

    protected void registerLongArgument(String key){
        this.longArgumentParameters.add("{" + key + "}");
    }

    public abstract boolean onDefaultExecute(CommandSender sender);

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
    public final boolean onCommand(final CommandSender commandSender, final Command command, String labels,
                                   final String[] arguments) {
        ArematicsExecutor.runAsync(() -> process(commandSender, arguments));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String labels, String[] arguments) {
        return searchAllMatches(arguments);
    }

    private List<String> searchAllMatches(String[] arguments){
        String argumentsJoined = StringUtils.join(arguments, " ");
        List<String> results = this.subCommands.stream()
                .filter(text -> !StringUtils.isBlank(text) && text.startsWith(argumentsJoined))
                .map(text -> filterMatch(text, argumentsJoined))
                .map(this::trimText)
                .distinct()
                .collect(Collectors.toList());
        results.addAll(Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().startsWith(arguments[arguments.length - 1]))
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));
        return results;
    }

    private String filterMatch(String text, String arguments){
        return text.substring(arguments.contains(" ") ? arguments.lastIndexOf(" ") : 0);
    }

    private String trimText(String text){
        text = text.trim();
        return text.contains(" ") ? text.substring(0, text.indexOf(" ")) : text;
    }

    private void process(CommandSender sender, String[] arguments){
        boolean isDefault = arguments.length == 0;
        Map<String, Object> dataPack = new Hashtable<>();
        dataPack.put(CommonData.COMMAND_SENDER.toString(), sender);
        dataPack.put("classLevelPermission", this.classPermission);
        try{

            if(isDefault)
                Permissions.check(sender, this.classPermission).ifPermitted(this::onDefaultExecute).submit();
            else {
                MethodProcessorEnvironment environment = MethodProcessorEnvironment
                        .newEnvironment(this, dataPack, this.processors);
                boolean success = Methods.of(this.sortedMethods).apply((method) -> {
                    String[] annotation = fetchSubCommand(method);
                    dataPack.put("arguments", getSetupMessageArray(annotation, arguments.clone()));
                    return isMatch(annotation, arguments) && environment.supply(method);
                });
                if(!success)
                    Permissions.check(sender, this.classPermission).ifPermitted(this::onDefaultExecute).submit();
            }
        }catch (Exception exception){
            exception.printStackTrace();
            Messages.create(CMD_FAILURE).FAILURE().to(sender).handle();
        }
    }



    private boolean isMatch(String[] annotation, String[] src){
        String[] clonedSource = getSetupMessageArray(annotation, src.clone());
        if(annotation.length != clonedSource.length) return false;
        return IntStream.range(0, annotation.length)
                .filter((index) -> !annotation[index].equals(clonedSource[index]))
                .filter((index) -> !isParameter(annotation[index]))
                .count() == 0;
    }

    private String[] getSetupMessageArray(String[] subArgs, String[] input){
        if(input.length > subArgs.length && this.longArgumentParameters.contains(subArgs[subArgs.length - 1])){
            String message = StringUtils.join(input, " ", subArgs.length - 1, input.length);
            input = Arrays.copyOf(input, subArgs.length);
            input[input.length - 1] = message;
        }

        return input;
    }

    private boolean isParameter(String s){
        return s.startsWith("{") && s.endsWith("}");
    }

    private String[] fetchSubCommand(Method method){
        return method.getAnnotation(SubCommand.class).value().split(" ");
    }
}
