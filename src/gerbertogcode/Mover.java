/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gerbertogcode;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vojta3310
 */
public class Mover {

  private float penSize = 0.2f;
  private float tolerance = 0.01f;
  private float scale = 1;
  private float zUp = 3;

  private final LinkedList<Move> moves = new LinkedList();
  private float x = 0;
  private float y = 0;
  private boolean up = false;
  private float d = 0.9f;
  private float minX = Float.MAX_VALUE;
  private float maxX = 0;
  private float minY = Float.MAX_VALUE;
  private float maxY = 0;
  private float ofX = 0;
  private float ofY = 0;

  public void setTool(float d) {
    this.d = d;
  }

  public void move(float x, float y, int how) {
    if (Float.isNaN(x)) {
      x = this.x;
    }
    if (Float.isNaN(y)) {
      x = this.y;
    }
    switch (how) {
      case 1:
        if (up) {
          add(new Move(0));
          up = false;
        }
        lineTo(x, y);
        break;
      case 2:
        if (!up) {
          add(new Move(zUp));
          up = true;
        }
        add(new Move(x, y));
        break;
      case 3:
        if (!up) {
          add(new Move(zUp));
          up = true;
        }
        drawShape(x, y);
        break;
    }
    this.x = x;
    this.y = y;
  }

  public StringBuilder toGcode() {
    StringBuilder a = new StringBuilder();
    a.append("G21 (metric ftw)\n");
    a.append("G90 (absolute mode)\n");
    a.append("G92 X0 Y0 Z0 (you are here)\n");
    moves.forEach((Move m) -> {
      m.scale(scale);
      m.ofset(ofX, ofY);
      a.append(m.toGcode());
    });
    a.append("G1 Z").append(zUp * scale);
    a.append("G1 X0.0Y0.0");
    a.append("M18");
    return a;
  }

  public void saveGcode(String path) {
    File file = new File(path);
    try (FileWriter fileWriter = new FileWriter(file)) {
      fileWriter.write(toGcode().toString());
      fileWriter.flush();
    } catch (IOException ex) {
      Logger.getLogger(Mover.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void draw(Graphics g2d, int h, int w) {
    h -= 10;
    w -= 10;
    float scaleGr = Math.min(w / Math.abs(maxX - minX), h / Math.abs(maxY - minY));
    float xl = 0, yl = 0, zl = 0;
    float xa, ya, za;
    boolean drawing = false;
    for (Move m : moves) {
      m.scale(scaleGr);
      m.ofset(ofX, ofY);
      xa = m.getToX();
      ya = m.getToY();
      za = m.getToZ();
      if (!Float.isNaN(za)) {
        drawing = (za < zl);
      } else {
        if (drawing) {
          g2d.setColor(Color.black);
        } else {
          g2d.setColor(Color.lightGray);
        }
        g2d.drawLine(5 + Math.round(xl), 5 + Math.round(yl), 5 + Math.round(xa), 5 + Math.round(ya));
      }
      if (!Float.isNaN(xa)) {
        xl = xa;
      }
      if (!Float.isNaN(ya)) {
        yl = ya;
      }
      if (!Float.isNaN(za)) {
        zl = za;
      }
    }
  }

  private void lineTo(float x, float y) {
    if (up) {
      add(new Move(0));
      up = false;
    }
    //line to xy
    add(new Move(x, y));

    float tx = x - this.x;
    float ty = y - this.y;
    float nx = -ty / (float) Math.sqrt(tx * tx + ty * ty);
    float ny = tx / (float) Math.sqrt(tx * tx + ty * ty);
    float txn = tx / (float) Math.sqrt(tx * tx + ty * ty);
    float tyn = ty / (float) Math.sqrt(tx * tx + ty * ty);

    float i = d / (penSize * 2) - 0.5f;
    for (; i > 0; i--) {
      //line to side
      add(new Move(x + nx * penSize * i, y + ny * penSize * i));
      //line back around
      add(new Move(this.x + nx * penSize * i, this.y + ny * penSize * i));
      //round around - circle vith center in this.x this.y and r = i*penSize
      for (float ii = 0; ii <= i * penSize * 2; ii += tolerance) {
        float xr = i * penSize - ii;
        float yr = (float) Math.sqrt(Math.pow(i * penSize, 2) - Math.pow(xr, 2));
        float px = nx * xr - txn * yr + this.x;
        float py = ny * xr - tyn * yr + this.y;
        add(new Move(px, py));
      }
      add(new Move(this.x - nx * penSize * i, this.y - ny * penSize * i));
      //line to around
      add(new Move(x - nx * penSize * i, y - ny * penSize * i));
      //round around
      for (float ii = 0; ii <= i * penSize * 2; ii += tolerance) {
        float xr = i * penSize - ii;
        float yr = (float) Math.sqrt(Math.pow(i * penSize, 2) - Math.pow(xr, 2));
        float px = -nx * xr + txn * yr + x;
        float py = -ny * xr + tyn * yr + y;
        add(new Move(px, py));
      }
    }
    //back to center
    add(new Move(x, y));
  }

  private void drawShape(float x, float y) {
    add(new Move(x, y));
    if (up) {
      add(new Move(0));
      up = false;
    }
    float i = d / (penSize * 2) - 0.5f;
    for (; i > 0; i--) {
      if (i == 0) {
        continue;
      }
      add(new Move(x - i * penSize, y));
      for (float xi = x - i * penSize + tolerance; xi <= x + i * penSize; xi += tolerance) {
        float py = y + (float) Math.sqrt(Math.pow(i * penSize, 2) - Math.pow(xi - x, 2));
        add(new Move(xi, py));
      }
      for (float xi = x + i * penSize - tolerance; xi >= x - i * penSize; xi -= tolerance) {
        float py = y - (float) Math.sqrt(Math.pow(i * penSize, 2) - Math.pow(xi - x, 2));
        add(new Move(xi, py));
      }
      add(new Move(x - i * penSize, y));
    }
    add(new Move(x, y));
  }

  private void add(Move m) {
    moves.add(m);
    if (m.getToX() < minX) {
      minX = m.getToX();
    }
    if (m.getToY() < minY) {
      minY = m.getToY();
    }
    if (m.getToY() > maxY) {
      maxY = m.getToY();
    }
    if (m.getToX() > maxX) {
      maxX = m.getToX();
    }
    ofX = -minX;
    ofY = -minY;
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

}
