package com.lotus.testintelligent;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.lotus.testintelligent.ContactAdapter.ViewHolder;

public class IntelligentActivity extends ListActivity implements OnItemClickListener{
	EditText editText;
	ListView listView;
	ContactAdapter contactAdapter;
	private List<ContactInfo> mContactInfos;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        editText = (EditText) findViewById(R.id.EditText01);
        editText.addTextChangedListener(textWatcher );
        listView = this.getListView();
        
        //===============================================
        Cursor phoneCur = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				null,
				null, null);
        mContactInfos = new ArrayList<ContactInfo>();
        initContactInfos(phoneCur);
        contactAdapter = new ContactAdapter(this, mContactInfos);
        
        listView.setAdapter(contactAdapter);
        listView.setOnItemClickListener(this);
        listView.setTextFilterEnabled(true);
    }
    
    private void initContactInfos(Cursor phoneCur) {
		// TODO Auto-generated method stub
		if (phoneCur == null) return;
		phoneCur.move(-1);
		while (phoneCur.moveToNext()) {
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.personId = phoneCur.getLong(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			contactInfo.number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contactInfo.name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			if (contactInfo.name == null) {
				contactInfo.name = contactInfo.number;
			}
			
			mContactInfos.add(contactInfo);
		}
	}

	private TextWatcher textWatcher = new TextWatcher() {
    	public void onTextChanged(CharSequence s, int start, int before, int count) {
    		contactAdapter.getFilter().filter(s);
    	}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		};
    };

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder) arg1.getTag();
		//这里可以获得contactId，有了contactId,就可以做很多其他事情了。
		Log.d("tag", "contact id =" + viewHolder.tvItemNumAddr.getTag());
	}
}