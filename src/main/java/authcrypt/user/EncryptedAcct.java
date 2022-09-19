package authcrypt.user;

import campaign.db.DBFetchToMapAry;
import campaign.db.DBFetchUnique;
import fileops.CloudOps;
import fmannotations.FMAnnotations;
import forms.utility.Alphabet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import type.sectiontype.DoubleCellSection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * This class holds the methods for a
 * person/student's member account.
 */
public class EncryptedAcct {

	// THE LOGGER
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptedAcct.class);

	// 0 = account_id
	// 1 = orig_email
	// 2 = account_status
	// 3 = catagory
	// 4 = paid_date
	// 7 = currency  //ie "usd" or "yen"
	// 8 = period // year or month
	// 9 = due_date
	// 10 = fee
	// 11 = subid subscription id
	private HashMap<String, String> map = null;

	
	public EncryptedAcct() { /* NO ARGS */ }

	/*
	 * checks if user's account exists. If not there
	 * is a problem. Error or bad actor?
	 */
	public boolean exists() {
		if(map == null) {
			// check if user account exists
			getAccountData();
		}

        return !map.get("empty").equals("true");
	}
	
	public boolean isCurrent() {
		// @TODO isCurrent
		if(map == null || map.isEmpty()) {
			// check if user account exists
			getAccountData();
		}
//		else {
			if(map.get("account_status") == null || map.get("account_status").equals("disabled")) {
				LOGGER.warn("ACCOUNT_DISABLED_WARNING: user <{}> , account_status: <{}>", Alphabet.encrypt(authcrypt.UserData.getUserName()), map.get("account_status"));
				map.put("isCurrent", "false");
				return false;
			}
			else {
				// catagory == freemium
				if (map.get("catagory").equals("free")) {
					// user is a freemium user,
					// has ability to buy with fee. Cannot sell.
					map.put("isCurrent", "false");
					return false;
				}
				// catagroy == premium
				if (map.get("catagory").contains("preem")) {
					if (verify(map.get("paid_date"), map.get("account_status"))) {
						map.put("isCurrent", "true");
						return true;
					}
					map.put("isCurrent", "false");
					return false;
				}
//			}
		}
		// should not get here
		LOGGER.warn("Something went wrong: ");
		map.put("isCurrent", "false");
		return false;
	}

	public long getFee() {
		if(map == null) {
			getAccountData();
		} else if(map.get("isCurrent") == null) {
			isCurrent();
		}
		if(map.get("isCurrent").equals("true")) {
			return 0;
		} else {
			return Long.parseLong( map.get("fee"));
		}
	}

	public String getCurrency() {
		if(map == null) {
			getAccountData();
		}
		return map.get("currency");
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
	 * Verify if user/member is current. Determines fees. Should not be used
	 * outside of class.
	 * @param paidDate
	 * @param acctStatus
	 * @return true if user is current
	 */
	protected boolean verify(String paidDate, String acctStatus) {
		String[] date = paidDate.split("-");
		int y = Integer.parseInt(date[0]);
		int m = Integer.parseInt(date[1]);
		int d = Integer.parseInt(date[2]);
		LocalDate payDate = LocalDate.of(y, m, d);
		LocalDate today = LocalDate.now();
		//LocalDate today = LocalDate.now();
		if(map == null) {
			//System.out.println("EncryptedAcct.verify: map is null, returning false...");
			return false;
		} else {
            switch (acctStatus) {
                case "ext_30": {
                    return today.isBefore(payDate.plusMonths(2));
                }
                case "ext_60": {
                    return today.isBefore(payDate.plusMonths(3));
                }
                case "special": {
                    return true;
                }
                case"ok\n": // correcting parser error
                case "ok": {
                    return today.isBefore(payDate.plusMonths(1));
                }
                default:
                case "suspend":
                case "cancelled": {
                    //LOGGER.debug("DEFAULT called at switch: acctStatus: {},  user's account is either freemium, cancelled, or suspended", acctStatus);
                    return false;
                }
            }
		}
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

	public boolean cancel() {
            return exists();
		// Make call to CloudOps and send to Vertx CancelRequest
	}

/*	public static void main(String[] args) {
		System.out.println("Testing verify()");
		EncryptedAcct acct = new EncryptedAcct();
		// manipulate payDate to get the expected result
		String paydate = "1605859200000";
		String period = "31";
		String status = "ext_30";
		boolean bool = acct.verify(paydate, period, status);

		System.out.println(bool + " = acct.verify(paydate, period, status = " + paydate + " " + period + " " + status);
	}

 */
}
