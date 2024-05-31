package com.example.quiz.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.AnswerRes;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private AnswerDao answerDao;

	@Override
	public BaseRes create(CreateOrUpdateReq req) {
		BaseRes res = checkParams(req);
		if (res != null) {
			return res;
		}
		// �ˬd�ݨ��O�_�w�s�b
		if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
			return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
		}
		// �ھڬO�_�n�o���A�A�� published ���� set ��ǰe�L�Ӫ� quizList ��
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		// �s�^ DB
		try {
			quizDao.saveAll(req.getQuizList());
		} catch (Exception e) {
			return new BaseRes(RtnCode.SAVE_QUIZ_ERROR.getCode(), RtnCode.SAVE_QUIZ_ERROR.getMessage());
		}
		
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public SearchRes search(String quizName, LocalDate startDate, LocalDate endDate, boolean isBackend) {
		if (!StringUtils.hasText(quizName)) {
			quizName = ""; // containing �N���ѼƭȬ��Ŧr��A��ܼ�������
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1); // �N�}�l�ɶ��q���ܦ�
		}
		if (endDate == null) {
			endDate = LocalDate.of(2099, 12, 31); // �N�����]���ܤ[����
		}
		if (isBackend) {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName,
							startDate, endDate));
		} else {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
							quizName, startDate, endDate));
		}
	}

	@Override
	public BaseRes deleteQuiz(List<Integer> quizIds) {
		if (CollectionUtils.isEmpty(quizIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteAllByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(quizIds, quizIds, LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes deleteQuestions(int quizId, List<Integer> quIds) {

		if (quizId <= 0 || CollectionUtils.isEmpty(quIds)) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(//
				quizId, quizId, LocalDate.now());

		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
		}

		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!quIds.contains(item.getQuId())) { // �O�d���b�R���M�椤��
				retainList.add(item);
			}
		}
		for (int i = 0; i < retainList.size(); i++) {
			retainList.get(i).setQuId(i + 1);
		}
		// �R����i�ݨ�
		quizDao.deleteByQuizId(quizId);
		// �N�O�d�����D�s�^DB
		if (!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes update(CreateOrUpdateReq req) {

		BaseRes res = checkParams(req);
		if (res != null) {
			return res;
		}
		if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(//
				req.getQuizList().get(0).getQuizId(), req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
			return new BaseRes(RtnCode.QUIZ_NOT_FOUND.getCode(), RtnCode.QUIZ_NOT_FOUND.getMessage());
		}
		// �R����i�ݨ�
		try {
			quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
		} catch (Exception e) {
			return new BaseRes(RtnCode.DELETE_QUIZ_ERROR.getCode(), RtnCode.DELETE_QUIZ_ERROR.getMessage());
		}
		
		// �ھڬO�_�n�o���A�A�� published ���� set ��ǰe�L�Ӫ� quizList ��
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		// �s�^ DB
		try {
			quizDao.saveAll(req.getQuizList());
		} catch (Exception e) {
			return new BaseRes(RtnCode.SAVE_QUIZ_ERROR.getCode(), RtnCode.SAVE_QUIZ_ERROR.getMessage());
		}
		
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}
	
	

	private BaseRes checkParams(CreateOrUpdateReq req) {
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Quiz item : req.getQuizList()) {
			// �ˬd���񶵥�
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		// �`�� req ���Ҧ��� quizId
		// ��h�W�O�@�� req ���Ҧ��� quizId�|�ۦP(�@�i�ݨ��h�Ӱ��D)�A���]���i��䤤�@�� quizId �O����
		// ���O�ҩҦ���ƪ����T�ʡA�N���h�`�� req ���Ҧ��� quizId
//		List<Integer> quizIds = new ArrayList<>(); // List ���\���ƪ��Ȧs�b
//		for(Quiz item : req.getQuizList()) {
//			if(!quizIdList.contains(item.getQuizId())) {
//				quizIdList.add(item.getQuizId());
//			}
//		}
		// �H�U�� set ���g�k�P�W���� List ���g�k���G�@�Ҥ@��
		Set<Integer> quizIds = new HashSet<>(); // set ���|�s�b�ۦP���ȡA�N�O set ���w�s�b�ۦP���ȡA�N���|�s�W
		Set<Integer> quIds = new HashSet<>(); // �ˬd���D�s���O�_������
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		// �ˬd�}�l�ɶ�����j�󵲧��ɶ�
		for (Quiz item : req.getQuizList()) {
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}
		}
		return null;
	}

	@Override
	public BaseRes answer(AnswerReq req) {
		if (CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() < 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
		// �ˬd��ƦC���Ҧ��� quizId ���@�ˡA�H�� quId ��������
		Set<Integer> quizIds = new HashSet<>(); // set ���|�s�b�ۦP���ȡA�N�O set ���w�s�b�ۦP���ȡA�N���|�s�W
		Set<Integer> quIds = new HashSet<>(); // �ˬd���D�s���O�_������
		for (Answer item : req.getAnswerList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_EXISTS.getCode(), RtnCode.QUIZ_EXISTS.getMessage());
		}
		if (quIds.size() != req.getAnswerList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}
		// �ˬd������D�O�_���^��
		List<Integer> res = quizDao.findQuidsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for (Answer item : req.getAnswerList()) {
			if (res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_NO_ANSWER.getCode(), RtnCode.QUESTION_NO_ANSWER.getMessage());
			}
		}
		// �T�{�P�@��email���୫�ƶ�g�P�@�i�ݨ�
		if(answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(), 
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		if (quizId <= 0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		// �����ݨ����o���D�� type �O�D²���D
		List<Quiz> quizs = quizDao.findByQuizId(quizId);
		// qus �O�D²���D���D�ؽs�������X
		List<Integer> qus = new ArrayList<>();
		// �Y�O²���D�Aoptions �O�Ū�
		for (Quiz item : quizs) {
			if (StringUtils.hasText(item.getOptions())) {
				qus.add(item.getQuId());
			}
		}
		List<Answer> answers = answerDao.findByQuizIdOrderByQuId(quizId);
		// quidAnswerMap: ���D�s���P���ת� mapping
		Map<Integer, String> quIdAnswerMap = new HashMap<>();
		// ��D²���D���C�D���צU�ۦꦨ�r��A�Y�@�ӿﶵ(����)�|���@�Ӧr��
		for (Answer item : answers) {
			// �Y�O�]�t�bqus��list�����N��ܬO����D(��B�h��)
			if (qus.contains(item.getQuId())) {
				// �Y key �w�s�b
				if (quIdAnswerMap.containsKey(item.getQuId())) {
					// 1. �z�L key ���o������ value
					String str = quIdAnswerMap.get(item.getQuId());
					// 2. ��Ҧ����ȩM�o�����o���Ȧ걵���s���r��
					str += item.getAnswer();
					// 3. �N�s���ȩ�^��쥻�� key ���U
					quIdAnswerMap.put(item.getQuId(), str);
				} else { // key ���s�b�A�����s�W key �M value
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			} 

		}		
		// �p��C�D�C�ӿﶵ������
		// Map ���� Map<String, Integer> �A�����O answerCountMap
		Map<Integer, Map<String, Integer>> quizIdAndAnsCountMap = new HashMap<>();
		// �ϥ�for each �M�� map �����C�Ӷ���
		// �M������H�n�q map �ন entrySet�A�n�B�O�i�H�������o map ���� key �M value
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			// answerCoontMap: �ﶵ(����)�P���ƪ� mapping
			Map<String, Integer> answerCountMap = new HashMap<>();
			// ���o�C�Ӱ��D���ﶵ
			//�D����D�� options �O null : �n�ư�
			if(quizs.get(item.getKey() - 1).getOptions() == null) {
				continue;
			}
			String[] optionList = quizs.get(item.getKey() - 1).getOptions().split(";");
			// ����D���ﶵ�P���ư� mapping
			for (String option : optionList) {
				String newStr = item.getValue();
				int length1 = newStr.length();
				newStr = newStr.replace(option, "");
				int length2 = newStr.length();
				// �n�� option ����]�O option �O�ﶵ�����e�A�Ӥ��O�ﶵ���s��
				int count = length1 - length2 / option.length();
				answerCountMap.put(option, count);
			}
			quizIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quizIdAndAnsCountMap);
	}

	@Override
	public BaseRes objMapper(String str) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Quiz quiz = mapper.readValue(str, Quiz.class);
		} catch (Exception e) {
			// 1. �^�ǩT�w���~�T��
			// return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			// 2. �^�� catch �� exception �����~�T��
			return new BaseRes(RtnCode.ERROR_CODE, e.getMessage());
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public SearchRes findAllInfo(int quizId) {
		
		return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
				quizDao.findByQuizId(quizId));
	}

	@Override
	public AnswerRes findAnswer(int quizId, int quId) {
		
		return new AnswerRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
				answerDao.findByQuizIdAndQuId(quizId, quId));
	}
}
