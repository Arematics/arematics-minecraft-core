package com.arematics.minecraft.core.messaging.advanced;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.ChatTheme;
import com.arematics.minecraft.data.global.model.PlaceholderAction;
import com.arematics.minecraft.core.chat.controller.PlaceholderController;
import com.arematics.minecraft.core.utils.JSONUtil;
import com.arematics.minecraft.data.global.model.GlobalPlaceholderAction;
import com.arematics.minecraft.data.global.model.ThemePlaceholder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MSG {

    public final List<Part> PARTS = new ArrayList<>();

    public MSG(Part... parts) {
        this(Arrays.asList(parts));

    }

    public MSG(List<Part> parts) {
        this.PARTS.addAll(parts);
    }

    /**
     * Constructs a MSG from message with all its formatting (Color- and Format-Codes)
     *
     * @param message Message to construct from
     */
    public MSG(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        String[] coloredParts = splitAndKeepDelimiter(message, Pattern.compile("(§[0-9]|§[a-f]|§r)"));

        for (String strPart : coloredParts) {
            JsonColor jsonColor = new Part(strPart).styleAndColorFromText().BASE_COLOR; // Only one in this part
            List<Format> formats = new ArrayList<>();

            String[] subPartsStrs = splitAndKeepDelimiter(strPart, Pattern.compile("(§[0-9]|§[a-f]|§[k-o]|§r)+"));
            for (String subPartStr : subPartsStrs) {
                Part part = new Part(subPartStr).styleAndColorFromText();
                part.BASE_COLOR = jsonColor;
                HashSet<Format> newFormats = part.MESSAGE_FORMATS;
                part.MESSAGE_FORMATS.addAll(formats);
                formats.addAll(newFormats);
                this.PARTS.add(part);
            }
        }

        // Remove all formatting (should already be translated into the parts):
        this.PARTS.forEach(part -> part.TEXT = ChatColor.stripColor(part.TEXT));
        clearEmptyParts();
    }

    public MSG(ChatTheme theme) {
        Part part = new Part(theme.getFormat());
        this.PARTS.add(part);
    }

    /**
     * @param term Term to search for (default: case-sensitive)
     * @return All Parts with the exact 'term', which are now separate in MSG.parts
     */
    public Part[] separateTerms(String term) {
        return separateTerms(term, false);
    }


    /**
     * @param term       Term to search for
     * @param ignoreCase Whether casing should be ignored
     * @return All Parts with the exact 'term', which are now separate in MSG.parts
     */
    public Part[] separateTerms(String term, boolean ignoreCase) {
        if (term == null || term.isEmpty())
            throw new IllegalArgumentException("Term is null or empty!");
        if (ignoreCase)
            term = term.toLowerCase();

        boolean termFound;
        searchLoop:
        do {
            termFound = false;

            for (int i = 0; i < this.PARTS.size(); i++) {
                Part part = PARTS.get(i);
                String text = part.TEXT;
                String textCheck = ignoreCase ? text.toLowerCase() : text;

                if (!textCheck.equals(term) && textCheck.contains(term)) {
                    int indexStart = textCheck.indexOf(term);
                    int indexEnd = indexStart + term.length();

                    Part beforeTerm = part.clone().setText(text.substring(0, indexStart));
                    Part atTerm = part.clone().setText(text.substring(indexStart, indexEnd));
                    Part afterTerm = part.clone().setText(text.substring(indexEnd));

                    PARTS.set(i, afterTerm);
                    PARTS.add(i, atTerm);
                    PARTS.add(i, beforeTerm);

                    // The above does:
                    // i     -> Part("before" + "term" + "after")
                    //      |
                    //      V
                    // i     -> Part("before")
                    // i + 1 -> Part("term")
                    // i + 2 -> Part("after")

                    termFound = true;
                    continue searchLoop;
                }
            }

        } while (termFound);

        clearEmptyParts();

        ArrayList<Part> matchingParts = new ArrayList<>();
        for (Part part : this.PARTS)
            if ((ignoreCase && part.TEXT.equalsIgnoreCase(term)) || (!ignoreCase && part.TEXT.equals(term)))
                matchingParts.add(part);
        return matchingParts.toArray(new Part[]{});
    }

    private void clearEmptyParts() {
        List<Part> empty = this.PARTS.stream()
                .filter(part -> StringUtils.isBlank(part.TEXT))
                .collect(Collectors.toList());
        this.PARTS.removeAll(empty);
    }

    /**
     * Simply replaces all found 'term's with the 'replacement'
     *
     * @param term        Term that gets replaced (case-sensitive)
     * @param replacement Replacement
     * @return Amount of parts that contained the term at least once
     */
    public int replaceAll(String term, Object replacement) {
        int replacedPartsCount = 0;
        for (Part part : this.PARTS) {
            if (part.TEXT.contains(term)) {
                part.setText(part.TEXT.replace(term, replacement.toString()));
                replacedPartsCount++;
            }
        }
        return replacedPartsCount;
    }

    /**
     * Simply replaces all found 'term's with the 'replacement'
     *
     * @param term        Term that gets replaced (case-sensitive)
     * @param replacement Replacement
     */
    public void replaceAllAt(String term, MSG replacement, boolean ignoreCase) {
        if (term == null || term.isEmpty())
            throw new IllegalArgumentException("Term is null or empty!");
        if (ignoreCase)
            term = term.toLowerCase();

        boolean termFound;
        searchLoop:
        do {
            termFound = false;

            for (int i = 0; i < this.PARTS.size(); i++) {
                Part part = PARTS.get(i);
                String text = part.TEXT;
                String textCheck = ignoreCase ? text.toLowerCase() : text;

                if (!textCheck.equals(term) && textCheck.contains(term)) {
                    int indexStart = textCheck.indexOf(term);
                    int indexEnd = indexStart + term.length();

                    Part beforeTerm = part.clone().setText(text.substring(0, indexStart));
                    Part atTerm = part.clone().setText(text.substring(indexStart, indexEnd));
                    Part afterTerm = part.clone().setText(text.substring(indexEnd));

                    replacement.PARTS.stream()
                            .filter(partVal -> partVal.BASE_COLOR == null)
                            .forEach(partVal -> partVal.setBaseColor(atTerm.BASE_COLOR));

                    PARTS.set(i, afterTerm);
                    PARTS.addAll(i, replacement.PARTS);
                    PARTS.add(i, beforeTerm);

                    termFound = true;
                    continue searchLoop;
                }
            }

        } while (termFound);
    }


    JsonColor lastColor = null;

    /**
     * Splits encoded chat format into seperate parts and applies action & placeholder value on them
     *
     * @param actions to use
     * @param player  needed for globalplaceholder value, chatter
     */
    public void createThemeParts(Map<String, PlaceholderAction> actions, Player player, PlaceholderController placeholderController) {
        if (PARTS.size() > 1) {
            Bukkit.broadcastMessage("Something wrong here, dont split this again");
            return;
        }
        String text = PARTS.get(0).TEXT;
        PARTS.clear();
        String[] parts = text.split(",");
        for (String s : parts) {
            Part part = new Part(s).styleAndColorFromText();
            if (this.lastColor == null) {
                part = part.setBaseColor(part.BASE_COLOR);
            } else {
                part = part.setBaseColor(this.lastColor);
            }
            this.lastColor = part.BASE_COLOR;
            this.PARTS.add(applyPlaceholderToPart(part.styleAndColorFromText(), getAction(s, actions), player, placeholderController));
        }
    }

    private PlaceholderAction getAction(String coloredPart, Map<String, PlaceholderAction> actions) {
        return actions.get(ChatColor.stripColor(coloredPart));
    }

    /**
     * replaces placeholder with actual value and applies hover/click actions on them
     *
     * @param part              to apply actions and value
     * @param placeholderAction to use
     * @param player            needed for globalplaceholder value, chatter
     */
    private Part applyPlaceholderToPart(Part part, PlaceholderAction placeholderAction, Player player, PlaceholderController placeholderController) {
        String value = "";
        if (placeholderAction instanceof GlobalPlaceholderAction) {
            value = placeholderController.getPlaceholder(placeholderAction.getPlaceholderKey()).getValues().get(CorePlayer.get(player)).get();
            part.setText(value);
        } else if (placeholderAction instanceof ThemePlaceholder) {
            value = ((ThemePlaceholder) placeholderAction).getValue();
            part.setText(value);
        }
        if (null == placeholderAction) {
            return part;
        }
        if (null != placeholderAction.getHoverAction()) {
            part.setHoverAction(placeholderAction.getHoverAction().getAction(),
                    injectPlaceholders(placeholderAction.getHoverAction().getValue(), placeholderAction.getPlaceholderKey(), value));
        }
        if (null != placeholderAction.getClickAction()) {
            part.setClickAction(placeholderAction.getClickAction().getAction(),
                    injectPlaceholders(placeholderAction.getClickAction().getValue(), placeholderAction.getPlaceholderKey(), value));
        }

        return part;
    }

    /**
     * allows for using %key% to actual key in hover and click messages
     *
     * @param actionMessage to replace key
     * @param value         to inject
     * @return replaced action value
     */
    private String injectPlaceholders(String actionMessage, String placeholderMatch, String value) {
        return actionMessage.replace(PlaceholderController.applyDelimiter("placeholderMatch"), placeholderMatch).
                replace(PlaceholderController.applyDelimiter("placeholderName"), PlaceholderController.stripPlaceholderDelimiter(placeholderMatch)).
                replace(PlaceholderController.applyDelimiter("value"), value);
    }

    /**
     * Copied from EnderSYS/Utils/TextUtils
     */
    private static String[] splitAndKeepDelimiter(String message, Pattern regex) {

        // Find matches and not positions (enclose on exact terms):
        Matcher regexMatcher = regex.matcher(message);
        ArrayList<Integer> positions = new ArrayList<>();

        positions.add(0); // Beginning of 'message'
        while (regexMatcher.find())
            positions.add(regexMatcher.start());
        positions.add(message.length()); // End of 'message'
        // ---

        // Split 'message' into parts determined by 'positions'
        // Visualization: message: BEGINPOS --<Part>-- 5 --<Part>-- 12 --<Part>-- 16 --<Part>-- ENDPOS
        ArrayList<String> parts = new ArrayList<>();
        for (int i = 1; i < positions.size(); i++) {
            int from = positions.get(i - 1), to = positions.get(i);
            if (to - from == 0) // Skip empty parts
                continue;
            parts.add(message.substring(from, to));
        }

        return parts.toArray(new String[]{});
    }


    /**
     * Copied from EnderSYS/Utils/JSONUtil
     */
    private static String toPlainText(String json) {
        JsonObject jsonobj = (JsonObject) new JsonParser().parse(json);

        StringBuilder text = new StringBuilder("");

        if (jsonobj.has("text"))
            text.append(jsonobj.get("text").getAsString());

        if (jsonobj.has("extra")) {
            JsonArray extra = jsonobj.get("extra").getAsJsonArray();

            for (int i = 0; i < extra.size(); i++) {
                JsonObject part = extra.get(i).getAsJsonObject();
                if (part.has("text"))
                    text.append(part.get("text").getAsString());
            }
        }
        return text.toString();
    }

    public JsonObject toJsonObject() {
        JsonObject message = new JsonObject();
        message.addProperty("text", "");
        if (PARTS.size() == 0)
            return message;

        JsonArray extra = new JsonArray();
        PARTS.forEach(part -> extra.add(part.toJsonObject()));
        if (extra.size() > 0) // If ignored: com.google.gson.JsonParseException: Unexpected empty array of components
            message.add("extra", extra);

        return message;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public MSG clone() {
        return new MSG(this.PARTS.stream().map(Part::clone).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MSG)) return false;

        MSG otherMsg = (MSG) other;
        List<Part> thisParts = this.PARTS, otherParts = otherMsg.PARTS;
        if (thisParts.size() != otherParts.size())
            return false;

        return thisParts.containsAll(otherParts) && otherParts.containsAll(thisParts);
    }

    public void send(CommandSender... senders) {
        final String json = toJsonString();
        Arrays.stream(senders).forEach(sender -> {
            if (sender instanceof Player)
                ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(createPacketPlayOutChat(json));
            else
                sender.sendMessage(JSONUtil.toPlainText(json));
        });
    }

    public void sendAll(List<CorePlayer> users, List<CorePlayer> ignoredBy) {
        final String json = toJsonString();
        users.forEach(user -> {
            Player player = user.getPlayer();
            if (!ignoredBy.contains(user)) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(createPacketPlayOutChat(json));
            }
        });
    }

    private static PacketPlayOutChat createPacketPlayOutChat(String s) {
        return new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(s));
    }
}
