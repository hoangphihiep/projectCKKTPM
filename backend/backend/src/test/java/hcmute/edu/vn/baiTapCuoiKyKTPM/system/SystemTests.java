package hcmute.edu.vn.baiTapCuoiKyKTPM.system;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Kiểm thử hệ thống - Kiểm tra toàn bộ hệ thống như một khối thống nhất
 * Kiểm tra end-to-end workflows và tích hợp hoàn chỉnh
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteStudentManagementWorkflow() throws Exception {
        // Kiểm tra luồng nghiệp vụ hoàn chỉnh: Quản lý sinh viên

        // Scenario: Giáo viên coi thi tra cứu thông tin sinh viên và vi phạm

        // Bước 1: Tìm kiếm sinh viên theo tên
        MvcResult searchResult = mockMvc.perform(get("/api/students/search")
                        .param("query", "Nguyễn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String searchContent = searchResult.getResponse().getContentAsString();
        assertNotNull(searchContent);

        // Bước 2: Kiểm tra thống kê vi phạm tổng quan
        MvcResult statsResult = mockMvc.perform(get("/api/violations/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String statsContent = statsResult.getResponse().getContentAsString();
        ViolationStatisticsResponse stats = objectMapper.readValue(statsContent, ViolationStatisticsResponse.class);
        assertNotNull(stats);
        assertTrue(stats.getTotalViolations() >= 0);

        // Bước 3: Lấy danh sách sinh viên bị cấm thi
        mockMvc.perform(get("/api/violations/suspended-expelled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCompleteExamRoomManagementWorkflow() throws Exception {
        // Kiểm tra luồng nghiệp vụ hoàn chỉnh: Quản lý phòng thi

        // Scenario: Quản lý tra cứu thông tin phòng thi và sinh viên

        String examDate = "2025-06-25";

        // Bước 1: Lấy tất cả khu vực thi trong ngày
        MvcResult areasResult = mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", examDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String areasContent = areasResult.getResponse().getContentAsString();
        String[] areas = objectMapper.readValue(areasContent, String[].class);

        // Bước 2: Với mỗi khu vực, lấy thông tin chi tiết
        for (String area : areas) {
            // Lấy ca thi
            MvcResult shiftsResult = mockMvc.perform(get("/api/students/exam-shifts")
                            .param("examDate", examDate)
                            .param("area", area)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String shiftsContent = shiftsResult.getResponse().getContentAsString();
            String[] shifts = objectMapper.readValue(shiftsContent, String[].class);

            // Với mỗi ca thi, lấy phòng thi
            for (String shift : shifts) {
                mockMvc.perform(get("/api/students/exam-rooms")
                                .param("examDate", examDate)
                                .param("area", area)
                                .param("shift", shift)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

                // Chỉ test một vài phòng để tránh quá tải
                break;
            }

            // Chỉ test một vài khu vực để tránh quá tải
            if (areas.length > 2) break;
        }
    }

    @Test
    void testCompleteViolationManagementWorkflow() throws Exception {
        // Kiểm tra luồng nghiệp vụ hoàn chỉnh: Quản lý vi phạm

        // Scenario: Thanh tra viên kiểm tra vi phạm và tạo báo cáo

        // Bước 1: Xem thống kê vi phạm tổng quan
        MvcResult statsResult = mockMvc.perform(get("/api/violations/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String statsContent = statsResult.getResponse().getContentAsString();
        ViolationStatisticsResponse stats = objectMapper.readValue(statsContent, ViolationStatisticsResponse.class);

        // Bước 2: Lấy tất cả vi phạm trong khoảng thời gian
        mockMvc.perform(get("/api/violations/date-range")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Bước 3: Kiểm tra vi phạm theo phòng thi cụ thể
        mockMvc.perform(get("/api/violations/exam-room")
                        .param("examDate", "2025-06-25")
                        .param("area", "A2")
                        .param("shift", "Ca 1")
                        .param("room", "P.201")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Bước 4: Lấy danh sách sinh viên bị cấm thi
        mockMvc.perform(get("/api/violations/suspended-expelled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
