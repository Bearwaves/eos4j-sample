package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bearwaves.eos4jsample.GdxGame;

public class BaseScreen {

    private final GdxGame game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final Table content;
    private final Label loadingLabel;

    public BaseScreen(GdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), this.batch);
        Gdx.app.getInput().setInputProcessor(this.stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Skin skin = createSkin();

        Table tabRow = new Table();
        table.add(tabRow).left();
        table.row();
        tabRow.defaults().space(Value.percentHeight(0.5f));

        this.content = new Table();
        table.add(content).grow();

        this.loadingLabel = new Label("Starting up...", skin);
        this.loadingLabel.setAlignment(Align.center);
        content.add(this.loadingLabel).grow();
        content.setDebug(true);
    }

    public void render() {
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
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);

        return skin;
    }

}
