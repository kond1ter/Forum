package study.konditer.forum.service;

import java.util.List;

import org.springframework.data.domain.Page;

import study.konditer.forum.dto.PinRequestInputDto;
import study.konditer.forum.dto.PinRequestOutputDto;

public interface PinRequestService {
    
    void add(PinRequestInputDto pinRequestDto);

    void approve(Long id);

    void reject(Long id);

    PinRequestOutputDto get(Long id);

    Page<PinRequestOutputDto> getPage(int page, int size);

    List<PinRequestOutputDto> getAll();

    List<PinRequestOutputDto> getAllByQuestion(Long questionId);
}
