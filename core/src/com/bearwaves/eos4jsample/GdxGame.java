package com.bearwaves.eos4jsample;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GdxGame extends ApplicationAdapter {
    private final PlatformManager platform;
    private SpriteBatch batch;
    private Texture img;

    public GdxGame(PlatformManager platform) {
        this.platform = platform;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        platform.init();
    }

    @Override
    public void render() {
        platform.tick();

        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();

        platform.dispose();
    }
}
