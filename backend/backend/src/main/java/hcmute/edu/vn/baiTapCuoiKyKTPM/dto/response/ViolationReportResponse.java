package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Invigilator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationReportResponse {
    private String studentId;
    private String studentName;
    private String studentClass;
    private LocalDate examDate;
    private String area;
    private String room;
    private String shift;
    private boolean hasViolation;
    private String violationLevel;
    private String description;
    private List<Invigilator> invigilators;
    private String notes;
    private String currentExamStatus; // active, suspended, expelled
    private String statusReason;
}
