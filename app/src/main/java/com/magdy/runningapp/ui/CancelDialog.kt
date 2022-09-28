package com.magdy.runningapp.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.magdy.runningapp.R

class CancelDialog:DialogFragment() {

    private var yesListener:(()->Unit)?=null

    fun setYesListener(listener:()->Unit){
        yesListener=listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogTheme
        ).apply {

            setIcon(R.drawable.ic_delete)
            setTitle("Cancel Run !")
            setMessage("Are you Want To Cancel Current Run ?")
            setPositiveButton("Yes") { _, _ ->
               yesListener?.let {yes->
                   yes()
               }

            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        }.create()

    }

}