package com.secres.memorycard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class View {

    private JFrame frame;
    private JPanel mainPanel, timePanel, cardPanel;
    private int numCards = 12;
    private JToggleButton[] memoryButtons = new JToggleButton[numCards];
    private String[] paths = {"/apple.png", "/bike.png", "/book.png", "/ketchup.png", "/mouse.png", "/orange.png"};
    private ArrayList<Integer> indexList = new ArrayList<>();
    //private ImageIcon[] images = new ImageIcon[numCards];
    private Timer timer;
    private Thread timerThread;
    
    public View() {
        frame = new JFrame("Memory Card Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(525, 600));
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        timePanel = new JPanel();
        timePanel.setBackground(Color.WHITE);
        JLabel timeLabel = new JLabel("0", SwingConstants.CENTER);
        timePanel.add(timeLabel);
        
        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(4, 3, 5, 5));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        for(int i = 0; i < numCards; i++) {
            indexList.add(i/2);
        }
        
        for(int i = 0; i < numCards; i++) {
            memoryButtons[i] = new JToggleButton();
            memoryButtons[i].setIcon(new ImageIcon(getClass().getResource("/blank.png")));
            Random random = new Random();
            int index = indexList.get(random.nextInt(indexList.size()));
            memoryButtons[i].setSelectedIcon(new ImageIcon(getClass().getResource(paths[index])));
            indexList.remove((Object)index);
            buttonFunctionality(i);
        }
        
        Random rand = new Random();

        Set<Integer> randInts = new LinkedHashSet<Integer>();
        while(randInts.size() < numCards) {
            Integer next = rand.nextInt(numCards) + 1;
            randInts.add(next);
        }
        
        Iterator<Integer> iterator = randInts.iterator();
  
        while(iterator.hasNext()) {
            cardPanel.add(memoryButtons[iterator.next() - 1]);
        }
        
        mainPanel.add(cardPanel);
        mainPanel.add(timePanel, BorderLayout.NORTH);
        frame.add(mainPanel);
        
        frame.pack();
        frame.setVisible(true);
        
        timerThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            while(true) {
                long time = System.currentTimeMillis() - start;
                int seconds = (int) (time / 1000);
                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText("Time Passed: " + seconds);
                });
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {
                    break;
                }
            }
        });
        timerThread.start();

        timer = new Timer(2000, e -> {
            for(int i = 0; i < numCards; i++) {
                memoryButtons[i].setSelected(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
        
        for(int i = 0; i < numCards; i++) {
            memoryButtons[i].setSelected(true);
        }
    }
    
    private void buttonFunctionality(int i) {
        memoryButtons[i].addActionListener(e -> {
            for(int j = 0; j < numCards; j++) {
                if(j != i) {
                    if(memoryButtons[j].isSelected() == true) {
                        if(memoryButtons[i].getSelectedIcon().toString().equals(memoryButtons[j].getSelectedIcon().toString())) {
                            memoryButtons[i].setVisible(false);
                            memoryButtons[j].setVisible(false);
                            memoryButtons[i].setEnabled(false);
                            memoryButtons[j].setEnabled(false);
                            memoryButtons[i].setSelected(false);
                            memoryButtons[j].setSelected(false);
                            boolean allInvisible = true;
                            for(int k = 0; k < numCards; k++) {
                                if(memoryButtons[k].isEnabled()) {
                                    allInvisible = false;
                                    break;
                                }
                            }
                            if(allInvisible) {
                                timerThread.interrupt();
                            }
                        }
                        else {
                            memoryButtons[i].setSelected(false);
                            memoryButtons[j].setSelected(false);
                        }
                        break;
                    }
                }
            }
        });
    }
    
    /*
    private ImageIcon rescaledImage(int i) {
        Image image = new ImageIcon(getClass().getResource(paths[i])).getImage();
        image = image.getScaledInstance(120, 140, Image.SCALE_DEFAULT);
        images[i] = new ImageIcon(image);
        return images[i];
    }
    */
    
}
