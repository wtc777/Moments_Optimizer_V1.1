package com.moments.optimizer.controller.api;

import com.moments.optimizer.api.ApiResponse;
import com.moments.optimizer.dto.HistoryDetailDto;
import com.moments.optimizer.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HistoryService.HistoryPage>> listHistory(
            @RequestParam("userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        HistoryService.HistoryPage result = historyService.listHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoryDetailDto>> getHistory(@PathVariable("id") String id) {
        HistoryDetailDto detail = historyService.getHistory(id);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }
}
