package ShallowAnalysis;

import DataSet.Benchmark;
import DataSet.DataSetPreprocessing;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import org.apache.jena.query.Query;


public class OperatorDistribution 
{
    ArrayList<Query> qs;
    public static int none = 0, F = 0, A = 0, AF = 0, CPF = 0, 
                                O = 0, OF = 0, AO = 0, AOF = 0, CPF_O = 0,
                                G = 0, CPF_G = 0,
                                U = 0, UF = 0, AU = 0, AUF = 0, CPF_U = 0,
                                FGP = 0, FGU = 0, FAG = 0, AOUF = 0, AOUFG = 0;

    public OperatorDistribution(ArrayList<Query> queries) {
        qs = queries;
    }

    public static void main(String[] args) {
        OperatorDistribution k = new OperatorDistribution(DataSetPreprocessing.getQueriesWithoutDuplicates(Benchmark.QALD_9));
        k.analysis();
    }

    public void analysis() {
        NumberFormat formatter = new DecimalFormat("#0.00");

        for (Query q : qs) {
            boolean FILTER = q.toString().toLowerCase().
                    replace("\n", "").replace("\r", "").replaceAll(" ", "").contains("filter("), 
                    
                    AND = q.toString().toLowerCase().contains(" .")
                    || q.toString().toLowerCase().contains(";"), 
                    
                    OPT = q.toString().toLowerCase().replaceAll(" ", "").
                    replace("\n", "").replace("\r", "").contains("optional{"),
                    
                    GRAPH = q.toString().toLowerCase().replaceAll(" ", "").
                    replace("\n", "").replace("\r", "").contains("graph{"), 
                    
                    UNION = q.toString().toLowerCase().replace("\n", "").replaceAll(" ", "").
                    replace("\n", "").replace("\r", "").contains("union{");
            
            if(FILTER && !AND && !OPT && !GRAPH && !UNION)F++;
            if(!FILTER && AND && !OPT && !GRAPH && !UNION)A++;
            if(FILTER && AND && !OPT && !GRAPH && !UNION)AF++;
            
            if(!FILTER && !AND && OPT && !GRAPH && !UNION)O++;
            if(FILTER && !AND && OPT && !GRAPH && !UNION)OF++;
            if(!FILTER && AND && OPT && !GRAPH && !UNION)AO++;
            if(FILTER && AND && OPT && !GRAPH && !UNION)AOF++;
            
            if(!FILTER && !AND && !OPT && GRAPH && !UNION)G++;
            
            if(!FILTER && !AND && !OPT && !GRAPH && UNION)U++;
            if(FILTER && !AND && !OPT && !GRAPH && UNION)UF++;
            if(!FILTER && AND && !OPT && !GRAPH && UNION)AU++;
            if(FILTER && AND && !OPT && !GRAPH && UNION)AUF++;
            
            if(!FILTER && !AND && !OPT && !GRAPH && !UNION)none++;
            
            
            if(FILTER && !AND && OPT && GRAPH && !UNION)FGP++;
            if(FILTER && !AND && !OPT && GRAPH && UNION)FGU++;
            if(FILTER && AND && !OPT && GRAPH && !UNION)FAG++;
            if(FILTER && AND && OPT && !GRAPH && UNION)AOUF++;
            if(FILTER && AND && OPT && GRAPH && UNION)AOUFG++;
                    
        }
        
        
        CPF = none + A + F + AF;
        CPF_O = O + OF + AO + AOF;
        CPF_G = G;
        CPF_U = U + UF + AU + AUF;
        
        System.out.println("\n\n\nOperator Distribution Analysis ----->");
        String format = "%-30s%-20s%-20s%n";
        System.out.format(format, "Operators"+"\t", "#Queries"+"\t", "Relative%");
        System.out.format(format, "=======", "========", "=========");
        
         System.out.format(format, "None" +"\t", none +"\t",   formatter.format((double) none/qs.size()*100) +"%");
         System.out.format(format, "F" +"\t", F +"\t",   formatter.format((double) F/qs.size()*100) +"%");
         System.out.format(format, "A" +"\t", A +"\t",   formatter.format((double) A/qs.size()*100) +"%");
         System.out.format(format, "A, F" +"\t", AF +"\t",   formatter.format((double) AF/qs.size()*100) +"%");
         System.out.format(format, "CPF" +"\t", CPF +"\t",   formatter.format((double) CPF/qs.size()*100) +"%");
        
         System.out.format(format, "O" +"\t", O +"\t",   formatter.format((double) O/qs.size()*100) +"%");
         System.out.format(format, "O, F" +"\t", OF +"\t",   formatter.format((double) OF/qs.size()*100) +"%");
         System.out.format(format, "A, O" +"\t", AO +"\t",   formatter.format((double) AO/qs.size()*100) +"%");
         System.out.format(format, "A, O, F" +"\t", AOF +"\t",   formatter.format((double) AOF/qs.size()*100) +"%");
         System.out.format(format, "CPF + O " +"\t", CPF_O +"\t",  formatter.format((double) CPF_O/qs.size()*100) +"%");
        
         //System.out.format(format, "G" , G ,   formatter.format((double) G/qs.size()*100) );
         //System.out.format(format, "CPF + G" , CPF_G , formatter.format((double) CPF_G/qs.size()*100) );
        
         System.out.format(format, "U" +"\t", U +"\t",   formatter.format((double) U/qs.size()*100) +"%");
         System.out.format(format, "UF" +"\t", UF +"\t",   formatter.format((double) UF/qs.size()*100) +"%");
         System.out.format(format, "AU" +"\t", AU +"\t",   formatter.format((double) AU/qs.size()*100) +"%");
         System.out.format(format, "AUF" +"\t", AUF +"\t",   formatter.format((double) AUF/qs.size()*100) +"%");
         System.out.format(format, "CPF + U" +"\t", CPF_U +"\t", formatter.format((double) CPF_U/qs.size()*100) +"%");
             
    }


}
