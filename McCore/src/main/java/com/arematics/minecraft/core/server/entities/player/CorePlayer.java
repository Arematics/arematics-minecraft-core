package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardSetHandler;
import com.arematics.minecraft.core.messaging.MessageInjector;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageReplace;
import com.arematics.minecraft.core.server.entities.CurrencyEntity;
import com.arematics.minecraft.core.server.entities.player.protocols.ActionBarHandler;
import com.arematics.minecraft.core.server.entities.player.protocols.BossBarHandler;
import com.arematics.minecraft.core.server.entities.player.protocols.Packets;
import com.arematics.minecraft.core.server.entities.player.protocols.Title;
import com.arematics.minecraft.core.utils.CommandUtils;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;

@Setter
@Getter
@Accessors(fluent = true)
public class CorePlayer implements CurrencyEntity {

    private static final Logger logger = LoggerFactory.getLogger(CorePlayer.class);

    private static Locale defaultLocale = Locale.GERMAN;

    private final Player player;
    private boolean ignoreMeta = false;
    private boolean disableLowerInventory = false;
    private boolean disableUpperInventory = false;

    private final UserService userService;
    private final GameStatsService service;

    private List<String> lastCommands = new ArrayList<>();
    private int page;

    private Packets packets;
    private ActionBarHandler actionBar;
    private BossBarHandler bossBar;
    private Title title;

    private Locale selectedLocale;

    private Rank cachedRank;
    private Rank cachedDisplayRank;

    private String chatMessage;
    private String ogColorCode = "§8";

    private Map<Class<? extends PlayerHandler>, Object> playerHandlers = new HashMap<>();

    public CorePlayer(Player player,
                      UserService userService,
                      GameStatsService gameStatsService,
                      OgService ogService,
                      Set<Class<? extends PlayerHandler>> handlers){
        handlers.forEach(handler -> {
            try {
                PlayerHandler newHandler = handler.newInstance();
                ConfigurableListableBeanFactory factory = Boots.getBoot(CoreBoot.class).getContext().getBeanFactory();
                factory.autowireBean(newHandler);
                newHandler.init(this);
                playerHandlers.put(handler, newHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logger.debug("Creating player: " + player.getName());
        this.player = player;
        this.userService = userService;
        this.service = gameStatsService;
        this.packets = new Packets(player);
        this.bossBar = new BossBarHandler(this);
        this.title = new Title(this);
        try{
            SoulOg og = ogService.findByUUID(player.getUniqueId());
            this.ogColorCode = "§" + og.getColorCode();
        }catch (Exception ignore){}

        User user = this.getUser();
        this.cachedRank = user.getRank();
        this.cachedDisplayRank = user.getDisplayRank();

        Configuration configuration = getUser().getConfigurations().get("locale");
        if(configuration != null) this.selectedLocale = Locale.forLanguageTag(configuration.getValue());
        else setLocale(CorePlayer.defaultLocale);
        refreshChatMessage();
    }

    public <T extends PlayerHandler> T handle(Class<T> handler){
        return handler.cast(playerHandlers.getOrDefault(handler, null));
    }

    public Player getPlayer(){
        return this.player();
    }

    public void refreshCache(){
        User user = this.getUser();
        logger.debug("Init Refresh Cached Data for user " + user.getLastName());
        this.cachedRank = user.getRank();
        this.cachedDisplayRank = user.getDisplayRank();
        refreshChatMessage();
        logger.debug("Refreshed Cached Data for user " + user.getLastName());
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

    public void unload() {
        logger.debug("Unloading player: " + this.getName());
        handle(BoardSetHandler.class).remove();
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
        return Messages.create(msg).to(this.getPlayer());
    }

    public MessageInjector warn(String msg){
        return Messages.create(msg).WARNING().to(this.getPlayer());
    }

    public MessageInjector failure(String msg){
        return Messages.create(msg).FAILURE().to(this.getPlayer());
    }

    public BoardSetHandler getBoard(){
        return handle(BoardSetHandler.class);
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
        return CommandUtils.shortenDecimal(this.getStats().getCoins().longValue());
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
