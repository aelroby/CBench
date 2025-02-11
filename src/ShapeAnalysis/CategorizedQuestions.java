package ShapeAnalysis;

import DataSet.Benchmark;
import DataSet.DataSetPreprocessing;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import qa.dataStructures.Question;
import java.io.StringWriter;
 
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
 

public class CategorizedQuestions {

    ArrayList<Question> singleShape_Qs = new ArrayList<Question>();
    ArrayList<Question> chain_Qs = new ArrayList<Question>();
    ArrayList<Question> chainSet_Qs = new ArrayList<Question>();
    ArrayList<Question> star_Qs = new ArrayList<Question>();
    ArrayList<Question> tree_Qs = new ArrayList<Question>();
    ArrayList<Question> forest_Qs = new ArrayList<Question>();
    ArrayList<Question> cycle_Qs = new ArrayList<Question>();
    ArrayList<Question> flower_Qs = new ArrayList<Question>();
    ArrayList<Question> flowerSet_Qs = new ArrayList<Question>();

    public CategorizedQuestions() {
        ArrayList qs = DataSetPreprocessing.getQueriesWithoutDuplicates(Benchmark.WebQuestions);
        for (Question q : DataSetPreprocessing.questionsWithoutDuplicates) {
            String queryString = q.getQuestionQuery();
            try {
                Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
                String current = query.toString();
                if (QueryShapeType.isSingleEdge(current)) {
                    singleShape_Qs.add(q);
                }
                if (QueryShapeType.isChain(current)) {
                    chain_Qs.add(q);
                }
                if (QueryShapeType.isChainSet(current)) {
                    chainSet_Qs.add(q);
                }
                if (QueryShapeType.isCycle(current)) {
                    cycle_Qs.add(q);
                }
                if (QueryShapeType.isFlower(current)) {
                    flower_Qs.add(q);
                }
                if (QueryShapeType.isFlowerSet(current)) {
                    flowerSet_Qs.add(q);
                }
                if (QueryShapeType.isForest(current)) {
                    forest_Qs.add(q);
                }
                if (QueryShapeType.isStar(current)) {
                    star_Qs.add(q);
                }
                if (QueryShapeType.isTree(current)) {
                    tree_Qs.add(q);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        CategorizedQuestions categories = new CategorizedQuestions();
        System.out.println();
        
        PrintWriter writer;
        try {
            writer = new PrintWriter("1- Qald - 9-- Single Edge questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.singleShape_Qs));
            writer.close();
            
            writer = new PrintWriter("2- Qald - 9-- Chain questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.chain_Qs));
            writer.close();
            
            writer = new PrintWriter("3- Qald - 9-- Chain set questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.chainSet_Qs));
            writer.close();
           
            writer = new PrintWriter("4- Qald - 9-- Star questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.star_Qs));
            writer.close();
            
            writer = new PrintWriter("5- Qald - 9-- Tree questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.tree_Qs));
            writer.close();
            
            writer = new PrintWriter("6- Qald - 9-- Forest questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.forest_Qs));
            writer.close();
            
            writer = new PrintWriter("7- Qald - 9-- Cycle questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.cycle_Qs));
            writer.close();
            
             
            writer = new PrintWriter("8- Qald - 9-- Flower questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.flower_Qs));
            writer.close();
            
            writer = new PrintWriter("9- Qald - 9-- Flowwerset questions.xml", "UTF-8");
            writer.println(categories.printQuestionsAsXML(categories.flowerSet_Qs));
            writer.close();
            
            
        } catch (FileNotFoundException ex) {
        } catch (UnsupportedEncodingException ex) {
        }
        
    }

    String printQuestionsAsXML(ArrayList<Question> qs) {
        String file = ("<questions>"  + "\n");
        int id = 0;
        for (Question q : qs) {
            id++;
            q.setId(id);
            file += jaxbObjectToXML(q);
        }
        file += ("</questions>"  + "\n");
        return file;
    }

    
    private static String jaxbObjectToXML(Question question) 
    {
        try
        {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Question.class);
             
            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
 
            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
 
            //Print XML String to Console
            StringWriter sw = new StringWriter();
             
            //Write XML to StringWriter
            jaxbMarshaller.marshal(question, sw);
             
            //Verify XML Content
            String xmlContent = sw.toString();
            return xmlContent;
 
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }
    
}
