package tech.zetapioneers.loan_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminStats {
    private long totalApplications;
    private double approvalRate;
    private long pending;
    private double avgIncome;
    private List<Map<String,Long>> monthlyTrends;
    private Map<String,Long> statusDistribution;
}
