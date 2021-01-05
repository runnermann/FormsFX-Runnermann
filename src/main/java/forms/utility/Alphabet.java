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
	
	
	
	public static String encrypt(String clearTxt) {
		StringBuilder sb = new StringBuilder();
		int[] primes = genPrimes();
		// int[] output = new int[clearTxt.length()];
		// create the encryption map
		for(int i = 0; i < alphabet.length; i++) {
			encMap.put(alphabet[i], primes[i]);
		}
		
		sb.append("{");
		
		for(int i = 0; i < clearTxt.length(); i++) {

			System.out.println("is char at: " + i + " null: " + clearTxt.charAt(i) + " = " + encMap.get(clearTxt.toLowerCase().charAt(i)));
			// System.out.println("or if result is null: ");

			System.out.println(encMap.get(clearTxt.toLowerCase().charAt(i)) * (i % 10 + 1));
			sb.append( encMap.get(clearTxt.toLowerCase().charAt(i)) * (i % 10 + 1) );
			if(i < clearTxt.length() - 1) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * @param encryptNum
	 * @return
	 */
	public static String decrypt(String encryptNum) {
		try {
			System.out.println("decrypting encryptNum123: " + encryptNum);
			// remove leading trailing ary brackets
			int startNum = encryptNum.indexOf("[") + 1;
			int endNum = encryptNum.indexOf("]");
			String sub = encryptNum.substring(startNum, (endNum));
			int[] primes = genPrimes();
			StringBuilder sb = new StringBuilder();
			String[] strAry = sub.split(",");
			// create decrypt map
			for (int i = 0; i < alphabet.length; i++) {
				decMap.put(primes[i], alphabet[i]);
				System.out.println("decMap( " + primes[i] + ", "+ alphabet[i] + ")")
				;
			}

			int num;
			for (int i = 0; i < strAry.length; i++) {
				num = Integer.parseInt(strAry[i].trim());
				System.out.println("num: " + (num / ( i % 10 + 1)));

				char c = decMap.get(num / (i % 10 + 1));
				sb.append(c);
			}

			System.out.println("result from decrypt: ");
			return sb.toString();
		}catch ( Exception e) {
			System.out.println(" EDRROR" + e.getMessage());
			e.printStackTrace();
		}

		return null;
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
