package hcmute.edu.vn.baiTapCuoiKyKTPM.functional;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử chức năng - Kiểm tra các chức năng cụ thể của phần mềm
 * Kiểm tra từng API endpoint và chức năng nghiệp vụ
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentFunctionalTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSearchStudentsByName_ShouldReturnCorrectResults() throws Exception {
        // Test chức năng tìm kiếm sinh viên theo tên
        mockMvc.perform(get("/api/students/search")
                        .param("query", "Nguyễn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchStudentsByStudentId_ShouldReturnCorrectResults() throws Exception {
        // Test chức năng tìm kiếm sinh viên theo MSSV
        mockMvc.perform(get("/api/students/search")
                        .param("query", "20110001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetExamAreas_WithValidDate_ShouldReturnAreaList() throws Exception {
        // Test chức năng lấy danh sách khu vực thi theo ngày
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "2025-06-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetExamShifts_WithValidParameters_ShouldReturnShiftList() throws Exception {
        // Test chức năng lấy danh sách ca thi
        mockMvc.perform(get("/api/students/exam-shifts")
                        .param("examDate", "2025-06-25")
                        .param("area", "A2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetExamRooms_WithValidParameters_ShouldReturnRoomList() throws Exception {
        // Test chức năng lấy danh sách phòng thi
        mockMvc.perform(get("/api/students/exam-rooms")
                        .param("examDate", "2025-06-25")
                        .param("area", "A2")
                        .param("shift", "Ca 1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetStudentsByExamRoom_WithValidParameters_ShouldReturnStudentList() throws Exception {
        // Test chức năng lấy danh sách sinh viên trong phòng thi
        mockMvc.perform(get("/api/students/exam-room-students")
                        .param("examDate", "2025-06-25")
                        .param("area", "A2")
                        .param("shift", "Ca 1")
                        .param("room", "P.201")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetStudentDetail_WithValidStudentId_ShouldReturnStudentInfo() throws Exception {
        // Test chức năng lấy thông tin chi tiết sinh viên
        MvcResult result = mockMvc.perform(get("/api/students/detail/21001237")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        if (!responseContent.isEmpty()) {
            StudentDetailResponse response = objectMapper.readValue(responseContent, StudentDetailResponse.class);
            assertNotNull(response);
            assertEquals("21001237", response.getStudentId());
        }
    }

    @Test
    void testGetStudentDetail_WithInvalidStudentId_ShouldReturn404() throws Exception {
        // Test chức năng với MSSV không tồn tại
        mockMvc.perform(get("/api/students/detail/99999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetExamAreas_WithInvalidDate_ShouldReturn400() throws Exception {
        // Test chức năng với ngày không hợp lệ
        mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExamShifts_WithMissingParameters_ShouldReturn400() throws Exception {
        // Test chức năng thiếu tham số
        mockMvc.perform(get("/api/students/exam-shifts")
                        .param("examDate", "2025-06-25")
                        // Missing area parameter
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testViolationWorkflow_CompleteFlow_ShouldWork() throws Exception {
        // Test luồng nghiệp vụ hoàn chỉnh: Tìm sinh viên -> Xem vi phạm

        // Bước 1: Tìm sinh viên
        MvcResult searchResult = mockMvc.perform(get("/api/students/search")
                        .param("query", "20110001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String searchContent = searchResult.getResponse().getContentAsString();
        if (!searchContent.equals("[]")) {
            // Bước 2: Lấy thông tin chi tiết sinh viên
            mockMvc.perform(get("/api/students/detail/20110001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Bước 3: Kiểm tra vi phạm của sinh viên
            mockMvc.perform(get("/api/violations/student/20110001")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray());
        }
    }
}
