package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;
import com.bearwaves.eos4jsample.stats.Stat;

public class StatsScreen extends ContentScreen {

    private final Label statsLabel;

    public StatsScreen(GdxGame game, Container<ContentScreen> container, Skin skin) {
        super(game, container, skin, "Stats");

        align(Align.topLeft);
        Table inputRow = new Table();
        inputRow.align(Align.left);
        inputRow.defaults().space(Value.percentHeight(0.5f));
        TextField nameField = new TextField("", skin);
        nameField.setMessageText("Stat name");
        TextField valueField = new TextField("", skin);
        valueField.setMessageText("Stat value");
        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameField.getText();
                String valueText = valueField.getText();
                if (name.isEmpty() || valueText.isEmpty()) {
                    return;
                }
                try {
                    int value = Integer.parseInt(valueText);
                    getGame().getPlatform().ingestStat(new Stat(name, value), success -> {
                        if (success) {
                            doFetchStats();
                        } else {
                            statsLabel.setText("Something went wrong - please see logs.");
                        }
                    });
                } catch (NumberFormatException e) {
                    statsLabel.setText("Something went wrong - please see logs.");
                    Gdx.app.error("StatsScreen", "Couldn't parse value as number", e);
                }
            }
        });
        inputRow.add(nameField);
        inputRow.add(valueField);
        inputRow.add(submitButton);
        this.add(inputRow).growX();
        this.row().left();

        TextButton button = new TextButton("Get all stats", skin);
        button.pad(Value.percentHeight(0.1f));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doFetchStats();
            }
        });
        this.add(button);
        this.row().left();

        this.statsLabel = new Label("Querying stats...", skin);
        this.add(statsLabel);
    }

    @Override
    public void handleNewLoginState(LoginState loginState) {
        if (loginState == LoginState.LOGGED_IN) {
            doFetchStats();
        }
    }

    private void doFetchStats() {
        statsLabel.setText("Querying stats...");
        getGame().getPlatform().getStats(result -> {
            if (result == null) {
                statsLabel.setText("Got an error fetching stats; see logs.");
                return;
            }
            StringBuilder statsString = new StringBuilder("Player stats: " + result.stats.length);
            for (Stat stat : result.stats) {
                statsString.append("\n - ").append(stat.name).append(" : ").append(stat.value);
            }
            statsLabel.setText(statsString.toString());
        });
    }
}
