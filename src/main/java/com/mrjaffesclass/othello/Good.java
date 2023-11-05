/*
 * A player for testing purposes
 * Copyright 2017 Roger Jaffe
 * All rights reserved
 */

package com.mrjaffesclass.othello;

import java.util.ArrayList;

/**
 * Test player
 */
public class Good extends Player {

    /**
     * Constructor
     * @param name Player's name
     * @param color Player color: one of Constants.BLACK or Constants.WHITE
     */
    public Good(int color) {
        super(color);
    }

    /**
     *
     * @param board
     * @return The player's next move
     */
    @Override
    public Position getNextMove(Board board) {
        ArrayList<Position> list = this.getLegalMoves(board);
        ArrayList<Position> backup = this.getLegalMoves(board);
        for (int i = 0; i < list.size(); i++) {
            if (isDanger(board, list.get(i))) {
                list.remove(list.get(i));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (isCorner(board, list.get(i))) {
                System.out.println("returned isCorner: " + list.get(i).getRow() + ", " + list.get(i).getCol());
                return list.get(i);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (isEdge(board, list.get(i))) {
                System.out.println("returned isEdge: " + list.get(i).getRow() + ", " + list.get(i).getCol());
                return list.get(i);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (!canBeTaken(board, list.get(i))) {
                System.out.println("return cant be taken: " + list.get(i).getRow() + ", " + list.get(i).getCol());
                return list.get(i);
            }
        }
        if (list.size() > 0) {
            int idx = (int) (Math.random() * list.size());
            return list.get(idx);
        } else if (backup.size() > 0) {
            int idx = (int) (Math.random() * backup.size());
            return backup.get(idx);
        }
        else return null;
    }
    private boolean isCorner(Board board, Position positionToCheck) {
         if (positionToCheck.getRow() == 0 && positionToCheck.getCol() == 7) {
             return true; 
         }
         if (positionToCheck.getRow() == 0 && positionToCheck.getCol() == 0) {
             return true; 
         }
         if (positionToCheck.getRow() == 7 && positionToCheck.getCol() == 7) {
             return true; 
         }
         if (positionToCheck.getRow() == 7 && positionToCheck.getCol() == 0) {
             return true; 
         }
         return false;
    }
    private boolean canBeTaken(Board board, Position pos) {
        board.makeMove(new Player(this.getColor()), pos);
        ArrayList<Position> list = this.getOtherPlayerLegalMoves(board);
        for (int i = 0; i < list.size(); i++) {
            Board testBoard = board;
            Player opp = new Player(this.getColor());
            opp.flipColor();
            testBoard.makeMove(opp, list.get(i));
            if (testBoard.getSquare(pos).getStatus() == opp.getColor()) {
                return true;
            }
        }
        return false;
    }
    private boolean isEdge(Board board, Position positionToCheck) {

        if (positionToCheck.getRow() == 0 || positionToCheck.getRow() == 7) {
            return true;
        }
        else if (positionToCheck.getCol() == 0 || positionToCheck.getCol() == 7) {
            return true;
        }
        else return false;
    }
    private ArrayList<Position> getOtherPlayerLegalMoves(Board board) {
        ArrayList<Position> list = new ArrayList<>();
        Player otherPlayer = new Player(this.getColor());
        otherPlayer.flipColor();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                if (board.isLegalMove(otherPlayer, pos)) {
                    list.add(pos);
                }
            }
        }
        return list;

    }
    private boolean isDanger(Board board, Position positionToCheck) {
        if (positionToCheck.getRow() == 0 && positionToCheck.getCol() == 1) {
            return true;
        }
        else if (positionToCheck.getRow() == 1 && positionToCheck.getCol() == 0) {
            return true;
        }
        else if (positionToCheck.getRow() == 0 && positionToCheck.getCol() == 6) {
            return true;
        }
        else if (positionToCheck.getRow() == 1 && positionToCheck.getCol() == 7) {
            return true;
        }
        else if (positionToCheck.getRow() == 6 && positionToCheck.getCol() == 0) {
            return true;
        }
        else if (positionToCheck.getRow() == 7 && positionToCheck.getCol() == 1) {
            return true;
        }
        if (positionToCheck.getRow() == 7 && positionToCheck.getCol() == 6) {
            return true;
        }
        else if (positionToCheck.getRow() == 6 && positionToCheck.getCol() == 7) {
            return true;
        }
        if (positionToCheck.getCol() == 1 || positionToCheck.getCol() == 6 || 
        positionToCheck.getRow() == 1 || positionToCheck.getRow() == 6) {
            if (positionToCheck.getCol() >= 1 && positionToCheck.getCol() <= 6 && 
            positionToCheck.getRow() >= 1 && positionToCheck.getRow() <= 6) {
                return true;              
            }
        }
        return false;
    }

    /**
     * Is this a legal move?
     * @param player Player asking
     * @param positionToCheck Position of the move being checked
     * @return True if this space is a legal move
     */
    private boolean isLegalMove(Board board, Position positionToCheck) {
        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            if (step(board, positionToCheck, directionVector, 0)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Traverses the board in the provided direction. Checks the status of
     * each space: 
     * a. If it's the opposing player then we'll move to the next
     *    space to see if there's a blank space
     * b. If it's the same player then this direction doesn't represent
     *    a legal move
     * c. If it's a blank AND if it's not the adjacent square then this
     *    direction is a legal move. Otherwise, it's not.
     * 
     * @param player  Player making the request
     * @param position Position being checked
     * @param direction Direction to move
     * @param count Number of steps we've made so far
     * @return True if we find a legal move
     */
    private boolean step(Board board, Position position, Position direction, int count) {
        Position newPosition = position.translate(direction);
        int color = this.getColor();
        if (newPosition.isOffBoard()) {
            return false;
        } else if (board.getSquare(newPosition).getStatus() == -color) {
            return this.step(board, newPosition, direction, count+1);
        } else if (board.getSquare(newPosition).getStatus() == color) {
            return count > 0;
        } else {
            return false;
        }
    }

    /**
     * Get the legal moves for this player on the board
     * @param board
     * @return True if this is a legal move for the player
     */
    public ArrayList<Position> getLegalMoves(Board board) {
        int color = this.getColor();
        ArrayList list = new ArrayList<>();
        for (int row = 0; row < Constants.SIZE; row++) {
            for (int col = 0; col < Constants.SIZE; col++) {
                if (board.getSquare(this, row, col).getStatus() == Constants.EMPTY) {
                    Position testPosition = new Position(row, col);
                    if (this.isLegalMove(board, testPosition)) {
                        list.add(testPosition);
                    }
                }        
            }
        }
        return list;
    }

}
