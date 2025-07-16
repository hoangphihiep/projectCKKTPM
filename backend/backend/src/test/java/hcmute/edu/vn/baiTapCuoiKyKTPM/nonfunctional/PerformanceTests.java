package hcmute.edu.vn.baiTapCuoiKyKTPM.nonfunctional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Kiểm thử phi chức năng - Kiểm tra hiệu suất, bảo mật, khả năng sử dụng
 */
@SpringBootTest
@AutoConfigureMockMvc
class PerformanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSearchStudents_ResponseTime_ShouldBeFast() throws Exception {
        // Kiểm tra thời gian phản hồi của API tìm kiếm sinh viên
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/api/students/search")
                        .param("query", "Nguyễn")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        stopWatch.stop();
        long executionTime = stopWatch.getTotalTimeMillis();

        // Thời gian phản hồi phải dưới 2 giây
        assertTrue(executionTime < 2000,
                "API response time should be less than 2 seconds, actual: " + executionTime + "ms");
    }

    @Test
    void testConcurrentRequests_ShouldHandleMultipleUsers() throws Exception {
        // Kiểm tra khả năng xử lý đồng thời nhiều request
        int numberOfThreads = 10;
        int requestsPerThread = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfThreads];

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < numberOfThreads; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        mockMvc.perform(get("/api/students/search")
                                        .param("query", "test" + j)
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
                    }
                } catch (Exception e) {
                    fail("Concurrent request failed: " + e.getMessage());
                }
            }, executor);
        }

        // Chờ tất cả requests hoàn thành
        CompletableFuture.allOf(futures).join();
        stopWatch.stop();

        long totalTime = stopWatch.getTotalTimeMillis();
        int totalRequests = numberOfThreads * requestsPerThread;

        // Thời gian xử lý trung bình mỗi request phải dưới 1 giây
        double avgTimePerRequest = (double) totalTime / totalRequests;
        assertTrue(avgTimePerRequest < 1000,
                "Average time per request should be less than 1 second, actual: " + avgTimePerRequest + "ms");

        executor.shutdown();
    }
}
