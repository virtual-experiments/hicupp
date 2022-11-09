package interactivehicupp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TreeDocumentFrame {
  private final static String[] pointsSourceTypeNames = {
    "Images",
    "General Point Sets"
  };
  private final static PointsSourceType[] pointsSourceTypes = {
    new ImagePointsSourceType(),
    new GeneralPointsSourceType()
  };

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> showTreeDocumentFrame(0, true));
  }
  
  static void showTreeDocumentFrame(int pointsSourceTypeIndex, boolean allowSwitching) {
    PointsSourceType pointsSourceType = pointsSourceTypes[pointsSourceTypeIndex];
    String title = "Interactive Hicupp for " + pointsSourceTypeNames[pointsSourceTypeIndex];
    
    TreeDocumentType docType = new TreeDocumentType(pointsSourceType);
    final DocumentFrame frame = new DocumentFrame(docType, title);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if (frame.askSaveIfDirty())
          System.exit(0);
      }
    });
    JMenu fileMenu = frame.getJMenuBar().getMenu(0);
    if (allowSwitching) {
      fileMenu.addSeparator();
      JMenu pointsSourceTypesMenu = new JMenu("Points Source Types");
      for (int i = 0; i < pointsSourceTypes.length; i++) {
        final int index = i;
        JMenuItem item = new JMenuItem(pointsSourceTypeNames[i]);
        item.addActionListener(e -> {
          if (frame.askSaveIfDirty()) {
            frame.dispose();
            EventQueue.invokeLater(() -> showTreeDocumentFrame(index, true));
          }
        });
        pointsSourceTypesMenu.add(item);
      }
      fileMenu.add(pointsSourceTypesMenu);
    }
    fileMenu.addSeparator();
    JMenuItem fileExitMenuItem = new JMenuItem("Exit");
    fileExitMenuItem.addActionListener(e -> {
      if (frame.askSaveIfDirty())
        System.exit(0);
    });
    fileMenu.add(fileExitMenuItem);
    frame.setVisible(true);
  }
}
