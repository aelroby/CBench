package qa.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.*;

import fileManagement.FileManager;
import qa.dataStructures.Question;

public class JSONParser {
	
	/**
	 * QALD-9 Parser for file qald-9-train-multilingual.json
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald9File(String fileDirectory, String sourceString, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		System.out.println("Reading file " + fileDirectory + "...");
		String fileContents = FileManager.readWholeFile(fileDirectory);
		System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			Question question = new Question();
			question.setDatabase(endpoint);
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONObject currentQuestionObject = arr.getJSONObject(i);
			JSONArray objectArr =currentQuestionObject.getJSONArray("question"); 
		    // Finding question in English
			for(int j = 0; j < objectArr.length(); ++j) {
		    	JSONObject currentQuestionLanguageObject = objectArr.getJSONObject(j);
		    	if(currentQuestionLanguageObject.getString("language").compareTo("en") == 0) {
		    		question.setQuestionString(currentQuestionLanguageObject.getString("string"));
		    		System.out.println("Question " + i + ": " + question.getQuestionString());
		    		break;
		    	}
		    }
			// The question's SPARQL Query
			question.setQuestionQuery(currentQuestionObject.getJSONObject("query").getString("sparql"));
			// The question's answers
			JSONArray answersArray = currentQuestionObject.getJSONArray("answers");
			for(int j = 0; j < answersArray.length(); ++j) {
				if(answersArray.getJSONObject(j).getJSONObject("results").isEmpty()) {
					if(answersArray.getJSONObject(j).getBoolean("boolean") == true) {
						question.addAnswer("Yes");
					}
					else {
						question.addAnswer("No");
					}
					
				}
				else {
					JSONArray resultsArray = answersArray.getJSONObject(j).getJSONObject("results").getJSONArray("bindings");
					for(int k = 0; k < resultsArray.length(); ++ k) {
						Set<String> keysSet = resultsArray.getJSONObject(k).keySet();
						Iterator<String> it = keysSet.iterator();
						question.addAnswer(resultsArray.getJSONObject(k).getJSONObject(it.next()).getString("value"));
					}
				}
				
			}
			questionsList.add(question);
		}
		return questionsList;
	}
	
	/**
	 * QALD-8 Parser for file {qald-8-train-multilingual,
	 * 							qald-8-test-multilingual.json}
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald8File(String fileDirectory, String sourceString, String endpoint) {
		return parseQald9File(fileDirectory, sourceString, endpoint);
	}
	/**
	 * QALD-7 Parser for files: {qald-7-train-multilingual-extended-json.json}
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald7File1(String fileDirectory, String sourceString, boolean print, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		if(print)
			System.out.println("Reading file " + fileDirectory + "...");
		
		String fileContents = FileManager.readWholeFile(fileDirectory);
		
		if(print)
			System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			Question question = new Question();
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONObject currentQuestionObject = arr.getJSONObject(i);
			JSONObject questionObject =currentQuestionObject.getJSONObject("question");
			JSONArray languageArr = questionObject.getJSONArray("language");
			for(int j = 0; j < languageArr.length(); ++j) {
				if(languageArr.getJSONObject(j).getString("language").compareTo("en") == 0) {
					question.setQuestionQuery(languageArr.getJSONObject(j).getString("SPARQL"));
					question.setQuestionString(languageArr.getJSONObject(j).getString("question"));
					if(print)
						System.out.println("Question " + i + ": " + question.getQuestionString());
					break;
				}
			}
			JSONObject answers = questionObject.getJSONObject("answers");

			JSONObject head = answers.getJSONObject("head");
			
			if(head.isEmpty()) {
				if(answers.getBoolean("boolean") == true) {
					question.addAnswer("Yes");
				}
				else {
					question.addAnswer("No");
				}
			}
			else {
				JSONArray varsArray = head.getJSONArray("vars");
				String type = (String) varsArray.get(0);
				JSONObject results = answers.getJSONObject("results");
				JSONArray bindingsArray = results.getJSONArray("bindings");
				for(int j = 0; j < bindingsArray.length(); ++j) {
					question.addAnswer(bindingsArray.getJSONObject(j).getJSONObject(type).getString("value"));
				}
			}
			questionsList.add(question);
		}
		return questionsList;
	}
	
	/**
	 * QALD-7 Parser for files: {qald-7-test-multilingual.json,
	 * 							qald-7-train-largescale.json,
	 * 							qald-6-train-multilingual.json}
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald7File2(String fileDirectory, String sourceString, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		System.out.println("Reading file " + fileDirectory + "...");
		String fileContents = FileManager.readWholeFile(fileDirectory);
		System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			Question question = new Question();
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONObject currentQuestionObject = arr.getJSONObject(i);
			String type = currentQuestionObject.getString("answertype");
			JSONArray questionArray =currentQuestionObject.getJSONArray("question");
			for(int j = 0; j < questionArray.length(); ++j) {
				if(questionArray.getJSONObject(j).getString("language").compareTo("en") == 0) {
					question.setQuestionString(questionArray.getJSONObject(j).getString("string"));
					System.out.println("Question " + i + ": " + question.getQuestionString());
					break;
				}
			}
			// This case checks if there's no query in the data set
			// This is encountered in the qald-6-train-multilingual.json file
			if(currentQuestionObject.getJSONObject("query").isEmpty()) {
				System.out.println("No query detected. Skipping this one!");
				continue;
			}
			question.setQuestionQuery(currentQuestionObject.getJSONObject("query").getString("sparql"));
			JSONArray answersArray = currentQuestionObject.getJSONArray("answers");
			if(type.compareTo("boolean") == 0) {
				if(answersArray.getJSONObject(0).getBoolean(type) == true) {
					question.addAnswer("Yes");
				}
				else {
					question.addAnswer("No");
				}
			}
			else {
				for(int j = 0; j < answersArray.length(); ++j) {
					JSONObject answer = answersArray.getJSONObject(j);
					JSONObject head = answer.getJSONObject("head");
					JSONArray varsArray = head.getJSONArray("vars");
					ArrayList<String> variables = new ArrayList<String>();
					for(int k = 0; k < varsArray.length(); ++k) {
						variables.add(varsArray.getString(k));
					}
					JSONObject results = answer.getJSONObject("results");
					
					JSONArray bindingsArray = results.getJSONArray("bindings");
					for(int k = 0; k < bindingsArray.length(); ++k) {
						String answerString = "";
						for(int l = 0; l < variables.size(); ++l) {
							answerString += bindingsArray.getJSONObject(k).getJSONObject(variables.get(l)).getString("value");
							if(l != variables.size()-1) {
								answerString += ",";
							}
						}
						question.addAnswer(answerString);
					}
				}
			}
			questionsList.add(question);
		}
		return questionsList;
	}
	/**
	 * QALD-7 Parser for files: {qald-7-train-hybrid-extended-json.json,
	 * 							./data/original/QALD-master/7/data/qald-7-train-multilingual-extended-json.json
	 * }
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald7File3(String fileDirectory, String sourceString, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		System.out.println("Reading file " + fileDirectory + "...");
		String fileContents = FileManager.readWholeFile(fileDirectory);
		System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			Question question = new Question();
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONObject currQuestion = arr.getJSONObject(i).getJSONObject("question");
			
			for(int j = 0; j < currQuestion.getJSONArray("language").length(); j++) {
				if(currQuestion.getJSONArray("language").getJSONObject(j).getString("language").compareTo("en") == 0) {
					try {
						//System.out.print(currQuestion.getJSONArray("language").getJSONObject(j).getString("question"));
						if(currQuestion.getJSONArray("language").getJSONObject(j).getString("SPARQL").isEmpty()) {
							System.out.println("No query detected. Skipping this one!");
							continue;
						}
					}
					catch(Exception e){
						System.out.println("No query detected. Skipping this file!");
						return questionsList;
					}
					question.setQuestionString(currQuestion.getJSONArray("language").getJSONObject(j).getString("question"));
				}	
			}
			//Get answers
			ArrayList<String> answers = new ArrayList<String>();
			if(currQuestion.getString("answertype").compareTo("resource") == 0) {
				for(int j = 0; j < currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("uri").getString("value"));
				}
			}
			else if(currQuestion.getString("answertype").compareTo("boolean") == 0) {
				if(currQuestion.getJSONObject("answers").getBoolean("boolean"))
					answers.add("true");
				else
					answers.add("false");
			}
			else if(currQuestion.getString("answertype").compareTo("date") == 0) {
				for(int j = 0; j < currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("date").getString("value"));
				}
			}
			else if(currQuestion.getString("answertype").compareTo("number") == 0) {
				for(int j = 0; j < currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("c").getString("value"));
				}
			}
			else if(currQuestion.getString("answertype").compareTo("string") == 0) {
				for(int j = 0; j < currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(currQuestion.getJSONObject("answers").getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("string").getString("value"));
					}
				}
			question.setAnswers(answers);
			questionsList.add(question);
		}
		return questionsList;
	}
	/**
	 * QALD-7 Parser for files: {qald-7-train-hybrid.json,
	 * 			
	 * }
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald7File4(String fileDirectory, String sourceString, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		System.out.println("Reading file " + fileDirectory + "...");
		String fileContents = FileManager.readWholeFile(fileDirectory);
		System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			Question question = new Question();
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONArray currQuestion = arr.getJSONObject(i).getJSONArray("question");
			
			for(int j = 0; j < currQuestion.length(); j++) {
					try {
						//System.out.print(currQuestion.getJSONArray("language").getJSONObject(j).getString("question"));
						if(arr.getJSONObject(i).getJSONObject("query").getString("pseudo").isEmpty()) {
							System.out.println("No query detected. Skipping this one!");
							continue;
						}
					}
					catch(Exception e){
						System.out.println("No query detected. Skipping this file!");
						return questionsList;
					}
					question.setQuestionString(currQuestion.getJSONObject(j).getString("string"));
			
			}
			//Get answers

			ArrayList<String> answers = new ArrayList<String>();
			if(arr.getJSONObject(i).getString("answertype").compareTo("resource") == 0) {
				for(int j = 0; j < arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("uri").getString("value"));
				}
			}
			
			else if(arr.getJSONObject(i).getString("answertype").compareTo("boolean") == 0) {
				if(arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getBoolean("boolean"))
					answers.add("true");
				else
					answers.add("false");
			}
			else if(arr.getJSONObject(i).getString("answertype").compareTo("date") == 0) {
				for(int j = 0; j < arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("date").getString("value"));
				}
			}
			else if(arr.getJSONObject(i).getString("answertype").compareTo("number") == 0) {
				for(int j = 0; j < arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("c").getString("value"));
				}
			}
			else if(arr.getJSONObject(i).getString("answertype").compareTo("string") == 0) {
				for(int j = 0; j < arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").length(); j++) {
					answers.add(arr.getJSONObject(i).getJSONArray("answers").getJSONObject(0).getJSONObject("results").getJSONArray("bindings").getJSONObject(j).getJSONObject("string").getString("value"));
					}
				}
			question.setAnswers(answers);
			questionsList.add(question);
		}
		return questionsList;
	}
	/**
	 * QALD-5 Parser for files: {qald-5_train.json}
	 * 
	 * @param fileDirectory the path to the file to be parsed
	 * @param sourceString the string describing the source associated with these question
	 * @return ArrayList<Question> the list of questions parsed from file
	 */
	public static ArrayList<Question> parseQald5(String fileDirectory, String sourceString, String endpoint) {
		ArrayList<Question> questionsList = new ArrayList<Question>();
		System.out.println("Reading file " + fileDirectory + "...");
		String fileContents = FileManager.readWholeFile(fileDirectory);
		System.out.println("Parsing file...");
		JSONObject obj = new JSONObject(fileContents);
		JSONArray arr = obj.getJSONArray("questions");
		for (int i = 0; i < arr.length(); i++) {
			// Skip the hybrid questions 
			if(arr.getJSONObject(i).getString("hybrid").compareTo("true") == 0)
				continue;
			Question question = new Question();
			// The source is predefined
			question.setQuestionSource(sourceString);
			question.setDatabase(endpoint);
			JSONObject currentQuestionObject = arr.getJSONObject(i);
			String type = currentQuestionObject.getString("answertype");
			JSONArray bodyArray =currentQuestionObject.getJSONArray("body");
			for(int j = 0; j < bodyArray.length(); ++j) {
				if(bodyArray.getJSONObject(j).getString("language").compareTo("en") == 0) {
					question.setQuestionString(bodyArray.getJSONObject(j).getString("string"));
					System.out.println("Question " + i + ": " + question.getQuestionString());
					break;
				}
			}
			question.setQuestionQuery(currentQuestionObject.getString("query"));
			JSONArray answersArray = currentQuestionObject.getJSONArray("answers");
			for(int j = 0; j < answersArray.length(); ++j) {
				if(type.compareTo("boolean") == 0) {
					if(answersArray.getJSONObject(j).getString("string").compareTo("true") == 0) {
						question.addAnswer("Yes");
					}
					else {
						question.addAnswer("No");
					}
				}
				else {
					question.addAnswer(answersArray.getJSONObject(j).getString("string"));
				}
			}
			questionsList.add(question);
		}
		return questionsList;
	}
}
