package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamRoomResponse {
    private String shift;
    private List<String> rooms;
}
