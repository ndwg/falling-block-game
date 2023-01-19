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
    long lastMoveTime = 2000000000, gravityTime = 2000000000;
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

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && TimeUtils.nanoTime() - lastMoveTime > 210000000){
            lastMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkXBounds(coords, 0) && checkCollisionLeft(coords)){
                for(int i = 0; i < coords.length; i++){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()][(int)coords[i].getX()-1] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && TimeUtils.nanoTime() - lastMoveTime > 210000000){
            lastMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkXBounds(coords, 9) && checkCollisionRight(coords)){
                for(int i = coords.length-1; i >= 0; i--){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()][(int)coords[i].getX()+1] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && TimeUtils.nanoTime() - lastMoveTime > 180000000){
            lastMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();

            if(checkYBounds(coords,19) && checkCollisionBelow(coords)){
                for(int i = coords.length-1; i >= 0; i--){
                    board[(int)coords[i].getY()][(int)coords[i].getX()] = 0;
                    board[(int)coords[i].getY()+1][(int)coords[i].getX()] = 2;
                }
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP) && TimeUtils.nanoTime() - lastMoveTime > 180000000) {
            lastMoveTime = TimeUtils.nanoTime();
            Point2D[] coords = findTetromino();
            List<Point2D> rotatedCoords = new ArrayList<>();

            if(blockID == 0){
                if(blockState == 0){
                    for(int i = 0; i < 4; i++){
                        rotatedCoords.add(new Point2D.Double(coords[2].getX(),coords[2].getY()-2+i));
                    }

                    Point2D[] rotatedCoordsArray = rotatedCoords.toArray(new Point2D[0]);

                    if(checkClearance(rotatedCoordsArray)){
                        setGridValues(coords,0);
                        setGridValues(rotatedCoordsArray, 2);
                        blockState = 1;
                    }
                }
            }
        }

        if(TimeUtils.nanoTime() - gravityTime > 750000000){
            gravityTime= TimeUtils.nanoTime();

            for(int i = 19; i >= 0; i--){
                for(int j = 0; j < 10; j++){
                    if(board[i][j] == 2 && i != 19 && board[i+1][j] != 1){
                        board[i][j] = 0;
                        board[i+1][j] = 2;
                    }
                    else if(board[i][j] == 2){
                        if(i!=19) {
                            j--;

                            while (j >= 0) {
                                if (board[i + 1][j] == 2) {
                                    board[i][j] = 2;
                                    board[i + 1][j] = 0;
                                }
                                j--;
                            }
                        }
                        paralyze();
                        break;
                    }
                }
            }
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
