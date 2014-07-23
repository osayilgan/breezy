package com.breezy.oauth;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.breezy.oauth.model.Printer;

public class PrinterAdapter extends BaseAdapter {
	
	public static interface PrinterClickListener {
		void onPrinterClicked(int position);
	}
	
	public void setPrinterClickListener(PrinterClickListener printerClickListener) {
		this.printerClickListener = printerClickListener;
	}
	
	private PrinterClickListener printerClickListener;
	private HashMap<Integer, Boolean> positionClickHolder;
	
	private int previouslyClickedItem = -1;
	
	private ArrayList<Printer> mPrinterList;
	private LayoutInflater inflater;
	
	public PrinterAdapter(Activity activity, ArrayList<Printer> printerList) {
		
		this.mPrinterList = printerList;
		this.inflater = activity.getLayoutInflater();
		
		initializePositionClickHolder();
	}
	
	private void initializePositionClickHolder() {
		
		positionClickHolder = new HashMap<Integer, Boolean>();
		
		for (int i = 0; i < mPrinterList.size(); i++) {
			positionClickHolder.put(i, false);
		}
	}
	
	private void setItemClicked(int position, boolean isClicked) {
		positionClickHolder.put(position, isClicked);
	}
	
	private boolean isItemClicked(int position) {
		return positionClickHolder.get(position);
	}
	
	@Override
	public int getCount() {
		return mPrinterList.size();
	}
	
	@Override
	public Printer getItem(int position) {
		return mPrinterList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return getItem(position).getEndPointId();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final PrinterViewHolder printerViewHolder;
		
		if (convertView == null) {
			
			printerViewHolder = new PrinterViewHolder();
			
			convertView = inflater.inflate(R.layout.printer_list_single_item_layout, parent, false);
			printerViewHolder.printerName = (TextView) convertView.findViewById(R.id.printer_row_name);
			
			convertView.setTag(printerViewHolder);
			
		} else {
			printerViewHolder = (PrinterViewHolder) convertView.getTag();
		}
		
		if (isItemClicked(position)) {
			printerViewHolder.printerName.setTextColor(Color.BLUE);
		} else {
			printerViewHolder.printerName.setTextColor(Color.BLACK);
		}
		
		/* Set Printer Name */
		printerViewHolder.printerName.setText(getItem(position).getDisplayName());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (printerClickListener != null) {
					
					printerClickListener.onPrinterClicked(position);
					printerViewHolder.printerName.setTextColor(Color.BLUE);
					setItemClicked(position, true);
					
					if (previouslyClickedItem != -1) {
						
						/* Clear Previous Item's background */
						setItemClicked(previouslyClickedItem, false);
						
						/* Refresh the List */
						notifyDataSetChanged();
					}
					
					previouslyClickedItem = position;
				}
			}
		});
		
		return convertView;
	}
	
	static class PrinterViewHolder {
		TextView printerName;
	}
}
