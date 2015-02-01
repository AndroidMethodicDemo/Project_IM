package com.lotus.testintelligent;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class CursorContactsAdapter extends ResourceCursorAdapter implements Filterable{
	private Context mContext;
	private HashMap<String, String> mContactInfo;
	public CursorContactsAdapter(Context context, Cursor c) {
		super(context,R.layout.dialcontactitem,  c);
		this.mContext = context;
		mContactInfo = new HashMap<String, String>();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		String id = mContactInfo.get(number);
		
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		String phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		
		
		if (phoneName != null)
			viewHolder.tvName.setText(phoneName);
		if (number != null) {
			viewHolder.tvPhone.setText(number);
		}
		viewHolder.tvPhone.setTag(id);
	}

    private Cursor queryPhoneNumbers(long contactId) {
        Uri baseUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri, Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = mContext.getContentResolver().query(dataUri,
                new String[] {Phone._ID, Phone.NUMBER, Phone.IS_SUPER_PRIMARY,
                        RawContacts.ACCOUNT_TYPE, Phone.TYPE, Phone.LABEL},
                Data.MIMETYPE + "=?", new String[] {Phone.CONTENT_ITEM_TYPE}, null);
        if (c != null) {
            if (c.moveToFirst()) {
                return c;
            }
            c.close();
        }
        return null;
    }
    
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = super.newView(context, cursor, parent);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.imPhoto = (ImageView) view.findViewById(R.id.ImPhoto);
		viewHolder.tvName = (TextView) view.findViewById(R.id.TvDialContactName);
		viewHolder.tvPhone = (TextView) view.findViewById(R.id.TvNumAddr);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// TODO Auto-generated method stub
		String seletion = ContactsContract.CommonDataKinds.Phone.NUMBER + " like '%" +
				constraint + "%' ";
		Cursor intellCursor = mContext.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null,
				seletion,
				null, null);
		if (intellCursor == null ) return null;
		while (intellCursor.moveToNext()) {
			String id = intellCursor.getString(intellCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			String number = intellCursor.getString(intellCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			mContactInfo.put(number, id);
		}
		
		return intellCursor;
	}
	
	class ViewHolder {
		ImageView imPhoto;
		TextView tvName;
		TextView tvPhone;
	}
	

}
