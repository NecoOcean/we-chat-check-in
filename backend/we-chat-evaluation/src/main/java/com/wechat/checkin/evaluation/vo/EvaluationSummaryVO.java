package com.wechat.checkin.evaluation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评价统计汇总VO
 * 用于展示评价的统计数据（饼图等可视化）
 * 
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationSummaryVO {

    /**
     * 总评价数
     */
    private Integer totalEvaluations;

    /**
     * 满意度统计
     */
    private SatisfactionStats q1Satisfaction;

    /**
     * 实用性统计
     */
    private PracticalityStats q2Practicality;

    /**
     * 质量统计
     */
    private QualityStats q3Quality;

    /**
     * 满意度统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SatisfactionStats {
        /**
         * 不满意数量（1）
         */
        private Integer unsatisfied;
        
        /**
         * 一般数量（2）
         */
        private Integer neutral;
        
        /**
         * 满意数量（3）
         */
        private Integer satisfied;
    }

    /**
     * 实用性统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PracticalityStats {
        /**
         * 弱数量（1）
         */
        private Integer weak;
        
        /**
         * 中数量（2）
         */
        private Integer medium;
        
        /**
         * 强数量（3）
         */
        private Integer strong;
    }

    /**
     * 质量统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityStats {
        /**
         * 差数量（1）
         */
        private Integer poor;
        
        /**
         * 中数量（2）
         */
        private Integer medium;
        
        /**
         * 好数量（3）
         */
        private Integer good;
    }
}
