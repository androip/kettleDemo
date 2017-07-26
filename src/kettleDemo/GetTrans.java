package kettleDemo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;

public class GetTrans {
	private static Map<String,String> columnNames = new HashMap<String,String>();
	static{
		columnNames.put("USER_CODE","USER_CODE");
		columnNames.put("ROLE_CODE","ROLE_CODE");
	}
	public static void main(String[] args) throws KettleException {
		DataBase fromdb = new DataBase("from");
    	DataBase todb = new DataBase("to");
		runTrans(fromdb, todb);
	}

	public static void runTrans(DataBase fromdb,DataBase todb) throws KettleException {
		
		try {
			KettleEnvironment.init();
		} catch (KettleException e) {
			e.printStackTrace();
		}
		
		DatabaseMeta inputDataBase = fromdb.getDatabase();
		DatabaseMeta outputDataBase = todb.getDatabase();
		//Database db = new Database(inputDataBase);
		Database db = new Database(null, inputDataBase);
		db.connect();
		String sql = "select * from V_USER_ROLE_EXPORT where  rownum=1";
		db.openQuery(sql);
		RowMetaInterface  idxRowMeta  = db.getReturnRowMeta();
		String[] columns = idxRowMeta.getFieldNames();
		int length = columns.length;
		boolean isCheckPass = true;
		
		String filename = "exportNotime.ktr";
		
		if(length >= 3){
			columnNames.put("LINK_INDATE","LINK_INDATE");
			filename = "export.ktr";
		}
		if(length < 2){
			System.out.println("ȱ���ֶ�");
			isCheckPass = false;
		}else{
			for(String str:idxRowMeta.getFieldNames()){
				String s = columnNames.get(str);
				if(s == null || s.trim().equals("")){
					System.out.println(str+"������ȷ�ֶ�");
					isCheckPass = false;
				}
			}	
		}
		if(isCheckPass){
			//�õ���Ҫ���ص�ת���ļ�
			String path = "./lib/"+filename;
			System.out.println("��ǰ�ļ�����·����"+new File(path).getAbsolutePath());
				
			TransMeta tm = new TransMeta(path);
			
			// �滻����������ݿ�
			for(int i = 0;i < tm.getDatabases().size();i++){
				String tableName = tm.getDatabase(i).getName();
				if(tableName.equals("from")){
					tm.getDatabase(i).replaceMeta(inputDataBase);
				}else if(tableName.equals("to")){
					tm.getDatabase(i).replaceMeta(outputDataBase);
				}
			}
			
			//�õ�ת��
			Trans trans = new Trans(tm);
			//������������
			String appcode = fromdb.getAppcode();
			trans.setVariable("APP_CODE", appcode);
			//Ԥִ�У�����û���õ�steps
			trans.prepareExecution(null);
			//ûʲô�ðɡ���������
			List<StepMetaDataCombi> steps = trans.getSteps();
			for (StepMetaDataCombi s : steps) {
				System.out.println(s.stepname);
			}
			// �������ȴ�ִ�����
			trans.startThreads();
			trans.waitUntilFinished();
		}
	}
	
	 /** 
     * java ���� kettle ��job 
     *  
     * @param jobname 
     *            �磺 String fName= "D:\\kettle\\informix_to_am_4.ktr"; 
     */  
    public static void runJob(String[] params, String jobPath) {  
        try {  
        	
            KettleEnvironment.init();  
            // jobname ��Job�ű���·��������  
            JobMeta jobMeta = new JobMeta(jobPath, null); 
            Job job = new Job(null, jobMeta);  
            // ��Job �ű����ݲ������ű��л�ȡ����ֵ��${������}  
            // job.setVariable(paraname, paravalue);  
            job.start();  
            job.waitUntilFinished();
            int errorNum = job.getErrors();
            System.out.println("errorNum=" + errorNum);
            if(errorNum <= 0){
            	System.out.println("�ɹ�");
            	//runTrans(fromdb,todb);
            }else{
            	System.out.println("У��ûͨ��");
            }
        } catch (Exception e) {
        	System.out.println("������ͼ�ֶβ��Ϸ���");
          //  e.printStackTrace();  
        }  
    }  

}
