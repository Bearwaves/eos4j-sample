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

public class LeaderboardsScreen extends ContentScreen {

    private final Table sidebar;
    private final Table content;
    private final Label infoLabel;

    public LeaderboardsScreen(GdxGame game, Container<ContentScreen> container, Skin skin) {
        super(game, container, skin, "Leaderboards");
        this.sidebar = new Table(skin);
        this.sidebar.setBackground(skin.newDrawable("white", Color.BLACK));
        this.sidebar.align(Align.topLeft);
        this.content = new Table(skin);
        this.infoLabel = new Label("Fetching leaderboards...", skin);

        this.add(sidebar).minWidth(Value.percentWidth(0.2f)).fill();
        this.add(content).grow();

        content.add(infoLabel).expand();
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
                        StringBuilder builder = new StringBuilder();
                        builder.append("Leaderboard: ").appendLine(definition.id);
                        builder.append("Stat: ").appendLine(definition.statName);
                        builder.append("Start time: ").appendLine(definition.startTime.toString());
                        builder.append("End time: ").appendLine(definition.endTime.toString());
                        infoLabel.setText(builder.toString());
                    }
                });
                sidebar.add(button).pad(Value.percentWidth(0.1f));
                sidebar.row();
            }
        });
    }
}
