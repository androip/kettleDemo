package kettleDemo;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesScript;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

public class TransDemo {
	
	public static TransDemo transDemo;

     /**

      * �������еı���

      */

     public static String bjdt_tablename = "T_USER";

     public static String kettle_tablename = "T_USER";


     /**

      * ���ݿ�������Ϣ,������DatabaseMeta���� һ��������DatabaseMeta(String xml)

      */

      public static final String[] databasesXML = {

             "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +

               "<connection>" +

                 "<name>bjdt</name>" +

                 "<server>192.168.1.101</server>" +

                 "<type>Oracle</type>" +

                 "<access>Native</access>" + 

                 "<database>orcl</database>" +

                 "<port>1521</port>" +

                 "<username>bjdtuser</username>" +

                 "<password>password</password>" +

               "</connection>",

               "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +

               "<connection>" +

                 "<name>kettle</name>" +

                 "<server>192.168.1.101</server>" +

                 "<type>Oracle</type>" +

                 "<access>Native</access>" + 

                 "<database>orcl</database>" +

                 "<port>1521</port>" +

                 "<username>kettleuser</username>" +

                 "<password>password</password>" +

               "</connection>"

         };   
      
      /**

       * @param args

       */

      public static void main(String[] args) {

          try {

              KettleEnvironment.init();

              transDemo = new TransDemo();

              TransMeta transMeta = transDemo.generateMyOwnTrans();

              String transXml = transMeta.getXML();

              //System.out.println("transXml:"+transXml);

              String transName = "etl/update_insert_Trans.ktr";

              File file = new File(transName);

              FileUtils.writeStringToFile(file, transXml, "UTF-8");

               

          //   
   System.out.println(databasesXML.length+"\n"+databasesXML[0]+"\n"+databasesXML[1]);

          } catch (Exception e) {

              e.printStackTrace();

              return;

          }
      }
      
      /**

       * ����һ��ת��,��һ�����ݿ��е�����ת�Ƶ���һ�����ݿ���,ֻ����������,��һ���Ǳ�����,�ڶ����Ǳ��������²���

       * @return

       * @throws KettleXMLException 

       */

      public TransMeta generateMyOwnTrans() throws KettleXMLException{

           

          System.out.println("************start to generate my own transformation***********");

           

          TransMeta transMeta = new TransMeta();

           

          //����ת�������� 

          transMeta.setName("insert_update");

           

          //���ת�������ݿ�����

          for (int i=0;i<databasesXML.length;i++){

              DatabaseMeta databaseMeta = new DatabaseMeta(databasesXML[i]);

              transMeta.addDatabase(databaseMeta);

          }

           

          //registry�Ǹ�ÿ����������һ����ʶId��

          PluginRegistry registry = PluginRegistry.getInstance();

           

          //******************************************************************

           

          //��һ�������벽��(TableInputMeta)

          TableInputMeta tableInput = new TableInputMeta();

          String tableInputPluginId = registry.getPluginId(StepPluginType.class, tableInput);

          //�����������һ��DatabaseMeta�������ݿ�

          DatabaseMeta database_bjdt = transMeta.findDatabase("bjdt");

          tableInput.setDatabaseMeta(database_bjdt);

          String select_sql = "SELECT ID, USERNAME, PASSWORD, SEX, AGE, TELEPHONE, ADDRESS FROM "+bjdt_tablename;

          tableInput.setSQL(select_sql);

           

          //���TableInputMeta��ת����

          StepMeta tableInputMetaStep = new StepMeta(tableInputPluginId,"table input",tableInput);

           

          //�����������spoon�����е���ʾλ��

          tableInputMetaStep.setDraw(true);

          tableInputMetaStep.setLocation(100, 100);

           

          transMeta.addStep(tableInputMetaStep);

          //******************************************************************

           

          //******************************************************************

          //�ڶ���������������

          InsertUpdateMeta insertUpdateMeta = new InsertUpdateMeta();

          String insertUpdateMetaPluginId = registry.getPluginId(StepPluginType.class,insertUpdateMeta);

          //������ݿ�����

          DatabaseMeta database_kettle = transMeta.findDatabase("kettle");

          insertUpdateMeta.setDatabaseMeta(database_kettle);

          //���ò����ı�

          insertUpdateMeta.setTableName(kettle_tablename);

           

          //����������ѯ�Ĺؼ���

          insertUpdateMeta.setKeyLookup(new String[]{"ID"});

          insertUpdateMeta.setKeyStream(new String[]{"ID"});

          insertUpdateMeta.setKeyStream2(new String[]{""});//һ��Ҫ����

          insertUpdateMeta.setKeyCondition(new String[]{"="});

           

          //����Ҫ���µ��ֶ�

          String[] updatelookup = {"ID","USERNAME","PASSWORD","SEX","AGE","TELEPHONE","ADDRESS"} ;

           String [] updateStream = {"ID","USERNAME","PASSWORD","SEX","AGE","TELEPHONE","ADDRESS"};

           Boolean[] updateOrNot = {false,true,true,true,true,true,true};

           insertUpdateMeta.setUpdateLookup(updatelookup);

          insertUpdateMeta.setUpdateStream(updateStream);

          insertUpdateMeta.setUpdate(updateOrNot);

          String[] lookup = insertUpdateMeta.getUpdateLookup();

          //System.out.println("******:"+lookup[1]);

          //System.out.println("insertUpdateMetaXMl:"+insertUpdateMeta.getXML());

          //��Ӳ��赽ת����

          StepMeta insertUpdateStep = new StepMeta(insertUpdateMetaPluginId,"insert_update",insertUpdateMeta);

          insertUpdateStep.setDraw(true);

          insertUpdateStep.setLocation(250,100);

          transMeta.addStep(insertUpdateStep);

          //******************************************************************

           

          //******************************************************************

          //���hop�����������������

          transMeta.addTransHop(new TransHopMeta(tableInputMetaStep, insertUpdateStep));

          System.out.println("***********the end************");
          

          return transMeta;

      }
      
}
