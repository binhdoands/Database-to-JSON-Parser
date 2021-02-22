
package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import net.sf.json.JSONArray;
import org.json.simple.*;
import org.json.simple.parser.*;


public class Converter {
    
    /*
    
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and
        other whitespace have been added for clarity).  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings, and which values should be encoded as integers!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160",
            "111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
    
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
    
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including example code.
    
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String results = "";
        
        
        try {
            
            /* Create Stringbuilder for JSON*/
            StringBuilder json= new StringBuilder();
            
            //BufferedReader reader = new BufferedReader(new FileReader("grades.csv"));
            CSVReader reader = new CSVReader(new FileReader("grades.csv"));
            List<String[]> csv= reader.readAll();
            Iterator<String[]> iterator = csv.iterator();
            
            
            
            
            JSONArray rHeaders= new JSONArray();
            JSONArray cHeaders= new JSONArray();
            JSONArray data = new JSONArray();
            String[] rows;
            
             /* Get first String[] array from CSV data (the column headers); add elements to "cHeaders" */
            
            cHeaders.addAll(Arrays.asList(iterator.next()));

            /* Iterate through remaining CSV rows */

            while (iterator.hasNext()){
                
                /* Create container for next row */
                
                JSONArray row = new JSONArray();
                
                /* Get next String[] array from CSV data */
                
                rows = iterator.next();
                
                /* Get first element (the row header); add element to "rHeaders" */
                
                rHeaders.add(rows[0]);
                
                /* Add remaining elements to "data" */
                
                for (int i = 1; i < rows.length; i++){
                    row.add(rows[i]);
                }
                
                /* Add row to "data" */
                
                data.add(row);
            
            }
            /* Construct JSON string (remember, this must be an *exact* match for the example!) */
            
            /* Add column and row headers */

            json.append("{\n    \"colHeaders\":").append(cHeaders.toString());
            json.append(",\n    \"rowHeaders\":").append(rHeaders.toString()).append(",\n");
            
            /* Split "data" rows */
            
            rows = data.toString().split("],");
            
            /* Add data */

            json.append("    \"data\":");

            for (int i = 0; i < rows.length; ++i){
                
                String s = rows[i];         /* Get next data row */

                s = s.replace("\"","");     /* Delete double-quotes */
                s = s.replace("]]","]");    /* Fix terminating square brackets */
                
                json.append(s);             /* Append row */
                
                 /* If this is not the last data row, close the row and begin a new one */
                
                if ((i % rows.length) != (rows.length - 1))
                    json.append("],\n            ");
                
            }
            
            /* Close JSON string */
            
            json.append("\n    ]\n}");
            
            
            
        
            }
        catch(CsvException | IOException e) { return e.toString(); }
        
        return results.trim();
        
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String results = "";
        
        try {

            StringWriter writer = new StringWriter();
            CSVReader reader = new CSVReader(new FileReader("grades.json"));
            
            
             /* Create json-simple JSON Parser */
            
            JSONParser parser = new JSONParser();
            
            /* Parse JSON Data */
            
            JSONObject jobject = (JSONObject) parser.parse(jsonString);
            JSONArray col = (JSONArray) jobject.get("colHeaders");
            JSONArray row = (JSONArray) jobject.get("rowHeaders");
            JSONArray data = (JSONArray) jobject.get("data");
            
            /* Create String[] arrays for OpenCSV */
            
            String[] csvcol = new String[col.size()];
            String[] csvrow = new String[row.size()];
            String[] csvdata = new String[data.size()];
            String[] rowdata;
            
            /* Copy column headers */

            for (int i = 0; i < col.size(); i++) {
                csvcol[i] = col.get(i) + "";
            }
            
            /* Copy row headers and row data */

            for (int i = 0; i < row.size(); i++) {
                
                csvrow[i] = row.get(i) + "";
                csvdata[i] = data.get(i) + "";

            }
            
            /* Create OpenCSV Writer */
            //StringWriter writer = new StringWriter();
            //CSVWriter csvWriter;
            CSVWriter csvWriter = new CSVWriter(new FileWriter("grades.json"));
            /* Write column headers */
            //csvWriter = new CSVWriter(writer, ',', '"', '\n');

            csvWriter.writeNext(csvcol);

            /* Write row headers and row data */

            for (int i = 0; i < csvdata.length; i++) {
                
                /* Strip square brackets from next row */
                
                csvdata[i] = csvdata[i].replace("[","");
                csvdata[i] = csvdata[i].replace("]","");
                
                /* Split csvdata[i] into row elements (using comma as delimiter) */

                String[] elements = csvdata[i].split(",");
                
                /* Create String[] container for row data (sized at the number of row elements, plus one for row header) */
                
                rowdata = new String[elements.length + 1];
                
                /* Copy row header into first element of "rowdata" */

                                rowdata[0] = csvrow[i];
                
                /* Copy row elements into remaining elements of "rowdata" */
                System.arraycopy(csvrow, 1, rowdata, 1, csvrow.length - 1);
                                
                
                /* Write new row */
                
                //csvWriter.writeNext(rowdata);
            }
            
        }
        
        catch(IOException | ParseException e) { return e.toString(); }
        
        return results.trim();
        
    }

}