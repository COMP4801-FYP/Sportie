package hk.hkucs.sportieapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.firestore.DocumentReference

import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    private lateinit var functions: FirebaseFunctions;
    private lateinit var db: FirebaseFirestore;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val inputFirstName = findViewById<EditText>(R.id.inputFirstName);
//        val inputLastName = findViewById<EditText>(R.id.inputLastName);

        functions = Firebase.functions // initialize Firebase functions
        db = FirebaseFirestore.getInstance();
//        db = Firebase.firestore
        val numOfPlayersTextView: TextView = findViewById(R.id.numOfPlayersTextView)

        val saveButton = findViewById<Button>(R.id.saveButton);
//        saveButton.setOnClickListener{
//
//            val docRef = db.collection("testVenueCollection").document("testVenue")
//            docRef.get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
//                        numOfPlayersTextView.text = document.data!!.get("player_count").toString();
//                    } else {
//                        Log.d(TAG, "No such document")
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    Log.d(TAG, "get failed with ", exception)
//                }
//
//        }
        saveButton.setOnClickListener{
//            val temp: Task<String> = addMessage(inputFirstName.text.toString());
//            temp.addOnSuccessListener {
//                Log.d("TAG",temp.getResult())
//            }
            Log.d("TAG","Save Button pressed")
            val numOfPlayersTextView: TextView = findViewById(R.id.numOfPlayersTextView)
            var playerCount: Task<String> = countPlayersImagesTESTTT("Lurus5");
            playerCount.addOnSuccessListener {
//                Log.d("TAG","Counting player succeed")
//                val count = playerCount.getResult()
//                Log.d("TAG",count)
//                numOfPlayersTextView.text = count;
            }
            countPlayersImagesTESTTT("3-29 Angled1");
            countPlayersImagesTESTTT("3-29 Angled2");
            countPlayersImagesTESTTT("3-29 Angled3");
            countPlayersImagesTESTTT("3-29 Angled4");
            countPlayersImagesTESTTT("3-29 Angled5");
            countPlayersImagesTESTTT("3-29 Angled6");
//            playerCount = countPlayersImagesTESTTT("Miring1");
//            playerCount.addOnSuccessListener {  }

        }
    }
    private fun countPlayersImagesTESTTT(text: String): Task<String>{
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )
        return functions
            .getHttpsCallable("countPlayersImagesTESTTT")
            .call(data)
            .continueWith{task->
                val result = task.result?.data as String
                result
            }
    }

    private fun countPlayersImages(): Task<String>{
        return functions
            .getHttpsCallable("countPlayersImages")
            .call()
            .continueWith{task->
                val result = task.result?.data as String
                result
            }
    }

    // Sample function to count players based on a video
    private fun countPlayers(): Task<String>{
        return functions
            .getHttpsCallable("countPlayers")
            .call()
            .continueWith{task->
                val result = task.result?.data as String
                result
            }
    }

    // Sample function for connecting
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

    // Sample function to save to firestore
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