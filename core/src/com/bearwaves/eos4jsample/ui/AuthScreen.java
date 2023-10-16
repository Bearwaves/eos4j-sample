package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;

public class AuthScreen extends ContentScreen {

    private final Label loadingLabel;

    public AuthScreen(GdxGame game, Container<ContentScreen> container, Skin skin) {
        super(game, container, skin, "Auth");
        this.loadingLabel = new Label("Starting up...", skin);
        this.loadingLabel.setAlignment(Align.center);
        this.add(this.loadingLabel).grow();
    }

    @Override
    public void handleNewLoginState(LoginState loginState) {
        switch (loginState) {
            case LOGGING_IN_EPIC:
                this.loadingLabel.setText("Logging in to Epic Auth...");
                break;
            case LOGGING_IN_CONNECT:
                this.loadingLabel.setText("Logging in to Epic Online Services...");
                break;
            case FAILED:
                this.loadingLabel.setText("Login failed; see logs for details.");
                break;
            case LOGGED_IN:
                this.loadingLabel.setText("Logged in.\nUser ID: " + getGame().getPlatform().getUserId());
                break;
        }
    }
}
