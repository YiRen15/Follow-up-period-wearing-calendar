package com.calendar.gui;

import com.calendar.model.DetectionTask;
import com.calendar.model.WearingCalendarRule;
import com.calendar.service.DateCalculator;
import com.calendar.service.WearingCalendarService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 随访周期规则添加面板
 * @version 1.0
 */
public class RuleAddPanel extends JPanel {

    private JTextField txtUnitCnt;
    private JComboBox<String> comboUnit;
    private JTextField txtCheckCnt;
    private DefaultListModel<String> listModel;
    private JList<String> ruleJList;
    private JLabel lblDetectionPeriod;

    private JCheckBox chkCustomPeriod;
    private JTextField txtCustomPeriodNum;
    private JComboBox<String> comboCustomPeriodUnit;

    private List<DetectionTask> currentTasks = new ArrayList<DetectionTask>();
    private Runnable onDirectTest;

    public void setOnDirectTest(Runnable callback) { this.onDirectTest = callback; }
    public List<DetectionTask> getCurrentTasks() { return currentTasks; }

    public WearingCalendarRule getCurrentRule() {
        WearingCalendarRule rule = new WearingCalendarRule();
        rule.setDetectionTaskList(new ArrayList<>(currentTasks));
        if (chkCustomPeriod.isSelected()) {
            rule.setUseCustomDetectionPeriod(true);
            try {
                rule.setDetectionPeriodNum(Integer.parseInt(txtCustomPeriodNum.getText().trim()));
            } catch (Exception ignored) {
            }
            rule.setDetectionPeriodUnit(DetectionTask.parseUnitByName((String) comboCustomPeriodUnit.getSelectedItem()));
        }
        return rule;
    }

