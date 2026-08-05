package com.calendar;

import com.calendar.gui.MainFrame;

import javax.swing.*;

/**
 * 程序启动主入口类
 */
public class Main {
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                String msg = e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : e.toString();
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "程序发生意外错误：\n" + msg, 
                    "系统错误", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
