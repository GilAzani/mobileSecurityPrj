package model;

import java.util.Random;

public class State {
    private CoinAndAsteroid coinsAndAsteroidsLocation [][];//state of one block
    private int spaceshipLocation;
    private boolean isAddedYet = false;//is a flag that will make sure there is at least a
    // block distance to avoid trap position

    public State(int spaceshipLocation,int rows, int cols) {
        this.spaceshipLocation = spaceshipLocation;
        initLocations(rows,cols);
    }

    private void initLocations(int rows, int cols) {
        coinsAndAsteroidsLocation = new CoinAndAsteroid[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                coinsAndAsteroidsLocation[i][j] = new CoinAndAsteroid(false,false);
                //at the start of the game no asteroids and coins
            }
        }
    }


    public CoinAndAsteroid[][] getCoinsAndAsteroidsLocation() {
        return coinsAndAsteroidsLocation;
    }

    public int getSpaceshipLocation() {
        return spaceshipLocation;
    }

    public void changeSpaceshipLocation(int newPosition){
        spaceshipLocation = newPosition;
    }

    public boolean checkCrash(){
        if(coinsAndAsteroidsLocation[0][spaceshipLocation].isAsteroid()){//if there is an asteroid at the ship's position it's a crash
            //lives-=1;
            coinsAndAsteroidsLocation[0][spaceshipLocation].setAsteroid(false);//at crash the asteroid disappears
            return true;
        }
        return false;
    }

    public boolean checkCoin(){
        if(coinsAndAsteroidsLocation[0][spaceshipLocation].isCoin()){//checks if there is a coin at the ship's position
            coinsAndAsteroidsLocation[0][spaceshipLocation].setCoin(false);//after collection of the coin it's disappears
            return true;
        }
        return false;
    }

    public void newAsteroidAndUpdate(){
        Random rand = new Random();
        int asteroid = rand.nextInt(coinsAndAsteroidsLocation[0].length);//put asteroid in every second row
        int coin = rand.nextInt(coinsAndAsteroidsLocation[0].length);
        //if it's equal to asteroid position no coin added (80% to get a coin)
        if(!isAddedYet) {
            updateLocations(coin, asteroid, true);//will add the asteroid and coin in a new line and update the rest
            isAddedYet = true;//update asteroid added
        }else{
            updateLocations(0, 0,false);//will not add any asteroid or coins because, the state is false
            isAddedYet = false;//update empty new row
        }
    }

    private void updateLocations(int coin, int asteroid, boolean state) {

        copyCoinsAndAsteroidsRowDown();

        for (int i = 0; i < coinsAndAsteroidsLocation[coinsAndAsteroidsLocation.length-1].length; i++) {
            coinsAndAsteroidsLocation[coinsAndAsteroidsLocation.length-1][i].setAsteroid(false);//init new row as false
            coinsAndAsteroidsLocation[coinsAndAsteroidsLocation.length-1][i].setCoin(false);//init new row as false
        }
        if(coin != asteroid){
            coinsAndAsteroidsLocation[coinsAndAsteroidsLocation.length-1][coin].setCoin(state);
        }
        coinsAndAsteroidsLocation[coinsAndAsteroidsLocation.length-1][asteroid].setAsteroid(state);//change the new asteroid position
    }

    private void copyCoinsAndAsteroidsRowDown() {
        for (int i = 0; i < coinsAndAsteroidsLocation.length-1; i++) {//will update all besides the new row
            for (int j = 0; j < coinsAndAsteroidsLocation[i].length; j++) {
                coinsAndAsteroidsLocation[i][j].setAsteroid(coinsAndAsteroidsLocation[i+1][j].isAsteroid());
                //copy above asteroid row to curr row
                coinsAndAsteroidsLocation[i][j].setCoin(coinsAndAsteroidsLocation[i+1][j].isCoin());
                //copy above coin row to curr row
            }
        }
    }
}
