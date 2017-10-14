package com.koiti.mctjobs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koiti.mctjobs.R;
import com.koiti.mctjobs.helpers.Constants;
import com.koiti.mctjobs.helpers.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private static final String TAG = MessageFragment.class.getSimpleName();

    private ImageView mImageMessage;
    private TextView mTitleMessage;
    private TextView mBodyMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Parameters
        Bundle messageParameters = MessageFragment.this.getArguments();
        Boolean success = messageParameters.getBoolean(Constants.MESSAGE_SUCCESS, true);
        String title = messageParameters.getString(Constants.MESSAGE_TITLE, null);
        String body = messageParameters.getString(Constants.MESSAGE_BODY, null);

        // View
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        if( success == false ) {
            view = inflater.inflate(R.layout.fragment_error, container, false);
        }

        // References
        mImageMessage = (ImageView) view.findViewById(R.id.message_image);
        mTitleMessage = (TextView) view.findViewById(R.id.message_title);
        mBodyMessage = (TextView) view.findViewById(R.id.message_body);

        // Successfull
        ImageLoader loaderImage = ImageLoader.getInstance();
        DisplayImageOptions optionsRightPet = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
        loaderImage.displayImage("drawable://" + R.drawable.right_pet, mImageMessage, optionsRightPet);

        mTitleMessage.setText( title );
        mBodyMessage.setText( Utils.fromHtml(body) );

        return view;
    }
}