/* Dynamic Astar Lite
 * author:	ShenJie
 * email:	bud000@126.com
 * Nanjing University of Science & Technology
 * 
 * FILENAME:Dstar.java
 * */

package edu.njust.Dstar;

import javax.swing.JFrame;

public class Dstar {
	public static void main(String[] paramArrayOfString) {
		D_applet localD_applet = new D_applet();
		localD_applet.init();
		localD_applet.start();
		localD_applet.setSize(1000, 600);
		JFrame localJFrame = new JFrame("D* Lite�㷨��ʾ-�Ͼ�����ѧ�Զ���ѧԺ-���");
		localJFrame.add(localD_applet);
		localJFrame.setSize(1000, 600);
		localJFrame.setVisible(true);
		localJFrame.setDefaultCloseOperation(2);
	}
}