package com.calendar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 随访周期整体佩戴规则模型
 * 对应设计文档中的整体设计表
 * @version 1.0
 */
public class WearingCalendarRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 佩戴模式：0:固定日期, 1:窗口期任意日
     * 来源：医生输入
     */
    private int WearMode;

    /**
     * 佩戴类型：0:固定模式（每个随访周期规则完全一致），1:自定义模式（存在不一致）
     * 来源：建立课题时输入，系统也可自动计算
     */
    private int WearType;

    /**
     * 手术日期（提醒医生作用，与实际运算关系不大）
     * 来源：医生输入
     */
    private String surgeryDate;

    /**
     * 佩戴起始日（参与整个运算）
     * 来源：医生输入
     */
    private String startWearingDate;

    /**
     * 建议佩戴日（提醒医生作用，与实际运算关系不大）
     * 来源：医生输入
     */
    private String suggestWearingDate;

    /**
     * 检测周期：数值（由 detectionTaskList 算出）
     * 转化规则：总天数能整除365→年，能整除30→月，能整除7→周，其余→天
     */
    private int detectionPeriodNum;

    /**
     * 检测周期单位：0:天, 1:周, 2:月, 3:年
     */
    private int detectionPeriodUnit;

    /**
     * 是否使用自定义设置的检测周期（不使用程序自动计算的天数）
     */
    private boolean useCustomDetectionPeriod = false;

    /**
     * 检测任务列表（每个元素代表一个随访周期）
     */
    private List<DetectionTask> detectionTaskList = new ArrayList<>();


    public WearingCalendarRule() {
    }

    // ========== Getters & Setters ==========

    public int getWearMode() {
        return WearMode;
    }

    public void setWearMode(int wearMode) {
        this.WearMode = wearMode;
    }

    public int getWearType() {
        return WearType;
    }

    public void setWearType(int wearType) {
        this.WearType = wearType;
    }

    public String getSurgeryDate() {
        return surgeryDate;
    }

    public void setSurgeryDate(String surgeryDate) {
        this.surgeryDate = surgeryDate;
    }

    public String getStartWearingDate() {
        return startWearingDate;
    }

    public void setStartWearingDate(String startWearingDate) {
        this.startWearingDate = startWearingDate;
    }

    public String getSuggestWearingDate() {
        return suggestWearingDate;
    }

    public void setSuggestWearingDate(String suggestWearingDate) {
        this.suggestWearingDate = suggestWearingDate;
    }

    public int getDetectionPeriodNum() {
        return detectionPeriodNum;
    }

    public void setDetectionPeriodNum(int detectionPeriodNum) {
        this.detectionPeriodNum = detectionPeriodNum;
    }

    public int getDetectionPeriodUnit() {
        return detectionPeriodUnit;
    }

    public void setDetectionPeriodUnit(int detectionPeriodUnit) {
        this.detectionPeriodUnit = detectionPeriodUnit;
    }

    public List<DetectionTask> getDetectionTaskList() {
        return detectionTaskList;
    }

    public void setDetectionTaskList(List<DetectionTask> detectionTaskList) {
        this.detectionTaskList = detectionTaskList;
        recalculateWearType();
        recalculateDetectionPeriod();
    }

    public void addDetectionTask(DetectionTask task) {
        if (this.detectionTaskList == null) {
            this.detectionTaskList = new ArrayList<>();
        }
        this.detectionTaskList.add(task);
        recalculateWearType();
        recalculateDetectionPeriod();
    }

    public boolean isUseCustomDetectionPeriod() {
        return useCustomDetectionPeriod;
    }

    public void setUseCustomDetectionPeriod(boolean useCustomDetectionPeriod) {
        this.useCustomDetectionPeriod = useCustomDetectionPeriod;
    }

    public void recalculateDetectionPeriod() {
        if (useCustomDetectionPeriod) {
            return;
        }
        if (detectionTaskList == null || detectionTaskList.isEmpty()) {
            this.detectionPeriodNum = 0;
            this.detectionPeriodUnit = 0;
            return;
        }
        int[] periodInfo = com.calendar.service.DateCalculator.calculateDetectionPeriodFromTasks(detectionTaskList);
        this.detectionPeriodNum = periodInfo[0];
        this.detectionPeriodUnit = periodInfo[1];
    }

    /**
     * 获取检测任务列表大小（随访周期个数）
     */
    public int getTaskCount() {
        return detectionTaskList != null ? detectionTaskList.size() : 0;
    }

    /**
     * 获取检测周期单位名称
     */
    public String getDetectionPeriodUnitName() {
        switch (detectionPeriodUnit) {
            case 0: return "天";
            case 1: return "周";
            case 2: return "个月";
            case 3: return "年";
            default: return "天";
        }
    }

    /**
     * 获取检测周期显示文案，如 "1 年"、"6 个月"、"14 周"
     */
    public String getDetectionPeriodDisplay() {
        return detectionPeriodNum + " " + getDetectionPeriodUnitName();
    }

    /**
     * 自动计算佩戴类型 WearType：
     * 如果所有规则的 detectionFrequencyUnit、detectionFrequencyNum、detectionFrequencyCount
     * 完全相同则为 0（固定），否则为 1（自定义）
     */
    public void recalculateWearType() {
        if (detectionTaskList == null || detectionTaskList.isEmpty()) {
            this.WearType = 0;
            return;
        }
        DetectionTask first = detectionTaskList.get(0);
        for (int i = 1; i < detectionTaskList.size(); i++) {
            DetectionTask cur = detectionTaskList.get(i);
            if (cur.getDetectionFrequencyUnit() != first.getDetectionFrequencyUnit() ||
                cur.getDetectionFrequencyNum() != first.getDetectionFrequencyNum() ||
                cur.getDetectionFrequencyCount() != first.getDetectionFrequencyCount()) {
                this.WearType = 1; // 自定义
                return;
            }
        }
        this.WearType = 0; // 固定
    }

    /**
     * 将整体规则对象转为格式化的 JSON 字符串（不依赖第三方库）
     */
    public String toJsonString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"WearMode\": ").append(WearMode).append(",\n");
        sb.append("  \"WearType\": ").append(WearType).append(",\n");
        sb.append("  \"surgeryDate\": ").append(jsonStr(surgeryDate)).append(",\n");
        sb.append("  \"startWearingDate\": ").append(jsonStr(startWearingDate)).append(",\n");
        sb.append("  \"suggestWearingDate\": ").append(jsonStr(suggestWearingDate)).append(",\n");
        sb.append("  \"detectionPeriodNum\": ").append(detectionPeriodNum).append(",\n");
        sb.append("  \"detectionPeriodUnit\": ").append(detectionPeriodUnit).append(",\n");
        sb.append("  \"useCustomDetectionPeriod\": ").append(useCustomDetectionPeriod).append(",\n");
        sb.append("  \"detectionTaskList\": ");
        if (detectionTaskList == null || detectionTaskList.isEmpty()) {
            sb.append("[]\n");
        } else {
            sb.append("[\n");
            for (int i = 0; i < detectionTaskList.size(); i++) {
                sb.append(detectionTaskList.get(i).toJsonString("    "));
                if (i < detectionTaskList.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ]\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String jsonStr(String val) {
        return val == null ? "null" : "\"" + val + "\"";
    }
}
