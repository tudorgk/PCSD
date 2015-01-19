package com.acertainfarm.fieldstatus.log;

/**
 * Created by tudorgk on 17/1/15.
 */
import com.acertainfarm.data.LogRecord;

import java.io.*;
import java.util.List;

public class FieldStatusLogManager {

    static int nStatsCount = 0;
    protected static String logFilePath = null;

    public FieldStatusLogManager(String logFilePath) throws IOException {

        this.logFilePath = logFilePath;
        File oFile = new  File(logFilePath);
        if (!oFile.exists()) {
            oFile.createNewFile();
        }
    }

    static public String getContents() {

        try {
            BufferedReader oReader = new BufferedReader(new FileReader(logFilePath));
            String sLine, sContent = "";
            while ((sLine=oReader.readLine()) != null) {
                sContent += (sContent=="")?sLine: ("\r\n"+sLine);
            }
            oReader.close();
            return sContent;
        }
        catch (IOException oException) {
            throw new IllegalArgumentException("Invalid file path/File cannot be read: \n" + logFilePath);
        }
    }

    static public LogRecord getLastLogRecord(){
        try {
            BufferedReader oReader = new BufferedReader(new FileReader(logFilePath));
            String sLine,lastLine= "";
            while ((sLine = oReader.readLine()) != null)
            {
                lastLine = sLine;
            }
            oReader.close();

            if (!lastLine.isEmpty()){
                // get the last log record
                String[] result = lastLine.split(";");
                long lsn = Long.parseLong(result[0]);
                String action = result[1];
                String transaction = result[2];
                String modified_data = result[3];

                return new LogRecord(lsn,action,transaction,modified_data);
            }else{
                //no log record present
                return null;
            }

        }
        catch (IOException oException) {
            throw new IllegalArgumentException("Invalid file path/File cannot be read: \n" + logFilePath);
        }
    }

    public static synchronized void flushToFileLog(List<LogRecord> recordList) {
        try {
            File oFile = new File(logFilePath);
            if (!oFile.exists()) {
                oFile.createNewFile();
            }
            if (oFile.canWrite()) {
                BufferedWriter oWriter = new BufferedWriter(new FileWriter(logFilePath, true));
                for (LogRecord record : recordList)
                {
                    oWriter.write (record.toString());
                }
                //TODO: Remember to write checkpoint
                oWriter.close();
            }

        }
        catch (IOException oException) {
            throw new IllegalArgumentException("Error appending/File cannot be written: \n" + logFilePath);
        }
    }
}