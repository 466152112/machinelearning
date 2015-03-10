package model.link.pageRank.tools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


/*
 * �������������ͼ��?-999 ��500000����
 */
public class RandomData {
	public static void main(String [] arg) throws IOException{
		
		//new RandomData().random1();
		new RandomData().random2();
	}
	
	protected void random2() {
		//���?00����
		final int  edgeNumber=500000;
		
		//���д��data.txt�ļ���
		
		DataWriter dataWriter=new DataWriter("data.txt");
		Random random=new Random();
		random.setSeed(100);
		
		for (int i = 0; i < edgeNumber; i++) {
			int a=random.nextInt(1000);
			int b;
			//�߲�������������ͬ�ڵ�
			while((b=random.nextInt(1000))==a);
			if (dataWriter.write(a+" "+b)) ;
			else {
				System.out.println("failure to write !");
			}
		}
	
	}
	protected void random1() throws IOException{
		//���?00����
		final int  edgeNumber=50000;
		
		//���д��data.txt�ļ���
		
		File file=new File("data.txt");
		FileWriter fw =new FileWriter(file);
		BufferedWriter bufferedWriter=new BufferedWriter(fw);
		Random random=new Random();
		random.setSeed(100);
		
		for (int i = 0; i < edgeNumber; i++) {
			int a=random.nextInt(100);
			int b;
			//�߲�������������ͬ�ڵ�
			while((b=random.nextInt(100))==a);
			bufferedWriter.write(a+" "+b);
			bufferedWriter.newLine();
		}
		bufferedWriter.close();
		fw.close();
	}
}
