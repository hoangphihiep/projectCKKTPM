package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamScheduleResponse {
    private LocalDate examDate;
    private List<String> areas;
}
