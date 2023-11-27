package com.jnu.student;

import static com.jnu.student.RewardFragment.RewardList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jnu.student.data.DataBank;
import com.jnu.student.data.NormalTask;
import com.jnu.student.data.TaskFinishedData;

import java.util.ArrayList;

public class NormalTaskFragment extends Fragment {

    private RecyclerView mainRecyclerView;
    private static ArrayList<NormalTask> normalTaskList; // 任务列表
    private static NormalTaskFragment.TaskAdapter TaskAdapter;
    public TaskFinishedData finishedData;
    public int flag=0;

    public NormalTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NormalTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NormalTaskFragment newInstance(String param1, String param2) {
        NormalTaskFragment fragment = new NormalTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public void onResume(){
        super.onResume();
        flag=1;
//        mainRecyclerView.setOnCreateContextMenuListener(this);
        TaskFragment.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在这里写您想要的功能，例如弹出一个Toast提示
                Intent intent = new Intent(requireActivity(), TaskDetailsActivity.class);
                addTaskLauncher.launch(intent);
            }
        });
    }
    @Override
    public void onPause(){
        super.onPause();
        flag=0;
        TaskFragment.buttonAdd.setOnClickListener(null);
//        mainRecyclerView.setOnCreateContextMenuListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_normal_task, container, false);


        mainRecyclerView = rootView.findViewById(R.id.recycleview_normal_task);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        normalTaskList =new DataBank().LoadNormalTaskItems(requireActivity());
        if(0== normalTaskList.size()){
            normalTaskList.add(new NormalTask("学习", 150, false));
        }


        TaskAdapter = new NormalTaskFragment.TaskAdapter(normalTaskList);
        mainRecyclerView.setAdapter(TaskAdapter);

        registerForContextMenu(mainRecyclerView);

        addTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("name");
                        String point1 = data.getStringExtra("point");

                        int point=Integer.parseInt(point1);
                        normalTaskList.add(new NormalTask(name,point,false));
                        TaskAdapter.notifyItemInserted(normalTaskList.size());

                        new DataBank().SavaNormalTaskItems(requireActivity(), normalTaskList);

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {

                    }
                }
        );
        updateTaskLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        int position=data.getIntExtra("position",0);
                        String name = data.getStringExtra("name");
                        String point1 = data.getStringExtra("point");

                        int point = Integer.parseInt(point1);
                        NormalTask normalTask = normalTaskList.get(position);
                        normalTask.setName(name);
                        normalTask.setPoint(point);
                        TaskAdapter.notifyItemChanged(position);

                        new DataBank().SavaNormalTaskItems(requireActivity(), normalTaskList);

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {

                    }
                }
        );
        return rootView;
    }

    ActivityResultLauncher<Intent> addTaskLauncher;
    ActivityResultLauncher<Intent> updateTaskLauncher;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(flag==1)
            switch (item.getItemId()) {
                case 0:
                    Intent intent = new Intent(requireActivity(), TaskDetailsActivity.class);
                    addTaskLauncher.launch(intent);
                    break;
                case 1:
                    AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
                    builder.setTitle("Delete Data");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            normalTaskList.remove(item.getOrder());
                            TaskAdapter.notifyItemRemoved(item.getOrder());

                            new DataBank().SavaNormalTaskItems(requireActivity(), normalTaskList);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    break;
                case 2:
                    Intent intentUpdate = new Intent(requireActivity(), TaskDetailsActivity.class);
                    NormalTask normalTask = normalTaskList.get(item.getOrder());
                    intentUpdate.putExtra("name", normalTask.getName());
                    intentUpdate.putExtra("point", normalTask.getPoint());
                    intentUpdate.putExtra("position",item.getOrder());
                    updateTaskLauncher.launch(intentUpdate);
                    break;
                //            default:
                //                return super.onContextItemSelected(item);
            }
        return false;
    }

    public class TaskAdapter extends RecyclerView.Adapter<NormalTaskFragment.TaskAdapter.ViewHolder> {

        private final ArrayList<NormalTask> normalTaskArrayList; // 任务列表

        public TaskAdapter(ArrayList<NormalTask> normalTaskList) {
            this.normalTaskArrayList = normalTaskList;
        }

        @NonNull
        @Override
        public NormalTaskFragment.TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
            return new NormalTaskFragment.TaskAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
            viewHolder.getCheckBox().setChecked(false);
            viewHolder.getCheckBox().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishedData=new DataBank().LoadFinishedDataItems(requireActivity());
                    if(finishedData.setPoint(normalTaskList.get(position).getPoint(), RewardList.get(position).getPoint())) {
                        finishedData.addFinishedDataItem(3,normalTaskList.get(position).getName(), normalTaskList.get(position).getPoint());
                        new DataBank().SavaFinishedDataItems(requireActivity(), finishedData);
                    }

                    normalTaskList.remove(viewHolder.getAdapterPosition());
                    NormalTaskFragment.TaskAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    new DataBank().SavaNormalTaskItems(requireActivity(), normalTaskList);
                }
            });
            viewHolder.getTaskName().setText(normalTaskArrayList.get(position).getName());
            viewHolder.getTaskPoint().setText("+"+normalTaskArrayList.get(position).getPoint());
        }

        @Override
        public int getItemCount() {
            return normalTaskArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView taskName;
            private final TextView taskPoint;
            private CheckBox checkBox;

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("具体操作");
                menu.add(0, 0, this.getAdapterPosition(), "添加" + this.getAdapterPosition());
                menu.add(0, 1, this.getAdapterPosition(), "删除" + this.getAdapterPosition());
                menu.add(0, 2, this.getAdapterPosition(), "修改" + this.getAdapterPosition());
            }

            public ViewHolder(View TaskView) {
                super(TaskView);

                checkBox=TaskView.findViewById(R.id.task_checkbox);
                taskName = TaskView.findViewById(R.id.task_name);
                taskPoint = TaskView.findViewById(R.id.task_point);
                TaskView.setOnCreateContextMenuListener(this);
            }

            public TextView getTaskName() {
                return taskName;
            }

            public TextView getTaskPoint() {
                return taskPoint;
            }

            public CheckBox getCheckBox(){return checkBox;}
        }
    }
}