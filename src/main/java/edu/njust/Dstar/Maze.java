package edu.njust.Dstar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class Maze {
	private int size_x;
	private int size_y;
	private Cell[][] cells;
	private Cell original_start;
	private Cell start;
	private Cell goal;
	private Cell robot_cell;
	private boolean diagonal;
	private int n_directions;
	private int iteration;
	private Key.Key_comparator cde;
	private TreeSet<Key> u;
	private static final int[] d_x = { 0, 1, -1, 0, 1, 1, -1, -1 };

	private static final int[] d_y = { 1, 0, 0, -1, -1, 1, 1, -1 };

	private static final String infinit = Character.toString('∞');
	private static final Color path_color = new Color(255, 100, 100, 255);
	private static final Color wall_color = new Color(100, 100, 100, 255);
	private static final Color visited_cell_color = new Color(150, 200, 150, 255);
	Color bgcolor = Color.white;

	public Maze(long paramLong, int paramInt1, int paramInt2) {
		Random localRandom = new Random(paramLong);
		this.iteration = 0;
		this.diagonal = false;
		this.n_directions = 4;
		this.cde = new Key.Key_comparator();
		this.u = new TreeSet<Key>(this.cde);
		this.size_x = paramInt1;
		this.size_y = paramInt2;
		this.cells = new Cell[paramInt1][paramInt2];
		for (int i = 0; i < paramInt1; i++) {
			for (int j = 0; j < paramInt2; j++) {
				this.cells[i][j] = new Cell(i, j);
			}
		}
		int i = (int) (localRandom.nextDouble() * 2147483647.0D) % paramInt1;
		int j = (int) (localRandom.nextDouble() * 2147483647.0D) % paramInt2;
		this.original_start = (this.robot_cell = this.start = this.cells[i][j]);
		int m;
		do {
			int k = (int) (localRandom.nextDouble() * 2147483647.0D) % paramInt1;
			m = (int) (localRandom.nextDouble() * 2147483647.0D) % paramInt2;
			this.goal = this.cells[k][m];
		} while ((this.start.x == this.goal.x) && (this.start.y == this.goal.y));
		for (int k = 0; k < paramInt1; k++) {
			for (m = 0; m < paramInt2; m++) {
				this.cells[k][m].h = (Math.abs(k - this.goal.x) + Math.abs(m - this.goal.y));
				this.cells[k][m].iteration = this.iteration;
				if ((this.cells[k][m] != this.start) && (this.cells[k][m] != this.goal)
						&& (localRandom.nextDouble() <= 0.25D))
					this.cells[k][m].real_type = 1;
			}
		}
	}

	private void Copy_maze() {
		for (int i = 0; i < this.size_x; i++) {
			for (int j = 0; j < this.size_y; j++)
				this.cells[i][j].type_robot_vision = this.cells[i][j].real_type;
		}
	}

	public void Set_diagonal(boolean paramBoolean) {
		this.diagonal = paramBoolean;
		if (paramBoolean)
			this.n_directions = 8;
		else
			this.n_directions = 4;
		Initialize();
	}

	public void Set_goal(int paramInt1, int paramInt2) {
		Point localPoint = Convert_coordinates_to_indexes(paramInt1, paramInt2);
		if (localPoint == null)
			return;
		paramInt1 = localPoint.x;
		paramInt2 = localPoint.y;
		if (this.cells[paramInt1][paramInt2] == this.start) {
			return;
		}

		this.goal = this.cells[paramInt1][paramInt2];
		this.goal.real_type = (this.goal.type_robot_vision = 0);
		Initialize();
	}

	public void Set_start(int paramInt1, int paramInt2) {
		Point localPoint = Convert_coordinates_to_indexes(paramInt1, paramInt2);
		if (localPoint == null)
			return;
		paramInt1 = localPoint.x;
		paramInt2 = localPoint.y;
		if (this.cells[paramInt1][paramInt2] == this.goal) {
			return;
		}

		this.robot_cell = (this.original_start = this.start = this.cells[paramInt1][paramInt2]);
		this.start.real_type = (this.start.type_robot_vision = 0);
		Initialize();
	}

	private Point Convert_coordinates_to_indexes(int paramInt1, int paramInt2) {
		if ((paramInt1 < 50) || (paramInt2 < 50))
			return null;
		paramInt1 -= 50;
		paramInt2 -= 50;
		int i = paramInt1 / 50;
		int j = paramInt2 / 50;
		if ((0 > i) || (i >= this.size_x) || (0 > j) || (j >= this.size_y)) {
			return null;
		}

		Point localPoint = new Point(i, j);
		return localPoint;
	}

	public void Print_cell(int paramInt1, int paramInt2) {
		Point localPoint = Convert_coordinates_to_indexes(paramInt1, paramInt2);
		if (localPoint == null) {
			return;
		}

		int i = localPoint.x;
		int j = localPoint.y;
		Cell localCell = this.cells[i][j];
		System.out.println("I am " + localCell + " and my parent is " + localCell.parent);
	}

	public void Transform_cell(int paramInt1, int paramInt2) {
		Point localPoint = Convert_coordinates_to_indexes(paramInt1, paramInt2);
		if (localPoint == null)
			return;
		int i = localPoint.x;
		int j = localPoint.y;
		Cell localCell = this.cells[i][j];
		if ((localCell != this.start) && (localCell != this.goal) && (localCell != this.robot_cell))
			if (localCell.real_type == 1)
				localCell.real_type = 0;
			else
				localCell.real_type = 1;
	}

	public Dimension Printed_maze_size() {
		return new Dimension(50 * (this.size_x + 1), 50 * (this.size_y + 1));
	}

	private void Print_cell_information(Graphics2D paramGraphics2D, Cell paramCell) {
		int i = 50 + paramCell.x * 50;
		int j = 50 + paramCell.y * 50;
		paramGraphics2D.setColor(Color.black);
		String str1;
		if (paramCell.g < 2147483647)
			str1 = Integer.toString(paramCell.g);
		else
			str1 = infinit;
		String str2;
		if (paramCell.rhs < 2147483647)
			str2 = Integer.toString(paramCell.rhs);
		else
			str2 = infinit;
		Rectangle2D localRectangle2D = paramGraphics2D.getFontMetrics()
				.getStringBounds(str1 + "/" + str2 + "/" + paramCell.h, paramGraphics2D);
		j += (50 + (int) localRectangle2D.getHeight()) / 2;
		i += (50 - (int) localRectangle2D.getWidth()) / 2;
		paramGraphics2D.drawString(str1 + "/" + str2 + "/" + paramCell.h, i, j);
	}

	private void Draw_robot(Graphics2D paramGraphics2D, Cell paramCell) {
		int i = 50 + paramCell.x * 50;
		int j = 50 + paramCell.y * 50;
		paramGraphics2D.setColor(Color.black);
		Rectangle2D localRectangle2D = paramGraphics2D.getFontMetrics().getStringBounds("R", paramGraphics2D);
		j += (50 + (int) localRectangle2D.getHeight()) / 2;
		i += (50 - (int) localRectangle2D.getWidth()) / 2;
		paramGraphics2D.drawString("R", i, j);
	}

	public void Draw_maze(Graphics2D paramGraphics2D) {
		Font localFont = paramGraphics2D.getFont();
		paramGraphics2D.setFont(new Font("SansSerif", 3, 25));
		int i = 48;
		int n;
		Rectangle2D localRectangle2D1;
		for (int k = 0; k < this.size_x; k++) {
			int m = 50 + k * 50;
			n = 0;
			paramGraphics2D.setColor(Color.white);
			paramGraphics2D.fillRect(m + 1, n + 1, i, i);
			localRectangle2D1 = paramGraphics2D.getFontMetrics().getStringBounds(Integer.toString(k + 1),
					paramGraphics2D);
			n += (50 + (int) localRectangle2D1.getHeight()) / 2;
			m += (50 - (int) localRectangle2D1.getWidth()) / 2;
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.drawString(Integer.toString(k + 1), m, n);
		}

		for (int k = 0; k < this.size_y; k++) {
			int m = 0;
			n = 50 + k * 50;
			paramGraphics2D.setColor(Color.white);
			paramGraphics2D.fillRect(m + 1, n + 1, i, i);
			localRectangle2D1 = paramGraphics2D.getFontMetrics().getStringBounds(Integer.toString(k), paramGraphics2D);
			n += (50 + (int) localRectangle2D1.getHeight()) / 2;
			m += (50 - (int) localRectangle2D1.getWidth()) / 2;
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.drawString(Character.toString((char) (k + 65)), m, n);
		}

		paramGraphics2D.setFont(localFont);
		int i1;
		for (int k = 0; k < this.size_x; k++) {
			for (int m = 0; m < this.size_y; m++) {
				n = 50 + k * 50;
				i1 = 50 + m * 50;
				paramGraphics2D.setColor(Color.black);
				paramGraphics2D.fillRect(n, i1, 50, 50);
				switch (this.cells[k][m].type_robot_vision) {
				case 1:
					bgcolor = wall_color;
					paramGraphics2D.setColor(wall_color);
					paramGraphics2D.fillRect(n + 1, i1 + 1, i, i);
					break;
				case 2:
					bgcolor = path_color;
					paramGraphics2D.setColor(path_color);
					paramGraphics2D.fillRect(n + 1, i1 + 1, i, i);
					break;
				case 0:
					/*
					 * if (this.cells[k][m].iteration == this.iteration) {
					 * paramGraphics2D.setColor(visited_cell_color); } else if
					 * (this.cells[k][m].iteration != 0)
					 * paramGraphics2D.setColor(Color.lightGray); else
					 * paramGraphics2D.setColor(Color.white);
					 */
					if (this.cells[k][m].iteration == 0)
						paramGraphics2D.setColor(Color.white);
					else {
						paramGraphics2D.setColor(visited_cell_color);
						bgcolor = visited_cell_color;
					}

					int i2;
					if (this.u.contains(new Key(this.cells[k][m]))) {
						// bgcolor = paramGraphics2D.getColor();
						// paramGraphics2D.setColor(Color.blue);
						// paramGraphics2D.fillRect(n, i1, 50, 50);
						i2 = 3;
					} else
						i2 = 1;
					// paramGraphics2D.setColor(bgcolor);
					paramGraphics2D.fillRect(n + i2, i1 + i2, 50 - i2 * 2, 50 - i2 * 2);
				}

				if ((this.cells[k][m] != this.start) && (this.cells[k][m] != this.goal)
						&& (this.cells[k][m].type_robot_vision != 1)) {
					Print_cell_information(paramGraphics2D, this.cells[k][m]);
				}
			}
		}

		int k = 50 + this.start.x * 50;
		int m = 50 + this.start.y * 50;
		paramGraphics2D.setColor(Color.black);
		paramGraphics2D.fillRect(k, m, 50, 50);
		paramGraphics2D.setColor(Color.white);
		GeneralPath localGeneralPath = new GeneralPath();

		if (this.u.contains(new Key(this.start)))
			i1 = 3;
		else
			i1 = 1;
		i = 50 - i1 * 2;
		paramGraphics2D.fillRect(k + i1, m + i1, i, i);
		localGeneralPath.moveTo(i / 2, 0.0F);
		localGeneralPath.lineTo(i, i / 2);
		localGeneralPath.lineTo(i / 2, i);
		localGeneralPath.lineTo(0.0F, i / 2);
		localGeneralPath.lineTo(i / 2, 0.0F);
		paramGraphics2D.setColor(Color.orange);
		paramGraphics2D.translate(k + i1, m + i1);
		paramGraphics2D.fill(localGeneralPath);
		paramGraphics2D.translate(-(k + i1), -(m + i1));
		Print_cell_information(paramGraphics2D, this.start);
		k = 50 + this.goal.x * 50;
		m = 50 + this.goal.y * 50;
		paramGraphics2D.setColor(Color.black);
		paramGraphics2D.fillRect(k, m, 50, 50);
		paramGraphics2D.setColor(Color.white);
		localGeneralPath = new GeneralPath();
		if (this.u.contains(new Key(this.goal)))
			i1 = 3;
		else
			i1 = 1;
		int j = 50 - i1 * 2;
		paramGraphics2D.fillRect(k + i1, m + i1, j, j);
		localGeneralPath.moveTo(j / 2, 0.0F);
		localGeneralPath.lineTo(j, j / 2);
		localGeneralPath.lineTo(j / 2, j);
		localGeneralPath.lineTo(0.0F, j / 2);
		localGeneralPath.lineTo(j / 2, 0.0F);
		paramGraphics2D.setColor(Color.orange);
		paramGraphics2D.translate(k + i1, m + i1);
		paramGraphics2D.fill(localGeneralPath);
		paramGraphics2D.translate(-(k + i1), -(m + i1));
		paramGraphics2D.setColor(Color.black);
		Rectangle2D localRectangle2D2 = paramGraphics2D.getFontMetrics().getStringBounds("G", paramGraphics2D);
		m += (50 + (int) localRectangle2D2.getHeight()) / 2;
		k += (50 - (int) localRectangle2D2.getWidth()) / 2;
		paramGraphics2D.drawString("G", k, m);
	}

	public void Draw_real_maze(Graphics2D paramGraphics2D) {
		Font localFont = paramGraphics2D.getFont();
		paramGraphics2D.setFont(new Font("SansSerif", 3, 25));
		int i = 48;
		int n;
		Rectangle2D localRectangle2D1;
		for (int k = 0; k < this.size_x; k++) {
			int m = 50 + k * 50;
			n = 0;
			paramGraphics2D.setColor(Color.white);
			paramGraphics2D.fillRect(m + 1, n + 1, i, i);
			localRectangle2D1 = paramGraphics2D.getFontMetrics().getStringBounds(Integer.toString(k + 1),
					paramGraphics2D);
			n += (50 + (int) localRectangle2D1.getHeight()) / 2;
			m += (50 - (int) localRectangle2D1.getWidth()) / 2;
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.drawString(Integer.toString(k + 1), m, n);
		}

		for (int k = 0; k < this.size_y; k++) {
			int m = 0;
			n = 50 + k * 50;
			paramGraphics2D.setColor(Color.white);
			paramGraphics2D.fillRect(m + 1, n + 1, i, i);
			localRectangle2D1 = paramGraphics2D.getFontMetrics().getStringBounds(Integer.toString(k), paramGraphics2D);
			n += (50 + (int) localRectangle2D1.getHeight()) / 2;
			m += (50 - (int) localRectangle2D1.getWidth()) / 2;
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.drawString(Character.toString((char) (k + 65)), m, n);
		}

		paramGraphics2D.setFont(localFont);
		for (int k = 0; k < this.size_x; k++) {
			for (int m = 0; m < this.size_y; m++) {
				n = 50 + k * 50;
				int i1 = 50 + m * 50;
				paramGraphics2D.setColor(Color.black);
				paramGraphics2D.fillRect(n, i1, 50, 50);
				switch (this.cells[k][m].real_type) {
				case 1:
					paramGraphics2D.setColor(wall_color);
					paramGraphics2D.fillRect(n + 1, i1 + 1, i, i);
					break;
				case 2:
					paramGraphics2D.setColor(path_color);
					paramGraphics2D.fillRect(n + 1, i1 + 1, i, i);
					break;
				case 0:
					paramGraphics2D.setColor(Color.white);
					paramGraphics2D.fillRect(n + 1, i1 + 1, 48, 48);
				}

			}

		}

		int k = 50 + this.original_start.x * 50;
		int m = 50 + this.original_start.y * 50;
		paramGraphics2D.setColor(Color.black);
		paramGraphics2D.fillRect(k, m, 50, 50);
		paramGraphics2D.setColor(Color.white);
		GeneralPath localGeneralPath = new GeneralPath();
		i = 48;
		paramGraphics2D.fillRect(k + 1, m + 1, i, i);
		localGeneralPath.moveTo(i / 2, 0.0F);
		localGeneralPath.lineTo(i, i / 2);
		localGeneralPath.lineTo(i / 2, i);
		localGeneralPath.lineTo(0.0F, i / 2);
		localGeneralPath.lineTo(i / 2, 0.0F);
		paramGraphics2D.setColor(Color.orange);
		paramGraphics2D.translate(k + 1, m + 1);
		paramGraphics2D.fill(localGeneralPath);
		paramGraphics2D.translate(-(k + 1), -(m + 1));
		paramGraphics2D.setColor(Color.black);
		Rectangle2D localRectangle2D2 = paramGraphics2D.getFontMetrics().getStringBounds("S", paramGraphics2D);
		m += (50 + (int) localRectangle2D2.getHeight()) / 2;
		k += (50 - (int) localRectangle2D2.getWidth()) / 2;
		paramGraphics2D.drawString("S", k, m);
		k = 50 + this.goal.x * 50;
		m = 50 + this.goal.y * 50;
		paramGraphics2D.setColor(Color.black);
		paramGraphics2D.fillRect(k, m, 50, 50);
		paramGraphics2D.setColor(Color.white);
		localGeneralPath = new GeneralPath();
		int j = 48;
		paramGraphics2D.fillRect(k + 1, m + 1, j, j);
		localGeneralPath.moveTo(j / 2, 0.0F);
		localGeneralPath.lineTo(j, j / 2);
		localGeneralPath.lineTo(j / 2, j);
		localGeneralPath.lineTo(0.0F, j / 2);
		localGeneralPath.lineTo(j / 2, 0.0F);
		paramGraphics2D.setColor(Color.orange);
		paramGraphics2D.translate(k + 1, m + 1);
		paramGraphics2D.fill(localGeneralPath);
		paramGraphics2D.translate(-(k + 1), -(m + 1));
		paramGraphics2D.setColor(Color.black);
		localRectangle2D2 = paramGraphics2D.getFontMetrics().getStringBounds("G", paramGraphics2D);
		m += (50 + (int) localRectangle2D2.getHeight()) / 2;
		k += (50 - (int) localRectangle2D2.getWidth()) / 2;
		paramGraphics2D.drawString("G", k, m);
		Draw_robot(paramGraphics2D, this.robot_cell);
	}

	public String Get_stack() {
		String str = new String();
		for (Iterator<Key> localIterator = this.u.iterator(); localIterator.hasNext();) {
			str = str + ((Key) localIterator.next()).toString() + "\n";
		}
		return str;
	}

	private int H(Cell paramCell) {
		if (!this.diagonal) {
			return Math.abs(paramCell.x - this.start.x) + Math.abs(paramCell.y - this.start.y);
		}
		return Math.max(Math.abs(paramCell.x - this.start.x), Math.abs(paramCell.y - this.start.y));
	}

	private void Clear_path() {
		for (int i = 0; i < this.size_x; i++) {
			for (int j = 0; j < this.size_y; j++)
				if (this.cells[i][j].type_robot_vision == 2)
					this.cells[i][j].type_robot_vision = 0;
		}
	}

	private boolean Mark_path() {
		if (this.start.g == 2147483647)
			return false;
		Cell localCell;
		for (Object localObject = this.start; localObject != this.goal; localObject = localCell) {
			int i = ((Cell) localObject).g;
			localCell = null;
			for (int j = 0; j < this.n_directions; j++) {
				int k = ((Cell) localObject).x + d_x[j];
				int m = ((Cell) localObject).y + d_y[j];
				if ((0 > k) || (k >= this.size_x) || (0 > m) || (m >= this.size_y)
						|| (this.cells[k][m].type_robot_vision == 1) || (this.cells[k][m].g >= i))
					continue;
				i = this.cells[k][m].g;
				localCell = this.cells[k][m];
			}

			((Cell) localObject).type_robot_vision = 2;
		}

		return true;
	}

	public void Initialize() {
		this.iteration = 0;
		Clear_path();
		Copy_maze();
		this.robot_cell = (this.start = this.original_start);
		for (int i = 0; i < this.size_x; i++) {
			for (int j = 0; j < this.size_y; j++) {
				this.cells[i][j].g = (this.cells[i][j].rhs = 2147483647);
				this.cells[i][j].h = H(this.cells[i][j]);
				this.cells[i][j].iteration = this.iteration;
				this.cells[i][j].parent = null;
			}

		}

		this.goal.rhs = 0;
		this.iteration += 1;
		this.u.clear();
		this.u.add(new Key(this.goal));
	}

	private void Update_cell(Cell paramCell) {
		Cell localCell = null;
		System.out.println("Update_cell: " + paramCell);
		Key localKey = new Key(paramCell);
		paramCell.iteration = this.iteration;
		if (paramCell != this.goal) {
			paramCell.rhs = 2147483647;
			for (int i = 0; i < this.n_directions; i++) {
				int j = paramCell.x + d_x[i];
				int k = paramCell.y + d_y[i];
				if ((0 > j) || (j >= this.size_x) || (0 > k) || (k >= this.size_y)
						|| (this.cells[j][k].type_robot_vision == 1))
					continue;
				int m;
				if (this.cells[j][k].g == 2147483647)
					m = 2147483647;
				else
					m = this.cells[j][k].g + 1;
				if (paramCell.rhs <= m)
					continue;
				paramCell.rhs = m;
				localCell = this.cells[j][k];
			}

		}

		this.u.remove(localKey);
		paramCell.parent = localCell;
		System.out.println("New Parent: " + localCell);
		if (paramCell.g != paramCell.rhs)
			this.u.add(new Key(paramCell));
	}

	public boolean Execution_end() {
		return (this.u.isEmpty()) || ((this.cde.compare(new Key(this.start), (Key) this.u.first()) <= 0)
				&& (this.start.g == this.start.rhs));
	}

	public boolean Reached_goal() {
		return this.robot_cell == this.goal;
	}

	public void Calculate_path(boolean paramBoolean) {
		do {
			if (Execution_end())
				break;
			Key localKey = (Key) this.u.first();
			this.u.remove(localKey);
			Cell localCell = localKey.cell;
			localCell.iteration = this.iteration;
			int i;
			int j;
			int k;
			if (localCell.g > localCell.rhs) {
				localCell.g = localCell.rhs;
				if (localCell == this.start)
					continue;
				for (i = 0; i < this.n_directions; i++) {
					j = localCell.x + d_x[i];
					k = localCell.y + d_y[i];
					if ((0 <= j) && (j < this.size_x) && (0 <= k) && (k < this.size_y)
							&& (this.cells[j][k].type_robot_vision != 1) && (localCell.g + 1 < this.cells[j][k].rhs)) {
						Update_cell(this.cells[j][k]);
					}
				}
			} else {
				localCell.g = 2147483647;
				Update_cell(localCell);
				for (i = 0; i < this.n_directions; i++) {
					j = localCell.x + d_x[i];
					k = localCell.y + d_y[i];
					if ((0 <= j) && (j < this.size_x) && (0 <= k) && (k < this.size_y)
							&& (this.cells[j][k].type_robot_vision != 1))
						Update_cell(this.cells[j][k]);
				}
			}
		} while (!paramBoolean);
		if (Execution_end())
			Mark_path();
	}

	public void Move(boolean paramBoolean) {
		int i = 2147483647;
		Cell localCell1 = null;
		int j = 0;

		for (int k = 0; k < 8; k++) {
			int n = this.robot_cell.x + d_x[k];
			int i2 = this.robot_cell.y + d_y[k];
			if ((0 > n) || (n >= this.size_x) || (0 > i2) || (i2 >= this.size_y)
					|| (this.cells[n][i2].type_robot_vision == this.cells[n][i2].real_type)
					|| ((this.cells[n][i2].type_robot_vision == 2) && (this.cells[n][i2].real_type == 0)))
				continue;
			Cell localCell3 = this.cells[n][i2];
			j = 1;
			int i6;
			if (localCell3.type_robot_vision == 1) {
				localCell3.type_robot_vision = 0;
				localCell3.g = (localCell3.rhs = 2147483647);
				localCell3.parent = null;
				Update_cell(localCell3);
				int i4 = 0;

				while (i4 < this.n_directions) {
					int i5 = localCell3.x + d_x[i4];
					i6 = localCell3.y + d_y[i4];
					if ((0 <= i5) && (i5 < this.size_x) && (0 <= i6) && (i6 < this.size_y)
							&& (this.cells[i5][i6].type_robot_vision != 1)
							&& (localCell3.g + 1 < this.cells[i5][i6].rhs))
						Update_cell(this.cells[i5][i6]);
					i4++;
				}
			}
			Key localKey2 = new Key(localCell3);
			this.u.remove(localKey2);
			localCell3.g = (localCell3.rhs = 2147483647);
			localCell3.parent = null;
			localCell3.type_robot_vision = 1;
			for (int i5 = 0; i5 < this.n_directions; i5++) {
				i6 = localCell3.x + d_x[i5];
				int i7 = localCell3.y + d_y[i5];
				if ((0 <= i6) && (i6 < this.size_x) && (0 <= i7) && (i7 < this.size_y)
						&& (this.cells[i6][i7].type_robot_vision != 1) && (this.cells[i6][i7].parent == localCell3)) {
					Update_cell(this.cells[i6][i7]);
				}
			}
		}

		if (j != 0) {
			Clear_path();
			this.start = this.robot_cell;
			this.iteration += 1;
			TreeSet<Key> localTreeSet = new TreeSet<Key>(this.cde);
			Cell localCell2;
			for (; this.u.size() > 0; localTreeSet.add(new Key(localCell2))) {
				Key localKey1 = (Key) this.u.first();
				this.u.remove(localKey1);
				localCell2 = localKey1.cell;
			}

			this.u = localTreeSet;
			Calculate_path(paramBoolean);
			return;
		}
		for (int m = 0; m < this.n_directions; m++) {
			int i1 = this.robot_cell.x + d_x[m];
			int i3 = this.robot_cell.y + d_y[m];
			if ((0 > i1) || (i1 >= this.size_x) || (0 > i3) || (i3 >= this.size_y)
					|| (this.cells[i1][i3].type_robot_vision == 1) || (this.cells[i1][i3].g >= i))
				continue;
			i = this.cells[i1][i3].g;
			localCell1 = this.cells[i1][i3];
		}

		if (i != 2147483647) {
			Clear_path();
			this.start = (this.robot_cell = localCell1);
			Mark_path();
		}
	}
}