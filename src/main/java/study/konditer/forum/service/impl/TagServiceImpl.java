package study.konditer.forum.service.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import study.konditer.forum.dto.TagInputDto;
import study.konditer.forum.dto.TagOutputDto;
import study.konditer.forum.exception.NotFoundServiceException;
import study.konditer.forum.model.Tag;
import study.konditer.forum.repository.TagRepository;
import study.konditer.forum.service.TagService;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void add(TagInputDto tagDto) {
        tagRepository.save(mapToEntity(tagDto));
    }

    @Override
    public void remove(long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tag not found"));
        tagRepository.delete(tag);
    }

    @Override
    public TagOutputDto get(long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundServiceException("Tag not found"));
        return mapToDto(tag);
    }

    @Override
    public List<TagOutputDto> getAll() {
        List<Tag> tags = tagRepository.findAll();
        return tags.stream()
                .map(tag -> mapToDto(tag)).toList();
    }

    @Override
    public List<TagOutputDto> getAllByQuestion(long questionId) {
        List<Tag> tags = tagRepository.findAllByQuestionId(questionId);
        return tags.stream()
                .map(tag -> mapToDto(tag)).toList();
    }

    private Tag mapToEntity(TagInputDto tagDto) {

        return new Tag(
            tagDto.name(),
            new HashSet<>()
        );
    }

    private TagOutputDto mapToDto(Tag tag) {
        return new TagOutputDto(
            tag.getId(),
            tag.getName()
        );
    }
}
