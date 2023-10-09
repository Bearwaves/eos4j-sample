package com.bearwaves.eos4jsample;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bearwaves.eos4jsample.ui.BaseScreen;

public class GdxGame extends ApplicationAdapter {
    private final PlatformManager platform;
    private BaseScreen baseScreen;

    public GdxGame(PlatformManager platform) {
        this.platform = platform;
    }

    @Override
    public void create() {
        platform.init();
        baseScreen = new BaseScreen(this);
    }

    @Override
    public void render() {
        platform.tick();

        ScreenUtils.clear(0, 0, 0, 1);
        baseScreen.render();
    }

    @Override
    public void dispose() {
        platform.dispose();
        baseScreen.dispose();
    }

    @Override
    public void resize(int width, int height) {
        baseScreen.resize(width, height);
    }
}
