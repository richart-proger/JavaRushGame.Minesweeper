package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private static final int SIDE = 9;

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;

    int defaultCountMinesOnField = countMinesOnField;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
//        super.onMouseLeftClick(x, y);
        if (isGameStopped){
            restart();
            return;
        }
        openTile(x, y);
    }

    @Override
    public void onMouseRightClick (int x, int y) {
//        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                setCellValue(x , y, "");
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        List<GameObject> listMines;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];
                if (!gameObject.isMine) {
                    listMines = getNeighbors(gameObject);
                    for (int i = 0; i < listMines.size(); i++){
                        if (listMines.get(i).isMine) {
                            gameObject.countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile (int x, int y) {
        GameObject field = gameField[y][x]; // объявление переменной по нашему объекту

        if (field.isOpen || field.isFlag || isGameStopped)
            return;

        field.isOpen = true;
        setCellColor(x, y, Color.GREEN);
        countClosedTiles--;

        if (countClosedTiles == countMinesOnField && !field.isMine) { //   если элемент не является миной
            win();
        }

        if (field.isOpen && !field.isMine){
            score += 5;
            setScore(score);
        }
        
        if (field.isMine){                           // если элемент является миной
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            }
// далее если элемент не является миной
        else if (field.countMineNeighbors!=0) {      // и количество соседей-мин не равно нулю,
            setCellNumber(x, y, field.countMineNeighbors);
        }
        else {                                  // и количество соседей-мин равно нулю,
            List<GameObject> neighbhors = getNeighbors(field);
            setCellValue(field.x, field.y, "");
            for (GameObject neighbhor : neighbhors){
                if (!neighbhor.isOpen){
                    openTile(neighbhor.x,neighbhor.y);  // рекурсия
                }
            }
        }
    }

    private void markTile (int x, int y) {
        GameObject field = gameField[y][x];

        if (isGameStopped || field.isOpen || (countFlags == 0 && !field.isFlag)){
            return;
        }

        if (!field.isFlag){ //markTile(int, int) должен устанавливать значение поля isFlag в true, уменьшать количество неиспользованных флагов на единицу, отрисовывать на поле знак FLAG, если текущий элемент — не флаг (используй метод setCellValue(int, int, String)) и менять фон ячейки на поле, используя метод setCellColor(int, int, Color). Например, в Color.YELLOW.
            field.isFlag = true;
            countFlags--;
            setCellValue(field.x, field.y, FLAG);
            setCellColor(field.x, field.y, Color.YELLOW);
        }
        else { //markTile(int, int) должен устанавливать значение поля isFlag в false, увеличивать количество неиспользованных флагов на единицу, отрисовывать на поле пустую ячейку, если текущий элемент — флаг (используй метод setCellValue(int, int, String)) и возвращать исходный цвет ячейки (используй метод setCellColor(int, int, Color)).
            field.isFlag = false;
            countFlags++;
            setCellValue(field.x, field.y, "");
            setCellColor(field.x, field.y, Color.ORANGE);
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "BOOM!!! GAME OVER", Color.BLACK, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "You win! Congratulations!!!", Color.BLACK, 50);

    }

    private void restart () {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = defaultCountMinesOnField;
        score = 0;
        createGame();
        setScore(score);

    }
}