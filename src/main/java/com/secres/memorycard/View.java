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

    private JDialog dialog;
    private JFrame frame;
    private JPanel mainPanel, timePanel, cardPanel;
    private JLabel timeLabel;
    private int numCards = 12;
    private JToggleButton[] memoryButtons;
    private String[] paths = {"/apple.png", "/bike.png", "/book.png", "/ketchup.png", "/mouse.png", "/orange.png", "/dolphin.png", "/ill-person.png"};
    private ArrayList<Integer> indexList = new ArrayList<>();
    private Timer timer;
    private Thread timerThread;
    
    public View() {
        dialog = new JDialog(frame, "Choose a difficulty level:");
        
        JButton buttonEasy = new JButton("Easy");
        JButton buttonMedium = new JButton("Medium");
        JButton buttonHard = new JButton("Hard");

        buttonEasy.addActionListener(e -> {
            numCards = 6;
            createMainFrame();
        });
        
        buttonMedium.addActionListener(e -> {
            numCards = 12;
            createMainFrame();
        });
        
        buttonHard.addActionListener(e -> {
            numCards = 16;
            createMainFrame();
        });

        JPanel dialogPanel = new JPanel();

        dialogPanel.add(buttonEasy);
        dialogPanel.add(buttonMedium);
        dialogPanel.add(buttonHard);

        dialog.add(dialogPanel);
        
        dialog.pack();
        dialog.setVisible(true);
    }
    
    private void createMainFrame() {
        frame = new JFrame("Memory Card Game");
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
            // used following answer for reading the image
            // https://stackoverflow.com/questions/4801386/how-do-i-add-an-image-to-a-jbutton/49324649#49324649
            memoryButtons[i].setIcon(new ImageIcon(getClass().getResource("/blank.png")));
            Random random = new Random();
            int index = indexList.get(random.nextInt(indexList.size()));
            memoryButtons[i].setSelectedIcon(new ImageIcon(getClass().getResource(paths[index])));
            indexList.remove((Object)index);
            buttonFunctionality(i);
        }
        
        Random rand = new Random();

        // took inspiration from this question for randomizer implementation
        // https://stackoverflow.com/questions/4040001/creating-random-numbers-with-no-duplicates
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
        
        startTimerThread();

        showCardsMomentary(numCards*200);
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
    
    private void startTimerThread() {
        // used following answer, but adopted to this game
        // https://stackoverflow.com/questions/6168498/how-to-put-a-timer-on-a-jlabel-to-update-itself-every-second/6168602#6168602
        timerThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            while(true) {
                long time = System.currentTimeMillis() - start;
                double seconds = (double) time / (double) 1000;
                SwingUtilities.invokeLater(() -> {
                    timeLabel.setText("Time Passed: " + seconds + " sec");
                });
                try {
                    Thread.sleep(0);
                } catch(Exception e) {
                    break;
                }
            }
        });
        timerThread.start();
    }
    
    private void showCardsMomentary(int delay) {
        for(int i = 0; i < numCards; i++) {
            memoryButtons[i].setSelected(true);
        }

        timer = new Timer(delay, e -> {
            for(int i = 0; i < numCards; i++) {
                memoryButtons[i].setSelected(false);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
}
