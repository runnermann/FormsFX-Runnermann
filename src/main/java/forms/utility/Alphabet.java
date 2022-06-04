package forms.utility;

import java.util.HashMap;

public class Alphabet {

	private static final char[] alphabet = {
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m',
			'n','o','p','q','r','s','t','u','v','w','x','y','z',
			'0','1','2','3','4','5','6','7','8','9','.','-','_',
			' ','@'
	};

	private static int length = alphabet.length;
	
	private static final HashMap<Integer, Character> decMap = new HashMap<>(length);
	private static final HashMap<Character, Integer> encMap = new HashMap<>(length);
	
	public Alphabet() {
		/* do nothing */
	}
	
	
	
	public static String encrypt(String clearTxt) {
		StringBuilder sb = new StringBuilder();
		int[] primes = genPrimes();
		// int[] output = new int[clearTxt.length()];
		// create the encryption map
		for(int i = 0; i < length; i++) {
			encMap.put(alphabet[i], primes[i]);
		}
		
		sb.append("{");
		
		for(int i = 0; i < clearTxt.length(); i++) {
			sb.append( encMap.get(clearTxt.charAt(i)) * (i % 10 + 1) );
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
			// remove leading trailing ary brackets
			// may break later, replaced "[" and "]" with "{" and "}"

			int startNum = encryptNum.indexOf("{");
			int endNum = -1;
			if(startNum != -1) {
				startNum += 1;
				endNum = encryptNum.indexOf("}");
			} else {
				startNum = encryptNum.indexOf("[") + 1;
				endNum = encryptNum.indexOf("]");
			}


			String sub = encryptNum.substring(startNum, endNum);
			int[] primes = genPrimes();
			StringBuilder sb = new StringBuilder();
			String[] strAry = sub.split(",");
			// create decrypt map
			for (int i = 0; i < length; i++) {
				decMap.put(primes[i], alphabet[i]);
			}

			int num;
			for (int i = 0; i < strAry.length; i++) {
				num = Integer.parseInt(strAry[i].trim());

				char c = decMap.get(num / (i % 10 + 1));
				sb.append(c);
			}

			return sb.toString();
		}catch ( Exception e) {

		}

		return null;
	}


	public static int[] genPrimes() {
		int num =0;
		//Empty String
		int[]  primeNumbers = new int[alphabet.length];
		
		for (int i = 100, j = 0; j < alphabet.length; i++) {
			int counter=0;
			for(num =i; num>=1; num--) {
				if(i%num==0) {
					counter = counter + 1;
				}
			}
			if (counter ==2) {
				//Appended the Prime number to the String
				primeNumbers[j] = i;
				j++;
			}
		}
		return primeNumbers;
	}
}
