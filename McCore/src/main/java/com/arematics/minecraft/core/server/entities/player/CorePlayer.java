package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardSet;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.MessageInjector;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageReplace;
import com.arematics.minecraft.core.server.entities.CurrencyEntity;
import com.arematics.minecraft.core.server.entities.player.protocols.ActionBar;
import com.arematics.minecraft.core.server.entities.player.protocols.Packets;
import com.arematics.minecraft.core.server.entities.player.protocols.Title;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.model.SoulOg;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.service.GameStatsService;
import com.arematics.minecraft.data.service.OgService;
import com.arematics.minecraft.data.service.UserService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Setter
@Getter
@Accessors(fluent = true)
public class CorePlayer implements CurrencyEntity {

    private static final Logger logger = Bukkit.getLogger();

    private static Locale defaultLocale = Locale.GERMAN;
    private static Map<UUID, CorePlayer> players = new HashMap<>();

    public static List<CorePlayer> getAll(List<HumanEntity> senders){
        return senders.stream().map(CorePlayer::get).collect(Collectors.toList());
    }

    @Nullable
    public static CorePlayer get(HumanEntity sender){
        if(!(sender instanceof Player)) return null;
        Player player = (Player) sender;
        if(!players.containsKey(player.getUniqueId()))
            players.put(player.getUniqueId(), new CorePlayer(player));
        return players.get(player.getUniqueId());
    }

    public static void invalidate(Player player){
        if(players.containsKey(player.getUniqueId())) players.get(player.getUniqueId()).unload();
        players.remove(player.getUniqueId());
    }

    private final Player player;
    private final BoardSet boardSet;
    private final PlayerRequestSettings requests;
    private final InventoryHandler inventories;
    private final RegionHandler regions;
    private final OnlineTimeHandler onlineTime;
    private boolean ignoreMeta = false;
    private boolean disableLowerInventory = false;
    private boolean disableUpperInventory = false;

    private boolean inFight = false;
    private BukkitTask inFightTask;
    private BukkitTask inTeleport;

    private final GameStatsService service;
    private final UserService userService;

    private List<String> lastCommands = new ArrayList<>();
    private int page;

    private Packets packets;
    private ActionBar actionBar;
    private Title title;

    private Locale selectedLocale;

    private Rank cachedRank;
    private Rank cachedDisplayRank;

    private String chatMessage;
    private String ogColorCode = "§8";

    public CorePlayer(Player player){
        this.player = player;
        this.boardSet = new BoardSet(player);
        this.userService = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        this.requests = new PlayerRequestSettings(this);
        this.inventories = new InventoryHandler(this);
        this.regions = new RegionHandler(this);
        this.onlineTime = new OnlineTimeHandler(this);
        this.service = Boots.getBoot(CoreBoot.class).getContext().getBean(GameStatsService.class);
        this.packets = new Packets(player);
        this.actionBar = new ActionBar(this);
        this.title = new Title(this);
        OgService ogService = Boots.getBoot(CoreBoot.class).getContext().getBean(OgService.class);
        try{
            SoulOg og = ogService.findByUUID(player.getUniqueId());
            System.out.println(og);
            this.ogColorCode = "§" + og.getColorCode();
        }catch (Exception ignore){
            ignore.printStackTrace();
        }

        User user = this.getUser();
        this.cachedRank = user.getRank();
        this.cachedDisplayRank = user.getDisplayRank();

        Configuration configuration = getUser().getConfigurations().get("locale");
        if(configuration != null) this.selectedLocale = Locale.forLanguageTag(configuration.getValue());
        else setLocale(CorePlayer.defaultLocale);
        refreshChatMessage();
    }

    public Player getPlayer(){
        return this.player();
    }

    public void refreshCache(){
        User user = this.getUser();
        logger.config("Init Refresh Cached Data for user " + user.getLastName());
        this.cachedRank = user.getRank();
        this.cachedDisplayRank = user.getDisplayRank();
        refreshChatMessage();
        logger.config("Refreshed Cached Data for user " + user.getLastName());
    }

    private void refreshChatMessage(){
        Rank topLevel = getTopLevel();
        String heart = ogColorCode.equals("§8") ? "" : "§c❤ ";
        this.chatMessage = heart + ogColorCode + "§l[" + topLevel.getColorCode() + topLevel.getName() + ogColorCode + "§l] §7"
                + player.getPlayer().getName()
                + " §8» " + colorCode(topLevel)
                + "%message%";
    }

