package com.fallingblock.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class GameScreen implements Screen {
    final FallingBlock game;

    Texture iImage,jImage,lImage,oImage,sImage,tImage,zImage,panelImage,blockImage,backgroundImage,upcomingImage,gameOverImage,retryImage,exitImage;
    OrthographicCamera camera;
    long lastRightMoveTime = 2000000000, gravityTime = 2000000000, lastHardDropTime = 2000000000, lastSoftDropTime = 2000000000, lastRotationTime = 2000000000, lastLeftMoveTime = 2000000000;
    int blockID, blockState;
    Stack completedLines = new Stack(), hold = new Stack();
    int[][] board = new int[20][10];
    boolean holdLock = true, gameOver = false;
    int[] next = new int[3];
    //Stage stage;
    public GameScreen(final FallingBlock game) {
        this.game = game;
        
        iImage = new Texture(Gdx.files.internal("I.png"));
        jImage = new Texture(Gdx.files.internal("J.png"));
        lImage = new Texture(Gdx.files.internal("L.png"));
        oImage = new Texture(Gdx.files.internal("O.png"));
        sImage = new Texture(Gdx.files.internal("S.png"));
        tImage = new Texture(Gdx.files.internal("T.png"));
        zImage = new Texture(Gdx.files.internal("Z.png"));
        panelImage = new Texture(Gdx.files.internal("panel.png"));
        blockImage = new Texture(Gdx.files.internal("block.png"));
        backgroundImage = new Texture(Gdx.files.internal("background.png"));
        upcomingImage = new Texture(Gdx.files.internal("upcoming.png"));
        gameOverImage = new Texture(Gdx.files.internal("gameover.png"));
        retryImage = new Texture(Gdx.files.internal("retry.png"));
        exitImage = new Texture(Gdx.files.internal("exit.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false,360, 480);

        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 10; j++){
                board[i][j] = 0;
            }
        }

        for(int i = 0; i < 3; i++){
            next[i] = MathUtils.random(6);
        }

        spawnBlock(MathUtils.random(6));
    }

    private void spawnBlock() {
        spawnBlock(next[0]);
        next[0] = next[1];
        next[1] = next[2];
        next[2] = MathUtils.random(6);
    }
    private void spawnBlock(int ID) {
        blockID = ID;
        blockState = 0;

        if(blockID==0){
            if(board[0][3] == 1 || board[0][4] == 1 || board[0][5] == 1 || board[0][6] == 1) {
                gameOver = true;
                return;
            }
            board[0][3] = 2;
            board[0][4] = 2;
            board[0][5] = 2;
            board[0][6] = 2;
        }
        else if(blockID==1){
            if(board[0][3] == 1 || board[1][3] == 1 || board[1][4] == 1 || board[1][5] == 1) {
                gameOver = true;
                return;
            }
            board[0][3] = 2;
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==2){
            if(board[1][3] == 1 || board[1][4] == 1 || board[1][5] == 1 || board[0][5] == 1) {
                gameOver = true;
                return;
            }
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
            board[0][5] = 2;
        }
        else if(blockID==3){
            if(board[0][4] == 1 || board[0][5] == 1 || board[1][4] == 1 || board[1][5] == 1) {
                gameOver = true;
                return;
            }
            board[0][4] = 2;
            board[0][5] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==4){
            if(board[0][5] == 1 || board[0][4] == 1 || board[1][4] == 1 || board[1][3] == 1) {
                gameOver = true;
                return;
            }
            board[0][5] = 2;
            board[0][4] = 2;
            board[1][4] = 2;
            board[1][3] = 2;
        }
        else if(blockID==5){
            if(board[0][4] == 1 || board[1][3] == 1 || board[1][4] == 1 || board[1][5] == 1) {
                gameOver = true;
                return;
            }
            board[0][4] = 2;
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==6){
            if(board[0][3] == 1 || board[0][4] == 1 || board[1][4] == 1 || board[1][5] == 1) {
                gameOver = true;
                return;
            }
            board[0][3] = 2;
            board[0][4] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(gameOver){
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        ScreenUtils.clear(0.05f,0,0.1f,0.5f);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(panelImage,240,0);
        game.batch.draw(backgroundImage,240,360);
        game.batch.draw(upcomingImage,240,0);
        if(!hold.isEmpty()){
            if((int)hold.peek()==0) {
                game.batch.draw(iImage,252,408);
            }
            else if((int)hold.peek()==1) {
                game.batch.draw(jImage,264,396);
            }
            else if((int)hold.peek()==2) {
                game.batch.draw(lImage,264,396);
            }
            else if((int)hold.peek()==3) {
                game.batch.draw(oImage,276,396);
            }
            else if((int)hold.peek()==4) {
                game.batch.draw(sImage,264,396);
            }
            else if((int)hold.peek()==5) {
                game.batch.draw(tImage,264,396);
            }
            else if((int)hold.peek()==6) {
                game.batch.draw(zImage,264,396);
            }
        }

        for(int i = 0; i < 3; i++){
            if(next[i] == 0) {
                game.batch.draw(iImage,252,288-(i*120));
            }
            else if(next[i] == 1) {
                game.batch.draw(jImage,264,276-(i*120));
            }
            else if(next[i] == 2) {
                game.batch.draw(lImage,264,276-(i*120));
            }
            else if(next[i] == 3) {
                game.batch.draw(oImage,276,276-(i*120));
            }
            else if(next[i] == 4) {
                game.batch.draw(sImage,264,276-(i*120));
            }
            else if(next[i] == 5) {
                game.batch.draw(tImage,264,276-(i*120));
            }
            else if(next[i] == 6) {
                game.batch.draw(zImage,264,276-(i*120));
            }
        }

        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 10; j++){
                if(board[i][j] > 0){
                    game.batch.draw(blockImage,j*24,(19-i)*24);
                }
            }
        }

        /*if(gameOver){
            game.batch.draw(gameOverImage,126,144);
            //game.batch.draw(exitImage,150,154);
            //game.batch.draw(retryImage,150,199);
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(retryImage));
            ImageButton playButton = new ImageButton(drawable);
            //game.batch.draw(playButton,0,0);
            ImageButton.setPosition(0,0);
            stage.addActor(playButton);
        }*/
        game.batch.end();

        completedLines = checkForCompletedLines();

        if(!completedLines.isEmpty()) removeCompletedLines(completedLines);

        //if(Gdx.input.isTouched(retryImage))

        if(Gdx.input.isKeyPressed(Input.Keys.C) && holdLock){
            Point2D coords[] = findTetromino();

            if(!hold.isEmpty()){
                setGridValues(coords,0);
                int heldBlock = (int)hold.pop();
                hold.push(blockID);
                spawnBlock(heldBlock);
            }
            else{
                setGridValues(coords,0);
                hold.push(blockID);
                spawnBlock();
            }

            holdLock = false;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && TimeUtils.nanoTime() - lastLeftMoveTime > 210000000){
            lastLeftMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkXBounds(coords, 0) && checkCollisionLeft(coords)){
                for(int i = 0; i < coords.length; i++){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()][(int)coords[i].getX()-1] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && TimeUtils.nanoTime() - lastRightMoveTime > 210000000){
            lastRightMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkXBounds(coords, 9) && checkCollisionRight(coords)){
                for(int i = coords.length-1; i >= 0; i--){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()][(int)coords[i].getX()+1] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && TimeUtils.nanoTime() - lastSoftDropTime > 180000000){
            lastSoftDropTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkYBounds(coords,19) && checkCollisionBelow(coords)){
                for(int i = coords.length-1; i >= 0; i--){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()+1][(int)coords[i].getX()] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP) && TimeUtils.nanoTime() - lastRotationTime > 210000000) {
            boolean rotatationCleared = false;
            lastRotationTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino(), rotatedCoordsArray = new Point2D[0];
            List<Point2D> rotatedCoords = new ArrayList<>();

            if(blockID == 0){
                if(blockState == 0){
                    for(int i = 0; i < 4; i++){
                        rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()-2+i));
                    }

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    for(int i = 0; i < 4; i++){
                        rotatedCoords.add(new Point2D.Double(coords[2].getX()-2+i,coords[2].getY()));
                    }

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }
            else if(blockID == 1){
                if(blockState == 0){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[1].getY()+1));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX()-1,coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[3].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 2;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 2){
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()-1));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[3].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[3].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 3;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 3){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX()+1,coords[1].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }
            else if(blockID == 2){
                if(blockState == 0){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()+1));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[2].getY()+1));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    rotatedCoords.add(new Point2D.Double(coords[1].getX()-1,coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX()-1,coords[2].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 2;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 2){
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[0].getY()-1));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[0].getY()-1));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[3].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 3;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 3){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX()+1,coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX()+1,coords[2].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }
            else if(blockID == 3){}
            else if(blockID == 4){
                if(blockState == 0){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[3].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[2].getY()+1));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX()+1,coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }
            else if(blockID == 5){
                if(blockState == 0){
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[3].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[2].getY()+1));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    rotatedCoords.add(new Point2D.Double(coords[0].getX()-1,coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[3].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 2;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 2){
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[0].getY()-1));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[3].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 3;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 3){
                    rotatedCoords.add(new Point2D.Double(coords[0].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[1].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[0].getX()+1,coords[2].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }
            else if(blockID == 6){
                if(blockState == 0){
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[3].getX(),coords[3].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()+1));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 1;
                        rotatationCleared = true;
                    }
                }
                else if(blockState == 1){
                    rotatedCoords.add(new Point2D.Double(coords[1].getX()-1,coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[0].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[1].getX(),coords[2].getY()));
                    rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()));

                    rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        blockState = 0;
                        rotatationCleared = true;
                    }
                }
            }

            if(rotatationCleared){
                setGridValues(coords,0);
                setGridValues(rotatedCoordsArray, 2);
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && TimeUtils.nanoTime() - lastHardDropTime > 600000000){
            lastHardDropTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            while(checkYBounds(coords,19) && checkCollisionBelow(coords)){
                for(int i = 3; i >= 0; i--){
                    board[(int)coords[i].getY()+1][(int)coords[i].getX()] = 2;
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                }
                coords = findTetromino();
            }
        }

        if(TimeUtils.nanoTime() - gravityTime > 750000000){
            gravityTime= TimeUtils.nanoTime();

            Point2D[] coords = findTetromino();

            if(checkYBounds(coords,19) && checkCollisionBelow(coords)){
                for(int i = coords.length-1; i >= 0; i--){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()+1][(int)coords[i].getX()] = 2;
                }
            }

            else paralyze();
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
        blockImage.dispose();
        backgroundImage.dispose();
        panelImage.dispose();
        upcomingImage.dispose();
    }

    public void paralyze() {
        Point2D[] coords = findTetromino();

        for(int i = 0; i < coords.length; i++){
            board[(int)coords[i].getY()][(int)coords[i].getX()] = 1;
        }

        holdLock = true;
        spawnBlock();
    }

    public Point2D[] findTetromino(){
        List<Point2D> coordinates = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 10; j++){
                if(board[i][j] == 2){
                    coordinates.add(new Point2D.Double(j,i));
                }
            }
        }

        Point2D[] coords = coordinates.toArray(new Point2D[0]);

        return coords;
    }

    public boolean checkXBounds(Point2D[] coords, int xBound){
        for(int i = 0; i < coords.length; i++){
            if(coords[i].getX() == xBound) return false;
        }
        return true;
    }

    public boolean checkYBounds(Point2D[] coords, int yBound){
        for(int i = 0; i < coords.length; i++){
            if(coords[i].getY() == yBound) return false;
        }
        return true;
    }

    public boolean checkCollisionLeft(Point2D[] coords){
        for(int i = 0; i < coords.length; i++){
            if(board[(int)coords[i].getY()][(int)coords[i].getX()-1] == 1) return false;
        }
        return true;
    }

    public boolean checkCollisionRight(Point2D[] coords){
        for(int i = 0; i < coords.length; i++){
            if(board[(int)coords[i].getY()][(int)coords[i].getX()+1] == 1) return false;
        }
        return true;
    }

    public boolean checkCollisionBelow(Point2D[] coords){
        for(int i = 0; i < coords.length; i++){
            if(board[(int)coords[i].getY()+1][(int)coords[i].getX()] == 1) return false;
        }
        return true;
    }

    public boolean checkClearance(Point2D[] coords){
        for(int i = 0; i < coords.length; i++){
            if(coords[i].getY() > 19||coords[i].getY() < 0||coords[i].getX() > 9||coords[i].getX() < 0) return false;
            if(board[(int)coords[i].getY()][(int)coords[i].getX()] == 1) return false;
        }
        return true;
    }

    public void setGridValues(Point2D[] coords, int value){
        for(int i = 0; i < coords.length; i++){
            board[(int)coords[i].getY()][(int)coords[i].getX()] = value;
        }
    }

    public Stack checkForCompletedLines(){
        Stack completedLines = new Stack();

        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == 0 || board[i][j] == 2) break;
                if(j == 9) completedLines.push(i);
            }
        }

        return completedLines;
    }

    public void removeCompletedLines(Stack lines){
        int offset = 0;

        while(!lines.isEmpty()){
            for(int i = 0; i < 10; i++){
                board[(int)lines.peek()+offset][i] = 0;
            }
            for(int i = (int)lines.peek()+offset; i > 0; i--){
                for(int j = 0; j < 10; j++){
                    if(board[i-1][j] == 1){
                        board[i-1][j] = 0;
                        board[i][j] = 1;
                    }
                }
            }

            lines.pop();
            offset++;
        }
    }

}