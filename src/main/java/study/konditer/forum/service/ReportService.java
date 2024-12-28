package study.konditer.forum.service;

import java.util.List;

import org.springframework.data.domain.Page;

import study.konditer.forum.dto.ReportInputDto;
import study.konditer.forum.dto.ReportOutputDto;

public interface ReportService {
    
    void add(ReportInputDto report);

    void approve(long id, boolean ban);

    void reject(long id);

    ReportOutputDto get(long id);

    List<ReportOutputDto> getAll();

    Page<ReportOutputDto> getPage(int page, int size);
}
