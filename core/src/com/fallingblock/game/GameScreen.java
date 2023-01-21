package com.fallingblock.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    final FallingBlock game;

    //String imageID;

    Texture iImage,jImage,lImage,oImage,sImage,tImage,zImage,blockImage;
    OrthographicCamera camera;
    long lastRightMoveTime = 2000000000, gravityTime = 2000000000, lastHardDropTime = 2000000000, lastSoftDropTime = 2000000000, lastRotationTime = 2000000000, lastLeftMoveTime = 2000000000;
    int blockID, blockState;
    int[][] board = new int[20][10];
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
        camera.setToOrtho(false,240, 480);

        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 10; j++){
                board[i][j] = 0;
            }
        }

        spawnBlock();
    }

    private void spawnBlock() {
        blockID = MathUtils.random(6);
        blockState = 0;
        if(blockID==0){
            board[0][3] = 2;
            board[0][4] = 2;
            board[0][5] = 2;
            board[0][6] = 2;
        }
        else if(blockID==1){
            board[0][3] = 2;
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==2){
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
            board[0][5] = 2;
        }
        else if(blockID==3){
            board[0][4] = 2;
            board[0][5] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==4){
            board[0][5] = 2;
            board[0][4] = 2;
            board[1][4] = 2;
            board[1][3] = 2;
        }
        else if(blockID==5){
            board[0][4] = 2;
            board[1][3] = 2;
            board[1][4] = 2;
            board[1][5] = 2;
        }
        else if(blockID==6){
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
        ScreenUtils.clear(0.05f,0,0.1f,0.5f);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        for(int i = 0; i < 20; i++){
            for(int j = 0; j < 10; j++){
                if(board[i][j] > 0){
                    game.batch.draw(iImage,j*24,(19-i)*24);
                }
            }
        }
        game.batch.end();

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
    }

    public void paralyze() {
        Point2D[] coords = findTetromino();

        for(int i = 0; i < coords.length; i++){
            board[(int)coords[i].getY()][(int)coords[i].getX()] = 1;
        }

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

}