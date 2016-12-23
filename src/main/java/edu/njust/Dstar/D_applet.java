/* Dynamic Astar Lite 
 * author:	ShenJie
 * email:	bud000@126.com
 * Nanjing University of Science & Technology
 * 
 * FILENAME:D_applet.java
 * */

package edu.njust.Dstar;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class D_applet extends Applet implements ActionListener, ItemListener, KeyListener, MouseListener {
	Maze maze;
	MyCanvas canvas1;
	MyCanvas canvas2;
	boolean place_start;
	boolean place_goal;
	boolean single_step;
	boolean executing;
	boolean first_change;
	Button restart;
	Button move;
	Button next_step;
	Button change_start;
	Button change_goal;
	Checkbox single_step_execution;
	Checkbox diagonals;
	TextArea ta;
	private static final long serialVersionUID = 1L;

	public void init() {
		GridBagConstraints localGridBagConstraints = new GridBagConstraints();
		Container localContainer = new Container();
		localContainer.setLayout(new FlowLayout());
		long l = (long) (Math.random() * 9.223372036854776E+018D);
		this.maze = new Maze(l, 7, 9);
		this.canvas1 = new MyCanvas(this.maze.Printed_maze_size());
		this.canvas2 = new MyCanvas(this.maze.Printed_maze_size());

		setLayout(new GridBagLayout());

		localGridBagConstraints.insets = new Insets(0, 3, 0, 0);
		localGridBagConstraints.fill = 0;
		localGridBagConstraints.gridx = 0;
		localGridBagConstraints.gridy = 0;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		Label localLabel1 = new Label("ʵ�ʵ�ͼ", 1);
		localLabel1.setFont(new Font("SansSerif", 3, 30));
		add(localLabel1, localGridBagConstraints);

		localGridBagConstraints.fill = 0;
		localGridBagConstraints.gridx = 1;
		localGridBagConstraints.gridy = 0;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		Label localLabel2 = new Label("D* Lite (�������Ӿ�)", 1);
		localLabel2.setFont(new Font("SansSerif", 3, 30));
		add(localLabel2, localGridBagConstraints);

		localGridBagConstraints.fill = 2;
		localGridBagConstraints.gridx = 2;
		localGridBagConstraints.gridy = 0;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		Label localLabel3 = new Label("D* Lite�㷨���ȼ�����", 1);
		localLabel3.setFont(new Font("����", 2, 14));
		add(localLabel3, localGridBagConstraints);

		localGridBagConstraints.fill = 0;
		localGridBagConstraints.gridx = 0;
		localGridBagConstraints.gridy = 1;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		add(this.canvas1, localGridBagConstraints);

		localGridBagConstraints.fill = 0;
		localGridBagConstraints.gridx = 1;
		localGridBagConstraints.gridy = 1;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		add(this.canvas2, localGridBagConstraints);

		localGridBagConstraints.fill = 3;
		localGridBagConstraints.gridx = 2;
		localGridBagConstraints.gridy = 1;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		this.ta = new TextArea("", 1, 12, 3);
		this.ta.setFocusable(false);
		add(this.ta, localGridBagConstraints);

		localGridBagConstraints.fill = 1;
		localGridBagConstraints.gridx = 0;
		localGridBagConstraints.gridy = 2;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 3;
		add(localContainer, localGridBagConstraints);

		this.restart = new Button("����");
		this.change_start = new Button("�������");
		this.change_goal = new Button("����Ŀ��");
		this.move = new Button("�ƶ�");
		this.next_step = new Button("Next step");
		this.single_step_execution = new Checkbox("Step by step");
		this.diagonals = new Checkbox("�Խ���ͨ��");
		this.move.addActionListener(this);
		localContainer.add(this.move);
		this.change_start.addActionListener(this);
		localContainer.add(this.change_start);
		this.change_goal.addActionListener(this);
		localContainer.add(this.change_goal);
		this.restart.addActionListener(this);
		localContainer.add(this.restart);
		this.next_step.setEnabled(false);
		this.next_step.addActionListener(this);

		this.single_step_execution.addItemListener(this);

		this.diagonals.addItemListener(this);
		localContainer.add(this.diagonals);
		this.canvas1.addMouseListener(this);
		this.canvas1.addKeyListener(this);
		this.canvas2.addMouseListener(this);
		this.canvas2.addKeyListener(this);
		this.executing = (this.single_step = this.place_start = this.place_goal = false);
		this.first_change = true;
		this.maze.Initialize();
		this.maze.Calculate_path(this.single_step);
		this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
		this.maze.Draw_maze(this.canvas2.getGraphics2D());
		this.canvas1.repaint();
		this.canvas2.repaint();
		this.ta.setText(this.maze.Get_stack());
	}

	public void stop() {
	}

	public void mouseClicked(MouseEvent paramMouseEvent) {
	}

	public void mouseEntered(MouseEvent paramMouseEvent) {
	}

	public void mouseExited(MouseEvent paramMouseEvent) {
	}

	public void mousePressed(MouseEvent paramMouseEvent) {
		if (paramMouseEvent.getButton() == 3) {
			this.maze.Print_cell(paramMouseEvent.getX(), paramMouseEvent.getY());
			return;
		}
		if (this.place_start) {
			this.maze.Set_start(paramMouseEvent.getX(), paramMouseEvent.getY());
			this.place_start = false;
			Set_buttons_enabled(true);
		} else if (this.place_goal) {
			this.maze.Set_goal(paramMouseEvent.getX(), paramMouseEvent.getY());
			this.place_goal = false;
			Set_buttons_enabled(true);
		} else {
			if (this.executing) {
				return;
			}

			this.maze.Transform_cell(paramMouseEvent.getX(), paramMouseEvent.getY());
			this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
			this.canvas1.repaint();
			return;
		}
		this.first_change = true;
		this.maze.Calculate_path(this.single_step);
		this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
		this.maze.Draw_maze(this.canvas2.getGraphics2D());
		if (this.single_step) {
			this.executing = true;
			this.next_step.setEnabled(true);
		}
		this.ta.setText(this.maze.Get_stack());
		if (this.maze.Execution_end()) {
			this.next_step.setEnabled(false);
			this.executing = false;
		}
		this.canvas1.repaint();
		this.canvas2.repaint();
	}

	public void mouseReleased(MouseEvent paramMouseEvent) {
	}

	public void keyPressed(KeyEvent paramKeyEvent) {
	}

	public void keyReleased(KeyEvent paramKeyEvent) {
	}

	public void keyTyped(KeyEvent paramKeyEvent) {
	}

	public void actionPerformed(ActionEvent paramActionEvent) {
		if (paramActionEvent.getActionCommand().equals("Next step")) {
			this.maze.Calculate_path(this.single_step);
			this.maze.Draw_maze(this.canvas2.getGraphics2D());
			this.canvas2.repaint();
			if (this.maze.Execution_end()) {
				this.next_step.setEnabled(false);
				this.move.setEnabled(true);
				this.executing = false;
			}
			this.ta.setText(this.maze.Get_stack());
		} else if (paramActionEvent.getActionCommand().equals("����")) {
			this.first_change = true;
			this.maze.Initialize();
			this.maze.Calculate_path(this.single_step);
			this.maze.Draw_maze(this.canvas2.getGraphics2D());
			this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
			this.canvas1.repaint();
			this.canvas2.repaint();
			this.move.setEnabled(true);
			this.next_step.setEnabled(false);
			this.executing = false;
			if (!this.maze.Execution_end()) {
				this.next_step.setEnabled(true);
				this.move.setEnabled(false);
				this.executing = true;
			}
			this.ta.setText(this.maze.Get_stack());
		} else if (paramActionEvent.getActionCommand().equals("�������")) {
			Set_buttons_enabled(false);
			this.place_start = true;
		} else if (paramActionEvent.getActionCommand().equals("����Ŀ��")) {
			Set_buttons_enabled(false);
			this.place_goal = true;
		} else if (paramActionEvent.getActionCommand().equals("�ƶ�")) {
			this.first_change = true;
			this.maze.Move(this.single_step);
			this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
			this.maze.Draw_maze(this.canvas2.getGraphics2D());
			this.canvas1.repaint();
			this.canvas2.repaint();
			if ((!this.maze.Execution_end()) || (this.maze.Reached_goal())) {
				this.move.setEnabled(false);
				if (this.single_step) {
					this.next_step.setEnabled(true);
					this.executing = true;
				}
			}
			this.ta.setText(this.maze.Get_stack());
		}
	}

	public void itemStateChanged(ItemEvent paramItemEvent) {
		System.out.println();
		if (((String) paramItemEvent.getItem()).equals("�Խ���ͨ��")) {
			this.first_change = true;
			this.maze.Set_diagonal(this.diagonals.getState());
			this.maze.Initialize();
			this.maze.Calculate_path(this.single_step);
			this.maze.Draw_maze(this.canvas2.getGraphics2D());
			this.maze.Draw_real_maze(this.canvas1.getGraphics2D());
			if (this.single_step) {
				this.executing = true;
				this.next_step.setEnabled(true);
			}
			this.ta.setText(this.maze.Get_stack());
			if (this.maze.Execution_end()) {
				this.next_step.setEnabled(false);
				this.executing = false;
			}
			this.canvas1.repaint();
			this.canvas2.repaint();
		} else {
			this.single_step = (!this.single_step);
		}
	}

	private void Set_buttons_enabled(boolean paramBoolean) {
		this.change_start.setEnabled(paramBoolean);
		this.change_goal.setEnabled(paramBoolean);
		this.move.setEnabled(paramBoolean);
		this.next_step.setEnabled(paramBoolean);
		this.single_step_execution.setEnabled(paramBoolean);
		this.diagonals.setEnabled(paramBoolean);
		this.restart.setEnabled(paramBoolean);
	}
}