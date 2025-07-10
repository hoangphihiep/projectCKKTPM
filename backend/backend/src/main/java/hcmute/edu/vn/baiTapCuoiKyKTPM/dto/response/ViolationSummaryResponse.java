package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationSummaryResponse {
    private String studentId;
    private String studentName;
    private String studentClass;
    private String currentExamStatus;
    private int totalViolations;
    private int khienTrachCount;
    private int canhCaoCount;
    private int dinhChiCount;
    private int duoiHocCount;
    private String latestViolationDate;
    private String latestViolationLevel;
    private String statusReason;
}
