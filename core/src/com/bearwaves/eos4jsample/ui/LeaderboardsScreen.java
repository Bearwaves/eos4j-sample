package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardDefinition;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardRecord;
import com.bearwaves.eos4jsample.leaderboards.LeaderboardUserScore;

import java.util.List;
import java.util.Map;

public class LeaderboardsScreen extends ContentScreen {

    private final Table sidebar;
    private final Label infoLabel;
    private final Table ranks;

    public LeaderboardsScreen(GdxGame game, Container<ContentScreen> container, Skin skin) {
        super(game, container, skin, "Leaderboards");
        this.sidebar = new Table(skin);
        this.sidebar.setBackground(skin.newDrawable("white", Color.BLACK));
        this.sidebar.align(Align.topLeft);
        Table content = new Table(skin);
        content.align(Align.top);
        this.infoLabel = new Label("Fetching leaderboards...", skin);
        this.ranks = new Table(skin);
        this.ranks.align(Align.topLeft);
        this.ranks.defaults().space(Value.percentHeight(0.2f));

        this.add(sidebar).minWidth(Value.percentWidth(0.2f)).fill();
        this.add(content).grow();

        content.add(infoLabel).row();
        content.add(ranks).pad(Value.percentWidth(0.1f)).grow();
    }

    @Override
    public void handleNewLoginState(LoginState loginState) {
        if (loginState == LoginState.LOGGED_IN) {
            doFetchLeaderboards();
        }
    }

    private void doFetchLeaderboards() {
        getGame().getPlatform().getLeaderboardDefinitions(result -> {
            if (result == null) {
                infoLabel.setText("Something went wrong fetching leaderboards - see logs.");
                return;
            }
            infoLabel.setText("Fetched " + result.definitions.length + " leaderboards.");
            for (LeaderboardDefinition definition : result.definitions) {
                TextButton button = new TextButton(definition.id, getSkin());
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ranks.clearChildren();
                        StringBuilder builder = new StringBuilder();
                        builder.append("Leaderboard: ").appendLine(definition.id);
                        builder.append("Stat: ").appendLine(definition.statName);
                        builder.append("Start time: ").appendLine(definition.startTime.toString());
                        builder.append("End time: ").appendLine(definition.endTime.toString());
                        infoLabel.setText(builder.toString());
                        doFetchLeaderboard(definition.id);
                    }
                });
                sidebar.add(button).pad(Value.percentWidth(0.1f));
                sidebar.row();
            }
            getGame().getPlatform().getLeaderboardUserScores(result.definitions, userScoresResult -> {
                if (userScoresResult == null) {
                    return;
                }
                StringBuilder sb = new StringBuilder().appendLine("Fetched " + result.definitions.length + " leaderboards.");
                sb.appendLine("\nUser scores:");
                for (Map.Entry<String, List<LeaderboardUserScore>> scores : userScoresResult.userScores.entrySet()) {
                    sb.appendLine(scores.getKey() + ": ");
                    for (LeaderboardUserScore score : scores.getValue()) {
                        sb.appendLine("  " + score.userId + ": " + score.score);
                    }
                }
                infoLabel.setText(sb.toString());
            });
        });
    }

    private void doFetchLeaderboard(String id) {
        getGame().getPlatform().getLeaderboardRanks(id, result -> {
            if (result == null) {
                infoLabel.setText("Something went wrong fetching leaderboard - see logs.");
                return;
            }
            for (LeaderboardRecord record : result.records) {
                ranks.add(new Label(record.rank + ".", getSkin()));
                ranks.add(new Label(record.displayName, getSkin()));
                ranks.add(new Label(": " + record.score, getSkin()));
                ranks.row();
            }
        });
    }
}
