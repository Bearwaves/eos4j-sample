package com.bearwaves.eos4jsample.ui;

import com.badlogic.gdx.Gdx;
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
import com.bearwaves.eos4jsample.ecom.Offer;

public class CatalogScreen extends ContentScreen {

    private final Table sidebar;
    private final Label infoLabel;

    public CatalogScreen(GdxGame game, Container<ContentScreen> container, Skin skin) {
        super(game, container, skin, "Catalog");

        this.sidebar = new Table(skin);
        this.sidebar.setBackground(skin.newDrawable("white", Color.BLACK));
        this.sidebar.align(Align.topLeft);
        Table content = new Table(skin);
        content.align(Align.top);
        this.infoLabel = new Label("Fetching leaderboards...", skin);
        this.infoLabel.setWrap(true);
        Table offers = new Table(skin);
        offers.align(Align.topLeft);
        offers.defaults().space(Value.percentHeight(0.2f));

        this.add(sidebar).minWidth(Value.percentWidth(0.2f)).fill();
        this.add(content).grow();

        content.add(infoLabel).fill().row();
        content.add(offers).pad(Value.percentWidth(0.1f)).grow();
    }

    @Override
    public void handleNewLoginState(LoginState loginState) {
        if (loginState == LoginState.LOGGED_IN) {
            doFetchOffers();
        }
    }

    private void doFetchOffers() {
        getGame().getPlatform().getOffers(result -> {
            if (result == null) {
                Gdx.app.error("EcomScreen", "Something went wrong fetching offers - see logs.");
                infoLabel.setText("Something went wrong fetching offers - see logs.");
                return;
            }
            infoLabel.setText("Fetched " + result.offers.length + " offers.");
            for (Offer offer : result.offers) {
                TextButton button = new TextButton(offer.title, getSkin());
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Title: ").appendLine(offer.title);
                        builder.append("Description: ").appendLine(offer.description);
                        builder.append("Owned: ").appendLine(offer.owned ? "Yes" : "No");
                        builder.append("Price: ").appendLine(offer.price);
                        builder.append("Release date: ").appendLine(offer.releaseDate.toString());
                        infoLabel.setText(builder.toString());
                    }
                });
                sidebar.add(button).pad(Value.percentWidth(0.1f));
                sidebar.row();
            }
        });
    }

}
