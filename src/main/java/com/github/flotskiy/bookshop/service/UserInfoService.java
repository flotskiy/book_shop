package com.github.flotskiy.bookshop.service;

import com.github.flotskiy.bookshop.model.dto.post.MessageForm;
import com.github.flotskiy.bookshop.model.dto.user.DocumentDto;
import com.github.flotskiy.bookshop.model.dto.user.FaqDto;
import com.github.flotskiy.bookshop.model.entity.book.review.MessageEntity;
import com.github.flotskiy.bookshop.model.entity.other.DocumentEntity;
import com.github.flotskiy.bookshop.model.entity.other.FaqEntity;
import com.github.flotskiy.bookshop.repository.DocumentRepository;
import com.github.flotskiy.bookshop.repository.FaqRepository;
import com.github.flotskiy.bookshop.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInfoService {

    private final MessageRepository messageRepository;
    private final FaqRepository faqRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public UserInfoService(
            MessageRepository messageRepository, FaqRepository faqRepository, DocumentRepository documentRepository
    ) {
        this.messageRepository = messageRepository;
        this.faqRepository = faqRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional
    public void createNewMessage(MessageForm messageForm, int userId) {
        MessageEntity message = new MessageEntity();
        message.setTime(LocalDateTime.now());
        message.setUserId(userId);
        if (userId == -1) {
            message.setEmail(messageForm.getMail());
            message.setName(messageForm.getName());
        }
        message.setSubject(messageForm.getTopic());
        message.setText(messageForm.getMessage());
        messageRepository.save(message);
    }

    @Transactional
    public List<FaqDto> getAllFaq() {
        List<FaqEntity> faqEntityList;
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if (lang.equals("ru")) {
            faqEntityList = faqRepository.getAllRuFaqOrderBySortIndexAsc();
        } else {
            faqEntityList = faqRepository.getAllEngFaqOrderBySortIndexAsc();
        }
        return convertFaqEntityListToFaqDtoList(faqEntityList);
    }

    @Transactional
    public List<DocumentDto> getAllDocuments() {
        List<DocumentEntity> documentEntityList;
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if (lang.equals("ru")) {
            documentEntityList = documentRepository.getAllRuDocumentsOrderBySortIndexAsc();
        } else {
            documentEntityList = documentRepository.getAllEngDocumentsOrderBySortIndexAsc();
        }
        return convertDocumentEntityListToDocumentDtoList(documentEntityList);
    }

    @Transactional
    public DocumentDto getDocumentDto(String slug) {
        DocumentEntity documentEntity = documentRepository.findDocumentEntityBySlug(slug);
        return convertDocumentEntityToDocumentDto(documentEntity);
    }

    private List<FaqDto> convertFaqEntityListToFaqDtoList(List<FaqEntity> faqEntityList) {
        List<FaqDto> faqDtoList = new ArrayList<>();
        for (FaqEntity faqEntity : faqEntityList) {
            FaqDto faqDto = new FaqDto();
            faqDto.setQuestion(faqEntity.getQuestion());
            faqDto.setAnswer(faqEntity.getAnswer());
            faqDtoList.add(faqDto);
        }
        return faqDtoList;
    }

    private List<DocumentDto> convertDocumentEntityListToDocumentDtoList(List<DocumentEntity> documentEntityList) {
        return documentEntityList.stream().map(this::convertDocumentEntityToDocumentDto).collect(Collectors.toList());
    }

    private DocumentDto convertDocumentEntityToDocumentDto(DocumentEntity documentEntity) {
        DocumentDto documentDto = new DocumentDto();
        documentDto.setSlug(documentEntity.getSlug());
        documentDto.setTitle(documentEntity.getTitle());
        documentDto.setDescription(documentEntity.getText().substring(0, documentEntity.getText().indexOf("\n")));
        documentDto.setText(documentEntity.getText());
        return documentDto;
    }
}
