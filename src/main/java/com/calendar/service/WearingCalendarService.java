package com.calendar.service;

import com.calendar.model.DetectionTask;
import com.calendar.model.WearingCalendarRule;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 随访周期佩戴日历 Excel 导入导出与核心服务 API
 * 供 GUI 客户端及服务器后端系统直接调用
 * @version 1.0
 */
public class WearingCalendarService {

    /**
     * 导出规则为 Excel 文件
     *
     * @param rule       随访周期规则对象
     * @param outputFile 目标文件
     */
    public static void exportExcel(WearingCalendarRule rule, File outputFile) throws IOException {
        byte[] bytes = exportExcelToBytes(rule);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);
            fos.write(bytes);
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 导出规则为字节数组（方便 HTTP 下载或服务器流存储）
     */
    public static byte[] exportExcelToBytes(WearingCalendarRule rule) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("随访佩戴规则");

        // 样式设置
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN);
        centerStyle.setBorderTop(BorderStyle.THIN);
        centerStyle.setBorderLeft(BorderStyle.THIN);
        centerStyle.setBorderRight(BorderStyle.THIN);

        // 如果未设置起始日，默认使用当天
        String startDate = rule.getStartWearingDate();
        if (startDate == null || startDate.trim().isEmpty()) {
            startDate = LocalDate.now().format(DateCalculator.DISPLAY_FORMATTER);
            rule.setStartWearingDate(startDate);
        }

        // 触发一次日期计算
        DateCalculator.calculatePeriods(rule, startDate);

        // 行1: 起始日
        Row row0 = sheet.createRow(0);
        Cell cell00 = row0.createCell(0);
        cell00.setCellValue("起始日");
        cell00.setCellStyle(titleStyle);
        Cell cell01 = row0.createCell(1);
        cell01.setCellValue(rule.getStartWearingDate());

        // 行2: 检测规则
        Row row1 = sheet.createRow(1);
        Cell cell10 = row1.createCell(0);
        cell10.setCellValue("检测规则");
        cell10.setCellStyle(titleStyle);
        Cell cell11 = row1.createCell(1);
        cell11.setCellValue(rule.getWearType() == 0 ? "0: 固定" : "1: 自定义");
        Cell cell12 = row1.createCell(2);
        cell12.setCellValue(rule.getWearType()); // 输出 0 或 1 结果 (WearType)
        cell12.setCellStyle(centerStyle);

        // 行3: 规则说明辅助提示
        Row row2 = sheet.createRow(2);
        Cell cell21 = row2.createCell(1);
        cell21.setCellValue(rule.getWearType() == 0 ? "1: 自定义" : "0: 固定");

        // 行4: 表头
        Row row3 = sheet.createRow(3);
        String[] headersLeft = {"佩戴周期", "检测周期", "单位", "次数"};
        for (int i = 0; i < headersLeft.length; i++) {
            Cell cell = row3.createCell(i);
            cell.setCellValue(headersLeft[i]);
            cell.setCellStyle(headerStyle);
        }
        Cell cellF4 = row3.createCell(5);
        cellF4.setCellValue("起始时间");
        cellF4.setCellStyle(headerStyle);
        Cell cellG4 = row3.createCell(6);
        cellG4.setCellValue("结束时间");
        cellG4.setCellStyle(headerStyle);

        // 数据行写入
        List<DetectionTask> taskList = rule.getDetectionTaskList();
        int curRowIdx = 4;
        if (taskList != null) {
            for (int i = 0; i < taskList.size(); i++) {
                DetectionTask item = taskList.get(i);
                Row row = sheet.createRow(curRowIdx++);

                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(item.getDetectionFrequencyNum());
                c1.setCellStyle(centerStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(item.getUnitName());
                c2.setCellStyle(centerStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(item.getDetectionFrequencyCount());
                c3.setCellStyle(centerStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(item.getStartDate() != null ? item.getStartDate() : "");
                c5.setCellStyle(centerStyle);

                Cell c6 = row.createCell(6);
                c6.setCellValue(item.getEndDate() != null ? item.getEndDate() : "");
                c6.setCellStyle(centerStyle);
            }
        }

        // 底部检测周期行
        Row totalRow = sheet.createRow(curRowIdx);
        Cell cPeriodLabel = totalRow.createCell(2);
        cPeriodLabel.setCellValue("检测周期");
        cPeriodLabel.setCellStyle(headerStyle);

        Cell cPeriodVal = totalRow.createCell(3);
        cPeriodVal.setCellValue(rule.getDetectionPeriodDisplay());
        cPeriodVal.setCellStyle(centerStyle);

        // 调整列宽
        for (int i = 0; i <= 6; i++) {
            sheet.setColumnWidth(i, 4000);
        }

        workbook.write(baos);
        return baos.toByteArray();
        }
    }

    /**
     * 从 Excel 文件导入并解析随访周期规则
     *
     * @param file Excel 文件
     * @return 随访周期规则对象
     */
    public static WearingCalendarRule parseExcel(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        try {
            return parseExcel(fis);
        } finally {
            fis.close();
        }
    }

    /**
     * 从 输入流 导入并解析随访周期规则（支持服务器上传文件流）
     *
     * @param inputStream Excel 文件输入流
     * @return 随访周期规则对象
     */
    public static WearingCalendarRule parseExcel(InputStream inputStream) throws IOException {
        WearingCalendarRule ruleResult = new WearingCalendarRule();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

        // 读取 A1 / B1 起始日
        Row row0 = sheet.getRow(0);
        if (row0 != null) {
            Cell cellB1 = row0.getCell(1);
            if (cellB1 != null) {
                ruleResult.setStartWearingDate(getCellValueAsString(cellB1));
            }
        }

        // 读取 A2 / B2 / C2 检测规则（暂时统一为 1 自定义模式）
        int excelWearType = 1;

        // 从第5行（row index 4）开始循环读取规则明细
        List<DetectionTask> taskList = new ArrayList<DetectionTask>();
        int lastRow = sheet.getLastRowNum();
        boolean isCustomPeriodInExcel = false;
        int customPeriodNumInExcel = 0;
        int customPeriodUnitInExcel = 0;

        for (int r = 4; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            Cell cellC = row.getCell(2);
            String unitStr = getCellValueAsString(cellC);
            // 兼容所有以"检测周期"或"总天数"开头的总结行，彻底杜绝误读多出一行
            if (unitStr.startsWith("检测周期") || unitStr.startsWith("总天数")) {
                if (unitStr.contains("自定义")) {
                    isCustomPeriodInExcel = true;
                    Cell cellD = row.getCell(3);
                    String periodValStr = getCellValueAsString(cellD); // 如 "2 个月" 或 "2 年"
                    if (!periodValStr.isEmpty()) {
                        String[] parts = periodValStr.trim().split("\\s+");
                        if (parts.length >= 2) {
                            try {
                                customPeriodNumInExcel = Integer.parseInt(parts[0].trim());
                                customPeriodUnitInExcel = DetectionTask.parseUnitByName(parts[1].trim());
                            } catch (Exception ignored) {}
                        }
                    }
                }
                break;
            }

            Cell cellA = row.getCell(0);
            Cell cellB = row.getCell(1);
            Cell cellD = row.getCell(3);

            String idxStr = getCellValueAsString(cellA);
            String cntStr = getCellValueAsString(cellB);
            String checkStr = getCellValueAsString(cellD);

            if (idxStr.isEmpty() && cntStr.isEmpty() && unitStr.isEmpty()) {
                continue;
            }

            int detectionFrequencyNum = parseIntegerOrDefault(cntStr, 1);
            int detectionFrequencyUnit = DetectionTask.parseUnitByName(unitStr);
            int rawCheckCount = parseIntegerOrDefault(checkStr, 1);

            int maxLimit = 365 * detectionFrequencyNum;
            if (detectionFrequencyUnit == 0) maxLimit = 1 * detectionFrequencyNum;
            else if (detectionFrequencyUnit == 1) maxLimit = 7 * detectionFrequencyNum;
            else if (detectionFrequencyUnit == 2) maxLimit = 31 * detectionFrequencyNum;
            
            int detectionFrequencyCount = Math.max(1, Math.min(maxLimit, rawCheckCount)); // 防御性校验

            DetectionTask task = new DetectionTask(detectionFrequencyUnit, detectionFrequencyNum, detectionFrequencyCount);

            // 设置 taskNum 和 taskName
            int taskIndex = taskList.size() + 1;
            task.setTaskNum(taskIndex);
            task.setTaskName("第" + convertIndexToChinese(taskIndex) + "周期");

            // 如果 Excel 中包含计算好的起始与结束时间，也尝试读取
            Cell cellF = row.getCell(5);
            Cell cellG = row.getCell(6);
            if (cellF != null) task.setStartDate(getCellValueAsString(cellF));
            if (cellG != null) task.setEndDate(getCellValueAsString(cellG));

            taskList.add(task);
        }

        ruleResult.setDetectionTaskList(taskList);
        if (isCustomPeriodInExcel) {
            ruleResult.setUseCustomDetectionPeriod(true);
            ruleResult.setDetectionPeriodNum(customPeriodNumInExcel);
            ruleResult.setDetectionPeriodUnit(customPeriodUnitInExcel);
        } else {
            ruleResult.setUseCustomDetectionPeriod(false);
            ruleResult.recalculateDetectionPeriod();
        }

        // 如果起始日有效，重新触发精准校验推算
        String startDateStr = ruleResult.getStartWearingDate();
        if (startDateStr != null && !startDateStr.trim().isEmpty()) {
            DateCalculator.calculatePeriods(ruleResult, startDateStr);
        } else {
            ruleResult.setStartWearingDate(null);
            for (DetectionTask task : taskList) {
                task.setStartDate(null);
                task.setEndDate(null);
            }
        }
        ruleResult.setWearType(excelWearType);

        return ruleResult;
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().format(DateCalculator.DISPLAY_FORMATTER);
            } else {
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            }
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.FORMULA) {
            try {
                return cell.getStringCellValue();
            } catch (Exception e) {
                return String.valueOf(cell.getNumericCellValue());
            }
        }
        return "";
    }

    private static int parseIntegerOrDefault(String str, int defaultVal) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private static String convertIndexToChinese(int num) {
        if (num < 1 || num > 99) return String.valueOf(num);
        String[] digits = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (num <= 9) return digits[num];
        if (num == 10) return "十";
        int tens = num / 10;
        int ones = num % 10;
        String res = (tens == 1) ? "十" : digits[tens] + "十";
        if (ones > 0) res += digits[ones];
        return res;
    }
}
