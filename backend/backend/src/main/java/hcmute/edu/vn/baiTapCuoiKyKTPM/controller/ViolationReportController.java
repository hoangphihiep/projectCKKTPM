package hcmute.edu.vn.baiTapCuoiKyKTPM.controller;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.*;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.ViolationReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/violations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ViolationReportController {

    private final ViolationReportService violationReportService;

    // API lấy tất cả vi phạm của một sinh viên
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ViolationReportResponse>> getStudentViolations(@PathVariable String studentId) {
        List<ViolationReportResponse> violations = violationReportService.getStudentViolations(studentId);
        return ResponseEntity.ok(violations);
    }

    // API lấy tóm tắt vi phạm của một sinh viên
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<ViolationSummaryResponse> getStudentViolationSummary(@PathVariable String studentId) {
        Optional<ViolationSummaryResponse> summary = violationReportService.getStudentViolationSummary(studentId);
        return summary.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // API lấy vi phạm theo phòng thi
    @GetMapping("/exam-room")
    public ResponseEntity<ExamRoomViolationResponse> getExamRoomViolations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate examDate,
            @RequestParam String area,
            @RequestParam String shift,
            @RequestParam String room) {
        ExamRoomViolationResponse violations = violationReportService.getExamRoomViolations(examDate, area, shift, room);
        return ResponseEntity.ok(violations);
    }

    // API lấy vi phạm trong khoảng thời gian
    @GetMapping("/date-range")
    public ResponseEntity<List<ViolationReportResponse>> getViolationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ViolationReportResponse> violations = violationReportService.getViolationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(violations);
    }

    // API lấy thống kê vi phạm tổng quan
    @GetMapping("/statistics")
    public ResponseEntity<ViolationStatisticsResponse> getViolationStatistics() {
        ViolationStatisticsResponse statistics = violationReportService.getViolationStatistics();
        return ResponseEntity.ok(statistics);
    }

    // API lấy danh sách sinh viên bị cấm thi (suspended/expelled)
    @GetMapping("/suspended-expelled")
    public ResponseEntity<List<ViolationSummaryResponse>> getSuspendedAndExpelledStudents() {
        List<ViolationSummaryResponse> students = violationReportService.getSuspendedAndExpelledStudents();
        return ResponseEntity.ok(students);
    }

    // API lấy tất cả vi phạm (có phân trang)
    @GetMapping("/all")
    public ResponseEntity<List<ViolationReportResponse>> getAllViolations(
            @RequestParam(defaultValue = "2020-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "2030-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ViolationReportResponse> violations = violationReportService.getViolationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(violations);
    }
}
