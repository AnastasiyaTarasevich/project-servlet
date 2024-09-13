package com.tictactoe;

public class MiniMax {

    private int evaluate(Field field) {
        Sign winner = field.checkWin();
        if (winner == Sign.CROSS) return -10; // Минимизирующий игрок выигрывает
        if (winner == Sign.NOUGHT) return 10; // Максимизирующий игрок выигрывает
        return 0; // Ничья
    }
    private int minimax(Field field, boolean isMaximizing) {
        int score = evaluate(field);

        // Если игра завершена
        if (score == 10 || score == -10) return score;
        if (field.getEmptyFieldIndex() == -1) return 0; // Ничья

        int best;
        if (isMaximizing) {
            best = Integer.MIN_VALUE;
            for (int i : field.getEmptyIndices()) {
                field.getField().put(i, Sign.NOUGHT);
                best = Math.max(best, minimax(field, !isMaximizing));
                field.getField().put(i, Sign.EMPTY);
            }
        } else {
            best = Integer.MAX_VALUE;
            for (int i : field.getEmptyIndices()) {
                field.getField().put(i, Sign.CROSS);
                best = Math.min(best, minimax(field, !isMaximizing));
                field.getField().put(i, Sign.EMPTY);
            }
        }
        return best;
    }

    // Метод для нахождения лучшего хода
    public int findBestMove(Field field) {
        int bestVal = Integer.MIN_VALUE;
        int bestMove = -1;
        for (int i : field.getEmptyIndices()) {
            field.getField().put(i, Sign.NOUGHT);
            int moveVal = minimax(field, false);
            field.getField().put(i, Sign.EMPTY);
            if (moveVal > bestVal) {
                bestMove = i;
                bestVal = moveVal;
            }
        }
        return bestMove;
    }
}
