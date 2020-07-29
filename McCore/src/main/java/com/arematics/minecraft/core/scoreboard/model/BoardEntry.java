package com.arematics.minecraft.core.scoreboard.model;

public class BoardEntry {
    public Board BOARD;

    private final String ENTRY_ID;
    private String NAME;
    private String PREFIX;
    private String SUFFIX;
    private int SCORE;
    private long LAST_UPDATE_TIME;

    BoardEntry(String entryid, Board board, String name, int initalScore, String prefix, String suffix){
        this.BOARD = board;
        this.ENTRY_ID = entryid;
        this.NAME = name;
        this.PREFIX = prefix == null ? "" : prefix;
        this.SUFFIX = suffix == null ? "" : suffix;

        if(this.PREFIX.length() > 16)
            this.PREFIX = prefix.substring(0, 16);
        if(this.SUFFIX.length() > 16)
            this.SUFFIX = suffix.substring(0, 16);

        this.SCORE = initalScore;
        this.BOARD.ENTRIES.put(entryid, this);
        this.BOARD.SET.onSetScore(this);
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public String getEntryId() {
        return this.ENTRY_ID;
    }

    public String getName() {
        return this.NAME;
    }

    public String getPrefix() {
        return this.PREFIX;
    }

    public String getSuffix() {
        return this.SUFFIX;
    }

    public int getScore() {
        return this.SCORE;
    }

    public long getLastUpdateTimestamp() {
        return this.LAST_UPDATE_TIME;
    }

    public void setName(String name) {
        // Safty-Checks
        if(name == null) name = "";
        if(this.NAME == null) this.NAME = "";
        if(name.equals(this.NAME)) return;
        if(name.length() > 16) name = name.substring(0, 16);

        // For minecraft, this basically recreates the score
        this.NAME = name;
        this.BOARD.SET.onScoreRemove(this);
        this.BOARD.SET.onTeamRemove(this);
        this.BOARD.SET.onSetScore(this);
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void setPrefix(String prefix) {
        if(prefix == null) prefix = "";
        if(this.PREFIX == null) this.PREFIX = "";
        if(prefix.equals(this.PREFIX)) return;
        if(prefix.length() > 16) prefix = prefix.substring(0, 16);

        this.PREFIX = prefix;
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void setSuffix(String suffix) {
        if(suffix == null) suffix = "";
        if(this.SUFFIX == null) this.SUFFIX = "";
        if(suffix.equals(this.SUFFIX)) return;
        if(suffix.length() > 16) suffix = suffix.substring(0, 16);

        this.SUFFIX = suffix;
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void setNameAndSuffix(String name, String suffix){
        if(this.NAME == null) this.NAME = "";
        if(this.SUFFIX == null) this.SUFFIX = "";
        if(name == null) name = "";
        if(suffix == null) suffix = "";
        if(suffix.equals(this.SUFFIX) && name.equals(this.NAME)) return;
        if(name.length() > 16) name = name.substring(0, 16);
        if(suffix.length() > 16) suffix = suffix.substring(0, 16);

        this.NAME = name;
        this.SUFFIX = suffix;
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void setPrefixAndSuffix(String prefix, String suffix){
        if(this.PREFIX == null) this.PREFIX = "";
        if(this.SUFFIX == null) this.SUFFIX = "";
        if(prefix == null) prefix = "";
        if(suffix == null) suffix = "";
        if(suffix.equals(this.SUFFIX) && prefix.equals(this.PREFIX)) return;
        if(prefix.length() > 16) prefix = prefix.substring(0, 16);
        if(suffix.length() > 16) suffix = suffix.substring(0, 16);

        this.PREFIX = prefix;
        this.SUFFIX = suffix;
        this.BOARD.SET.onSetTeam(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void setScore(int score) {
        if(this.SCORE == score) return;
        this.SCORE = score;
        this.BOARD.SET.onSetScore(this);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
    }

    public void remove(){
        this.BOARD.SET.onScoreRemove(this);
        this.BOARD.SET.onTeamRemove(this);
        this.BOARD.ENTRIES.remove(this.ENTRY_ID);
        this.LAST_UPDATE_TIME = System.currentTimeMillis();
        this.BOARD.LAST_UPDATE_TIME = System.currentTimeMillis();
    }
}
