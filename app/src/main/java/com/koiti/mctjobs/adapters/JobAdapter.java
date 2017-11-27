package com.koiti.mctjobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koiti.mctjobs.R;
import com.koiti.mctjobs.StepActivity;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.Utils;
import com.koiti.mctjobs.models.Job;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private Context mContext;
    private FragmentActivity fActivity;
    private List<Job> items;
    private Boolean progress;

    public JobAdapter(List<Job> items, FragmentActivity fActivity, Boolean progress) {
        this.fActivity = fActivity;
        this.mContext = fActivity.getApplicationContext();
        this.items = items;
        this.progress = progress;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_list_row, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        Job item = items.get(position);
        holder.itemView.setTag(item);

        holder.title.setText( Utils.capitalize(item.getWorktypename()) );
        holder.id.setText( "(" + Integer.toString(item.getId()) + ") " + item.getDocument() );
        holder.abstrac.setText( Utils.fromHtml(item.getAbstrac()) );
        holder.formatdate.setText(item.getFormatdate());
        if( item.getPendingsync() ) {
            holder.pending_sync.setVisibility(View.VISIBLE);
        }

        if(this.progress && item.getAmountsteps() != 0) {
            holder.layout_progress.setVisibility(View.VISIBLE);

            int percentage = (int) Math.round(((double) item.getCurrentstep() / (double) item.getAmountsteps()) * 100);
            holder.text_progress_job.setText(percentage + "%");

            holder.progress_job.setMax(item.getAmountsteps());
            holder.progress_job.setProgress(item.getCurrentstep());
        }

        holder.setClickListener(new JobClickListener() {
            @Override
            public void onClick(View view, int position) {
                Job item = (Job) items.get(position);

                Intent intent = new Intent(mContext, StepActivity.class);
                intent.putExtra("JOB", item.getId());
                fActivity.startActivityForResult(intent, Constants.PICK_STEP_REQUEST);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;
        protected TextView id;
        protected TextView abstrac;
        protected TextView formatdate;
        protected ImageView pending_sync;

        protected RelativeLayout layout_progress;
        protected ProgressBar progress_job;
        protected TextView text_progress_job;

        private JobClickListener clickListener;

        public JobViewHolder(View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.title);
            this.id = (TextView) itemView.findViewById(R.id.id);
            this.abstrac = (TextView) itemView.findViewById(R.id.abstrac);
            this.formatdate = (TextView) itemView.findViewById(R.id.formatdate);
            this.pending_sync = (ImageView) itemView.findViewById(R.id.pending_sync);

            this.layout_progress = (RelativeLayout) itemView.findViewById(R.id.layout_progress);
            this.progress_job = (ProgressBar) itemView.findViewById(R.id.progress_job);
            this.text_progress_job = (TextView) itemView.findViewById(R.id.text_progress_job);

            itemView.setOnClickListener(this);
        }

        public void setClickListener(JobClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View itemView) {
            clickListener.onClick(itemView, getPosition());
        }
    }
}
