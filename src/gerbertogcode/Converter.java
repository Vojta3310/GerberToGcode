/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gerbertogcode;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author vojta3310
 */
public class Converter {

  private float penSize = 0.2f;
  private float tolerance = 0.01f;
  private float scale = 1;
  private float zUp = 3;
  private boolean bebug = false;
  private boolean show = false;
  private String fileIn;
  private String fileOut ="out.gcode";

  public void convert() {
    Mover mo = new Mover();
    mo.setPenSize(penSize);
    mo.setScale(scale);
    mo.setTolerance(tolerance);
    mo.setzUp(zUp);
    Parser pa = new Parser();
    pa.setBebug(bebug);
    pa.parseFile(fileIn, mo);
    mo.saveGcode(fileOut);
    if (show) {
      JFrame fr = new JFrame("G2G - Show: "+fileIn);
      fr.setVisible(true);
      fr.setSize(1000, 800);
      JPanel p = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          g.setColor(Color.white);
          g.fillRect(0, 0, getWidth(), getHeight());
          mo.draw(g, getHeight(), getWidth());
        }
      };
      fr.add(p);
      fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
  }
  
  public void printOption() {
    System.out.println(""
      + "Settings:\n"
      + "   penSize:    "+penSize
      + "\n   penUp:      "+zUp
      + "\n   precision:  "+tolerance
      + "\n   scale:      "+scale
      + "\n   show:       "+show
      + "\n   debug:      "+bebug);
  }

  public void setShow(boolean show) {
    this.show = show;
  }

  public void setPenSize(float penSize) {
    this.penSize = penSize;
  }

  public void setTolerance(float tolerance) {
    this.tolerance = tolerance;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  public void setzUp(float zUp) {
    this.zUp = zUp;
  }

  public void setBebug(boolean bebug) {
    this.bebug = bebug;
  }

  public void setFileIn(String fileIn) {
    this.fileIn = fileIn;
  }

  public void setFileOut(String fileOut) {
    this.fileOut = fileOut;
  }
}
