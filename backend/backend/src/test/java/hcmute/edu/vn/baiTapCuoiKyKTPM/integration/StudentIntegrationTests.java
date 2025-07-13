package hcmute.edu.vn.baiTapCuoiKyKTPM.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfo;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetStudentDetailById() throws Exception {
        Student student = new Student();
        student.setStudentId("22009999");
        repository.save(student);

        String id = repository.findAll().get(0).getId();

        mockMvc.perform(get("/api/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("22009999"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testSearchStudentByKeyword() throws Exception {
        Student student = new Student();
        student.setStudentId("SV00123");

        StudentInfo info = new StudentInfo();
        info.setFullName("Nguyen Van A");
        info.setDob("2001-01-01");
        info.setGender("Nam");
        info.setStudentClass("DHKTPM17A");
        info.setEmail("test@ute.vn");
        info.setPhone("0909123456");
        student.setCurrentInfo(info);

        repository.save(student);

        mockMvc.perform(get("/api/students/search")
                        .param("query", "SV001"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("SV00123")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetNotFoundStudentById() throws Exception {
        mockMvc.perform(get("/api/students/invalid-id"))
                .andExpect(status().isNotFound());
    }
}
