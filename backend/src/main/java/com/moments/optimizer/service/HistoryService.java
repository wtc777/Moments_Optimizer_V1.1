package com.moments.optimizer.service;

import com.moments.optimizer.api.ErrorCodes;
import com.moments.optimizer.domain.AnalysisHistory;
import com.moments.optimizer.dto.HistoryDetailDto;
import com.moments.optimizer.dto.HistorySummaryDto;
import com.moments.optimizer.exception.BadRequestException;
import com.moments.optimizer.exception.NotFoundException;
import com.moments.optimizer.repository.AnalysisHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final AnalysisHistoryRepository historyRepository;

    public HistoryService(AnalysisHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional(readOnly = true)
    public HistoryPage listHistory(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            throw new BadRequestException(ErrorCodes.VALIDATION_ERROR, "userId is required");
        }
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageable = PageRequest.of(safePage, safeSize);
        Page<AnalysisHistory> historyPage = historyRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<HistorySummaryDto> items = historyPage.getContent().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return new HistoryPage(items, historyPage.getNumber(), historyPage.getSize(), historyPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public HistoryDetailDto getHistory(String id) {
        AnalysisHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCodes.HISTORY_NOT_FOUND, "History not found"));
        return toDetail(history);
    }

    private HistorySummaryDto toSummary(AnalysisHistory history) {
        HistorySummaryDto dto = new HistorySummaryDto();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setImagePath(history.getImagePath());
        dto.setInputText(history.getInputText());
        dto.setOutputText(history.getOutputText());
        dto.setDurationMs(history.getDurationMs());
        dto.setModelName(history.getModelName());
        dto.setSuccess(history.getSuccess());
        dto.setErrorMessage(history.getErrorMessage());
        return dto;
    }

    private HistoryDetailDto toDetail(AnalysisHistory history) {
        HistoryDetailDto dto = new HistoryDetailDto();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setImagePath(history.getImagePath());
        dto.setInputText(history.getInputText());
        dto.setOutputText(history.getOutputText());
        dto.setDurationMs(history.getDurationMs());
        dto.setInputTokens(history.getInputTokens());
        dto.setOutputTokens(history.getOutputTokens());
        dto.setTotalTokens(history.getTotalTokens());
        dto.setModelName(history.getModelName());
        dto.setSuccess(history.getSuccess());
        dto.setErrorMessage(history.getErrorMessage());
        return dto;
    }

    public record HistoryPage(List<HistorySummaryDto> items, int page, int size, long total) {}
}
