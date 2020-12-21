package com.javarush.games.minesweeper;

        import com.javarush.engine.cell.*;

public class GameObject {
    public int x, y;
    public boolean isMine;
    public boolean isFlag;
    public boolean isOpen;
    public int countMineNeighbors;

    public GameObject(int x, int y, boolean isMine){
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }
}
