package sudoku;

import java.util.*;
import java.lang.*;
import java.io.*;


public class Game {
	//represents the initial setup of the puzzle
	Board sudoku;

	//Represents a single cell of the puzzle
	public class Cell{
		private int row = 0;
		private int column = 0;

		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
		}
		public int getRow() {
			return row;
		}
		public int getColumn() {
			return column;
		}
	}

	public class Region{
		private Cell[] matrix;					//An array of cells (each element has a row and column coordinates)
		private int num_cells;  				//The size of the given region

		public Region(int num_cells) {          //Initializes the region 
			this.matrix = new Cell[num_cells];  
			this.num_cells = num_cells;
		}
		public Cell[] getCells() {				//Returns the given region array
			return matrix;
		}
		public void setCell(int pos, Cell element){ //Sets a new value at a given point in the matrix array
			matrix[pos] = element;
		}

	}

	public class Board{
		private int[][] board_values;		//Numbers displayed at each square
		private Region[] board_regions;		//An array of regions.  Each element is a region. 
		private int num_rows;				
		private int num_columns;
		private int num_regions;

		public Board(int num_rows,int num_columns, int num_regions){
			this.board_values = new int[num_rows][num_columns];		//Sets the outer dimensions of the board
			this.board_regions = new Region[num_regions];			//An array of regions.  Each element contains an array of coordinates which are within their region
			this.num_rows = num_rows;
			this.num_columns = num_columns;
			this.num_regions = num_regions;
		}

		public int[][] getValues(){		//Return the board
			return board_values;
		}
		public int getValue(int row, int column) { //Returns the number given the coordinates to a square
			return board_values[row][column];
		}
		public Region getRegion(int index) {	//Returns the coordinates of other square within the same region as the input number.  **The input number is the location in the Region array
			return board_regions[index];
		}
		public Region[] getRegions(){	        //Returns the full set of region	
			return board_regions;
		}
		public void setValue(int row, int column, int value){ //Sets a number given the coordinates
			board_values[row][column] = value;
		}
		public void setRegion(int index, Region initial_region) {
			board_regions[index] = initial_region;  	//Can change the squares within a region
		}	
		public void setValues(int[][] values) {			//Initializes all squares with the input array of values
			board_values = values;
		}

	}
	//A method which which iterate across the board. At every empty cell, it will check try all possible numbers
	public int[][] solver() {
		//cur_reg represents the current region.  Will iterate accross all of the regions in the game board. 
		for (Region cur_reg : sudoku.getRegions()) {
			//cur_cell represents the current cell(row,col) in the current region.  Will iterate accross all cells in the current region
			for(Cell cur_cell : cur_reg.matrix) {
				//Only checks empty cells
				if(sudoku.getValue(cur_cell.row, cur_cell.column)==-1) {
					for(int num=1; num<=cur_reg.num_cells; num++) {
						//If the current number is valid in the current cell, update the value
						if(isValid(cur_cell.row, cur_cell.column, num) && notInReg(cur_reg, num)) {
							sudoku.setValue(cur_cell.row, cur_cell.column, num);

							if (solver()==sudoku.getValues()) {
								return sudoku.getValues();
							} else {
								sudoku.setValue(cur_cell.row, cur_cell.column, -1);
							}
						}
					}
					return null;
				}
			}
		}
		return sudoku.getValues();
	}
		/*
		for (int row = 0; row < sudoku.num_rows ; row++) {		//iterates down a row			
			for (int col = 0 ; col < sudoku.num_columns; col++) { //Iterates across a row				
				if (sudoku.getValue(row, col) == -1) {	//If the cell is empty...
					for (int num=1; num<=sudoku.getRegion(getRegion2(row, col)).num_cells; num++) { 
						if (isValid(row,col,num)) {		//If the current number is valid it is added
							sudoku.setValue(row, col, num);
							if (row == (sudoku.num_rows-1) && col == (sudoku.num_columns-1)) {//Check to see if we are on the last cell
								return sudoku.getValues();
							}  
							if (solver()==sudoku.getValues()) {//Recursive call which checks if the current term addition led to the solution
								return sudoku.getValues();
							} else {  //If the current number did not lead to a solution, empty out the cell
								sudoku.setValue(row, col, -1); 	//Resets the current value to -1 if the current addition did not lead to a solution
							}
						}								
					}
					return null;
				}
			}
		}
		 */
	public boolean notInReg (Region reg, int num) {
		for (Cell cur_cell : reg.matrix) {
			if (sudoku.getValue(cur_cell.row, cur_cell.column)==num){
				return false;
			}
		}
		return true;
	}
	//Helper method which checks if a given input is valid.  Checks to see if the same number is present in the 8 surrounding cells
	public boolean isValid (int row, int col, int num) {
		//Initialize is Valid is true before running the checks.  If one of the checks fails the method will return false
		boolean isValid=true;
		//If there are neighbors to down
		if (row+1<sudoku.num_rows) {
			if(sudoku.getValue(row+1, col)==num) {
				isValid = false;
			}
		}		
		//If there are neighbors up
		if(row !=0) {
			if(sudoku.getValue(row-1, col)==num) {
				isValid = false;
			}
		}
		//If there are neighbors right
		if (col+1<sudoku.num_columns) {
			if(sudoku.getValue(row, col+1)==num) {
				isValid = false;
			}
		}		
		//If there are neighbors left
		if(col !=0) {
			if(sudoku.getValue(row, col-1)==num) {
				isValid = false;
			}
		}
		//If there are neighbors down and to the right 
		if(row+1<sudoku.num_rows && col+1<sudoku.num_columns) {
			if(sudoku.getValue(row+1, col+1)==num) {
				return false;
			}
		}
		//If there are neighbors up and to the right
		if(row!=0 && col!=0) {
			if(sudoku.getValue(row-1, col-1)==num) {
				return false;
			}
		}
		//If there are neighbors up and to the right
		if(row!=0 && col+1<sudoku.num_columns) {
			if(sudoku.getValue(row-1, col+1)==num) {
				return false;
			}
		}
		//If there are neighbors down and left
		if(row+1<sudoku.num_rows && col!=0) {
			if(sudoku.getValue(row+1, col-1)==num) {
				return false;
			}

		}
		
		return isValid;
	}	
	//A helper method which will return the current index in the region array based on input cell coordinates
	public int getRegion2(int row, int col) {
		for (int i=0; i<sudoku.board_regions.length; i++) {  //Iterates across the board regions
			for (int j=0; j<sudoku.board_regions[i].matrix.length; j++) { //Iterates across the cells which make up the region
				if (sudoku.board_regions[i].matrix[j].getRow()==row && sudoku.board_regions[i].matrix[j].getColumn()==col) {
					return i;
				}
			}
		}
		return -1;
	}
	public static void main(String[] args) {
		System.out.println("starting up");
		Scanner sc = new Scanner(System.in);
		int rows = sc.nextInt();
		int columns = sc.nextInt();
		int[][] board = new int[rows][columns];
		//Reading the board
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				String value = sc.next();
				if (value.equals("-")) {
					board[i][j] = -1;
				}else {
					try {
						board[i][j] = Integer.valueOf(value);
					}catch(Exception e) {
						System.out.println("Ups, something went wrong");
					}
				}	
			}
		}
		int regions = sc.nextInt();
		Game game = new Game();
		game.sudoku = game.new Board(rows, columns, regions);
		game.sudoku.setValues(board);
		for (int i=0; i< regions;i++) {
			int num_cells = sc.nextInt();
			Game.Region new_region = game.new Region(num_cells);
			for (int j=0; j< num_cells; j++) {
				String cell = sc.next();
				String value1 = cell.substring(cell.indexOf("(") + 1, cell.indexOf(","));
				String value2 = cell.substring(cell.indexOf(",") + 1, cell.indexOf(")"));
				Game.Cell new_cell = game.new Cell(Integer.valueOf(value1)-1,Integer.valueOf(value2)-1);
				new_region.setCell(j, new_cell);
			}
			game.sudoku.setRegion(i, new_region);
		}
		int[][] answer = game.solver();
		for (int i=0; i<answer.length;i++) {
			for (int j=0; j<answer[0].length; j++) {
				System.out.print(answer[i][j]);
				if (j<answer[0].length -1) {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}



}
