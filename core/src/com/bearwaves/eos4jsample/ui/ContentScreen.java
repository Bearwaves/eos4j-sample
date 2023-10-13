package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;

public abstract class ContentScreen extends Table {

    private final GdxGame game;
    private final String name;
    private final TextButton button;

    public ContentScreen(GdxGame game, Container<ContentScreen> container, Skin skin, String name) {
        super(skin);
        this.game = game;
        this.name = name;
        this.setFillParent(true);
        this.setBackground(skin.newDrawable("white", Color.DARK_GRAY));
        this.pad(Value.percentHeight(0.05f));
        this.defaults().spaceTop(20);
        this.button = new TextButton(name, skin);
        this.button.pad(Value.percentHeight(0.2f));
        this.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                container.setActor(ContentScreen.this);
            }
        });
    }

    public GdxGame getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public TextButton getButton() {
        return button;
    }

    public abstract void handleNewLoginState(LoginState loginState);
}