    public Rank getTopLevel(){
        return cachedDisplayRank != null ? cachedDisplayRank : cachedRank;
    }

    private String colorCode(Rank rank){
        return rank.isInTeam() ? "§c" : "§f";
    }

    public String getName(){
        return player.getName();
    }

    public void setLocale(Locale locale){
        Configuration configuration = getUser().getConfigurations().get("locale");
        if(configuration == null) configuration = new Configuration(locale.toLanguageTag());
        configuration.setValue(locale.toLanguageTag());
        User user = getUser();
        user.getConfigurations().put("locale", configuration);
        userService.update(user);
        this.selectedLocale = locale;
    }

    public World getWorld(){
        return player.getWorld();
    }

    public ZonedDateTime parseTime(LocalDateTime time){
        return ZonedDateTime.of(time, TimeZone.getTimeZone("Europe/Berlin").toZoneId());
    }
    private void unload() {
        logger.config("Unloading player: " + this.getName());
        this.boardSet.remove();
    }

    public void addLastCommand(String command){
        if(lastCommands.size() == 5)
            lastCommands.remove(0);
        this.lastCommands.add(command);
    }

    public String getLastCommand(int number){
        try{
            return lastCommands.get(lastCommands.size() - number);
        }catch (ArrayIndexOutOfBoundsException e){
            return "";
        }
    }

    public User getUser(){
        return this.userService.getOrCreateUser(this);
    }

    public String getLastCommand(){
        return getLastCommand(2);
    }

    public void setInFight(){
        logger.config("Enable fight for player " + getName());
        this.inFight = true;
        if(inFightTask != null) inFightTask.cancel();
        this.inFightTask = ArematicsExecutor.asyncDelayed(this::fightEnd, 7, TimeUnit.SECONDS);
    }

    public void fightEnd(){
        logger.config("Ending fight for player " + getName());
        if(inFight) this.info("Could log out now").handle();
        this.inFight = false;
    }

    public Entity next(){
        return next(5);
    }

    public Entity next(int range){
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        if(entities.isEmpty()) return null;
        return entities.get(0);
    }

    public void removeAmountFromHand(int amount){
        ArematicsExecutor.syncRun(() -> syncRemoveFromHand(amount));
    }

    private void syncRemoveFromHand(int amount){
        if(getItemInHand() != null)
            if(!player.getGameMode().equals(GameMode.CREATIVE)){
                int am = player.getItemInHand().getAmount();
                if(amount >= am)
                    player.setItemInHand(new ItemStack(Material.AIR));
                else
                    player.getItemInHand().setAmount(am - amount);
            }
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        this.player().addPotionEffect(potionEffect);
    }

    public boolean hasEffect(PotionEffectType type) {
        return this.player().hasPotionEffect(type);
    }

    public void removePotionEffect(PotionEffectType type) {
        this.player().removePotionEffect(type);
    }

    public void dispatchCommand(String command){
        ArematicsExecutor.syncRun(() -> this.player().performCommand(command.replaceFirst("/", "")));
    }

    @SuppressWarnings("unused")
    public void equip(CoreItem... items){
        ArematicsExecutor.runAsync(() -> this.equipItems(items));
    }

    private void equipItems(CoreItem... items){
        CoreItem[] drop = noUse(items);
        if(drop.length > 0){
            this.warn("" + drop.length + " items have been dropped").handle();
            Arrays.stream(drop).forEach(this::dropItem);
        }
    }

    public void dropItem(CoreItem drop){
        ArematicsExecutor.syncRun(() -> this.getLocation().getWorld().dropItemNaturally(this.getLocation(), drop));
    }

    private CoreItem[] noUse(CoreItem... item){
        return Arrays.stream(item)
                .filter(this::equipArmor)
                .toArray(CoreItem[]::new);
    }

    private boolean equipArmor(CoreItem item) {
        return hasEffect(PotionEffectType.INVISIBILITY);
    }

    public void stopTeleport(){
        if(inTeleport != null){
            inTeleport.cancel();
            warn("Your teleport has been cancelled").handle();
            inTeleport = null;
        }
    }

    public TeleportScheduler teleport(Location location, boolean instant){
        return new TeleportScheduler(this, location, instant);
    }

    public TeleportScheduler teleport(Location location){
        return teleport(location, false);
    }

