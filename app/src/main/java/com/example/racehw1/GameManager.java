package com.example.racehw1;

public class GameManager {

    private int lives;
    private State state;


    public GameManager(int spaceshipLocation, int rows, int cols, int lives) {
        this.lives = lives;
        state = new State(spaceshipLocation, rows, cols);
    }

    public boolean[][] getAsteroidsLocation() {
        return state.getAsteroidsLocation();
    }

    public int getLives() {
        return lives;
    }

    public void changeSpaceshipLocation(int newPosition){
        state.changeSpaceshipLocation(newPosition);
    }

    public boolean checkCrash(){
        if(state.checkCrash()){
            lives-=1;
            return true;
        }
        return false;
    }

    public void newAsteroidAndUpdate(){
        state.newAsteroidAndUpdate();
    }

    public int getSpaceshipLocation() {
        return state.getSpaceshipLocation();
    }
}
