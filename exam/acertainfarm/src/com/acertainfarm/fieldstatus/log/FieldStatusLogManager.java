package com.acertainfarm.fieldstatus.log;

/**
 * Created by tudorgk on 17/1/15.
 */
import com.acertainfarm.data.Event;
import com.acertainfarm.data.LogRecord;

import java.io.*;
import java.util.List;

public class FieldStatusLogManager {

    protected long lastLogRecordID = 0;
    protected LogRecord lastLogRecord = null;
    protected String logFilePath = null;
    protected File oFile = null ;
    protected BufferedReader oReader = null;
    protected BufferedWriter oWriter = null;

    public FieldStatusLogManager(String logFilePath) throws IOException {

        this.logFilePath = logFilePath;

        //open the file
        oFile = new  File(logFilePath);
        if (!oFile.exists()) {
            oFile.createNewFile();
        }

        oReader = new BufferedReader(new FileReader(logFilePath));
        oWriter = new BufferedWriter(new FileWriter(logFilePath,true));

       lastLogRecord = getLastLogRecord();
        if (lastLogRecord == null){
            lastLogRecord = new LogRecord(0,"-",0,"-");
            lastLogRecordID = 0;
        }
        lastLogRecordID = lastLogRecord.getLogSN();
    }

    public synchronized String getContents() {

        try {
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

    public synchronized LogRecord getLastLogRecord(){
        try {
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
                long timePeriod = Long.parseLong(result[1]);
                String action = result[2];
                String modified_data = result[3];
                return new LogRecord(lsn,action,timePeriod,modified_data);
            }else{
                //no log record present
                return null;
            }

        }
        catch (IOException oException) {
            throw new IllegalArgumentException("Invalid file path/File cannot be read: \n" + logFilePath);
        }
    }



    public synchronized void flushToFileLog(String action, long timePeriod, List<Event> events ) throws IOException {

            if (oFile.canWrite()) {

                for (Event ev : events){
                    LogRecord record = new LogRecord(++lastLogRecordID, action,timePeriod, ev.toString());
                    oWriter.write(record.toString());
                    lastLogRecord = record;
                }

                //add a checkpoint on finished update
                oWriter.write(new LogRecord(++lastLogRecordID,"CHECKPOINT",timePeriod,"-").toString());

                oWriter.flush();

            }


    }


    public synchronized void closeFile(){
        try {
            oWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}