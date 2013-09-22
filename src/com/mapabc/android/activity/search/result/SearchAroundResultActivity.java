package com.mapabc.android.activity.search.result;

import android.os.Bundle;
import android.util.Log;

import com.mapabc.android.activity.base.Constants;
import com.mapabc.naviapi.SearchAPI;
import com.mapabc.naviapi.search.GetIconPoint;
import com.mapabc.naviapi.search.SearchResultInfo;
import com.mapabc.naviapi.search.TypeInfo;
import com.mapabc.naviapi.type.NSLonLat;

/**
 * @description: 周边查询结果UI
 * @author menglin.cao 2012-09-06
 * @version:
 * @modify:
 * @Copyright: mapabc.com
 */
public class SearchAroundResultActivity extends SearchPOIResultActivity {
	// protected AdminManager adminMgr = AdminManager.getInstance();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.startSynchronizedLoad();
	}

	protected void searchPOI() {
		lstPOI.clear();
		Bundle bundle = getIntent().getBundleExtra("SearchArroundPOIActivity");
		if (bundle != null) {
			int oType = bundle.getInt(Constants.SEARCHAROUND_OTYPE);
			int sType = bundle.getInt(Constants.SEARCHAROUND_STYPE);
			NSLonLat center = (NSLonLat)bundle.getSerializable(Constants.SEARCHAROUND_CENTER_POINT);
			Log.e("SearchAroundResultActivity", "====oType====" + oType);
			Log.e("SearchAroundResultActivity", "====sType====" + sType);

			TypeInfo oTypeInfo = new TypeInfo();
			boolean bGetOType = SearchAPI.getInstance().getOType(oType,oTypeInfo);
			TypeInfo sTypeInfo = new TypeInfo();
			if(bGetOType){
				SearchAPI.getInstance().getSType(oTypeInfo.code,sType,sTypeInfo);
			}
			String [] typeCodes = null;
			if(sTypeInfo.subCount>0){
			typeCodes = new String[sTypeInfo.subCount];
			TypeInfo aTypeInfo = new TypeInfo();
			for(int i=0;i<sTypeInfo.subCount;i++){
				SearchAPI.getInstance().getAType(sTypeInfo.code, i, aTypeInfo);
				typeCodes[i] = aTypeInfo.code;
			}
//			typeCodes[0] = sTypeInfo.code;
			}else{
				typeCodes = new String[1];
				typeCodes[0] = sTypeInfo.code;
			}
			int cout = SearchAPI.getInstance().startAroundSearch(typeCodes,5000,center,"",true);
			Log.e("CategoryItemClickListener", "===cout=====" + cout);
			int len = cout;
			GetIconPoint getIconPoint = new GetIconPoint();
			SearchResultInfo info;
			for (int i = 0; i < len; i++) {
				SearchAPI.getInstance().getAroundRecordByIndex(i,getIconPoint);
				info = new SearchResultInfo();
				info.addr = getIconPoint.addr;
				info.lat = getIconPoint.lat;
				info.lon = getIconPoint.lon;
				info.name = getIconPoint.name;
				info.telephone = getIconPoint.telephone;
				info.typeCode = getIconPoint.typeCode;
				info.typeCodeIndex = getIconPoint.typeCodeIndex;
				//info.typeIcon = getIconPoint.typeIcon;
				//info.typeName = getIconPoint.typeName;
				lstPOI.add(info);
			}
		}

	}

}