    public TeleportScheduler instantTeleport(Location location){
        return teleport(location, true);
    }

    public void update(User user){
        this.userService.update(user);
    }

    @SuppressWarnings("unused")
    public void addKarma(int amount){
        User user = getUser();
        user.setKarma(user.getKarma() + amount);
        update(user);
    }

    @SuppressWarnings("unused")
    public void removeKarma(int amount){
        User user = getUser();
        user.setKarma(user.getKarma() - amount);
        update(user);
    }

    public AdvancedMessageReplace send(Part part){
        return info("%value%")
                .setInjector(AdvancedMessageInjector.class)
                .replace("value", part);
    }

    public MessageInjector info(String msg){
        return Messages.create(msg)
                .to(this.getPlayer());
    }

    public MessageInjector warn(String msg){
        return Messages.create(msg)
                .WARNING()
                .to(this.getPlayer());
    }

    public MessageInjector failure(String msg){
        return Messages.create(msg)
                .FAILURE()
                .to(this.getPlayer());
    }

    public BoardSet getBoard(){
        return this.boardSet;
    }

    public GameStats getStats(){
        return this.service.getOrCreate(getUUID());
    }

    private void saveStats(GameStats stats){
        this.service.save(stats);
    }

    @SuppressWarnings("unused")
    public void cleanStats(){
        this.service.delete(getStats());
    }

    public void onStats(Consumer<GameStats> execute){
        GameStats stats = getStats();
        execute.accept(stats);
        saveStats(stats);
    }

    @SuppressWarnings("unused")
    public void setKills(int kills){
        onStats(stats -> stats.setKills(kills));
        getBoard().getBoard("main")
                .setEntrySuffix("Kills", "§7" + this.getStats().getKills());
    }

    public void addKill(){
        onStats(stats -> stats.setKills(stats.getKills() + 1));
        getBoard().getBoard("main")
                .setEntrySuffix("Kills", "§7" + this.getStats().getKills());
    }

    @SuppressWarnings("unused")
    public void setDeaths(int deaths){
        onStats(stats -> stats.setDeaths(deaths));
        onStats(stats -> stats.setDeaths(stats.getDeaths() + 1));
        getBoard().getBoard("main")
                .setEntrySuffix("Deaths", "§7" + this.getStats().getDeaths());
    }

    public void addDeath(){
        onStats(stats -> stats.setDeaths(stats.getDeaths() + 1));
        getBoard().getBoard("main").setEntrySuffix("Deaths", "§7" + this.getStats().getDeaths());
    }

    @Override
    public double getMoney(){
        return this.getStats().getCoins();
    }

    public String stripMoney(){
        DecimalFormat format = new DecimalFormat("#");
        return format.format(this.getStats().getCoins());
    }

    @Override
    public void setMoney(double money){
        onStats(stats -> stats.setCoins(money));
        getBoard().getBoard("main").setEntrySuffix("Coins", "§7" + stripMoney());
    }

    @Override
    public void addMoney(double amount){
        onStats(stats -> stats.setCoins(stats.getCoins() + amount));
        getBoard().getBoard("main").setEntrySuffix("Coins", "§7" + stripMoney());
    }

    @Override
    public void removeMoney(double amount) throws RuntimeException{
        if(getStats().getCoins() < amount) throw new RuntimeException("Not enough coins");
        onStats(stats -> stats.setCoins(stats.getCoins() - amount));
        getBoard().getBoard("main").setEntrySuffix("Coins", "§7" + stripMoney());
    }

    @SuppressWarnings("unused")
    public void setBounty(int bounty){
        onStats(stats -> stats.setBounty(bounty));
    }

    public UUID getUUID(){
        return this.player.getUniqueId();
    }

    public CoreItem getItemInHand(){
        return CoreItem.create(player.getItemInHand());
    }

    public void setItemInHand(ItemStack item){
        player.setItemInHand(item);
    }

    public Location getLocation() {
        return this.player.getLocation();
    }

    public boolean hasPermission(String permission){
        return userService.hasPermission(getUUID(), permission);
    }

    /**
     * Permission Check Consumer executing method if permission is given or other method if permission is not given.
     * @param permission Permission searching for
     * @return Class with ifPermitted Consumer and orElse Consumer
     */
    public PermConsumer check(String permission){
        return new PermissionData(this, permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorePlayer player1 = (CorePlayer) o;
        return Objects.equals(player, player1.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
