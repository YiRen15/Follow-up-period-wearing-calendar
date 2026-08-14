package com.calendar;

import com.calendar.gui.MainFrame;

import javax.swing.*;

/**
 * 程序启动主入口类
 * @version Fupwc-1.00.02
 */
public class Main {

    /**
     * 当前系统/Jar 包版本号
     */
    public static final String VERSION = "Fupwc-1.00.02";

    /**
     * 获取版本号字符串
     * @return 版本号 (如 "Fupwc-1.00.01")
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * 打印版本号到标准控制台输出
     */
    public static void printVersion() {
        System.out.println(VERSION);
    }

    public static void main(String[] args) {
        // 命令行支持查看版本内容
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if ("-v".equalsIgnoreCase(arg) || "-version".equalsIgnoreCase(arg) || "--version".equalsIgnoreCase(arg) || "version".equalsIgnoreCase(arg)) {
                    printVersion();
                    return;
                }
            }
        }
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
