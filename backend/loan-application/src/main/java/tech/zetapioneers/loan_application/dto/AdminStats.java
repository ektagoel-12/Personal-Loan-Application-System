package tech.zetapioneers.loan_application.DTO;

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
    private double growth;
    private double approvalRate;
    private double approvalGrowth;
    private long pending;
    private double avgIncome;
    private List<Map<String,Object>> monthlyTrends;
    private List<Map<String,Object>> statusDistribution;
}
