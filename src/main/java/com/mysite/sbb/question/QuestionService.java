package com.mysite.sbb.question;

import com.mysite.sbb.category.Category;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.mysite.sbb.DataNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.mysite.sbb.answer.Answer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> getSortedList(Category category, int page, String kw, String sortType) {
        Pageable pageable = PageRequest.of(page, 10); // 기본 페이지 크기 설정

        if (category != null) {
            return this.questionRepository.findAllByCategoryWithSort(category, kw, sortType, pageable);
        } else {
            return this.questionRepository.findAllWithSort(kw, sortType, pageable);
        }
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        }
        else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user, Category category) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);
        question.setCategory(category);
        this.questionRepository.save(question);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public Page<Question> getMyList(SiteUser siteUser, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.questionRepository.findByAuthor(siteUser, pageable);
    }

    public void view(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            Question targetQuestion = question.get();
            targetQuestion.setViews(targetQuestion.getViews() + 1);
            this.questionRepository.save(targetQuestion);
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

}
