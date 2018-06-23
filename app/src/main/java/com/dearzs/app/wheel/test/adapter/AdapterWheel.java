package com.dearzs.app.wheel.test.adapter;

import android.content.Context;

import com.dearzs.app.wheel.test.WheelAdapter;
import com.dearzs.app.wheel.test.adapter.AbstractWheelTextAdapter;

/**
 * Adapter class for old wheel adapter (deprecated WheelAdapter class).
 * 
 * @deprecated Will be removed soon
 */
public class AdapterWheel extends AbstractWheelTextAdapter {

    // Source adapter
    private WheelAdapter adapter;
    
    /**
     * Constructor
     * @param context the current context
     * @param adapter the source adapter
     */
    public AdapterWheel(Context context, WheelAdapter adapter) {
        super(context);
        
        this.adapter = adapter;
    }

    /**
     * Gets original adapter
     * @return the original adapter
     */
    public WheelAdapter getAdapter() {
        return adapter;
    }
    
    
    public int getItemsCount() {
        return adapter.getItemsCount();
    }

    
    protected CharSequence getItemText(int index) {
        return adapter.getItem(index);
    }

}
