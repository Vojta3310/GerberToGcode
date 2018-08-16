/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gerbertogcode;

import java.io.File;

/**
 *
 * @author vojta3310
 */
public class GerberToGcode {

  private static void printHelp() {
    System.out.println(""
      + "Usage:\n"
      + "   G2G [options] <fileIn> <fileOut>\n"
      + "   <fileIn>        Path to input file.\n"
      + "   <fileOut>       Path to output file.\n"
      + "\n"
      + "Options:\n"
      + "   -penSize=<w>    Set line widht to <w>\n"
      + "   -penUp=<z>      Set z coords for pen up to <z>.\n"
      + "   -precision=<p>  Set min step to <p>.\n"
      + "   -scale=<s>      Set scale of drawing to <s>\n"
      + "   -show           Show final drawing in window.\n"
      + "   -debug          Write bebug messages.\n"
      + "\n"
      + "Gerber To Gecode (G2G) version 0.0.2 (c)Vojta3310, 2018\n"
      + "Aviable from: https://github.com/Vojta3310/GerberToGcode\n");
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Converter co = new Converter();
    String fileIn = "";
    String fileOut = "";
    for (String arg : args) {
      if (arg.startsWith("-")) {
        if (arg.equals("-show")) {
          co.setShow(true);
        } else if (arg.equals("-debug")) {
          co.setBebug(true);
        } else if (arg.startsWith("-penSize=")) {
          co.setPenSize(Float.parseFloat(arg.substring(9)));
        } else if (arg.startsWith("-penUp=")) {
          co.setzUp(Float.parseFloat(arg.substring(7)));
        } else if (arg.startsWith("-precision=")) {
          co.setTolerance(Float.parseFloat(arg.substring(11)));
        } else if (arg.startsWith("-scale=")) {
          co.setScale(Float.parseFloat(arg.substring(7)));
        } else {
          printHelp();
          return;
        }
      } else if (fileIn.equals("")) {
        fileIn = arg;
      } else if (fileOut.equals("")) {
        fileOut = arg;
      } else {
        printHelp();
        return;
      }
    }
    if (fileIn.equals("") || fileOut.equals("")) {
      printHelp();
      return;
    }
    File fI = new File(fileIn);
    File fO = new File(fileOut);
    if (!fI.exists()) {
      System.out.println("File " + fileIn + " doesnt exist!");
      return;
    }
    if (fO.isDirectory() || fI.isDirectory()) {
      System.out.println("Not a file!");
      return;
    }
    co.setFileIn(fileIn);
    co.setFileOut(fileOut);
    co.convert();
    System.out.println("Sucesfully converted with this setting.");
    co.printOption();
  }
}
