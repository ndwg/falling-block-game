package com.fallingblock.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class GameScreen implements Screen {
    final FallingBlock game;

    //String imageID;

    Texture iImage,jImage,lImage,oImage,sImage,tImage,zImage,blockImage;
    OrthographicCamera camera;
    Array<Rectangle> blocks;
    public GameScreen(final FallingBlock game) {
        this.game = game;
        
        iImage = new Texture(Gdx.files.internal("I.png"));
        jImage = new Texture(Gdx.files.internal("J.png"));
        lImage = new Texture(Gdx.files.internal("L.png"));
        oImage = new Texture(Gdx.files.internal("O.png"));
        sImage = new Texture(Gdx.files.internal("S.png"));
        tImage = new Texture(Gdx.files.internal("T.png"));
        zImage = new Texture(Gdx.files.internal("Z.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false,600,800);
        
        blocks = new Array<>();
        spawnBlock();
    }

    private void spawnBlock() {
        Rectangle block = new Rectangle();
        block.x = Gdx.graphics.getWidth()/2;
        block.y = Gdx.graphics.getHeight();
        int blockID = MathUtils.random(6);
        if(blockID==0){
            block.height = 24;
            block.width = 96;
            blockImage = iImage;
        }
        else if(blockID==1){
            block.height = 48;
            block.width = 72;
            blockImage = jImage;
        }
        else if(blockID==2){
            block.height = 48;
            block.width = 72;
            blockImage = lImage;
        }
        else if(blockID==3){
            block.height = 48;
            block.width = 48;
            blockImage = oImage;
        }
        else if(blockID==4){
            block.height = 48;
            block.width = 72;
            blockImage = sImage;
        }
        else if(blockID==5){
            block.height = 48;
            block.width = 72;
            blockImage = tImage;
        }
        else if(blockID==6){
            block.height = 48;
            block.width = 72;
            blockImage = zImage;
        }

        blocks.add(block);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0.4f,1);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        for(Rectangle block : blocks){
            game.batch.draw(blockImage,block.x,block.y);
        }
        game.batch.end();

        Iterator<Rectangle> iter = blocks.iterator();
        while(iter.hasNext()){
            Rectangle block = iter.next();
            block.y -= 100*Gdx.graphics.getDeltaTime();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        iImage.dispose();
        jImage.dispose();
        lImage.dispose();
        oImage.dispose();
        sImage.dispose();
        tImage.dispose();
        zImage.dispose();
    }
}