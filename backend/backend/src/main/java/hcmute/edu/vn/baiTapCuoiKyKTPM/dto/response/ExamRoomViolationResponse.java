package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRoomViolationResponse {
    private LocalDate examDate;
    private String area;
    private String room;
    private String shift;
    private int totalStudents;
    private int violationCount;
    private List<ViolationReportResponse> violations;
}
