/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java2ddrawingapplication;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame {
    private final JFrame frame;
    private final JPanel mainPanel;
    private final JPanel topPanel;
    private final JPanel bottomPanel;
    private final JPanel drawPanel;
    private final GridLayout layout;
    private final JLabel shape;
    private final JComboBox<String> dropdown;
    public static final String[] names = {"Line", "Oval", "Rectangle"};
    private final JButton firstColor;
    private final JButton secondColor;
    private final JButton undo;
    private final JButton clear;
    private final JLabel options;
    private final JCheckBox filled;
    private final JCheckBox gradient;
    private final JCheckBox dashed;
    private final JLabel lineWidth;
    private final JSpinner lineWidthNum;
    private final JLabel dashLength;
    private final JSpinner dashLengthNum;
    private final JLabel status;
    private ArrayList<MyShapes> shapes = new ArrayList<>();
    private Color color1  = Color.black;
    private Color color2 = Color.black;
    private MyShapes currentShape;
  
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame() {
        frame = new JFrame();
        mainPanel = new JPanel();
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        drawPanel = new DrawPanel();
        layout = new GridLayout(2, 1);
        mainPanel.setLayout(layout);
        topPanel.setBackground(new Color(179, 255, 255));
        bottomPanel.setBackground(new Color(179, 255, 255));
                
        shape = new JLabel("Shape: ");
        topPanel.add(shape);
        dropdown = new JComboBox<>(names);
        dropdown.setMaximumRowCount(3);
        
        topPanel.add(dropdown);
        firstColor = new JButton("1st Color...");
        firstColor.addActionListener((ActionEvent event) -> {
            color1 = JColorChooser.showDialog(null, "Change Button Background",
                    color1);
        });
        topPanel.add(firstColor);
        secondColor = new JButton("2nd Color...");
        secondColor.addActionListener((ActionEvent event) -> {
            color2 = JColorChooser.showDialog(null, "Change Button Background",
                    color2);
        });
        topPanel.add(secondColor);
        undo = new JButton("Undo");
        undo.addActionListener((ActionEvent event) -> {
            if(!shapes.isEmpty()){
                shapes.remove(shapes.size() - 1);
                drawPanel.repaint();
            }
        });
        topPanel.add(undo);
        clear = new JButton("Clear");
        clear.addActionListener((ActionEvent event) -> {
            if(!shapes.isEmpty()){
                shapes.clear();
                drawPanel.repaint();
            }
        });
        topPanel.add(clear);
        
        options = new JLabel("Options: ");
        bottomPanel.add(options);
        filled = new JCheckBox("Filled");
        bottomPanel.add(filled);
        gradient = new JCheckBox("Use Gradient");
        bottomPanel.add(gradient);
        dashed = new JCheckBox("Dashed");
        bottomPanel.add(dashed);
        lineWidth = new JLabel("Line Width: ");
        bottomPanel.add(lineWidth);
        lineWidthNum = new JSpinner();
        bottomPanel.add(lineWidthNum);
        dashLength = new JLabel("Dash Length: ");
        bottomPanel.add(dashLength);
        dashLengthNum = new JSpinner();
        bottomPanel.add(dashLengthNum);
        
        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);
        
        status = new JLabel("( , )");
        status.setOpaque(true);
        status.setBackground(new Color(215, 215, 215));
        
        frame.add(mainPanel,  BorderLayout.NORTH);
        frame.add(drawPanel,  BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Java 2D Drawings");
        frame.pack();
        frame.setSize(650,500);
        frame.setVisible(true);
    }

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel { //THINKING NEED TO KEEP CURRENT UPDATING THROUGH DRAGGED AND ENDS AT RELEASED

        public DrawPanel() {
            MouseHandler handler = new MouseHandler();
            this.addMouseListener(handler);
            this.addMouseMotionListener(handler);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < shapes.size(); i++){
                shapes.get(i).draw(g2d);
            }
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener {
            
            @Override
            public void mousePressed(MouseEvent event) { // start points here
                Integer pressedX = event.getX();
                Integer pressedY = event.getY();
                Paint paint; //create paint variable and set its variable based on the two panels (gradient or solid color)
                Stroke stroke; //create stroke variable width and wether dash
                Boolean fill = filled.isSelected(); //filled = yes or no

                if(gradient.isSelected()){
                    if(color2 == null){
                        paint = new GradientPaint(0, 0, color1, 50, 50, color1, true);
                    } else {
                        paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                    }
                } else {
                    paint = color1;
                }
                float lineWidthStroke = Float.valueOf(lineWidthNum.getValue().toString());
                float dashLengthStroke[] = {Float.valueOf(dashLengthNum.getValue().toString())};
                if(dashed.isSelected() && dashLengthStroke[0] != 0){
                    stroke = new BasicStroke(lineWidthStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLengthStroke, 0);
                } else {
                    stroke  = new BasicStroke(lineWidthStroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                switch (dropdown.getSelectedItem().toString()) {
                    case "Line":
                        currentShape = new MyLine(new Point(pressedX, pressedY), new Point(pressedX, pressedY), paint, stroke);
                        break;
                    case "Oval":
                        if (fill){
                            currentShape = new MyOval(new Point(pressedX, pressedY), new Point(pressedX, pressedY), paint, stroke, true);
                        } else {
                            currentShape = new MyOval(new Point(pressedX, pressedY), new Point(pressedX, pressedY), paint, stroke, false);
                        }   break;
                    case "Rectangle":
                        if (fill){
                            currentShape = new MyRectangle(new Point(pressedX, pressedY), new Point(pressedX, pressedY), paint, stroke, true);
                        } else {
                            currentShape = new MyRectangle(new Point(pressedX, pressedY), new Point(pressedX, pressedY), paint, stroke, false);
                        }   break;
                }
                currentShape.setStartPoint(event.getPoint());
                currentShape.setEndPoint(event.getPoint());
                shapes.add(currentShape);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                status.setText("(" + event.getX() + ", " + event.getY() + ")");
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                status.setText("(" + event.getX() + ", " + event.getY() + ")");
                shapes.get(shapes.size()-1).setEndPoint(event.getPoint());
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event) { 
                status.setText("(" + event.getX() + ", " + event.getY() + ")");
            }
        }

    }
}
