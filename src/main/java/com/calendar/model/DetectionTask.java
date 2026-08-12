package com.calendar.model;

import java.io.Serializable;

/**
 * 检测任务结构体（单个随访周期）
 * 对应设计文档中 detectionTaskList 的每一项
 * @version 1.0.1
 */
public class DetectionTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 周期名称，如 "第一周期"
     */
    private String taskName;

    /**
     * 周期开始日期（由 JAR 包根据 startWearingDate 推算得出）
     * Excel 中的起始时间仅为例子显示
     */
    private String startDate;

    /**
     * 周期结束日期（由 JAR 包根据规则推算得出）
     * Excel 中的结束时间仅为例子显示
     */
    private String endDate;

    /**
     * 任务编号排序
     */
    private int taskNum;

    /**
     * 佩戴日（暂定，先给 NULL）
     */
    private String wearingDate;

    /**
     * 超期天数（先默认 0）
     */
    private int overdueDays;

    /**
     * 任务状态：-1 超期未佩戴，0 未佩戴，1 已佩戴（先默认 1）
     */
    private int status = 1;

    /**
     * 检测频率值（周期数值，如 "2周" 中的 2）
     */
    private int detectionFrequencyNum;

    /**
     * 检测频率单位：0:天, 1:周, 2:月, 3:年
     */
    private int detectionFrequencyUnit;

    /**
     * 检测频率次数（封顶 10 次）
     */
    private int detectionFrequencyCount;

    /**
     * 有效检测时长（默认 6）
     */
    private int validDetectionDuration = 6;

    /**
     * 有效检测时长单位：0:分钟, 1:小时（默认 1，代表小时）
     */
    private int validDetectionDurationUnit = 1;

    public DetectionTask() {
    }

    public DetectionTask(int detectionFrequencyUnit, int detectionFrequencyNum, int detectionFrequencyCount) {
        this.detectionFrequencyUnit = detectionFrequencyUnit;
        this.detectionFrequencyNum = detectionFrequencyNum;
        this.detectionFrequencyCount = detectionFrequencyCount;
    }

    public DetectionTask(int detectionFrequencyUnit, int detectionFrequencyNum, int detectionFrequencyCount,
                         String startDate, String endDate) {
        this.detectionFrequencyUnit = detectionFrequencyUnit;
        this.detectionFrequencyNum = detectionFrequencyNum;
        this.detectionFrequencyCount = detectionFrequencyCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // ========== Getters & Setters ==========

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getWearingDate() {
        return wearingDate;
    }

    public void setWearingDate(String wearingDate) {
        this.wearingDate = wearingDate;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDetectionFrequencyNum() {
        return detectionFrequencyNum;
    }

    public void setDetectionFrequencyNum(int detectionFrequencyNum) {
        this.detectionFrequencyNum = detectionFrequencyNum;
    }

    public int getDetectionFrequencyUnit() {
        return detectionFrequencyUnit;
    }

    public void setDetectionFrequencyUnit(int detectionFrequencyUnit) {
        this.detectionFrequencyUnit = detectionFrequencyUnit;
    }

    public int getDetectionFrequencyCount() {
        return detectionFrequencyCount;
    }

    public void setDetectionFrequencyCount(int detectionFrequencyCount) {
        this.detectionFrequencyCount = detectionFrequencyCount;
    }

    public int getValidDetectionDuration() {
        return validDetectionDuration;
    }

    public void setValidDetectionDuration(int validDetectionDuration) {
        this.validDetectionDuration = validDetectionDuration;
    }

    public int getValidDetectionDurationUnit() {
        return validDetectionDurationUnit;
    }

    public void setValidDetectionDurationUnit(int validDetectionDurationUnit) {
        this.validDetectionDurationUnit = validDetectionDurationUnit;
    }

    /**
     * 获取单位名称描述
     */
    public String getUnitName() {
        switch (detectionFrequencyUnit) {
            case 0:
                return "天";
            case 1:
                return "周";
            case 2:
                return "月";
            case 3:
                return "年";
            default:
                return "其他";
        }
    }

    /**
     * 根据单位名称解析单位枚举值
     */
    public static int parseUnitByName(String unitName) {
        if (unitName == null) return 1;
        String name = unitName.trim();
        if ("天".equals(name) || "日".equals(name)) return 0;
        if ("周".equals(name) || "星期".equals(name)) return 1;
        if ("月".equals(name)) return 2;
        if ("年".equals(name)) return 3;
        return 0; // 默认天
    }

    /**
     * 获取可读格式描述，如"一周1次"、"一月2次"
     */
    public String getDisplayText() {
        String numStr = convertNumberToChinese(detectionFrequencyNum);
        String unitStr = getUnitName();
        return numStr + unitStr + " " + detectionFrequencyCount + " 次";
    }

    private String convertNumberToChinese(int num) {
        if (num < 1 || num > 99) return String.valueOf(num);
        if (num == 2) return "两";
        String[] digits = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        if (num <= 9) return digits[num];
        if (num == 10) return "十";

        int tens = num / 10;
        int ones = num % 10;

        String res = "";
        if (tens == 1) {
            res = "十";
        } else {
            res = digits[tens] + "十";
        }

        if (ones > 0) {
            res += digits[ones];
        }
        return res;
    }

    @Override
    public String toString() {
        return "DetectionTask{" +
                "taskName='" + taskName + '\'' +
                ", taskNum=" + taskNum +
                ", detectionFrequencyUnit=" + detectionFrequencyUnit +
                ", detectionFrequencyNum=" + detectionFrequencyNum +
                ", detectionFrequencyCount=" + detectionFrequencyCount +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * 将当前对象转为格式化的 JSON 字符串（不依赖第三方库）
     */
    public String toJsonString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("{\n");
        String inner = indent + "  ";
        sb.append(inner).append("\"taskName\": ").append(jsonStr(taskName)).append(",\n");
        sb.append(inner).append("\"startDate\": ").append(jsonStr(startDate)).append(",\n");
        sb.append(inner).append("\"endDate\": ").append(jsonStr(endDate)).append(",\n");
        sb.append(inner).append("\"taskNum\": ").append(taskNum).append(",\n");
        sb.append(inner).append("\"wearingDate\": ").append(jsonStr(wearingDate)).append(",\n");
        sb.append(inner).append("\"overdueDays\": ").append(overdueDays).append(",\n");
        sb.append(inner).append("\"status\": ").append(status).append(",\n");
        sb.append(inner).append("\"detectionFrequencyNum\": ").append(detectionFrequencyNum).append(",\n");
        sb.append(inner).append("\"detectionFrequencyUnit\": ").append(detectionFrequencyUnit).append(",\n");
        sb.append(inner).append("\"detectionFrequencyCount\": ").append(detectionFrequencyCount).append(",\n");
        sb.append(inner).append("\"validDetectionDuration\": ").append(validDetectionDuration).append(",\n");
        sb.append(inner).append("\"validDetectionDurationUnit\": ").append(validDetectionDurationUnit).append("\n");
        sb.append(indent).append("}");
        return sb.toString();
    }

    private static String jsonStr(String val) {
        return val == null ? "null" : "\"" + val + "\"";
    }
}

