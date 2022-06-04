package DataBase;

import authcrypt.user.EncryptedAcct;
import fileops.DirectoryMgr;
import flashmonkey.*;

import forms.DeckMetaModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;


import java.io.*;
import java.time.*;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueryEncryptedAcctTest extends ApplicationTest {
	
	@Test
	public void noUserReturnsEMPTY() {
		EncryptedAcct e = new EncryptedAcct();
		boolean bool = e.exists();
		assertTrue(bool == false, "No user did not return \"EMPTY\"");
	}



	@Test
	public void verifyUserIsCurrent() throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		EncryptedAcct acct = new EncryptedAcct();
		String paydate;// = Long.toString(validateWithPayDate());
		String period = ""; // monthly
		String status = "";
		paydate = Long.toString(validateWithPayDate());

		// USING REFLECTION TO TEST PRIVATE METHOD
		Method method = EncryptedAcct.class.getDeclaredMethod("verify", String.class, String.class, String.class);
		method.setAccessible(true);

		boolean bool;
		// bool = (boolean) method.invoke(acct, this.paydate, this.period, this.status);

		//System.out.println(bool + " = acct.verify(paydate, period, status = " + paydate + " " + period + " " + status);
		//assertTrue(bool == false, "verify did not fail when more than 31 days past paydate");

		// *** NOTE THAT the first object is THE OBJECT OF THE TESTED CLASS THAT THE METHOD BELONGS TO. *** //
		bool = (boolean) method.invoke(acct, paydate, period, status);
		//bool = e.verify(paydate, period, status);
		assertTrue(bool, "verify did not pass when less than 31 days from paydate");
	//	paydate = Long.toString(validateWithPayDate());
	//	period = "year";
	//	bool = e.verify(paydate, period, status);
	//	assertTrue(bool == false, "verify year did not fail when more than 365 days from paydate");
	}


	/**
	 * To completely test validatorActionSwitch, the verify method now date must be changed from time.now
	 * to a date in the future. Not all features can be tested with accurcay due to the date.
	 * For further testing, also check main method in EncryptedAcct.
	 * @return
	 */
	private long validateWithPayDate() {
		LocalDateTime today = LocalDateTime.now();
		System.out.println("today: " + today);
		long payDate = 0;
		String dStr = today.toString();
		String s[] = dStr.split("-");
		//int year = Integer.parseInt(s[0]);
		//int month = Integer.parseInt(s[1]);
		int day = Integer.parseInt(s[2].substring(0,2));

		System.out.println("today: " + today);
		System.out.println("day: " + day);
		if(day < 20) {
			LocalDateTime startDate = today.minusDays(day -1 );
			System.out.println("startDate: " + startDate);
			payDate = startDate.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
		} else {
			LocalDateTime startDate = today.plusMonths(1).minusDays(day - 1);
			System.out.println("startDate: " + startDate);
			payDate = startDate.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
		}

		LocalDate ld = Instant.ofEpochMilli(payDate).atZone(ZoneId.systemDefault()).toLocalDate();
		System.out.println("paydate: " + ld);
		System.out.println(ld);
		return payDate;
	}
}
