package model;

public class CoinAndAsteroid {
    private boolean isAsteroid;//flag to indicate asteroid
    private boolean isCoin;//flag to indicate coin

    public CoinAndAsteroid(boolean isAsteroid, boolean isCoin) {
        this.isAsteroid = isAsteroid;
        this.isCoin = isCoin;
    }

    public boolean isAsteroid() {
        return isAsteroid;
    }

    public CoinAndAsteroid setAsteroid(boolean asteroid) {
        isAsteroid = asteroid;
        return this;
    }

    public boolean isCoin() {
        return isCoin;
    }

    public CoinAndAsteroid setCoin(boolean coin) {
        isCoin = coin;
        return this;
    }
}
