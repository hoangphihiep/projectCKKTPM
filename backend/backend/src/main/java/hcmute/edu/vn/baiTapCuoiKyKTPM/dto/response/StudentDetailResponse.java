package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.ExamParticipation;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.ExamStatus;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfo;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfoHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDetailResponse {
    private String id;
    private String studentId;
    private StudentInfo currentInfo;
    private List<StudentInfoHistory> infoHistory;
    private List<ExamParticipation> examParticipations;
    private ExamStatus status;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
