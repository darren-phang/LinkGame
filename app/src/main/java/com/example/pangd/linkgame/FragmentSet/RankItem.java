package com.example.pangd.linkgame.FragmentSet;

public class RankItem {
    private String rank_number;

    private String cost_time;

    private String player_name;

    private String input_time;

    public RankItem(String rank_number, String cost_time, String player_name, String input_time){
        this.rank_number = rank_number;
        this.cost_time = cost_time;
        this.player_name = player_name;
        this.input_time = input_time;
    }

    public String getRank_number() {
        return rank_number;
    }

    public void setRank_number(String rank_number) {
        this.rank_number = rank_number;
    }

    public String getCost_time() {
        return cost_time;
    }

    public void setCost_time(String cost_time) {
        this.cost_time = cost_time;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getInput_time() {
        return input_time;
    }

    public void setInput_time(String input_time) {
        this.input_time = input_time;
    }
}
