package hk.hkucs.sportieapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.firestore.DocumentReference

import com.google.android.gms.tasks.OnSuccessListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputFirstName = findViewById<EditText>(R.id.inputFirstName);
        val inputLastName = findViewById<EditText>(R.id.inputLastName);

        val saveButton = findViewById<Button>(R.id.saveButton);
        saveButton.setOnClickListener{
            val firstName = inputFirstName.text.toString();
            val lastName = inputLastName.text.toString();
            saveFireStore(firstName, lastName);
        }


    }

    fun saveFireStore(firstName: String, lastName: String){
        val db = FirebaseFirestore.getInstance();
        val user: MutableMap<String, Any> = HashMap();
        user["firstName"] = firstName;
        user["lastName"] = lastName;

        db.collection("users")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "record added successfully", Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "record added unsuccessful", Toast.LENGTH_SHORT).show();
            }
    }

}