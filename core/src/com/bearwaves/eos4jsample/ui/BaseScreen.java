package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bearwaves.eos4jsample.GdxGame;
import com.bearwaves.eos4jsample.LoginState;

public class BaseScreen {

    private final GdxGame game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final Table tabRow;
    private final Container<ContentScreen> content;

    // Screens
    private final AuthScreen authScreen;
    private final StatsScreen statsScreen;
    // Buttons
    private final TextButton authButton;
    private final TextButton statsButton;
    private LoginState loginState;

    public BaseScreen(GdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        this.loginState = LoginState.NOT_LOGGED_IN;
        Gdx.app.getInput().setInputProcessor(this.stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Skin skin = createSkin();

        this.tabRow = new Table();
        table.add(tabRow).left();
        table.row();
        tabRow.defaults().space(Value.percentHeight(0.5f));

        this.authButton = new TextButton("Auth", skin);
        this.authButton.pad(Value.percentHeight(0.2f));
        this.authButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                content.setActor(authScreen);
            }
        });
        this.statsButton = new TextButton("Stats", skin);
        this.statsButton.pad(Value.percentHeight(0.2f));
        this.statsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                content.setActor(statsScreen);
            }
        });

        tabRow.add(this.authButton);
        this.authScreen = new AuthScreen(game, skin);
        this.statsScreen = new StatsScreen(game, skin);

        this.content = new Container<>();
        table.add(content).grow();
        this.content.setActor(this.authScreen);
        this.content.align(Align.bottomLeft);
    }

    public void render() {
        LoginState newLoginState = game.getPlatform().getLoginState();
        if (newLoginState != loginState) {
            loginState = newLoginState;
            handleNewLoginState(loginState);
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private Skin createSkin() {
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        skin.add("default", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.WHITE);
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = skin.newDrawable("white", Color.BLACK);
        textFieldStyle.cursor = skin.newDrawable("white", Color.LIGHT_GRAY);
        textFieldStyle.selection = skin.newDrawable("white", Color.LIGHT_GRAY);
        skin.add("default", textFieldStyle);

        return skin;
    }

    private void handleNewLoginState(LoginState loginState) {
        authScreen.handleNewLoginState(loginState);
        statsScreen.handleNewLoginState(loginState);
        populateTabRow(loginState);
    }

    private void populateTabRow(LoginState loginState) {
        this.tabRow.clearChildren();
        this.tabRow.add(this.authButton);

        if (loginState == LoginState.LOGGED_IN) {
            this.tabRow.add(this.statsButton);
        }
    }

}
