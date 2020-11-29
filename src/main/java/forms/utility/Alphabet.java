package forms.utility;

import java.util.HashMap;

public class Alphabet {
	
	
	private static final char[] alphabet = {
			'a','b','c','d','e','f','g','h','i','j','k','l','m',
			'n','o','p','q','r','s','t','u','v','w','x','y','z',
			'0','1','2','3','4','5','6','7','8','9','.','-','_',
			' ','@'
	};
	
	private static final HashMap<Integer, Character> decMap = new HashMap<>(41);
	private static final HashMap<Character, Integer> encMap = new HashMap<>(41);
	
	public Alphabet() {
		/* do nothing */
	}
	
	
	
	public static String encrypt(String clearStr) {
		StringBuilder sb = new StringBuilder();
		int[] primes = genPrimes();
		// int[] output = new int[clearStr.length()];
		// create the encryption map
		for(int i = 0; i < alphabet.length; i++) {
			encMap.put(alphabet[i], primes[i]);
		}
		
		sb.append("{");
		
		for(int i = 0; i < clearStr.length(); i++) {

			System.out.println("if char at: " + i + " is null: " + encMap.get(clearStr.toLowerCase().charAt(i)));
			// System.out.println("or if result is null: ");

			System.out.println(encMap.get(clearStr.toLowerCase().charAt(i)) * (i % 10 + 1));
			sb.append( encMap.get(clearStr.toLowerCase().charAt(i)) * (i % 10 + 1) );
			if(i < clearStr.length() - 1) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	public static String decrypt(String encryptNum) {
		int[] primes = genPrimes();
		StringBuilder sb = new StringBuilder();
		String[] strAry = encryptNum.split(",");
		
		
		// create decrypt map
		for(int i = 0; i < alphabet.length; i++) {
			decMap.put(primes[i], alphabet[i]);
		}
	
		int num;
		for(int i = 0; i < strAry.length; i++) {
			num = Integer.parseInt(strAry[i].trim());
			//System.out.println("num: " + (num / ( i % 10 + 1)));
			
			char c = decMap.get(num / (i % 10 + 1));
			sb.append(c);
		}
		
		return sb.toString();
		
	}
	
	
	
	
	
	public static int[] genPrimes() {
		
		int num =0;
		//Empty String
		int[]  primeNumbers = new int[41];
		
		for (int i = 100, j = 0; j < 41; i++)
		{
			int counter=0;
			for(num =i; num>=1; num--)
			{
				if(i%num==0)
				{
					counter = counter + 1;
				}
			}
			if (counter ==2)
			{
				//Appended the Prime number to the String
				primeNumbers[j] = i;
				j++;
			}
		}
		return primeNumbers;
	}
	
	
}
