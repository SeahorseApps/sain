package com.sain.azmoon.helpers;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Utils
{
    public static void showMessageBoxOK(Context context, String title, String message, DialogInterface.OnClickListener clickListener)
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("باشه", clickListener);
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }

    public static void showMessageBoxYesNo(Context context, String title, String message, DialogInterface.OnClickListener yesClickListener, DialogInterface.OnClickListener noClickListener)
    {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("آره", yesClickListener);
        dlgAlert.setNegativeButton("نه", noClickListener);
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }
}
