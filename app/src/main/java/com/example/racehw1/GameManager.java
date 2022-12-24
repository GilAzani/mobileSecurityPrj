package com.example.racehw1;

import model.CoinAndAsteroid;
import model.State;

public class GameManager {

    private int lives;
    private State state;
    private int score;


    public GameManager(int spaceshipLocation, int rows, int cols, int lives) {
        this.lives = lives;
        score = 0;
        state = new State(spaceshipLocation, rows, cols);
    }

    public CoinAndAsteroid[][] getAsteroidsLocation() {
        return state.getCoinsAndAsteroidsLocation();
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

    public void makeOneStep(){
        score ++;
    }

    public int getScore() {
        return  score;
    }

    public boolean checkCoin() {
        if(state.checkCoin()){
            score += 10;//10 pts for getting a coin
            return true;
        }
        return false;
    }
}
