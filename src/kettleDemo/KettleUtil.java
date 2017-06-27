package kettleDemo;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

public class KettleUtil {

	/**
	 * ����trans�ļ�
	 * 
	 * @param transFileName
	 * @throws Exception
	 */
	public static void callNativeTrans(String transFileName) throws Exception {
		callNativeTransWithParams(null, transFileName);
	}

	/**
	 * ����trans�ļ� ��������
	 * 
	 * @param params
	 * @param transFileName
	 * @throws Exception
	 */
	public static void callNativeTransWithParams(String[] params, String transFileName) throws Exception {
		// ��ʼ��
		KettleEnvironment.init();
		EnvUtil.environmentInit();
		TransMeta transMeta = new TransMeta(transFileName);
		// ת��
		Trans trans = new Trans(transMeta);
		// ִ��
		trans.execute(params);
		// �ȴ�����
		trans.waitUntilFinished();
		// �׳��쳣
		if (trans.getErrors() > 0) {
			throw new Exception("There are errors during transformation exception!(��������з����쳣)");
		}
	}

	/**
	 * ����job�ļ�
	 * 
	 * @param jobName
	 * @throws Exception
	 */
	public static void callNativeJob(String jobName) throws Exception {
		// ��ʼ��
		KettleEnvironment.init();

		JobMeta jobMeta = new JobMeta(jobName, null);
		Job job = new Job(null, jobMeta);
		// ��Job �ű����ݲ������ű��л�ȡ����ֵ��${������}
		// job.setVariable(paraname, paravalue);
		job.start();
		job.waitUntilFinished();
		if (job.getErrors() > 0) {
			throw new Exception("There are errors during job exception!(ִ��job�����쳣)");
		}

	}
}
