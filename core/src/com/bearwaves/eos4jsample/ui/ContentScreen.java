package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;

public abstract class ContentScreen extends Table {

    private final GdxGame game;
    private final String name;

    public ContentScreen(GdxGame game, Skin skin, String name) {
        super(skin);
        this.game = game;
        this.name = name;
        this.setFillParent(true);
        this.setBackground(skin.newDrawable("white", Color.DARK_GRAY));
        this.pad(Value.percentHeight(0.05f));
        this.defaults().spaceTop(20);
    }

    public GdxGame getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    public abstract void handleNewLoginState(LoginState loginState);
}
