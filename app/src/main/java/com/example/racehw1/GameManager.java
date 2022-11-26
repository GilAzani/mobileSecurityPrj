package com.example.racehw1;

import java.util.Random;

public class GameManager {

    private boolean asteroidsLocation [][];//true if asteroid there otherwise false
    private int spaceshipLocation;
    private int lives;

    public GameManager(int spaceshipLocation, int rows, int cols, int lives) {
        this.spaceshipLocation = spaceshipLocation;
        this.lives = lives;
        initLocations(rows, cols);
    }

    private void initLocations(int rows, int cols) {
        asteroidsLocation = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                asteroidsLocation[i][j] = false; //at the start of the game no asteroids
            }
        }
    }


    public boolean[][] getAsteroidsLocation() {
        return asteroidsLocation;
    }

    public int getLives() {
        return lives;
    }

    public void changeSpaceshipLocation(int newPosition){
        spaceshipLocation = newPosition;
    }

    public boolean checkCrash(){
        if(asteroidsLocation[0][spaceshipLocation]){//if there is an asteroid at the ship's position it's a crash
            lives-=1;
            return true;
        }
        return false;
    }

    public void newAsteroidAndUpdate(){
        Random rand = new Random();
        int asteroid = rand.nextInt(asteroidsLocation[0].length*2);//wil have a 50% chance of an asteroid
        if(asteroid<asteroidsLocation[0].length) {
            updateLocations(asteroid, true);//will add the asteroid in a new line and update the rest
        }else{
            updateLocations(0,false);//will not add any asteroid because, the state is false
        }
    }

    private void updateLocations(int asteroid, boolean state) {
        for (int i = 0; i < asteroidsLocation.length-1; i++) {//will update all besides the new row
            for (int j = 0; j < asteroidsLocation[i].length; j++) {
                asteroidsLocation[i][j] = asteroidsLocation[i+1][j];//copy above row to curr row
            }
        }
        for (int i = 0; i < asteroidsLocation[asteroidsLocation.length-1].length; i++) {
            asteroidsLocation[asteroidsLocation.length-1][i] = false;//init new row as false
        }
        asteroidsLocation[asteroidsLocation.length-1][asteroid] = state;//change the new asteroid position
    }

}
