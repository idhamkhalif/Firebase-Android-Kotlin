package com.example.demofirebase

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import java.text.FieldPosition

class Adapter(val mCtx:Context,val layoutResId: Int, val list:List<Users>)
    :ArrayAdapter<Users>(mCtx,layoutResId,list){

        override fun getView(position: Int, convertView: View?,parent: ViewGroup):View{
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view : View = layoutInflater.inflate(layoutResId,null)

            val textNama = view.findViewById<TextView>(R.id.textNama)
            val textEmail = view.findViewById<TextView>(R.id.textEmail)
            val btnEdit = view.findViewById<Button>(R.id.updateBtn)
            val deleteBtn = view.findViewById<Button>(R.id.deleteBtn)

            val user = list[position]

            textNama.text = user.name
            textEmail.text = user.email

            btnEdit.setOnClickListener {showUpdate(user)  }
            deleteBtn.setOnClickListener { deleteInfo(user) }
            return view
        }

    private fun deleteInfo(user:Users)
    {
        //val progressDialog = ProgressDialog(context, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog)
        val progressDialog = ProgressDialog(context, com.google.android.material.R.style.Theme_MaterialComponents_BottomSheetDialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Deleting..")
        progressDialog.show()
        val mydatabase = FirebaseDatabase.getInstance().getReference("USERS")
        mydatabase.child(user.id).removeValue()
        Toast.makeText(mCtx,"Deleted Data",Toast.LENGTH_SHORT).show()
        val intent = Intent(mCtx,Show::class.java)
        context.startActivities(arrayOf(intent))
    }

    private fun showUpdate(user: Users)
    {
        val builder = AlertDialog.Builder(mCtx)
        builder.setTitle("Update")

        val inflater = LayoutInflater.from(mCtx)
        val view = inflater.inflate(R.layout.update,null)

        val textName = view.findViewById<EditText>(R.id.inputNama)
        val textEmail = view.findViewById<EditText>(R.id.inputEmail)

        textName.setText(user.name)
        textEmail.setText(user.email)

        builder.setView(view)

        builder.setPositiveButton("Update"){dialog, which->
            val dbUsers = FirebaseDatabase.getInstance().getReference("USERS")
            val nama = textName.text.toString().trim()
            val email = textEmail.text.toString().trim()

            if(nama.isEmpty() || email.isEmpty())
            {
                textName.error = "Please enter name"
                textEmail.error = "Please enter email"
                textName.requestFocus()
                return@setPositiveButton
            }

            val user = Users(user.id,nama,email)

            dbUsers.child(user.id).setValue(user).addOnCompleteListener{
                Toast.makeText(mCtx,"Update",Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("No"){dialog,wich ->

        }
        val alert = builder.create()
        alert.show()
    }
}