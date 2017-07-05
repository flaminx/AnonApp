package com.example.flaminx.anonapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Flaminx on 03/07/2017.
 */
//This class handles the app tutorial, should be called from fresh install or options menu
public class Tutorial {
    Context context;
    View parentView;
    private boolean closeTutorial;
    private int tutorialStage;
    public Tutorial(Context vContext, View pView) {
       context = vContext;
        closeTutorial = false;
        parentView = pView;
        tutorialStage = 3;
    }

    public void beginTutorial()
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.tutorial_layout,null);

        final PopupWindow tutorialPopup = new PopupWindow(customView , LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tutorialPopup.setElevation(5);
        }

        tutorialPopup.showAtLocation(parentView.findViewById(R.id.Main_layout), Gravity.CENTER,0,0);

        TextView tTitle = (TextView) customView.findViewById(R.id.PopupTitle);
        final TextView tText = (TextView) customView.findViewById(R.id.PopupText);

        Button quit = (Button) customView.findViewById(R.id.popupQuit);
        Button next = (Button) customView.findViewById(R.id.popupNext);

        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(closeTutorial)
                {
                    tutorialPopup.dismiss();
                }
                else
                {
                    tText.setText(R.string.TutorialText2);
                    closeTutorial = true;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switch (tutorialStage) {
                    case 3:
                        tText.setText(R.string.TutorialText3);
                        break;
                    case 4:
                        tText.setText(R.string.TutorialText4);
                        break;
                    case 5:
                        tText.setText(R.string.TutorialText5);
                        break;
                    case 6:
                        tText.setText(R.string.TutorialText6);
                        break;
                    default:
                        tutorialPopup.dismiss();
                        break;
                }

            tutorialStage++;
            }
        });

        tTitle.setText(R.string.TutorialTitle);
        tText.setText(R.string.TutorialText1);

    }


}
