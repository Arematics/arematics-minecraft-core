package com.arematics.minecraft.core.messaging.advanced;

public class PartBuilder {

    public static Part createHoverAndSuggest(String text, String hover, String suggestCommand) {
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.SUGGEST_COMMAND, suggestCommand);
    }

    public static Part createHoverAndRun(String text, String hover, String runCommand) {
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.RUN_COMMAND, runCommand);
    }

    public static Part createHoverAndLink(String text, String hover, String link) {
        return new Part(text)
                .setHoverAction(HoverAction.SHOW_TEXT, hover)
                .setClickAction(ClickAction.OPEN_URL, link);
    }

    public static Part createAcceptMessage(String acceptMessage, String cmd){
        return PartBuilder.createHoverAndRun("ACCEPT", "§aAccept " + acceptMessage,
                "/" + cmd).setBaseColor(JsonColor.GREEN);
    }

    public static Part createDenyMessage(String denyMessage, String cmd){
        return PartBuilder.createHoverAndRun("DENY", "§cDeny " + denyMessage,
                "/" + cmd).setBaseColor(JsonColor.RED);
    }

}
