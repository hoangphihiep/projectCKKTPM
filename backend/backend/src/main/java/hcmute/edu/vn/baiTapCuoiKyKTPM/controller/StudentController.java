package hcmute.edu.vn.baiTapCuoiKyKTPM.controller;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentDetailResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    @GetMapping("/search")
    public ResponseEntity<List<StudentSearchResponse>> searchStudents(
            @RequestParam String query) {
        List<StudentSearchResponse> students = studentService.searchStudents(query);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/exam-areas")
    public ResponseEntity<List<String>> getExamAreas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate) {
        List<String> areas = studentService.getExamAreasByDate(examDate);
        return ResponseEntity.ok(areas);
    }

    // API lấy danh sách ca thi theo ngày và khu vực
    @GetMapping("/exam-shifts")
    public ResponseEntity<List<String>> getExamShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam String area) {
        List<String> shifts = studentService.getExamShiftsByDateAndArea(examDate, area);
        return ResponseEntity.ok(shifts);
    }

    // API lấy danh sách phòng thi theo ngày, khu vực và ca thi
    @GetMapping("/exam-rooms")
    public ResponseEntity<List<String>> getExamRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam String area,
            @RequestParam String shift) {
        List<String> rooms = studentService.getExamRoomsByDateAreaAndShift(examDate, area, shift);
        return ResponseEntity.ok(rooms);
    }

    // API lấy danh sách sinh viên trong phòng thi
    @GetMapping("/exam-room-students")
    public ResponseEntity<List<StudentSearchResponse>> getStudentsByExamRoom(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam String area,
            @RequestParam String shift,
            @RequestParam String room) {
        List<StudentSearchResponse> students = studentService.getStudentsByExamRoom(examDate, area, shift, room);
        return ResponseEntity.ok(students);
    }

    // API lấy thông tin chi tiết sinh viên theo mã sinh viên
    @GetMapping("/detail/{studentId}")
    public ResponseEntity<StudentDetailResponse> getStudentDetail(@PathVariable String studentId) {
        Optional<StudentDetailResponse> student = studentService.getStudentDetail(studentId);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // API lấy thông tin chi tiết sinh viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentDetailResponse> getStudentById(@PathVariable String id) {
        Optional<StudentDetailResponse> student = studentService.getStudentDetailById(id);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
