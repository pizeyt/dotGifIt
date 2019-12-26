package com.gregory.learning.Gui.recording;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BackgroundPane extends JPanel {

  private Point mouseAnchor;
  private Point dragPoint;
  private JPanel jPanel;
  private JLabel jLabel;
  int darkX;
  int darkY;
  int darkW;
  int darkH;
  int screenHeight, screenWidth;
  Point topLeftPixel, topRightPixel, bottomLeftPixel, bottomRightPixel;

  private SelectionPane selectionPane;

  public BackgroundPane() {
    super.setOpaque(false);
    // Get the size of the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    screenHeight = (int) dim.getHeight();
    screenWidth = (int) dim.getWidth();
    topLeftPixel = new Point(0, 0);
    bottomLeftPixel = new Point(0, dim.height);
    topRightPixel = new Point(dim.width, 0);
    bottomRightPixel = new Point(dim.width, dim.height);

    // Determine the new location of the window
    int w = 300;
    int h = 300;
    int x = (dim.width - w) / 2;
    int y = (dim.height - h) / 2;

    selectionPane = new SelectionPane();
    jLabel = new JLabel("Drag to set recording area");
    jLabel.setFont(jLabel.getFont().deriveFont(20.0f));
    jLabel.setForeground(Color.RED);
    jPanel = new JPanel();
    jPanel.setSize(500, 100);
    jPanel.setOpaque(false);
    jPanel.setLayout(new FlowLayout());
    jPanel.add(new JButton("Record"));
    jPanel.add(new JButton("Back"));
    setLayout(null);
    selectionPane.setBounds(x, y, w, h);
    setDarkCoordinates(x, y, w, h);
    jPanel.setLocation(x + w / 2 - jPanel.getWidth() / 2, y + h);
    add(selectionPane);
    selectionPane.add(jLabel);
    add(jPanel);

    MouseAdapter adapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        selectionPane.remove(jLabel);
        mouseAnchor = e.getPoint();
        dragPoint = null;
        selectionPane.setLocation(mouseAnchor);
        setDarkCoordinates(x, y, 0, 0);
        jPanel.setLocation(e.getX() - jPanel.getWidth() / 2, e.getY());
        selectionPane.setSize(0, 0);
        repaint();
      }

      @Override
      public void mouseDragged(MouseEvent e) {
        selectionPane.remove(jLabel);
        BackgroundPane.super.add(jPanel);
        dragPoint = e.getPoint();
        int width = dragPoint.x - mouseAnchor.x;
        int height = dragPoint.y - mouseAnchor.y;

        int x = mouseAnchor.x;
        int y = mouseAnchor.y;

        if (width < 0) {
          x = dragPoint.x;
          width *= -1;
        }
        if (height < 0) {
          y = dragPoint.y;
          height *= -1;
        }

        setDarkCoordinates(x, y, width, height);

        selectionPane.setBounds(x, y, width, height);
        jPanel.setLocation(x + width / 2 - jPanel.getWidth() / 2, y + height);
        selectionPane.revalidate();
        repaint();
      }

    };
    addMouseListener(adapter);
    addMouseMotionListener(adapter);

  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(new Color(0, 0, 0, 60));
    // Left
    g2d.fillRect(0, darkY, darkX, selectionPane.getHeight());
    g2d.drawRect(0, darkY, darkX, selectionPane.getHeight());
    // Right
    g2d.fillRect(darkX + selectionPane.getWidth(), darkY, screenWidth, selectionPane.getHeight());
    g2d.drawRect(darkX + selectionPane.getWidth(), darkY, screenWidth, selectionPane.getHeight());
    // Top
    g2d.fillRect(0, 0, screenWidth, darkY);
    g2d.drawRect(0, 0, screenWidth, darkY);
    // Bottom
    g2d.fillRect(0, darkY + selectionPane.getHeight(), screenWidth, screenHeight);
    g2d.drawRect(0, darkY + selectionPane.getHeight(), screenWidth, screenHeight);

    g2d.dispose();
  }

  private void setDarkCoordinates(int darkX, int darkY, int darkW, int darkH) {
    this.darkX = darkX;
    this.darkY = darkY;
    this.darkW = darkW;
    this.darkH = darkH;
  }

  public class SelectionPane extends JPanel {

    public SelectionPane() {
      setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setColor(new Color(128, 128, 128, 0));
      g2d.fillRect(0, 0, getWidth(), getHeight());

      float dash1[] = {5.0f};
      BasicStroke dashed =
          new BasicStroke(1.5f,
              BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_MITER,
              5.0f, dash1, 0.0f);
      g2d.setColor(Color.BLACK);
      g2d.setStroke(dashed);
      g2d.drawRect(0, 0, getWidth(), getHeight());
      g2d.dispose();
    }
  }

  public static Rectangle getScreenViewableBounds() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();

    return getScreenViewableBounds(gd);
  }

  public static Rectangle getScreenViewableBounds(GraphicsDevice gd) {
    Rectangle bounds = new Rectangle(0, 0, 0, 0);
    if (gd != null) {
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      bounds = gc.getBounds();

      Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gc);

      bounds.x += insets.left;
      bounds.y += insets.top;
      bounds.width -= (insets.left + insets.right);
      bounds.height -= (insets.top + insets.bottom);
    }
    return bounds;
  }
}