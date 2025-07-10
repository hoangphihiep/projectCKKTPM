package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationStatisticsResponse {
    private int totalViolations;
    private Map<String, Integer> violationsByLevel;
    private Map<String, Integer> violationsByArea;
    private Map<String, Integer> violationsByRoom;
    private Map<String, Integer> studentsByStatus;
    private int activeStudents;
    private int suspendedStudents;
    private int expelledStudents;
}
