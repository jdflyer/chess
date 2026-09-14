package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 */
public class ChessPosition {
    int row;
    int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return a new position where the offset is given
     */
    public ChessPosition getOffset(int row, int col) {
        return new ChessPosition(getRow()+row,getColumn()+col);
    }

    /**
     * @return true if position is in bounds of the field
     */
    public boolean isValid() {
        return row >= 1 && col >= 1 && row <= ChessBoard.height && col <= ChessBoard.width;
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + getRow() +
                ", col=" + getColumn() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
