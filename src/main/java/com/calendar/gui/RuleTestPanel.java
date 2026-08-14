package com.calendar.gui;

import com.calendar.model.DetectionTask;
import com.calendar.model.WearingCalendarRule;
import com.calendar.service.DateCalculator;
import com.calendar.service.WearingCalendarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * 随访周期规则测试面板
 * @version 1.0.1
 */
public class RuleTestPanel extends JPanel {

    private DefaultListModel<String> listModel;
    private JList<String> ruleJList;
    private JLabel lblDetectionPeriod;

    private JComboBox<String> comboYear;
    private JComboBox<String> comboMonth;
    private JComboBox<String> comboDay;

    private JComboBox<String> comboSurgeryYear;
    private JComboBox<String> comboSurgeryMonth;
    private JComboBox<String> comboSurgeryDay;

    private JComboBox<String> comboSuggestYear;
    private JComboBox<String> comboSuggestMonth;
    private JComboBox<String> comboSuggestDay;

    private JTable dateResultTable;
    private DefaultTableModel tableModel;

    private WearingCalendarRule importedRule;

    public RuleTestPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 左侧：规则导入与规则明细显示
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "具体规则显示区域", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14)));

        JButton btnImport = new JButton("导入规则");
        btnImport.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14));
        btnImport.setPreferredSize(new Dimension(120, 36));

        JPanel topImportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topImportPanel.add(btnImport);

        listModel = new DefaultListModel<String>();
        ruleJList = new JList<String>(listModel);
        ruleJList.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(ruleJList);

        JPanel bottomRightPanel = new JPanel(new BorderLayout(10, 10));
        bottomRightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblDetectionPeriod = new JLabel("根据规则计算检测周期为 0 天", SwingConstants.LEFT);
        lblDetectionPeriod.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        lblDetectionPeriod.setForeground(new Color(33, 115, 70));

        bottomRightPanel.add(lblDetectionPeriod, BorderLayout.CENTER);

        leftPanel.add(topImportPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        // 右侧：起始日期选择与个人佩戴日历推算显示
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "个人佩戴日历推算测试", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14)));

        // 年月日选择面板（包含佩戴起始日、手术日期、建议佩戴日）
        JPanel dateSelectPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        dateSelectPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        LocalDate now = LocalDate.now();
        String[] years = new String[21];
        years[0] = "--";
        for (int i = 0; i < 20; i++) years[i + 1] = String.valueOf(now.getYear() - 5 + i);

        String[] months = new String[13];
        months[0] = "--";
        for (int i = 0; i < 12; i++) months[i + 1] = String.valueOf(i + 1);

        String[] days = new String[32];
        days[0] = "--";
        for (int i = 0; i < 31; i++) days[i + 1] = String.valueOf(i + 1);

        // 1. 佩戴起始日
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row1.add(new JLabel("佩戴起始日:"));
        comboYear = new JComboBox<String>(years); comboYear.setSelectedItem("--");
        comboMonth = new JComboBox<String>(months); comboMonth.setSelectedItem("--");
        comboDay = new JComboBox<String>(days); comboDay.setSelectedItem("--");
        row1.add(comboYear); row1.add(new JLabel("年"));
        row1.add(comboMonth); row1.add(new JLabel("月"));
        row1.add(comboDay); row1.add(new JLabel("日"));

        // 2. 手术日期
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row2.add(new JLabel("手术日期:    "));
        comboSurgeryYear = new JComboBox<String>(years); comboSurgeryYear.setSelectedItem("--");
        comboSurgeryMonth = new JComboBox<String>(months); comboSurgeryMonth.setSelectedItem("--");
        comboSurgeryDay = new JComboBox<String>(days); comboSurgeryDay.setSelectedItem("--");
        row2.add(comboSurgeryYear); row2.add(new JLabel("年"));
        row2.add(comboSurgeryMonth); row2.add(new JLabel("月"));
        row2.add(comboSurgeryDay); row2.add(new JLabel("日"));

        // 3. 建议佩戴日
        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        row3.add(new JLabel("建议佩戴日:"));
        comboSuggestYear = new JComboBox<String>(years); comboSuggestYear.setSelectedItem("--");
        comboSuggestMonth = new JComboBox<String>(months); comboSuggestMonth.setSelectedItem("--");
        comboSuggestDay = new JComboBox<String>(days); comboSuggestDay.setSelectedItem("--");
        row3.add(comboSuggestYear); row3.add(new JLabel("年"));
        row3.add(comboSuggestMonth); row3.add(new JLabel("月"));
        row3.add(comboSuggestDay); row3.add(new JLabel("日"));

        dateSelectPanel.add(row1);
        dateSelectPanel.add(row2);
        dateSelectPanel.add(row3);

        // 日历表格面板
        String[] columnNames = {"佩戴周期", "起始时间", "结束时间", "检测规则明细"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dateResultTable = new JTable(tableModel);
        dateResultTable.setRowHeight(28);
        dateResultTable.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < dateResultTable.getColumnCount(); i++) {
            dateResultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane tableScrollPane = new JScrollPane(dateResultTable);

        // 底部按钮面板（查看JSON数据）
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnViewJson = new JButton("查看JSON数据");
        btnViewJson.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        btnViewJson.setBackground(new Color(41, 98, 166));
        btnViewJson.setOpaque(true);
        btnViewJson.setBorderPainted(false);
        btnViewJson.setForeground(Color.WHITE);
        bottomPanel.add(btnViewJson);

        rightPanel.add(dateSelectPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        btnViewJson.addActionListener(e -> handleViewJson());

        // 分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(350);
        add(splitPane, BorderLayout.CENTER);

        // 事件监听
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleImportExcel();
            }
        });

        ActionListener dateChangeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == comboYear || e.getSource() == comboMonth) {
                    updateDayComboBox();
                }
                recalculateAndRefreshTable();
            }
        };

        comboYear.addActionListener(dateChangeListener);
        comboMonth.addActionListener(dateChangeListener);
        comboDay.addActionListener(dateChangeListener);

        comboSurgeryYear.addActionListener(dateChangeListener);
        comboSurgeryMonth.addActionListener(dateChangeListener);
        comboSurgeryDay.addActionListener(dateChangeListener);

        comboSuggestYear.addActionListener(dateChangeListener);
        comboSuggestMonth.addActionListener(dateChangeListener);
        comboSuggestDay.addActionListener(dateChangeListener);

        comboSurgeryYear.addActionListener(dateChangeListener);
        comboSurgeryMonth.addActionListener(dateChangeListener);
        comboSurgeryDay.addActionListener(dateChangeListener);

        java.awt.event.ItemListener itemListener = e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                if (e.getSource() == comboYear || e.getSource() == comboMonth) {
                    updateDayComboBox();
                }
                recalculateAndRefreshTable();
            }
        };
        comboYear.addItemListener(itemListener);
        comboMonth.addItemListener(itemListener);
        comboDay.addItemListener(itemListener);
        comboSurgeryYear.addItemListener(itemListener);
        comboSurgeryMonth.addItemListener(itemListener);
        comboSurgeryDay.addItemListener(itemListener);
        comboSuggestYear.addItemListener(itemListener);
        comboSuggestMonth.addItemListener(itemListener);
        comboSuggestDay.addItemListener(itemListener);
    }

    private void updateDayComboBox() {
        if (comboYear.getSelectedItem() == null || comboMonth.getSelectedItem() == null) return;
        String yStr = (String) comboYear.getSelectedItem();
        String mStr = (String) comboMonth.getSelectedItem();
        if ("--".equals(yStr) || "--".equals(mStr)) return;

        try {
            int year = Integer.parseInt(yStr);
            int month = Integer.parseInt(mStr);
            int maxDay = java.time.YearMonth.of(year, month).lengthOfMonth();
            String currentDayStr = (String) comboDay.getSelectedItem();

            comboDay.removeAllItems();
            comboDay.addItem("--");
            for (int i = 1; i <= maxDay; i++) {
                comboDay.addItem(String.valueOf(i));
            }
            if (currentDayStr != null && !"--".equals(currentDayStr)) {
                int curD = Integer.parseInt(currentDayStr);
                comboDay.setSelectedItem(String.valueOf(Math.min(curD, maxDay)));
            } else {
                comboDay.setSelectedItem("--");
            }
        } catch (Exception ignored) {
        }
    }

    private void handleImportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择 Excel 规则文件");
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                importedRule = WearingCalendarService.parseExcel(selectedFile);
                updateRuleDisplay();
                recalculateAndRefreshTable();
                JOptionPane.showMessageDialog(this, "Excel 规则读取成功！共解析出 " + importedRule.getTaskCount() + " 个随访周期。", "读取成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "读取 Excel 错误：" + ex.getMessage(), "读取失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateRuleDisplay() {
        listModel.clear();
        if (importedRule == null || importedRule.getDetectionTaskList() == null) {
            lblDetectionPeriod.setText("根据规则计算检测周期为 0 天");
            return;
        }

        List<DetectionTask> taskList = importedRule.getDetectionTaskList();
        for (int i = 0; i < taskList.size(); i++) {
            DetectionTask task = taskList.get(i);
            listModel.addElement("佩戴周期 " + (i + 1) + " :   " + task.getDisplayText());
        }

        if (importedRule.isUseCustomDetectionPeriod()) {
            lblDetectionPeriod.setText("根据自定义设置检测周期为 " + importedRule.getDetectionPeriodDisplay());
        } else {
            int totalEstDays = DateCalculator.calculateTotalActualDays(importedRule.getDetectionTaskList(), importedRule.getStartWearingDate());
            lblDetectionPeriod.setText("所有周期自动计算检测周期为 " + importedRule.getDetectionPeriodDisplay() + " (实际累积总天数: " + totalEstDays + " 天)");
        }
    }

    private void recalculateAndRefreshTable() {
        tableModel.setRowCount(0);
        if (importedRule == null || importedRule.getDetectionTaskList() == null
                || importedRule.getDetectionTaskList().isEmpty()) {
            return;
        }

        // 1. 佩戴起始日处理
        String yStr = (String) comboYear.getSelectedItem();
        String mStr = (String) comboMonth.getSelectedItem();
        String dStr = (String) comboDay.getSelectedItem();

        if (yStr != null && mStr != null && dStr != null && !"--".equals(yStr) && !"--".equals(mStr) && !"--".equals(dStr)) {
            try {
                int year = Integer.parseInt(yStr);
                int month = Integer.parseInt(mStr);
                int day = Integer.parseInt(dStr);
                LocalDate selectedStartDate = LocalDate.of(year, month, day);
                String startDateStr = selectedStartDate.format(DateCalculator.DISPLAY_FORMATTER);
                DateCalculator.calculatePeriods(importedRule, startDateStr);
            } catch (Exception ignored) {
                importedRule.setStartWearingDate(null);
            }
        } else {
            importedRule.setStartWearingDate(null);
            // 起始日为空时重置周期起止时间为 null
            for (DetectionTask task : importedRule.getDetectionTaskList()) {
                task.setStartDate(null);
                task.setEndDate(null);
            }
        }

        // 2. 手术日期处理
        String syStr = (String) comboSurgeryYear.getSelectedItem();
        String smStr = (String) comboSurgeryMonth.getSelectedItem();
        String sdStr = (String) comboSurgeryDay.getSelectedItem();

        if (syStr != null && smStr != null && sdStr != null && !"--".equals(syStr) && !"--".equals(smStr) && !"--".equals(sdStr)) {
            try {
                int year = Integer.parseInt(syStr);
                int month = Integer.parseInt(smStr);
                int day = Integer.parseInt(sdStr);
                LocalDate surgeryDate = LocalDate.of(year, month, day);
                importedRule.setSurgeryDate(surgeryDate.format(DateCalculator.DISPLAY_FORMATTER));
            } catch (Exception ignored) {
                importedRule.setSurgeryDate(null);
            }
        } else {
            importedRule.setSurgeryDate(null);
        }

        // 3. 建议佩戴日处理
        String gyStr = (String) comboSuggestYear.getSelectedItem();
        String gmStr = (String) comboSuggestMonth.getSelectedItem();
        String gdStr = (String) comboSuggestDay.getSelectedItem();

        if (gyStr != null && gmStr != null && gdStr != null && !"--".equals(gyStr) && !"--".equals(gmStr) && !"--".equals(gdStr)) {
            try {
                int year = Integer.parseInt(gyStr);
                int month = Integer.parseInt(gmStr);
                int day = Integer.parseInt(gdStr);
                LocalDate suggestDate = LocalDate.of(year, month, day);
                importedRule.setSuggestWearingDate(suggestDate.format(DateCalculator.DISPLAY_FORMATTER));
            } catch (Exception ignored) {
                importedRule.setSuggestWearingDate(null);
            }
        } else {
            importedRule.setSuggestWearingDate(null);
        }

        // 如果起始日有效，表格填充展示起止时间
        if (importedRule.getStartWearingDate() != null) {
            List<DetectionTask> taskList = importedRule.getDetectionTaskList();
            for (int i = 0; i < taskList.size(); i++) {
                DetectionTask item = taskList.get(i);
                tableModel.addRow(new Object[]{
                        "佩戴周期" + (i + 1),
                        item.getStartDate(),
                        item.getEndDate(),
                        item.getDisplayText()
                });
            }
        }
    }

    private void handleViewJson() {
        if (importedRule == null || importedRule.getDetectionTaskList() == null
                || importedRule.getDetectionTaskList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先导入规则或从规则添加面板传入规则！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String json = importedRule.toJsonString();

        // 同时在控制台打印
        System.out.println("\n=========================== 随访周期数据结构 JSON ===========================");
        System.out.println(json);
        System.out.println("=============================================================================\n");

        // 弹出窗口展示 JSON
        JTextArea textArea = new JTextArea(json);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(650, 500));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnCopy = new JButton("复制 JSON 到剪贴板");
        btnCopy.addActionListener(ev -> {
            java.awt.datatransfer.StringSelection selection =
                    new java.awt.datatransfer.StringSelection(json);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "JSON 已复制到剪贴板！", "成功", JOptionPane.INFORMATION_MESSAGE);
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnCopy);
        panel.add(btnPanel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "JSON 数据结构", JOptionPane.PLAIN_MESSAGE);
    }

    public void loadRulesDirectly(WearingCalendarRule rule) {
        if (rule == null || rule.getDetectionTaskList() == null || rule.getDetectionTaskList().isEmpty()) return;
        importedRule = rule;
        if (!importedRule.isUseCustomDetectionPeriod()) {
            importedRule.recalculateDetectionPeriod();
        }
        updateRuleDisplay();
        recalculateAndRefreshTable();
    }

    public void loadRulesDirectly(List<DetectionTask> tasks) {
        if (tasks == null || tasks.isEmpty()) return;
        WearingCalendarRule rule = new WearingCalendarRule();
        rule.setDetectionTaskList(tasks);
        loadRulesDirectly(rule);
    }
}
