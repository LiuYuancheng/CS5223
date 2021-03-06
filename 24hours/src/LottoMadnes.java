
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class LottoEvent implements ItemListener, ActionListener, Runnable{
    LottoMadnes gui;
    Thread playing;
    public LottoEvent(LottoMadnes gui){
        this.gui = gui;
    }

    public void actionPerformed(ActionEvent event){
        String evtCmd = event.getActionCommand();
        switch(evtCmd){
            case "Play" :
                startPlaying();
                break;
            case "Reset":
                resetPlaying();
                break;
            case "Stop":
                stopPlaying();
                break;
            default:
                System.out.println("The event ["+evtCmd+"] is not avaliable.");
        }

    }

    void startPlaying(){
        playing = new Thread(this);
        playing.start();
        gui.play.setEnabled(false);
        gui.stop.setEnabled(true);
        gui.reset.setEnabled(false);
        gui.quickpick.setEnabled(false);
        gui.personal.setEnabled(false);
    }

    void stopPlaying(){
        playing = null;
        gui.play.setEnabled(true);
        gui.stop.setEnabled(false);
        gui.reset.setEnabled(true);
        gui.quickpick.setEnabled(true);
        gui.personal.setEnabled(true);
    }

    void resetPlaying() {
        for (int i = 0; i < 6; i++) {
            gui.numbers[i].setText(null);
            gui.winners[i].setText(null);
        }
        gui.got3Tf.setText("0");
        gui.got4Tf.setText("0");
        gui.got5Tf.setText("0");
        gui.got6Tf.setText("0");
        gui.drawTf.setText("0");
        gui.yearTf.setText("0");
    }

    void addOneToField(JTextField field) {
        int num = Integer.parseInt("0" + field.getText());
        num++;
        field.setText("" + num);
    }

    boolean numberGone(int num, JTextField[] pastNums, int count) {
        for (int i = 0; i < count; i++) {
            if (Integer.parseInt(pastNums[i].getText()) == num)
                return true;
        }
        return false;
    }

    boolean matchedOne(JTextField win, JTextField[] allPicks) {
        for (int i = 0; i < 6; i++) {
            String winText = win.getText();
            if (winText.equals(allPicks[i].getText()))
                return true;
        }
        return false;
    }

    public void itemStateChanged(ItemEvent event) {
        Object item = event.getItem();
        if (item == gui.quickpick) {
            for (int i = 0; i < 6; i++) {
                int pick;
                do {
                    pick = (int) Math.floor(Math.random() * 50 + 1);
                } while (numberGone(pick, gui.numbers, i));
                gui.numbers[i].setText("" + pick);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                gui.numbers[i].setText(null);
            }
        }
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (playing == thisThread) {
            addOneToField(gui.drawTf);
            int draw = Integer.parseInt(gui.drawTf.getText());
            gui.yearTf.setText("" + (float) draw / 104);

            int matches = 0;
            for (int i = 0; i < 6; i++) {
                int ball;
                do {
                    ball = (int) Math.floor(Math.random() * 50 + 1);
                } while (numberGone(ball, gui.winners, i));
                gui.winners[i].setText("" + ball);
                if (matchedOne(gui.winners[i], gui.numbers))
                    matches++;
            }
            switch (matches) {
                case 3:
                    addOneToField(gui.got3Tf);
                    break;
                case 4:
                    addOneToField(gui.got4Tf);
                    break;
                case 5:
                    addOneToField(gui.got5Tf);
                    break;
                case 6:
                    addOneToField(gui.got6Tf);
                    gui.stop.setEnabled(false);
                    gui.play.setEnabled(true);
                    playing = null;
                    break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("" + e);
            }
        }
    }
}

public class LottoMadnes extends JFrame {

    // Add the event listener
    LottoEvent lotto = new LottoEvent(this);

    JPanel row1 = new JPanel();
    ButtonGroup option = new ButtonGroup();
    JCheckBox quickpick = new JCheckBox("Quick Pick", false);
    JCheckBox personal = new JCheckBox("Personal", true);

    JPanel row2 = new JPanel();
    JLabel numLabel = new JLabel("You pick :", JLabel.RIGHT);
    JTextField[] numbers = new JTextField[6];
    JLabel winLb = new JLabel("Winners :", JLabel.RIGHT);
    JTextField[] winners = new JTextField[6];

    JPanel row3 = new JPanel();
    JButton play = new JButton("Play");
    JButton reset = new JButton("Reset");
    JButton stop = new JButton("Stop");

    JPanel row4 = new JPanel();
    JLabel got3Lb = new JLabel("3 of 6", JLabel.RIGHT);
    JTextField got3Tf = new JTextField("0");
    JLabel got4Lb = new JLabel("4 of 6", JLabel.RIGHT);
    JTextField got4Tf = new JTextField("0");
    JLabel got5Lb = new JLabel("5 of 6", JLabel.RIGHT);
    JTextField got5Tf = new JTextField("0");
    JLabel got6Lb = new JLabel("6 of 6", JLabel.RIGHT);
    JTextField got6Tf = new JTextField("0", 10);

    JLabel drawLb = new JLabel("Drawings:", JLabel.RIGHT);
    JTextField drawTf = new JTextField("0");
    JLabel yearLb = new JLabel("Years:", JLabel.RIGHT);
    JTextField yearTf = new JTextField();

    public LottoMadnes() {
        super("Lotto Madness.");
        setSize(550, 270);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GridLayout layout = new GridLayout(5, 1, 10, 10);
        setBackground(new Color(200,210,200));
        setLayout(layout);

        quickpick.addItemListener(lotto);
        personal.addItemListener(lotto);
        play.addActionListener(lotto);
        stop.addActionListener(lotto);
        reset.addActionListener(lotto);

        FlowLayout layout1 = new FlowLayout(FlowLayout.CENTER, 10, 10);
        option.add(quickpick);
        option.add(personal);
        row1.setLayout(layout1);
        row1.add(quickpick);
        row1.add(personal);
        add(row1);

        GridLayout layout2 = new GridLayout(2, 7, 10, 10);
        row2.setLayout(layout2);
        row2.add(numLabel);
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new JTextField();
            row2.add(numbers[i]);
        }

        row2.add(winLb);
        for (int i = 0; i < winners.length; i++) {
            winners[i] = new JTextField();
            winners[i].setEditable(false);
            row2.add(winners[i]);
        }
        add(row2);

        FlowLayout layout3 = new FlowLayout(FlowLayout.CENTER, 10, 10);
        row3.setLayout(layout3);
        row3.add(play);
        row3.add(reset);
        row3.add(stop);
        add(row3);

        GridLayout layout4 = new GridLayout(2, 3, 10, 10);
        row4.setLayout(layout4);

        row4.add(got3Lb);
        got3Tf.setEditable(false);
        row4.add(got3Tf);

        row4.add(got4Lb);
        got3Tf.setEditable(false);
        row4.add(got4Tf);

        row4.add(got5Lb);
        got3Tf.setEditable(false);
        row4.add(got5Tf);

        row4.add(got6Lb);
        got3Tf.setEditable(false);
        row4.add(got6Tf);

        row4.add(drawLb);
        got3Tf.setEditable(false);
        row4.add(drawTf);

        row4.add(yearLb);
        got3Tf.setEditable(false);
        row4.add(yearTf);
        
        add(row4);

        setVisible(true);
    }
}