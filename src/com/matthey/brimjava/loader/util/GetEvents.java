package com.matthey.brimjava.loader.util;

import com.matthey.brimjava.loader.events.LoadEmail;
import com.matthey.brimjava.loader.events.LoadFile;
import com.matthey.brimjava.loader.events.LoadSms;

public class GetEvents {
	public static EventGroups byType(String str) {
		EventGroups out = new EventGroups();
		//indexed on j_event.AttachmentFK // type id
		LoadEmail.loadAll(str, out);
		LoadSms.loadAll(str, out);
		LoadFile.loadAll(str, out);
		//LoadOee.quick(i, out);
		return out;
	}
}
