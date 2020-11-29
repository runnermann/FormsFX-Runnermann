package authcrypt.user;

import campaign.db.DBFetchToMapAry;
import campaign.db.DBFetchUnique;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.sectiontype.DoubleCellSection;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class holds the methods for a
 * person/student's member account.
 */
public class EncryptedAcct {

	// 0 = account_id
	// 1 = orig_email
	// 2 = account_status
	// 3 = catagory
	// 4 = paid_date
	// 7 = currency  //ie "usd" or "yen"
	// 8 = period // year or month
	// 9 = due_date
	// 10 = fee

	private HashMap<String, String> map = null;
	// THE LOGGER
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptedAcct.class);
	
	public EncryptedAcct() { /* NO ARGS */ }

	/*
	 * checks if user's account exists. If not there
	 * is a problem. Error or bad actor?
	 */
	public boolean exists() {

		if(map == null) {
			// check if user account exists
			getAccountData();
		} else {
			if(map.get("empty").equals("true")) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCurrent() {
		// @TODO isCurrent
		if(map == null) {
			// check if user account exists
			getAccountData();
		} else {
			if(map.get("account_status").equals("disabled")) {
				LOGGER.warn("ACOUNT_DISABLED_WARNING: user <{}> attempted to access a disabled account", Alphabet.encrypt(authcrypt.UserData.getUserName()));
				map.put("isCurrent", "false");
				return false;
			}
			// catagory == freemium
			if (map.get("catagory").equals("free")) {
				// user is a freemium user,
				// has ability to buy with fee. Cannot sell.
				map.put("isCurrent", "false");
				return false;
			}
			// catagroy == premium
			if(map.get("catagory").contains("preem")) {
				if(verify(map.get("paid_date"), map.get("period"), map.get("account_status"))) {
					map.put("isCurrent", "true");
					return true;
				}
				map.put("isCurrent", "false");
				return false;
			}
		}
		map.put("isCurrent", "false");
		return false;
	}

	public double getFee() {
		if(map == null) {
			getAccountData();
		} else if(map.get("isCurrent") == null) {
			isCurrent();
		}
		if(! map.get("isCurrent").equals("true")) {
			return 0;
		} else {
			return Double.parseDouble( map.get("fee"));
		}
	}
	
	public boolean join() {
		// @TODO join
		// UserForm
		// Join Button Action
		// Pay Action
		// Insert Member

		return false;
	}
	
	public boolean remove() {
		// @TODO remove
		return false;
	}
	
	public boolean adminGrant(long timeAction, long duration ) {
		// @TODO adminGrant
		return false;
	}
	
	private void getAccountData() {
		// query db with user email
		String[] args = {authcrypt.UserData.getUserName()};
		ArrayList<HashMap<String, String>> resMap = DBFetchToMapAry.ACCT_DATA.query(args);
		this.map = resMap.get(0);
	}

	/**
	 * Verify if user is current. Determines fees. Should not be used
	 * outside of class.
	 * @param paidDate
	 * @param period
	 * @param status
	 * @return true if user is current
	 */
	private boolean verify(String paidDate, String period, String status) {
		long days = period.equals("year") ? 365 : 31;
		long num = 0;
		if(status.equals("ext_30")) {
			//System.out.println("status contains ext_30" );
			num = 32;
			//return (Long.parseLong(paidDate.strip()) - (num + days * 24 * 60 * 60 * 1000) > 0);
		} else if (status.equals("ext_60")){
			num = 62;
		} else if (status.equals("ext_90")) {
			num = 93;
		}
		long now = System.currentTimeMillis();
		//long addTime = (num + days) * 86400000l;
		//System.out.println("result in days: " + addTime / 86400000l);
		//System.out.println("paidDate: " + Long.parseLong(paidDate.strip()) + ", plus additional time: " + addTime + " = " + (Long.parseLong(paidDate.strip()) + ((num + days) * 24 * 60 * 60 * 1000)));
		//System.out.println(" now: " + now);
		//System.out.println("days: " + days);
		//System.out.println("num: " + num);
		//System.out.println("num + days: " + (num + days));
		return ((Long.parseLong(paidDate.strip()) + ((num + days) * 24 * 60 * 60 * 1000)) > now);
	}

	private String getStatus() {
		// @TODO getStatus
		return "idk ... idk ... idk ...?";
	}
	
	public boolean upgrade() {
		// @TODO upgrade
		return false;
	}
	
	public boolean downgrade() {
		// @TODO downgrade
		return false;
	}
	
	public boolean suspend(long timeAction, long duration) {
		// @TODO suspend
		return false;
	}

	/*public static void main(String[] args) {
		System.out.println("Testing verify()");
		EncryptedAcct acct = new EncryptedAcct();
		String paydate = "1596265200000";
		String period = "31";
		String status = "ext_30";
		boolean bool = acct.verify(paydate, period, status);

		System.out.println(bool + " = acct.verify(paydate, period, status = " + paydate + " " + period + " " + status);
	}
	 */
	
}
