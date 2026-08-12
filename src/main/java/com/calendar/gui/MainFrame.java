package com.calendar.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 客户端主界面窗口框架
 * @version 1.0.1
 */
public class MainFrame extends JFrame {

    static String getPreferredFontName() {
        String[] candidates = {"Microsoft YaHei", "PingFang SC", "Noto Sans CJK SC", "SimHei"};
        String[] available = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        java.util.Set<String> fontSet = new java.util.HashSet<>(java.util.Arrays.asList(available));
        for (String name : candidates) {
            if (fontSet.contains(name)) return name;
        }
        return "SansSerif";
    }

    public MainFrame() {
        // 启用现代高清 Nimbus LookAndFeel，彻底解决 macOS 硬编码挤压顶部页签的问题
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("随访周期佩戴日历及规则生成测试系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(getPreferredFontName(), Font.BOLD, 14));

        RuleAddPanel ruleAddPanel = new RuleAddPanel();
        RuleTestPanel ruleTestPanel = new RuleTestPanel();

        ruleAddPanel.setOnDirectTest(() -> {
            ruleTestPanel.loadRulesDirectly(ruleAddPanel.getCurrentRule());
            tabbedPane.setSelectedComponent(ruleTestPanel);
        });

        tabbedPane.addTab("规则添加", ruleAddPanel);
        tabbedPane.addTab("规则测试", ruleTestPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
