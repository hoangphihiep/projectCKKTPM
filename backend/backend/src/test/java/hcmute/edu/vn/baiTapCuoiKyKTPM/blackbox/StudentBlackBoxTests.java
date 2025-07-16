package hcmute.edu.vn.baiTapCuoiKyKTPM.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentDetailResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử hộp đen - Kiểm tra chức năng mà không quan tâm cấu trúc nội bộ
 * Sử dụng các kỹ thuật: Phân vùng tương đương, Phân tích giá trị biên, Bảng quyết định, Chuyển đổi trạng thái
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentBlackBoxTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== PHÂN VÙNG TƯƠNG ĐƯƠNG (Equivalence Partitioning) =====
    @Test
    void testExamDate_EquivalencePartitioning() throws Exception {
        // Phân vùng cho ngày thi

        // Partition 1: Ngày hợp lệ trong tương lai
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "2025-06-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        // Partition 2: Ngày hợp lệ trong quá khứ
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "2020-06-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        // Partition 3: Ngày không hợp lệ
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Partition 4: Định dạng ngày sai
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "25/06/2025")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ===== PHÂN TÍCH GIÁ TRỊ BIÊN (Boundary Value Analysis) =====

    @Test
    void testStudentId_BoundaryValues() throws Exception {
        // Giá trị biên cho Student ID

        // Boundary 1: MSSV ngắn nhất có thể
        mockMvc.perform(get("/api/students/detail/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Boundary 2: MSSV dài bình thường (8 chữ số)
        mockMvc.perform(get("/api/students/detail/20110001")
                .contentType(MediaType.APPLICATION_JSON));
        // Không check status vì có thể có hoặc không có data

        // Boundary 3: MSSV rất dài
        mockMvc.perform(get("/api/students/detail/999999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    // ===== BẢNG QUYẾT ĐỊNH (Decision Table) =====

    @Test
    void testExamRoomQuery_DecisionTable() throws Exception {
        // Bảng quyết định cho query phòng thi
        // Conditions: examDate (T/F), area (T/F), shift (T/F), room (T/F)
        // Actions: Success (200), BadRequest (400)

        String validDate = "2025-06-25";
        String validArea = "A2";
        String validShift = "Ca 1";
        String validRoom = "P.201";

        // Rule 1: T T T T -> Success
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", validDate)
                        .param("area", validArea)
                        .param("shift", validShift)
                        .param("room", validRoom)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Rule 2: F T T T -> BadRequest
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", "invalid-date")
                        .param("area", validArea)
                        .param("shift", validShift)
                        .param("room", validRoom)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Rule 3: T F T T -> BadRequest (missing area)
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", validDate)
                        .param("shift", validShift)
                        .param("room", validRoom)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Rule 4: T T F T -> BadRequest (missing shift)
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", validDate)
                        .param("area", validArea)
                        .param("room", validRoom)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Rule 5: T T T F -> BadRequest (missing room)
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", validDate)
                        .param("area", validArea)
                        .param("shift", validShift)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // ===== CHUYỂN ĐỔI TRẠNG THÁI (State Transition) =====
    @Test
    void testExamRoomNavigationTransition() throws Exception {
        // Kiểm tra chuyển đổi trạng thái khi duyệt phòng thi

        String examDate = "2025-06-25";

        // State 1: Date Selected - Lấy khu vực
        MvcResult areasResult = mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", examDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String areasContent = areasResult.getResponse().getContentAsString();
        String[] areas = objectMapper.readValue(areasContent, String[].class);

        if (areas.length > 0) {
            String selectedArea = areas[0];

            // State 2: Area Selected - Lấy ca thi
            MvcResult shiftsResult = mockMvc.perform(get("/api/students/exam-shifts")
                            .param("examDate", examDate)
                            .param("area", selectedArea)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String shiftsContent = shiftsResult.getResponse().getContentAsString();
            String[] shifts = objectMapper.readValue(shiftsContent, String[].class);

            if (shifts.length > 0) {
                String selectedShift = shifts[0];

                // State 3: Shift Selected - Lấy phòng thi
                MvcResult roomsResult = mockMvc.perform(get("/api/students/exam-rooms")
                                .param("examDate", examDate)
                                .param("area", selectedArea)
                                .param("shift", selectedShift)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

                String roomsContent = roomsResult.getResponse().getContentAsString();
                String[] rooms = objectMapper.readValue(roomsContent, String[].class);

                if (rooms.length > 0) {
                    String selectedRoom = rooms[0];

                    // State 4: Room Selected - Lấy danh sách sinh viên
                    mockMvc.perform(get("/api/students/exam-room-students")
                                    .param("examDate", examDate)
                                    .param("area", selectedArea)
                                    .param("shift", selectedShift)
                                    .param("room", selectedRoom)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk())
                            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                            .andExpect(jsonPath("$").isArray());
                }
            }
        }
    }
}
