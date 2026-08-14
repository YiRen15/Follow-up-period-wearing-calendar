package com.calendar.service;

import com.calendar.model.DetectionTask;
import com.calendar.model.WearingCalendarRule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 日期与随访周期推算引擎 (基于 Java 8 java.time API)
 * @version 1.0.1
 */
public class DateCalculator {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 解析各种常见格式的日期字符串为 LocalDate
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return LocalDate.now();
        }
        String cleanStr = dateStr.trim();
        try {
            if (cleanStr.contains("年")) {
                // 处理 "2026年7月27日" 或 "2026年07月27日"
                cleanStr = cleanStr.replace("日", "").replace("号", "");
                String[] parts = cleanStr.split("[年月]");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    int day = Integer.parseInt(parts[2].trim());
                    return LocalDate.of(year, month, day);
                }
            } else if (cleanStr.contains("-")) {
                String[] parts = cleanStr.split("-");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    int day = Integer.parseInt(parts[2].trim());
                    return LocalDate.of(year, month, day);
                }
            } else if (cleanStr.contains("/")) {
                String[] parts = cleanStr.split("/");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    int day = Integer.parseInt(parts[2].trim());
                    return LocalDate.of(year, month, day);
                }
            }
        } catch (Exception e) {
            System.err.println("日期解析异常: " + dateStr + ", 错误信息: " + e.getMessage());
        }
        return LocalDate.now();
    }

    /**
     * 计算单条规则在指定开始日期下的结束日期
     */
    public static LocalDate calculateEndDate(LocalDate startDate, DetectionTask task) {
        if (startDate == null || task == null) return startDate;
        int unit = task.getDetectionFrequencyUnit();
        int cnt = Math.max(1, task.getDetectionFrequencyNum());

        LocalDate endDate;
        switch (unit) {
            case 0: // 天
                endDate = startDate.plusDays(cnt - 1);
                break;
            case 1: // 周
                endDate = startDate.plusDays(cnt * 7L - 1);
                break;
            case 2: // 月
                endDate = startDate.plusMonths(cnt).minusDays(1);
                break;
            case 3: // 年
                endDate = startDate.plusYears(cnt).minusDays(1);
                break;
            default:
                endDate = startDate.plusDays(cnt - 1);
                break;
        }
        return endDate;
    }

    /**
     * 根据起始日期，批量推算所有随访周期的起始时间、结束时间，
     * 并计算 detectionPeriodNum 和 detectionPeriodUnit
     */
    public static void calculatePeriods(WearingCalendarRule calendarRule, String startDateStr) {
        if (calendarRule == null || calendarRule.getDetectionTaskList() == null
                || calendarRule.getDetectionTaskList().isEmpty()) {
            if (calendarRule != null) {
                calendarRule.setDetectionPeriodNum(0);
                calendarRule.setDetectionPeriodUnit(0);
            }
            return;
        }

        LocalDate curStart = parseLocalDate(startDateStr);
        calendarRule.setStartWearingDate(curStart.format(DISPLAY_FORMATTER));

        LocalDate firstStart = curStart;
        LocalDate lastEnd = curStart;

        int anchorDay = firstStart.getDayOfMonth();

        List<DetectionTask> taskList = calendarRule.getDetectionTaskList();
        for (int i = 0; i < taskList.size(); i++) {
            DetectionTask task = taskList.get(i);
            int unit = task.getDetectionFrequencyUnit();
            int cnt = Math.max(1, task.getDetectionFrequencyNum());
            LocalDate curEnd;
            LocalDate nextStart;

            switch (unit) {
                case 2: // 月（真实自然满月算法，避免被首日锚定截断）
                    curEnd = curStart.plusMonths(cnt).minusDays(1);
                    nextStart = curEnd.plusDays(1);
                    break;
                case 3: // 年
                    curEnd = curStart.plusYears(cnt).minusDays(1);
                    nextStart = curEnd.plusDays(1);
                    break;
                default: // 天(0), 周(1)
                    curEnd = calculateEndDate(curStart, task);
                    nextStart = curEnd.plusDays(1);
                    break;
            }

            task.setStartDate(curStart.format(DISPLAY_FORMATTER));
            task.setEndDate(curEnd.format(DISPLAY_FORMATTER));

            // 自动设置 taskNum 和 taskName
            task.setTaskNum(i + 1);
            if (task.getTaskName() == null || task.getTaskName().isEmpty()) {
                task.setTaskName("第" + convertNumberToChinese(i + 1) + "周期");
            }

            lastEnd = curEnd;
            curStart = nextStart;
        }

        // 计算总天数
        long totalDays = ChronoUnit.DAYS.between(firstStart, lastEnd) + 1;
        int tDays = (int) totalDays;

        if (!calendarRule.isUseCustomDetectionPeriod()) {
            int[] periodInfo = calculateDetectionPeriodFromTasks(taskList);
            calendarRule.setDetectionPeriodNum(periodInfo[0]);
            calendarRule.setDetectionPeriodUnit(periodInfo[1]);
        }

        calendarRule.recalculateWearType();
    }

    /**
     * 根据规则列表计算 detectionPeriodNum 和 detectionPeriodUnit（以当天为起始日）
     * 返回 int[2]：[0]=detectionPeriodNum, [1]=detectionPeriodUnit
     */
    public static int[] calculateDetectionPeriodFromTasks(List<DetectionTask> tasks) {
        if (tasks == null || tasks.isEmpty()) return new int[]{0, 0};

        int firstUnit = tasks.get(0).getDetectionFrequencyUnit();
        boolean sameUnit = true;
        int totalUnits = 0;

        for (DetectionTask task : tasks) {
            int unit = task.getDetectionFrequencyUnit();
            int num = Math.max(1, task.getDetectionFrequencyNum());
            if (unit != firstUnit) {
                sameUnit = false;
            }
            totalUnits += num;
        }

        // 如果全部是同一种单位，直接同单位精准累加
        if (sameUnit) {
            switch (firstUnit) {
                case 3: // 年
                    return new int[]{totalUnits, 3};
                case 2: // 月
                    if (totalUnits > 0 && totalUnits % 12 == 0) {
                        return new int[]{totalUnits / 12, 3}; // 自动转换为年
                    }
                    return new int[]{totalUnits, 2};
                case 1: // 周
                    if (totalUnits > 0 && totalUnits % 52 == 0) {
                        return new int[]{totalUnits / 52, 3}; // 自动转换为年
                    }
                    return new int[]{totalUnits, 1};
                default: // 天
                    if (totalUnits > 0 && totalUnits % 365 == 0) {
                        return new int[]{totalUnits / 365, 3};
                    } else if (totalUnits > 0 && totalUnits % 30 == 0) {
                        return new int[]{totalUnits / 30, 2};
                    } else if (totalUnits > 0 && totalUnits % 7 == 0) {
                        return new int[]{totalUnits / 7, 1};
                    }
                    return new int[]{totalUnits, 0};
            }
        }

        // 如果为混合单位，按物理天数估算
        LocalDate curStart = LocalDate.now();
        LocalDate firstStart = curStart;
        for (DetectionTask task : tasks) {
            int unit = task.getDetectionFrequencyUnit();
            int num = Math.max(1, task.getDetectionFrequencyNum());
            if (unit == 2) {
                curStart = curStart.plusMonths(num);
            } else if (unit == 3) {
                curStart = curStart.plusYears(num);
            } else {
                LocalDate curEnd = calculateEndDate(curStart, task);
                curStart = curEnd.plusDays(1);
            }
        }
        LocalDate lastEnd = curStart.minusDays(1);
        int tDays = (int) (ChronoUnit.DAYS.between(firstStart, lastEnd) + 1);

        if (tDays > 0 && tDays % 365 == 0) {
            return new int[]{tDays / 365, 3}; // 年
        } else if (tDays > 0 && tDays % 30 == 0) {
            return new int[]{tDays / 30, 2}; // 月
        } else if (tDays > 0 && tDays % 7 == 0) {
            return new int[]{tDays / 7, 1}; // 周
        } else {
            return new int[]{tDays, 0}; // 天
        }
    }

    /**
     * 获取检测周期单位名称
     */
    public static String getPeriodUnitName(int unitCode) {
        switch (unitCode) {
            case 0: return "天";
            case 1: return "周";
            case 2: return "个月";
            case 3: return "年";
            default: return "天";
        }
    }

    /**
     * 计算所有任务在日历上的真实物理总天数（精准包含跨越的大月、小月及闰年 2月29日 多出的天数）
     *
     * @param tasks        任务列表
     * @param startDateStr 起始日期字符串（若为空，默认从今天 LocalDate.now() 算起）
     * @return 实际物理总天数
     */
    public static int calculateTotalActualDays(List<DetectionTask> tasks, String startDateStr) {
        if (tasks == null || tasks.isEmpty()) return 0;
        LocalDate curStart = parseLocalDate(startDateStr);
        LocalDate firstStart = curStart;
        for (DetectionTask task : tasks) {
            int unit = task.getDetectionFrequencyUnit();
            int num = Math.max(1, task.getDetectionFrequencyNum());
            switch (unit) {
                case 3: // 年
                    curStart = curStart.plusYears(num);
                    break;
                case 2: // 月
                    curStart = curStart.plusMonths(num);
                    break;
                case 1: // 周
                    curStart = curStart.plusDays(num * 7L);
                    break;
                default: // 天
                    curStart = curStart.plusDays(num);
                    break;
            }
        }
        LocalDate lastEnd = curStart.minusDays(1);
        return (int) (ChronoUnit.DAYS.between(firstStart, lastEnd) + 1);
    }

    private static String convertNumberToChinese(int num) {
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
