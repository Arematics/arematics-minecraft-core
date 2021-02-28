package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.PermissionAnnotationProcessor;
import com.arematics.minecraft.core.command.processor.SubCommandAnnotationProcessor;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.supplier.standard.CommandSupplier;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.processor.methods.AnnotationProcessor;
import com.arematics.minecraft.core.processor.methods.MethodProcessorEnvironment;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.ClassUtils;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.core.utils.Methods;
import com.arematics.minecraft.data.service.InventoryService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class CoreCommand extends Command {

    private static final String CMD_NO_PERMS = "cmd_noperms";
    private static final String CMD_FAILURE = "cmd_failure";
    private static final String CMD_SAME_SUB_METHOD = "cmd_same_sub_method_exist";
    private static final String PASTE_SUB_COMMAND = "paste_sub_command";

    private final String classPermission;

    /**
     * Sorted Methods. Sorting is ordered by Default first and SubCommand Annotation Length
     */
    private final List<Method> sortedMethods;
    private final List<String> subCommands;
    private final List<String> longArgumentParameters = new ArrayList<>();
    private final String commandInformationString;

    private final byte uiSlots;

    private final List<AnnotationProcessor<?>> processors = new ArrayList<>();

    public CoreCommand(String name, String... aliases){
        this(name, new AnnotationProcessor<?>[]{}, aliases);
    }

    public CoreCommand(String name, AnnotationProcessor<?>[] processors, String... aliases) {
        super(name);
        setAliases(Arrays.asList(aliases));
        registerLongArgument("message");
        registerLongArgument("name");
        this.classPermission = ClassUtils
                .fetchAnnotationValueSave(this, Perm.class, Perm::permission)
                .orElse("");
        this.sortedMethods = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SubCommand.class))
                .sorted((m1, m2) -> this.moreSubArguments(fetchSubCommand(m1), fetchSubCommand(m2)))
                .collect(Collectors.toList());
        this.subCommands = Methods
                .fetchAllAnnotationValueSave(this, SubCommand.class, SubCommand::value);
        this.commandInformationString = CommandUtils.prettyReplace("Command", "/" + this.getName());
        this.registerStandards(processors);
        this.uiSlots = calculateSlots();
    }

    private void registerStandards(AnnotationProcessor<?>[] processors){
        this.processors.add(new PermissionAnnotationProcessor());
        this.processors.addAll(Arrays.asList(processors));
        this.processors.add(new SubCommandAnnotationProcessor());
    }

    public void register(){
        if(this.subCommands.stream().distinct().count() != this.subCommands.size())
            throw new RuntimeException("The command: " + this.getName() + " has identical sub command methods");
        else {
            if(Bukkit.getCommandMap().getCommand(this.getName()) != null) {
                Bukkit.getLogger().info("Command: " + this.getName() + " double register detected, removing first register");
                try{
                    unregisterCommand();
                }catch (Exception ignore){}
            }
            Bukkit.getServer().getCommandMap().register(this.getName(), this);
        }
    }

    public void unregisterCommand() throws NoSuchFieldException, IllegalAccessException {
        SimplePluginManager manager = (SimplePluginManager) Boots.getBoot(CoreBoot.class).getServer().getPluginManager();

        Field commandMapField = SimplePluginManager.class.getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        CommandMap map = (CommandMap) commandMapField.get(manager);

        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(map);

        Command command = knownCommands.get(this.getName());
        if (command != null) {
            command.unregister(map);
            knownCommands.remove(this.getName());
        }
    }

    protected void registerLongArgument(String key){
        this.longArgumentParameters.add("{" + key + "}");
    }

    public void onDefaultExecute(CommandSender sender){
        CommandSupplier.create().setCLI(this::onDefaultCLI).setGUI(this::onDefaultGUI).accept(sender);
    }

    protected boolean onDefaultCLI(CommandSender sender){
        Part[] commandInformationValues = this.subCommands.stream()
                .map(subcmd -> toSubCommandExecute(sender, subcmd))
                .toArray(Part[]::new);
        Messages.create(this.commandInformationString)
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("value", new MSG(commandInformationValues))
                .disableServerPrefix()
                .handle();
        return true;
    }

    protected boolean onDefaultGUI(CorePlayer player){
        InventoryService service = Boots.getBoot(CoreBoot.class).getContext().getBean(InventoryService.class);
        Inventory inv = service.getOrCreate("command.default.menu." + this.getName(),
                "§8Command: §c" + this.getName(), this.uiSlots);
        player.openInventory(inv);
        return true;
    }

    private byte calculateSlots(){
        int size = this.subCommands.size();
        int min = 1;
        int max = 15;
        byte val = 27;
        while(!(between(min, max, size))){
            min = max + 1;
            max = max + 5;
            val += 9;
            if(val >= 54) return 54;
        }
        return val;
    }

    private boolean between(int min, int max, int value){
        return value >= min && value <= max;
    }


    private Part toSubCommandExecute(CommandSender sender, String cmdName){
        return new Part("     §7/" + this.getName() + " §c" + cmdName + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT,
                        LanguageAPI.prepareRawMessage(sender, PASTE_SUB_COMMAND).replaceAll("%subcmd%", cmdName))
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/" + this.getName() + " " + cmdName);
    }

    private int moreSubArguments(String[] v1, String[] v2){
        if(v2.length > v1.length) return 1;
        if(v1.length > v2.length) return -1;
        for(int i = 0; i < v1.length; i++){
            String sub = v1[i];
            String sub2 = v2[i];
            if(!isParameter(sub) && (isParameter(sub2))) return -1;
            else if(!isParameter(sub2)) return 1;
        }

        return 0;
    }

    @Override
    public final boolean execute(final CommandSender commandSender, String labels, String[] arguments) {
        ArematicsExecutor.runAsync(() -> process(commandSender, arguments));
        return true;
    }

    @Override
    public final List<String> tabComplete(CommandSender commandSender, String labels, String[] arguments) {
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
                .map(HumanEntity::getName)
                .filter(name -> name.toLowerCase().startsWith(arguments[arguments.length - 1].toLowerCase()))
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
        dataPack.put("sender", sender);
        dataPack.put("classLevelPermission", this.classPermission);
        if(sender instanceof Player) CorePlayer.get((Player) sender)
                .addLastCommand(getName() + " " + StringUtils.join(arguments, " "));
        try{
            if(isDefault)
                Permissions.check(sender, this.classPermission).ifPermitted(this::onDefaultExecute).submit();
            else {
                MethodProcessorEnvironment environment = MethodProcessorEnvironment
                        .newEnvironment(this, dataPack, this.processors);
                AtomicBoolean matchFound = new AtomicBoolean(false);
                boolean success = Methods.of(this.sortedMethods).apply((method) -> {
                    String[] annotation = fetchSubCommand(method);
                    dataPack.put("arguments", getSetupMessageArray(annotation, arguments.clone()));
                    matchFound.set(isMatch(annotation, arguments));
                    return matchFound.get() && environment.supply(method);
                });
                if(!matchFound.get())
                    Permissions.check(sender, this.classPermission).ifPermitted(this::onDefaultExecute).submit();
            }
        }catch (InvocationTargetException pe){
            if(pe.getCause() instanceof CommandProcessException)
                handleProcessorException(sender, (CommandProcessException) pe.getCause());
        }catch (CommandProcessException pe){
            handleProcessorException(sender, pe);
        } catch (Throwable exception){
            exception.printStackTrace();
            Messages.create(CMD_FAILURE).FAILURE().to(sender).handle();
        }
    }

    private void handleProcessorException(CommandSender sender, CommandProcessException pe){
        if(pe.getInjector() != null) pe.getInjector().handle();
        else Messages.create(pe.getMessage()).WARNING().to(sender).handle();
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
