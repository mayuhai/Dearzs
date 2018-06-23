package com.dearzs.app.util;

import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.entity.resp.RespPatientList;

import java.util.Comparator;

/**
 * @author Luyanlong
 */
public class PinyinComparator implements Comparator<EntityPatientInfo> {
	public int compare(EntityPatientInfo o1, EntityPatientInfo o2) {
		if(o1 == null || o2 == null || o1.getSortLetters() == null || o2.getSortLetters() == null){
			return -1;
		}
		if ("@".equals(o1.getSortLetters())
				|| "#".equals(o2.getSortLetters())) {
			return -1;
		} else if ("#".equals(o1.getSortLetters())
				|| "@".equals(o2.getSortLetters())) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}
}
