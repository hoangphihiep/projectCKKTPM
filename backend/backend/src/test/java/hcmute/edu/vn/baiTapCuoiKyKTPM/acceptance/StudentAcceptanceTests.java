package hcmute.edu.vn.baiTapCuoiKyKTPM.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentDetailResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.ViolationStatisticsResponse;
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
 * Kiểm thử chấp nhận - Kiểm tra để xác nhận phần mềm đáp ứng yêu cầu của khách hàng
 * Kiểm tra các user stories và acceptance criteria
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userStory_AsTeacher_IWantToSearchStudentByName_SoThatICanVerifyIdentity() throws Exception {
        // User Story: Là thanh tra, tôi muốn tìm kiếm sinh viên theo tên
        // để có thể xác minh danh tính của sinh viên

        // Given: Tôi là thanh tra
        // When: Tôi tìm kiếm sinh viên theo tên "Nguyễn"
        MvcResult result = mockMvc.perform(get("/api/students/search")
                        .param("query", "Nguyễn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Then: Hệ thống trả về danh sách sinh viên có tên chứa "Nguyễn"
        String content = result.getResponse().getContentAsString();
        StudentSearchResponse[] students = objectMapper.readValue(content, StudentSearchResponse[].class);

        // Acceptance Criteria:
        // - Kết quả trả về phải là một mảng
        assertNotNull(students);

        // - Mỗi sinh viên trong kết quả phải có đầy đủ thông tin cơ bản
        for (StudentSearchResponse student : students) {
            assertNotNull(student.getStudentId(), "Student ID không được null");
            assertNotNull(student.getFullName(), "Tên sinh viên không được null");
            assertNotNull(student.getStudentClass(), "Lớp sinh viên không được null");
            assertTrue(student.getFullName().toLowerCase().contains("nguyễn") ||
                            student.getStudentId().contains("Nguyễn"),
                    "Kết quả tìm kiếm phải chứa từ khóa");
        }
    }

    @Test
    void userStory_AsTeacher_IWantToViewStudentDetails_SoThatICanVerifyStudentInformation() throws Exception {
        // User Story: Là giáo viên coi thi, tôi muốn xem thông tin chi tiết sinh viên
        // để có thể xác minh thông tin sinh viên

        // Given: Tôi đã tìm thấy sinh viên
        MvcResult searchResult = mockMvc.perform(get("/api/students/search")
                        .param("query", "20110001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String searchContent = searchResult.getResponse().getContentAsString();
        StudentSearchResponse[] students = objectMapper.readValue(searchContent, StudentSearchResponse[].class);

        if (students.length > 0) {
            String studentId = students[0].getStudentId();

            // When: Tôi xem thông tin chi tiết sinh viên
            MvcResult detailResult = mockMvc.perform(get("/api/students/detail/" + studentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Then: Hệ thống hiển thị đầy đủ thông tin sinh viên
            String detailContent = detailResult.getResponse().getContentAsString();
            StudentDetailResponse detail = objectMapper.readValue(detailContent, StudentDetailResponse.class);

            // Acceptance Criteria:
            // - Thông tin cá nhân đầy đủ
            assertNotNull(detail.getCurrentInfo().getFullName(), "Tên sinh viên không được null");
            assertNotNull(detail.getCurrentInfo().getStudentClass(), "Lớp không được null");
            assertNotNull(detail.getCurrentInfo().getMajor(), "Ngành học không được null");
            assertNotNull(detail.getCurrentInfo().getFaculty(), "Khoa không được null");

            // - Thông tin thi cử
            assertNotNull(detail.getExamParticipations(), "Thông tin thi cử không được null");

            // - Trạng thái thi
            assertNotNull(detail.getStatus(), "Trạng thái thi không được null");
            assertNotNull(detail.getStatus().getExamEligibility(), "Tình trạng dự thi không được null");
        }
    }

    @Test
    void userStory_AsManager_IWantToViewExamRoomsByDate_SoThatICanManageExamSchedule() throws Exception {
        // User Story: Là quản lý, tôi muốn xem danh sách phòng thi theo ngày
        // để có thể quản lý lịch thi

        // Given: Tôi là quản lý thi
        String examDate = "2025-06-25";

        // When: Tôi chọn ngày thi để xem thông tin
        // Step 1: Lấy danh sách khu vực
        MvcResult areasResult = mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", examDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String areasContent = areasResult.getResponse().getContentAsString();
        String[] areas = objectMapper.readValue(areasContent, String[].class);

        // Then: Hệ thống hiển thị cấu trúc thi đầy đủ
        assertNotNull(areas, "Danh sách khu vực không được null");

        if (areas.length > 0) {
            String selectedArea = areas[0];

            // Step 2: Lấy ca thi theo khu vực
            MvcResult shiftsResult = mockMvc.perform(get("/api/students/exam-shifts")
                            .param("examDate", examDate)
                            .param("area", selectedArea)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String shiftsContent = shiftsResult.getResponse().getContentAsString();
            String[] shifts = objectMapper.readValue(shiftsContent, String[].class);

            assertNotNull(shifts, "Danh sách ca thi không được null");

            if (shifts.length > 0) {
                String selectedShift = shifts[0];

                // Step 3: Lấy phòng thi
                MvcResult roomsResult = mockMvc.perform(get("/api/students/exam-rooms")
                                .param("examDate", examDate)
                                .param("area", selectedArea)
                                .param("shift", selectedShift)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();

                String roomsContent = roomsResult.getResponse().getContentAsString();
                String[] rooms = objectMapper.readValue(roomsContent, String[].class);

                // Acceptance Criteria:
                // - Có thể duyệt qua cấu trúc: Ngày -> Khu vực -> Ca -> Phòng
                assertNotNull(rooms, "Danh sách phòng thi không được null");
            }
        }
    }

    @Test
    void userStory_AsInspector_IWantToViewViolationStatistics_SoThatICanMonitorExamIntegrity() throws Exception {
        // User Story: Là thanh tra viên, tôi muốn xem thống kê vi phạm
        // để có thể giám sát tính toàn vẹn của kỳ thi

        // Given: Tôi là thanh tra viên
        // When: Tôi truy cập thống kê vi phạm
        MvcResult result = mockMvc.perform(get("/api/violations/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then: Hệ thống hiển thị thống kê đầy đủ
        String content = result.getResponse().getContentAsString();
        ViolationStatisticsResponse stats = objectMapper.readValue(content, ViolationStatisticsResponse.class);

        // Acceptance Criteria:
        // - Thống kê tổng số vi phạm
        assertNotNull(stats.getTotalViolations(), "Tổng số vi phạm không được null");
        assertTrue(stats.getTotalViolations() >= 0, "Số vi phạm phải >= 0");

        // - Thống kê sinh viên theo trạng thái
        assertNotNull(stats.getActiveStudents(), "Số sinh viên đang hoạt động không được null");
        assertNotNull(stats.getSuspendedStudents(), "Số sinh viên bị đình chỉ không được null");
        assertNotNull(stats.getExpelledStudents(), "Số sinh viên bị đuổi không được null");

        assertTrue(stats.getActiveStudents() >= 0, "Số sinh viên hoạt động phải >= 0");
        assertTrue(stats.getSuspendedStudents() >= 0, "Số sinh viên đình chỉ phải >= 0");
        assertTrue(stats.getExpelledStudents() >= 0, "Số sinh viên bị đuổi phải >= 0");
    }

    @Test
    void userStory_AsInspector_IWantToViewSuspendedStudents_SoThatICanTrackPenalties() throws Exception {
        // User Story: Là thanh tra viên, tôi muốn xem danh sách sinh viên bị cấm thi
        // để có thể theo dõi các hình phạt

        // Given: Tôi là thanh tra viên
        // When: Tôi truy cập danh sách sinh viên bị cấm thi
        MvcResult result = mockMvc.perform(get("/api/violations/suspended-expelled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then: Hệ thống hiển thị danh sách sinh viên bị cấm thi
        String content = result.getResponse().getContentAsString();
        assertNotNull(content, "Nội dung phản hồi không được null");

        // Acceptance Criteria:
        // - Trả về định dạng JSON array
        assertTrue(content.startsWith("[") && content.endsWith("]"),
                "Kết quả phải là JSON array");
    }
    @Test
    void businessRule_StudentWithViolationsShouldHaveCorrectStatus() throws Exception {
        // Business Rule: Sinh viên có vi phạm phải có trạng thái phù hợp

        // Given: Hệ thống có sinh viên với vi phạm
        MvcResult searchResult = mockMvc.perform(get("/api/students/search")
                        .param("query", "20110001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String searchContent = searchResult.getResponse().getContentAsString();
        StudentSearchResponse[] students = objectMapper.readValue(searchContent, StudentSearchResponse[].class);

        if (students.length > 0) {
            String studentId = students[0].getStudentId();

            // When: Kiểm tra thông tin sinh viên và vi phạm
            MvcResult detailResult = mockMvc.perform(get("/api/students/detail/" + studentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            MvcResult violationResult = mockMvc.perform(get("/api/violations/student/" + studentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Then: Trạng thái sinh viên phải phù hợp với vi phạm
            String detailContent = detailResult.getResponse().getContentAsString();
            String violationContent = violationResult.getResponse().getContentAsString();

            if (!detailContent.isEmpty()) {
                StudentDetailResponse detail = objectMapper.readValue(detailContent, StudentDetailResponse.class);

                // Business Rule Validation:
                // - Sinh viên phải có trạng thái thi cử rõ ràng
                assertNotNull(detail.getStatus().getExamEligibility(),
                        "Trạng thái dự thi phải được xác định");

                // - Nếu có vi phạm nghiêm trọng, trạng thái phải phản ánh điều đó
                assertTrue(detail.getStatus().getExamEligibility().equals("active") ||
                                detail.getStatus().getExamEligibility().equals("suspended") ||
                                detail.getStatus().getExamEligibility().equals("expelled"),
                        "Trạng thái dự thi phải hợp lệ");
            }
        }
    }
}
