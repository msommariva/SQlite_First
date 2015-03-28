package com.example.matteo.sqlite_first;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TestDatabaseActivity extends ListActivity implements AddElementDialog.AddElementDialogListener, ActionMode.Callback
{

    private CommentsDataSource dataSource;
    private List<Comment> values;
    protected Object mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        //Database connection
        dataSource = new CommentsDataSource(this);
        try
        {
            dataSource.open();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        ListView list = getListView();
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
                ListView listView = getListView();

                if (listView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE)
                {
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    adapter = new ArrayAdapter<Comment>(TestDatabaseActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, values);
                    setListAdapter(adapter);
                }

                if(mActionMode != null)
                {
                    return false;
                }

                TestDatabaseActivity.this.startActionMode(TestDatabaseActivity.this);
                view.setSelected(true);
                return true;
            }
        });

        values = dataSource.getAllComments();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view)
    {
        ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
        Comment comment = null;
        switch (view.getId())
        {
            case R.id.add:
//                String[] comments = new String[] { "Cool", "Very Cool", "Hate it"};
//                int nextInt = new Random().nextInt(3);
//                comment = dataSource.createComment(comments[nextInt]);
                showAddElementDialog();
//                adapter.add(comment);
//                new AlertDialog.Builder(this)
//                        .setTitle("Success")
//                        .setMessage("Element " + comment.getComment() + " added!")
//                        .setPositiveButton(android.R.string.yes, null)
//                        .setIcon(android.R.drawable.ic_dialog_info)
//                        .show();
                break;


            case R.id.delete:
                if (getListAdapter().getCount() > 0)
                {
                    ListView listView = getListView();
                    comment = (Comment) getListAdapter().getItem(0);
                    dataSource.deleteComment(comment);
                    adapter.remove(comment);
                    adapter.notifyDataSetChanged();
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        try
        {
            dataSource.open();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    private void showAddElementDialog()
    {
        FragmentManager fm = getFragmentManager();
        AddElementDialog addElementDialog = new AddElementDialog();
        addElementDialog.show(fm, "fragment_add_new_element");
    }

    @Override
    public void onFinishEditDialog(String inputText)
    {
        if(!inputText.isEmpty())
        {
            ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
            Comment comment = dataSource.createComment(inputText);
            adapter.add(comment);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Added " + inputText, Toast.LENGTH_SHORT).show();
        }
    }

    public void changeSelectionMode()
    {
        ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
        ListView listView = getListView();

        if (listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE)
        {
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            adapter = new ArrayAdapter<Comment>(this,
                    android.R.layout.simple_list_item_1, values);
            setListAdapter(adapter);
        }
        else if (listView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE)
        {
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            adapter = new ArrayAdapter<Comment>(this,
                    android.R.layout.simple_list_item_multiple_choice, values);
            setListAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual,menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        ListView listView = getListView();
        ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();

        switch(item.getItemId())
        {
            case R.id.item_delete:

                SparseBooleanArray checked = listView.getCheckedItemPositions();
                ArrayList<Comment> selectedItems = new ArrayList<Comment>();
                for (int i = 0; i < checked.size(); i++)
                {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i))
                    {
                        selectedItems.add((Comment) getListAdapter().getItem(position));
                    }
                }

                for (int i = 0; i < selectedItems.size(); i++)
                {
                    Comment comment = selectedItems.get(i);
                    dataSource.deleteComment(comment);
                    adapter.remove(comment);
                }

                adapter.notifyDataSetChanged();

                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
        mActionMode = null;

        changeSelectionMode();
    }
}