    public RuleAddPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 左侧输入面板
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "随访周期规则添加", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14)));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. 周期数值
        JLabel lblUnitCnt = new JLabel("周期数值:");
        lblUnitCnt.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 12));
        txtUnitCnt = new JTextField("1");
        txtUnitCnt.setPreferredSize(new Dimension(45, 28));
        txtUnitCnt.setHorizontalAlignment(JTextField.CENTER);

        // 2. 单位类型（移除季度）
        JLabel lblUnit = new JLabel("单位类型:");
        lblUnit.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 12));
        comboUnit = new JComboBox<String>(new String[]{"周", "月", "天", "年"});
        comboUnit.setPreferredSize(new Dimension(65, 28));

        // 3. 检测次数
        JLabel lblCheckCnt = new JLabel("检测次数:");
        lblCheckCnt.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 12));
        txtCheckCnt = new JTextField("1");
        txtCheckCnt.setPreferredSize(new Dimension(45, 28));
        txtCheckCnt.setHorizontalAlignment(JTextField.CENTER);

        // 单行网格添加
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(lblUnitCnt, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(txtUnitCnt, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(lblUnit, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(comboUnit, gbc);

        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(lblCheckCnt, gbc);

        gbc.gridx = 5; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(txtCheckCnt, gbc);

        // 按钮行
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("添加规则");
        btnAdd.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        btnAdd.setPreferredSize(new Dimension(95, 32));

        JButton btnDelete = new JButton("删除选中");
        btnDelete.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        btnDelete.setPreferredSize(new Dimension(95, 32));

        JButton btnClear = new JButton("清空规则");
        btnClear.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        btnClear.setPreferredSize(new Dimension(95, 32));

        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.NORTH);
        topContainer.add(btnPanel, BorderLayout.SOUTH);

        leftPanel.add(topContainer, BorderLayout.NORTH);

        // 右侧显示区域
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "具体规则显示区域", TitledBorder.LEFT, TitledBorder.TOP,
                new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14)));

        listModel = new DefaultListModel<String>();
        ruleJList = new JList<String>(listModel);
        ruleJList.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 14));
        ruleJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(ruleJList);

        JPanel bottomRightPanel = new JPanel(new BorderLayout(10, 10));
        bottomRightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblDetectionPeriod = new JLabel("根据规则计算检测周期为 0 天", SwingConstants.LEFT);
        lblDetectionPeriod.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 13));
        lblDetectionPeriod.setForeground(new Color(33, 115, 70));

        // 自定义检测周期覆盖区域
        JPanel customPeriodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        chkCustomPeriod = new JCheckBox("使用自定义检测周期");
        chkCustomPeriod.setFont(new Font(MainFrame.getPreferredFontName(), Font.PLAIN, 12));
        txtCustomPeriodNum = new JTextField("1", 3);
        txtCustomPeriodNum.setEnabled(false);
        comboCustomPeriodUnit = new JComboBox<String>(new String[]{"天", "周", "个月", "年"});
        comboCustomPeriodUnit.setSelectedIndex(3); // 默认年
        comboCustomPeriodUnit.setEnabled(false);

        customPeriodPanel.add(chkCustomPeriod);
        customPeriodPanel.add(txtCustomPeriodNum);
        customPeriodPanel.add(comboCustomPeriodUnit);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("生成Excel规则文件");
        btnExport.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14));
        btnExport.setBackground(new Color(33, 115, 70));
        btnExport.setOpaque(true);
        btnExport.setBorderPainted(false);
        btnExport.setForeground(Color.WHITE);

        JButton btnDirectTest = new JButton("直接测试");
        btnDirectTest.setFont(new Font(MainFrame.getPreferredFontName(), Font.BOLD, 14));

        buttonPanel.add(btnDirectTest);
        buttonPanel.add(btnExport);

        JPanel infoAndCustomPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        infoAndCustomPanel.add(lblDetectionPeriod);
        infoAndCustomPanel.add(customPeriodPanel);

        bottomRightPanel.add(infoAndCustomPanel, BorderLayout.NORTH);
        bottomRightPanel.add(buttonPanel, BorderLayout.SOUTH);

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);

        // 主界面分栏分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(470);
        add(splitPane, BorderLayout.CENTER);

        // 事件监听
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddRule();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentTasks.clear();
                updateRuleDisplay();
            }
        });

        btnDelete.addActionListener(e -> {
            int selectedIndex = ruleJList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < currentTasks.size()) {
                currentTasks.remove(selectedIndex);
                updateRuleDisplay();
            } else {
                JOptionPane.showMessageDialog(this, "请先在右侧列表中选中要删除的规则！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        ActionListener customPeriodListener = e -> {
            boolean selected = chkCustomPeriod.isSelected();
            txtCustomPeriodNum.setEnabled(selected);
            comboCustomPeriodUnit.setEnabled(selected);
            updateRuleDisplay();
        };
        chkCustomPeriod.addActionListener(customPeriodListener);
        comboCustomPeriodUnit.addActionListener(customPeriodListener);
        txtCustomPeriodNum.addActionListener(customPeriodListener);

        txtCustomPeriodNum.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateRuleDisplay(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateRuleDisplay(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateRuleDisplay(); }
        });

        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!validateCustomPeriod()) return;
                handleExportExcel();
            }
        });

        btnDirectTest.addActionListener(e -> {
            if (!validateCustomPeriod()) return;
            if (onDirectTest != null) {
                onDirectTest.run();
            }
        });
    }

    private void handleAddRule() {
        try {
            int unitCnt = Integer.parseInt(txtUnitCnt.getText().trim());
            if (unitCnt <= 0) {
                JOptionPane.showMessageDialog(this, "周期数值必须大于 0", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedUnit = (String) comboUnit.getSelectedItem();
            int unit = DetectionTask.parseUnitByName(selectedUnit);

            int maxAllowedCount;
            String unitDesc;
            switch (unit) {
                case 0: // 天
                    maxAllowedCount = 1 * unitCnt;
                    unitDesc = "每天最多不能超过 1 次（此周期最多 " + maxAllowedCount + " 次）";
                    break;
                case 1: // 周
                    maxAllowedCount = 7 * unitCnt;
                    unitDesc = "每周最多不能超过 7 次（此周期最多 " + maxAllowedCount + " 次）";
                    break;
                case 2: // 月
                    maxAllowedCount = 31 * unitCnt;
                    unitDesc = "每月最多不能超过 31 次（此周期最多 " + maxAllowedCount + " 次）";
                    break;
                case 3: // 年
                    maxAllowedCount = 365 * unitCnt;
                    unitDesc = "每年最多不能超过 365 次（此周期最多 " + maxAllowedCount + " 次）";
                    break;
                default:
                    maxAllowedCount = 365 * unitCnt;
                    unitDesc = "检测次数超出合理限制";
                    break;
            }

            int checkCnt = Integer.parseInt(txtCheckCnt.getText().trim());
            if (checkCnt <= 0 || checkCnt > maxAllowedCount) {
                JOptionPane.showMessageDialog(this, "检测次数不合法！" + unitDesc, "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DetectionTask task = new DetectionTask(unit, unitCnt, checkCnt);
            currentTasks.add(task);
            updateRuleDisplay();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效数字！", "格式错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRuleDisplay() {
        listModel.clear();
        for (int i = 0; i < currentTasks.size(); i++) {
            DetectionTask task = currentTasks.get(i);
            listModel.addElement("佩戴周期 " + (i + 1) + " :   " + task.getDisplayText());
        }

        if (currentTasks.isEmpty()) {
            lblDetectionPeriod.setText("根据规则计算检测周期为 0 天");
        } else {
            int[] periodInfo = DateCalculator.calculateDetectionPeriodFromTasks(currentTasks);
            int periodNum = periodInfo[0];
            int periodUnit = periodInfo[1];
            String unitName = DateCalculator.getPeriodUnitName(periodUnit);

            if (chkCustomPeriod.isSelected()) {
                String numStr = txtCustomPeriodNum.getText().trim();
                String customUnitStr = (String) comboCustomPeriodUnit.getSelectedItem();
                try {
                    int val = Integer.parseInt(numStr);
                    if (val <= 0) {
                        lblDetectionPeriod.setForeground(Color.RED);
                        lblDetectionPeriod.setText("⚠️ 自定义检测周期必须为大于 0 的正整数！");
                    } else {
                        lblDetectionPeriod.setForeground(new Color(33, 115, 70));
                        lblDetectionPeriod.setText("根据自定义设置检测周期为 " + val + " " + customUnitStr);
                    }
                } catch (Exception ex) {
                    lblDetectionPeriod.setForeground(Color.RED);
                    lblDetectionPeriod.setText("⚠️ 自定义检测周期请输入有效正整数（不可填小数或符号）");
                }
            } else {
                lblDetectionPeriod.setForeground(new Color(33, 115, 70));
                int totalEstDays = (periodUnit == 0 ? periodNum : periodNum * (periodUnit == 3 ? 365 : (periodUnit == 2 ? 30 : 7)));
                lblDetectionPeriod.setText("所有周期自动计算检测周期为 " + periodNum + " " + unitName + " (实际累积总天数: " + totalEstDays + " 天)");
            }
        }
    }

    private void handleExportExcel() {
        if (currentTasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先添加至少一条随访周期规则！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存 Excel 规则文件");
        fileChooser.setSelectedFile(new File("随访周期规则.xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }
            try {
                WearingCalendarRule rule = new WearingCalendarRule();
                rule.setDetectionTaskList(currentTasks);
                if (chkCustomPeriod.isSelected()) {
                    rule.setUseCustomDetectionPeriod(true);
                    try {
                        rule.setDetectionPeriodNum(Integer.parseInt(txtCustomPeriodNum.getText().trim()));
                    } catch (Exception ignored) {
                    }
                    rule.setDetectionPeriodUnit(DetectionTask.parseUnitByName((String) comboCustomPeriodUnit.getSelectedItem()));
                }
                rule.setStartWearingDate(LocalDate.now().format(DateCalculator.DISPLAY_FORMATTER));
                WearingCalendarService.exportExcel(rule, fileToSave);
                JOptionPane.showMessageDialog(this, "Excel 规则文件生成成功！\n文件路径：" + fileToSave.getAbsolutePath(), "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "导出 Excel 失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateCustomPeriod() {
        if (!chkCustomPeriod.isSelected()) return true;
        String numStr = txtCustomPeriodNum.getText().trim();
        try {
            int val = Integer.parseInt(numStr);
            if (val <= 0) {
                JOptionPane.showMessageDialog(this, "自定义检测周期数值必须为大于 0 的正整数！", "格式错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "【格式错误】自定义检测周期必须为有效正整数（不可填小数或符号）！\n\n提示：如需设置半年或半月，请将单位切换为【个月/周/天】输入整数。", "输入格式错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
