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
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputFirstName = findViewById<EditText>(R.id.inputFirstName);
        val inputLastName = findViewById<EditText>(R.id.inputLastName);

        functions = Firebase.functions

        val saveButton = findViewById<Button>(R.id.saveButton);
        saveButton.setOnClickListener{
            val firstName = inputFirstName.text.toString();
            val lastName = inputLastName.text.toString();
            saveFireStore(firstName, lastName);
            val temp: Task<String> = addMessage(inputFirstName.text.toString());
            temp.addOnSuccessListener {
                Log.d("TAG",temp.getResult())

            }
        }



    }
    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("addMessage")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
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