package kettleDemo;

import java.io.File;
import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;

public class GetTrans {
	
	
	public static void main(String[] args) throws KettleException {
		 DataBase fromdb = new DataBase();
		 DataBase todb = new DataBase();
		 fromdb.getdatabase("from");
		 todb.getdatabase("to");
		 
		try {
			getTrans(fromdb,todb);
		} catch (KettleXMLException e) {
			e.printStackTrace();
		} catch (KettleMissingPluginsException e) {
			e.printStackTrace();
		}
	}

	public static void getTrans(DataBase fromdb,DataBase todb) throws KettleException {
		try {
			KettleEnvironment.init();
		} catch (KettleException e) {
			e.printStackTrace();
		}
		//�õ���Ҫ���ص�ת���ļ�
		String filename = fromdb.getFilename();
		String path = "./"+filename;
		System.out.println("��ǰ�ļ�����·����"+new File(path).getAbsolutePath());
		TransMeta tm = new TransMeta(path);

		// �滻����������ݿ�
		DatabaseMeta inputDataBase = new DatabaseMeta(fromdb.getName(), 
														fromdb.getType(),
														fromdb.getAccess(), 
														fromdb.getHost(),
														fromdb.getDb(), 
														fromdb.getPort(), 
														fromdb.getUser(),
														fromdb.getPass());
		
		DatabaseMeta outputDataBase = new DatabaseMeta(todb.getName(), 
														todb.getType(),
														todb.getAccess(),
														todb.getHost(), 
														todb.getDb(), 
														todb.getPort(), 
														todb.getUser(),
														todb.getPass());
		tm.getDatabase(0).replaceMeta(outputDataBase);
		tm.getDatabase(1).replaceMeta(inputDataBase);
		//�õ�ת��
		Trans trans = new Trans(tm);
		//������������
		/*Scanner inputStr = new Scanner(System.in);
		System.out.println("����Ӧ��ID");
		String appid = inputStr.nextLine();
		System.out.println("����Ȩ����������");
		String authorType = inputStr.nextLine();
		if(!authorType.trim().equals("")&&(authorType.equals("200")||authorType.equals("100"))){
			trans.setVariable("param-authortype", authorType);
		}else{
			System.out.println("����ȷ����Ȩ������");
			authorType = inputStr.nextLine();
		}
		inputStr.close();
		trans.setVariable("param-appid", appid);*/
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
