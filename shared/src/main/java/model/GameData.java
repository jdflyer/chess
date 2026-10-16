package model;

import chess.ChessGame;

public record GameData(int gameID, int whiteUsername, int blackUsername, int gameName, ChessGame game) {
}
