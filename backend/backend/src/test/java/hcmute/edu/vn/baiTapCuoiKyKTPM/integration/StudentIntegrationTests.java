package hcmute.edu.vn.baiTapCuoiKyKTPM.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentDetailResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.ViolationReportResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tích hợp - Kiểm tra sự tương tác giữa các module/thành phần
 * Kiểm tra tích hợp giữa Controller, Service, Repository và Database
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStudentSearchAndDetailIntegration() throws Exception {
        // Kiểm tra tích hợp giữa tìm kiếm sinh viên và lấy thông tin chi tiết

        // Bước 1: Tìm kiếm sinh viên
        MvcResult searchResult = mockMvc.perform(get("/api/students/search")
                        .param("query", "Nguyễn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String searchContent = searchResult.getResponse().getContentAsString();
        StudentSearchResponse[] searchResponses = objectMapper.readValue(searchContent, StudentSearchResponse[].class);

        if (searchResponses.length > 0) {
            String studentId = searchResponses[0].getStudentId();

            // Bước 2: Lấy thông tin chi tiết sinh viên đầu tiên
            MvcResult detailResult = mockMvc.perform(get("/api/students/detail/" + studentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            String detailContent = detailResult.getResponse().getContentAsString();
            StudentDetailResponse detailResponse = objectMapper.readValue(detailContent, StudentDetailResponse.class);

            // Kiểm tra tính nhất quán dữ liệu
            assertEquals(studentId, detailResponse.getStudentId());
            assertEquals(searchResponses[0].getFullName(), detailResponse.getCurrentInfo().getFullName());
        }
    }

    @Test
    void testExamRoomWorkflowIntegration() throws Exception {
        // Kiểm tra tích hợp luồng nghiệp vụ: Ngày thi -> Khu vực -> Ca thi -> Phòng -> Sinh viên

        String examDate = "2025-06-25";

        // Bước 1: Lấy danh sách khu vực thi
        MvcResult areasResult = mockMvc.perform(get("/api/students/exam-areas")
                        .param("examDate", examDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String areasContent = areasResult.getResponse().getContentAsString();
        String[] areas = objectMapper.readValue(areasContent, String[].class);

        if (areas.length > 0) {
            String selectedArea = areas[0];

            // Bước 2: Lấy danh sách ca thi theo khu vực
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

                // Bước 3: Lấy danh sách phòng thi
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

                    // Bước 4: Lấy danh sách sinh viên trong phòng
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
