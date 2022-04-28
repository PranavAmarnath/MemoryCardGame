// this code was written collaboratively between 2 partners

package com.secres.memorycard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class Main {

    public Main() {
        new View();
    }
    
    class View {
        
        private JDialog dialog;
        private JFrame frame;
        private JPanel mainPanel, timePanel, cardPanel;
        private JLabel timeLabel;
        private JToggleButton[] memoryButtons;
        /*
        Used the following links for the images in the paths:
        
        (blank.png) https://upload.wikimedia.org/wikipedia/commons/4/48/BLANK_ICON.png - 12 June 2011 by Virgiltracey
        (orange.png) https://clipartix.com/orange-clipart-image-55466/ - Public Domain
        (apple.png) https://clipartix.com/green-clipart-image-59069/ - Public Domain
        (bike.png) https://clipartix.com/bicycle-clip-art-image-6000/ - Public Domain
        (ketchup.png) https://clipartix.com/ketchup-bottle-clipart-image-60212/ - Public Domain
        (mouse.png) https://clipartix.com/mickey-mouse-clipart-image-52285/ - Public Domain
        (book.png) https://clipartix.com/open-book-clip-art-image-22013/ - Public Domain
        (ill-person.png) https://clipartix.com/ill-person-clipart-image-58178/ - Public Domain
        (dolphin.png) https://clipartix.com/whale-clip-art-image-2923/ - Public Domain
        */
        private String[] paths = {"/apple.png", "/bike.png", "/book.png", "/ketchup.png", "/mouse.png", "/orange.png", "/dolphin.png", "/ill-person.png"};
        private ArrayList<Integer> indexList = new ArrayList<>();
        private Timer timer;
        private Thread timerThread;
        
        public View() {
            dialog = new JDialog(frame, "Choose a difficulty level:");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            JButton buttonEasy = new JButton("Easy");
            JButton buttonMedium = new JButton("Medium");
            JButton buttonHard = new JButton("Hard");
            
            buttonEasy.addActionListener(e -> {
                createMainFrame(6, buttonEasy.getText());
            });
            
            buttonMedium.addActionListener(e -> {
                createMainFrame(12, buttonMedium.getText());
            });
            
            buttonHard.addActionListener(e -> {
                createMainFrame(16, buttonHard.getText());
            });
            
            JPanel dialogPanel = new JPanel();

            dialogPanel.add(buttonEasy);
            dialogPanel.add(buttonMedium);
            dialogPanel.add(buttonHard);

            dialog.add(dialogPanel);
            
            dialog.pack();
            dialog.setVisible(true);
        }
        
        private void createMainFrame(int numCards, String level) {
            frame = new JFrame("Memory Card Game (" + level + ")");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(525, 600));
            
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            
            timePanel = new JPanel();
            timePanel.setBackground(Color.WHITE);
            timeLabel = new JLabel("0", SwingConstants.CENTER);
            timePanel.add(timeLabel);
            
            cardPanel = new JPanel();
            cardPanel.setLayout(new GridLayout(4, 3, 5, 5));
            cardPanel.setBackground(Color.WHITE);
            cardPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            memoryButtons = new JToggleButton[numCards];
            
            for(int i = 0; i < numCards; i++) {
                indexList.add(i/2);
            }
            
            for(int i = 0; i < numCards; i++) {
                memoryButtons[i] = new JToggleButton();
                memoryButtons[i].setIcon(new ImageIcon(getClass().getResource("/blank.png")));
                Random random = new Random();
                int index = indexList.get(random.nextInt(indexList.size()));
                memoryButtons[i].setSelectedIcon(new ImageIcon(getClass().getResource(paths[index])));
                indexList.remove((Integer) index);
                buttonFunctionality(i, numCards);
            }
            
            Random rand = new Random();

            // took inspiration from this question for randomizer implementation
            // https://stackoverflow.com/questions/4040001/creating-random-numbers-with-no-duplicates
            // credits to user Woong-Sup Jung
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

            showCardsMomentary(numCards, level);
            
            startTimerThread(level);
        }
        
        private void buttonFunctionality(int i, int numCards) {
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
        
        private void startTimerThread(String level) {
            if(level.equals("Easy")) {
                timerThread = new Thread(() -> {
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long startTime = System.currentTimeMillis();
                    while(!timerThread.isInterrupted()) {
                        long currentTime = System.currentTimeMillis() - startTime;
                        SwingUtilities.invokeLater(() -> {
                            timeLabel.setText("Time Taken: " + (double) (currentTime) / 1000.0 + " sec");
                        });
                        try {
                            Thread.sleep(0);
                        } catch (Exception e) {
                            break;
                        }
                    }
                });
                timerThread.start();
            }
            else if(level.equals("Medium")) {
                timerThread = new Thread(() -> {
                    try {
                        Thread.sleep(2400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long startTime = System.currentTimeMillis();
                    while(!timerThread.isInterrupted()) {
                        long currentTime = System.currentTimeMillis() - startTime;
                        SwingUtilities.invokeLater(() -> {
                            timeLabel.setText("Time Taken: " + (double) (currentTime) / 1000.0 + " sec");
                        });
                        try {
                            Thread.sleep(0);
                        } catch (Exception e) {
                            break;
                        }
                    }
                });
                timerThread.start();
            }
            else if(level.equals("Hard")) {
                timerThread = new Thread(() -> {
                    try {
                        Thread.sleep(3200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long startTime = System.currentTimeMillis();
                    while(!timerThread.isInterrupted()) {
                        long currentTime = System.currentTimeMillis() - startTime;
                        SwingUtilities.invokeLater(() -> {
                            timeLabel.setText("Time Taken: " + (double) (currentTime) / 1000.0 + " sec");
                        });
                        try {
                            Thread.sleep(0);
                        } catch (Exception e) {
                            break;
                        }
                    }
                });
                timerThread.start();
            }
        }
        
        private void showCardsMomentary(int numCards, String level) {
            for(int i = 0; i < numCards; i++) {
                memoryButtons[i].setSelected(true);
            }
            
            if(level.equals("Easy")) {
                timer = new Timer(1200, e -> {
                    for(int i = 0; i < numCards; i++) {
                        memoryButtons[i].setSelected(false);
                    }
                });
            }
            else if(level.equals("Medium")) {
                timer = new Timer(2400, e -> {
                    for(int i = 0; i < numCards; i++) {
                        memoryButtons[i].setSelected(false);
                    }
                });
            }
            else if(level.equals("Hard")) {
                timer = new Timer(3200, e -> {
                    for(int i = 0; i < numCards; i++) {
                        memoryButtons[i].setSelected(false);
                    }
                });
            }
            
            timer.setRepeats(false);
            timer.start();
        }
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }

}
