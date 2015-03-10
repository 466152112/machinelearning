package tool.NLPIWordTool;

public class TestNLPIR {

	public static void main(String[] args) throws Exception
	{
		try
		{
			String sInput = "�Ż�ƽ�Ƴ���NLPIR�ִ�ϵͳ������ICTCLAS2013�������´�ʶ�𡢹ؼ����ȡ��΢���ִʹ��ܡ�";

			//����Ӧ�ִ�
			test(sInput);		
			
		}
		catch (Exception ex)
		{
		} 


	}

	public static void test(String sInput)
	{
		try
		{
			NLPIR testNLPIR = new NLPIR();
	
			String argu = "ICTCLAS2014";
			System.out.println("NLPIR_Init");
			if (testNLPIR.NLPIR_Init(argu.getBytes("GB2312"),1) == false)
			{
				System.out.println("Init Fail!");
				return;
			}
			
					//�����û��ʵ�ǰ
			byte nativeBytes[] = testNLPIR.NLPIR_ParagraphProcess(sInput.getBytes("GB2312"), 1);
			String nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
	
			System.out.println("�ִʽ��Ϊ�� " + nativeStr);

	
			
			//��ʼ���ִ����
			String argu1 = "E:/NLPIR/test/test.TXT";
			String argu2 = "E:/NLPIR/test/test_result1.TXT";
		
			nativeBytes  =testNLPIR.NLPIR_GetFileNewWords(argu1.getBytes("GB2312"),50,true);
			//����Ǵ����ڴ棬���Ե���testNLPIR.NLPIR_GetNewWords
			nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			System.out.println("�´�ʶ����Ϊ�� " + nativeStr);
			
			nativeBytes  =testNLPIR.NLPIR_GetFileKeyWords(argu1.getBytes("GB2312"),50,true);
			//����Ǵ����ڴ棬���Ե���testNLPIR.NLPIR_GetKeyWords
			nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
			System.out.println("�ؼ��ʶ����Ϊ�� " + nativeStr);
			
			
			
			testNLPIR.NLPIR_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 1);
	
			testNLPIR.NLPIR_NWI_Start();
			testNLPIR.NLPIR_NWI_AddFile(argu1.getBytes("GB2312"));
			
			testNLPIR.NLPIR_NWI_Complete();
			
			nativeBytes= testNLPIR.NLPIR_NWI_GetResult(true);
			nativeStr = new String(nativeBytes, 0, nativeBytes.length, "GB2312");
	
			System.out.println("�´�ʶ���� " + nativeStr);
		
			testNLPIR.NLPIR_NWI_Result2UserDict();//�´�ʶ����
			argu2 = "E:/NLPIR/test/test_result2.TXT";
			testNLPIR.NLPIR_FileProcess(argu1.getBytes("GB2312"), argu2.getBytes("GB2312"), 1);
	
			testNLPIR.NLPIR_Exit();
		}
		catch (Exception ex)
		{
		} 
}
}
 
