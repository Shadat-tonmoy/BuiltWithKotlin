package com.shadattonmoy.imagepickerforandroid.ui.screenView;

import android.content.Context;
import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseScreenView<ListenerType>
{
    public abstract void inflateUIElements();
    public abstract void initUserInteractions();

    private View rootView;
    private Set<ListenerType> listeners = new HashSet<>();

    public void setRootView(View rootView)
    {
        this.rootView = rootView;
    }

    public <T extends View> T findViewById(int id)
    {
        return rootView.findViewById(id);
    }

    public Context getContext()
    {
        return rootView.getContext();
    }

    public void registerListener(ListenerType listener)
    {
        listeners.add(listener);
    }

    public void unregisterListener(ListenerType listener)
    {
        listeners.remove(listener);
    }

    public Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(listeners);
    }

    public View getRootView() {
        return rootView;
    }
}
