package com.example.quiz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quiz.entity.Answer;

@Repository
public interface AnswerDao extends JpaRepository<Answer, Integer>{

	public List<Answer> findByQuizIdOrderByQuId(int quizId);
	
	public List<Answer> findByQuizIdAndQuId(int quizId, int quId);
	
	public boolean existsByQuizIdAndEmail(int quizId, String email);
}
