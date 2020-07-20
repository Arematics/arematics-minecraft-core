package com.arematics.minecraft.watcher.analyses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatAnalysesTest {

    @Test
    void doChatMessageCheck() {
        String message = "Du bist ein Arschloch";
        assertArrayEquals(new String[]{"Arschloch"}, ChatAnalyses.doChatMessageCheck(message));
        String message2 = "Du bist ein A*schloch";
        assertArrayEquals(new String[]{"A*schloch"}, ChatAnalyses.doChatMessageCheck(message2));
        String message3 = "Du bist ein A*s**loch";
        assertArrayEquals(new String[]{"A*s**loch"}, ChatAnalyses.doChatMessageCheck(message3));
        String message4 = "Du bist ein A*schloche";
        assertArrayEquals(new String[]{"A*schloche"}, ChatAnalyses.doChatMessageCheck(message4));
        String message5 = "Du bist ein A*sch**h";
        assertArrayEquals(new String[]{"A*sch**h"}, ChatAnalyses.doChatMessageCheck(message5));
        String message6 = "Du bist ein A4sch**h";
        assertArrayEquals(new String[]{"A4sch**h"}, ChatAnalyses.doChatMessageCheck(message6));
        String message7 = "Du bist ein Arschooch";
        assertArrayEquals(new String[]{"Arschooch"}, ChatAnalyses.doChatMessageCheck(message7));
        String message8 = "Du bist ein A*gfs**h";
        assertArrayEquals(new String[]{}, ChatAnalyses.doChatMessageCheck(message8));
        String message9 = "Du bist ein A*sfs**ch";
        assertArrayEquals(new String[]{"A*sfs**ch"}, ChatAnalyses.doChatMessageCheck(message9));
    }
}