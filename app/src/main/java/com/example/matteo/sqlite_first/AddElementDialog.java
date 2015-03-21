package com.example.matteo.sqlite_first;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Matteo on 21/03/2015.
 */
public class AddElementDialog extends DialogFragment implements TextView.OnEditorActionListener
{
    public interface AddElementDialogListener
    {
        void onFinishEditDialog(String inputText);
    }

    private EditText mEditText;

    public AddElementDialog()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_addelement, container);
        mEditText = (EditText) view.findViewById(R.id.element_name);

        getDialog().setTitle(R.string.add_element);

        mEditText.requestFocus();
        mEditText.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if(EditorInfo.IME_ACTION_DONE == actionId)
        {
            AddElementDialogListener activity = (AddElementDialogListener) getActivity();
            activity.onFinishEditDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }

        return false;
    }
}
