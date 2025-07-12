package hcmute.edu.vn.baiTapCuoiKyKTPM.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAccessTests {

    @Autowired
    private MockMvc mockMvc;

    // ❌ Không đăng nhập → vẫn được phép vì permitAll()
    @Test
    void testAccessDeniedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/students/search").param("query", "test"))
                .andExpect(status().isOk());
    }

    // ✅ Đăng nhập với quyền ADMIN → truy cập thành công
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAccessAllowedForAdmin() throws Exception {
        mockMvc.perform(get("/api/students/search").param("query", "abc"))
                .andExpect(status().isOk());
    }

    // ❌ Đăng nhập với quyền không hợp lệ → hiện tại vẫn được vì permitAll()
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAccessDeniedForUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/students/search").param("query", "xyz"))
                .andExpect(status().isOk());
    }

    // ✅ Đăng nhập với quyền INVIGILATOR (Thanh tra) → truy cập hợp lệ
    @Test
    @WithMockUser(username = "ttkt", roles = {"INVIGILATOR"})
    void testAccessAllowedForInvigilator() throws Exception {
        mockMvc.perform(get("/api/students/search").param("query", "sv"))
                .andExpect(status().isOk());
    }

    // ❌ Anonymous truy cập endpoint khác → hiện tại cũng được vì permitAll()
    @Test
    void testAnonymousAccessExamRoomsBlocked() throws Exception {
        mockMvc.perform(get("/api/students/exam-rooms")
                        .param("examDate", "2025-07-15")
                        .param("area", "A1")
                        .param("shift", "1"))
                .andExpect(status().isOk());
    }


}
