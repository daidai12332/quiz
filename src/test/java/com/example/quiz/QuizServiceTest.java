package com.example.quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizId;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizDao quizDao;

//	@BeforeEach
//	private void addData() {
//		CreateOrUpdateReq req = new CreateOrUpdateReq();
//		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
//				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
//		quizService.create(req);
//	}
//	
//	@AfterEach
//	private void afterEach() {
//		quizDao.deleteById(new QuizId(1,1));
//	}

	@Test
	public void createTest() {
		// �P�_list����
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test list null fail!!");
		// ========================= ���� quizId
		quizIdTest(req,res);
		// ======================== ���� quId
		quIdTest(req,res);
		// ======================== ���� quizName
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test quizName fail!!");
		// ======================== ���� startDate
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", null, LocalDate.now().plusDays(9),
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test startDate fail!!");
		// ======================== ���� endDate
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2), null,
				"q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test endDate fail!!");
		// ======================== ���� question(���D�W��)
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test question fail!!");
		// ======================== ���� type
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test type fail!!");
		// ======================== ���� startDate > endDate
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(9),
				LocalDate.now().plusDays(2), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test data range fail!!");
		// ======================== ���զ��\
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "create test success fail!!");
		// ======================== ���հݨ��w�s�b
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test exists fail!!");
		// ======================== �R�����ո��
		
	}

	private void quizIdTest(CreateOrUpdateReq req, BaseRes res) {
		// ========================= ���� quizId
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(0, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test quizId fail!!");
	}
	
	private void quIdTest(CreateOrUpdateReq req, BaseRes res) {
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, -1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "single", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "create test quId fail!!");
	}

	@Test
	public void updateTest() {
		LocalDate now = LocalDate.now();
		LocalDate te = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
		if(now.getMonthValue() == te.getMonthValue()) {
			System.out.println("equal!");
		}
		
	}
}
