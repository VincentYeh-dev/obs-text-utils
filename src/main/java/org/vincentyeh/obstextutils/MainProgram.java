package org.vincentyeh.obstextutils;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainProgram {
    private JPanel root;
    private JButton browseButton;
    private JTextField textField_file;
    private JSlider slider_update_interval;
    private JTextField textField_format;
    private JButton startButton;
    private JButton stopButton;
    private JLabel label_preview;
    private JLabel label_interval;
    private JSpinner spinner_hours;
    private JSpinner spinner_minutes;
    private JSpinner spinner_seconds;

    private boolean run;
    public MainProgram() {
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser=new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        String name=f.getName();
                        if(name.contains("."))
                            return name.substring(name.lastIndexOf(".")+1).equals("txt");

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "*.txt (Text file)";
                    }
                });
                int result=chooser.showDialog(null,null);
                if(result==JFileChooser.APPROVE_OPTION){
                    textField_file.setText(chooser.getSelectedFile().toString());
                    System.out.println(chooser.getSelectedFile());
                }

            }
        });

        slider_update_interval.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                label_interval.setText(slider_update_interval.getValue()+" ms");
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
    }

    private void start(){
        final Date goalTime=getGoalTime();
        final File file=new File(textField_file.getText());
        final String format_pattern=textField_format.getText();
        final int update_interval= slider_update_interval.getValue();

        final SimpleDateFormat format= new SimpleDateFormat(format_pattern);
        Thread thread=generateThread(goalTime,format,file,update_interval);
        run=true;
        thread.start();
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }
    private void stop(){
        run=false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private Thread generateThread(Date goalTime, SimpleDateFormat format, File file, int update_interval){
        return new Thread(() -> {
            while(run&&goalTime.after(new Date())){
                Date date=getDifference(goalTime);
                String output=format.format(date);
                update(output,file);
                try {
                    Thread.sleep(update_interval);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            String output=format.format(getZero());
            update(output,file);

            stop();
        });
    }
    private void update(String output,File file){
        label_preview.setText(output);
        try(BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), StandardCharsets.UTF_8))){
            writer.write(output);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private Date getDifference(Date goal){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(goal);
        Calendar current_calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,-current_calendar.get(Calendar.HOUR_OF_DAY));
        calendar.add(Calendar.MINUTE,-current_calendar.get(Calendar.MINUTE));
        calendar.add(Calendar.SECOND,-current_calendar.get(Calendar.SECOND));
        return calendar.getTime();
    }
    private Date getZero(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(0, Calendar.JANUARY,0,0,0,0);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        FlatDarkLaf.setup();
        JFrame frame = new JFrame("OBS Text Utils");
        frame.setContentPane(new MainProgram().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private Date getGoalTime(){
        Calendar calendar =Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY,(int)spinner_hours.getValue());
        calendar.add(Calendar.MINUTE,(int)spinner_minutes.getValue());
        calendar.add(Calendar.SECOND,(int)spinner_seconds.getValue());
        return calendar.getTime();
    }
}
